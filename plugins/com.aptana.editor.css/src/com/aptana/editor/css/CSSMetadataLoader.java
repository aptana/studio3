/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import java.io.IOException;
import java.net.URI;

import org.osgi.framework.Bundle;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.css.contentassist.CSSIndexQueryHelper;
import com.aptana.editor.css.contentassist.index.CSSIndexWriter;
import com.aptana.editor.css.contentassist.index.CSSMetadataReader;
import com.aptana.editor.css.contentassist.index.ICSSIndexConstants;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.contentassist.model.PseudoClassElement;
import com.aptana.editor.css.contentassist.model.PseudoElementElement;
import com.aptana.editor.css.preferences.IPreferenceConstants;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.MetadataLoader;

public class CSSMetadataLoader extends MetadataLoader<CSSMetadataReader>
{
	/**
	 * CSSMetadataLoader
	 */
	public CSSMetadataLoader()
	{
		super(Messages.CSSMetadataLoader_Loading_Metadata);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#createMetadataReader()
	 */
	@Override
	protected CSSMetadataReader createMetadataReader()
	{
		return new CSSMetadataReader();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#getBundle()
	 */
	@Override
	protected Bundle getBundle()
	{
		return CSSPlugin.getDefault().getBundle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#getIndexVersion()
	 */
	@Override
	protected double getIndexVersion()
	{
		return ICSSIndexConstants.INDEX_VERSION;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#getIndexVersionKey()
	 */
	@Override
	protected String getIndexVersionKey()
	{
		return IPreferenceConstants.CSS_INDEX_VERSION;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#getMetadataFiles()
	 */
	@Override
	protected String[] getMetadataFiles()
	{
		return new String[] { "/metadata/css_metadata.xml" }; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#getPluginId()
	 */
	@Override
	protected String getPluginId()
	{
		return CSSPlugin.PLUGIN_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#writeIndex(com.aptana.editor.common.contentassist.
	 * MetadataReader)
	 */
	@Override
	protected void writeIndex(CSSMetadataReader reader)
	{
		// remove old index
		getIndexManager().removeIndex(URI.create(ICSSIndexConstants.METADATA_INDEX_LOCATION));

		CSSIndexWriter indexer = new CSSIndexWriter();

		// TODO: The following should be done in the index writer, but this will introduce a dependency to
		// com.aptana.parsing in com.aptana.index.core
		Index index = getIndex();

		for (ElementElement element : reader.getElements())
		{
			indexer.writeElement(index, element);
		}

		for (PropertyElement property : reader.getProperties())
		{
			indexer.writeProperty(index, property);
		}

		for (PseudoClassElement pseudoClass : reader.getPseudoClasses())
		{
			indexer.writePseudoClass(index, pseudoClass);
		}

		for (PseudoElementElement pseudoElement : reader.getPseudoElements())
		{
			indexer.writePseudoElement(index, pseudoElement);
		}

		try
		{
			index.save();
		}
		catch (IOException e)
		{
			IdeLog.logError(CSSPlugin.getDefault(), e);
		}
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	@Override
	protected Index getIndex()
	{
		return CSSIndexQueryHelper.getIndex();
	}
}
