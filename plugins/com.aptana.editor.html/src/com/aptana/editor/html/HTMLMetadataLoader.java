/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import java.io.IOException;
import java.net.URI;

import org.osgi.framework.Bundle;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.html.contentassist.HTMLIndexQueryHelper;
import com.aptana.editor.html.contentassist.index.HTMLIndexWriter;
import com.aptana.editor.html.contentassist.index.HTMLMetadataReader;
import com.aptana.editor.html.contentassist.index.IHTMLIndexConstants;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
import com.aptana.editor.html.contentassist.model.EventElement;
import com.aptana.editor.html.core.preferences.IPreferenceConstants;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.MetadataLoader;

/**
 * HTMLMetadataLoader
 */
public class HTMLMetadataLoader extends MetadataLoader<HTMLMetadataReader>
{
	/**
	 * HTMLMetadataLoader
	 */
	public HTMLMetadataLoader()
	{
		super(Messages.HTMLMetadataLoader_Loading_Metadata);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#createMetadataReader()
	 */
	@Override
	protected HTMLMetadataReader createMetadataReader()
	{
		return new HTMLMetadataReader();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#getBundle()
	 */
	@Override
	protected Bundle getBundle()
	{
		return HTMLPlugin.getDefault().getBundle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#getIndexVersion()
	 */
	@Override
	protected double getIndexVersion()
	{
		return IHTMLIndexConstants.INDEX_VERSION;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#getIndexVersionKey()
	 */
	@Override
	protected String getIndexVersionKey()
	{
		return IPreferenceConstants.HTML_INDEX_VERSION;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#getMetadataFiles()
	 */
	@Override
	protected String[] getMetadataFiles()
	{
		return new String[] { "/metadata/html_metadata.xml" }; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#getPluginId()
	 */
	@Override
	protected String getPluginId()
	{
		return HTMLPlugin.PLUGIN_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#writeIndex(com.aptana.editor.common.contentassist.
	 * MetadataReader)
	 */
	@Override
	protected void writeIndex(HTMLMetadataReader reader)
	{
		// remove old index
		getIndexManager().resetIndex(URI.create(IHTMLIndexConstants.METADATA_INDEX_LOCATION));

		HTMLIndexWriter indexer = new HTMLIndexWriter();

		// TODO: The following should be done in the index writer, but this will introduce a dependency to
		// com.aptana.parsing in com.aptana.index.core
		Index index = getIndex();

		for (ElementElement element : reader.getElements())
		{
			indexer.writeElement(index, element);
		}

		for (AttributeElement attribute : reader.getAttributes())
		{
			indexer.writeAttribute(index, attribute);
		}

		for (EventElement event : reader.getEvents())
		{
			indexer.writeEvent(index, event);
		}

		for (EntityElement entity : reader.getEntities())
		{
			indexer.writeEntity(index, entity);
		}

		try
		{
			index.save();
		}
		catch (IOException e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), e);
		}
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	@Override
	protected Index getIndex()
	{
		return HTMLIndexQueryHelper.getIndex();
	}
}
