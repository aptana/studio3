/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
