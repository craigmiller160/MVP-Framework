package listener;

import stockmarket.gui.ListenerView;

/**
 * Interface extending the <tt>ListenerView</tt> interface and
 * adding methods specifically intended for dialog implementations.
 * The main purpose of this interface is to allow dialogs to best
 * utilize the <ttListenerView</tt> API, and so the documentations
 * for that super-interface should be consulted before implementing
 * this interface.
 * <p>
 * In addition to the <tt>ListenerView</tt> methods, this interface
 * provides four dialog-specific methods that will need to be 
 * implemented. <tt>showDialog()</tt> and <tt>hideDialog()</tt>
 * display and dispose of the dialog window respectively.
 * <tt>setModal(boolean)</tt> and <tt>getModal()</tt> deal
 * with the modality of the dialog, and allow it to be
 * determined and changed if necessary.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe, and all operations
 * on the dialogs inheriting this interface must be done from the
 * <tt>EventDispatchThread</tt>
 * 
 * @author craig
 * @version 2.0
 */
public interface ListenerDialog extends ListenerView {

	/**
	 * Display the dialog in the GUI. The dialog should not
	 * be visible prior to invoking this method, as this
	 * provides time for the dialog to be configured prior
	 * to it being displayed.
	 */
	void showDialog();
	
	/**
	 * Close the dialog when it is no longer needed.
	 */
	void closeDialog();
	
	/**
	 * Get whether or not this dialog is modal.
	 * 
	 * @return true if this dialog is modal.
	 */
	boolean getModal();
	
	/**
	 * Set the modality of this dialog.
	 * 
	 * @param modal whether or not this dialog is modal.
	 */
	void setModal(boolean modal);
	
}
