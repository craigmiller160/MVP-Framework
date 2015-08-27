# MVP-FRAMEWORK

#### PROJECT NAME: 
Model-View-Presenter Framework

#### PROJECT DESCRIPTION:
A re-usable abstract framework conforming to the Model-View-Presenter design pattern. The classes and interfaces in this project, when inherited by subclasses in a program, provide the skeleton for an easy implementation of this pattern. This framework is intended to provide the maximum amount of low-coupling between
elements of the program through the use of dependency inversion. Information is passed between models, views, and controllers by means of the methods in the classes and interfaces in this framework, allowing them to exist in the program without needing any actual knowledge of each other.

At its core, this framework resembles a basic MVP pattern design that can be found on countless tutorial websites. However, this particular framework brings a great many enhancements on top of that basic pattern, which will be enumerated below.

-------------------------------------------------------------------------------------------------------

## PACKAGE AND CLASS BREAKDOWN

##### PACKAGE NAME: 
mvp.core

##### PACKAGE DESCRIPTION: 
The core MVP classes for this framework. The classes in this package are primarily derived from the basic, commonly used MVP structure, but even they have several key enhancements.

First and foremost, synchronization has been properly applied to the two abstract classes in this package. This makes them inherently thread safe, and compatible with the concurrent package (discussed below).

The major enhancement, though, can be found in the *AbstractController* class. While the use of reflection in MVP is common, the *setModelProperty(String,Object...)* and *getModelProperty(String,Object...)* methods are designed to utilize the full power of the Java Reflection library to be as flexible as possible. Any public getter or setter method in any property model registered with this controller can be invoked by these methods, regardless of the number of or types of the parameters. Even variable argument parameters are handled properly.

#### CLASSES:

##### CLASS NAME: 
PropertyChangeView

##### TYPE: 
Interface

##### DESCRIPTION: 
Provides a single method, *changeProperty(PropertyChangeEvent)*, that allows updates to  roperties displayed by this view to be easily passed to it by the controller. The event itself will need to be parsed by the implementing class to determine the best way to respond to it, if a response is even needed at all.
	
##### CLASS NAME: 
AbstractPropertyModel

##### TYPE: 
Abstract Class

##### DESCRIPTION: 
Basic JavaBean property model class, wrapping around a *PropertyChangeSupport* object and providing utility methods for adding/removing PropertyChangeListeners and firing PropertyChangeEvents. This class is thread safe, but subclasses should take care to avoid invoking the *firePropertyChange(String,Object,Object)* method inside a synchronized block or method at all costs.
	
##### CLASS NAME: 
AbstractController

##### TYPE: 
Abstract Class

##### DESCRIPTION: 
Basic controller, with methods to add/remove models and views that implement *AbstractPropertyModel* and *PropertyChangeView* respectively. Proper synchronization is used when adding/removing/iterating over the lists of these models and views. In addition, the powerful reflective methods *setModelProperty(String,Object...)* and *getModelProperty(String,Object...)* are provided, which are capable of invoking any setter or getter method in any of the models. Lastly, this class implements the *PropertyChangeListener* interface, and adds itself as a listener on any of the property models added to it. When a *PropertyChangeEvent* is received, it is automatically passed to all registered views, so they can parse it and decide if a response is needed.

-----------------------------------------------------------------------------------------------------------
			 
##### PACKAGE NAME: 
mvp.listener

##### PACKAGE DESCRIPTION: 
A major enhancement over the core MVP, utilizing the *ActionListener* interface to provide an even greater level of de-coupling between the view and controller. Views and controllers now both implement the *ActionListener* interface, in order to facilitate the rapid passing of events from actionable components in the GUI to the controller. Views use the interface to provide a class-level listener that all actionable components contained in that class can add. This provides a simple mechanism for routing all *ActionEvent*s to a single *actionPerformed(ActionEvent)* method, where they can then be passed to any external listeners registered with the class. The controller implementation of *ActionListener* makes it that external listener. It adds itself as a listener to any compatible view when it is registered with the controller, and therefore receives all events triggered by user input in the view.

In addition, an abstract *getValueForAction(String)* method has also been provided for views. This should be implemented to allow the controller to get any values it would need for a given action, and action commands should be used as constants to facilitate this transwer. Examples of values that might be needed include a selection index from a list or input into a text field.

Lastly, a separate dialog framework mirroring what was created for standard GUI classes is also provided, allowing dialogs to conform to this framework as well.

