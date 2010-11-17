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
package com.aptana.editor.html.formatter.nodes;

import com.aptana.editor.html.formatter.HTMLFormatterConstants;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;

/**
 * An HTML formatter node for special HTML element which represents a non-HTML content, such as CSS, JS etc.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterSpecialElementNode extends FormatterDefaultElementNode
{

	/**
	 * @param document
	 * @param element
	 */
	public FormatterSpecialElementNode(IFormatterDocument document, String element)
	{
		super(document, element);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.html.formatter.nodes.FormatterDefaultElementNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode#accept(com.aptana.formatter.IFormatterContext,
	 * com.aptana.formatter.IFormatterWriter)
	 */
	@Override
	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		// boolean prevValue = context.isInForeignNode();
		visitor.write(context, getStartOffset(), getEndOffset());
		// context.setInForeignNode(true);
		// super.accept(context, visitor);
		// context.setInForeignNode(prevValue);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode#getBlankLinesAfter(com.aptana.formatter.IFormatterContext
	 * )
	 */
	protected int getBlankLinesAfter(IFormatterContext context)
	{
		return getInt(HTMLFormatterConstants.LINES_AFTER_NON_HTML_ELEMENTS);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode#getBlankLinesBefore(com.aptana.formatter.IFormatterContext
	 * )
	 */
	protected int getBlankLinesBefore(IFormatterContext context)
	{
		return getInt(HTMLFormatterConstants.LINES_BEFORE_NON_HTML_ELEMENTS);
	}
}
