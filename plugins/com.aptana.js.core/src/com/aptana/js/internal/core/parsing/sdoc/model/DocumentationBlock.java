/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing.sdoc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import beaver.Symbol;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;

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
	public List<Tag> getTags(EnumSet<TagType> tagSelector) // $codepro.audit.disable declareAsInterface
	{
		List<Tag> result;

		if (!CollectionsUtil.isEmpty(this._tags))
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
	 * Determine if this documentation block contains the given tag type.
	 * 
	 * @param type
	 *            The tag type to test
	 * @return Returns a boolean
	 */
	public boolean hasTag(TagType type)
	{
		boolean result = false;

		if (type != null && !CollectionsUtil.isEmpty(this._tags))
		{
			for (Tag tag : this._tags)
			{
				if (tag.getType() == type)
				{
					result = true;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * hasTags
	 * 
	 * @return
	 */
	public boolean hasTags()
	{
		return this._tags != null && !this._tags.isEmpty();
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

		if (this._text != null && !StringUtil.isEmpty(this._text))
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
