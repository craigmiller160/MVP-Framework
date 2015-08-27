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
core

###### PACKAGE DESCRIPTION: 
The core MVP classes for this framework. The classes in this package are primarily derived from the basic, commonly used MVP structure, but even they have several key enhancements.

First and foremost, synchronization has been properly applied to the two abstract classes in this package. This makes them inherently thread safe, and compatible with the concurrent package (discussed below).

The major enhancement, though, can be found in the AbstractController class. While the use of reflection in MVP is common, the setModelProperty(String,Object...) and getModelProperty(String,Object...) methods are designed to utilize the full power of the Java Reflection library to be as flexible as possible. Any public getter or setter method in any property model registered with this controller can be invoked by these methods, regardless of the number of or types of the parameters. Even variable argument parameters are handled properly.

#### CLASSES:

##### CLASS NAME: 
PropertyChangeView

##### TYPE: 
Interface

##### DESCRIPTION: 
Provides a single method, changeProperty(PropertyChangeEvent), that allows updates to  roperties displayed by this view to be easily passed to it by the controller. The event itself will need to be parsed by the implementing class to determine the best way to respond to it, if a response is even needed at all.
	
##### CLASS NAME: 
AbstractPropertyModel

##### TYPE: 
Abstract Class

##### DESCRIPTION: 
Basic JavaBean property model class, wrapping around a PropertyChangeSupport object and providing utility methods for adding/removing PropertyChangeListeners and firing PropertyChangeEvents. This class is thread safe, but subclasses should take care to avoid invoking the firePropertyChange(String,Object,Object) method inside a synchronized block or method at all costs.
	
##### CLASS NAME: 
AbstractController

##### TYPE: 
Abstract Class

##### DESCRIPTION: 
Basic controller, with methods to add/remove models and views that implement AbstractPropertyModel and PropertyChangeView respectively. Proper synchronization is used when adding/removing/iterating over the lists of these models and views. In addition, the powerful reflective methods setModelProperty(String,Object...) and getModelProperty(String,Object...) are provided, which are capable of invoking any setter or getter method in any of the models. Lastly, this class implements the PropertyChangeListener interface, and adds itself as a listener on any of the property models added to it. When a PropertyChangeEvent is received, it is automatically passed to all registered views, so they can parse it and decide if a response is needed.

-----------------------------------------------------------------------------------------------------------
			 
##### PACKAGE NAME: 
listener

##### PACKAGE DESCRIPTION: 
A major enhancement over the core MVP, utilizing the ActionListener interface to provide an even greater level of de-coupling between the view and controller. Views and controllers now both implement the ActionListener interface, in order to facilitate the rapid passing of events from actionable components in the GUI to the controller. 