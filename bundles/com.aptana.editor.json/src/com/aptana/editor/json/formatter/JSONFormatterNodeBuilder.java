/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.formatter;

import beaver.Symbol;

import com.aptana.editor.json.formatter.nodes.JSONArrayFormatNode;
import com.aptana.editor.json.formatter.nodes.JSONEntryFormatNode;
import com.aptana.editor.json.formatter.nodes.JSONObjectFormatNode;
import com.aptana.editor.json.formatter.nodes.JSONRootFormatNode;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.nodes.AbstractFormatterNodeBuilder;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.nodes.IFormatterTextNode;
import com.aptana.formatter.nodes.FormatterEmptyNode;
import com.aptana.json.core.parsing.ast.JSONArrayNode;
import com.aptana.json.core.parsing.ast.JSONEntryNode;
import com.aptana.json.core.parsing.ast.JSONFalseNode;
import com.aptana.json.core.parsing.ast.JSONNode;
import com.aptana.json.core.parsing.ast.JSONNullNode;
import com.aptana.json.core.parsing.ast.JSONNumberNode;
import com.aptana.json.core.parsing.ast.JSONObjectNode;
import com.aptana.json.core.parsing.ast.JSONParseRootNode;
import com.aptana.json.core.parsing.ast.JSONStringNode;
import com.aptana.json.core.parsing.ast.JSONTreeWalker;
import com.aptana.json.core.parsing.ast.JSONTrueNode;
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
