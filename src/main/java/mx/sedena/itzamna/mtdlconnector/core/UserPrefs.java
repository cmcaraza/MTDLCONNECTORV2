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

package mx.sedena.itzamna.mtdlconnector.core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Wrapper for a properties file to persist user settings.
 */
public final class UserPrefs {

    /** The Constant PROPS_FILE. */
    private static final String PROPS_FILE = "/cmcasharepointclient/cmcasettings/AEW-C4I.props.xml";

    /** The log. */
    private static Logger log = Logger.getLogger(UserPrefs.class);

    /** The Constant LAST_OPEN_DIRECTORY. */
    public static final String LAST_OPEN_DIRECTORY = "directory.last";

    /** The Constant LOCAL_ADDRESS. */
    public static final String LOCAL_ADDRESS = "address.local.ip";

    /** The Constant LOCAL_PORT. */
    public static final String LOCAL_PORT = "address.local.port";

    /** The Constant REMOTE_ADDRESS. */
    public static final String REMOTE_ADDRESS = "address.remote.ip";

    /** The Constant REMOTE_PORT. */
    public static final String REMOTE_PORT = "address.remote.port";

    /** The Constant FRAME_WINDOW. */
    public static final String FRAME_WINDOW = "frame.message_window.millis";

    /** The Constant FRAME_MAX_MSG_COUNT. */
    public static final String FRAME_MAX_MSG_COUNT = "frame.max_message_count";

    /** The Constant LINK_ID. */
    public static final String LINK_ID = "link.participant_id";

    /** The Constant LINK_REMOTE_ID. */
    public static final String LINK_REMOTE_ID = "link.remote_participant_id";

    /** The Constant PASSWORD. */
    public static final String PASSWORD = "link.password";

    public static final String HEARTBEAT_TIMEOUT = "heartbeat.timeout";

    public static final String HEARTBEAT_RATE = "freq.heartbeat.millis";

    public static final String LINK_STATUS_RATE = "freq.link_status.millis";

    public static final String EXTRAPOLATE_RATE = "freq.extrapolate.millis";

    /**
     * Gets the props.
     *
     * @return the props
     */
    public static Properties getProps() {
        return props;
    }

    /**
     * Save.
     */
    public static void save() {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(UserPrefs.PROPS_FILE);
            getProps().storeToXML(os, "AEW-C4I Simulator Properties");
        }
        catch (IOException ex) {
            log.error("", ex);
        }
        finally {
            try {
                os.close();
            }
            catch (IOException e) {
                log.error("", e);
            }
        }
    }

    /** The props. */
    private static Properties props = new Properties();

    /**
     * Instantiates a new user prefs.
     */
    private UserPrefs() {}

    /**
     * Load.
     */
    private static void load() {
        FileInputStream is = null;
        try {
            is = new FileInputStream(UserPrefs.PROPS_FILE);
            getProps().loadFromXML(is);
        }
        catch (IOException e) {
            log.info(UserPrefs.PROPS_FILE + " not found, using defaults.");
        }
    }

    static {
        load();
    }
}
// UNCLASSIFIED