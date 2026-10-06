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

import mx.sedena.itzamna.mtdlconnector.core.AirTrackData;
import mx.sedena.itzamna.mtdlconnector.core.C4ITrackData;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class MtdlDao {


    /**
     * Inserta o actualiza una traza aérea (AirTrackData) en la base de datos.
     */

    public void guardarTrazaAerea(AirTrackData track){

        String sql = "{CALL sp_upsert_air_track(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            // 1. Extraemos la información del POJO MTDL
            stmt.setString(1, track.getTrackNumber());
            stmt.setDouble(2, track.getLatitudeDegrees());
            stmt.setDouble(3, track.getLongitudeDegrees());
            stmt.setInt(4, track.getAltitude()); // Usualmente ya decodificado por ModeC

            // Convertimos arreglos de bytes a String para la identidad (limpiando espacios en blanco)
            String identity = new String(track.getIdentity()).trim();
            stmt.setString(5, identity);

            // 2. Ejecutamos la consulta en MySQL
            stmt.execute();
            // System.out.println("Traza aérea " + track.getTrackNumber() + " guardada en BD.");

        } catch (SQLException e) {
            System.err.println("Error en BD al guardar Traza Aérea: " + e.getMessage());
        }

    }

    public void guardarTrazaC4I(C4ITrackData track) {
        String sql = "{CALL sp_upsert_c4i_track(?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, track.getTrackNumber());
            stmt.setDouble(2, track.getLatitudeDegrees());
            stmt.setDouble(3, track.getLongitudeDegrees());
          //  stmt.setString(4, String.valueOf(track.getTrackClass()));

            stmt.execute();

        } catch (SQLException e) {
            System.err.println("Error en BD al guardar Traza C4I: " + e.getMessage());
        }
    }



}
