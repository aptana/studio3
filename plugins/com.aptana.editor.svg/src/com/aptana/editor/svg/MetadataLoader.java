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

import com.aptana.core.util.IOUtil;
import com.aptana.editor.js.Activator;
import com.aptana.editor.svg.contentassist.index.SVGIndexConstants;
import com.aptana.editor.svg.preferences.IPreferenceConstants;
import com.aptana.editor.xml.contentassist.index.IKeyProvider;
import com.aptana.editor.xml.contentassist.index.XMLIndexWriter;
import com.aptana.editor.xml.contentassist.index.XMLKeyProvider;
import com.aptana.editor.xml.contentassist.model.AttributeElement;
import com.aptana.editor.xml.contentassist.model.DTDTransformer;
import com.aptana.editor.xml.contentassist.model.ElementElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

/**
 * MetadataLoader
 */
public class MetadataLoader extends Job
{
	private static final String SVG_DTD = "DTD/svg11-flat.dtd"; //$NON-NLS-1$

	/**
	 * MetadataLoader
	 */
	public MetadataLoader()
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
			IndexManager.getInstance().removeIndex(metadataLocation);

			// grab DTD source
			InputStream stream = FileLocator.openStream(Platform.getBundle(SVGPlugin.PLUGIN_ID), new Path(SVG_DTD), false);
			String source = IOUtil.read(stream);

			// transform into our CA model
			DTDTransformer transformer = new DTDTransformer();
			transformer.transform(source);

			// get metadata index
			Index index = IndexManager.getInstance().getIndex(metadataLocation);

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
			SVGPlugin.logError(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		double expectedVersion = Platform.getPreferencesService().getDouble(SVGPlugin.PLUGIN_ID, IPreferenceConstants.SVG_INDEX_VERSION, 0.0, null);

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
		IEclipsePreferences prefs = (new InstanceScope()).getNode(Activator.PLUGIN_ID);

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
