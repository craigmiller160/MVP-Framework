package listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract implementation of <tt>ListenerDialog</tt> that
 * mirrors the <tt>AbstractListenerView</tt> class. The main
 * difference between the two is that this class does not
 * also implement the <tt>PropertyChangeView</tt> interface,
 * as dialogs have a short lifespan and in general won't
 * persist long enough to need to receive <tt>PropertyChangeEvent</tt>s.
 * If a unique case arises where such functionality would be 
 * necessary, that interface can still be implemented by
 * a subclass of this.
 * <p>
 * Like <tt>AbstractListenerView</tt>, this class has been implemented to accept multiple external 
 * <tt>ActionListener</tt>s and store them in a list. This class
 * also implements <tt>actionPerformed(ActionEvent)</tt> inherited
 * from <tt>ActionListener</tt> as a final method. This implementation
 * simply creates a new <tt>ActionEvent</tt> with the same action command
 * as the one that was passed to it, but with this class as the event's
 * source. This allows for the external listening controller(s) to 
 * abstractly access this class, and the <tt>getValueForAction(String)</tt>
 * method specifically.
 * <p>
 * The <tt>getValueForAction(String)</tt> method inherited from the 
 * <tt>ListenerView</tt> interface will also need to be implemented by 
 * subclasses. The documentation for <tt>ListenerView</tt> should be 
 * consulted for the best way to implement this method.
 * <p>
 * <b>NOTE:</b> A view extending this class cannot also extend a GUI component class,
 * so views using this API will have to rely on composition (wrapping around
 * an instance of the GUI component their building) rather than inheritence
 * to create components.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe. All
 * methods in this class MUST be invoked on the <tt>EventDispatchThread</tt>.
 * 
 * @author craig
 * @version 2.0
 * @see stockmarket.gui.AbstractListenerView AbstractListenerView
 * @see stockmarket.gui.ListenerView ListenerView
 */
public abstract class AbstractListenerDialog
implements ListenerDialog {

	/**
	 * List of listeners/controllers assigned to this class.
	 */
	private final List<ActionListener> listeners;
	
	/**
	 * Constructor that initializes the listener list.
	 */
	public AbstractListenerDialog() {
		listeners = new ArrayList<>();
	}

	/**
	 * Add the specified <tt>ActionListener</tt> to this
	 * dialog. The listener should function as an external
	 * controller for executing actions invoked by this dialog.
	 * 
	 * @param listener the listener to add to this dialog.
	 */
	@Override
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove the specified <tt>ActionListener</tt> from this
	 * dialog.
	 * 
	 * @param listener the listener to remove from this dialog.
	 */
	@Override
	public void removeActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Passes an event from an actionable component in the dialog to the
	 * listeners assigned to this dialog.
	 * 
	 * @param event the event that needs to be executed.
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		ActionEvent newEvent = new ActionEvent(
				this, ActionEvent.ACTION_PERFORMED, event.getActionCommand());
		
		for(ActionListener l : listeners){
			l.actionPerformed(newEvent);
		}
	}

}
