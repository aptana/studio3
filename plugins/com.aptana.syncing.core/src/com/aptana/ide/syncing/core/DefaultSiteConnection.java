/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.epl.XMLMemento;
import com.aptana.ide.core.io.ConnectionPointUtils;
import com.aptana.ide.core.io.IConnectionPoint;

/**
 * A singleton for defining the default connection available to users.
 * 
 * @author Michael Xia (mxia@aptana.com)
 */
public final class DefaultSiteConnection extends SiteConnection
{

	public static final String NAME = "Default"; //$NON-NLS-1$

	protected static final String STATE_FILENAME = "defaultConnection"; //$NON-NLS-1$

	private static final String ELEMENT_ROOT = "connection"; //$NON-NLS-1$
	private static final String ELEMENT_SITE = "connection"; //$NON-NLS-1$

	private static final String HOME_DIR = System.getProperty("user.home"); //$NON-NLS-1$

	private static DefaultSiteConnection fInstance;

	private DefaultSiteConnection()
	{
	}

	public static DefaultSiteConnection getInstance()
	{
		if (fInstance == null)
		{
			fInstance = new DefaultSiteConnection();
			fInstance.setName(NAME);
			fInstance.setSource(getDefaultSource());
		}
		return fInstance;
	}

	/**
	 * loadState
	 * 
	 * @param path
	 */
	protected void loadState(IPath path)
	{
		File file = path.toFile();
		if (file.exists())
		{
			FileReader reader = null;
			try
			{
				reader = new FileReader(file);
				XMLMemento memento = XMLMemento.createReadRoot(reader);
				loadState(memento.getChild(ELEMENT_SITE));

				fInstance.setName(NAME);
				if (fInstance.getSource() == null)
				{
					fInstance.setSource(getDefaultSource());
				}
				// the destination is context sensitive, so sets back to null
				fInstance.setDestination(null);
			}
			catch (IOException e)
			{
			}
			catch (CoreException e)
			{
			}
			finally
			{
				if (reader != null)
				{
					try
					{
						reader.close();
					}
					catch (IOException e)
					{
					}
				}
			}
		}
	}

	/**
	 * saveState
	 * 
	 * @param path
	 */
	protected void saveState(IPath path)
	{
		XMLMemento memento = XMLMemento.createWriteRoot(ELEMENT_ROOT);
		saveState(memento.createChild(ELEMENT_SITE));
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(path.toFile());
			memento.save(writer);
			isChanged();
		}
		catch (IOException e)
		{
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}

	private static IConnectionPoint getDefaultSource()
	{
		return ConnectionPointUtils.findOrCreateLocalConnectionPoint(Path.fromOSString(HOME_DIR));
	}
}
