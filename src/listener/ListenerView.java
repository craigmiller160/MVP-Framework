package listener;

import java.awt.event.ActionListener;

/**
 * This interface provides a basic API to allow for a GUI class
 * to pass events to <tt>ActionListener</tt>s outside of the class.
 * This design is meant to facilitate loose coupling between view
 * and controller components of a program, allowing them to interact
 * through the <tt>ActionListener</tt> interface without needing
 * direct knowledge of each other.
 * <p>
 * In addition to providing methods to add and remove external
 * <tt>ActionListener</tt>s, this interface extends the <tt>ActionListener</tt>
 * interface itself. This allows implementing classes to simply assign
 * <tt>this</tt> as the listener for all actionable components, 
 * easily funneling all events to an external listener without the
 * components needing any knowledge of the listener's existence.
 * <p>
 * Lastly, the <tt>getValueForAction(String)</tt> method is provided as a tool
 * to allow the external listeners to perform the proper action. 
 * Many times, the action that needs to be done will require a value
 * inputed by the user. This method provides an abstract way to acquire
 * the necessary value(s) based on the <tt>actionCommand</tt> parameter
 * passed to the method. <b>NOTE:</b> This works best when action command
 * values are set as constants.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe. Since this interface
 * is intended to be used with Swing classes, its methods should
 * only be accessed on the <tt>EventDispatchThread</tt>.
 * 
 * @author craig
 * @version 2.0
 */
public interface ListenerView 
extends ActionListener{

	/**
	 * Returns a value from the view that is needed to perform
	 * a given action. The <tt>actionCommand</tt> parameter passed
	 * here determines which, if any, value will be returned. If
	 * no value is needed for a particular action, simply return
	 * <tt>null</tt>.
	 * <p>
	 * <b>THREAD SAFETY:</b> Again, this is a Swing class. Controllers that
	 * off-load their tasks to other threads need to make sure that they only
	 * call on this method on the <tt>EventDispatchThread</tt>.
	 * 
	 * @param actionCommand the action command for the action being performed,
	 * to determine which, if any, values are needed from this view.
	 * @return the value needed for the specified action, or <tt>null</tt> if
	 * no value is needed.
	 */
	Object getValueForAction(String actionCommand);
	
	/**
	 * Adds an <tt>ActionListener</tt> to the list of listeners in this view. 
	 * The listener being passed to the view should function as a controller 
	 * for implementing business logic in response to user-triggered <tt>ActionEvents</tt> 
	 * in the GUI.
	 * 
	 * @param listener the listener being added to this view.
	 */
	void addActionListener(ActionListener listener);
	
	/**
	 * Removes an <tt>ActionListener</tt> to the list of listeners in this view. 
	 * 
	 * @param listener the listener being removed from this view.
	 */
	void removeActionListener(ActionListener listener);
	
}
