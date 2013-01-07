/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.IDocument;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.IDebugScopes;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.text.JSFlexLexemeProvider;
import com.aptana.index.core.Index;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.inferencing.JSNodeTypeInferrer;
import com.aptana.js.core.inferencing.JSScope;
import com.aptana.js.core.inferencing.JSTypeMapper;
import com.aptana.js.core.parsing.JSFlexScanner;
import com.aptana.js.core.parsing.JSTokenType;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSGetPropertyNode;
import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.Lexeme;

/**
 * ParseUtil
 */
public class ParseUtil
{
	private static class FunctionParameterParser
	{
		private final IDocument document;
		private final int offset;
		/**
		 * We use ArrayList so we can trim the size down explicitly
		 */
		private ArrayList<String> parameters;
		private JSFlexLexemeProvider lexemeProvider;
		private Lexeme<JSTokenType> currentLexeme;
		private int lexemeIndex;

		public FunctionParameterParser(IDocument document, int offset)
		{
			this.document = document;
			this.offset = offset;
		}

		/**
		 * Advance to the next token in the token stream. The currentLexeme is updated appropriately. If we advance
		 * beyond the end of the stream, currentLexeme will be null indicating an EOF condition
		 */
		private void advance()
		{
			if (lexemeIndex < lexemeProvider.size())
			{
				currentLexeme = lexemeProvider.getLexeme(lexemeIndex++);
			}
			else
			{
				currentLexeme = null;
			}
		}

		/**
		 * Advance to the next token if the the current lexeme is of the specified type.
		 * 
		 * @param type
		 *            The type to test against the current lexeme
		 * @return Returns true if the current lexeme was of the specified type and was advanced
		 */
		private boolean advanceIfType(JSTokenType type)
		{
			boolean result = isType(type);

			if (result)
			{
				advance();
			}

			return result;
		}

		/**
		 * Try to extract the formal function parameters from the document and offset that were specified in the
		 * constructor. This currently recognizes the following cases:<br>
		 * 1. <sdoc-comment>function abc() {}<br>
		 * 2. <sdoc-comment>(function() {})()<br>
		 * 3. <sdoc-comment>key: function() {}<br>
		 * 4. <sdoc-comment>"key": function() {}<br>
		 * 5. <sdoc-comment>var x = function() {}<br>
		 * 6. <sdoc-comment>x = function() {}<br>
		 * 7. <sdoc-comment>x.y = function() {}
		 * 
		 * @throws Exception
		 */
		private void extractParameters() throws Exception
		{
			if (currentLexeme != null)
			{
				switch (currentLexeme.getType())
				{
					case LPAREN:
						parseSelfInvokingLambda();
						break;

					case FUNCTION:
						parseFunctionDeclaration();
						break;

					case VAR:
						parseVarDeclaration();
						break;

					case IDENTIFIER:
						parseIdentifier();
						break;

					case STRING:
						parseString();
						break;

					default:
						// ignore
				}
			}
		}

		/**
		 * Return a list of parameter names
		 * 
		 * @return
		 */
		public List<String> getFunctionParameters()
		{
			// initialize parameter list
			parameters = new ArrayList<String>();

			// setup lexeme source
			lexemeProvider = new JSFlexLexemeProvider(document, offset, new JSFlexScanner());

			// prime lexeme provider
			lexemeIndex = lexemeProvider.getLexemeCeilingIndex(offset);
			advance();

			// parse
			try
			{
				extractParameters();
			}
			catch (Exception e)
			{
				// ignore since this is just for flow-control
			}

			parameters.trimToSize();
			return parameters;
		}

		/**
		 * Determine if the current lexeme is of the specified type
		 * 
		 * @param type
		 *            The type to test against the current lexeme
		 * @return Returns true if the type matches the current lexeme
		 */
		private boolean isType(JSTokenType type)
		{
			return (currentLexeme != null && currentLexeme.getType() == type);
		}

