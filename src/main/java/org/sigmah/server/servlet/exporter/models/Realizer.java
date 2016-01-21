package org.sigmah.server.servlet.exporter.models;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;

/**
 * Creates plain objects from Hibernate proxies.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr) V1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) V2.0
 */
public class Realizer {

	private final static Log LOG = LogFactory.getLog(Realizer.class);

	private Realizer() {
	}

	/**
	 * Creates a new instance of <code>object</code> with <code>ArrayList</code>s instead of <code>PersistentBag</code>s
	 * and <code>HashSet</code> instead of <code>PersistentSet</code>s. <br>
	 * <b>Note:</b> do not use this without testing its compatibility with your objects.
	 * 
	 * @param <T> Type of the object to copy.
	 * @param object
	 *          An hibernate proxy.
	 * @param ignores Array of classes to ignore.
	 * @return A copy of the given object without any proxy instance.
	 */
	public static <T> T realize(T object, Class<?>... ignores) {
		return realize(object, new HashMap<>(), new HashSet<>(Arrays.asList(ignores)));
	}

	@SuppressWarnings("unchecked")
	private static <T> T realize(T object, Map<Object, Object> alreadyRealizedObjects, Set<Class<?>> ignores) {
		T result = null;

		if (object != null) {

			// If the given object has already been instantiated, no need to instantiate it again
			if (alreadyRealizedObjects.containsKey(object)) {
				return (T) alreadyRealizedObjects.get(object);
			}

			LOG.trace("Realizing " + object.getClass() + "...");

			try {
				// Extracting the class of the current object
				final Class<T> clazz = object instanceof HibernateProxy ?
					((HibernateProxy)object).getHibernateLazyInitializer().getPersistentClass() :
					(Class<T>) object.getClass();
				final Constructor<T> emptyConstructor = clazz.getConstructor();

				// Creating a new instance of the current object
				// REM: this will crash if the object doesn't have an empty constructor
				final T instance = emptyConstructor.newInstance();
				alreadyRealizedObjects.put(object, instance);

				// Accessing fields from the given object
				final ArrayList<Field> fields = new ArrayList<Field>();

				fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

				Class<?> superClass = clazz.getSuperclass();
				while (superClass.getPackage().getName().startsWith("org.sigmah")) {

					fields.addAll(Arrays.asList(superClass.getDeclaredFields()));

					superClass = superClass.getSuperclass();
				}

				for (final Field field : fields) {
					// Avoid trying to modify static fields
					if (!Modifier.isStatic(field.getModifiers())) {
						LOG.trace("\tfield " + field.getName());
						
						if(field.getName().equalsIgnoreCase("organization")) {
							// Organization should not be exported.
							continue;
						}
						
						final Method getterMethod = getGetterMethod(field, clazz);
						final Method setterMethod = getSetterMethod(field, clazz);

						final Object sourceValue = getterMethod.invoke(object);
						final Object destinationValue;
						
						final Class<?> sourceValueClass = sourceValue != null ? sourceValue.getClass() : null;
						
						if(sourceValue instanceof PersistentCollection || sourceValue instanceof HibernateProxy) {
							Hibernate.initialize(sourceValue);
						}

						if (sourceValue == null || sourceValueClass == null) {
							destinationValue = null;

						} else if (sourceValue instanceof PersistentBag || sourceValue instanceof PersistentList) {
							// Turning persistent bags into array lists
							final ArrayList<Object> list = new ArrayList<Object>();

							for (Object value : (PersistentBag) sourceValue) {
								list.add(realize(value, alreadyRealizedObjects, ignores));
							}

							destinationValue = list;

						} else if (sourceValue instanceof PersistentSet) {
							// Turning persistent sets into hash sets

							final HashSet<Object> set = new HashSet<Object>();

							for (Object value : (PersistentSet) sourceValue) {
								set.add(realize(value, alreadyRealizedObjects, ignores));
							}

							destinationValue = set;

						} else if (ignores.contains(sourceValueClass) || sourceValueClass.getName().startsWith("java.") || sourceValueClass.isEnum()) {
							// Simple copy if the current field is a jdk type or an enum
							destinationValue = sourceValue;

						} else {
							destinationValue = realize(sourceValue, alreadyRealizedObjects, ignores);
						}
						
						// Setting the field of the new object
						if(setterMethod != null) {
							setterMethod.invoke(instance, destinationValue);
						} else {
							field.setAccessible(true); // Force the accessibility of the current field
							field.set(instance, destinationValue);
						}
					}
				}

				result = instance;

			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | HibernateException e) {
				LOG.debug("An error occured while realizing " + object, e);
			}

		}

		return result;
	}
	
	private static Method getGetterMethod(Field field, Class<?> clazz) {
		final String getAccessor = "get" + field.getName().toLowerCase();
		
		for(final Method method : clazz.getMethods()) {
			if(getAccessor.equals(method.getName().toLowerCase()) && method.getParameterTypes().length == 0) {
				return method;
			}
		}
		
		final String isAccessor = "is" + field.getName().toLowerCase();
		
		for(final Method method : clazz.getMethods()) {
			if(isAccessor.equals(method.getName().toLowerCase()) && method.getParameterTypes().length == 0) {
				return method;
			}
		}
		
		return null;
	}
	
	private static Method getSetterMethod(Field field, Class<?> clazz) {
		final String getAccessor = "set" + field.getName().toLowerCase();
		
		for(final Method method : clazz.getMethods()) {
			if(getAccessor.equals(method.getName().toLowerCase()) && method.getParameterTypes().length == 1) {
				return method;
			}
		}
		
		return null;
	}
}
