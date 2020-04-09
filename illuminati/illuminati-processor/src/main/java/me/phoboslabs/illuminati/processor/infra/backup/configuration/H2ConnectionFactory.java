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

package me.phoboslabs.illuminati.processor.infra.backup.configuration;

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

    private H2ConnectionFactory () throws Exception {
        this.dbConnection = this.getDBConnection();
    }

    public static H2ConnectionFactory getInstance () throws Exception {
        if (H2_CONNECTION_FACTORY == null) {
            synchronized (H2ConnectionFactory.class) {
                if (H2_CONNECTION_FACTORY == null) {
                    H2_CONNECTION_FACTORY = new H2ConnectionFactory();
                }
            }
        }

        return H2_CONNECTION_FACTORY;
    }

    private Connection getDBConnection() throws Exception {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            final String errorMessage = "H2 driver class not found. ("+e.toString()+")";
            h2ConnectionFactoryLogger.warn(errorMessage, e);
            throw new Exception(errorMessage);
        }

        try {
            Connection dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            dbConnection.setAutoCommit(true);
            return dbConnection;
        } catch (SQLException e) {
            final String errorMessage = "Failed to create H2 connection. ("+e.toString()+")";
            h2ConnectionFactoryLogger.warn(errorMessage, e);
            throw new Exception(errorMessage);
        }
    }

    public boolean isConnected () {
        try {
            if (this.dbConnection != null && this.dbConnection.isValid(this.connectionValidCheckTimeout)) {
                return true;
            }
            throw new SQLException();
        } catch (SQLException se) {
            h2ConnectionFactoryLogger.warn("Failed to connect to H2 connection.", se);
            return false;
        }
    }

    public Connection getDbConnection() throws Exception {
        if (this.isConnected()) {
            return this.dbConnection;
        }
        throw new Exception("Can't connect to database.");
    }
}
