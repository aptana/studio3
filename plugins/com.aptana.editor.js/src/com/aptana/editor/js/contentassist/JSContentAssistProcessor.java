/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.js.JSLanguageConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.index.IJSIndexConstants;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.inferencing.JSPropertyCollection;
import com.aptana.editor.js.inferencing.JSScope;
import com.aptana.editor.js.parsing.JSTokenScanner;
import com.aptana.editor.js.parsing.ast.IJSNodeTypes;
import com.aptana.editor.js.parsing.ast.JSArgumentsNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSObjectNode;
import com.aptana.editor.js.parsing.lexer.JSLexemeProvider;
import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.index.core.Index;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;

public class JSContentAssistProcessor extends CommonContentAssistProcessor
{
	private static final Image JS_FUNCTION = JSPlugin.getImage("/icons/js_function.png"); //$NON-NLS-1$
	private static final Image JS_PROPERTY = JSPlugin.getImage("/icons/js_property.png"); //$NON-NLS-1$
	private static final Image JS_KEYWORD = JSPlugin.getImage("/icons/keyword.png"); //$NON-NLS-1$

	private static String[] KEYWORDS = ArrayUtil.flatten(JSLanguageConstants.KEYWORD_OPERATORS,
			JSLanguageConstants.GRAMMAR_KEYWORDS, JSLanguageConstants.KEYWORD_CONTROL);

	private JSIndexQueryHelper _indexHelper;
	private IParseNode _targetNode;
	private IParseNode _statementNode;
	private IRange _replaceRange;

	// NOTE: temp (I hope) until we get proper partitions for JS inside of HTML
	private IRange _activeRange;

	/**
	 * JSContentAssistProcessor
	 * 
	 * @param editor
	 * @param activeRange
	 */
	public JSContentAssistProcessor(AbstractThemeableEditor editor, IRange activeRange)
	{
		this(editor);

		this._activeRange = activeRange;
	}

	/**
	 * JSIndexContentAssistProcessor
	 * 
	 * @param editor
	 */
	public JSContentAssistProcessor(AbstractThemeableEditor editor)
	{
		super(editor);

		this._indexHelper = new JSIndexQueryHelper();
	}

	/**
	 * The currently active range
	 * 
	 * @param activeRange
	 */
	public void setActiveRange(IRange activeRange)
	{
		this._activeRange = activeRange;
	}

	/**
	 * addCoreGlobals
	 * 
	 * @param proposals
	 * @param offset
	 */
	private void addCoreGlobals(Set<ICompletionProposal> proposals, int offset)
	{
		List<PropertyElement> globals = this._indexHelper.getCoreGlobals();

		if (globals != null)
		{
			URI projectURI = this.getProjectURI();
			String location = IJSIndexConstants.CORE;

			for (PropertyElement property : globals)
			{
				String name = property.getName();
				String description = JSModelFormatter.getDescription(property, projectURI);
				Image image = JSModelFormatter.getImage(property);
				Image[] userAgents = this.getUserAgentImages(property.getUserAgentNames());

				this.addProposal(proposals, name, image, description, userAgents, location, offset);
			}
		}
	}

	/**
	 * addObjectLiteralProperties
	 * 
	 * @param proposals
	 * @param offset
	 */
	protected void addObjectLiteralProperties(Set<ICompletionProposal> proposals, ITextViewer viewer, int offset)
	{
		FunctionElement function = this.getFunctionElement(viewer, offset);

		if (function != null)
		{
			List<ParameterElement> params = function.getParameters();
			int index = this.getArgumentIndex(offset);

			if (0 <= index && index < params.size())
			{
				ParameterElement param = params.get(index);

				for (String type : param.getTypes())
				{
					List<PropertyElement> properties = this._indexHelper.getTypeProperties(this.getIndex(), type);

					for (PropertyElement property : properties)
					{
						String name = property.getName();
						String description = JSModelFormatter.getDescription(property, this.getProjectURI());
						Image image = JSModelFormatter.getImage(property);
						List<String> userAgentNames = property.getUserAgentNames();
						Image[] userAgents = getUserAgentImages(userAgentNames);
						String owningType = JSModelFormatter.getTypeDisplayName(property.getOwningType());

						this.addProposal(proposals, name, image, description, userAgents, owningType, offset);
					}
				}
			}
		}
	}

