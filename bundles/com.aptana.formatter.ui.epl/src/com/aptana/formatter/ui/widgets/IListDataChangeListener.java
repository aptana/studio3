/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.formatter.ui.widgets;

/**
 * A listener that will be notified when a {@link ICustomListViewer} is having a change in its data model.<br>
 * 
 * @see ICustomListViewer
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public interface IListDataChangeListener
{
	/**
	 * Input change notification that is fired when a {@link CListViewer} model was changed.
	 * 
	 * @param input
	 * @param oldInput
	 */
	public void inputChanged(Object input, Object oldInput);
}
