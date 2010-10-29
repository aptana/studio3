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
package com.aptana.editor.json.formatter;

import beaver.Symbol;

import com.aptana.editor.json.formatter.nodes.JSONObjectFormatNode;
import com.aptana.editor.json.formatter.nodes.JSONEntryFormatNode;
import com.aptana.editor.json.formatter.nodes.JSONRootFormatNode;
import com.aptana.editor.json.parsing.ast.JSONEntryNode;
import com.aptana.editor.json.parsing.ast.JSONNode;
import com.aptana.editor.json.parsing.ast.JSONNodeType;
import com.aptana.editor.json.parsing.ast.JSONObjectNode;
import com.aptana.editor.json.parsing.ast.JSONParseRootNode;
import com.aptana.editor.json.parsing.ast.JSONTreeWalker;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.nodes.AbstractFormatterNodeBuilder;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.nodes.IFormatterTextNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * CSS formatter node builder.<br>
 * This builder generates the formatter nodes that will then be processed by the {@link JSONFormatterNodeRewriter} to
 * produce the output for the code formatting process.
 */
public class JSONFormatterNodeBuilder extends AbstractFormatterNodeBuilder
{
	private FormatterDocument _document;

	/**
	 * build
	 * 
	 * @param parseResult
	 * @param document
	 * @return
	 */
	public IFormatterContainerNode build(IParseNode parseResult, FormatterDocument document)
	{
		this._document = document;
		
		// create the formatter root node
		IFormatterContainerNode rootNode = new JSONRootFormatNode(document);

		// begin the transformation
		start(rootNode);
		
		JSONFormattingWalker walker = new JSONFormattingWalker();
		JSONParseRootNode jsonRootNode = (JSONParseRootNode) parseResult;
		jsonRootNode.accept(walker);
		
		// end the transformation
		checkedPop(rootNode, document.getLength());

		return rootNode;
	}
	
	public class JSONFormattingWalker extends JSONTreeWalker
	{
		protected void visit(IParseNode node)
		{
			if (node instanceof JSONNode)
			{
				((JSONNode) node).accept(this);
			}
		}
		
		/* (non-Javadoc)
		 * @see com.aptana.editor.json.parsing.ast.JSONTreeWalker#visit(com.aptana.editor.json.parsing.ast.JSONObjectNode)
		 */
		@Override
		public void visit(JSONObjectNode node)
		{
			JSONObjectFormatNode object = new JSONObjectFormatNode(_document, node.getParent().getNodeType() == JSONNodeType.ENTRY.getIndex());
			int startingOffset = node.getStartingOffset();
			int endingOffset = node.getEndingOffset();
			IFormatterTextNode textNode = createTextNode(_document, startingOffset, startingOffset + 1);
			
			object.setBegin(textNode);
			push(object);
			this.visitChildren(node);
			checkedPop(object, endingOffset);
			
			textNode = createTextNode(_document, endingOffset, endingOffset + 1);
			object.setEnd(textNode);
		}

		/* (non-Javadoc)
		 * @see com.aptana.editor.json.parsing.ast.JSONTreeWalker#visit(com.aptana.editor.json.parsing.ast.JSONEntryNode)
		 */
		@Override
		public void visit(JSONEntryNode node)
		{
			JSONEntryFormatNode entry = new JSONEntryFormatNode(_document);
			IParseNode name = node.getFirstChild();
			Symbol colon = node.getColon();
			IParseNode value = node.getLastChild();
			IFormatterTextNode nameText = createTextNode(_document, name.getStartingOffset(), name.getEndingOffset() + 1);
			IFormatterTextNode colonText = createTextNode(_document, colon.getStart(), colon.getEnd() + 1);
			
			entry.addChild(nameText);
			entry.addChild(colonText);
			push(entry);
			this.visit(value);
			checkedPop(entry, node.getEndingOffset() + 1);
		}
	}
}
