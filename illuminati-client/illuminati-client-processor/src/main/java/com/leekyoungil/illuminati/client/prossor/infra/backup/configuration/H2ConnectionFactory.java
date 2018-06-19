package com.leekyoungil.illuminati.client.prossor.infra.backup.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 04/05/2018.
 */
public class H2ConnectionFactory {

    private final Logger h2ConnectionFactoryLogger = LoggerFactory.getLogger(this.getClass());

    private static H2ConnectionFactory H2_CONNECTION_FACTORY;

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:file:./illuminati-backup;FILE_LOCK=NO;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    private final int connectionValidCheckTimeout = 1000;

    private final Connection dbConnection;

    private H2ConnectionFactory () {
        this.dbConnection = this.getDBConnection();
    }

    public static H2ConnectionFactory getInstance () {
        if (H2_CONNECTION_FACTORY == null) {
            synchronized (H2ConnectionFactory.class) {
                if (H2_CONNECTION_FACTORY == null) {
                    H2_CONNECTION_FACTORY = new H2ConnectionFactory();
                }
            }
        }

        return H2_CONNECTION_FACTORY;
    }

    private Connection getDBConnection() {
        Connection dbConnection = null;

        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            h2ConnectionFactoryLogger.warn("H2 driver class not found.", e);
            return null;
        }

        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            dbConnection.setAutoCommit(true);
        } catch (SQLException e) {
            h2ConnectionFactoryLogger.warn("Failed to create H2 connection.", e);
        }

        return dbConnection;
    }

    public boolean isConnected () {
        try {
            if (this.dbConnection != null && this.dbConnection.isValid(this.connectionValidCheckTimeout)) {
                return true;
            }
        } catch (SQLException se) {
            h2ConnectionFactoryLogger.warn("Failed to connect to H2 connection.", se);
        }

        return false;
    }

    public Connection getDbConnection() {
        if (this.isConnected()) {
            return this.dbConnection;
        }

        return null;
    }
}
