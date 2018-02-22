package com.leekyoungil.illuminati.client.prossor.infra.backup.impl;

import com.leekyoungil.illuminati.common.dto.enums.IlluminatiInterfaceType;
import com.leekyoungil.illuminati.client.prossor.infra.backup.Backup;
import com.leekyoungil.illuminati.client.prossor.infra.backup.configuration.H2ConnectionFactory;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import org.apache.commons.collections.CollectionUtils;
import org.h2.tools.DeleteDbFiles;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class H2Backup<T> implements Backup<T> {

    private static H2Backup H2_BACKUP;

    private final H2ConnectionFactory H2_CONN = H2ConnectionFactory.getInstance();
    private Connection connection = null;
    private static final String TABLE_NAME = "illuminati_backup";

    private H2Backup () {
        if (H2_CONN.isConnected() == true) {
            this.connection = H2_CONN.getDbConnection();
            this.deleteTable();
            this.createTable();
        }
    }

    public static H2Backup getInstance () {
        if (H2_BACKUP == null) {
            synchronized (H2Backup.class) {
                if (H2_BACKUP == null) {
                    H2_BACKUP = new H2Backup();
                }
            }
        }

        return H2_BACKUP;
    }

    private void deleteTable() {
        DeleteDbFiles.execute("./", TABLE_NAME, true);
    }

    private void createTable () {
        //CREATE TABLE IF NOT EXISTS TEST(ID INT PRIMARY KEY, NAME VARCHAR(255));
        StringBuilder tableExecuteCommand = new StringBuilder();
        tableExecuteCommand.append("CREATE TABLE IF NOT EXISTS ");
        tableExecuteCommand.append(TABLE_NAME);
        tableExecuteCommand.append(" ( ");
        tableExecuteCommand.append(" ID INTEGER PRIMARY KEY AUTO_INCREMENT");
        tableExecuteCommand.append(", EXECUTOR_TYPE INTEGER NOT NULL");
        tableExecuteCommand.append(", DATA OTHER NOT NULL ");
        tableExecuteCommand.append(" ) ");

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(tableExecuteCommand.toString());
            preparedStatement.execute();
            this.connection.commit();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override public void append(IlluminatiInterfaceType illuminatiInterfaceType, T data) {
        StringBuilder insertExecuteCommand = new StringBuilder();
        insertExecuteCommand.append("INSERT INTO ");
        insertExecuteCommand.append(TABLE_NAME);
        insertExecuteCommand.append(" (EXECUTOR_TYPE, DATA) ");
        insertExecuteCommand.append("VALUES (?, ?)");

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(insertExecuteCommand.toString());
            preparedStatement.setObject(1, illuminatiInterfaceType.getExecutorId());
            preparedStatement.setObject(2, data);
            preparedStatement.execute();
            this.connection.commit();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
                dataList.add((T) rs.getString("DATA"));
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        if (isAfterDelete == true && CollectionUtils.isNotEmpty(idList) == true) {
            for (Integer id : idList) {
                this.deleteById(id);
            }
        }

        return dataList;
    }

    @Override public Map<Integer, T> getDataByMap(boolean isPaging, boolean isAfterDelete, int from, int size) {
        String selectQuery = this.getSelectQuery(isPaging, from, size);

        if (StringObjectUtils.isValid(selectQuery) == false) {
            return null;
        }

        Map<Integer, T> dataMap = new HashMap<Integer, T>();

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(selectQuery);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                dataMap.put(rs.getInt("ID"), (T) rs.getString("DATA"));
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        if (isAfterDelete == true && dataMap.isEmpty() == false) {
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
            e.printStackTrace();
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
            if (rs.next() == true) {
                resultCount = rs.getInt(1);
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

        return resultCount;
    }

    private String getSelectQuery (boolean isPaging, int from, int size) {
        StringBuilder selectExecuteCommand = new StringBuilder();
        selectExecuteCommand.append("SELECT ID, DATA FROM ");
        selectExecuteCommand.append(TABLE_NAME);

        if (isPaging == true) {
            selectExecuteCommand.append(" LIMIT "+from+", "+size);
        }

        return selectExecuteCommand.toString();
    }
}
