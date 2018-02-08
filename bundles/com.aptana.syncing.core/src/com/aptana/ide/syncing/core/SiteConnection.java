/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.syncing.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;

import com.aptana.core.epl.IMemento;
import com.aptana.core.logging.IdeLog;
import com.aptana.ide.core.io.ConnectionPointType;
import com.aptana.ide.core.io.ConnectionPointUtils;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;

/**
 * @author Max Stepanov
 */
/* package */class SiteConnection extends PlatformObject implements ISiteConnection
{

	private static final String ELEMENT_NAME = "name"; //$NON-NLS-1$
	private static final String ELEMENT_SOURCE = "source"; //$NON-NLS-1$
	private static final String ELEMENT_DESTINATION = "destination"; //$NON-NLS-1$
	private static final String ELEMENT_EXCLUDES = "excludes"; //$NON-NLS-1$
	private static final String ELEMENT_PATH = "path"; //$NON-NLS-1$
	private static final String ELEMENT_WILDCARD = "wildcard"; //$NON-NLS-1$

	private String name;
	private IConnectionPoint sourceConnectionPoint;
	private IConnectionPoint destinationConnectionPoint;
	private List<Object> excludes = new ArrayList<Object>();

	private boolean dirty;

	/**
	 * 
	 */
	/* package */SiteConnection()
	{
	}

	/**
	 * @see com.aptana.ide.syncing.core.ISiteConnection#getName()
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * set site connection name
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
		notifyChanged();
	}

	/**
	 * @see com.aptana.ide.syncing.core.ISiteConnection#getSource()
	 */
	public IConnectionPoint getSource()
	{
		return sourceConnectionPoint;
	}

	/**
	 * Sets the source connection point.
	 * 
	 * @param source
	 */
	public void setSource(IConnectionPoint source)
	{
		this.sourceConnectionPoint = source;
		notifyChanged();
	}

	/**
	 * @see com.aptana.ide.syncing.core.ISiteConnection#getDestination()
	 */
	public IConnectionPoint getDestination()
	{
		return destinationConnectionPoint;
	}

	/**
	 * Sets the destination connection point.
	 * 
	 * @param destination
	 */
	public void setDestination(IConnectionPoint destination)
	{
		this.destinationConnectionPoint = destination;
		notifyChanged();
	}

	/**
	 * @see com.aptana.ide.syncing.core.ISiteConnection#excludes(org.eclipse.core.runtime.IPath)
	 */
	public boolean excludes(IPath path)
	{
		for (Object i : excludes)
		{
			if (i instanceof IPath)
			{
				if (path.equals(i))
				{
					return true;
				}
			}
		}
		return false;
	}

	public void addExcludePath(IPath path)
	{
		if (!excludes.contains(path))
		{
			excludes.add(path);
		}
	}

	public void addExcludeWildcard(String wildcard)
	{
		if (!excludes.contains(wildcard))
		{
			excludes.add(wildcard);
		}
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append('(');
		IConnectionPoint source = getSource();
		if (source == null)
		{
			text.append(Messages.SiteConnection_LBL_NoSource);
		}
		else
		{
			ConnectionPointType type = CoreIOPlugin.getConnectionPointManager().getType(source);
			if (type != null)
			{
				text.append(type.getName()).append(':');
			}
			text.append(source.getName());
		}

		text.append(" <-> "); //$NON-NLS-1$

		IConnectionPoint target = getDestination();
		if (target == null)
		{
			text.append(Messages.SiteConnection_LBL_NoDestination);
		}
		else
		{
			ConnectionPointType type = CoreIOPlugin.getConnectionPointManager().getType(target);
			if (type != null)
			{
				text.append(type.getName()).append(':');
			}
			text.append(target.getName());
		}
		text.append(')');

		return text.toString();
	}

	protected void loadState(IMemento memento)
	{
		IMemento child = memento.getChild(ELEMENT_NAME);
		if (child != null)
		{
			name = child.getTextData();
		}
		child = memento.getChild(ELEMENT_SOURCE);
		if (child != null)
		{
			URI uri = URI.create(child.getTextData());
			sourceConnectionPoint = ConnectionPointUtils.findConnectionPoint(uri);
			if (sourceConnectionPoint == null)
			{
				IdeLog.logWarning(SyncingPlugin.getDefault(), "Failed to load source connection point from URI " + uri, //$NON-NLS-1$
						IDebugScopes.DEBUG);
			}
		}
		child = memento.getChild(ELEMENT_DESTINATION);
		if (child != null)
		{
			URI uri = URI.create(child.getTextData());
			destinationConnectionPoint = ConnectionPointUtils.findConnectionPoint(uri);
			if (destinationConnectionPoint == null)
			{
				IdeLog.logWarning(SyncingPlugin.getDefault(),
						"Failed to load destination connection point from URI " + uri, //$NON-NLS-1$
						IDebugScopes.DEBUG);
			}
		}
		child = memento.getChild(ELEMENT_EXCLUDES);
		if (child != null)
		{
			for (IMemento i : child.getChildren(ELEMENT_PATH))
			{
				excludes.add(Path.fromPortableString(i.getTextData()));
			}
			for (IMemento i : child.getChildren(ELEMENT_WILDCARD))
			{
				excludes.add(i.getTextData());
			}
		}
	}

	protected void saveState(IMemento memento)
	{
		memento.createChild(ELEMENT_NAME).putTextData(name);
		if (sourceConnectionPoint != null)
		{
			memento.createChild(ELEMENT_SOURCE).putTextData(sourceConnectionPoint.getRootURI().toString());
		}
		if (destinationConnectionPoint != null)
		{
			memento.createChild(ELEMENT_DESTINATION).putTextData(destinationConnectionPoint.getRootURI().toString());
		}
		if (!excludes.isEmpty())
		{
			IMemento excludesMemento = memento.createChild(ELEMENT_EXCLUDES);
			for (Object i : excludes)
			{
				if (i instanceof IPath)
				{
					excludesMemento.createChild(ELEMENT_PATH).putTextData(((IPath) i).toPortableString());
				}
				else if (i instanceof String)
				{
					excludesMemento.createChild(ELEMENT_PATH).putTextData((String) i);
				}
			}
		}
	}

	protected final void notifyChanged()
	{
		dirty = true;
	}

	/* package */final boolean isChanged()
	{
		try
		{
			return dirty;
		}
		finally
		{
			dirty = false;
		}
	}

	/* package */final boolean isValid()
	{
		return sourceConnectionPoint != null && destinationConnectionPoint != null;
	}
}
