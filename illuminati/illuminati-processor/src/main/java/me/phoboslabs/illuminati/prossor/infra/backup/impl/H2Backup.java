package me.phoboslabs.illuminati.prossor.infra.backup.impl;

import com.google.gson.JsonSyntaxException;
import me.phoboslabs.illuminati.prossor.infra.backup.Backup;
import me.phoboslabs.illuminati.prossor.infra.backup.configuration.H2ConnectionFactory;
import me.phoboslabs.illuminati.prossor.infra.backup.enums.TableDDLType;
import me.phoboslabs.illuminati.prossor.properties.IlluminatiH2Properties;
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

    private final H2ConnectionFactory H2_CONN = H2ConnectionFactory.getInstance();
    private Connection connection = null;
    private static final String TABLE_NAME = "illuminati_backup";

    private H2Backup (Class<T> type) {
        this.type = type;
        if (H2_CONN.isConnected()) {
            this.connection = H2_CONN.getDbConnection();

            final String backTableReset = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiH2Properties.class, "illuminati", "backTableReset", "false");
            if ("true".equalsIgnoreCase(backTableReset)) {
                this.tableDDL(TableDDLType.DROP);
            }
            this.tableDDL(TableDDLType.CREATE);
        }
    }

    public static H2Backup getInstance (Class type) {
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
        StringBuilder tableExecuteCommand = new StringBuilder();
        tableExecuteCommand.append("DROP TABLE IF EXISTS ");
        tableExecuteCommand.append(TABLE_NAME);

        this.executeDDL(tableExecuteCommand.toString(), TableDDLType.DROP.name());
    }

    private void createTable () {
        StringBuilder tableExecuteCommand = new StringBuilder();
        tableExecuteCommand.append("CREATE TABLE IF NOT EXISTS ");
        tableExecuteCommand.append(TABLE_NAME);
        tableExecuteCommand.append(" ( ");
        tableExecuteCommand.append(" ID INTEGER PRIMARY KEY AUTO_INCREMENT");
        tableExecuteCommand.append(", EXECUTOR_TYPE INTEGER NOT NULL");
        tableExecuteCommand.append(", JSON_DATA TEXT NOT NULL ");
        tableExecuteCommand.append(" ) ");

        this.executeDDL(tableExecuteCommand.toString(), TableDDLType.CREATE.name());
    }

    private void executeDDL (String ddlQuery, String ddlTypeForLog) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(ddlQuery);
            preparedStatement.execute();
            this.connection.commit();
            preparedStatement.close();
        } catch (SQLException e) {
            this.h2BackupLogger.warn("Failed to ", ddlTypeForLog, " syntax the Backup Table. Check your H2 Driver");
        }
    }

    @Override public void appendByJsonString(IlluminatiInterfaceType illuminatiInterfaceType, String jsonStringData) {
        StringBuilder insertExecuteCommand = new StringBuilder();
        insertExecuteCommand.append("INSERT INTO ");
        insertExecuteCommand.append(TABLE_NAME);
        insertExecuteCommand.append(" (EXECUTOR_TYPE, JSON_DATA) ");
        insertExecuteCommand.append("VALUES (?, ?)");

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(insertExecuteCommand.toString());
            preparedStatement.setObject(1, illuminatiInterfaceType.getExecutorId());
            preparedStatement.setObject(2, jsonStringData);
            preparedStatement.execute();
            this.connection.commit();
            preparedStatement.close();
        } catch (SQLException e) {
            this.h2BackupLogger.warn("Failed to insert data to Table.");
        }
    }

    @Override public List<T> getDataByList(boolean isPaging, boolean isAfterDelete, int from, int size) {
        List<T> dataList = new ArrayList<T>();
        List<Integer> idList = new ArrayList<Integer>();

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(this.getSelectQuery(isPaging, from, size));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                idList.add(rs.getInt("ID"));
                try {
                    dataList.add(IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson(rs.getString("JSON_DATA"), this.type));
                } catch (JsonSyntaxException ex) {
                    this.h2BackupLogger.warn("Failed to json parse - JsonSyntaxException ()", ex.getMessage());
                }
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            this.h2BackupLogger.warn("Failed to select data from Table.");
            return null;
        }

        if (isAfterDelete && CollectionUtils.isNotEmpty(idList)) {
            for (Integer id : idList) {
                this.deleteById(id);
            }
        }

        return dataList;
    }

    @Override public Map<Integer, T> getDataByMap(boolean isPaging, boolean isAfterDelete, int from, int size) {
        final String selectQuery = this.getSelectQuery(isPaging, from, size);

        if (StringObjectUtils.isValid(selectQuery) == false) {
            return null;
        }

        Map<Integer, T> dataMap = new HashMap<Integer, T>();

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(selectQuery);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                try {
                    dataMap.put(rs.getInt("ID"), IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson(rs.getString("JSON_DATA"), this.type));
                } catch (JsonSyntaxException ex) {
                    this.h2BackupLogger.warn("Failed to json parse - JsonSyntaxException ()", ex.getMessage());
                }
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            this.h2BackupLogger.warn("Failed to select data from Table.");
            return null;
        }

        if (isAfterDelete && dataMap.isEmpty() == false) {
            for (Map.Entry<Integer, T> entry : dataMap.entrySet()) {
                this.deleteById(entry.getKey());
            }
        }

        return dataMap;
    }

    @Override public void deleteById(int id) {
        StringBuilder deleteExecuteCommand = new StringBuilder();
        deleteExecuteCommand.append("DELETE FROM ");
        deleteExecuteCommand.append(TABLE_NAME);
        deleteExecuteCommand.append(" WHERE ID = ");
        deleteExecuteCommand.append(id);

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(deleteExecuteCommand.toString());
            preparedStatement.execute();
            this.connection.commit();
            preparedStatement.close();
        } catch (SQLException e) {
            this.h2BackupLogger.warn("Failed to delete data from Table.");
        }
    }

    @Override public int getCount() {
        int resultCount = 0;

        StringBuilder countExecuteCommand = new StringBuilder();
        countExecuteCommand.append("SELECT count(1) FROM ");
        countExecuteCommand.append(TABLE_NAME);

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(countExecuteCommand.toString());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                resultCount = rs.getInt(1);
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            this.h2BackupLogger.warn("Failed to select data from Table.");
            return 0;
        }

        return resultCount;
    }

    private String getSelectQuery (boolean isPaging, int from, int size) {
        StringBuilder selectExecuteCommand = new StringBuilder();
        selectExecuteCommand.append("SELECT ID, JSON_DATA FROM ");
        selectExecuteCommand.append(TABLE_NAME);

        if (isPaging) {
            selectExecuteCommand.append(" LIMIT "+from+", "+size);
        }

        return selectExecuteCommand.toString();
    }
}
