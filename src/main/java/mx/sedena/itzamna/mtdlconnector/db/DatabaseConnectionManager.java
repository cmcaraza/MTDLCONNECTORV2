/*
 * UNCLASSIFIED
 *
 * Revision History:
 *
 *   Date     SEC.No.  Programmer           Description
 * ---------- ------- -------------------- -------------------------------------
 * 2026-07-31 IMDL001  Zopiloman            Initial release
 *
 */

package mx.sedena.itzamna.mtdlconnector.db;

import mx.sedena.itzamna.mtdlconnector.util.ConfigManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {

    private static DatabaseConnectionManager instace;

    private Connection connection;

    private DatabaseConnectionManager() {

        conectar();

    }

    public static synchronized DatabaseConnectionManager getInstance(){

        if(instace == null){

            instace = new DatabaseConnectionManager();

        }
        return instace;

    }

    /**
     * Establece la conexión solicitando las credenciales al ConfigManager.
     */

    private void conectar(){

        ConfigManager config = ConfigManager.getInstance();

        try{
            this.connection = DriverManager.getConnection(

                    config.getDbUrl(),
                    config.getDbUser(),
                    config.getDbPass()
            );

            System.out.println("Conexión a MySQL (" + config.getDbUrl() + ") establecida con éxito.");

        }catch (SQLException e){

            System.err.println("Fallo al conectar con MYSQL: " + e.getMessage());

        }

    }

/**
 * Devuelve la conexión activa. Si se cerró (por ejemplo, por un reinicio
 * del servicio de MySQL o un timeout), intenta reconectar automáticamente.
 */

public Connection getConnection(){

    try {

        if (this.connection == null || this.connection.isClosed()){

            System.out.println("La conexión estaba cerrada. Intentando reconectar...");
            conectar();
        }


    }catch (SQLException e){

        System.err.println("Error al verificar el estado de la conexión: " + e.getMessage());
    }

    return this.connection;
}

}
