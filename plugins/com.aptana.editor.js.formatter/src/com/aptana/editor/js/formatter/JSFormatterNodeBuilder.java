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
package com.aptana.editor.js.formatter;

import com.aptana.editor.js.formatter.nodes.FormatterJSBlockNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSCaseNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSDeclarationNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSDoWhileBlockNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSFunctionBodyNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSNonBlockedWhileNode;
import com.aptana.editor.js.parsing.ast.JSCaseNode;
import com.aptana.editor.js.parsing.ast.JSDefaultNode;
import com.aptana.editor.js.parsing.ast.JSDoNode;
import com.aptana.editor.js.parsing.ast.JSForInNode;
import com.aptana.editor.js.parsing.ast.JSForNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSIfNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.editor.js.parsing.ast.JSSwitchNode;
import com.aptana.editor.js.parsing.ast.JSTreeWalker;
import com.aptana.editor.js.parsing.ast.JSWhileNode;
import com.aptana.editor.js.parsing.ast.JSWithNode;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.nodes.AbstractFormatterNodeBuilder;
import com.aptana.formatter.nodes.FormatterBlockNode;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * JS formatter node builder.<br>
 * This builder generates the formatter nodes that will then be processed by the {@link JSFormatterNodeRewriter} to
 * produce the output for the code formatting process.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class JSFormatterNodeBuilder extends AbstractFormatterNodeBuilder
{
	private FormatterDocument document;

	/**
	 * @param parseResult
	 * @param document
	 * @return
	 */
	public IFormatterContainerNode build(IParseNode parseResult, FormatterDocument document)
	{
		this.document = document;
		final IFormatterContainerNode rootNode = new FormatterBlockNode(document);
		start(rootNode);
		JSParseRootNode jsRootNode = (JSParseRootNode) parseResult;
		jsRootNode.accept(new JSFormatterTreeWalker());
		checkedPop(rootNode, document.getLength());
		return rootNode;
	}

	private class JSFormatterTreeWalker extends JSTreeWalker
	{

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFunctionNode)
		 */
		@Override
		public void visit(JSFunctionNode node)
		{
			// First, push the function declaration node
			FormatterJSDeclarationNode declarationNode = new FormatterJSDeclarationNode(document, true);
			IParseNode body = node.getBody();
			declarationNode.setBegin(createTextNode(document, node.getStartingOffset(), body.getStartingOffset()));
			push(declarationNode);
			checkedPop(declarationNode, -1);

			// Then, push the body
			FormatterJSFunctionBodyNode bodyNode = new FormatterJSFunctionBodyNode(document);
			bodyNode.setBegin(createTextNode(document, body.getStartingOffset(), body.getStartingOffset() + 1));
			push(bodyNode);
			super.visit(node);
			checkedPop(bodyNode, body.getEndingOffset());
			int end = locateSemicolonInLine(bodyNode.getEndOffset() + 1, document);
			bodyNode.setEnd(createTextNode(document, body.getEndingOffset(), end));
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSIfNode)
		 */
		@Override
		public void visit(JSIfNode node)
		{
			JSNode trueBlock = (JSNode) node.getTrueBlock();
			JSNode falseBlock = (JSNode) node.getFalseBlock();

			boolean isEmptyFalseBlock = (trueBlock.getNodeType() == JSNodeTypes.EMPTY);
			boolean isCurlyTrueBlock = (trueBlock.getNodeType() == JSNodeTypes.STATEMENTS);
			boolean isCurlyFalseBlock = (!isEmptyFalseBlock && falseBlock.getNodeType() == JSNodeTypes.STATEMENTS);

			// First, construct the if condition node
			FormatterJSDeclarationNode conditionNode = new FormatterJSDeclarationNode(document, isCurlyTrueBlock);
			conditionNode.setBegin(createTextNode(document, node.getStartingOffset(), trueBlock.getStartingOffset()));
			push(conditionNode);

			// Construct the 'true' part of the 'if' and visit its children
			if (isCurlyTrueBlock)
			{
				pushBlockNode(trueBlock, isEmptyFalseBlock);
			}
			else
			{
				// Just visit the children
				visitChildren(trueBlock);
			}
			checkedPop(conditionNode, trueBlock.getEndingOffset());

			// Construct the 'false' part if exist
			if (!isEmptyFalseBlock)
			{
				if (isCurlyFalseBlock)
				{
					pushBlockNode(falseBlock, true);
				}
				else
				{
					// Just visit the children
					visitChildren(falseBlock);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSDoNode)
		 */
		@Override
		public void visit(JSDoNode node)
		{
			// First, push the 'do' declaration node
			FormatterJSDeclarationNode declarationNode = new FormatterJSDeclarationNode(document, true);
			IParseNode body = node.getBody();
			declarationNode.setBegin(createTextNode(document, node.getStartingOffset(), body.getStartingOffset()));
			push(declarationNode);
			checkedPop(declarationNode, -1);

			// Push the special do-while block node
			FormatterJSDoWhileBlockNode doWhileBlock = new FormatterJSDoWhileBlockNode(document);
			doWhileBlock.setBegin(createTextNode(document, body.getStartingOffset(), body.getStartingOffset() + 1));
			push(doWhileBlock);
			// visit the body only
			super.visit((JSNode) node.getBody());
			int blockEnd = body.getEndingOffset();
			checkedPop(doWhileBlock, blockEnd);
			doWhileBlock.setEnd(createTextNode(document, blockEnd, blockEnd + 1));

			// now deal with the 'while' condition part. we need to include the word 'while' that appears
			// somewhere between the block-end and the condition start.
			// We wrap this node as a begin-end node that will hold the condition internals as children
			JSNode condition = (JSNode) node.getCondition();
			FormatterJSNonBlockedWhileNode whileNode = new FormatterJSNonBlockedWhileNode(document);
			whileNode.setBegin(createTextNode(document, blockEnd + 1, condition.getStartingOffset()));
			push(whileNode);
			visitChildren(condition);
			int conditionEnd = condition.getEndingOffset() + 1;
			checkedPop(whileNode, conditionEnd);
			int end = locateSemicolonInLine(conditionEnd, document);
			whileNode.setEnd(createTextNode(document, conditionEnd, end));
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSWhileNode)
		 */
		@Override
		public void visit(JSWhileNode node)
		{
			visitCommonBlock(node, node.getBody());
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSWithNode)
		 */
		@Override
		public void visit(JSWithNode node)
		{
			visitCommonBlock(node, node.getBody());
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSForInNode)
		 */
		@Override
		public void visit(JSForInNode node)
		{
			visitCommonBlock(node, node.getBody());
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSForNode)
		 */
		@Override
		public void visit(JSForNode node)
		{
			visitCommonBlock(node, node.getBody());
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSSwitchNode)
		 */
		@Override
		public void visit(JSSwitchNode node)
		{
			// Push the switch-case declaration node
			FormatterJSDeclarationNode switchNode = new FormatterJSDeclarationNode(document, true);
			int blockStart = node.getLeftBrace().getStart();
			switchNode.setBegin(createTextNode(document, node.getStartingOffset(), blockStart));
			push(switchNode);
			checkedPop(switchNode, -1);

			// push a switch-case body node
			FormatterJSBlockNode blockNode = new FormatterJSBlockNode(document);
			blockNode.setBegin(createTextNode(document, blockStart, blockStart + 1));
			push(blockNode);
			// visit the children under that block node
			super.visit(node);
			int endingOffset = node.getEndingOffset();
			// pop the block node
			checkedPop(blockNode, endingOffset);
			int endWithSemicolon = locateSemicolonInLine(endingOffset + 1, document);
			blockNode.setEnd(createTextNode(document, endingOffset, endWithSemicolon));
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSCaseNode)
		 */
		@Override
		public void visit(JSCaseNode node)
		{
			visitCaseOrDefaultNode(node, node.getColon().getStart() + 1);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSDefaultNode)
		 */
		@Override
		public void visit(JSDefaultNode node)
		{
			visitCaseOrDefaultNode(node, node.getColon().getStart() + 1);
		}

		/**
		 * Common visit behavior for 'case' and 'default' nodes under a switch-case block
		 * 
		 * @param node
		 * @param colonOffset
		 */
		private void visitCaseOrDefaultNode(JSNode node, int colonOffset)
		{
			// push the case/default node till the colon
			FormatterJSCaseNode switchNode = new FormatterJSCaseNode(document);
			switchNode.setBegin(createTextNode(document, node.getStartingOffset(), colonOffset));
			push(switchNode);
			visitChildren(node);
			checkedPop(switchNode, node.getEndingOffset() + 1);
		}

		/**
		 * Push a FormatterJSBlockNode
		 * 
		 * @param block
		 */
		private void pushBlockNode(JSNode block, boolean consumeEndingSemicolon)
		{
			FormatterJSBlockNode bodyNode = new FormatterJSBlockNode(document);
			bodyNode.setBegin(createTextNode(document, block.getStartingOffset(), block.getStartingOffset() + 1));
			push(bodyNode);
			// visit the children
			visitChildren(block);
			checkedPop(bodyNode, block.getEndingOffset());
			int end = block.getEndingOffset() + 1;
			if (consumeEndingSemicolon)
			{
				locateSemicolonInLine(end, document);
			}
			bodyNode.setEnd(createTextNode(document, block.getEndingOffset(), block.getEndingOffset() + 1));
		}

		/**
		 * @param node
		 */
		private void visitCommonBlock(JSNode node, IParseNode body)
		{
			// First, push the while declaration
			FormatterJSDeclarationNode declarationNode = new FormatterJSDeclarationNode(document,
					(body.getNodeType() == JSNodeTypes.STATEMENTS));
			declarationNode.setBegin(createTextNode(document, node.getStartingOffset(), body.getStartingOffset()));
			push(declarationNode);
			checkedPop(declarationNode, -1);

			// Then, push the body
			FormatterJSBlockNode blockNode = new FormatterJSBlockNode(document);
			blockNode.setBegin(createTextNode(document, body.getStartingOffset(), body.getStartingOffset() + 1));
			push(blockNode);
			visitChildren(node);
			checkedPop(blockNode, body.getEndingOffset());
			int end = locateSemicolonInLine(blockNode.getEndOffset() + 1, document);
			blockNode.setEnd(createTextNode(document, body.getEndingOffset(), end));
		}

		/**
		 * Scan for a semicolon terminator located at the same line. Return the given offset if non is found.
		 * 
		 * @param offset
		 * @param document
		 * @return The semicolon offset; The given offset if a semicolon not found.
		 */
		private int locateSemicolonInLine(int offset, FormatterDocument document)
		{
			int i = offset;
			int size = document.getLength();
			for (; i < size; i++)
			{
				char c = document.charAt(i);
				if (c == ';')
				{
					return i + 1;
				}
				if (c == '\n' || c == '\r')
				{
					break;
				}
			}
			return offset;
		}

	}
}
