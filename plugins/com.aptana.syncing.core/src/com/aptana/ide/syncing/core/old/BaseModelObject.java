/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.syncing.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class BaseModelObject implements IModifiableObject
{

	/**
	 * The listener list
	 */
	protected ListenerList listeners;

	/**
	 * Suspend event firing
	 */
	protected boolean suspendEvents = false;

	/**
	 * Has there been any outstanding events while event firing has been suspended?
	 */
	protected boolean outstandingEvents = false;

	/**
	 * Base model object
	 */
	public BaseModelObject()
	{
		listeners = new ListenerList();
	}

	/**
	 * Suspends event firing
	 */
	public void suspendEvents()
	{
		suspendEvents = true;
	}

	/**
	 * Resumes event firing
	 */
	public void resumeEvents()
	{
		suspendEvents = false;
		if (outstandingEvents)
		{
			fireChange();
			outstandingEvents = false;
		}
	}

	/**
	 * This method notifies all registerd listeners about a change to this object's model
	 */
	protected void fireChange()
	{
		if (!suspendEvents)
		{
			Object[] listens = listeners.getListeners();
			for (int i = 0; i < listens.length; i++)
			{
				if (listens[i] instanceof IModelListener)
				{
					try
					{
						((IModelListener) listens[i]).modelChanged(this);
					}
					catch (Exception e)
					{
						SyncingUIPlugin.logError(Messages.BaseModelObject_ErrorNotifyingModelListener, e); //$NON-NLS-1$
					}
					catch (Error e)
					{
						SyncingUIPlugin.logError(Messages.BaseModelObject_ErrorNotifyingModelListener, e); //$NON-NLS-1$
					}
				}
			}
		}
		else
		{
			outstandingEvents = true;
		}
	}

	/**
	 * Adds all the listeners in the passed in array that are implementers of IModelListener
	 * 
	 * @param listeners
	 */
	public void addListeners(Object[] listeners)
	{
		if (listeners != null)
		{
			for (int i = 0; i < listeners.length; i++)
			{
				if (listeners[i] instanceof IModelListener)
				{
					addListener((IModelListener) listeners[i]);
				}
			}
		}
	}

	/**
	 * Tests if a value is different and return true if it is
	 * 
	 * @param oldValue
	 * @param newValue
	 * @return - true if changed
	 */
	protected boolean isModelChanged(Object oldValue, Object newValue)
	{
		if (oldValue == null && newValue != null)
		{
			return true;
		}
		else if (oldValue != null && newValue == null)
		{
			return true;
		}
		else if (oldValue != null && !oldValue.equals(newValue))
		{
			return true;
		}
		return false;
	}

	/**
	 * Helper method to determine whether or not to update and fire an event for a new value. Ensures the new value is
	 * not null, has a length greater than zero and is changed compared with the old value
	 * 
	 * @param oldValue
	 * @param newValue
	 * @return - true if the new value should be set as the current value for the field
	 */
	protected boolean isNewValueValid(String oldValue, String newValue)
	{
		return newValue != null && newValue.length() > 0 && isModelChanged(oldValue, newValue);
	}

	/**
	 * Gets the listeners
	 * 
	 * @return - model listeners
	 */
	public IModelListener[] getListeners()
	{
		List<IModelListener> mls = new ArrayList<IModelListener>();
		Object[] listens = listeners.getListeners();
		for (int i = 0; i < listens.length; i++)
		{
			if (listens[i] instanceof IModelListener)
			{
				mls.add((IModelListener) listens[i]);
			}
		}
		return mls.toArray(new IModelListener[0]);
	}

	/**
	 * @see com.aptana.ide.core.model.IModifiableObject#addListener(com.aptana.ide.core.model.IModelListener)
	 */
	public void addListener(IModelListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * @see com.aptana.ide.core.model.IModifiableObject#removeListener(com.aptana.ide.core.model.IModelListener)
	 */
	public void removeListener(IModelListener listener)
	{
		listeners.remove(listener);

	}

}
