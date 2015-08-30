package io.craigmiller160.mvp.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * Abstract controller class with the basic structure for managing
 * a program following the Model-View-Presenter design pattern.
 * It provides methods to add/remove models that extend the 
 * <tt>AbstractPropertyModel</tt> class  and views that implement the
 * <tt>PropertyChangeView</tt> interface. It is not meant to 
 * be used with other view/model classes that do not apply
 * this MVP framework.
 * <p>
 * In addition to maintaining lists of registered models and views,
 * this class has powerful reflective getter and setter methods to
 * modify the models it is assigned. <tt>setModelProperty(String,Object...)</tt>
 * and <tt>getModelProperty(String,Object...)</tt> are designed to 
 * call upon any public "get" or "set" method in any model class, so long 
 * as the property name is synonymous with the method name. This 
 * allows subclasses to modify the models assigned to this controller
 * without needing any direct knowledge of their implementation.
 * <p>
 * The only restriction on these two methods is they will only
 * successfully invoke a method that matches the name and parameters
 * provided once. If more than one matching method exists across the instances
 * of the models added to this controller, only the first one found
 * will be invoked. This is to ensure a consistent ability to return
 * a value, if necessary. Because of this, it is highly recommended
 * to keep all property names unique, and not to add multiple instances
 * of the same model to this controller.
 * <p>
 * Lastly, this controller functions as a <tt>PropertyChangeListener</tt>.
 * All models added to this class have this controller added as a
 * listener for any <tt>PropertyChangeEvent</tt>s they fire.
 * Any events received will be passed to all the views registered
 * with this controller. The individual views will be responsible
 * for determining if they need to respond to the event or not.
 * <p>
 * <b>THREAD SAFETY:</b> This class is completely thread safe. The lists
 * containing the registered models and views are both synchronized 
 * lists, and thread safety is delegated to them. Iteration of 
 * those lists in this class is synchronized on the intrinsic lock
 * of the lists. Any iteration of those lists by subclasses needs 
 * to do the same.
 * 
 * @author Craig
 * @version 2.0
 * @see io.craigmiller160.mvp.core.PropertyChangeView PropertyChangeView
 * @see io.craigmiller160.mvp.core.AbstractPropertyModel AbstractPropertyModel
 */
