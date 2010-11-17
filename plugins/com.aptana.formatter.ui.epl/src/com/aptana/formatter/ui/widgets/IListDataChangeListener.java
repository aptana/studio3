/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
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
