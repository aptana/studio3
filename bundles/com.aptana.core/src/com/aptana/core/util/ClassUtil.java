/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Stepanov
 */
public final class ClassUtil
{

	/**
	 * 
	 */
	private ClassUtil()
	{
	}

	public static List<Class<?>> getClassesTree(Class<?> clazz)
	{
		List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(clazz);
		if (clazz.isInterface())
		{
			processInterface(clazz, classes);
		}
		else
		{
			processClass(clazz, classes);
		}
		return classes;
	}

	private static void processClass(Class<?> clazz, List<Class<?>> classes)
	{
		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> i : interfaces)
		{
			if (!classes.contains(i))
			{
				classes.add(i);
				processInterface(i, classes);
			}
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null && !classes.contains(superClass))
		{
			classes.add(superClass);
			processClass(superClass, classes);
		}
	}

	private static void processInterface(Class<?> clazz, List<Class<?>> classes)
	{
		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> i : interfaces)
		{
			if (!classes.contains(i))
			{
				classes.add(i);
				processInterface(i, classes);
			}
		}
	}
}
