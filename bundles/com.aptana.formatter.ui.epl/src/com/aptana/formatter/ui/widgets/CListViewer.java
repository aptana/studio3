/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.formatter.ui.widgets;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

/**
 * A custom list viewer that allows setting an external listener to be notified when the input is changed.<br>
 * Note that only changes that are made through the content-provider will be reflected here. Other, direct changes, will
 * not.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class CListViewer extends ListViewer
{

	private ListenerList listeners;

	/**
	 * @param parent
	 */
	public CListViewer(Composite parent)
	{
		super(parent);
		init();
	}

	/**
	 * @param list
	 */
	public CListViewer(List list)
	{
		super(list);
		init();
	}

	/**
	 * @param parent
	 * @param style
	 */
	public CListViewer(Composite parent, int style)
	{
		super(parent, style);
		init();
	}

	public void addListDataChangeListener(IListDataChangeListener listener)
	{
		listeners.add(listener);
	}

	public void removeListDataChangeListener(IListDataChangeListener listener)
	{
		listeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractListViewer#inputChanged(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void inputChanged(Object input, Object oldInput)
	{
		super.inputChanged(input, oldInput);
		Object[] allListeners = listeners.getListeners();
		for (Object listener : allListeners)
		{
			((IListDataChangeListener) listener).inputChanged(input, oldInput);
		}
	}

	/**
	 * Initialize the listeners list.
	 */
	private void init()
	{
		listeners = new ListenerList();
	}
}
