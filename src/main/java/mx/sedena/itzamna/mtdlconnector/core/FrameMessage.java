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

import mx.sedena.itzamna.mtdlconnector.core.Message;

/**
 * Tags a message as a Frame Message.
 */
/*
 * This can probably be refactored away.
 */
public interface FrameMessage {

    /**
     * Gets the message.
     *
     * @return the message
     */
    public Message getMessage();
}
// UNCLASSIFIED