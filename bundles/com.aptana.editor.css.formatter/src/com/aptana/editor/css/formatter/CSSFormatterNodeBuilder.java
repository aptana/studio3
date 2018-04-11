/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

import com.aptana.css.core.parsing.ast.CSSDeclarationNode;
import com.aptana.css.core.parsing.ast.CSSErrorDeclarationNode;
import com.aptana.css.core.parsing.ast.CSSExpressionNode;
import com.aptana.css.core.parsing.ast.CSSFontFaceNode;
import com.aptana.css.core.parsing.ast.CSSMediaNode;
import com.aptana.css.core.parsing.ast.CSSNode;
import com.aptana.css.core.parsing.ast.CSSPageNode;
import com.aptana.css.core.parsing.ast.CSSPageSelectorNode;
import com.aptana.css.core.parsing.ast.CSSRuleNode;
import com.aptana.css.core.parsing.ast.CSSSelectorNode;
import com.aptana.css.core.parsing.ast.CSSTermListNode;
import com.aptana.css.core.parsing.ast.CSSTextNode;
import com.aptana.css.core.parsing.ast.ICSSNodeTypes;
import com.aptana.editor.css.formatter.nodes.FormatterCSSAtRuleNode;
import com.aptana.editor.css.formatter.nodes.FormatterCSSBlockNode;
import com.aptana.editor.css.formatter.nodes.FormatterCSSDeclarationPropertyNode;
import com.aptana.editor.css.formatter.nodes.FormatterCSSDeclarationValueNode;
import com.aptana.editor.css.formatter.nodes.FormatterCSSParenthesesNode;
import com.aptana.editor.css.formatter.nodes.FormatterCSSPunctuationNode;
import com.aptana.editor.css.formatter.nodes.FormatterCSSRootNode;
import com.aptana.editor.css.formatter.nodes.FormatterCSSSelectorNode;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.nodes.AbstractFormatterNodeBuilder;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.nodes.NodeTypes.TypePunctuation;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.ast.ParseRootNode;
import com.aptana.parsing.lexer.IRange;

/**
 * CSS formatter node builder.<br>
 * This builder generates the formatter nodes that will then be processed by the {@link CSSFormatterNodeRewriter} to
 * produce the output for the code formatting process.
 */
public class CSSFormatterNodeBuilder extends AbstractFormatterNodeBuilder
{

	/**
	 * The length of @media rule.
	 */
	private static final int MEDIA_AT_RULE_LENGTH = 6;
	/**
	 * The length of @page rule.
	 */
	private static final int PAGE_AT_RULE_LENGTH = 5;

	private FormatterDocument document;

	@SuppressWarnings("nls")
	public static final HashSet<String> PUNCTUATION = new HashSet<String>(Arrays.asList(",", ";", ":", ")", "(", ">"));
	private static final Pattern NON_LETTER_PATTERN = Pattern.compile("[^a-zA-Z]"); //$NON-NLS-1$

	/**
	 * @param parseResult
	 * @param document
	 * @return
	 */
	public IFormatterContainerNode build(IParseNode parseResult, FormatterDocument document)
	{
		this.document = document;
		final IFormatterContainerNode rootNode = new FormatterCSSRootNode(document);
		start(rootNode);
		IParseNode[] children = parseResult.getChildren();
		addNodes(children);
		checkedPop(rootNode, document.getLength());
		// Collect Off/On tags
		if (parseResult instanceof IParseRootNode)
		{
			setOffOnRegions(resolveOffOnRegions((IParseRootNode) parseResult, document,
					CSSFormatterConstants.FORMATTER_OFF_ON_ENABLED, CSSFormatterConstants.FORMATTER_OFF,
					CSSFormatterConstants.FORMATTER_ON));
		}
		return rootNode;
	}

	/**
	 * @param children
	 * @param rootNode
	 */
	private void addNodes(IParseNode[] children)
	{
		if (children == null || children.length == 0)
		{
			return;
		}
		for (IParseNode child : children)
		{
			addNode(child);
		}
	}

