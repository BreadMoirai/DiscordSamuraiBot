/*
 *       Copyright 2017 Ton Ly (BreadMoirai)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.github.breadmoirai.database;

import org.jdbi.v3.core.ConnectionFactory;
import org.jdbi.v3.core.Jdbi;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides a Singleton instance of a JDBC compatible database connection through JDBI.
 * In order to use the Database, a connection must be provided to {@link Database#create(ConnectionFactory)}.
 * <p>
 * <p>Required Dependencies: (in gradle format)
 * <pre><code>
 *     compile 'org.jdbi:jdbi3:3.0.0-beta1'
 *     compile 'org.jdbi:jdbi3-parent:3.0.0-beta1'
 *     compile 'org.jdbi:jdbi3-sqlobject:3.0.0-beta1'
 </code></pre>
 *
 */
public class Database {

    private static Database instance;

    public static Jdbi get() {
        if (instance == null) throw new NullPointerException("Database has not been created.");
        return instance.jdbi;
    }

    private final Jdbi jdbi;

    /**
     * Supply a connection to a database.
     * <p>
     * This can be used with a lambda expression as such:
     * <pre>
     * <code>
     * {@link Database}.create(() -> {@link java.sql.DriverManager}.{@link java.sql.DriverManager#getConnection(String) getConnection}("jdbc:derby:myDB;create=true"))
     * </code>
     * </pre>
     *
     * @param connectionFactory This is equivalent to a {@link java.util.function.Supplier Supplier}{@literal <Connection>}
     */
    public static void create(ConnectionFactory connectionFactory) {
        instance = new Database(Jdbi.create(connectionFactory));
    }

    private Database(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public static boolean hasTable(String tableName) {
        if (instance == null) throw new NullPointerException("Database has not been created.");
        return instance.tableExists(tableName);
    }

    private boolean tableExists(String tableName) {
        final String s = tableName.toUpperCase();
        return jdbi.withHandle(handle -> {
            try {
                final DatabaseMetaData metaData = handle.getConnection().getMetaData();
                try (ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"})) {
                    while (tables.next()) {
                        if (tables.getString("TABLE_NAME").equals(s)) {
                            return true;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(5);
            }
            return false;
        });
    }

}