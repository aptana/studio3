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
package com.aptana.editor.css;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.editor.css.contentassist.CSSIndexQueryHelper;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.css.contentassist.index.CSSIndexWriter;
import com.aptana.editor.css.contentassist.index.CSSMetadataReader;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.preferences.IPreferenceConstants;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class CSSMetadataLoader extends Job
{
	/**
	 * MetadataLoader
	 */
	public CSSMetadataLoader()
	{
		super("Loading CSS metadata...");

		setPriority(Job.LONG);
	}

	/**
	 * loadMetadata
	 * 
	 * @param monitor
	 * @param reader
	 * @param resources
	 */
	private void loadMetadata(IProgressMonitor monitor, CSSMetadataReader reader, String... resources)
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor, resources.length);

		for (String resource : resources)
		{
			URL url = FileLocator.find(CSSPlugin.getDefault().getBundle(), new Path(resource), null);

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
					CSSPlugin.logError("Error loading CSS metadata: " + resource, t);
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
	private void rebuildMetadataIndex(IProgressMonitor monitor)
	{
		IndexManager.getInstance().removeIndex(URI.create(CSSIndexConstants.METADATA_INDEX_LOCATION));

		CSSMetadataReader reader = new CSSMetadataReader();

		this.loadMetadata(monitor, reader, "/metadata/css_metadata.xml");

		CSSIndexWriter indexer = new CSSIndexWriter();
		Index index = CSSIndexQueryHelper.getIndex();

		for (ElementElement element : reader.getElements())
		{
			indexer.writeElement(index, element);
		}

		for (PropertyElement property : reader.getProperties())
		{
			indexer.writeProperty(index, property);
		}

		try
		{
			index.save();
		}
		catch (IOException e)
		{
			CSSPlugin.logError(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		double expectedVersion = Platform.getPreferencesService().getDouble(CSSPlugin.PLUGIN_ID, IPreferenceConstants.CSS_INDEX_VERSION, 0.0, null);

		if (expectedVersion != CSSIndexConstants.INDEX_VERSION)
		{
			// rebuild indexes
			this.rebuildMetadataIndex(monitor);
			// this.rebuildProjectIndexes();

			// update version preference to the latest version
			this.updateVersionPreference();
		}

		return Status.OK_STATUS;
	}

	/**
	 * updateVersionPreference
	 */
	private void updateVersionPreference()
	{
		IEclipsePreferences prefs = (new InstanceScope()).getNode(CSSPlugin.PLUGIN_ID);

		prefs.putDouble(IPreferenceConstants.CSS_INDEX_VERSION, CSSIndexConstants.INDEX_VERSION);

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}
	}
}
