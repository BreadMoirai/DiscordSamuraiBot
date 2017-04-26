/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package samurai.database;

import samurai.database.impl.SDatabaseImpl;

import java.sql.SQLException;

/**
 * @author TonTL
 * @version 3/23/2017
 */
public class Database {

    private static final SDatabase database;
    private static boolean open;

    static {
        try {
            database = new SDatabaseImpl();
            open = true;
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
            throw new ExceptionInInitializerError("Failed to initialize database");
        }
    }


    public static SDatabase getDatabase() {
        if (!open) throw new UnsupportedOperationException("Cannot retrieve closed database.");
        return database;
    }

    public static void close() {
        if (!open) throw new UnsupportedOperationException("Database is already closed");
        else {
            open = false;
            database.close();
        }
    }
}
