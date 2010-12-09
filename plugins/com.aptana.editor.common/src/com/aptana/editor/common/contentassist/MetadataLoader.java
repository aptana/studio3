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
package com.aptana.editor.common.contentassist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.editor.common.CommonEditorPlugin;

/**
 * MetadataLoader
 */
public abstract class MetadataLoader<T extends MetadataReader> extends Job
{
	/**
	 * MetadataLoader
	 * 
	 * @param name
	 */
	public MetadataLoader(String name)
	{
		super(name);

		setPriority(Job.LONG);
	}

	/**
	 * createMetadataReader
	 * 
	 * @return
	 */
	protected abstract T createMetadataReader();

	/**
	 * getBundle
	 * 
	 * @return
	 */
	protected abstract Bundle getBundle();

	/**
	 * getDefaultIndexVersion
	 * 
	 * @return
	 */
	protected double getDefaultIndexVersion()
	{
		return 0.0;
	}

	/**
	 * getIndexVersion
	 * 
	 * @return
	 */
	protected abstract double getIndexVersion();

	/**
	 * getIndexVersionKey
	 * 
	 * @return
	 */
	protected abstract String getIndexVersionKey();

	/**
	 * getMetadataFiles
	 * 
	 * @return
	 */
	protected abstract String[] getMetadataFiles();

	/**
	 * getPluginId
	 * 
	 * @return
	 */
	protected abstract String getPluginId();

	/**
	 * loadMetadata
	 * 
	 * @param monitor
	 * @param reader
	 * @param resources
	 */
	private void loadMetadata(IProgressMonitor monitor, T reader, String... resources)
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor, resources.length);

		for (String resource : resources)
		{
			URL url = FileLocator.find(this.getBundle(), new Path(resource), null);

			if (url != null)
			{
				InputStream stream = null;

				try
				{
					stream = url.openStream();

					reader.loadXML(stream);
				}
				catch (Throwable t)
				{
					CommonEditorPlugin.logError(Messages.MetadataLoader_Error_Loading_Metadata + resource, t);
				}
				finally
				{
					if (stream != null)
					{
						try
						{
							stream.close();
						}
						catch (IOException e)
						{
						}
					}
				}
			}

			subMonitor.worked(1);
		}

		subMonitor.done();
	}

	/**
	 * rebuildMetadataIndex
	 * 
	 * @param monitor
	 */
	protected void rebuildMetadataIndex(IProgressMonitor monitor)
	{
		T reader = this.createMetadataReader();

		this.loadMetadata(monitor, reader, this.getMetadataFiles());

		this.writeIndex(reader);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		if (this.versionChanged())
		{
			this.rebuildMetadataIndex(monitor);

			this.updateVersionPreference();

			this.postRebuild();
		}

		return Status.OK_STATUS;
	}

	/**
	 * 
	 */
	protected void postRebuild()
	{
		// Sub-classes should implement this if they need to do more than just rebuilding the metadata index when the
		// index version changes
	}

	/**
	 * updateVersionPreference
	 */
	protected void updateVersionPreference()
	{
		IEclipsePreferences prefs = (new InstanceScope()).getNode(this.getPluginId());

		prefs.putDouble(this.getIndexVersionKey(), this.getIndexVersion());

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}
	}

	/**
	 * versionChanged
	 * 
	 * @return
	 */
	protected boolean versionChanged()
	{
		double expectedVersion = Platform.getPreferencesService().getDouble( //
			this.getPluginId(), //
			this.getIndexVersionKey(), //
			getDefaultIndexVersion(), //
			null //
			);

		return expectedVersion != this.getIndexVersion();
	}

	/**
	 * writeIndex
	 * 
	 * @param reader
	 */
	protected abstract void writeIndex(T reader);
}
