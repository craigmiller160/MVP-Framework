package io.craigmiller160.mvp.listener;

import io.craigmiller160.mvp.core.AbstractController;
import io.craigmiller160.mvp.core.PropertyChangeView;

import java.awt.event.ActionListener;

import net.jcip.annotations.ThreadSafe;

/**
 * Expanded version of <tt>AbstractController</tt>. Like its superclass,
 * this class is capable of updating a range of models and views
 * registered with it, so long as they conform to this MVP framework.
 * Consult the documentation for this class's superclass for more
 * details.
 * <p>
 * The main new functionality of this class comes from the fact that
 * it implements the <tt>ActionListener</tt> interface. This allows
 * it to function as listener for events that occur in the views
 * that are added to it. The add/remove view methods of this class
 * have been overriden to check the views being added to see if
 * they are an instance of <tt>ListenerView</tt>. If they are, 
 * this class is added to it as a listener.
 * <p>
 * Subclasses will need to implement the <tt>actionPerformed(ActionEvent)</tt>
 * method, which will receive all events from the views so that any
 * model updates that are necessary can be performed. Classes implementing
 * <tt>ListenerView</tt> also have the <tt>getValueForAction(String)</tt>
 * method, which provides a great abstract tool for acquiring values
 * from the view that are needed to perform the required action.
 * Consult the documentation for <tt>ListenerView</tt> for more details.
 * <p>
 * Lastly, the listener structure of this MVP framework functions best
 * when the action commands of all <tt>ActionEvent</tt>s passed between
 * views and this controller are constants.
 * <p>
 * <b>THREAD SAFETY:</b> This class is thread safe because its super class
 * is thread safe. While this class adds methods, it doesn't add any state
 * variables, therefore the thread-safe design of its superclass applies
 * to this class as well.
 * 
 * @author Craig
 * @version 2.0
 * @see io.craigmiller160.mvp.listener.ListenerView ListenerView
 * @see io.craigmiller160.mvp.core.AbstractPropertyModel AbstractPropertyModel
 */
@ThreadSafe
public abstract class AbstractListenerController 
extends AbstractController
implements ActionListener{

	/**
	 * Creates the controller.
	 */
	public AbstractListenerController() {
		super();
	}
	
	/**
	 * Add a view. If the type of view supports it, this controller
	 * is added to it as an <tt>ActionListener</tt>.
	 * 
	 * @param view the view to be managed by this controller.
	 */
	@Override
	public void addView(PropertyChangeView view){
		if(view instanceof ListenerView){
			((ListenerView) view).addActionListener(this);
		}
		super.addView(view);
	}
	
	/**
	 * Remove a view. If the type of view supports it, this controller
	 * is removed from it as an <tt>ActionListener</tt>.
	 * 
	 * @param view the view to be removed from this controller.
	 */
	@Override
	public void removeView(PropertyChangeView view){
		if(view instanceof ListenerView){
			((ListenerView) view).removeActionListener(this);
		}
		super.removeView(view);
	}

}
