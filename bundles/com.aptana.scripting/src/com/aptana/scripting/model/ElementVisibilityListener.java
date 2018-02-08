/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

public interface ElementVisibilityListener
{
	/**
	 * This event fires whenever an AbstractElement is no longer visible according to the bundle precedence rules. Note
	 * this event will fire for elements that have been deleted as well as elements that have become hidden due to the
	 * bundle precedence rules.
	 * 
	 * @param element
	 */
	void elementBecameHidden(AbstractElement element);

	/**
	 * This event fires whenever an AbstractElement has become visible according to the bundle precedence rules. Note
	 * this event will fire for elements that have been added that are visible or when another bundle or command has
	 * been deleted thus exposing the element due to bundle precedence
	 * 
	 * @param element
	 */
	void elementBecameVisible(AbstractElement element);
}