	/**
	 * @param node
	 * @param rootNode
	 */
	private void addNode(IParseNode node)
	{
		CSSNode cssNode = (CSSNode) node;
		short type = cssNode.getNodeType();

		switch (type)
		{
			case ICSSNodeTypes.RULE:
				pushFormatterRuleNode((CSSRuleNode) cssNode);
				break;
			case ICSSNodeTypes.PAGE:
				pushFormatterPageNode((CSSPageNode) cssNode);
				break;
			case ICSSNodeTypes.FONTFACE:
				pushFormatterFontFaceNode((CSSFontFaceNode) cssNode);
				break;
			case ICSSNodeTypes.MEDIA:
				pushFormatterMediaNode((CSSMediaNode) cssNode);
				break;
			case ICSSNodeTypes.AT_RULE:
			case ICSSNodeTypes.CHAR_SET:
			case ICSSNodeTypes.NAMESPACE:
			case ICSSNodeTypes.IMPORT:
				// Custom at-rule and import nodes currently fall under the same formatting case. This may need to
				// change once the parser returns the url part as a textnode
				pushAtRuleNode(cssNode);
				break;
			default:
				break;
		}

	}

	// This is a temporary fix for custom at-rules. When the parser adds support to return the ruleID, this will need to
	// be changed
	private void pushAtRuleNode(CSSNode atRuleNode)
	{

		int length = document.getLength();
		int selectorStartingOffset = atRuleNode.getStartingOffset();
		int selectEndingOffset = atRuleNode.getEndingOffset();

		// Locate first white space after the @rule
		while (selectorStartingOffset < length)
		{
			if (Character.isWhitespace(document.charAt(selectorStartingOffset)))
			{
				break;
			}
			selectorStartingOffset++;
		}
		// Find the starting offset for the selector
		selectorStartingOffset = getBeginWithoutWhiteSpaces(selectorStartingOffset, document);

		// Find the end offset for the selector
		while (selectEndingOffset >= selectorStartingOffset)
		{
			if (!Character.isWhitespace(document.charAt(selectEndingOffset - 1)))
			{
				break;
			}
			selectEndingOffset--;
		}
		// Push an At-Node to control the lines separators.
		FormatterCSSAtRuleNode atNode = new FormatterCSSAtRuleNode(document);
		atNode.setBegin(createTextNode(document, atRuleNode.getStartingOffset(), selectorStartingOffset));
		push(atNode);
		checkedPop(atNode, -1);

		// We use a selector node for now, we may want to create a new formatter node type for rule id
		FormatterBlockWithBeginNode formatterSelectorNode = new FormatterCSSSelectorNode(document, false, false);
		formatterSelectorNode.setBegin(createTextNode(document,
				getBeginWithoutWhiteSpaces(selectorStartingOffset, document),
				getEndWithoutWhiteSpaces(selectEndingOffset, document) + 1));
		push(formatterSelectorNode);

		findAndPushPunctuationNode(TypePunctuation.SEMICOLON, selectEndingOffset, false);

		checkedPop(formatterSelectorNode, -1);
	}

	/**
	 * Accepts a CSSMediaNode and breaks down the node into different formatter nodes which should represent it while
	 * rewriting the doc.<br>
	 * The statements of the media node will also be recursively added as formatter nodes.
	 * 
	 * @param pageNode
	 */
	private void pushFormatterMediaNode(CSSMediaNode mediaNode)
	{

		// Push an At-Node to control the lines separators.
		FormatterCSSAtRuleNode atNode = new FormatterCSSAtRuleNode(document);
		int mediaNodeStart = mediaNode.getStartingOffset();
		atNode.setBegin(createTextNode(document, mediaNodeStart, mediaNodeStart + MEDIA_AT_RULE_LENGTH));
		push(atNode);
		checkedPop(atNode, -1);

		CSSTextNode[] medias = mediaNode.getMedias();

		int blockStartOffset = getBlockStartOffset(medias[medias.length - 1].getEndingOffset() + 1, document);
		pushFormatterMediaSelectorNodes(medias, 0);

		FormatterBlockWithBeginEndNode formatterBlockNode = new FormatterCSSBlockNode(document, false);
		formatterBlockNode.setBegin(createTextNode(document, blockStartOffset, blockStartOffset + 1));

		push(formatterBlockNode);

		// Recursively add this node's children
		CSSNode[] statements = mediaNode.getStatements();
		addNodes(statements);

		if (statements.length > 0)
		{
			formatterBlockNode.addChild(createTextNode(document,
					statements[statements.length - 1].getEndingOffset() + 1, mediaNode.getEndingOffset()));
		}

		checkedPop(formatterBlockNode, -1);
		formatterBlockNode
				.setEnd(createTextNode(document, mediaNode.getEndingOffset(), mediaNode.getEndingOffset() + 1));

	}