The purpose of this listener system, as was mentioned earlier, is to completely de-couple the view from the controller. Views now no longer need any reference to the actual controller itself (except maybe for static imports of constants), and its implementation is almost completely independent from the view.

#### CLASSES:

##### CLASS NAME:
ListenerView

##### TYPE:
Interface

##### DESCRIPTION:
Extends *ActionListener* and provides methods for adding/removing external *ActionListener*s. It also provides the abstract *getValueForAction(String)* method, which the controller can use to get any values it may need from the view.

##### CLASS NAME:
AbstractListenerView

##### TYPE:
Abstract Class

##### DESCRIPTION:
Implements the *ListenerView* and *PropertyChangeView* interfaces to create a highly flexible abstract view class. It supports multiple external listeners being added to this view, and implements the *actionPerformed(ActionEvent)* as a final method that receives an event, re-packages it as a new *ActionEvent* with the class as the source, but with the action command from the original event, and then passes it along to any listeners assigned to the class. It also supports the dialog framework in this package, any events from *ListenerDialog* subclasses do not have their source re-assigned, and are passed along as-is.

##### CLASS NAME:
ListenerDialog

##### TYPE:
Interface

##### DESCRIPTION:
Extends the *ListenerView* interface, and adds additional methods for managing the lifecycle of a dialog. All attributes of *ListenerView* also apply to this interface.

##### CLASS NAME:
AbstractListenerDialog

##### TYPE:
Abstract Class

##### DESCRIPTION:
An abstract implementation of the *ListenerDialog* interface, and extremely similar to the *AbstractListenerView* class. The main difference between them is this class does NOT implement *PropertyChangeView*, as dialogs have a short lifespan and won't be receiving *PropertyChangeEvent*s. If a subclass decides it wants this attribute, it is welcome to choose to implement that interface as well.

##### CLASS NAME:
AbstractListenerController

##### TYPE:
Abstract Class

##### DESCRIPTION:
An enhanced *AbstractController* implementing the *ActionListener* interface. Any views that are added to it are checked to see if they implement the *ListenerView* interface, and if they do this controller adds itself as a listener. Because it implements *ActionListener*, it also provides the *actionPerformed(ActionEvent)* method for its subclasses to implement to handle the events passed to it. Otherwise, it inherits all the existing functionality of its superclass.

----------------------------------------------------------------------------------------------------

##### PACKAGE NAME:
mvp.concurrent

##### PACKAGE DESCRIPTION:
This package is designed to facilitate a multi-threaded design for a program using the MVP pattern. The sole class of this package is a concurrent version of the *AbstractListenerController*. This new controller passes all events it receives to background threads for parsing and execution. Updates to the GUI via *PropertyChangeEvent*s are wrapped in *SwingUtilities.invokeLater(Runnable)* to ensure that they take place on the *EventDispatchThread*. Because all the other classes in this framework are thread safe, programs that use this framework only have to ensure the thread safety of their own classes to achieve a good, concurrent design.

The only way for thread safety to be compromised is for the program using this framework to ignore basic rules of Java thread safety, such as keeping GUI classes accessed only on the *EventDispatchThread*.

Lastly, as was mentioned earlier, the *firePropertyChange(String,Object,Object)* method in the *AbstractPropertyModel* class should NEVER be invoked within a synchronized method or block.

##### CLASS NAME:
AbstractConcurrentListenerController

##### TYPE:
Abstract Class

##### DESCRIPTION:
This class extends *AbstractListenerClass* and provides both a thread pool and methods to configure this pool to facilitate concurrent operations. Multi-threaded functionality is primarily achieved by three main methods:

*actionPerformed(ActionEvent)* is implemented as a final method in this class. When it is invoked, it first checks the event's source to see if it was a subclass of *ListenerView*. If so, it calls *getValueForAction(String)* right away, to retrieve a value if needed (or null if not). This call is thread-safe because it is done while still on the *EventDispatchThread*. After that, the event and the value (or null, if no value) is passed to the executor wrapped in a *Runnable* to be parsed on a separate thread.

*processEvent(String,Object)* is the sole abstract method that needs to be implemented by subclasses. This method gets invoked by multiple worker threads during the lifespan of the program, and thus should be implemented with the knowledge that it needs to be safe for concurrent access. It receives the action command from the event and any value(s) from the view (or null if none) that are necessary for executing the action. This method should parse the action command, update property models, and perform any other necessary background tasks.

*propertyChange(PropertyChangeEvent)* has been overriden and made into a final method as well. Any events received by this method are wrapped in *SwingUtilities.invokeLater(Runnable)* before they are passed to the various views registered with this program.