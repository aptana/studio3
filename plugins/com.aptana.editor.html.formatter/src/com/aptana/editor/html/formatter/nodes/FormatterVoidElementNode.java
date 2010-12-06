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

import java.util.Set;

import com.aptana.editor.html.formatter.HTMLFormatterConstants;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;

/**
 * A formatter node which represents a 'Void' element (as describe at W3C).<br>
 * A 'Void' element does not, and should not, have any closing tag pair. They only have a start tag.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterVoidElementNode extends FormatterBlockWithBeginNode
{

	private String element;

	/**
	 * Constructs a new FormatterVoidElementNode
	 * 
	 * @param document
	 */
	public FormatterVoidElementNode(IFormatterDocument document, String element)
	{
		super(document);
		this.element = element;
	}

	/**
	 * Returns true if the element should trigger a deeper indentation on the next line. <br>
	 * Note that this node is representing a 'void' node, so in most cases (if not all) this call should return false.
	 * However, we leave this open for the user decision, and in case this node is removed from the exclusions list, it
	 * will increase the indentation.<br>
	 * Because this node does not have any closing tag, there will be no way to decrease the indentation once it's
	 * increased!
	 * 
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		Set<String> set = getDocument().getSet(HTMLFormatterConstants.INDENT_EXCLUDED_TAGS);
		return !set.contains(element);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	protected boolean isAddingBeginNewLine()
	{
		Set<String> set = getDocument().getSet(HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS);
		return !set.contains(element);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingEndNewLine()
	 */
	protected boolean isAddingEndNewLine()
	{
		return isAddingBeginNewLine();
	}
}
