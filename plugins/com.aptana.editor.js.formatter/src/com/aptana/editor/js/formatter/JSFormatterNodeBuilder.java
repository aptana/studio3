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
import com.aptana.editor.js.formatter.nodes.FormatterJSDefaultLineNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSElseIfNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSElseNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSFunctionBodyNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSGroupNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSNonBlockedWhileNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSObjectNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSSwitchNode;
import com.aptana.editor.js.parsing.ast.JSAssignmentNode;
import com.aptana.editor.js.parsing.ast.JSBinaryArithmeticOperatorNode;
import com.aptana.editor.js.parsing.ast.JSBinaryBooleanOperatorNode;
import com.aptana.editor.js.parsing.ast.JSBreakNode;
import com.aptana.editor.js.parsing.ast.JSCaseNode;
import com.aptana.editor.js.parsing.ast.JSCatchNode;
import com.aptana.editor.js.parsing.ast.JSContinueNode;
import com.aptana.editor.js.parsing.ast.JSDefaultNode;
import com.aptana.editor.js.parsing.ast.JSDoNode;
import com.aptana.editor.js.parsing.ast.JSErrorNode;
import com.aptana.editor.js.parsing.ast.JSFinallyNode;
import com.aptana.editor.js.parsing.ast.JSForInNode;
import com.aptana.editor.js.parsing.ast.JSForNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGroupNode;
import com.aptana.editor.js.parsing.ast.JSIfNode;
import com.aptana.editor.js.parsing.ast.JSInvokeNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.editor.js.parsing.ast.JSObjectNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.editor.js.parsing.ast.JSReturnNode;
import com.aptana.editor.js.parsing.ast.JSSwitchNode;
import com.aptana.editor.js.parsing.ast.JSThrowNode;
import com.aptana.editor.js.parsing.ast.JSTreeWalker;
import com.aptana.editor.js.parsing.ast.JSTryNode;
import com.aptana.editor.js.parsing.ast.JSVarNode;
import com.aptana.editor.js.parsing.ast.JSWhileNode;
import com.aptana.editor.js.parsing.ast.JSWithNode;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.nodes.AbstractFormatterNodeBuilder;
import com.aptana.formatter.nodes.FormatterBlockNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;
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

	/**
	 * Build the formatter nodes by walking the JavaScript AST.
	 */
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
			FormatterJSDeclarationNode declarationNode = new FormatterJSDeclarationNode(document, true, node);
			declarationNode.setBegin(createTextNode(document, node.getStartingOffset(), node.getParameters()
					.getEndingOffset() + 1));
			push(declarationNode);
			checkedPop(declarationNode, -1);

			// Then, push the body
			IParseNode body = node.getBody();
			FormatterJSFunctionBodyNode bodyNode = new FormatterJSFunctionBodyNode(document);
			bodyNode.setBegin(createTextNode(document, body.getStartingOffset(), body.getStartingOffset() + 1));
			push(bodyNode);
			super.visit(node);
			checkedPop(bodyNode, body.getEndingOffset());
			int end = locateColonOrSemicolonInLine(bodyNode.getEndOffset() + 1, document);
			bodyNode.setEnd(createTextNode(document, body.getEndingOffset(), end));
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSErrorNode)
		 */
		@Override
		public void visit(JSErrorNode node)
		{
			// do nothing. Avoid visiting the children.
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

			boolean isEmptyFalseBlock = (falseBlock.getNodeType() == JSNodeTypes.EMPTY);
			boolean isCurlyTrueBlock = (trueBlock.getNodeType() == JSNodeTypes.STATEMENTS);
			boolean isCurlyFalseBlock = (!isEmptyFalseBlock && falseBlock.getNodeType() == JSNodeTypes.STATEMENTS);
			// First, construct the if condition node
			FormatterBlockWithBeginNode conditionNode = new FormatterJSDeclarationNode(document, isCurlyTrueBlock, node);
			conditionNode.setBegin(createTextNode(document, node.getStartingOffset(), node.getRightParenthesis()
					.getEnd() + 1));
			push(conditionNode);

			// Construct the 'true' part of the 'if' and visit its children
			if (isCurlyTrueBlock)
			{
				pushBlockNode(trueBlock, isEmptyFalseBlock);
			}
			else
			{
				// Just visit the children
				trueBlock.accept(this);
			}
			checkedPop(conditionNode, trueBlock.getEndingOffset());

			if (!isEmptyFalseBlock)
			{
				// Construct the 'false' part if exist.
				// Note that the JS parser does not provide us with the start offset of the 'else' keyword, so we need
				// to
				// locate it in between the end of the 'true' block and the begin of the 'false' block.
				int trueBlockEnd = trueBlock.getEndingOffset();
				int falseBlockStart = falseBlock.getStartingOffset();
				String segment = document.get(trueBlockEnd + 1, falseBlockStart);
				int elsePos = segment.toLowerCase().indexOf("else"); //$NON-NLS-1$
				int elseBlockStart = elsePos + trueBlockEnd + 1;
				int elseBlockDeclarationEnd = elseBlockStart + 4; // +4 for the keyword 'else'
				FormatterBlockWithBeginNode elseNode = new FormatterJSElseNode(document, isCurlyFalseBlock);
				elseNode.setBegin(createTextNode(document, elseBlockStart, elseBlockDeclarationEnd));
				push(elseNode);
				if (isCurlyFalseBlock)
				{
					pushBlockNode(falseBlock, true);
				}
				else
				{
					boolean isElseIf = (falseBlock.getNodeType() == JSNodeTypes.IF);
					if (isElseIf)
					{
						// Wrap the incoming 'if' with an Else-If node that will allow us later to break it and indent
						// it.
						FormatterJSElseIfNode elseIfNode = new FormatterJSElseIfNode(document);
						elseIfNode.setBegin(createTextNode(document, falseBlockStart, falseBlockStart));
						push(elseIfNode);
						falseBlock.accept(this);
						int falseBlockEnd = falseBlock.getEndingOffset() + 1;
						checkedPop(elseIfNode, falseBlockEnd);
						elseIfNode.setEnd(createTextNode(document, falseBlockEnd, falseBlockEnd));
					}
					else
					{
						// Just visit the children
						falseBlock.accept(this);
					}
				}
				checkedPop(elseNode, falseBlock.getEndingOffset() + 1);
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
			FormatterJSDeclarationNode declarationNode = new FormatterJSDeclarationNode(document, true, node);
			int startingOffset = node.getStartingOffset();
			declarationNode.setBegin(createTextNode(document, startingOffset, startingOffset + 2));
			push(declarationNode);
			checkedPop(declarationNode, -1);

			// Push the special do-while block node
			JSNode body = (JSNode) node.getBody();
			FormatterJSBlockNode doWhileBlock = new FormatterJSBlockNode(document);
			doWhileBlock.setBegin(createTextNode(document, body.getStartingOffset(), body.getStartingOffset() + 1));
			push(doWhileBlock);
			// visit the body only
			body.accept(this);
			int blockEnd = body.getEndingOffset();
			checkedPop(doWhileBlock, blockEnd);
			doWhileBlock.setEnd(createTextNode(document, blockEnd, blockEnd + 1));

			// now deal with the 'while' condition part. we need to include the word 'while' that appears
			// somewhere between the block-end and the condition start.
			// We wrap this node as a begin-end node that will hold the condition internals as children
			JSNode condition = (JSNode) node.getCondition();
			FormatterJSNonBlockedWhileNode whileNode = new FormatterJSNonBlockedWhileNode(document);
			// Search for the exact 'while' start offset
			int whileBeginOffset = locateCharacterSkippingWhitespaces(document, blockEnd + 1, 'w', true);
			whileNode.setBegin(createTextNode(document, whileBeginOffset, condition.getStartingOffset()));
			push(whileNode);
			visitChildren(condition);
			int conditionEnd = condition.getEndingOffset() + 1;
			checkedPop(whileNode, conditionEnd);
			conditionEnd = locateCharacterSkippingWhitespaces(document, conditionEnd, ')', false);
			int end = locateColonOrSemicolonInLine(conditionEnd + 1, document);
			whileNode.setEnd(createTextNode(document, conditionEnd, end));
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSWhileNode)
		 */
		@Override
		public void visit(JSWhileNode node)
		{
			visitCommonBlock(node, node.getRightParenthesis().getStart(), node.getBody());
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSWithNode)
		 */
		@Override
		public void visit(JSWithNode node)
		{
			visitCommonBlock(node, node.getRightParenthesis().getStart(), node.getBody());
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSForInNode)
		 */
		@Override
		public void visit(JSForInNode node)
		{
			visitCommonBlock(node, node.getRightParenthesis().getStart(), node.getBody());
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSForNode)
		 */
		@Override
		public void visit(JSForNode node)
		{
			visitCommonBlock(node, node.getRightParenthesis().getStart(), node.getBody());
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSObjectNode)
		 */
		@Override
		public void visit(JSObjectNode node)
		{
			FormatterJSObjectNode objectNode = new FormatterJSObjectNode(document);
			objectNode.setBegin(createTextNode(document, node.getStartingOffset(), node.getStartingOffset() + 1));
			push(objectNode);
			visitChildren(node);
			int end = node.getEndingOffset();
			checkedPop(objectNode, end);
			end = locateColonOrSemicolonInLine(end + 1, document);
			objectNode.setEnd(createTextNode(document, node.getEndingOffset(), end));
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSSwitchNode)
		 */
		@Override
		public void visit(JSSwitchNode node)
		{
			// Push the switch-case declaration node
			FormatterJSDeclarationNode switchNode = new FormatterJSDeclarationNode(document, true, node);
			switchNode.setBegin(createTextNode(document, node.getStartingOffset(),
					node.getRightParenthesis().getEnd() + 1));
			push(switchNode);
			checkedPop(switchNode, -1);

			// push a switch-case body node
			int blockStart = node.getLeftBrace().getStart();
			FormatterJSSwitchNode blockNode = new FormatterJSSwitchNode(document);
			blockNode.setBegin(createTextNode(document, blockStart, blockStart + 1));
			push(blockNode);
			// visit the children under that block node
			super.visit(node);
			int endingOffset = node.getEndingOffset();
			// pop the block node
			checkedPop(blockNode, endingOffset);
			int endWithSemicolon = locateColonOrSemicolonInLine(endingOffset + 1, document);
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

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSTryNode)
		 */
		@Override
		public void visit(JSTryNode node)
		{
			// The declarationEndOffset that we pass here is defined to the end of the 'try' word (avoiding any trailing
			// new-lines and spaces)
			visitCommonBlock(node, node.getStartingOffset() + 2, node.getBody());
			IParseNode catchBlock = node.getCatchBlock();
			if (catchBlock.getNodeType() == JSNodeTypes.CATCH)
			{
				visit((JSCatchNode) catchBlock);
			}
			IParseNode finallyBlock = node.getFinallyBlock();
			if (finallyBlock.getNodeType() == JSNodeTypes.FINALLY)
			{
				visit((JSFinallyNode) finallyBlock);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSCatchNode)
		 */
		@Override
		public void visit(JSCatchNode node)
		{
			// search for the closing brace of the catch declaration
			int declarationEndOffset = node.getIdentifier().getEndingOffset() + 1;
			declarationEndOffset = locateCharacterSkippingWhitespaces(document, declarationEndOffset, ')', false);
			visitCommonBlock(node, declarationEndOffset, node.getBody());
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFinallyNode)
		 */
		@Override
		public void visit(JSFinallyNode node)
		{
			// The declarationEndOffset that we pass here is defined to the end of the 'finally' word (avoiding any
			// trailing new-lines and spaces)
			visitCommonBlock(node, node.getStartingOffset() + 6, node.getBlock());
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSAssignmentNode)
		 */
		@Override
		public void visit(JSAssignmentNode node)
		{
			if (node.getSemicolonIncluded())
			{
				IFormatterContainerNode lineNode = pushLineNode(node);
				super.visit(node);
				checkedPop(lineNode, lineNode.getEndOffset());
			}
			else
			{
				super.visit(node);
			}
		}

		/*
		 * (non-Javadoc)
		 * @seecom.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.
		 * JSBinaryArithmeticOperatorNode)
		 */
		@Override
		public void visit(JSBinaryArithmeticOperatorNode node)
		{
			if (node.getSemicolonIncluded())
			{
				IFormatterContainerNode lineNode = pushLineNode(node);
				super.visit(node);
				checkedPop(lineNode, lineNode.getEndOffset());
			}
			else
			{
				super.visit(node);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSBinaryBooleanOperatorNode
		 * )
		 */
		@Override
		public void visit(JSBinaryBooleanOperatorNode node)
		{
			if (node.getSemicolonIncluded())
			{
				IFormatterContainerNode lineNode = pushLineNode(node);
				super.visit(node);
				checkedPop(lineNode, lineNode.getEndOffset());
			}
			else
			{
				super.visit(node);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSContinueNode)
		 */
		@Override
		public void visit(JSContinueNode node)
		{
			if (node.getSemicolonIncluded())
			{
				IFormatterContainerNode lineNode = pushLineNode(node);
				super.visit(node);
				checkedPop(lineNode, lineNode.getEndOffset());
			}
			else
			{
				super.visit(node);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSInvokeNode)
		 */
		@Override
		public void visit(JSInvokeNode node)
		{
			if (node.getSemicolonIncluded())
			{
				IFormatterContainerNode lineNode = pushLineNode(node);
				super.visit(node);
				checkedPop(lineNode, lineNode.getEndOffset());
			}
			else
			{
				super.visit(node);
			}
			// FormatterJSInvokeNode invokeNode = new FormatterJSInvokeNode(document);
			// IParseNode arguments = node.getArguments();
			// int beginOffset = node.getStartingOffset();
			// invokeNode.setBegin(createTextNode(document, beginOffset, arguments.getStartingOffset() + 1));
			// push(invokeNode);
			// visitChildren(node);
			// int invokeEnd = invokeNode.getEndOffset();
			// checkedPop(invokeNode, invokeEnd);
			// int endOffset = locateColonOrSemicolonInLine(arguments.getEndingOffset() + 1, document);
			// invokeNode.setEnd(createTextNode(document, invokeEnd, endOffset));
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSReturnNode)
		 */
		@Override
		public void visit(JSReturnNode node)
		{
			if (node.getSemicolonIncluded())
			{
				IFormatterContainerNode lineNode = pushLineNode(node);
				super.visit(node);
				checkedPop(lineNode, lineNode.getEndOffset());
			}
			else
			{
				super.visit(node);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSThrowNode)
		 */
		@Override
		public void visit(JSThrowNode node)
		{
			// TODO Auto-generated method stub
			super.visit(node);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSBreakNode)
		 */
		@Override
		public void visit(JSBreakNode node)
		{
			if (node.getSemicolonIncluded())
			{
				IFormatterContainerNode lineNode = pushLineNode(node);
				super.visit(node);
				checkedPop(lineNode, lineNode.getEndOffset());
			}
			else
			{
				super.visit(node);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSVarNode)
		 */
		@Override
		public void visit(JSVarNode node)
		{
			if (node.getSemicolonIncluded())
			{
				IFormatterContainerNode lineNode = pushLineNode(node);
				super.visit(node);
				checkedPop(lineNode, lineNode.getEndOffset());
			}
			else
			{
				super.visit(node);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSGroupNode)
		 */
		@Override
		public void visit(JSGroupNode node)
		{
			int beginOffset = node.getLeftParenthesis().getStart();
			int endOffset = node.getRightParenthesis().getStart();
			FormatterJSGroupNode groupNode = new FormatterJSGroupNode(document);
			groupNode.setBegin(createTextNode(document, beginOffset, beginOffset + 1));
			push(groupNode);
			visitChildren(node);
			checkedPop(groupNode, endOffset);
			groupNode.setEnd(createTextNode(document, endOffset, endOffset + 1));
		}

		/**
		 * Common push and return of a FormatterJSDefaultLineNode (a line that terminates with a semicolon).
		 * 
		 * @param node
		 * @return
		 */
		private FormatterJSDefaultLineNode pushLineNode(JSNode node)
		{
			FormatterJSDefaultLineNode lineNode = new FormatterJSDefaultLineNode(document);
			lineNode.setBegin(createTextNode(document, node.getStartingOffset(), node.getStartingOffset()));
			push(lineNode);
			return lineNode;
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
				end = locateColonOrSemicolonInLine(end, document);
			}
			bodyNode.setEnd(createTextNode(document, block.getEndingOffset(), end));
		}

		/**
		 * A common visit of a block.<br>
		 * In case it's a block that has brackets, we make sure that no new-lines exist in the block beginning or the
		 * previous code ending. This will allow us to move that block one line up in case the user decides he/she want
		 * to see the blocks at the same line.
		 * 
		 * @param node
		 */
		private void visitCommonBlock(JSNode node, int declarationEndOffset, IParseNode body)
		{
			boolean hasBody = (body.getNodeType() != JSNodeTypes.EMPTY);
			// First, push the declaration part
			// In some cases, the body is empty (like in a 'while' with no body). Those cases get a special treatment.
			FormatterJSDeclarationNode declarationNode = new FormatterJSDeclarationNode(document,
					(body.getNodeType() == JSNodeTypes.STATEMENTS), node);
			int endDeclarationOffset;
			if (hasBody)
			{
				endDeclarationOffset = declarationEndOffset + 1;
			}
			else
			{
				endDeclarationOffset = body.getEndingOffset() + 1;
			}
			declarationNode.setBegin(createTextNode(document, node.getStartingOffset(), endDeclarationOffset));
			push(declarationNode);
			checkedPop(declarationNode, -1);

			if (hasBody)
			{
				// Then, push the body (the body might be defined without any brackets, so in those cases the begin and
				// end would be empty)
				FormatterJSBlockNode blockNode = new FormatterJSBlockNode(document);
				boolean bodyInBrackets = (body.getNodeType() == JSNodeTypes.STATEMENTS);
				if (bodyInBrackets)
				{
					blockNode
							.setBegin(createTextNode(document, body.getStartingOffset(), body.getStartingOffset() + 1));
					push(blockNode);
					visitChildren((JSNode) body);
					checkedPop(blockNode, body.getEndingOffset());
					int end = locateColonOrSemicolonInLine(blockNode.getEndOffset() + 1, document);
					blockNode.setEnd(createTextNode(document, body.getEndingOffset(), end));
				}
				else
				{
					// Create an empty block and visit its children
					blockNode.setBegin(createTextNode(document, body.getStartingOffset(), body.getStartingOffset()));
					push(blockNode);
					visitChildren((JSNode) body);
					int blockEnd = locateColonOrSemicolonInLine(body.getEndingOffset(), document);
					checkedPop(blockNode, blockEnd);
					blockNode.setEnd(createTextNode(document, blockEnd, blockEnd));
				}
			}
		}

		/**
		 * Scan for a colon or a semicolon terminator located at the same line. Return the given offset if non is found.
		 * 
		 * @param offset
		 * @param document
		 * @return The semicolon offset; The given offset if a semicolon not found.
		 */
		private int locateColonOrSemicolonInLine(int offset, FormatterDocument document)
		{
			int i = offset;
			int size = document.getLength();
			for (; i < size; i++)
			{
				char c = document.charAt(i);
				if (c == ';' || c == ',')
				{
					return i + 1;
				}
				if (c == '\n' || c == '\r')
				{
					break;
				}
				if (c != ' ' || c != '\t')
				{
					break;
				}
			}
			return offset;
		}
	}
}
