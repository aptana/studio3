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
package com.aptana.editor.js.sdoc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import beaver.Symbol;

import com.aptana.core.util.StringUtil;
import com.aptana.parsing.io.SourcePrinter;

public class DocumentationBlock extends Symbol
{
	private String _text;
	private List<Tag> _tags;

	/**
	 * Block
	 * 
	 * @param tags
	 */
	public DocumentationBlock(List<Tag> tags)
	{
		this("", tags); //$NON-NLS-1$
	}

	/**
	 * Block
	 * 
	 * @param text
	 */
	public DocumentationBlock(String text)
	{
		this(text, new ArrayList<Tag>());
	}

	/**
	 * Block
	 * 
	 * @param text
	 * @param tags
	 */
	public DocumentationBlock(String text, List<Tag> tags)
	{
		this._text = text;
		this._tags = tags;
	}

	/**
	 * getTags
	 * 
	 * @return
	 */
	public List<Tag> getTags()
	{
		List<Tag> result = this._tags;
		
		if (result == null)
		{
			result = Collections.emptyList();
		}
		
		return result;
	}

	/**
	 * getTags
	 * 
	 * @param tagSelector
	 * @return
	 */
	public List<Tag> getTags(EnumSet<TagType> tagSelector)
	{
		List<Tag> result;

		if (this._tags != null && this._tags.isEmpty() == false)
		{
			result = new ArrayList<Tag>();

			for (Tag tag : this._tags)
			{
				if (tagSelector.contains(tag.getType()))
				{
					result.add(tag);
				}
			}
		}
		else
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * getTags
	 * 
	 * @param type
	 * @return
	 */
	public List<Tag> getTags(TagType type)
	{
		return this.getTags(EnumSet.of(type));
	}

	/**
	 * getText
	 * 
	 * @return
	 */
	public String getText()
	{
		return this._text;
	}

	/**
	 * hasTags
	 * 
	 * @return
	 */
	public boolean hasTags()
	{
		return (this._tags != null && this._tags.isEmpty() == false);
	}

	/**
	 * setRange
	 * 
	 * @param start
	 * @param end
	 */
	public void setRange(int start, int end)
	{
		this.start = start;
		this.end = end;
	}

	/**
	 * toSource
	 * 
	 * @return
	 */
	public String toSource()
	{
		SourcePrinter writer = new SourcePrinter(" * "); //$NON-NLS-1$

		this.toSource(writer);

		return writer.toString();
	}

	/**
	 * toSource
	 * 
	 * @param writer
	 */
	public void toSource(SourcePrinter writer)
	{
		writer.println("/**").increaseIndent(); //$NON-NLS-1$

		if (this._text != null && StringUtil.isEmpty(this._text) == false)
		{
			writer.printlnWithIndent(this._text);

			if (this.hasTags())
			{
				writer.printIndent().println();
			}
		}

		if (this.hasTags())
		{
			for (Tag tag : this._tags)
			{
				writer.printIndent();
				tag.toSource(writer);
				writer.println();
			}
		}

		writer.decreaseIndent().println(" */"); //$NON-NLS-1$
	}
}