	/**
	 * Pushes the selector nodes in a media node. (Also recursively handles nodes inside parenthesis)
	 * 
	 * @param medias
	 * @param startIndex
	 * @return The index of the last medias that it was pushed
	 */
	private int pushFormatterMediaSelectorNodes(CSSTextNode[] medias, int startIndex)
	{
		boolean previousNodeIsPunctuation = startIndex > 0;
		int lastMediaIndex = startIndex;
		for (; lastMediaIndex < medias.length; lastMediaIndex++)
		{
			CSSTextNode mediaSelectorNode = medias[lastMediaIndex];
			String selectorText = mediaSelectorNode.getText();

			// For media nodes that are just punctuation, we skip it
			if (selectorText.length() == 1 && NON_LETTER_PATTERN.matcher(selectorText).matches())
			{
				TypePunctuation punctuation = getTypePunctuationForChar(selectorText.charAt(0));
				// push a punctuation node if it's part of the list
				if (punctuation != null)
				{
					findAndPushPunctuationNode(punctuation, mediaSelectorNode.getStartingOffset(), false);
				}
				else if (selectorText.charAt(0) == '(')
				{
					// This is the start of a parenthesis block
					int openParen = mediaSelectorNode.getStartingOffset();
					FormatterBlockWithBeginEndNode parenthesisNode = new FormatterCSSParenthesesNode(document);
					parenthesisNode.setBegin(createTextNode(document, openParen, openParen + 1));

					push(parenthesisNode);
					// push contents inside the parenthesis
					lastMediaIndex = pushFormatterMediaSelectorNodes(medias, lastMediaIndex + 1);

					checkedPop(parenthesisNode, -1);
					if (lastMediaIndex >= medias.length)
					{
						break;
					}
					mediaSelectorNode = medias[lastMediaIndex];
					parenthesisNode.setEnd(createTextNode(document, mediaSelectorNode.getStartingOffset(),
							mediaSelectorNode.getStartingOffset() + 1));
				}
				else if (selectorText.charAt(0) == ')')
				{
					// This is the end of a parenthesis block
					return lastMediaIndex;
				}
				previousNodeIsPunctuation = true;
				// otherwise, we still skip all other punctuation
				continue;
			}

			FormatterBlockWithBeginNode formatterSelectorNode = new FormatterCSSSelectorNode(document, false,
					previousNodeIsPunctuation);
			formatterSelectorNode.setBegin(createTextNode(document,
					getBeginWithoutWhiteSpaces(mediaSelectorNode.getStartingOffset(), document),
					getEndWithoutWhiteSpaces(mediaSelectorNode.getEndingOffset() + 1, document) + 1));
			push(formatterSelectorNode);
			checkedPop(formatterSelectorNode, -1);
			previousNodeIsPunctuation = false;
		}
		return lastMediaIndex;
	}

