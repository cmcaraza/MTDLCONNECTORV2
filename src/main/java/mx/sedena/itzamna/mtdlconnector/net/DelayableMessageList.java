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


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Defines a list of messages with an associated delay time.
 */
public class DelayableMessageList extends ArrayList<DelayableMessage> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The log. */
    private static Logger log = Logger.getLogger(DelayableMessageList.class);

    /** The listeners. */
    private static transient ArrayList<ListListener> listeners = new ArrayList<ListListener>();

    /** The list. */
    private static DelayableMessageList list = new DelayableMessageList();

    /**
     * Instantiates a new delayable message list.
     */
    private DelayableMessageList() {}

    /**
     * Gets the message list.
     *
     * @return the message list
     */
    public static DelayableMessageList getMessageList() {
        return list;
    }

    /**
     * Gets the message list.
     *
     * @param listener
     *           the listener
     * @return the message list
     */
    public static DelayableMessageList getMessageList(ListListener listener) {
        addListener(listener);
        return getMessageList();
    }

    /**
     * Adds the listener.
     *
     * @param listener
     *           the listener
     */
    public static void addListener(ListListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the listener.
     *
     * @param listener
     *           the listener
     */
    public static void removeListener(ListListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notify listeners.
     */
    private static void notifyListeners() {
        for (ListListener listener : listeners) {
            listener.listUpdated();
        }
    }

    /**
     * Save.
     *
     * @param fileName
     *           the file name
     */
    public static void save(String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(list);
            out.close();
        }
        catch (FileNotFoundException e) {
            log.debug(e);
        }
        catch (IOException e) {
            log.debug(e);
        }
    }

    /**
     * Append.
     *
     * @param fileName
     *           the file name
     */
    public static void append(String fileName) {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fis);
            DelayableMessageList tmp = (DelayableMessageList) in.readObject();
            list.addAll(tmp);
        }
        catch (FileNotFoundException e) {
            log.debug(e);
        }
        catch (IOException e) {
            log.debug(e);
        }
        catch (ClassNotFoundException e) {
            log.debug(e);
        }
        finally {
            notifyListeners();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.ArrayList#add(java.lang.Object)
     */
    @Override
    public boolean add(DelayableMessage message) {
        try {
            return super.add(message);
        }
        finally {
            notifyListeners();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.ArrayList#add(int, java.lang.Object)
     */
    @Override
    public void add(int index, DelayableMessage message) {
        super.add(index, message);
        notifyListeners();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.ArrayList#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object message) {
        try {
            return super.remove(message);
        }
        finally {
            notifyListeners();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.ArrayList#remove(int)
     */
    @Override
    public DelayableMessage remove(int index) {
        try {
            return super.remove(index);
        }
        finally {
            notifyListeners();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.ArrayList#clear()
     */
    @Override
    public void clear() {
        super.clear();
        notifyListeners();
    }

    /**
     * Swap.
     *
     * @param from
     *           the from
     * @param to
     *           the to
     */
    public void swap(int from, int to) {
        DelayableMessage message = super.remove(from);
        ensureCapacity(size() + 1);
        if (to > from) {
            to--;
        }
        super.add(to, message);
        notifyListeners();
    }
}
// UNCLASSIFIED