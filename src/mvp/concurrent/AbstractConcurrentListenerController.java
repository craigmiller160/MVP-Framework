package mvp.concurrent;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import mvp.core.PropertyChangeView;
import mvp.listener.AbstractListenerController;
import mvp.listener.ListenerView;
import net.jcip.annotations.ThreadSafe;

/**
 * A concurrent enhancement of the <tt>AbstractListenerController</tt>.
 * This class is designed to handle the process of off-loading the
 * execution of events, updating of property models, and other 
 * processes from the <tt>EventDispatchThread</tt> to background 
 * threads. It also ensures that that all property changes are
 * returned to the view on the <tt>EventDispatchThread</tt>, thus
 * preserving the thread safety of the GUI.
 * <p>
 * This process is controlled by three methods:
 * <p>
 * <tt>actionPerformed(ActionEvent)</tt> is a method that is
 * invoked while still on the EDT. It checks the event to see
 * if the source is an instance of <tt>ListenerView</tt>, and
 * if so it invokes the <tt>getValueForAction(String)</tt>. This
 * invocation is safe because it occurs while still on the EDT.
 * This method then passes both the event and the value (if 
 * it was acquired) to a background thread for processing.
 * <p>
 * <tt>processEvent(ActionEvent,Object)</tt> parses the
 * event to determine the appropriate response. This method
 * is abstract and needs to be implemented to extend this
 * class. This method is virtually guaranteed to be accessed
 * by more than one thread at once, so proper synchronization
 * and thread safety is a must. Implementations of this method
 * generally update property models and perform various other
 * lengthy background tasks.
 * <p>
 * <tt>propertyChange(PropertyChangeEvent)</tt> is invoked
 * while on the background thread when a bound property is 
 * changed in one of the models. It safely wraps the 
 * <tt>changeProperty(PropertyChangeEvent)</tt> methods from
 * the views in <tt>SwingUtilities.invokeLater(Runnable) to
 * ensure that the event is only passed to the view on the 
 * EDT.
 * <p>
 * Lastly, the executor used by this class to manage the 
 * thread pool is a protected field and available to subclasses.
 * This is primarily done to allow for its threads to be used
 * for other concurrent operations, avoiding the need to create
 * additional thread pools. However, subclasses can determine
 * if they wish to use the existing pool for this purpose,
 * or if they would rather create additional pools instead.
 * <p>
 * <b>THREAD SAFETY:</b> This class is thread safe. The only
 * possible compromise to its safety would come from the
 * GUI classes sending <tt>ActionEvent</tt>s to this class
 * not being properly contained to the EDT, but that would
 * be a problem outside of the control of this class. 
 * 
 * @author craig
 * @version 2.0
 * @see mvp.listener.ListenerView ListenerView
 */
