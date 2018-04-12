/*******************************************************************************
 * Copyright (c) 2007 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Michael Scharf (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.terminal.model;

import java.util.HashMap;
import java.util.Map;

/** 
 * 
 * Flyweight
 * Threadsafe.
 */
public class StyleColor {
	private final static Map fgStyleColors=new HashMap();
	final String fName;
	
	/**
	 * @param name the name of the color. It is up to the UI to associate a
	 * named color with a visual representation
	 * @return a StyleColor
	 */
	public static StyleColor getStyleColor(String name) {
		StyleColor result;
		synchronized (fgStyleColors) {
			result=(StyleColor) fgStyleColors.get(name);
			if(result==null) {
				result=new StyleColor(name);
				fgStyleColors.put(name, result);
			}
		}
		return result;
	}
	// nobody except the factory method is allowed to instantiate this class!
	private StyleColor(String name) {
		fName = name;
	}

	public String getName() {
		return fName;
	}

	public String toString() {
		return fName;
	}
	// no need to override equals and hashCode, because Object uses object identity
}