	/**
	 * addProjectGlobalFunctions
	 * 
	 * @param proposals
	 * @param offset
	 */
	private void addProjectGlobals(Set<ICompletionProposal> proposals, int offset)
	{
		List<PropertyElement> projectGlobals = this._indexHelper.getProjectGlobals(this.getIndex());

		if (projectGlobals != null && !projectGlobals.isEmpty())
		{
			Image[] userAgents = this.getAllUserAgentIcons();
			URI projectURI = this.getProjectURI();

			for (PropertyElement property : projectGlobals)
			{
				String name = property.getName();
				String description = JSModelFormatter.getDescription(property, projectURI);
				Image image = JSModelFormatter.getImage(property);
				List<String> documents = property.getDocuments();
				String location = (documents != null && documents.size() > 0) ? JSModelFormatter
						.getDocumentDisplayName(documents.get(0)) : null;

				this.addProposal(proposals, name, image, description, userAgents, location, offset);
			}
		}
	}

	/**
	 * addProperties
	 * 
	 * @param proposals
	 * @param offset
	 */
	protected void addProperties(Set<ICompletionProposal> proposals, int offset)
	{
		JSGetPropertyNode node = ASTUtil.getGetPropertyNode(this._targetNode, this._statementNode);
		List<String> types = this.getParentObjectTypes(node, offset);

		// add all properties of each type to our proposal list
		for (String type : types)
		{
			this.addTypeProperties(proposals, type, offset);
		}
	}

	/**
	 * @param prefix
	 * @param completionProposals
	 */
	private void addKeywords(Set<ICompletionProposal> proposals, int offset)
	{
		for (String name : KEYWORDS)
		{
			String description = StringUtil.format(Messages.JSContentAssistProcessor_KeywordDescription, name);
			this.addProposal(proposals, name, JS_KEYWORD, description, this.getAllUserAgentIcons(),
					Messages.JSContentAssistProcessor_KeywordLocation, offset);
		}
	}

	/**
	 * addProposal - The display name is used as the insertion text
	 * 
	 * @param proposals
	 * @param name
	 * @param image
	 * @param description
	 * @param userAgents
	 * @param fileLocation
	 * @param offset
	 */
	private void addProposal(Set<ICompletionProposal> proposals, String name, Image image, String description,
			Image[] userAgents, String fileLocation, int offset)
	{
		this.addProposal(proposals, name, name, image, description, userAgents, fileLocation, offset);
	}

	/**
	 * addProposal - The display name and insertion text are defined separately
	 * 
	 * @param proposals
	 * @param displayName
	 * @param insertionText
	 * @param image
	 * @param description
	 * @param userAgents
	 * @param fileLocation
	 * @param offset
	 */
	private void addProposal(Set<ICompletionProposal> proposals, String displayName, String insertionText, Image image,
			String description, Image[] userAgents, String fileLocation, int offset)
	{
		int length = insertionText.length();

		// calculate what text will be replaced
		int replaceLength = 0;

		if (this._replaceRange != null)
		{
			offset = this._replaceRange.getStartingOffset(); // $codepro.audit.disable questionableAssignment
			replaceLength = this._replaceRange.getLength();
		}

		// build proposal
		IContextInformation contextInfo = null;

		CommonCompletionProposal proposal = new CommonCompletionProposal(insertionText, offset, replaceLength, length,
				image, displayName, contextInfo, description);
		proposal.setFileLocation(fileLocation);
		proposal.setUserAgentImages(userAgents);
		proposal.setTriggerCharacters(getProposalTriggerCharacters());

		// add the proposal to the list
		proposals.add(proposal);
	}

	/**
	 * addSymbolsInScope
	 * 
	 * @param proposals
	 */
	protected void addSymbolsInScope(Set<ICompletionProposal> proposals, int offset)
	{
		if (this._targetNode != null)
		{
			JSScope globalScope = ASTUtil.getGlobalScope(this._targetNode);

			if (globalScope != null)
			{
				JSScope localScope = globalScope.getScopeAtOffset(offset);

				if (localScope != null)
				{
					String fileLocation = this.getFilename();
					Image[] userAgents = this.getAllUserAgentIcons();
					List<String> symbols = localScope.getSymbolNames();

					for (String symbol : symbols)
					{
						boolean isFunction = false;
						JSPropertyCollection object = localScope.getSymbol(symbol);
						List<JSNode> nodes = object.getValues();

						if (nodes != null)
						{
							for (JSNode node : nodes)
							{
								if (node instanceof JSFunctionNode)
								{
									isFunction = true;
									break;
								}
							}
						}

						String name = symbol;
						String description = null;
						Image image = (isFunction) ? JS_FUNCTION : JS_PROPERTY;

						this.addProposal(proposals, name, image, description, userAgents, fileLocation, offset);
					}
				}
			}
		}
	}

