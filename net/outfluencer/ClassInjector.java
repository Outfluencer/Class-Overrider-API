/*
 * Copyright 2021 © Outfluencer
 */
package net.outfluencer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import sun.misc.Resource;
import sun.misc.URLClassPath;

/*
 * This class allows you to override classes in ClassLoaders
 * witch don't have the class or not have loaded it yet.
 * if a class with the same name is already loaded nothing
 * happens
 */
public class ClassInjector {

	
	/*
	 * The String className is the name of the class you trying to override.
	 * The URLClassLoader from is the ClassLoader where the class came from.
	 * The URLClassLoader to is the ClassLoader where the class should be added.
	 */
	public static void overrideClass(String className, URLClassLoader from, URLClassLoader to) throws PrivilegedActionException {
		/* Run it privileged to gain more permissions */
		AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
			@Override
			public Class<?> run() throws Exception {
				/* Get the Field of the sun.misc.URLClassPath */
				Field URLClassPathField = URLClassLoader.class.getDeclaredField("ucp");
				URLClassPathField.setAccessible(true);
				
				/* Get the sun.misc.URLClassPath */
				URLClassPath path = (URLClassPath) URLClassPathField.get(from);
				
				/* Get the sun.misc.Resource of the URLClassLoader we want to load the class from*/
				final Resource resource = path.getResource(className.replace('.', '/').concat(".class"), false);
				
				/* Get the method to define the class in the java.net.URLClassLoader 
				 * and make it accessible */
				Method loadClass = URLClassLoader.class.getDeclaredMethod("defineClass", new Class[] { String.class, Resource.class });
				loadClass.setAccessible(true);
				
				/* invoke the method */
				return (Class<?>) loadClass.invoke(to, className, resource);
			}
		});
	}


}