	/**
	 * Accepts a CSSFontFaceNode and breaks down the node into different formatter nodes which should represent it while
	 * rewriting the doc.<br>
	 * The CSSFontFaceNode will be broken down into several nodes of type FormatterCSSSelectorNode,
	 * FormatterCSSBlockNode, and FormatterCSSDeclarationNode (if declarations are present).
	 * 
	 * @param pageNode
	 */
	private void pushFormatterFontFaceNode(CSSFontFaceNode faceFontNode)
	{

		CSSDeclarationNode[] declarations = faceFontNode.getDeclarations();
		int blockStartOffset = getBlockStartOffset(faceFontNode.getStartingOffset() + 9, document);

		// create a FormatterCSSSelectorNode for @font-face
		FormatterCSSAtRuleNode atFontFaceNode = new FormatterCSSAtRuleNode(document);
		atFontFaceNode.setBegin(createTextNode(document,
				getBeginWithoutWhiteSpaces(faceFontNode.getStartingOffset(), document),
				getEndWithoutWhiteSpaces(faceFontNode.getStartingOffset() + 9, document) + 1));
		push(atFontFaceNode);
		checkedPop(atFontFaceNode, -1);

		FormatterBlockWithBeginEndNode formatterBlockNode = new FormatterCSSBlockNode(document, false);
		formatterBlockNode.setBegin(createTextNode(document, blockStartOffset, blockStartOffset + 1));

		push(formatterBlockNode);

		// Don't create text nodes when there are no declarations, or only white space
		if (declarations != null && declarations.length != 0
				&& getBeginWithoutWhiteSpaces(blockStartOffset + 1, document) < declarations[0].getStartingOffset())
		{
			formatterBlockNode.addChild(createTextNode(document, blockStartOffset + 1,
					declarations[0].getStartingOffset()));

		}

		pushFormatterDeclarationNodes(faceFontNode.getEndingOffset(), declarations, formatterBlockNode);

		checkedPop(formatterBlockNode, -1);
		formatterBlockNode.setEnd(createTextNode(document, faceFontNode.getEndingOffset(),
				faceFontNode.getEndingOffset() + 1));
	}

	/**
	 * Accepts a CSSPageNode and breaks down the node into different formatter nodes which should represent it while
	 * rewriting the doc.<br>
	 * The CSSPageNode will be broken down into several nodes of type FormatterCSSSelectorNode, FormatterCSSBlockNode,
	 * and FormatterCSSDeclarationNode (if declarations are present).
	 * 
	 * @param pageNode
	 */
	private void pushFormatterPageNode(CSSPageNode pageNode)
	{
		// Push an At-Node to control the lines separators.
		FormatterCSSAtRuleNode atNode = new FormatterCSSAtRuleNode(document);
		// +5 for @page length
		int pageNodeStart = pageNode.getStartingOffset();
		atNode.setBegin(createTextNode(document, pageNodeStart, pageNodeStart + PAGE_AT_RULE_LENGTH));
		push(atNode);
		checkedPop(atNode, -1);

		CSSPageSelectorNode selector = pageNode.getSelector();
		CSSDeclarationNode[] declarations = pageNode.getDeclarations();
		int blockStartOffset = getBlockStartOffset(pageNodeStart + 1, document);

		if (selector != null)
		{
			blockStartOffset = getBlockStartOffset(selector.getEndingOffset() + 1, document);

			FormatterBlockWithBeginNode formatterSelectorNode = new FormatterCSSSelectorNode(document, true, false);
			formatterSelectorNode.setBegin(createTextNode(document,
					getBeginWithoutWhiteSpaces(pageNodeStart, document),
					getEndWithoutWhiteSpaces(pageNodeStart + PAGE_AT_RULE_LENGTH, document) + 1));
			push(formatterSelectorNode);
			checkedPop(formatterSelectorNode, -1);

			formatterSelectorNode = new FormatterCSSSelectorNode(document, false, false);
			// we do startingOffset - 1 to account for the ':'
			formatterSelectorNode.setBegin(createTextNode(document,
					getBeginWithoutWhiteSpaces(selector.getStartingOffset() - 1, document),
					getEndWithoutWhiteSpaces(selector.getEndingOffset() + 1, document) + 1));

			push(formatterSelectorNode);
			checkedPop(formatterSelectorNode, -1);
		}

		FormatterBlockWithBeginEndNode formatterBlockNode = new FormatterCSSBlockNode(document, false);
		formatterBlockNode.setBegin(createTextNode(document, blockStartOffset, blockStartOffset + 1));
		push(formatterBlockNode);

		// Don't create text nodes when there are no declarations, or only white space
		if (declarations != null && declarations.length != 0
				&& getBeginWithoutWhiteSpaces(blockStartOffset + 1, document) < declarations[0].getStartingOffset())
		{
			formatterBlockNode.addChild(createTextNode(document, blockStartOffset + 1,
					declarations[0].getStartingOffset()));

		}

		pushFormatterDeclarationNodes(pageNode.getEndingOffset(), declarations, formatterBlockNode);

		checkedPop(formatterBlockNode, -1);
		formatterBlockNode.setEnd(createTextNode(document, pageNode.getEndingOffset(), pageNode.getEndingOffset() + 1));

	}

