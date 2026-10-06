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
package mx.sedena.itzamna.mtdlconnector;

import mx.sedena.itzamna.mtdlconnector.db.DatabaseConnectionManager;
import mx.sedena.itzamna.mtdlconnector.net.Network;
import mx.sedena.itzamna.mtdlconnector.util.ConfigManager;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {

        System.out.println("==================================================");
        System.out.println("   INICIANDO CONECTOR MTDL - PROYECTO ITZAMNA     ");
        System.out.println("==================================================");

        try {
            // 1. Cargar la configuración desde el archivo XML
            System.out.println("[1/3] Cargando configuración táctica XML...");
            ConfigManager config = ConfigManager.getInstance();
            System.out.println("      - IP Local MTDL : " + config.getMtdl_LocalIp() + ":" + config.getMtdl_LocalPort());
            System.out.println("      - IP Remota MTDL: " + config.getMtdlRemoteIp() + ":" + config.getMtdl_RemotePort());
            System.out.println("      - Identidad     : " + config.getParticipantId());

            // 2. Verificar la conexión a la Base de Datos (MySQL)
            System.out.println("[2/3] Levantando conexión a Base de Datos...");
            Connection conn = DatabaseConnectionManager.getInstance().getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("      - Conexión MySQL: ESTABLECIDA");
            } else {
                System.err.println("      - ALERTA: No se pudo conectar a MySQL. El sistema arrancará, pero no habrá persistencia.");
            }

            // 3. Iniciar el motor de red (Receiver y Sender)
            System.out.println("[3/3] Iniciando Sockets de Red MTDL...");
            Network.connect();
            System.out.println("      - Sockets activos y escuchando.");

            System.out.println("==================================================");
            System.out.println(" SISTEMA OPERATIVO. ESPERANDO TRÁFICO TÁCTICO...  ");
            System.out.println("==================================================");

            // 4. Registrar un "Shutdown Hook" para cerrar todo limpiamente si se detiene el servicio
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n[!] Apagando Conector MTDL...");
                Network.disconnect();
                System.out.println("[!] Sistema detenido correctamente.");
            }));

            // Mantenemos el hilo principal vivo (modo servidor)
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("Fallo crítico durante el arranque del sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}