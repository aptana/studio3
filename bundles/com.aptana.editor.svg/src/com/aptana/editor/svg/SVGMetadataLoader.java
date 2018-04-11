/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.svg;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.editor.svg.contentassist.index.SVGIndexConstants;
import com.aptana.editor.svg.preferences.IPreferenceConstants;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.xml.core.index.IKeyProvider;
import com.aptana.xml.core.index.XMLIndexWriter;
import com.aptana.xml.core.index.XMLKeyProvider;
import com.aptana.xml.core.model.AttributeElement;
import com.aptana.xml.core.model.DTDTransformException;
import com.aptana.xml.core.model.DTDTransformer;
import com.aptana.xml.core.model.ElementElement;

/**
 * MetadataLoader
 */
public class SVGMetadataLoader extends Job
{
	private static final String SVG_DTD = "DTD/svg11-flat.dtd"; //$NON-NLS-1$

	/**
	 * MetadataLoader
	 */
	public SVGMetadataLoader()
	{
		super(Messages.MetadataLoader_Loading_SVG_Metadata);

		this.setPriority(Job.LONG);
	}

	/**
	 * rebuildMetadataIndex
	 * 
	 * @param monitor
	 */
	private void rebuildMetadataIndex(IProgressMonitor monitor)
	{
		try
		{
			// grab the metadata index URI
			IKeyProvider keyProvider = new XMLKeyProvider();
			XMLIndexWriter writer = new XMLIndexWriter(keyProvider);
			URI metadataLocation = URI.create(keyProvider.getMetadataLocation());

			// remove old index
			getIndexManager().resetIndex(metadataLocation);

			// grab DTD source
			InputStream stream = FileLocator.openStream(Platform.getBundle(SVGPlugin.PLUGIN_ID), new Path(SVG_DTD),
					false);
			String source = IOUtil.read(stream);

			// transform into our CA model
			DTDTransformer transformer = new DTDTransformer();
			transformer.transform(source);

			// get metadata index
			Index index = getIndexManager().getIndex(metadataLocation);

			// write elements to the index
			for (ElementElement element : transformer.getElements())
			{
				writer.writeElement(index, element);
			}

			// write attributes to the index
			for (AttributeElement attribute : transformer.getAttributes())
			{
				writer.writeAttribute(index, attribute);
			}

			index.save();
		}
		catch (IOException e)
		{
			IdeLog.logError(SVGPlugin.getDefault(), e);
		}
		catch (DTDTransformException e)
		{
			IdeLog.logError(SVGPlugin.getDefault(), e);
		}
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		double expectedVersion = Platform.getPreferencesService().getDouble(SVGPlugin.PLUGIN_ID,
				IPreferenceConstants.SVG_INDEX_VERSION, 0.0, null);

		if (expectedVersion != SVGIndexConstants.INDEX_VERSION)
		{
			// rebuild indexes
			rebuildMetadataIndex(monitor);

			// update version preference to latest version
			this.updateVersionPreference();
		}

		return Status.OK_STATUS;
	}

	/**
	 * updateVersionPreference
	 */
	private void updateVersionPreference()
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(SVGPlugin.PLUGIN_ID);

		prefs.putDouble(IPreferenceConstants.SVG_INDEX_VERSION, SVGIndexConstants.INDEX_VERSION);

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}
	}
}