		/**
		 * Parse a function declaration and extract the formal parameter list as a side-effect
		 * 
		 * @throws Exception
		 */
		private void parseFunctionDeclaration() throws Exception
		{
			// advance over 'function'
			testAndAdvance(JSTokenType.FUNCTION);

			// advance over optional identifier
			advanceIfType(JSTokenType.IDENTIFIER);

			// test for '('
			if (advanceIfType(JSTokenType.LPAREN))
			{
				do
				{
					if (isType(JSTokenType.IDENTIFIER))
					{
						parameters.add(currentLexeme.getText());
						advance();
					}
					else
					{
						break;
					}
				}
				while (advanceIfType(JSTokenType.COMMA));
			}
		}

		/**
		 * Parse an assignment or a key/value pair
		 * 
		 * @throws Exception
		 */
		private void parseIdentifier() throws Exception
		{
			testAndAdvance(JSTokenType.IDENTIFIER);

			if (advanceIfType(JSTokenType.COLON))
			{
				parseFunctionDeclaration();
			}
			else
			{
				while (advanceIfType(JSTokenType.DOT))
				{
					if (!advanceIfType(JSTokenType.IDENTIFIER))
					{
						break;
					}
				}

				if (advanceIfType(JSTokenType.EQUAL))
				{
					parseFunctionDeclaration();
				}
			}
		}

		/**
		 * Parse a key/value pair
		 * 
		 * @throws Exception
		 */
		private void parseString() throws Exception
		{
			testAndAdvance(JSTokenType.STRING);

			if (advanceIfType(JSTokenType.COLON))
			{
				parseFunctionDeclaration();
			}
		}

		/**
		 * Parse a self-invoking lambda
		 * 
		 * @throws Exception
		 */
		private void parseSelfInvokingLambda() throws Exception
		{
			testAndAdvance(JSTokenType.LPAREN);

			parseFunctionDeclaration();
		}

		/**
		 * Parse a var declaration assuming the first value after '=' is a function
		 */
		private void parseVarDeclaration() throws Exception
		{
			testAndAdvance(JSTokenType.VAR);

			while (advanceIfType(JSTokenType.IDENTIFIER))
			{
				advanceIfType(JSTokenType.COMMA);
			}

			if (advanceIfType(JSTokenType.EQUAL))
			{
				parseFunctionDeclaration();
			}
		}

		/**
		 * Verify that the current lexeme is of the specified type and advance to the next token. This throws an
		 * exception if the current lexeme does not match the specified type.
		 * 
		 * @param type
		 *            The type to test against the current lexeme
		 * @throws Exception
		 */
		private void testAndAdvance(JSTokenType type) throws Exception
		{
			if (!advanceIfType(type))
			{
				// @formatter:off
				String message = MessageFormat.format(
					"Parser error: Expected {0} but encountered {1}", //$NON-NLS-1$
					type,
					(currentLexeme != null) ? currentLexeme.getType() : JSTokenType.EOF
				);
				// @formatter:on

				throw new Exception(message);
			}
		}
	}

	/**
	 * Try to extract a formal parameter list for the function declaration immediately following the specified offset in
	 * a document
	 * 
	 * @param document
	 *            The document that contains the source to be processed
	 * @param offset
	 *            The offset within the document where the function declaration begins
	 * @return
	 */
	public static List<String> getFunctionParameters(IDocument document, int offset)
	{
		return new FunctionParameterParser(document, offset).getFunctionParameters();
	}

