/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import beaver.Symbol;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.js.formatter.nodes.FormatterJSBlockNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSCaseBodyNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSCaseNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSDeclarationNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSDefaultLineNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSElseIfNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSElseNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSExpressionWrapperNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSFunctionBodyNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSFunctionInvocationNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSGetElementNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSGetPropertyNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSGroupNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSIdentifierNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSIfNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSImplicitBlockNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSNameValuePairNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSNonBlockedWhileNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSObjectNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSOperatorNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSParenthesesNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSPunctuationNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSRootNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSSwitchNode;
import com.aptana.editor.js.formatter.nodes.FormatterJSTextNode;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.FormatterUtils;
import com.aptana.formatter.IDebugScopes;
import com.aptana.formatter.nodes.AbstractFormatterNodeBuilder;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.nodes.NodeTypes.TypeBracket;
import com.aptana.formatter.nodes.NodeTypes.TypeOperator;
import com.aptana.formatter.nodes.NodeTypes.TypePunctuation;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSArgumentsNode;
import com.aptana.js.core.parsing.ast.JSArrayNode;
import com.aptana.js.core.parsing.ast.JSAssignmentNode;
import com.aptana.js.core.parsing.ast.JSBinaryArithmeticOperatorNode;
import com.aptana.js.core.parsing.ast.JSBinaryBooleanOperatorNode;
import com.aptana.js.core.parsing.ast.JSBreakNode;
import com.aptana.js.core.parsing.ast.JSCaseNode;
import com.aptana.js.core.parsing.ast.JSCatchNode;
import com.aptana.js.core.parsing.ast.JSCommaNode;
import com.aptana.js.core.parsing.ast.JSConditionalNode;
import com.aptana.js.core.parsing.ast.JSConstructNode;
import com.aptana.js.core.parsing.ast.JSContinueNode;
import com.aptana.js.core.parsing.ast.JSDeclarationNode;
import com.aptana.js.core.parsing.ast.JSDefaultNode;
import com.aptana.js.core.parsing.ast.JSDoNode;
import com.aptana.js.core.parsing.ast.JSElementsNode;
import com.aptana.js.core.parsing.ast.JSEmptyNode;
import com.aptana.js.core.parsing.ast.JSErrorNode;
import com.aptana.js.core.parsing.ast.JSFalseNode;
import com.aptana.js.core.parsing.ast.JSFinallyNode;
import com.aptana.js.core.parsing.ast.JSForInNode;
import com.aptana.js.core.parsing.ast.JSForNode;
import com.aptana.js.core.parsing.ast.JSFunctionNode;
import com.aptana.js.core.parsing.ast.JSGetElementNode;
import com.aptana.js.core.parsing.ast.JSGetPropertyNode;
import com.aptana.js.core.parsing.ast.JSGroupNode;
import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSIfNode;
import com.aptana.js.core.parsing.ast.JSInvokeNode;
import com.aptana.js.core.parsing.ast.JSNameValuePairNode;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.core.parsing.ast.JSNullNode;
import com.aptana.js.core.parsing.ast.JSNumberNode;
import com.aptana.js.core.parsing.ast.JSObjectNode;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.js.core.parsing.ast.JSPostUnaryOperatorNode;
import com.aptana.js.core.parsing.ast.JSPreUnaryOperatorNode;
import com.aptana.js.core.parsing.ast.JSRegexNode;
import com.aptana.js.core.parsing.ast.JSRestElementNode;
import com.aptana.js.core.parsing.ast.JSReturnNode;
import com.aptana.js.core.parsing.ast.JSSpreadElementNode;
import com.aptana.js.core.parsing.ast.JSStatementsNode;
import com.aptana.js.core.parsing.ast.JSStringNode;
import com.aptana.js.core.parsing.ast.JSSwitchNode;
import com.aptana.js.core.parsing.ast.JSThisNode;
import com.aptana.js.core.parsing.ast.JSThrowNode;
import com.aptana.js.core.parsing.ast.JSTreeWalker;
import com.aptana.js.core.parsing.ast.JSTrueNode;
import com.aptana.js.core.parsing.ast.JSTryNode;
import com.aptana.js.core.parsing.ast.JSVarNode;
import com.aptana.js.core.parsing.ast.JSWhileNode;
import com.aptana.js.core.parsing.ast.JSWithNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

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
	private boolean hasErrors;
	private Set<Integer> singleLinecommentEndOffsets;
	private Set<Integer> multiLinecommentEndOffsets;

	/**
	 * @param parseResult
	 * @param document
	 * @return
	 */
	public IFormatterContainerNode build(IParseNode parseResult, FormatterDocument document)
	{
		this.document = document;
		final IFormatterContainerNode rootNode = new FormatterJSRootNode(document);
		start(rootNode);
		JSParseRootNode jsRootNode = (JSParseRootNode) parseResult;
		generateCommentEndOffsets(jsRootNode.getCommentNodes());
		jsRootNode.accept(new JSFormatterTreeWalker());
		checkedPop(rootNode, document.getLength());
		return rootNode;
	}

	/**
	 * Returns true in case the node building stumble into a JS error node.
	 * 
	 * @return True if there are error nodes in the AST.
	 */
	public boolean hasErrors()
	{
		return hasErrors;
	}

	private void generateCommentEndOffsets(IParseNode[] comments)
	{
		singleLinecommentEndOffsets = new HashSet<Integer>();
		multiLinecommentEndOffsets = new HashSet<Integer>();
		if (comments == null)
		{
			return;
		}
		boolean onOffEnabled = document.getBoolean(JSFormatterConstants.FORMATTER_OFF_ON_ENABLED);
		LinkedHashMap<Integer, String> commentsMap = onOffEnabled ? new LinkedHashMap<Integer, String>(comments.length)
				: null;
		for (IParseNode comment : comments)
		{
			short commentType = comment.getNodeType();
			int end = comment.getEndingOffset();
			if (commentType == IJSNodeTypes.SINGLE_LINE_COMMENT)
			{
				singleLinecommentEndOffsets.add(getNextNonWhiteCharOffset(document, end + 1));
			}
			else if (commentType == IJSNodeTypes.MULTI_LINE_COMMENT || commentType == IJSNodeTypes.SDOC_COMMENT
					|| commentType == IJSNodeTypes.VSDOC_COMMENT)
			{
				multiLinecommentEndOffsets.add(getNextNonWhiteCharOffset(document, end + 1));
			}
			// Add to the map of comments when the On-Off is enabled.
			if (onOffEnabled)
			{
				int start = comment.getStartingOffset();
				// The end offset of a JS multi-line comment should be increased by 1 in order to include the last
				// closing char. However, for the OFF/ON support it does'nt really matter, since the regex will never
				// match that ending char, so we can simply use the 'end' offset as is.
				String commentStr = document.get(start, end + 1);
				commentsMap.put(start, commentStr);
			}
		}
		// Generate the On-Off regions
		if (onOffEnabled && !commentsMap.isEmpty())
		{
			Pattern onPattern = Pattern.compile(Pattern.quote(document.getString(JSFormatterConstants.FORMATTER_ON)));
			Pattern offPattern = Pattern.compile(Pattern.quote(document.getString(JSFormatterConstants.FORMATTER_OFF)));
			setOffOnRegions(FormatterUtils.resolveOnOffRegions(commentsMap, onPattern, offPattern,
					document.getLength() - 1));
		}
	}

	/**
	 * Returns true if there is a single-line comment right before the given element.<br>
	 * There should be only whitespaces between the given offset and the comment.
	 * 
	 * @param offset
	 * @return True, if the given offset is right after a comment.
	 */
	private boolean hasSingleCommentBefore(int offset)
	{
		return singleLinecommentEndOffsets.contains(offset);
	}

	/**
	 * Returns true if there is a multi-line comment right before the given element.<br>
	 * There should be only whitespaces between the given offset and the comment.
	 * 
	 * @param offset
	 * @return True, if the given offset is right after a comment.
	 */
	private boolean hasMultiLineCommentBefore(int offset)
	{
		return multiLinecommentEndOffsets.contains(offset);
	}

	private boolean hasAnyCommentBefore(int offset)
	{
		return hasSingleCommentBefore(offset) || hasMultiLineCommentBefore(offset);
	}

	/**
	 * Build the formatter nodes by walking the JavaScript AST.
	 */
	private class JSFormatterTreeWalker extends JSTreeWalker
	{
	
		// FIXME Handle formatting of:
		// - JSRestElementNode
		// - JSSpreadElementNode
		// - JSExportNode
		// - JSImportNode

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFunctionNode)
		 */
		@Override
		public void visit(JSFunctionNode node)
		{
			// First, push the 'function' declaration part
			int startingOffset = node.getStartingOffset();
			int declarationEnd = startingOffset + 8;
			FormatterJSDeclarationNode declarationNode = new FormatterJSDeclarationNode(document, true, node,
					hasAnyCommentBefore(startingOffset));
			declarationNode.setBegin(createTextNode(document, startingOffset, declarationEnd));
			push(declarationNode);
			checkedPop(declarationNode, -1);

			// Push the function name, if exists
			JSNode functionName = (JSNode) node.getName();
			if (functionName != null && functionName.getNodeType() != IJSNodeTypes.EMPTY)
			{
				// push the function name
				visitTextNode(functionName, true, 1);
				declarationEnd = functionName.getEnd() + 1;
			}

			// Push any parameters
			JSNode body = (JSNode) node.getBody();
			IParseNode functionParameters = node.getParameters();
			List<JSNode> parameters = asJSNodesList(functionParameters.getChildren());
			pushParametersInParentheses(functionParameters.getStartingOffset(), functionParameters.getEndingOffset(),
					parameters, TypePunctuation.COMMA, false, TypeBracket.DECLARATION_PARENTHESIS);

			// Push the function body
			FormatterJSFunctionBodyNode bodyNode = new FormatterJSFunctionBodyNode(document,
					FormatterJSDeclarationNode.isPartOfExpression(node.getParent()),
					hasAnyCommentBefore(body.getStartingOffset()));
			bodyNode.setBegin(createTextNode(document, body.getStartingOffset(), body.getStartingOffset() + 1));
			push(bodyNode);
			visitChildren(body);
			checkedPop(bodyNode, body.getEndingOffset());
			int end = body.getEndingOffset() + 1;
			bodyNode.setEnd(createTextNode(document, body.getEndingOffset(), end));
			if (node.getSemicolonIncluded())
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEnd(), false, true);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSErrorNode)
		 */
		@Override
		public void visit(JSErrorNode node)
		{
			// Stop the formatting
			JSFormatterNodeBuilder.this.hasErrors = true;
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFalseNode)
		 */
		@Override
		public void visit(JSFalseNode node)
		{
			visitTextNode(node, true, 0);
			if (node.getSemicolonIncluded())
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEnd(), false, true);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSTrueNode)
		 */
		@Override
		public void visit(JSTrueNode node)
		{
			visitTextNode(node, true, 0);
			if (node.getSemicolonIncluded())
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEnd(), false, true);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSEmptyNode)
		 */
		@Override
		public void visit(JSEmptyNode node)
		{
			if (node.getLength() > 0 && node.getChildCount() == 0)
			{
				visitTextNode(node.getStartingOffset(), node.getEndingOffset() + 1, false, 0);
			}
			super.visit(node);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSPostUnaryOperatorNode)
		 */
		@Override
		public void visit(JSPostUnaryOperatorNode node)
		{
			JSNode expression = (JSNode) node.getExpression();
			TypeOperator op = TypeOperator.getTypeOperator(node.getOperator().value.toString());
			expression.accept(this);
			pushTypeOperator(op, node.getOperator().getStart(), true);
			if (node.getSemicolonIncluded())
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEndingOffset(), false, true);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSPreUnaryOperatorNode)
		 */
		@Override
		public void visit(JSPreUnaryOperatorNode node)
		{
			JSNode expression = (JSNode) node.getExpression();
			TypeOperator op = TypeOperator.getTypeOperator(node.getOperator().value.toString());
			pushTypeOperator(op, node.getOperator().getStart(), true);
			expression.accept(this);
			if (node.getSemicolonIncluded())
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEndingOffset(), false, true);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSConditionalNode)
		 */
		@Override
		public void visit(JSConditionalNode node)
		{
			// We need to wrap this conditional node with a wrapper that will handle new-lines before the nore.
			int nodeStart = node.getStartingOffset();
			FormatterJSExpressionWrapperNode conditionalWrapperNode = new FormatterJSExpressionWrapperNode(document,
					node, hasSingleCommentBefore(nodeStart));
			conditionalWrapperNode.setBegin(createTextNode(document, nodeStart, nodeStart));
			push(conditionalWrapperNode);

			JSNode condition = (JSNode) node.getTestExpression();
			condition.accept(this);

			// push the conditional operator
			pushTypeOperator(TypeOperator.CONDITIONAL, node.getQuestionMark().getStart(), false);
			// visit the true part
			JSNode trueExpression = (JSNode) node.getTrueExpression();
			trueExpression.accept(this);
			// push the colon separator
			pushTypeOperator(TypeOperator.CONDITIONAL_COLON, node.getColon().getStart(), false);
			// visit the false part
			JSNode falseExpression = (JSNode) node.getFalseExpression();
			falseExpression.accept(this);
			// push any semicolon
			if (node.getSemicolonIncluded())
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEndingOffset(), false, true);
			}

			checkedPop(conditionalWrapperNode, -1);
			IFormatterContainerNode topNode = peek();
			int endingOffset = topNode.getEndOffset();
			conditionalWrapperNode.setEnd(createTextNode(document, endingOffset, endingOffset));
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
			boolean isEmptyFalseBlock = (falseBlock.getNodeType() == IJSNodeTypes.EMPTY);
			boolean isCurlyTrueBlock = (trueBlock.getNodeType() == IJSNodeTypes.STATEMENTS);
			boolean isCurlyFalseBlock = (!isEmptyFalseBlock && falseBlock.getNodeType() == IJSNodeTypes.STATEMENTS);
			// First, construct the if condition node
			FormatterJSIfNode ifNode = new FormatterJSIfNode(document, isCurlyTrueBlock, node,
					hasAnyCommentBefore(node.getStartingOffset()));
			int ifStart = node.getStartingOffset();
			ifNode.setBegin(createTextNode(document, ifStart, ifStart + 2));
			push(ifNode);
			// push the 'if' condition
			pushNodeInParentheses('(', ')', node.getLeftParenthesis().getStart(),
					node.getRightParenthesis().getEnd() + 1, (JSNode) node.getCondition(), false,
					TypeBracket.CONDITIONAL_PARENTHESIS);
			// Construct the 'true' part of the 'if' and visit its children
			if (isCurlyTrueBlock)
			{
				pushBlockNode(trueBlock, false);
			}
			else
			{
				// Wrap with an empty block node and visit the children
				wrapInImplicitBlock(trueBlock, false);
			}
			checkedPop(ifNode, trueBlock.getEndingOffset());
			if (!isEmptyFalseBlock)
			{
				// Construct the 'false' part if exist.
				// Note that the JS parser does not provide us with the start offset of the 'else' keyword, so we need
				// to locate it in between the end of the 'true' block and the begin of the 'false' block.
				int trueBlockEnd = trueBlock.getEndingOffset();
				int falseBlockStart = falseBlock.getStartingOffset();
				String segment = document.get(trueBlockEnd + 1, falseBlockStart);
				int elsePos = segment.toLowerCase().indexOf("else"); //$NON-NLS-1$
				int elseBlockStart = elsePos + trueBlockEnd + 1;
				int elseBlockDeclarationEnd = elseBlockStart + 4; // +4 for the keyword 'else'
				boolean isElseIf = (falseBlock.getNodeType() == IJSNodeTypes.IF);
				FormatterJSElseNode elseNode = new FormatterJSElseNode(document, isCurlyFalseBlock, isElseIf,
						isCurlyTrueBlock, hasAnyCommentBefore(elseBlockStart));
				elseNode.setBegin(createTextNode(document, elseBlockStart, elseBlockDeclarationEnd));
				push(elseNode);
				if (isCurlyFalseBlock)
				{
					pushBlockNode(falseBlock, false);
				}
				else
				{
					if (isElseIf)
					{
						// Wrap the incoming 'if' with an Else-If node that will allow us later to break it and indent
						// it.
						FormatterJSElseIfNode elseIfNode = new FormatterJSElseIfNode(document,
								hasAnyCommentBefore(falseBlockStart));
						elseIfNode.setBegin(createTextNode(document, falseBlockStart, falseBlockStart));
						push(elseIfNode);
						falseBlock.accept(this);
						int falseBlockEnd = falseBlock.getEndingOffset() + 1;
						checkedPop(elseIfNode, falseBlockEnd);
						elseIfNode.setEnd(createTextNode(document, falseBlockEnd, falseBlockEnd));
					}
					else
					{
						// Wrap with an empty block node and visit the children
						wrapInImplicitBlock(falseBlock, false);
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
			int startingOffset = node.getStartingOffset();
			FormatterJSDeclarationNode declarationNode = new FormatterJSDeclarationNode(document, true, node,
					hasAnyCommentBefore(startingOffset));
			declarationNode.setBegin(createTextNode(document, startingOffset, startingOffset + 2));
			push(declarationNode);
			checkedPop(declarationNode, -1);

			// Push the special do-while block node
			JSNode body = (JSNode) node.getBody();
			int blockEnd;
			boolean bodyInBrackets = (body.getNodeType() == IJSNodeTypes.STATEMENTS);
			FormatterJSBlockNode doWhileBlock = new FormatterJSBlockNode(document,
					hasAnyCommentBefore(body.getStartingOffset()));
			if (bodyInBrackets)
			{
				doWhileBlock.setBegin(createTextNode(document, body.getStartingOffset(), body.getStartingOffset() + 1));
				push(doWhileBlock);
				// visit the body's children
				visitChildren(body);
				blockEnd = body.getEndingOffset();
				checkedPop(doWhileBlock, blockEnd);
				doWhileBlock.setEnd(createTextNode(document, blockEnd, blockEnd + 1));
			}
			else
			{

				doWhileBlock.setBegin(createTextNode(document, body.getStartingOffset(), body.getStartingOffset()));
				push(doWhileBlock);
				// visit the body only
				body.accept(this);
				blockEnd = body.getEndingOffset() + 1;
				checkedPop(doWhileBlock, blockEnd);
				doWhileBlock.setEnd(createTextNode(document, blockEnd, blockEnd));
				// have to adjust the block end (see TISTUD-1572)
				blockEnd--;
			}

			// now deal with the 'while' condition part. we need to include the word 'while' that appears
			// somewhere between the block-end and the condition start.
			// We wrap this node as a begin-end node that will hold the condition internals as children
			FormatterJSNonBlockedWhileNode whileNode = new FormatterJSNonBlockedWhileNode(document);
			// Search for the exact 'while' start offset
			int whileBeginOffset = locateCharacterSkippingWhitespaces(document, blockEnd + 1, 'w', true);
			int conditionEnd = locateColonOrSemicolonInLine(node.getEndingOffset() + 1, document);
			whileNode.setBegin(createTextNode(document, whileBeginOffset, conditionEnd));
			push(whileNode);
			checkedPop(whileNode, -1);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSArrayNode)
		 */
		@Override
		public void visit(JSArrayNode node)
		{
			pushNodeInParentheses('[', ']', node.getStartingOffset(), node.getEndingOffset(), node, true,
					TypeBracket.ARRAY_SQUARE);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSElementsNode)
		 */
		@Override
		public void visit(JSElementsNode node)
		{
			IParseNode[] children = node.getChildren();
			List<JSNode> jsChildren = new ArrayList<JSNode>(children.length);
			for (IParseNode child : children)
			{
				jsChildren.add((JSNode) child);
			}
			visitNodeLists(jsChildren, null, null, TypePunctuation.COMMA);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSWhileNode)
		 */
		@Override
		public void visit(JSWhileNode node)
		{
			JSNode condition = (JSNode) node.getCondition();
			JSNode body = (JSNode) node.getBody();
			// visit the 'while' keyword
			int declarationEndOffset = node.getStartingOffset() + 4;
			pushCommonDeclaration(node, declarationEndOffset, null);
			// visit the elements in the parentheses
			int openParen = locateCharForward(document, '(', node.getLeftParenthesis().getStart());
			int closeParen = locateCharBackward(document, ')', node.getRightParenthesis().getStart());
			FormatterJSParenthesesNode parenthesesNode = new FormatterJSParenthesesNode(document,
					TypeBracket.LOOP_PARENTHESIS);
			parenthesesNode.setBegin(createTextNode(document, openParen, openParen + 1));
			push(parenthesesNode);
			// visit the 'while' condition
			condition.accept(this);
			// close the parentheses node.
			checkedPop(parenthesesNode, -1);
			parenthesesNode.setEnd(createTextNode(document, closeParen, closeParen + 1));
			// in case we have a 'body', visit it.
			body.accept(this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSForNode)
		 */
		@Override
		public void visit(JSForNode node)
		{
			List<JSNode> initializers = Arrays.asList((JSNode) node.getInitializer());
			List<JSNode> conditions = Arrays.asList((JSNode) node.getCondition());
			List<JSNode> updaters = Arrays.asList((JSNode) node.getAdvance());
			JSNode body = (JSNode) node.getBody();
			// visit the 'for' keyword
			int declarationEndOffset = node.getStartingOffset() + 2;
			pushCommonDeclaration(node, declarationEndOffset, null);
			// visit the elements in the parentheses
			int openParen = locateCharForward(document, '(', node.getLeftParenthesis().getStart());
			int closeParen = locateCharBackward(document, ')', node.getRightParenthesis().getStart());
			FormatterJSParenthesesNode parenthesesNode = new FormatterJSParenthesesNode(document,
					TypeBracket.LOOP_PARENTHESIS);
			parenthesesNode.setBegin(createTextNode(document, openParen, openParen + 1));
			push(parenthesesNode);
			// visit the initializers, the conditions and the updaters.
			// between them, push the semicolons
			visitNodeLists(initializers, null, null, TypePunctuation.COMMA);
			int semicolonOffset = locateCharForward(document, ';', declarationEndOffset);
			pushTypePunctuation(TypePunctuation.FOR_SEMICOLON, semicolonOffset);
			visitNodeLists(conditions, null, null, TypePunctuation.COMMA);
			semicolonOffset = locateCharForward(document, ';', semicolonOffset + 1);
			pushTypePunctuation(TypePunctuation.FOR_SEMICOLON, semicolonOffset);
			visitNodeLists(updaters, null, null, TypePunctuation.COMMA);
			// close the parentheses node.
			checkedPop(parenthesesNode, -1);
			parenthesesNode.setEnd(createTextNode(document, closeParen, closeParen + 1));
			// in case we have a 'body', visit it.
			if (body.getNodeType() == IJSNodeTypes.STATEMENTS || body.getNodeType() == IJSNodeTypes.EMPTY)
			{
				body.accept(this);
			}
			else
			{
				wrapInImplicitBlock(body, true);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSForInNode)
		 */
		@Override
		public void visit(JSForInNode node)
		{
			JSNode expression = (JSNode) node.getExpression();
			JSNode initializer = (JSNode) node.getInitializer();
			JSNode body = (JSNode) node.getBody();
			// visit the 'for' keyword
			int declarationEndOffset = node.getStartingOffset() + 2;
			pushCommonDeclaration(node, declarationEndOffset, null);
			// visit the elements in the parentheses
			int openParen = locateCharForward(document, '(', node.getLeftParenthesis().getStart());
			int closeParen = locateCharBackward(document, ')', node.getRightParenthesis().getStart());
			FormatterJSParenthesesNode parenthesesNode = new FormatterJSParenthesesNode(document,
					TypeBracket.LOOP_PARENTHESIS);
			parenthesesNode.setBegin(createTextNode(document, openParen, openParen + 1));
			push(parenthesesNode);
			// push the expression
			initializer.accept(this);
			// add the 'in' node (it's between the initializer and the expression)
			int inStart = node.getIn().getStart();
			visitTextNode(inStart, inStart + 2, true, 1, 1, false);
			// push the expression.
			expression.accept(this);
			// close the parentheses node.
			checkedPop(parenthesesNode, closeParen);
			parenthesesNode.setEnd(createTextNode(document, closeParen, closeParen + 1));
			// in case we have a 'body', visit it.
			body.accept(this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSWithNode)
		 */
		@Override
		public void visit(JSWithNode node)
		{
			JSNode expression = (JSNode) node.getExpression();
			JSNode body = (JSNode) node.getBody();
			// visit the 'with' keyword
			int declarationEndOffset = node.getStartingOffset() + 3;
			pushCommonDeclaration(node, declarationEndOffset, null);
			// visit the elements in the parentheses
			int openParen = locateCharForward(document, '(', node.getLeftParenthesis().getStart());
			int closeParen = locateCharBackward(document, ')', node.getRightParenthesis().getStart());
			FormatterJSParenthesesNode parenthesesNode = new FormatterJSParenthesesNode(document,
					TypeBracket.LOOP_PARENTHESIS);
			parenthesesNode.setBegin(createTextNode(document, openParen, openParen + 1));
			push(parenthesesNode);
			// push the expression
			expression.accept(this);
			// close the parentheses node.
			checkedPop(parenthesesNode, closeParen);
			parenthesesNode.setEnd(createTextNode(document, closeParen, closeParen + 1));
			// in case we have a 'body', visit it.
			body.accept(this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSStatementsNode)
		 */
		@Override
		public void visit(JSStatementsNode node)
		{
			// Statements arrive as curly-blocks structures
			int startingOffset = node.getStartingOffset();
			FormatterJSBlockNode blockNode = new FormatterJSBlockNode(document, hasAnyCommentBefore(startingOffset));
			blockNode.setBegin(createTextNode(document, startingOffset, startingOffset + 1));
			push(blockNode);
			visitChildren(node);
			int endingOffset = node.getEndingOffset();
			checkedPop(blockNode, endingOffset);
			blockNode.setEnd(createTextNode(document, endingOffset, endingOffset + 1));
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSObjectNode)
		 */
		@Override
		public void visit(JSObjectNode node)
		{
			FormatterJSObjectNode objectNode = new FormatterJSObjectNode(document, node, hasAnyCommentBefore(node
					.getLeftBrace().getStart()));
			objectNode.setBegin(createTextNode(document, node.getStartingOffset(), node.getStartingOffset() + 1));
			push(objectNode);
			visitNodeLists(asJSNodesList(node.getChildren()), null, null, TypePunctuation.COMMA);
			int end = node.getEndingOffset();
			checkedPop(objectNode, end);
			objectNode.setEnd(createTextNode(document, end, end + 1));
			if (node.getSemicolonIncluded())
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEndingOffset(), false, true);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSNameValuePairNode)
		 */
		@Override
		public void visit(JSNameValuePairNode node)
		{
			Symbol colon = node.getColon();
			if (colon != null)
			{
				IParseNode name = node.getName();
				// Create a formatter name-value node, but note that this node only holds the 'name' part for the
				// formatting. The rest is handled in the same line.
				FormatterJSNameValuePairNode nameValuePairNode = new FormatterJSNameValuePairNode(document, node,
						hasAnyCommentBefore(node.getStartingOffset()));
				nameValuePairNode.setBegin(createTextNode(document, name.getStartingOffset(),
						name.getEndingOffset() + 1));
				push(nameValuePairNode);
				checkedPop(nameValuePairNode, -1);

				pushTypeOperator(TypeOperator.KEY_VALUE_COLON, colon.getStart(), false);

				// Push the 'value'
				JSNode value = (JSNode) node.getValue();
				value.accept(this);
			}
			else
			{
				// get or set
				// First, push the 'get' or 'set' declaration part
				int startingOffset = node.getStartingOffset();
				int declarationEnd = startingOffset + 3;
				FormatterJSDeclarationNode declarationNode = new FormatterJSDeclarationNode(document, true, node,
						hasAnyCommentBefore(startingOffset));
				declarationNode.setBegin(createTextNode(document, startingOffset, declarationEnd));
				push(declarationNode);
				checkedPop(declarationNode, -1);

				// Push the property name
				JSNode functionName = (JSNode) node.getName();
				visitTextNode(functionName, true, 1);
				declarationEnd = functionName.getEnd() + 1;

				// Push the function body
				JSNode body = (JSNode) node.getValue();
				FormatterJSFunctionBodyNode bodyNode = new FormatterJSFunctionBodyNode(document,
						FormatterJSDeclarationNode.isPartOfExpression(node.getParent()),
						hasAnyCommentBefore(body.getStartingOffset()));
				bodyNode.setBegin(createTextNode(document, body.getStartingOffset(), body.getStartingOffset() + 1));
				push(bodyNode);
				visitChildren(body);
				checkedPop(bodyNode, body.getEndingOffset());
				int end = body.getEndingOffset() + 1;
				bodyNode.setEnd(createTextNode(document, body.getEndingOffset(), end));
				if (node.getSemicolonIncluded())
				{
					findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEnd(), false, true);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSSwitchNode)
		 */
		@Override
		public void visit(JSSwitchNode node)
		{
			// Push the switch-case declaration node
			int startingOffset = node.getStartingOffset();
			FormatterJSDeclarationNode switchNode = new FormatterJSDeclarationNode(document, true, node,
					hasAnyCommentBefore(startingOffset));
			switchNode.setBegin(createTextNode(document, startingOffset, node.getRightParenthesis().getEnd() + 1));
			push(switchNode);
			checkedPop(switchNode, -1);

			// push a switch-case body node
			int blockStart = node.getLeftBrace().getStart();
			FormatterJSSwitchNode blockNode = new FormatterJSSwitchNode(document, hasAnyCommentBefore(blockStart));
			blockNode.setBegin(createTextNode(document, blockStart, blockStart + 1));
			push(blockNode);
			// visit the children under that block node. We have to skip the first child, which is the switch-condition.
			// Otherwise, we may have errors due to the fact that the switch-begin already encapsulate it.
			// Note - This should be changed in the future once we have space-formatting within the switch condition.
			visitChildren(node, 1);
			int endingOffset = node.getEndingOffset();
			// pop the block node
			checkedPop(blockNode, endingOffset);
			int end = endingOffset + 1;
			if (node.getSemicolonIncluded())
			{
				end = locateColonOrSemicolonInLine(end, document);
			}
			blockNode.setEnd(createTextNode(document, endingOffset, end));
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSCaseNode)
		 */
		@Override
		public void visit(JSCaseNode node)
		{
			visitCaseOrDefaultNode(node, node.getColon().getStart());
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSDefaultNode)
		 */
		@Override
		public void visit(JSDefaultNode node)
		{
			visitCaseOrDefaultNode(node, node.getColon().getStart());
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
			if (catchBlock.getNodeType() == IJSNodeTypes.CATCH)
			{
				visit((JSCatchNode) catchBlock);
			}
			IParseNode finallyBlock = node.getFinallyBlock();
			if (finallyBlock.getNodeType() == IJSNodeTypes.FINALLY)
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
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSVarNode)
		 */
		@Override
		public void visit(JSVarNode node)
		{
			// Visit the 'var' part
			int startingOffset = node.getStartingOffset();
			FormatterJSDeclarationNode var = new FormatterJSDeclarationNode(document, false, node,
					hasAnyCommentBefore(startingOffset));
			var.setBegin(createTextNode(document, startingOffset, startingOffset + 3));
			push(var);
			checkedPop(var, -1);
			// Visit the right-part of the 'var' expression.
			if (node.getChildCount() > 1)
			{
				// we have a list of variables separated with commas (var a, b, c=3, d;)
				List<JSNode> jsChildren = asJSNodesList(node.getChildren());
				visitNodeLists(jsChildren, null, null, TypePunctuation.COMMA);
			}
			else
			{
				visitChildren(node);
			}
			if (node.getSemicolonIncluded())
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEnd(), false, true);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSDeclarationNode)
		 */
		@Override
		public void visit(JSDeclarationNode node)
		{
			if (node.getEqualSign() != null)
			{
				visitLeftRightExpression(node, (JSNode) node.getIdentifier(), (JSNode) node.getValue(),
						node.getEqualSign().value.toString());
			}
			else
			{
				pushCommonDeclaration(node, node.getEndingOffset(), null);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSAssignmentNode)
		 */
		@Override
		public void visit(JSAssignmentNode node)
		{
			visitLeftRightExpression(node, (JSNode) node.getLeftHandSide(), (JSNode) node.getRightHandSide(),
					node.getOperator().value.toString());
			if (node.getSemicolonIncluded())
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEnd(), false, true);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSIdentifierNode)
		 */
		@Override
		public void visit(JSIdentifierNode node)
		{
			int startingOffset = node.getStartingOffset();
			FormatterJSIdentifierNode identifierNode = new FormatterJSIdentifierNode(document, node,
					hasSingleCommentBefore(startingOffset));
			identifierNode.setBegin(createTextNode(document, startingOffset, node.getEndingOffset() + 1));
			push(identifierNode);
			checkedPop(identifierNode, -1);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSConstructNode)
		 */
		public void visit(JSConstructNode node)
		{
			IParseNode expression = node.getExpression();
			IParseNode argumentsNode = node.getArguments();
			List<JSNode> arguments = asJSNodesList(argumentsNode.getChildren());
			// push the 'new' keyword.
			int start = node.getStartingOffset();
			visitTextNode(start, start + 3, !hasAnyCommentBefore(start), 0, 0, node.getSemicolonIncluded());
			// push the expression
			visitTextNode(expression, true, 1);
			int argumentsStartOffset = argumentsNode.getStartingOffset();
			if (!arguments.isEmpty()
					|| (document.getLength() > argumentsStartOffset && document.charAt(argumentsStartOffset) == '('))
			{
				pushParametersInParentheses(argumentsNode.getStartingOffset(), argumentsNode.getEndingOffset(),
						arguments, TypePunctuation.COMMA, false, TypeBracket.DECLARATION_PARENTHESIS);
			}
			if (node.getSemicolonIncluded())
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEndingOffset(), false, true);
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
			visitLeftRightExpression(node, (JSNode) node.getLeftHandSide(), (JSNode) node.getRightHandSide(),
					node.getOperator().value.toString());
			if (node.getSemicolonIncluded())
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEndingOffset(), false, true);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSArgumentsNode)
		 */
		@Override
		public void visit(JSArgumentsNode node)
		{
			pushParametersInParentheses(node.getStartingOffset(), node.getEndingOffset() + 1,
					asJSNodesList(node.getChildren()), TypePunctuation.COMMA, false,
					TypeBracket.DECLARATION_PARENTHESIS);
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
			// Wrap the node with a generic expression node to handle new-lines.
			FormatterJSExpressionWrapperNode wrapperNode = new FormatterJSExpressionWrapperNode(document, node,
					hasSingleCommentBefore(node.getStartingOffset()));
			int startingOffset = node.getStartingOffset();
			wrapperNode.setBegin(createTextNode(document, startingOffset, node.getStartingOffset()));
			push(wrapperNode);
			visitLeftRightExpression(node, (JSNode) node.getLeftHandSide(), (JSNode) node.getRightHandSide(),
					node.getOperator().value.toString());
			if (node.getSemicolonIncluded())
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEndingOffset(), false, true);
			}
			checkedPop(wrapperNode, -1);
			IFormatterContainerNode topNode = peek();
			int endingOffset = topNode.getEndOffset();
			wrapperNode.setEnd(createTextNode(document, endingOffset, endingOffset));
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
				int startingOffset = node.getStartingOffset();
				visitTextNode(startingOffset, startingOffset + 8, true, 0, 0, false);
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEndingOffset(), false, true);
				checkedPop(lineNode, lineNode.getEndOffset());
			}
			else
			{
				super.visit(node);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSCommaNode)
		 */
		@Override
		public void visit(JSCommaNode node)
		{
			// a = s, d, f;
			// Wrap this node with a generic expression node to manage any new-lines before the comma.
			FormatterJSExpressionWrapperNode wrapperNode = new FormatterJSExpressionWrapperNode(document, node,
					hasSingleCommentBefore(node.getStartingOffset()));
			int startingOffset = node.getStartingOffset();
			wrapperNode.setBegin(createTextNode(document, startingOffset, node.getStartingOffset()));
			push(wrapperNode);
			visitNodeLists(asJSNodesList(node.getChildren()), null, null, TypePunctuation.COMMA);
			checkedPop(wrapperNode, -1);
			if (node.getSemicolonIncluded())
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEndingOffset(), false, true);
			}
			int endingOffset = peek().getEndOffset();
			wrapperNode.setEnd(createTextNode(document, endingOffset, endingOffset));
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSInvokeNode)
		 */
		@Override
		public void visit(JSInvokeNode node)
		{
			// Push the function's name. Node that the function 'name' may be an expression in a group.
			JSNode functionName = (JSNode) node.getExpression();
			short nodeType = functionName.getNodeType();
			if (nodeType == IJSNodeTypes.GROUP || nodeType == IJSNodeTypes.FUNCTION
					|| nodeType == IJSNodeTypes.GET_PROPERTY)
			{
				// inline invocation.
				// We force an invocation node before visiting the function name. This should handle any new lines that
				// need to be before the invocation code.
				FormatterJSFunctionInvocationNode fin = new FormatterJSFunctionInvocationNode(document, node,
						hasAnyCommentBefore(node.getStartingOffset()));
				fin.setBegin(createTextNode(document, functionName.getStart(), functionName.getStart()));
				push(fin);
				checkedPop(fin, -1);
				functionName.accept(this);
			}
			else
			{
				pushFunctionInvocationName(node, functionName.getStart(), functionName.getEnd() + 1);
			}
			// Push the parenthesis and the parameters (if exist)
			List<JSNode> parameters = asJSNodesList(node.getArguments().getChildren());
			pushParametersInParentheses(functionName.getEnd() + 1, node.getEndingOffset(), parameters,
					TypePunctuation.COMMA, false, TypeBracket.INVOCATION_PARENTHESIS);
			if (node.getSemicolonIncluded())
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEndingOffset(), false, true);
			}
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
				int startingOffset = node.getStartingOffset();
				if (node.getChildCount() > 0 && node.getChild(0).getNodeType() != IJSNodeTypes.EMPTY)
				{
					// push the 'return' keyword with a forced space after it.
					visitTextNode(startingOffset, startingOffset + 6, true, 0, 1, false);
					visitChildren(node);
				}
				else
				{
					visitTextNode(startingOffset, startingOffset + 6, true, 0, 0, false);
				}
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEndingOffset(), false, true);
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
			if (node.getSemicolonIncluded())
			{
				IFormatterContainerNode lineNode = pushLineNode(node);
				// push the 'throw' keyword with a forced space after it.
				int startingOffset = node.getStartingOffset();
				visitTextNode(startingOffset, startingOffset + 5, true, 0, 1, false);
				visitChildren(node);
				checkedPop(lineNode, lineNode.getEndOffset());
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEndingOffset(), false, true);
			}
			else
			{
				super.visit(node);
			}
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
				int startingOffset = node.getStartingOffset();
				visitTextNode(startingOffset, startingOffset + 5, true, 0, 0, false);
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEndingOffset(), false, true);
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
			FormatterJSGroupNode groupNode = new FormatterJSGroupNode(document,
					hasAnyCommentBefore(node.getStartingOffset()));
			groupNode.setBegin(createTextNode(document, beginOffset, beginOffset + 1));
			push(groupNode);
			visitChildren(node);
			checkedPop(groupNode, endOffset);
			groupNode.setEnd(createTextNode(document, endOffset, endOffset + 1));
			if (node.getSemicolonIncluded())
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, node.getEndingOffset(), false, true);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSGetElementNode)
		 */
		@Override
		public void visit(JSGetElementNode node)
		{
			// a[b]
			JSNode leftHandSide = (JSNode) node.getLeftHandSide();
			FormatterJSGetElementNode getElementNode = new FormatterJSGetElementNode(document, node,
					hasAnyCommentBefore(node.getStartingOffset()));
			getElementNode.setBegin(createTextNode(document, node.getStartingOffset(), leftHandSide.getEndingOffset()));
			push(getElementNode);
			checkedPop(getElementNode, -1);
			int leftBracketOffset = node.getLeftBracket().getStart();
			int rightBracketOffset = node.getRightBracket().getStart();
			JSNode rightHandSide = (JSNode) node.getRightHandSide();
			pushNodeInParentheses('[', ']', leftBracketOffset, rightBracketOffset, rightHandSide, false,
					TypeBracket.ARRAY_SQUARE);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSGetPropertyNode)
		 */
		@Override
		public void visit(JSGetPropertyNode node)
		{
			// a.b.c
			int startingOffset = node.getStartingOffset();
			FormatterJSGetPropertyNode getElementNode = new FormatterJSGetPropertyNode(document, node,
					hasAnyCommentBefore(startingOffset));
			getElementNode.setBegin(createTextNode(document, startingOffset, startingOffset));
			push(getElementNode);

			// visit the internals
			JSNode leftHandSide = (JSNode) node.getLeftHandSide();
			JSNode rightHandSide = (JSNode) node.getRightHandSide();
			Symbol operator = node.getOperator();
			if (leftHandSide != null)
			{
				leftHandSide.accept(this);
			}
			pushTypePunctuation(TypePunctuation.JS_DOT_PROPERTY, operator.getStart());
			if (rightHandSide != null)
			{
				rightHandSide.accept(this);
			}
			int endingOffset = node.getEndingOffset() + 1;
			getElementNode.setEnd(createTextNode(document, endingOffset, endingOffset));
			checkedPop(getElementNode, endingOffset);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSNumberNode)
		 */
		public void visit(JSNumberNode node)
		{
			visitTextNode(node, true, 0);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSStringNode)
		 */
		@Override
		public void visit(JSStringNode node)
		{
			// We have to handle directives properly! Directives are strings on one line as a full "statement"
			// themselves. See http://dmitrysoshnikov.com/ecmascript/es5-chapter-2-strict-mode/
			IParseNode parent = node.getParent();
			if (parent instanceof JSStatementsNode || parent instanceof IParseRootNode)
			{
				visitTextNode(node.getStartingOffset(), node.getEndingOffset(), true, 0, 0, true);
			}
			else
			{
				visitTextNode(node, true, 0);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSNullNode)
		 */
		@Override
		public void visit(JSNullNode node)
		{
			visitTextNode(node, true, 0);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSThisNode)
		 */
		@Override
		public void visit(JSThisNode node)
		{
			visitTextNode(node, true, 0);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSRegexNode)
		 */
		@Override
		public void visit(JSRegexNode node)
		{
			visitTextNode(node, true, 0);
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
		 * Push a FormatterJSParenthesesNode for the JSNode. <br>
		 * 
		 * @param openChar
		 *            The parentheses open char (e.g. '(', '[' etc.)
		 * @param closeChar
		 *            The parentheses close char (e.g. ')', ']' etc.)
		 * @param declarationEndOffset
		 * @param expressionEndOffset
		 * @param node
		 * @param visitOnlyChildren
		 *            When true, the visit will be only for the node's children. Otherwise, a call on the node.accept
		 *            will be made.
		 * @param type
		 *            The bracket type.
		 */
		private void pushNodeInParentheses(char openChar, char closeChar, int parenLookupStart, int parenLookupEnd,
				JSNode node, boolean visitOnlyChildren, TypeBracket type)
		{
			int openParen = locateCharForward(document, openChar, parenLookupStart);
			int closeParen = locateCharBackward(document, closeChar, parenLookupEnd);
			FormatterJSParenthesesNode parenthesesNode = new FormatterJSParenthesesNode(document, false,
					hasSingleCommentBefore(openParen), hasSingleCommentBefore(closeParen), type);
			parenthesesNode.setBegin(createTextNode(document, openParen, openParen + 1));
			push(parenthesesNode);
			if (node != null)
			{
				if (visitOnlyChildren)
				{
					visitChildren(node);
				}
				else
				{
					node.accept(this);
				}
			}
			if (hasAnyCommentBefore(closeParen))
			{
				checkedPop(parenthesesNode, closeParen);
			}
			else
			{
				checkedPop(parenthesesNode, -1);
			}
			parenthesesNode.setEnd(createTextNode(document, closeParen, closeParen + 1));
		}

		/**
		 * This method is used to visit and push nodes that are separated with some delimiter, and potentially have
		 * operators between them.<br>
		 * For example, {"one" : "1", "two" : "2"}
		 * 
		 * @param leftNodes
		 *            A list of JSNode.
		 * @param rightNodes
		 *            A list of JSNode that are pairing with the leftNodes. In case there are no pairs, this list may be
		 *            null. However, if there are pairs (such as assignments), the size of this group must match the
		 *            size of the left group.
		 * @param pairsOperator
		 *            The operator {@link TypeOperator} that should appear between the left and the right pair, when
		 *            there are pairs. May be null only when the rightNodes are null.
		 * @param pairsSeparator
		 *            A separator that appears between the leftNodes. If there are pairs, the separator appears between
		 *            one pair to the other (may only be null in case a separator is not needed, e.g. we have only one
		 *            item/pair)
		 */
		private void visitNodeLists(List<? extends JSNode> leftNodes, List<? extends JSNode> rightNodes,
				TypeOperator pairsOperator, TypePunctuation pairsSeparator)
		{
			// push the expressions one at a time, with comma nodes between them.
			int leftSize = leftNodes.size();
			for (int i = 0; i < leftSize; i++)
			{
				JSNode left = leftNodes.get(i);
				JSNode right = (rightNodes != null) ? rightNodes.get(i) : null;
				left.accept(this);
				if (right != null && pairsOperator != null)
				{
					int startIndex = left.getEnd();
					String text = document.get(startIndex, right.getStart());
					String typeStr = pairsOperator.toString();
					startIndex += text.indexOf(typeStr);
					pushTypeOperator(pairsOperator, startIndex, false);
					right.accept(this);
				}
				// add a separator if needed
				if (i + 1 < leftSize)
				{
					int startIndex = left.getEnd() + 1;
					String text = document.get(startIndex, leftNodes.get(i + 1).getStart());
					String separatorStr = pairsSeparator.toString();
					startIndex += text.indexOf(separatorStr);
					pushTypePunctuation(pairsSeparator, startIndex);
				}
			}
		}

		/**
		 * Push an operator node.
		 * 
		 * @param operator
		 * @param startOffset
		 * @param isUnary
		 */
		private void pushTypeOperator(TypeOperator operator, int startOffset, boolean isUnary)
		{
			FormatterJSOperatorNode node = new FormatterJSOperatorNode(document, operator, isUnary,
					hasSingleCommentBefore(startOffset));
			node.setBegin(createTextNode(document, startOffset, startOffset + operator.toString().length()));
			push(node);
			checkedPop(node, -1);
		}

		/**
		 * Push a puctuation node.
		 * 
		 * @param punctuation
		 * @param startOffset
		 */
		private void pushTypePunctuation(TypePunctuation punctuation, int startOffset)
		{
			FormatterJSPunctuationNode node = new FormatterJSPunctuationNode(document, punctuation, false,
					hasSingleCommentBefore(startOffset));
			node.setBegin(createTextNode(document, startOffset, startOffset + punctuation.toString().length()));
			push(node);
			checkedPop(node, -1);
		}

		/**
		 * Push a FormatterJSParenthesesNode that contains a parameters array. <br>
		 * Each parameter in the parameters list is expected to be separated from the others with a comma.
		 * 
		 * @param declarationEndOffset
		 * @param expressionEndOffset
		 * @param parameters
		 * @param punctuationType
		 *            A {@link TypePunctuation}. Usually this should be a COMMA, but it can also be something else to
		 *            provide special formatting styles.
		 * @param lookForExtraComma
		 *            Indicate that the parameters list may end with an extra comma that is not included in them. This
		 *            function will look for that comma if the value is true and will add it as a punctuation node in
		 *            case it was found.
		 * @param bracketsType
		 */
		private void pushParametersInParentheses(int declarationEndOffset, int expressionEndOffset,
				List<? extends JSNode> parameters, TypePunctuation punctuationType, boolean lookForExtraComma,
				TypeBracket bracketsType)
		{
			FormatterJSParenthesesNode parenthesesNode = null;
			int openParen = getNextNonWhiteCharOffset(document, declarationEndOffset);
			if (document.charAt(openParen) == '(')
			{
				parenthesesNode = new FormatterJSParenthesesNode(document, bracketsType);
				parenthesesNode.setBegin(createTextNode(document, openParen, openParen + 1));
			}
			else
			{
				parenthesesNode = new FormatterJSParenthesesNode(document, true);
				parenthesesNode.setBegin(createTextNode(document, openParen, openParen));
			}
			push(parenthesesNode);

			if (parameters != null && parameters.size() > 0)
			{
				visitNodeLists(parameters, null, null, punctuationType);
				if (lookForExtraComma)
				{
					// Look ahead to find any extra comma that we may have. If found, push it as a punctuation node.
					int lastParamEnd = parameters.get(parameters.size() - 1).getEnd();
					int nextNonWhitespace = getNextNonWhiteCharOffset(document, lastParamEnd);
					if (document.charAt(nextNonWhitespace) == ',')
					{
						pushTypePunctuation(punctuationType, nextNonWhitespace);
					}
				}
			}
			int closeParenStart = expressionEndOffset;
			int closeParenEnd = expressionEndOffset;
			if (!parenthesesNode.isAsWrapper())
			{
				closeParenStart = locateCharBackward(document, ')', expressionEndOffset);
				closeParenEnd = closeParenStart + 1;
			}
			int popCheckOffset = -1;
			if (hasSingleCommentBefore(openParen))
			{
				parenthesesNode.setHasCommentBeforeOpen(true);
			}
			if (hasSingleCommentBefore(closeParenStart))
			{
				parenthesesNode.setHasCommentBeforeClose(true);
				popCheckOffset = closeParenStart;
			}
			if (hasMultiLineCommentBefore(closeParenStart))
			{
				popCheckOffset = closeParenStart;
			}
			checkedPop(parenthesesNode, popCheckOffset);
			parenthesesNode.setEnd(createTextNode(document, closeParenStart, closeParenEnd));
		}

		/**
		 * Locate and push a punctuation char node.
		 * 
		 * @param offsetToSearch
		 *            - The offset that will be used as the start for the search of the semicolon.
		 * @param ignoreNonWhitespace
		 *            indicate that a non-whitespace chars that appear before the semicolon will be ignored. If this
		 *            flag is false, and a non-whitespace appear between the given offset and the semicolon, the method
		 *            will <b>not</b> push a semicolon node.
		 * @param isLineTerminating
		 *            Indicates that this punctuation node is a line terminating one.
		 */
		private void findAndPushPunctuationNode(TypePunctuation type, int offsetToSearch, boolean ignoreNonWhitespace,
				boolean isLineTerminating)
		{
			char punctuationType = type.toString().charAt(0);
			int punctuationOffset = locateCharForward(document, punctuationType, offsetToSearch);
			if (punctuationOffset != offsetToSearch || document.charAt(punctuationOffset) == punctuationType)
			{
				String segment = document.get(offsetToSearch, punctuationOffset);
				if (!ignoreNonWhitespace && segment.trim().length() > 0)
				{
					return;
				}
				if (isLineTerminating)
				{
					// We need to make sure that the termination only happens when the line does not
					// have a terminator already.
					int lineEnd = locateWhitespaceLineEndingOffset(punctuationOffset + 1);
					isLineTerminating = lineEnd < 0;
				}
				FormatterJSPunctuationNode punctuationNode = new FormatterJSPunctuationNode(document, type,
						isLineTerminating, hasSingleCommentBefore(punctuationOffset));
				punctuationNode.setBegin(createTextNode(document, punctuationOffset, punctuationOffset + 1));
				push(punctuationNode);
				checkedPop(punctuationNode, -1);
			}
		}

		/**
		 * Common visit behavior for 'case' and 'default' nodes under a switch-case block
		 * 
		 * @param node
		 * @param colonOffset
		 */
		private void visitCaseOrDefaultNode(JSNode node, int colonOffset)
		{

			boolean hasBlockedChild = false;
			JSNode lastChild = null;
			if (node.getChildCount() > 0)
			{
				lastChild = (JSNode) node.getLastChild();
				while (lastChild.getNodeType() == IJSNodeTypes.EMPTY && lastChild.getSemicolonIncluded())
				{
					// get the previous one to the semicolon node
					lastChild = (JSNode) lastChild.getPreviousSibling();
				}
				if (lastChild != null && lastChild.getNodeType() == IJSNodeTypes.STATEMENTS)
				{
					hasBlockedChild = true;
				}
			}
			// push the case/default node till the colon
			FormatterJSCaseNode caseNode = new FormatterJSCaseNode(document, hasBlockedChild);
			caseNode.setBegin(createTextNode(document, node.getStartingOffset(), colonOffset + 1));
			push(caseNode);

			if (hasBlockedChild)
			{
				// we have a 'case' with a curly-block
				// lastChild == JSNodeTypes.STATEMENTS
				FormatterJSCaseBodyNode caseBodyNode = new FormatterJSCaseBodyNode(document,
						hasAnyCommentBefore(lastChild.getStartingOffset()));
				caseBodyNode.setBegin(createTextNode(document, lastChild.getStartingOffset(),
						lastChild.getStartingOffset() + 1));
				push(caseBodyNode);
				short nodeType = node.getNodeType();
				if (nodeType == IJSNodeTypes.CASE || nodeType == IJSNodeTypes.DEFAULT)
				{
					visitChildren(lastChild);
				}
				else
				{
					visitChildren(node);
				}
				int endingOffset = lastChild.getEndingOffset();
				checkedPop(caseBodyNode, endingOffset);
				int end = locateColonOrSemicolonInLine(endingOffset + 1, document);
				caseBodyNode.setEnd(createTextNode(document, endingOffset, end));
			}
			else
			{
				if (node.getNodeType() == IJSNodeTypes.CASE)
				{
					visitChildren(node, 1);
				}
				else
				{
					visitChildren(node);
				}
			}
			checkedPop(caseNode, node.getEndingOffset() + 1);
		}

		/**
		 * Push a FormatterJSBlockNode
		 * 
		 * @param block
		 */
		private void pushBlockNode(JSNode block, boolean consumeEndingSemicolon)
		{
			FormatterJSBlockNode bodyNode = new FormatterJSBlockNode(document,
					hasAnyCommentBefore(block.getStartingOffset()));
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
			pushCommonDeclaration(node, declarationEndOffset, body);
			boolean hasBody = (body.getNodeType() != IJSNodeTypes.EMPTY);
			if (hasBody)
			{
				// Then, push the body (the body might be defined without any brackets, so in those cases the begin and
				// end would be empty)
				FormatterJSBlockNode blockNode = new FormatterJSBlockNode(document,
						hasAnyCommentBefore(body.getStartingOffset()));
				boolean bodyInBrackets = (body.getNodeType() == IJSNodeTypes.STATEMENTS);
				if (bodyInBrackets)
				{
					blockNode
							.setBegin(createTextNode(document, body.getStartingOffset(), body.getStartingOffset() + 1));
					push(blockNode);
					visitChildren((JSNode) body);
					checkedPop(blockNode, body.getEndingOffset());
					int end = blockNode.getEndOffset() + 1;
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
		 * Wrap a given node in an implicit block node and visit the node to insert it as a child of that block.
		 * 
		 * @param node
		 *            The node to wrap and visit.
		 * @param indent
		 */
		private void wrapInImplicitBlock(JSNode node, boolean indent)
		{
			FormatterJSImplicitBlockNode emptyBlock = new FormatterJSImplicitBlockNode(document, false, indent, 0);
			int start = node.getStartingOffset();
			int end = node.getEndingOffset() + 1;
			emptyBlock.setBegin(createTextNode(document, start, start));
			push(emptyBlock);
			node.accept(this);
			checkedPop(emptyBlock, -1);
			emptyBlock.setEnd(createTextNode(document, end, end));
		}

		private void pushCommonDeclaration(JSNode node, int declarationEndOffset, IParseNode body)
		{
			// First, push the declaration part
			// In some cases, the body is empty (like in a 'while' with no body). Those cases get a special treatment.
			FormatterJSDeclarationNode declarationNode = new FormatterJSDeclarationNode(document,
					(body != null && body.getNodeType() == IJSNodeTypes.STATEMENTS), node,
					hasAnyCommentBefore(node.getStartingOffset()));
			int endDeclarationOffset = declarationEndOffset + 1;
			if (body != null)
			{
				if (body.getNodeType() != IJSNodeTypes.EMPTY)
				{
					endDeclarationOffset = declarationEndOffset + 1;
				}
				else
				{
					endDeclarationOffset = body.getEndingOffset() + 1;
				}
			}
			declarationNode.setBegin(createTextNode(document, node.getStartingOffset(), endDeclarationOffset));
			push(declarationNode);
			checkedPop(declarationNode, -1);
		}

		/**
		 * Visit an expression with left node, right node and an operator in between.<br>
		 * Note that the left <b>or</b> the right may be null.
		 * 
		 * @param parentNode
		 * @param left
		 * @param right
		 * @param operatorString
		 */
		private void visitLeftRightExpression(JSNode parentNode, JSNode left, JSNode right, String operatorString)
		{
			int leftOffset;
			int rightOffset;
			if (left != null)
			{
				left.accept(this);
				leftOffset = left.getEnd();
			}
			else
			{
				leftOffset = parentNode.getStart();
			}
			if (right != null)
			{
				rightOffset = right.getStart();
			}
			else
			{
				rightOffset = parentNode.getEnd();
			}
			int operatorOffset = document.get(leftOffset, rightOffset).indexOf(operatorString) + leftOffset;
			TypeOperator typeOperator = TypeOperator.getTypeOperator(operatorString);
			pushTypeOperator(typeOperator, operatorOffset, false);
			if (right != null)
			{
				right.accept(this);
			}
		}

		/**
		 * A simple visit and push of a node that pushes a JS text node which consumes any white-spaces before that node
		 * by request.
		 * 
		 * @param node
		 * @param consumePreviousWhitespaces
		 * @param spacesCountBefore
		 * @see #visitTextNode(int, int, boolean, int)
		 */
		private void visitTextNode(IParseNode node, boolean consumePreviousWhitespaces, int spacesCountBefore)
		{
			visitTextNode(node.getStartingOffset(), node.getEndingOffset() + 1, consumePreviousWhitespaces,
					spacesCountBefore);
		}

		/**
		 * A simple visit and push of a node that pushes a JS text node which consumes any white-spaces before that node
		 * by request.
		 * 
		 * @param startOffset
		 * @param endOffset
		 * @param consumePreviousWhitespaces
		 * @param spacesCountBefore
		 * @see #visitTextNode(ASTNode, boolean, int)
		 */
		private void visitTextNode(int startOffset, int endOffset, boolean consumePreviousWhitespaces,
				int spacesCountBefore)
		{
			visitTextNode(startOffset, endOffset, consumePreviousWhitespaces, spacesCountBefore, 0, false);
		}

		/**
		 * A simple visit and push of a node that pushes a JS text node which consumes any white-spaces before that node
		 * by request.
		 * 
		 * @param startOffset
		 * @param endOffset
		 * @param consumePreviousWhitespaces
		 * @param spacesCountBefore
		 * @param spacesCountAfter
		 */
		private void visitTextNode(int startOffset, int endOffset, boolean consumePreviousWhitespaces,
				int spacesCountBefore, int spacesCountAfter, boolean isAddingBeginLine)
		{
			// make sure that every text node we add right after a single line comment will have a new-line char(s)
			// prefix
			isAddingBeginLine |= hasSingleCommentBefore(startOffset);
			FormatterJSTextNode textNode = new FormatterJSTextNode(document, consumePreviousWhitespaces,
					spacesCountBefore, spacesCountAfter, isAddingBeginLine);
			textNode.setBegin(createTextNode(document, startOffset, endOffset));
			push(textNode);
			checkedPop(textNode, endOffset);
		}

		/**
		 * Push the name part of a function invocation.
		 * 
		 * @param invocationNode
		 * @param nameStart
		 * @param nameEnd
		 */
		private void pushFunctionInvocationName(JSNode invocationNode, int nameStart, int nameEnd)
		{
			FormatterJSFunctionInvocationNode node = new FormatterJSFunctionInvocationNode(document, invocationNode,
					hasAnyCommentBefore(invocationNode.getStartingOffset()));
			node.setBegin(createTextNode(document, nameStart, nameEnd));
			push(node);
			checkedPop(node, -1);
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

		/**
		 * Locate a line ending offset. The line should only contain whitespace characters.
		 * 
		 * @return The line ending offset, or -1 in case not found.
		 */
		private int locateWhitespaceLineEndingOffset(int start)
		{
			int length = document.getLength();
			for (int offset = start; offset < length; offset++)
			{
				char c = document.charAt(offset);
				if (c == '\n' || c == '\r')
				{
					return offset;
				}
				if (!Character.isWhitespace(c))
				{
					return -1;
				}
			}
			return -1;
		}

		private List<JSNode> asJSNodesList(IParseNode[] nodes)
		{
			List<JSNode> jsChildren = new ArrayList<JSNode>(nodes.length);
			for (IParseNode child : nodes)
			{
				if (child instanceof JSNode)
				{
					jsChildren.add((JSNode) child);
				}
				else
				{
					// we'll have a problem with such a node.
					IdeLog.logError(
							JSFormatterPlugin.getDefault(),
							MessageFormat.format("Expected JSFormatter and got {0}", child.getClass().getName()), IDebugScopes.DEBUG); //$NON-NLS-1$
				}
			}
			return jsChildren;
		}

		/**
		 * Visit the nodes children with an option to skip some of the first ones.
		 * 
		 * @param node
		 * @param skip
		 *            The number of children to skip visiting
		 */
		private void visitChildren(JSNode node, int skip)
		{
			for (IParseNode child : node)
			{
				if (skip > 0)
				{
					skip--;
					continue;
				}
				if (child instanceof JSNode)
				{
					((JSNode) child).accept(this);
				}
			}
		}
	}
}
