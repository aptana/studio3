/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.index;

import java.net.URI;

import com.aptana.core.util.StringUtil;
import com.aptana.css.core.index.ICSSIndexConstants;
import com.aptana.css.core.model.ElementElement;
import com.aptana.css.core.model.PropertyElement;
import com.aptana.css.core.model.PseudoClassElement;
import com.aptana.css.core.model.PseudoElementElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexWriter;

public class CSSIndexWriter extends IndexWriter
{
	/**
	 * getDocumentPath
	 * 
	 * @return
	 */
	protected URI getDocumentPath()
	{
		return URI.create(ICSSIndexConstants.METADATA_INDEX_LOCATION);
	}

	/**
	 * writeElement
	 * 
	 * @param index
	 * @param element
	 */
	public void writeElement(Index index, ElementElement element)
	{
		if (index != null && element != null)
		{
			String key = StringUtil.join( //
					ICSSIndexConstants.DELIMITER, //
					element.getName(), //
					this.serialize(element) //
					);

			index.addEntry(ICSSIndexConstants.ELEMENT, key, this.getDocumentPath());
		}
	}

	/**
	 * writeProperty
	 * 
	 * @param index
	 * @param property
	 */
	public void writeProperty(Index index, PropertyElement property)
	{
		if (index != null && property != null)
		{
			String key = StringUtil.join( //
					ICSSIndexConstants.DELIMITER, //
					property.getName(), //
					this.serialize(property) //
					);

			index.addEntry(ICSSIndexConstants.PROPERTY, key, this.getDocumentPath());
		}
	}

	/**
	 * writePseudoClassElement
	 * 
	 * @param index
	 * @param pseudoClass
	 */
	public void writePseudoClass(Index index, PseudoClassElement pseudoClass)
	{
		if (index != null && pseudoClass != null)
		{
			String key = this.serialize(pseudoClass);

			index.addEntry(ICSSIndexConstants.PSUEDO_CLASS, key, this.getDocumentPath());
		}
	}

	/**
	 * writePseudoElement
	 * 
	 * @param index
	 * @param pseudoElement
	 */
	public void writePseudoElement(Index index, PseudoElementElement pseudoElement)
	{
		if (index != null && pseudoElement != null)
		{
			String key = this.serialize(pseudoElement);

			index.addEntry(ICSSIndexConstants.PSUEDO_ELEMENT, key, this.getDocumentPath());
		}
	}
}
