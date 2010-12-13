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
import java.net.URI;

import org.osgi.framework.Bundle;

import com.aptana.editor.common.contentassist.MetadataLoader;
import com.aptana.editor.css.contentassist.CSSIndexQueryHelper;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.css.contentassist.index.CSSIndexWriter;
import com.aptana.editor.css.contentassist.index.CSSMetadataReader;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.contentassist.model.PseudoClassElement;
import com.aptana.editor.css.contentassist.model.PseudoElementElement;
import com.aptana.editor.css.preferences.IPreferenceConstants;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

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
		return CSSIndexConstants.INDEX_VERSION;
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
		IndexManager.getInstance().removeIndex(URI.create(CSSIndexConstants.METADATA_INDEX_LOCATION));

		CSSIndexWriter indexer = new CSSIndexWriter();
		
		// TODO: The following should be done in the index writer, but this will introduce a dependency to com.aptana.parsing in com.aptana.index.core
		Index index = CSSIndexQueryHelper.getIndex();

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
			CSSPlugin.logError(e.getMessage(), e);
		}
	}
}