	/**
	 * getGetPropertyNode
	 * 
	 * @param targetNode
	 * @param statementNode
	 * @return
	 */
	public static JSGetPropertyNode getGetPropertyNode(IParseNode targetNode, IParseNode statementNode)
	{
		JSGetPropertyNode propertyNode = null;

		if (targetNode != null)
		{
			if (targetNode.getNodeType() == IJSNodeTypes.GET_PROPERTY)
			{
				propertyNode = (JSGetPropertyNode) targetNode;
			}
			else if (targetNode.getNodeType() == IJSNodeTypes.ARGUMENTS)
			{
				IParseNode candidate = targetNode.getParent().getFirstChild();

				if (candidate instanceof JSGetPropertyNode)
				{
					propertyNode = (JSGetPropertyNode) candidate;
				}
			}
			else
			{
				IParseNode parentNode = targetNode.getParent();

				if (parentNode != null && parentNode.getNodeType() == IJSNodeTypes.GET_PROPERTY)
				{
					propertyNode = (JSGetPropertyNode) parentNode;
				}
			}
		}

		if (propertyNode == null && statementNode != null)
		{
			if (statementNode.getNodeType() == IJSNodeTypes.GET_PROPERTY)
			{
				propertyNode = (JSGetPropertyNode) statementNode;
			}
			else
			{
				IParseNode child = statementNode.getFirstChild();

				if (child != null && child.getNodeType() == IJSNodeTypes.GET_PROPERTY)
				{
					propertyNode = (JSGetPropertyNode) child;
				}
			}
		}

		return propertyNode;
	}

	/**
	 * getGlobalScope
	 * 
	 * @param node
	 * @return
	 */
	public static JSScope getGlobalScope(IParseNode node)
	{
		JSScope result = null;

		if (node != null)
		{
			IParseNode root = node;

			while (root != null)
			{
				if (root instanceof JSParseRootNode)
				{
					result = ((JSParseRootNode) root).getGlobals();
					break;
				}
				else
				{
					root = root.getParent();
				}
			}
		}

		return result;
	}

	/**
	 * Infers types for the receiver in a getProperty call. (receiver.property)
	 * 
	 * @param projectIndex
	 * @param fileURI
	 * @param targetNode
	 * @param getPropertyNode
	 * @param offset
	 * @return
	 * FIXME This is ugly stuff. Can I refactor to what makes sense and properly holds onto an index query helper?
	 */
	public static List<String> getReceiverTypeNames(JSIndexQueryHelper queryHelper, Index projectIndex, URI fileURI, IParseNode targetNode,
			JSGetPropertyNode getPropertyNode, int offset)
	{
		List<String> result = new ArrayList<String>();

		if (getPropertyNode != null)
		{
			// collect the scope for the target node
			JSScope localScope = ParseUtil.getScopeAtOffset(targetNode, offset);

			if (localScope != null)
			{
				List<String> typeList = Collections.emptyList();

				// lookup in current file
				IParseNode lhs = getPropertyNode.getLeftHandSide();
				// Infer types for the receiver
				if (lhs instanceof JSNode)
				{
					JSNodeTypeInferrer typeWalker = new JSNodeTypeInferrer(localScope, projectIndex, fileURI, queryHelper);
					typeWalker.visit((JSNode) lhs);
					typeList = typeWalker.getTypes();
				}

				IdeLog.logInfo(JSPlugin.getDefault(),
						"types: " + StringUtil.join(", ", typeList), IDebugScopes.CONTENT_ASSIST_TYPES); //$NON-NLS-1$ //$NON-NLS-2$

				// add all properties of each type to our proposal list
				for (String type : typeList)
				{
					// Fix up type names as might be necessary
					type = JSTypeMapper.getInstance().getMappedType(type);

					// FIXME: (hopefully temporary) hack to fixup static properties on $ and jQuery
					if (JSTypeConstants.FUNCTION_JQUERY.equals(type)
							&& lhs instanceof JSIdentifierNode
							&& (JSTypeConstants.DOLLAR.equals(lhs.getText()) || JSTypeConstants.JQUERY.equals(lhs
									.getText())))
					{
						result.add(JSTypeConstants.CLASS_JQUERY);
					}
					else
					{
						result.add(type);
					}
				}
			}
		}

		return result;
	}

	/**
	 * getScopeAtOffset
	 * 
	 * @param node
	 * @param offset
	 * @return
	 */
	public static JSScope getScopeAtOffset(IParseNode node, int offset)
	{
		JSScope result = null;

		// grab global scope
		JSScope global = ParseUtil.getGlobalScope(node);

		if (global != null)
		{
			JSScope candidate = global.getScopeAtOffset(offset);

			result = (candidate != null) ? candidate : global;
		}

		return result;
	}

	/**
	 * Prevent instantiation of this class
	 */
	private ParseUtil()
	{
	}
}