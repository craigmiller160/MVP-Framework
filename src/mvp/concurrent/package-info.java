/**
 * 
 */
/**
 * This package is designed to facilitate a multi-threaded design for a program using the MVP pattern. 
 * The sole class of this package is a concurrent version of the <tt>AbstractListenerController</tt>. This 
 * new controller passes all events it receives to background threads for parsing and execution. Updates 
 * to the GUI via <tt>PropertyChangeEvent</tt>s are wrapped in <tt>SwingUtilities.invokeLater(Runnable)</tt> to ensure 
 * that they take place on the <tt>EventDispatchThread</tt>. Because all the other classes in this framework are 
 * thread safe, programs that use this framework only have to ensure the thread safety of their own classes 
 * to achieve a good, concurrent design.
 * <p>
 * The only way for thread safety to be compromised is for the program using this framework to ignore 
 * basic rules of Java thread safety, such as keeping GUI classes accessed only on the <tt>EventDispatchThread</tt>.
 * <p>
 * Lastly, as was mentioned earlier, the <tt>firePropertyChange(String,Object,Object)</tt> method in the 
 * <tt>AbstractPropertyModel</tt> class should NEVER be invoked within a synchronized method or block.
 * 
 * @author craig
 * @version 2.0
 */
package mvp.concurrent;