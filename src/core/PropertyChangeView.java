package core;

import java.beans.PropertyChangeEvent;

import net.jcip.annotations.NotThreadSafe;

/**
 * A basic interface for a simple API for making GUI classes that can
 * be updated abstractly in a Model-View-Presenter design pattern.
 * Its single method, <tt>changeProperty(PropertyChangeEvent)</tt>, allows any controller 
 * looking to update it to pass <tt>PropertyChangeEvent</tt>s to
 * the class via this interface using dependency inversion. The GUI 
 * class then handles the event without the controller
 * needing any knowledge of the view's implementation. The <tt>PropertyChangeEvent</tt>
 * passed to the method can be acted on or ignored, depending on the event's
 * attributes.
 * <p>
 * Property name values should be constants stored in a single class.
 * This ensures consistency and avoids errors.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe. All
 * methods in this class MUST be invoked on the <tt>EventDispatchThread</tt>.
 * 
 * @author Craig
 * @version 2.0
 * @see stockmarket.model.AbstractPropertyModel AbstractPropertyModel
 * @see stockmarket.controller.AbstractController AbstractController
 *
 */
@NotThreadSafe
public interface PropertyChangeView {

	/**
	 * Passes a <tt>PropertyChangeEvent</tt> to this view to
	 * update a property being displayed in the GUI. Implementations
	 * of this method should use conditional logic to test the 
	 * property name to see if it applies to this view and what
	 * action to take. If the property passed to this view does not
	 * apply to it, simple ignore the event.
	 * 
	 * @param event the <tt>PropertyChangeEvent</tt> being passed to this view.
	 * @throws IllegalArgumentException if the <tt>newValue</tt> parameter
	 * of the event is not a value type that the view is expecting to receive
	 * for that particular property.
	 */
	void changeProperty(final PropertyChangeEvent event);
	
}
