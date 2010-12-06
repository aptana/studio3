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
package com.aptana.ide.syncing.ui.old.views;

import org.eclipse.core.runtime.ListenerList;

import com.aptana.core.CorePlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.old.ISmartSyncListener;
import com.aptana.ide.syncing.core.old.SmartSyncEvent;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class SmartSyncEventManager
{

	private static SmartSyncEventManager manager;

	private ListenerList listeners;

	private SmartSyncEventManager()
	{
		listeners = new ListenerList();
	}

	/**
	 * Adds a listener
	 * 
	 * @param listener
	 */
	public void addListener(ISmartSyncListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * Removes a listener
	 * 
	 * @param listener
	 */
	public void removeListener(ISmartSyncListener listener)
	{
		listeners.remove(listener);
	}

	/**
	 * Fires a smart sync event
	 * 
	 * @param completedPairs
	 * @param sourceManager
	 * @param destinationManager
	 * @param comment
	 */
	public void fireEvent(VirtualFileSyncPair[] completedPairs, IConnectionPoint sourceManager,
			IConnectionPoint destinationManager, String comment)
	{
		SmartSyncEvent event = new SmartSyncEvent();
		event.sourceManager = sourceManager;
		event.destinationManager = destinationManager;
		event.completedPairs = completedPairs;
		event.comment = comment;
		Object[] listens = listeners.getListeners();
		for (int i = 0; i < listens.length; i++)
		{
			if (listens[i] instanceof ISmartSyncListener)
			{
				try
				{
					((ISmartSyncListener) listens[i]).smartSyncComplete(event);
				}
				catch (Exception e)
				{
					CorePlugin.logError(Messages.SmartSyncEventManager_ERR_ExceptionNotifyingSmartSyncListener, e);
				}
				catch (Error e)
				{
					CorePlugin.logError(Messages.SmartSyncEventManager_ERR_ErrorNotifyingSmartSyncListener, e);
				}
			}
		}
	}

	/**
	 * Gets the smart sync event manager
	 * 
	 * @return - event manager
	 */
	public synchronized static SmartSyncEventManager getManager()
	{
		if (manager == null)
		{
			manager = new SmartSyncEventManager();
		}
		return manager;
	}
}