@ThreadSafe
public abstract class AbstractConcurrentListenerController 
extends AbstractListenerController{

	/**
	 * The executor that offloads various actions onto background threads.
	 */
	protected final ExecutorService eventExecutor;
	
	/**
	 * Constructs an instance of this controller with no set thread pool size.
	 * Threads are created as needed, as many threads as are necessary, 
	 * and when they're no longer in use they are disposed of.
	 */
	public AbstractConcurrentListenerController(){
		super();
		eventExecutor = Executors.newCachedThreadPool();
	}
	
	/**
	 * Constructs an instance of this controller with a fixed thread pool size.
	 * The exact number of threads specified here are created at initialization.
	 * Unless the properties of the thread pool are altered, there will never be
	 * more threads than this available during the lifespan of this object,
	 * and any threads that die will be replaced.
	 * 
	 * @param threadPoolSize the fixed size of the thread pool.
	 */
	public AbstractConcurrentListenerController(int threadPoolSize){
		super();
		eventExecutor = Executors.newFixedThreadPool(threadPoolSize);
	}
	
	/**
	 * Constructs an instance of this controller with a much more detailed
	 * configuration. The parameters set the minimum number of threads to
	 * be available at all times (corePoolSize), the maximum number of threads
	 * that could be created (maximumPoolSize), and the time to keep any additional
	 * threads beyond the core pool size alive (keepAliveTime, unit). 
	 * 
	 * @param corePoolSize the minimum number of threads to be available at all times.
	 * @param maximumPoolSize the maximum number of threads that could be created.
	 * @param keepAliveTime the raw number for the amount of time to keep threads beyond
	 * the core pool size alive.
	 * @param unit the unit of time for the keepAliveTime value.
	 */
	public AbstractConcurrentListenerController(int corePoolSize, int maximumPoolSize, 
			long keepAliveTime, TimeUnit unit){
		super();
		eventExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 
				keepAliveTime, unit, new LinkedBlockingQueue<Runnable>());
	}
	
	/**
	 * Sends a shutdown command to the executor. If the boolean parameter is
	 * true, threads will be interrupted while running to accelerate the 
	 * shutdown process.
	 * 
	 * @param interruptIfRunning whether or not to interrupt running threads
	 * during shutdown.
	 */
	public void shutdown(boolean interruptIfRunning){
		if(interruptIfRunning){
			eventExecutor.shutdownNow();
		}
		else{
			eventExecutor.shutdown();
		}
	}
	
	/**
	 * Sets a <tt>ThreadFactory</tt> to this controller's thread pool,
	 * which allows specially designed threads from the factory to be 
	 * used by the pool.
	 * 
	 * @param factory the <tt>ThreadFactory</tt> to set to this controller's
	 * thread pool.
	 */
	public void setThreadFactory(ThreadFactory factory){
		((ThreadPoolExecutor) eventExecutor).setThreadFactory(factory);
	}
	
	/**
	 * Sets a <tt>RejectedExecutionHandler</tt> to this controller's thread pool,
	 * providing a process for handling the rejection of tasks passed to it.
	 * 
	 * @param handler the <tt>RejectedExecutionHandler</tt> to set to this controller's
	 * thread pool.
	 */
	public void setRejectedExecutionHandler(RejectedExecutionHandler handler){
		((ThreadPoolExecutor) eventExecutor).setRejectedExecutionHandler(handler);
	}
	
	/**
	 * Sets the properties of the thread pool. This method allows for the properties
	 * of the thread pool to be changed or more finely configured after the 
	 * instantiation of this controller. The core pool size, maximum pool size,
	 * and keep alive time can all be adjusted here.
	 * 
	 * @param corePoolSize the minimum number of threads to be available at all times.
	 * @param maximumPoolSize the maximum number of threads that could be created.
	 * @param keepAliveTime the raw number for the amount of time to keep threads beyond
	 * the core pool size alive.
	 * @param unit the unit of time for the keepAliveTime value.
	 */
	public void setThreadPoolProperties(int corePoolSize, int maximumPoolSize, 
			long keepAliveTime, TimeUnit unit){
		((ThreadPoolExecutor) eventExecutor).setCorePoolSize(corePoolSize);
		((ThreadPoolExecutor) eventExecutor).setMaximumPoolSize(maximumPoolSize);
		((ThreadPoolExecutor) eventExecutor).setKeepAliveTime(keepAliveTime, unit);
	}

	/**
	 * Receives an <tt>ActionEvent</tt> from a view and passes it to another
	 * thread for parsing and execution. If the event comes from a GUI class
	 * that implements <tt>ListenerView</tt>, the <tt>getValueForAction(String)</tt>
	 * method on the view is invoked BEFORE the task is passed to another thread.
	 * This acquires any necessary values from the view while still on the
	 * <tt>EventDispatchThread</tt>, ultimately passing those values along
	 * with the event to the background thread.
	 * 
	 * @param event the <tt>ActionEvent</tt> to execute.
	 */
	@Override
	public final void actionPerformed(final ActionEvent event) {
		Runnable eventTask = null;
		final String actionCommand = event.getActionCommand();
		if(event.getSource() instanceof ListenerView){
			final Object valueFromView = ((ListenerView) event.getSource()).getValueForAction(
					actionCommand);
			
			eventTask = new Runnable(){
				@Override
				public void run() {
					processEvent(actionCommand, valueFromView);
				}
			};
		}
		else{
			eventTask = new Runnable(){
				@Override
				public void run(){
					processEvent(actionCommand, null);
				}
			};
		}
		
		eventExecutor.execute(eventTask);
	}
	
	/**
	 * Process events passed from views and execute the appropriate
	 * response to them on background threads. The event's action command
	 * and any value(s) from the view that are necessary for execution
	 * are provided as parameters.
	 * <p>
	 * Because of the concurrent nature of this class, this method
	 * WILL be invoked from multiple threads. Therefore its 
	 * implementation should use synchronization where necessary
	 * to ensure concurrent access.
	 * <p>
	 * The <tt>getValueForAction(String)</tt> method from <tt>ListenerView</tt>
	 * is invoked before this method is, and the <tt>valueFromView</tt>
	 * parameter will contain any values provided by that method, or
	 * null if none were.
	 * 
	 * @param actionCommand the action to be executed.
	 * @param valueFromView the value returned by <tt>getValueForAction(String)</tt>
	 * in the <tt>ListenerView</tt> interface. If no value is set to be returned
	 * by the view that sent this event, this parameter will be <tt>null</tt>.
	 */
	protected abstract void processEvent(final String actionCommand, 
			final Object valueFromView);
	
	/**
	 * Receive <tt>PropertyChangeEvent</tt>s and pass them to the 
	 * views registered with this controller. Because of the concurrent
	 * nature of this class, the view classes are passed back to the 
	 * <tt>EventDispatchThread</tt> before the <tt>changeProperty(PropertyChangeEvent)</tt>
	 * method is invoked. This ensures that the view classes will
	 * have their single-thread-access guarantee protected.
	 * 
	 * @param event the <tt>PropertyChangeEvent</tt> to pass to the views.
	 */
	@Override
	public final void propertyChange(final PropertyChangeEvent event){
		synchronized(viewList){
			for(final PropertyChangeView view : viewList){
				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run() {
						view.changeProperty(event);
					}
				});
			}
		}
	}
	
		
	
}
