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

import java.io.Serializable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import mx.sedena.itzamna.mtdlconnector.core.Message;

/**
 * Defines a delayable message, intended to be used with a BlockingQueue. The
 * message will be removed from the queue after the specified amount of time has
 * elapsed.
 */
public class DelayableMessage implements Delayed, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The message. */
    private Message message = null;

    /** The start time. */
    private static transient long startTime = 0;

    /** The delay. */
    private long delay = 0;

    /** The units. */
    private static TimeUnit UNITS = TimeUnit.MILLISECONDS;

    /**
     * Instantiates a new delayable message.
     *
     * @param message
     *           the message
     */
    public DelayableMessage(Message message) {
        this(message, 0);
    }

    /**
     * Instantiates a new delayable message.
     *
     * @param message
     *           the message
     * @param delay
     *           the delay
     */
    public DelayableMessage(Message message, long delay) {
        this.message = message;
        this.delay = delay;

    }

    /**
     * Sets the start time.
     *
     * @param startTime
     *           the new start time
     */
    public static void setStartTime(long startTime) {
        DelayableMessage.startTime = startTime;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Delayed o) {
        int compare = 0;
        if (this.getDelay(UNITS) < o.getDelay(UNITS)) {
            compare = -1;
        }
        if (this.getDelay(UNITS) > o.getDelay(UNITS)) {
            compare = 1;
        }
        return compare;

    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.concurrent.Delayed#getDelay(java.util.concurrent.TimeUnit)
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert((startTime - System.currentTimeMillis()) + delay,
                UNITS);
    }

    /**
     * Sets the delay time.
     *
     * @param delay
     *           the new delay time
     */
    public void setDelayTime(long delay) {
        this.delay = delay;
    }

    /**
     * Gets the delay time.
     *
     * @return the delay time
     */
    public long getDelayTime() {
        return delay;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public Message getMessage() {
        return message;
    }
}
// UNCLASSIFIED