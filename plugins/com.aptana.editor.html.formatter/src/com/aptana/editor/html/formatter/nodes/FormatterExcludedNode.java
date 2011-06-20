/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.formatter.nodes;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import com.aptana.formatter.ExcludeRegionList.EXCLUDE_STRATEGY;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;

/**
 * A formatter node that will be left untouched when the node is written back.<br>
 * This node
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class FormatterExcludedNode extends FormatterBlockWithBeginNode
{

	/**
	 * @param document
	 */
	public FormatterExcludedNode(IFormatterDocument document)
	{
		super(document);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockWithBeginNode#accept(com.aptana.formatter.IFormatterContext,
	 * com.aptana.formatter.IFormatterWriter)
	 */
	@Override
	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		// Add an exclusion
		int startOffset = getStartOffset();
		int endOffset = getEndOffset();
		IRegion excludedRegion = new Region(startOffset, endOffset - startOffset);
		visitor.excludeRegion(excludedRegion, EXCLUDE_STRATEGY.WRITE_AS_IS);
		visitor.writeIndent(context);
		super.accept(context, visitor);
		visitor.ensureLineStarted(context);
	}
}
