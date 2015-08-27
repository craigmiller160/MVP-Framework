/**
 * 
 */
/**
 * A major enhancement over the core MVP, utilizing the *ActionListener* interface to 
 * provide an even greater level of de-coupling between the view and controller. Views
 * and controllers now both implement the <tt>ActionListener</tt> interface, in order to 
 * facilitate the rapid passing of events from actionable components in the GUI to the 
 * controller. Views use the interface to provide a class-level listener that all actionable
 * components contained in that class can add. This provides a simple mechanism for routing 
 * all <tt>ActionEvent</tt>s to a single <tt>actionPerformed(ActionEvent)</tt> method, where they can then 
 * be passed to any external listeners registered with the class. The controller implementation 
 * of <tt>ActionListener</tt> makes it that external listener. It adds itself as a listener to any 
 * compatible view when it is registered with the controller, and therefore receives all events
 * triggered by user input in the view.
 *
 * In addition, an abstract <tt>getValueForAction(String)</tt> method has also been provided for
 * views. This should be implemented to allow the controller to get any values it would need for
 * a given action, and action commands should be used as constants to facilitate this transfer. 
 * Examples of values that might be needed include a selection index from a list or input into 
 * a text field.
 *
 * Lastly, a separate dialog framework mirroring what was created for standard GUI classes is 
 * also provided, allowing dialogs to conform to this framework as well.
 *
 * The purpose of this listener system, as was mentioned earlier, is to completely de-couple
 * the view from the controller. Views now no longer need any reference to the actual controller 
 * itself (except maybe for static imports of constants), and its implementation is almost 
 * completely independent from the view.
 * 
 * 
 * @author craig
 * @version 2.0
 */
package mvp.listener;