	/**
	 * Accepts a CSSRuleNode and breaks down the node into different formatter nodes which should represent it while
	 * rewriting the doc.<br>
	 * The CSSRuleNode will be broken down into several nodes of type FormatterCSSSelectorNode, FormatterCSSBlockNode,
	 * and FormatterCSSDeclarationNode (if declarations are present).
	 * 
	 * @param ruleNode
	 */
	private void pushFormatterRuleNode(CSSRuleNode ruleNode)
	{

		CSSSelectorNode[] selectors = ruleNode.getSelectors();
		CSSDeclarationNode[] declarations = ruleNode.getDeclarations();
		int blockStartOffset = getBlockStartOffset(selectors[selectors.length - 1].getEndingOffset() + 1, document);

		pushFormatterSelectorNodes(selectors);

		// Check to see whether the rule node is a declaration of a media node
		boolean isDeclaration = (ruleNode.getParent() == null) || !(ruleNode.getParent() instanceof ParseRootNode);

		FormatterBlockWithBeginEndNode formatterBlockNode = new FormatterCSSBlockNode(document, isDeclaration);
		formatterBlockNode.setBegin(createTextNode(document, blockStartOffset, blockStartOffset + 1));
		push(formatterBlockNode);

		// Don't create text nodes when there are no declarations, or only white space
		if (declarations != null && declarations.length != 0
				&& getBeginWithoutWhiteSpaces(blockStartOffset + 1, document) < declarations[0].getStartingOffset())
		{
			formatterBlockNode.addChild(createTextNode(document, blockStartOffset + 1,
					declarations[0].getStartingOffset()));

		}

		pushFormatterDeclarationNodes(ruleNode.getEndingOffset(), declarations, formatterBlockNode);
		checkedPop(formatterBlockNode, ruleNode.getEndingOffset());
		formatterBlockNode.setEnd(createTextNode(document, ruleNode.getEndingOffset(), ruleNode.getEndingOffset() + 1));

	}

	private void pushFormatterDeclarationNodes(int parentEndOffset, CSSDeclarationNode[] declarations,
			FormatterBlockWithBeginEndNode formatterBlockNode)
	{
		for (int i = 0; i < declarations.length; ++i)
		{

			CSSDeclarationNode declarationNode = declarations[i];
			CSSExpressionNode expressionNode = declarationNode.getAssignedValue();

			// If we run into a CSSErrorDeclarationNode, we just create a text node and move on to the next declaration
			// node
			if (declarationNode instanceof CSSErrorDeclarationNode)
			{
				formatterBlockNode.addChild(createTextNode(document, declarationNode.getStartingOffset(),
						declarationNode.getEndingOffset() + 1));
				// Create text nodes for comments between declaration
				findAndPushCommentsBetweenDeclarations(parentEndOffset, declarations, formatterBlockNode, i);
				continue;
			}

			// push the property
			FormatterBlockWithBeginNode formatterDeclarationPropertyNode = new FormatterCSSDeclarationPropertyNode(
					document);
			int propertyEndOffset = getEndWithoutWhiteSpaces(
					locateCharacterInSameLine(':', declarationNode.getStartingOffset(), document), document);

			formatterDeclarationPropertyNode.setBegin(createTextNode(document, declarationNode.getStartingOffset(),
					propertyEndOffset + 1));
			push(formatterDeclarationPropertyNode);
			checkedPop(formatterDeclarationPropertyNode, -1);

			// push the ':'
			findAndPushPunctuationNode(TypePunctuation.PROPERTY_COLON, propertyEndOffset, false);

			// push the value for the declaration
			if (expressionNode != null)
			{
				if (expressionNode instanceof CSSTermListNode)
				{
					pushTermListNode((CSSTermListNode) expressionNode, i == declarations.length - 1);
				}
				else
				{
					pushDeclarationValueNode(expressionNode, i == declarations.length - 1);
				}

				// Push a text node for status nodes: currently it's only !important
				IRange statusRange = declarationNode.getStatusRange();
				if (declarationNode.getStatus() != null)
				{
					formatterBlockNode.addChild(createTextNode(document, statusRange.getStartingOffset(),
							statusRange.getEndingOffset() + 1));
				}
			}

			// Push a semicolon if the declaration ends with one
			if (document.charAt(declarationNode.getEndingOffset()) == ';')
			{
				findAndPushPunctuationNode(TypePunctuation.SEMICOLON, declarationNode.getEndingOffset(), true);
			}

			// Create text nodes for comments between declaration
			findAndPushCommentsBetweenDeclarations(parentEndOffset, declarations, formatterBlockNode, i);
		}
	}

