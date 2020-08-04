/*
 * Copyright 2017 Phoboslabs.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.phoboslabs.illuminati.processor.infra.backup.impl;

import com.google.gson.JsonSyntaxException;
import me.phoboslabs.illuminati.processor.infra.backup.Backup;
import me.phoboslabs.illuminati.processor.infra.h2.configuration.H2ConnectionFactory;
import me.phoboslabs.illuminati.processor.infra.backup.enums.TableDDLType;
import me.phoboslabs.illuminati.processor.properties.IlluminatiH2Properties;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.enums.IlluminatiInterfaceType;
import me.phoboslabs.illuminati.common.properties.IlluminatiPropertiesHelper;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 04/05/2018.
 */
public class H2Backup<T> implements Backup<T> {

    private final Logger h2BackupLogger = LoggerFactory.getLogger(this.getClass());

    final Class<T> type;

    private static H2Backup H2_BACKUP;

    private final H2ConnectionFactory h2Conn;
    private Connection connection;
    private static final String DB_NAME = "illuminati-backup";
    private static final String TABLE_NAME = "illuminati_backup";

    private H2Backup (Class<T> type) throws Exception {
        this.h2Conn = new H2ConnectionFactory();
        this.type = type;
        this.connection = this.h2Conn.makeDBConnection(DB_NAME);
        if (this.h2Conn.isConnected(this.connection)) {
            final String backTableReset = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiH2Properties.class, "illuminati", "backTableReset", "false");
            if ("true".equalsIgnoreCase(backTableReset)) {
                this.tableDDL(TableDDLType.DROP);
            }
            this.tableDDL(TableDDLType.CREATE);
        } else {
            throw new Exception("H2 is can't connect.");
        }
    }

    public static H2Backup getInstance (Class type) throws Exception {
        if (H2_BACKUP == null) {
            synchronized (H2Backup.class) {
                if (H2_BACKUP == null) {
                    H2_BACKUP = new H2Backup(type);
                }
            }
        }

        return H2_BACKUP;
    }

    private void tableDDL (TableDDLType tableDDLType) {
        switch (tableDDLType) {
            case CREATE :
                this.createTable();
                break;

            case DROP :
                this.deleteTable();
                break;

            default :
                this.h2BackupLogger.warn("Failed to DDL syntax. Check your tableDDLType parameter");
                break;
        }
    }

    private void deleteTable() {
        StringBuilder tableExecuteCommand = new StringBuilder("DROP TABLE IF EXISTS ").append(TABLE_NAME);
        this.executeDDL(tableExecuteCommand.toString(), TableDDLType.DROP.name());
    }

    private void createTable () {
        StringBuilder tableExecuteCommand = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                                                .append(TABLE_NAME)
                                                .append(" ( ")
                                                .append(" ID INTEGER PRIMARY KEY AUTO_INCREMENT")
                                                .append(", EXECUTOR_TYPE INTEGER NOT NULL")
                                                .append(", JSON_DATA TEXT NOT NULL ")
                                                .append(" ) ");
        this.executeDDL(tableExecuteCommand.toString(), TableDDLType.CREATE.name());
    }

    private void executeDDL (String ddlQuery, String ddlTypeForLog) {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(ddlQuery)) {
            preparedStatement.execute();
            this.connection.commit();
        } catch (SQLException e) {
            this.h2BackupLogger.warn("Failed to ", ddlTypeForLog, " syntax the Backup Table. Check your H2 Driver");
        }
    }

    @Override public void appendByJsonString(IlluminatiInterfaceType illuminatiInterfaceType, String jsonStringData) {
        StringBuilder insertExecuteCommand = new StringBuilder("INSERT INTO ")
                                                .append(TABLE_NAME)
                                                .append(" (EXECUTOR_TYPE, JSON_DATA) ")
                                                .append("VALUES (?, ?)");

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(insertExecuteCommand.toString())) {
            preparedStatement.setObject(1, illuminatiInterfaceType.getExecutorId());
            preparedStatement.setObject(2, jsonStringData);
            preparedStatement.execute();
            this.connection.commit();
        } catch (SQLException e) {
            this.h2BackupLogger.warn("Failed to insert data to Table.");
        }
    }

    @Override public List<T> getDataByList(boolean isPaging, boolean isAfterDelete, int from, int size) throws Exception {
        List<T> dataList = new ArrayList<>();
        List<Integer> idList = new ArrayList<>();

        try(
                PreparedStatement preparedStatement = this.connection.prepareStatement(this.getSelectQuery(isPaging, from, size));
                ResultSet rs = preparedStatement.executeQuery();
                ) {
            while (rs.next()) {
                idList.add(rs.getInt("ID"));
                try {
                    dataList.add(IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson(rs.getString("JSON_DATA"), this.type));
                } catch (JsonSyntaxException ex) {
                    this.h2BackupLogger.warn("Failed to json parse - JsonSyntaxException ()", ex.toString());
                }
            }
        } catch (SQLException e) {
            final String errorMessage = "Failed to select data from Table. ("+e.toString()+")";
            this.h2BackupLogger.warn(errorMessage, e);
            throw new Exception(errorMessage);
        }

        if (isAfterDelete && CollectionUtils.isNotEmpty(idList)) {
            for (Integer id : idList) {
                this.deleteById(id);
            }
        }

        return dataList;
    }

    @Override public Map<Integer, T> getDataByMap(boolean isPaging, boolean isAfterDelete, int from, int size) throws Exception {
        final String selectQuery = this.getSelectQuery(isPaging, from, size);

        if (!StringObjectUtils.isValid(selectQuery)) {
            throw new Exception("Check your select query.");
        }

        Map<Integer, T> dataMap = new HashMap<>();

        try(
                PreparedStatement preparedStatement = this.connection.prepareStatement(selectQuery);
                ResultSet rs = preparedStatement.executeQuery();
                ) {
            while (rs.next()) {
                try {
                    dataMap.put(rs.getInt("ID"), IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson(rs.getString("JSON_DATA"), this.type));
                } catch (JsonSyntaxException ex) {
                    this.h2BackupLogger.warn("Failed to json parse - JsonSyntaxException ()", ex.toString());
                }
            }
        } catch (SQLException e) {
            final String errorMessage = "Failed to select data from Table. ("+e.toString()+")";
            this.h2BackupLogger.warn(errorMessage, e);
            throw new Exception(errorMessage);
        }

        if (isAfterDelete && !dataMap.isEmpty()) {
            dataMap.forEach((key, value) -> this.deleteById(key));
        }

        return dataMap;
    }

    @Override public void deleteById(int id) {
        StringBuilder deleteExecuteCommand = new StringBuilder("DELETE FROM ")
                                                .append(TABLE_NAME)
                                                .append(" WHERE ID = ")
                                                .append(id);

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(deleteExecuteCommand.toString())) {
            preparedStatement.execute();
            this.connection.commit();
        } catch (SQLException e) {
            this.h2BackupLogger.warn("Failed to delete data from Table.");
        }
    }

    @Override public int getCount() throws Exception {
        StringBuilder countExecuteCommand = new StringBuilder("SELECT count(1) FROM ").append(TABLE_NAME);

        try(
                PreparedStatement preparedStatement = this.connection.prepareStatement(countExecuteCommand.toString());
                ResultSet rs = preparedStatement.executeQuery();
                ) {
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        } catch (SQLException e) {
            final String errorMessage = "Failed to select data from Table. ("+e.toString()+")";
            this.h2BackupLogger.warn(errorMessage, e);
            throw new Exception(errorMessage);
        }
    }

    private String getSelectQuery (boolean isPaging, int from, int size) {
        StringBuilder selectExecuteCommand = new StringBuilder("SELECT ID, JSON_DATA FROM ").append(TABLE_NAME);

        if (isPaging) {
            selectExecuteCommand.append(" LIMIT "+from+", "+size);
        }

        return selectExecuteCommand.toString();
    }
}