	/**
	 * addTypeProperties
	 * 
	 * @param proposals
	 * @param typeName
	 * @param offset
	 */
	protected void addTypeProperties(Set<ICompletionProposal> proposals, String typeName, int offset)
	{
		Index index = this.getIndex();

		// grab all ancestors of the specified type
		List<String> allTypes = this._indexHelper.getTypeAncestorNames(index, typeName);

		// include the type in the list as well
		allTypes.add(0, typeName);

		// add properties and methods
		List<PropertyElement> properties = this._indexHelper.getTypeMembers(index, allTypes);

		for (PropertyElement property : properties)
		{
			String name = property.getName();
			String description = JSModelFormatter.getDescription(property, this.getProjectURI());
			Image image = JSModelFormatter.getImage(property);
			List<String> userAgentNames = property.getUserAgentNames();
			Image[] userAgents = getUserAgentImages(userAgentNames);
			String owningType = JSModelFormatter.getTypeDisplayName(property.getOwningType());

			this.addProposal(proposals, name, image, description, userAgents, owningType, offset);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.CommonContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer
	 * , int)
	 */
	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset)
	{
		List<IContextInformation> result = new ArrayList<IContextInformation>();
		FunctionElement function = this.getFunctionElement(viewer, offset);

		if (function != null)
		{
			JSArgumentsNode node = this.getArgumentsNode(offset);

			if (node != null)
			{
				boolean inObjectLiteral = false;

				// find argument we're in
				for (IParseNode arg : node)
				{
					if (arg.contains(offset))
					{
						// Not foolproof, but this should cover 99% of the cases we're likely to encounter
						inObjectLiteral = (arg instanceof JSObjectNode);
						break;
					}
				}

				// prevent context info popup from appearing and immediately disappearing
				if (!inObjectLiteral)
				{
					String info = JSModelFormatter.getContextInfo(function);
					List<String> lines = JSModelFormatter.getContextLines(function);
					IContextInformation ci = new JSContextInformation(info, lines, node.getStartingOffset());

					result.add(ci);
				}
			}
		}

		return result.toArray(new IContextInformation[result.size()]);
	}

	/**
	 * createLexemeProvider
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	LexemeProvider<JSTokenType> createLexemeProvider(IDocument document, int offset)
	{
		LexemeProvider<JSTokenType> result;

		// NOTE: temp until we get proper partitions for JS inside of HTML
		if (this._activeRange != null)
		{
			result = new JSLexemeProvider(document, this._activeRange, new JSTokenScanner());
		}
		else if (this._statementNode != null)
		{
			result = new JSLexemeProvider(document, this._statementNode, new JSTokenScanner());
		}
		else
		{
			result = new JSLexemeProvider(document, offset, new JSTokenScanner());
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.CommonContentAssistProcessor#doComputeCompletionProposals(org.eclipse.jface.text.ITextViewer
	 * , int, char, boolean)
	 */
	@Override
	protected ICompletionProposal[] doComputeCompletionProposals(ITextViewer viewer, int offset, char activationChar,
			boolean autoActivated)
	{
		IDocument document = viewer.getDocument();
		Set<ICompletionProposal> result = new HashSet<ICompletionProposal>();

		// first step is to determine where we are
		LocationType location = this.getLocation(document, offset);

		switch (location)
		{
			case IN_PROPERTY_NAME:
				this.addProperties(result, offset);
				break;

			case IN_VARIABLE_NAME:
			case IN_GLOBAL:
			case IN_CONSTRUCTOR:
				this.addKeywords(result, offset);
				this.addCoreGlobals(result, offset);
				this.addProjectGlobals(result, offset);
				this.addSymbolsInScope(result, offset);
				break;

			case IN_OBJECT_LITERAL_PROPERTY:
				this.addObjectLiteralProperties(result, viewer, offset);
				break;

			default:
				break;
		}

		ICompletionProposal[] resultList = result.toArray(new ICompletionProposal[result.size()]);

		// select the current proposal based on the range
		if (this._replaceRange != null)
		{
			try
			{
				String prefix = document.get(this._replaceRange.getStartingOffset(), this._replaceRange.getLength());
				setSelectedProposal(prefix, resultList);
			}
			catch (BadLocationException e) // $codepro.audit.disable emptyCatchClause
			{
				// ignore
			}
		}

		return resultList;
	}