	private void findAndPushCommentsBetweenDeclarations(int parentEndOffset, CSSDeclarationNode[] declarations,
			FormatterBlockWithBeginEndNode formatterBlockNode, int currentDeclarationIndex)
	{
		if (currentDeclarationIndex + 1 < declarations.length)
		{
			if (getBeginWithoutWhiteSpaces(declarations[currentDeclarationIndex].getEndingOffset() + 1, document) < declarations[currentDeclarationIndex + 1]
					.getStartingOffset())
			{
				formatterBlockNode.addChild(createTextNode(document,
						declarations[currentDeclarationIndex].getEndingOffset() + 1,
						declarations[currentDeclarationIndex + 1].getStartingOffset()));
			}
		}
		else if (getBeginWithoutWhiteSpaces(declarations[currentDeclarationIndex].getEndingOffset() + 1, document) < parentEndOffset)
		{
			formatterBlockNode.addChild(createTextNode(document,
					declarations[currentDeclarationIndex].getEndingOffset() + 1, parentEndOffset));
		}
	}

	private void pushDeclarationValueNode(CSSExpressionNode expressionNode, boolean isLastDeclaration)
	{

		int expressionEndOffset = expressionNode.getEndingOffset();
		int semicolonLocation = locateCharacterSkippingWhitespaces(document, expressionEndOffset + 1, ';', false);
		int commaLocation = locateCharacterSkippingWhitespaces(document, expressionEndOffset + 1, ',', false);
		int forwardSlashLocation = locateCharacterSkippingWhitespaces(document, expressionEndOffset + 1, '/', false);
		int LFLocation = locateCharacterSkippingWhitespaces(document, expressionEndOffset + 1, '\n', false);
		int CRLocation = locateCharacterSkippingWhitespaces(document, expressionEndOffset + 1, '\r', false);

		boolean endsWithSemicolon = false;
		boolean endsWithComma = false;
		boolean endsWithSlash = false;
		boolean isLastNodeInDeclaration = false;

		if (document.charAt(semicolonLocation) == ';')
		{
			endsWithSemicolon = true;
		}

		if (document.charAt(commaLocation) == ',')
		{
			endsWithComma = true;
		}

		if (document.charAt(forwardSlashLocation) == '/')
		{
			endsWithSlash = true;
		}

		if (document.charAt(forwardSlashLocation) == '/')
		{
			endsWithSlash = true;
		}

		if ((document.charAt(LFLocation) == '\n' || document.charAt(CRLocation) == '\r') && isLastDeclaration)
		{
			isLastNodeInDeclaration = true;
		}

		FormatterBlockWithBeginNode formatterDeclarationValueNode = new FormatterCSSDeclarationValueNode(document,
				isLastNodeInDeclaration, endsWithComma || endsWithSemicolon || endsWithSlash);

		formatterDeclarationValueNode.setBegin(createTextNode(document, expressionNode.getStartingOffset(),
				expressionNode.getEndingOffset() + 1));
		push(formatterDeclarationValueNode);
		checkedPop(formatterDeclarationValueNode, -1);

		if (endsWithComma)
		{
			findAndPushPunctuationNode(TypePunctuation.COMMA, commaLocation, false);
		}
	}

	private void pushTermListNode(CSSTermListNode termListNode, boolean isLastDeclaration)
	{
		CSSExpressionNode leftExpression = termListNode.getLeftExpression();
		CSSExpressionNode rightExpression = termListNode.getRightExpression();
		if (leftExpression instanceof CSSTermListNode)
		{
			pushTermListNode((CSSTermListNode) leftExpression, isLastDeclaration);
			// push the right expression here, as we are moving back up the tree
			pushDeclarationValueNode(rightExpression, isLastDeclaration);
		}
		else
		{
			pushDeclarationValueNode(leftExpression, isLastDeclaration);
			pushDeclarationValueNode(rightExpression, isLastDeclaration);
		}
	}