@ThreadSafe
public abstract class AbstractController 
implements PropertyChangeListener{

	/**
	 * A list of <tt>JavaBean</tt> bound property models that
	 * this controller manages.
	 */
	@GuardedBy("modelList")
	protected List<AbstractPropertyModel> modelList;
	
	/**
	 * A list of GUI classes implementing the <tt>PropertyChangeView</tt>
	 * interface that this controller manages.
	 */
	@GuardedBy("viewList")
	protected List<PropertyChangeView> viewList;
	
	/**
	 * Create the controller.
	 */
	public AbstractController(){
		initLists();
	}
	
	/**
	 * Initialize the synchronized lists to store the models
	 * and views.
	 */
	private void initLists(){
		modelList = Collections.synchronizedList(new ArrayList<>(10));
		viewList = Collections.synchronizedList(new ArrayList<>(10));
	}
	
	/**
	 * Add a model, and set this controller as a <tt>PropertyChangeListener</tt>
	 * on it.
	 * 
	 * @param model the model to be added to this controller.
	 */
	public void addPropertyModel(AbstractPropertyModel model){
		modelList.add(model);
		model.addPropertyChangeListener(this);
	}
	
	/**
	 * Remove a model, and remove this controller as a <tt>PropertyChangeListener</tt>
	 * from it.
	 * 
	 * @param model the model to be removed from this controller.
	 */
	public void removePropertyModel(AbstractPropertyModel model){
		modelList.remove(model);
		model.removePropertyChangeListener(this);
	}
	
	/**
	 * Add a view to this controller.
	 * 
	 * @param view the view to be added to this controller.
	 */
	public void addView(PropertyChangeView view){
		viewList.add(view);
	}
	
	/**
	 * Remove a view from this controller.
	 * 
	 * @param view the view to be removed from this controller.
	 */
	public void removeView(PropertyChangeView view){
		viewList.remove(view);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt){
		synchronized(viewList){
			for(PropertyChangeView view : viewList){
				view.changeProperty(evt);
			}
		}
	}
	
	/**
	 * Invokes the getter method for the named property on one of the models 
	 * assigned to this controller. This method is invoked through reflection
	 * using the provided parameters. If the property name submitted is not
	 * a property contained by the models assigned to this controller, an
	 * exception will be thrown. If the getter invoked throws an exception,
	 * or if the reflective method is unable to successfully invoke the 
	 * method, the appropriate exception will be thrown.
	 * <p>
	 * This method is capable of reflectively invoking ANY method provided to it, 
	 * so long as that method is public and matches the types of the parameters
	 * submitted here. However, if multiple methods that would invoke successfully 
	 * are provided in the list, this method will only end up invoking one of them. 
	 * Multiple invocations risk interfering with the consistency of the return 
	 * type, and therefore this method ends after the first successful invocation
	 * even if there are other methods remaining to test.
	 * <p>
	 * The order of the methods is affected by the order models were added
	 * to this controller, but ultimately the order they are tested in cannot
	 * be completely guaranteed. As such, it is highly recommended to keep 
	 * method names across all the models added to this controller unique,
	 * thus avoiding this issue. 
	 * 
	 * @param propertyName the name of the property to invoke the setter of.
	 * @param newParams the parameters to pass to the getter method. These are optional,
	 * and the method will still run if no parameters are passed if the getter can accept
	 * a no-argument invocation.
	 * @return the return value of the method invoked.
	 * @throws NoSuchMethodException if no matching getter method can be found.
	 * if the application does not have access to invoke the method.
	 * @throws IllegalAccessException if the application does not have access to invoke the method.
	 * @throws IllegalArgumentException if any or all of the parameters submitted do not match
	 * what is required for any of the potential methods.
	 * @throws ReflectiveOperationException if an unknown problem prevents this reflective
	 * operation from completing successfully.
	 * @throws Exception if a checked exception is thrown when a matching method is successfully
	 * invoked. 
	 * @throws RuntimeException if an unchecked exception is thrown when a matching method is
	 * successfully invoked.
	 * @throws Error if an error is thrown when a matching method is successfully invoked.
	 */
	protected final Object getModelProperty(String propertyName, Object...newParams) 
			throws NoSuchMethodException, IllegalAccessException, ReflectiveOperationException, Exception{
		//Create method name, adding "get" to the propertyName
		String methodName = "get" + propertyName;
		
		//Search for matching methods and creating a list of 
		//ModelMethod objects to store them
		List<ModelMethod> matchingMethods = new ArrayList<>();
		synchronized(modelList){
			for(AbstractPropertyModel model : modelList){
				matchingMethods.addAll(getMatchingSignatureMethods(methodName, model));
			}
		}
		
		//If no matches have been found, throw exception
		if(matchingMethods.size() == 0){
			throw new NoSuchMethodException(methodName + " " + newParams);
		}
		
		//Run the reflectMethod to invoke the setter method
		return reflectMethod(matchingMethods, newParams);
	}
	
	
	/**
	 * Invokes the setter method for the named property on one of the models 
	 * assigned to this controller. This method is invoked through reflection
	 * using the provided parameters. If the property name submitted is not
	 * a property contained by the models assigned to this controller, an
	 * exception will be thrown. If the setter invoked throws an exception,
	 * or if the reflective method is unable to successfully invoke the 
	 * method, the appropriate exception will be thrown.
	 * <p>
	 * This method is capable of reflectively invoking ANY method provided to it, 
	 * so long as that method is public and matches the types of the parameters
	 * submitted here. However, if multiple methods that would invoke successfully 
	 * are provided in the list, this method will only end up invoking one of them. 
	 * Multiple invocations risk interfering with the consistency of the operation,
	 * and therefore this method ends after the first successful invocation
	 * even if there are other methods remaining to test.
	 * <p>
	 * The order of the methods is affected by the order models were added
	 * to this controller, but ultimately the order they are tested in cannot
	 * be completely guaranteed. As such, it is highly recommended to keep 
	 * method names across all the models added to this controller unique,
	 * thus avoiding this issue. 
	 * 
	 * @param propertyName the name of the property to invoke the setter of.
	 * @param newParams the parameters to pass to the setter method. These are optional,
	 * and the method will still run if no parameters are passed if the setter can accept
	 * a no-argument invocation.
	 * @throws NoSuchMethodException if no matching setter method can be found.
	 * if the application does not have access to invoke the method.
	 * @throws IllegalAccessException if the application does not have access to invoke the method.
	 * @throws IllegalArgumentException if any or all of the parameters submitted do not match
	 * what is required for any of the potential methods.
	 * @throws ReflectiveOperationException if an unknown problem prevents this reflective
	 * operation from completing successfully.
	 * @throws Exception if a checked exception is thrown when a matching method is successfully
	 * invoked. 
	 * @throws RuntimeException if an unchecked exception is thrown when a matching method is
	 * successfully invoked.
	 * @throws Error if an error is thrown when a matching method is successfully invoked.
	 */
	protected final void setModelProperty(String propertyName, Object... newParams) 
			throws NoSuchMethodException, IllegalAccessException, ReflectiveOperationException, Exception{
		//Create method name, adding "set" to the propertyName
		String methodName = "set" + propertyName;
		
		//Search for matching methods and creating a list of 
		//ModelMethod objects to store them
		List<ModelMethod> matchingMethods = new ArrayList<>();
		synchronized(modelList){
			for(AbstractPropertyModel model : modelList){
				matchingMethods.addAll(getMatchingSignatureMethods(methodName, model));
			}
		}
		
		//If no matches have been found, throw exception
		if(matchingMethods.size() == 0){
			throw new NoSuchMethodException(methodName + " " + newParams);
		}
		
		//Run the reflectMethod to invoke the setter method
		reflectMethod(matchingMethods, newParams);
		
	}
	
	/**
	 * Parses the list of potentially matching methods, and attempts to invoke
	 * them through reflection with the provided parameters. If the method invoked
	 * returns a value, it is returned by this method. If not, then <tt>null</tt>
	 * is returned. If invoking the method throws an exception, or not method is
	 * successfully invoked, the appropriate exception will be thrown.
	 * <p>
	 * This method is capable of reflectively invoking ANY method provided to it, 
	 * so long as the parameters provided match what the method requires. However,
	 * if multiple methods that would invoke successfully are provided in the list,
	 * this method will only end up invoking one of them. Multiple invocations risk
	 * interfering with the consistency of the return type, and therefore this method
	 * ends after the first successful invocation even if there are other methods
	 * remaining to test.
	 * <p>
	 * The order methods will be invoked is determined by the list passed to this 
	 * method, and this method can make no other guarantees about the order. It
	 * is highly recommended to avoid situations where multiple matching methods
	 * could be passed to this method.
	 * 
	 * @param matchingMethods list of <tt>ModelMethod</tt> containers with potentially
	 * matching methods.
	 * @param newParams the parameters submitted for invocation on a matching method. These are optional,
	 * and the method will still run if no parameters are passed if the method can accept
	 * a no-argument invocation.
	 * @return the return value of the method invoked. If no return value, then <tt>null</tt>
	 * is returned.
	 * @throws IllegalAccessException if the application does not have access to invoke the method.
	 * @throws IllegalArgumentException if any or all of the parameters submitted do not match
	 * what is required for any of the potential methods.
	 * @throws ReflectiveOperationException if an unknown problem prevents this reflective
	 * operation from completing successfully.
	 * @throws Exception if a checked exception is thrown when a matching method is successfully
	 * invoked. 
	 * @throws RuntimeException if an unchecked exception is thrown when a matching method is
	 * successfully invoked.
	 * @throws Error if an error is thrown when a matching method is successfully invoked.
	 */
	protected Object reflectMethod(List<ModelMethod> matchingMethods, Object...newParams) 
			throws IllegalAccessException, ReflectiveOperationException, Exception{
		boolean success = false; //Set to true if the operation succeeds
		Exception exceptionToThrow = null; //Stores caught exceptions so the last one can be thrown if failure...
		Object result = null; //The result of this operation, if there is one
		
		try{
			//Parses the potential matches, checking their parameters prior
			//to attempting invocation.
			for(ModelMethod mm : matchingMethods){
				try{
					//Get the class types of the method's parameters
					Class<?>[] methodParamTypes = mm.getParamTypes(); 
					//If parameters were submitted to this reflective method
					if(newParams.length > 0){
						//If more parameters were submitted than the method has,
						//It must be varargs or it will have too many args and an exception is thrown.
						if(newParams.length > mm.getMethod().getParameterCount()){
							if(mm.getMethod().isVarArgs()){
								newParams = getParamsWithVarargs(mm, newParams); //Throws IllegalArgumentException
							}
							else{
								throw new IllegalArgumentException("Wrong parameters for this method");
							}
						}
						//If the number of parameters submitted is equal to the number of parameters
						//the method has, adjust for varargs if necessary but proceed regardless
						else if(newParams.length == mm.getMethod().getParameterCount()){
							if(mm.getMethod().isVarArgs()){
								newParams = getParamsWithVarargs(mm, newParams); //Throws IllegalArgumentException
							}
						}
						//If the number of parameters submitted is one less than the method's parameter count,
						//then it must be a varargs method with optional params being ignored, or else exception is thrown
						//Create varargs array with a length of 0 for attempting invocation
						else if(newParams.length == mm.getMethod().getParameterCount() - 1){
							if(mm.getMethod().isVarArgs()){
								newParams = getParamsWithEmptyVarargs(mm, newParams);
							}
							else{
								throw new IllegalArgumentException("Wrong parameters for this method");
							}
						}
						//If the code gets here, then at least one parameter was submitted, but not enough
						//parameters were submitted to successfully invoke the method
						else{
							throw new IllegalArgumentException("Wrong parameters for this method");
						}
						
						//Attempt invocation, and if successful, end the loop
						result = mm.getMethod().invoke(mm.getModel(), newParams);
						success = true;
						break;
						
					}
					//If no parameters were submitted, then the method being searched for is one that
					//does not require parameters.
					else{
						//The method is indeed a no-parameter method. Attempt invocatin and end loop if successful.
						if(methodParamTypes.length == 0){
							result = mm.getMethod().invoke(mm.getModel());
							success = true;
							break;
						}
						//The method has a single parameter, meaning its a varargs method, but this invocation
						//isn't using the optional parameter. Attempt invocation with an emtpy varargs array
						//If successful, end the loop
						else if(methodParamTypes.length == 1 && mm.getMethod().isVarArgs()){
							newParams = getParamsWithEmptyVarargs(mm, newParams);
							result = mm.getMethod().invoke(mm.getModel(), newParams);
							success = true;
							break;
						}
						//If the code reaches here, then the method being checked is not either a 0-parameter
						//or a 1-vararg-parameter method, and therefore cannot be invoked.
						else{
							throw new IllegalArgumentException("Wrong parameters for this method");
						}
					}
				}
				catch(IllegalArgumentException ex){
					exceptionToThrow = ex;
				}
			}
		}
		catch(InvocationTargetException ex){
			launderThrowable(ex);
		}
		
		//If none of the attempted invocations succeeded, throw the appropriate
		//exception
		if(!success){
			if(exceptionToThrow instanceof IllegalAccessException){
				throw (IllegalAccessException) exceptionToThrow;
			}
			else{
				throw new ReflectiveOperationException(
						"An unknown failure has occurred during this reflective operation");
			}
		}
		
		return result;
	}
	
	/**
	 * Returns an altered version of the new parameter array, adjusted to fit
	 * with a varargs method where the vararg argument is not being used.
	 * This is accomplished by creating a new array one index larger than
	 * the new parameter array, with all of the new parameter array's contents.
	 * An array of the varargs argument type with a length of 0 is then added as the final
	 * parameter, so that the method can be invoked successfully.
	 * 
	 * @param mmthe <tt>ModelMethod</tt> container with the method the parameters are being
	 * prepared for.
	 * @param newParams the array of submitted parameters to the reflective method.
	 * @return a new array of all the submitted parameters, with an empty array with a 
	 * length of 0 added as a new final element.
	 */
	private Object[] getParamsWithEmptyVarargs(ModelMethod mm, Object... newParams){
		int varargsIndex = mm.getMethod().getParameterCount() - 1;
		Class<?> varargsComponentType = mm.getParamTypes()[varargsIndex].getComponentType();
		
		newParams = Arrays.copyOf(newParams, newParams.length + 1);
		newParams[newParams.length - 1] = Array.newInstance(varargsComponentType, 0);
		
		return newParams;
	}
	
	/**
	 * Returns an altered version of the new parameters array, adjusted to fit
	 * with a varargs method. This is accomplished by enclosing all the submitted 
	 * parameters that should be a part of the varargs argument in a separate
	 * array, and having that array be the final parameter of a new array
	 * that has the same number of elements as the method's parameter count.
	 * <p>
	 * This method makes the assumption that the parameters submitted are
	 * appropriate for this method's varargs argument. If that is not the
	 * case, an exception is thrown. 
	 * 
	 * @param mm the <tt>ModelMethod</tt> container with the method the parameters are being
	 * prepared for.
	 * @param newParams the array of submitted parameters to the reflective method.
	 * @return a new array of all the submitted parameters, with the varargs parameters in
	 * a separate array at the last index of the enclosing array.
	 * @throws IllegalArgumentException if the parameters that should be a part of the 
	 * varargs argument are not of a compatible type.
	 */
	private Object[] getParamsWithVarargs(ModelMethod mm, Object... newParams){
		int varargsIndex = mm.getMethod().getParameterCount() - 1;
		Class<?> varargsComponentType = mm.getParamTypes()[varargsIndex].getComponentType();
		
		Object[] varargsArray = (Object[]) Array.newInstance(
				varargsComponentType, newParams.length - varargsIndex);
		
		try{
			for(int i = 0; i < newParams.length - varargsIndex; i++){
				varargsArray[i] = newParams[varargsIndex + i];
			}
		}
		catch(ArrayStoreException ex){
			throw new IllegalArgumentException("Wrong parameters for this method");
		}
		
		//Create the finalParams array, with all regular parameters + the varargsArray 
		//as the final element
		Object[] finalParams = new Object[mm.getMethod().getParameterCount()];
		for(int i = 0; i < mm.getMethod().getParameterCount() - 1; i++){
			finalParams[i] = newParams[i];
		}
		finalParams[finalParams.length - 1] = varargsArray;
		
		return finalParams;
	}
	
	/**
	 * Checks an object for methods matching the given signature, and
	 * returns a list of <tt>ModelMethod</tt> containers with any matches 
	 * that are found. If no matches are found, an empty list is returned.
	 * 
	 * @param signature the signature of the method being searched for.
	 * @param obj the object that is being searched for the method.
	 * @return a list of <tt>ModelMethod</tt> containers with any matches. 
	 * An empty list if nothing is found.
	 */
	private List<ModelMethod> getMatchingSignatureMethods(String signature, Object obj){
		List<ModelMethod> matches = new ArrayList<>();
		Method[] methods = obj.getClass().getMethods();
		for(Method m : methods){
			if(m.getName().equals(signature)){
				matches.add(new ModelMethod(obj, m));
			}
		}
		
		return matches;
	}
	
	/**
	 * Parses a throwable that's a cause from an <tt>InvocationTargetException</tt>
	 * that is thrown by one of the reflective methods in this class. Rethrows it
	 * as either an <tt>Exception</tt>, <tt>RuntimeException</tt>, or <tt>Error</tt>.
	 * 
	 * @param t the throwable being checked.
	 * @throws Exception if the throwable is a subclass of <tt>Exception</tt>.
	 * @throws RuntimeException if the throwable is a subclass of <tt>RuntimeException</tt>.
	 */
	private void launderThrowable(Throwable t) throws Exception{
		if(t instanceof RuntimeException){
			throw (RuntimeException) t;
		}
		else if(t instanceof Error){
			throw (Error) t;
		}
		else{
			throw (Exception) t;
		}
	}
	
	/**
	 * Immutable container object to hold a potentially matching method and the 
	 * model it comes from for the reflective getter and setter methods
	 * of this class.
	 * 
	 * @author craig
	 * @version 2.0
	 */
	@Immutable
	private class ModelMethod{
		
		/**
		 * The object that is the source of the method.
		 */
		private final Object obj;
		
		/**
		 * The potentially matching method.
		 */
		private final Method m;
		
		/**
		 * The parameter types of the potentially matching method.
		 */
		private final Class<?>[] paramTypes;
		
		/**
		 * Create this container object.
		 * 
		 * @param obj the object that is the source of the method.
		 * @param m the potentially matching method.
		 */
		public ModelMethod(Object obj, Method m){
			this.obj = obj;
			this.m = m;
			this.paramTypes = m.getParameterTypes();
		}
		
		/**
		 * Get the object that is the source of the method.
		 * 
		 * @return the object that is the source of the method.
		 */
		public Object getModel(){
			return obj;
		}
		
		/**
		 * Get the potentially matching method.
		 * 
		 * @return the potentially matching method.
		 */
		public Method getMethod(){
			return m;
		}
		
		/**
		 * Get the parameter types of the potentially matching method.
		 * 
		 * @return the parameter types of the potentially matching method.
		 */
		public Class<?>[] getParamTypes(){
			return paramTypes;
		}
		
	}
	
}
