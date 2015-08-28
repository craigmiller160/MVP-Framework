package mvp.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import net.jcip.annotations.ThreadSafe;

/**
 * Abstract framework for a JavaBean bound property model. It provides the basic
 * functions of adding, removing, and firing events to property change listeners.
 * Subclasses should take care to follow JavaBean guidelines for maximum
 * effectiveness. Property names should be kept as constants to ensure consistency,
 * and setter and getter methods should be provided for all properties.
 * <p>
 * <b>NOTE:</b> All models extending this class should be added to
 * the appropriate controller during the initialization of the program.
 * <p>
 * <b>THREAD SAFETY:</b> The <tt>firePropertyChangeEvent(String,Object,Object)</tt>
 * method should NEVER be invoked within a synchronized method or block. So long
 * as that rule is followed, this class is completely thread safe. It wraps around a thread-safe
 * <tt>PropertyChangeSupport</tt> object and delegates its thread safety to it.
 * No additional synchronization or locking is needed for the methods provided herein.
 * 
 * @author Craig
 * @version 2.0
 * @see mvp.core.PropertyChangeView ControllableView
 * @see mvp.core.AbstractController AbstractController
 */
@ThreadSafe
public abstract class AbstractPropertyModel 
implements Serializable{

	/**
	 * SerialVersionUID for implementing Serialization.
	 */
	private static final long serialVersionUID = 873132566748734660L;

	/**
	 * <tt>PropertyChangeSupport</tt> object used to monitor property changes and handle events.
	 * <p>
	 * <tt>PropertyChangeSupport</tt> is thread-safe.
	 */
	private final PropertyChangeSupport propertyChangeSupport;
	
	/**
	 * Creates a new model with a property change support object linked to it.
	 */
	public AbstractPropertyModel(){
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	/**
	 * Adds a property change listener to this model.
	 * 
	 * @param listener the property change listener to add to this model.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener){
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	/**
	 * Removes a property change listener from this model.
	 * 
	 * @param listener the property change listener to remove from this model.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener){
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	/**
	 * Fires a property change event for a bound property of this model.
	 * <p>
	 * <b>THREAD SAFETY:</b> As is mentioned above, this method should NEVER 
	 * be invoked from within a synchronized method or block, as this could
	 * have unknowable consequences for liveness.
	 * 
	 * @param propertyName the name of the property.
	 * @param oldValue the old value of the property.
	 * @param newValue the new vale being assigned to this property.
	 */
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue){
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
}