	private void pushFormatterSelectorNodes(CSSSelectorNode[] selectors)
	{

		for (int i = 0; i < selectors.length; i++)
		{
			CSSSelectorNode selectorNode = selectors[i];
			FormatterBlockWithBeginNode formatterSelectorNode = new FormatterCSSSelectorNode(document, i == 0, false);
			int selectorEndOffset = getEndWithoutWhiteSpaces(selectorNode.getEndingOffset() + 1, document) + 1;

			formatterSelectorNode.setBegin(createTextNode(document,
					getBeginWithoutWhiteSpaces(selectorNode.getStartingOffset(), document), selectorEndOffset));
			push(formatterSelectorNode);

			checkedPop(formatterSelectorNode, -1);

			int nextNonWhiteSpaceOffset = getBeginWithoutWhiteSpaces(selectorEndOffset, document);

			TypePunctuation punctuation = getTypePunctuationForChar(document.charAt(nextNonWhiteSpaceOffset));

			if (punctuation != null)
			{
				findAndPushPunctuationNode(punctuation, nextNonWhiteSpaceOffset, false);
			}
		}
	}

	private TypePunctuation getTypePunctuationForChar(char punctuation)
	{
		switch (punctuation)
		{
			case ',':
				return TypePunctuation.COMMA;
			case ';':
				return TypePunctuation.SEMICOLON;
			case ':':
				return TypePunctuation.SELECTOR_COLON;
			case '>':
				return TypePunctuation.CSS_CHILD_COMBINATOR;
			default:
				break;
		}
		return null;
	}

	/**
	 * Locate and push a punctuation node.
	 * 
	 * @param offsetToSearch
	 *            - The offset that will be used as the start for the search of the punctuation characters.
	 */
	private void findAndPushPunctuationNode(TypePunctuation type, int offsetToSearch, boolean isEndofDeclarationNode)
	{
		char punctuationType = type.toString().charAt(0);
		int punctuationOffset = locateCharForward(document, punctuationType, offsetToSearch);
		if (punctuationOffset != offsetToSearch || document.charAt(punctuationOffset) == punctuationType)
		{
			FormatterCSSPunctuationNode punctuationNode = new FormatterCSSPunctuationNode(document, type,
					isEndofDeclarationNode);
			punctuationNode.setBegin(createTextNode(document, punctuationOffset, punctuationOffset + 1));
			push(punctuationNode);
			checkedPop(punctuationNode, -1);
		}
	}

	private int getBlockStartOffset(int offset, FormatterDocument document)
	{
		int length = document.getLength();
		while (offset < length)
		{
			if (document.charAt(offset) == '{')
			{
				break;
			}
			offset++;
		}
		return offset;
	}

	/**
	 * @param offset
	 * @param document
	 * @return
	 */
	private int getBeginWithoutWhiteSpaces(int offset, FormatterDocument document)
	{
		int length = document.getLength();
		while (offset < length)
		{
			if (!Character.isWhitespace(document.charAt(offset)))
			{
				break;
			}
			offset++;
		}
		return offset;
	}

	/**
	 * Searchings backwards starting at 'offset' and continues until it finds a non-whitespace char (ignores
	 * punctuation)
	 * 
	 * @param offset
	 * @param document
	 * @return
	 */
	private int getEndWithoutWhiteSpaces(int offset, FormatterDocument document)
	{

		while (offset > 0)
		{
			if (!Character.isWhitespace(document.charAt(offset)) && document.charAt(offset) != '{'
					&& !PUNCTUATION.contains(Character.toString(document.charAt(offset))))
			{
				break;
			}
			offset--;
		}
		return offset;
	}

	/**
	 * Scan for given character located at the same line. Return the given offset if non is found.
	 * 
	 * @param offset
	 * @param document
	 * @return The offset of the character; The given offset if character not found.
	 */
	private int locateCharacterInSameLine(char character, int offset, FormatterDocument document)
	{
		for (int i = offset; i < document.getLength(); i++)
		{
			char c = document.charAt(i);
			if (c == character)
			{
				return i;
			}
			if (c == '\n' || c == '\r')
			{
				break;
			}
		}
		return offset;
	}
}
