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

/**
 * The Class InvalidMessageException.
 */
@SuppressWarnings("serial")
public class InvalidMessageException extends Exception {

    /**
     * Instantiates a new invalid message exception.
     *
     * @param msg
     *           the msg
     */
    public InvalidMessageException(String msg) {
        super(msg);
    }

}