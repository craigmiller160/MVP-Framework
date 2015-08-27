package listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import stockmarket.gui.dialog.ListenerDialog;
import net.jcip.annotations.NotThreadSafe;

/**
 * An abstract implementation of the <tt>ListenerView</tt> and
 * <tt>PropertyChangeView</tt> interfaces. <tt>PropertyChangeView</tt>'s
 * sole method is inherited in abstract form and will need to be 
 * implemented by a subclass of this, but most of <tt>ListenerView</tt>'s
 * methods have an implementation here.
 * <p>
 * This class has been implemented to accept multiple external 
 * <tt>ActionListener</tt>s and store them in a list. This class
 * also implements <tt>actionPerformed(ActionEvent)</tt> inherited
 * from <tt>ActionListener</tt> as a final method. This implementation
 * simply creates a new <tt>ActionEvent</tt> with the same action command
 * as the one that was passed to it, but with this class as the event's
 * source. This allows for the external listening controller(s) to 
 * abstractly access this class, and the <tt>getValueForAction(String)</tt>
 * method specifically. (<b>NOTE:</b> If the event comes from an instance
 * of <tt>ListenerDialog</tt>, the source is not reassigned. This allows
 * events from the dialogs in this framework to convey their source to
 * the external listening controller(s), for the same reasons already
 * stated).
 * <p>
 * In addition to the <tt>changeProperty(PropertyChangeEvent)</tt> method 
 * inherited from <tt>PropertyChangeView</tt>, subclasses will need
 * to implement <tt>getValueForAction(String)</tt> from <tt>ListenerView</tt>
 * in order to extend this class. The documentation for these interfaces
 * should be consulted for the proper way to implement these methods.
 * <p>
 * <b>NOTE:</b> A view extending this class cannot also extend a GUI component class,
 * so views using this API will have to rely on composition (wrapping around
 * an instance of the GUI component their building) rather than inheritence
 * to create components.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe. All
 * methods in this class MUST be invoked on the <tt>EventDispatchThread</tt>.
 * 
 * @author Craig
 * @version 2.0
 * @see stockmarket.controller.AbstractListenerController AbstractListenerController
 * @see stockmarket.model.AbstractPropertyModel AbstractPropertyModel
 * @see stockmarket.gui.ListenerDialog ListenerDialog
 */
@NotThreadSafe
public abstract class AbstractListenerView 
implements ListenerView, PropertyChangeView{
	
	/**
	 * List of listeners/controllers assigned to this class.
	 */
	private final List<ActionListener> listeners;
	
	/**
	 * Creates a view class with default configuration.
	 */
	public AbstractListenerView(){
		this.listeners = new ArrayList<>(10);
	}
	
	@Override
	public void addActionListener(ActionListener listener){
		listeners.add(listener);
	}
	
	@Override
	public void removeActionListener(ActionListener listener){
		listeners.remove(listener);
	}
	
	/**
	 * Passes an event from an actionable component in the GUI to the
	 * listeners assigned to this view.
	 * 
	 * @param event the event that needs to be executed.
	 */
	@Override
	public final void actionPerformed(ActionEvent event){
		ActionEvent newEvent = null;
		if(!(event.getSource() instanceof ListenerDialog)){
			newEvent = new ActionEvent(
					this, ActionEvent.ACTION_PERFORMED, event.getActionCommand());
		}
		else{
			newEvent = event;
		}
		
		for(ActionListener listener : listeners){
			listener.actionPerformed(newEvent);
		}
	}
	
	
}
