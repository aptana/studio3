/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.formatter.ui.preferences;

/**
 * Returns the prompt that should be used in the popup box that indicates a build needs to occur.
 */
public interface IPreferenceChangeRebuildPrompt
{

	/**
	 * Returns the title
	 * 
	 * @return
	 */
	String getTitle();

	/**
	 * Returns the message
	 * 
	 * @return
	 */
	String getMessage();

}
