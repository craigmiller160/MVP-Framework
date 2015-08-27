/**
 * 
 */
/**
 * The core MVP classes for this framework. The classes in this package are primarily derived 
 * from the basic, commonly used MVP structure, but even they have several key enhancements.
 *
 * First and foremost, synchronization has been properly applied to the two abstract classes 
 * in this package. This makes them inherently thread safe, and compatible with the concurrent 
 * package.
 *
 * The major enhancement, though, can be found in the <tt>AbstractController</tt> class. While the use 
 * of reflection in MVP is common, the <tt>setModelProperty(String,Object...)</tt> and 
 * <tt>getModelProperty(String,Object...)</tt> methods are designed to utilize the full power of the 
 * Java Reflection library to be as flexible as possible. Any public getter or setter method in
 * any property model registered with this controller can be invoked by these methods, regardless
 * of the number of or types of the parameters. Even variable argument parameters are handled 
 * properly.
 * 
 * @author craig
 * @version 2.0
 */
package mvp.core;