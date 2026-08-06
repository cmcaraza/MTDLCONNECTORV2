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



/**
 * The listener interface for receiving list events.
 * The class that is interested in processing a list
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addListListener<code> method. When
 * the list event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ListEvent
 */
public interface ListListener {

    /**
     * Invoked when list update occurs.
     */
    public void listUpdated();
}
// UNCLASSIFIED