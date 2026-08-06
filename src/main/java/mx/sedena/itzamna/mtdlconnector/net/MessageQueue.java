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

package mx.sedena.itzamna.mtdlconnector.net;

import java.util.concurrent.DelayQueue;

/**
 * Defines a queue of messages to be sent.
 */

public class MessageQueue {

    /** The queue. */
    private static DelayQueue<DelayableMessage> queue = new DelayQueue<DelayableMessage>();

    /**
     * Instantiates a new message queue.
     */
    private MessageQueue() {}

    /**
     * Gets the queue.
     *
     * @return the queue
     */
    public static DelayQueue<DelayableMessage> getQueue() {
        return queue;
    }

    /**
     * Sets the queue.
     *
     * @param queue
     *           the new queue
     */
    public static void setQueue(DelayQueue<DelayableMessage> queue) {
        MessageQueue.queue = queue;
    }

}
// UNCLASSIFIED