	/**
	 * getActiveASTNode
	 * 
	 * @param offset
	 * @return
	 */
	IParseNode getActiveASTNode(int offset)
	{
		// force a parse
		editor.getFileService().parse();

		IParseNode ast = this.getAST();
		IParseNode result = null;

		if (ast != null)
		{
			result = ast.getNodeAtOffset(offset);

			// We won't get a current node if the cursor is outside of the positions
			// recorded by the AST
			if (result == null)
			{
				if (offset < ast.getStartingOffset())
				{
					result = ast.getNodeAtOffset(ast.getStartingOffset());
				}
				else if (ast.getEndingOffset() < offset)
				{
					result = ast.getNodeAtOffset(ast.getEndingOffset());
				}
			}
		}

		return result;
	}

	/**
	 * getArgumentIndex
	 * 
	 * @param offset
	 * @return
	 */
	private int getArgumentIndex(int offset)
	{
		JSArgumentsNode arguments = this.getArgumentsNode(offset);
		int result = -1;

		if (arguments != null)
		{
			for (IParseNode child : arguments)
			{
				if (child.contains(offset))
				{
					result = child.getIndex();
					break;
				}
			}
		}

		return result;
	}

	/**
	 * getArgumentsNode
	 * 
	 * @param offset
	 * @return
	 */
	private JSArgumentsNode getArgumentsNode(int offset)
	{
		IParseNode node = this.getActiveASTNode(offset);
		JSArgumentsNode result = null;

		// work a way up the AST to determine if we're in an arguments node
		while (node instanceof JSNode && node.getNodeType() != IJSNodeTypes.ARGUMENTS)
		{
			node = node.getParent();
		}

		// process arguments node as long as we're not to the left of the opening parenthesis
		if (node instanceof JSNode && node.getNodeType() == IJSNodeTypes.ARGUMENTS
				&& node.getStartingOffset() != offset)
		{
			result = (JSArgumentsNode) node;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getContextInformationValidator()
	 */
	@Override
	public IContextInformationValidator getContextInformationValidator()
	{
		return new JSContextInformationValidator();
	}

	/**
	 * getFunctionElement
	 * 
	 * @param viewer
	 * @param offset
	 * @return
	 */
	private FunctionElement getFunctionElement(ITextViewer viewer, int offset)
	{
		JSArgumentsNode node = this.getArgumentsNode(offset);
		FunctionElement result = null;

		// process arguments node as long as we're not to the left of the opening parenthesis
		if (node != null)
		{
			// save current replace range. A bit hacky but better than adding a flag into getLocation's signature
			IRange range = this._replaceRange;

			// grab the content assist location type for the symbol before the arguments list
			int functionOffset = node.getStartingOffset();
			LocationType location = this.getLocation(viewer.getDocument(), functionOffset);

			// restore replace range
			this._replaceRange = range;

			// init type and method names
			String typeName = null;
			String methodName = null;

			switch (location)
			{
				case IN_VARIABLE_NAME:
				{
					typeName = JSTypeConstants.WINDOW_TYPE;
					methodName = node.getParent().getFirstChild().getText();
					break;
				}

				case IN_PROPERTY_NAME:
				{
					JSGetPropertyNode propertyNode = ASTUtil.getGetPropertyNode(node,
							((JSNode) node).getContainingStatementNode());
					List<String> types = this.getParentObjectTypes(propertyNode, offset);

					if (types.size() > 0)
					{
						typeName = types.get(0);
						methodName = propertyNode.getLastChild().getText();
					}
					break;
				}

				default:
					break;
			}

			if (typeName != null && methodName != null)
			{
				PropertyElement property = this._indexHelper.getTypeMember(this.getIndex(), typeName, methodName);

				if (property instanceof FunctionElement)
				{
					result = (FunctionElement) property;
				}
			}
		}

		return result;
	}

	/**
	 * getLocation
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	LocationType getLocation(IDocument document, int offset)
	{
		JSLocationIdentifier identifier = new JSLocationIdentifier(offset, this.getActiveASTNode(offset - 1));
		LocationType result = identifier.getType();

		this._targetNode = identifier.getTargetNode();
		this._statementNode = identifier.getStatementNode();
		this._replaceRange = identifier.getReplaceRange();

		// if we couldn't determine the location type with the AST, then
		// fallback to using lexemes
		if (result == LocationType.UNKNOWN)
		{
			// NOTE: this method call sets replaceRange as a side-effect
			result = this.getLocationByLexeme(document, offset);
		}

		return result;
	}

	/**
	 * getLocationByLexeme
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	LocationType getLocationByLexeme(IDocument document, int offset)
	{
		// grab relevant lexemes around the current offset
		LexemeProvider<JSTokenType> lexemeProvider = this.createLexemeProvider(document, offset);

		// assume we can't determine the location type
		LocationType result = LocationType.UNKNOWN;

		// find lexeme nearest to our offset
		int index = lexemeProvider.getLexemeIndex(offset);

		if (index < 0)
		{
			int candidateIndex = lexemeProvider.getLexemeFloorIndex(offset);
			Lexeme<JSTokenType> lexeme = lexemeProvider.getLexeme(candidateIndex);

			if (lexeme != null)
			{
				if (lexeme.getEndingOffset() == offset)
				{
					index = candidateIndex;
				}
				else if (lexeme.getType() == JSTokenType.NEW)
				{
					index = candidateIndex;
				}
			}
		}

		if (index >= 0)
		{
			Lexeme<JSTokenType> lexeme = lexemeProvider.getLexeme(index);

			switch (lexeme.getType())
			{
				case DOT:
					result = LocationType.IN_PROPERTY_NAME;
					break;

				case SEMICOLON:
					if (index > 0)
					{
						Lexeme<JSTokenType> previousLexeme = lexemeProvider.getLexeme(index - 1);

						switch (previousLexeme.getType())
						{
							case IDENTIFIER:
								result = LocationType.IN_GLOBAL;
								break;

							default:
								break;
						}
					}
					break;

				case LPAREN:
					if (offset == lexeme.getEndingOffset())
					{
						Lexeme<JSTokenType> previousLexeme = lexemeProvider.getLexeme(index - 1);

						if (previousLexeme.getType() != JSTokenType.IDENTIFIER)
						{
							result = LocationType.IN_GLOBAL;
						}
					}
					break;

				case RPAREN:
					if (offset == lexeme.getStartingOffset())
					{
						result = LocationType.IN_GLOBAL;
					}
					break;

				case IDENTIFIER:
					if (index > 0)
					{
						Lexeme<JSTokenType> previousLexeme = lexemeProvider.getLexeme(index - 1);

						switch (previousLexeme.getType())
						{
							case DOT:
								result = LocationType.IN_PROPERTY_NAME;
								break;

							case NEW:
								result = LocationType.IN_CONSTRUCTOR;
								break;

							default:
								result = LocationType.IN_VARIABLE_NAME;
								break;
						}
					}
					else
					{
						result = LocationType.IN_VARIABLE_NAME;
					}
					break;

				default:
					break;
			}
		}
		else if (lexemeProvider.size() == 0)
		{
			result = LocationType.IN_GLOBAL;
		}

		return result;
	}

	/**
	 * getParentObjectTypes
	 * 
	 * @param node
	 * @param offset
	 * @return
	 */
	protected List<String> getParentObjectTypes(JSGetPropertyNode node, int offset)
	{
		return ASTUtil.getParentObjectTypes(this.getIndex(), this.getURI(), this._targetNode, node, offset);
	}

	/**
	 * Expose replace range field for unit tests
	 * 
	 * @return
	 */
	IRange getReplaceRange()
	{
		return this._replaceRange;
	}

	/**
	 * getUserAgentImages
	 * 
	 * @param userAgentNames
	 * @return
	 */
	protected Image[] getUserAgentImages(List<String> userAgentNames)
	{
		return UserAgentManager.getInstance().getUserAgentImages(userAgentNames);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#triggerAdditionalAutoActivation(char, int,
	 * org.eclipse.jface.text.IDocument, int)
	 */
	public boolean isValidAutoActivationLocation(char c, int keyCode, IDocument document, int offset)
	{
		JSTokenType[] types = new JSTokenType[] { JSTokenType.LPAREN, JSTokenType.COMMA };
		Arrays.sort(types);
		LexemeProvider<JSTokenType> lexemeProvider = this.createLexemeProvider(document, offset);
		if (offset > 0)
		{
			Lexeme<JSTokenType> lexeme = lexemeProvider.getFloorLexeme(offset - 1);
			return (lexeme != null) ? Arrays.binarySearch(types, lexeme.getType()) >= 0 : false;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#isValidIdentifier(char, int)
	 */
	public boolean isValidIdentifier(char c, int keyCode)
	{
		return Character.isJavaIdentifierStart(c) || Character.isJavaIdentifierPart(c) || c == '$';
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#isValidActivationCharacter(char, int)
	 */
	public boolean isValidActivationCharacter(char c, int keyCode)
	{
		return Character.isWhitespace(c);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getPreferenceNodeQualifier()
	 */
	protected String getPreferenceNodeQualifier()
	{
		return JSPlugin.PLUGIN_ID;
	}
}
