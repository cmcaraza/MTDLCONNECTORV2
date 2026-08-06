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
package mx.sedena.itzamna.mtdlconnector.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ConfigManager {

    private static ConfigManager instance;

    // Variables de Base de Datos
    private String dbHost;
    private String dbName;
    private String dbUser;
    private String dbPass;
    private String dbPort;

    // Variables de Red MTDL
    private String mtdl_LocalIp;
    private  String mtdlRemoteIp;
    private int mtdl_LocalPort;
    private int mtdl_RemotePort;
    private String mtdlNetInterface;

    private ConfigManager() {
        loadConfig();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadConfig() {
        try {
            File xmlFile = new File("/cmcasharepointclient/settings/cmca_config.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Extracción de parámetros de Base de Datos
            this.dbHost = getTextValue(doc, "HOSTNAME", "host");
            this.dbName = getTextValue(doc, "DATABASENAME", "database");
            this.dbUser = getTextValue(doc, "USERNAME", "user");
            this.dbPass = getTextValue(doc, "PASS", "password");
            this.dbPort = getTextValue(doc, "PORT", "portnumber");

            // Extracción de parámetros del nuevo bloque MTDL
            this.mtdl_LocalIp = getTextValue(doc, "MTDL", "ip");
            this.mtdlNetInterface = getTextValue(doc, "MTDL", "net_interface");

            // Parseo seguro del puerto
            String portStr = getTextValue(doc, "MTDL", "puerto");
            if (!portStr.isEmpty()) {
                this.mtdl_LocalPort = Integer.parseInt(portStr);
            }

        } catch (Exception e) {
            System.err.println("Error crítico: No se pudo leer el archivo XML. " + e.getMessage());
        }
    }

    private String getTextValue(Document doc, String parentTag, String childTag) {
        Element parent = (Element) doc.getElementsByTagName(parentTag).item(0);
        if (parent != null) {
            return parent.getElementsByTagName(childTag).item(0).getTextContent();
        }
        return "";
    }

    // Getters para la Base de Datos
    public String getDbUrl() {
        return "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?useSSL=false&serverTimezone=UTC";
    }
    public String getDbUser() { return dbUser; }
    public String getDbPass() { return dbPass; }

    // Getters para la Red MTDL
    public String getMtdl_LocalIp()     { return mtdl_LocalIp; }
    public String getMtdlRemoteIp()     { return  mtdlRemoteIp; }
    public int getMtdl_LocalPort()      { return mtdl_LocalPort; }
    public  int getMtdl_RemotePort()    { return mtdl_RemotePort; }
    public String getMtdlNetInterface() { return mtdlNetInterface; }

}