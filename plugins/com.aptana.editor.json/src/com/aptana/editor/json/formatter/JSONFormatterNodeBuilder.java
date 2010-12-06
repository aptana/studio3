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

import com.aptana.editor.json.formatter.nodes.JSONArrayFormatNode;
import com.aptana.editor.json.formatter.nodes.JSONEntryFormatNode;
import com.aptana.editor.json.formatter.nodes.JSONObjectFormatNode;
import com.aptana.editor.json.formatter.nodes.JSONRootFormatNode;
import com.aptana.editor.json.parsing.ast.JSONArrayNode;
import com.aptana.editor.json.parsing.ast.JSONEntryNode;
import com.aptana.editor.json.parsing.ast.JSONFalseNode;
import com.aptana.editor.json.parsing.ast.JSONNode;
import com.aptana.editor.json.parsing.ast.JSONNullNode;
import com.aptana.editor.json.parsing.ast.JSONNumberNode;
import com.aptana.editor.json.parsing.ast.JSONObjectNode;
import com.aptana.editor.json.parsing.ast.JSONParseRootNode;
import com.aptana.editor.json.parsing.ast.JSONStringNode;
import com.aptana.editor.json.parsing.ast.JSONTreeWalker;
import com.aptana.editor.json.parsing.ast.JSONTrueNode;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.nodes.AbstractFormatterNodeBuilder;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.nodes.IFormatterTextNode;
import com.aptana.formatter.nodes.FormatterEmptyNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * JSONFormatterNodeBuilder
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
		public void visit(JSONArrayNode node)
		{
			JSONArrayFormatNode formatNode = new JSONArrayFormatNode(_document, node);
			
			this.addComposite(formatNode, node);
		}
		
		/* (non-Javadoc)
		 * @see com.aptana.editor.json.parsing.ast.JSONTreeWalker#visit(com.aptana.editor.json.parsing.ast.JSONObjectNode)
		 */
		@Override
		public void visit(JSONObjectNode node)
		{
			JSONObjectFormatNode formatNode = new JSONObjectFormatNode(_document, node);
			
			this.addComposite(formatNode, node);
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
			
			// add children
			entry.addChild(nameText);
			entry.addChild(colonText);
			
			// push node, visit children, then remove from stack
			push(entry);
			this.visit(value);
			checkedPop(entry, node.getEndingOffset() + 1);
		}

		/* (non-Javadoc)
		 * @see com.aptana.editor.json.parsing.ast.JSONTreeWalker#visit(com.aptana.editor.json.parsing.ast.JSONFalseNode)
		 */
		@Override
		public void visit(JSONFalseNode node)
		{
			this.addPrimitive(node);
		}

		/* (non-Javadoc)
		 * @see com.aptana.editor.json.parsing.ast.JSONTreeWalker#visit(com.aptana.editor.json.parsing.ast.JSONNullNode)
		 */
		@Override
		public void visit(JSONNullNode node)
		{
			this.addPrimitive(node);
		}

		/* (non-Javadoc)
		 * @see com.aptana.editor.json.parsing.ast.JSONTreeWalker#visit(com.aptana.editor.json.parsing.ast.JSONNumberNode)
		 */
		@Override
		public void visit(JSONNumberNode node)
		{
			this.addPrimitive(node);
		}

		/* (non-Javadoc)
		 * @see com.aptana.editor.json.parsing.ast.JSONTreeWalker#visit(com.aptana.editor.json.parsing.ast.JSONStringNode)
		 */
		@Override
		public void visit(JSONStringNode node)
		{
			this.addPrimitive(node);
		}

		/* (non-Javadoc)
		 * @see com.aptana.editor.json.parsing.ast.JSONTreeWalker#visit(com.aptana.editor.json.parsing.ast.JSONTrueNode)
		 */
		@Override
		public void visit(JSONTrueNode node)
		{
			this.addPrimitive(node);
		}
		
		/**
		 * addComposite
		 * 
		 * @param node
		 */
		public void addComposite(FormatterBlockWithBeginEndNode formatNode, JSONNode node)
		{
			int startingOffset = node.getStartingOffset();
			int endingOffset = node.getEndingOffset();
			IFormatterTextNode textNode = createTextNode(_document, startingOffset, startingOffset + 1);
			
			formatNode.setBegin(textNode);
			push(formatNode);
			
			// process children
			this.visitChildren(node);
			
			// remove any trailing whitespace
			int lastOffset = (node.hasChildren()) ? node.getLastChild().getEndingOffset(): startingOffset;
			FormatterEmptyNode emptyNode = new FormatterEmptyNode(_document);
			emptyNode.addChild(createTextNode(_document, lastOffset + 1, endingOffset));
			formatNode.addChild(emptyNode);
			
			// all done
			checkedPop(formatNode, endingOffset);
			
			textNode = createTextNode(_document, endingOffset, endingOffset + 1);
			formatNode.setEnd(textNode);
		}
		
		/**
		 * pushPrimitiveText
		 * 
		 * @param node
		 */
		public void addPrimitive(JSONNode node)
		{
			JSONPrimitiveFormatNode primitive = new JSONPrimitiveFormatNode(_document, node);
			int startingOffset = node.getStartingOffset();
			int endingOffset = node.getEndingOffset();
			IFormatterTextNode textNode = createTextNode(_document, startingOffset, endingOffset + 1);
			
			primitive.addChild(textNode);
			push(primitive);
			checkedPop(primitive, primitive.getEndOffset());
		}
	}
}
