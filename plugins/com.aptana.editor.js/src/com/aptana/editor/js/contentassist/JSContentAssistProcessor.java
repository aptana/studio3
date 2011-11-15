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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import beaver.Scanner;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.ILexemeProvider;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.common.outline.IParseListener;
import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.js.JSLanguageConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.index.IJSIndexConstants;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.inferencing.JSPropertyCollection;
import com.aptana.editor.js.inferencing.JSScope;
import com.aptana.editor.js.parsing.JSFlexLexemeProvider;
import com.aptana.editor.js.parsing.JSFlexScanner;
import com.aptana.editor.js.parsing.JSParseState;
import com.aptana.editor.js.parsing.ast.IJSNodeTypes;
import com.aptana.editor.js.parsing.ast.JSArgumentsNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSObjectNode;
import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.index.core.Index;
import com.aptana.parsing.IParseState;
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

	private static Set<String> AUTO_ACTIVATION_PARTITION_TYPES;

	{
		AUTO_ACTIVATION_PARTITION_TYPES = new HashSet<String>();
		AUTO_ACTIVATION_PARTITION_TYPES.add(JSSourceConfiguration.DEFAULT);
		AUTO_ACTIVATION_PARTITION_TYPES.add(IDocument.DEFAULT_CONTENT_TYPE);
	}

	private JSIndexQueryHelper _indexHelper;
	private IParseNode _targetNode;
	private IParseNode _statementNode;
	private IRange _replaceRange;
	private IParseListener _parseListener;

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

		_activeRange = activeRange;
	}

	/**
	 * JSIndexContentAssistProcessor
	 * 
	 * @param editor
	 */
	public JSContentAssistProcessor(AbstractThemeableEditor editor)
	{
		super(editor);

		_indexHelper = new JSIndexQueryHelper();
	}

	/**
	 * The currently active range
	 * 
	 * @param activeRange
	 */
	public void setActiveRange(IRange activeRange)
	{
		_activeRange = activeRange;
	}

	/**
	 * addCoreGlobals
	 * 
	 * @param proposals
	 * @param offset
	 */
	private void addCoreGlobals(Set<ICompletionProposal> proposals, int offset)
	{
		List<PropertyElement> globals = _indexHelper.getCoreGlobals();

		if (globals != null)
		{
			URI projectURI = getProjectURI();
			String location = IJSIndexConstants.CORE;

			for (PropertyElement property : globals)
			{
				String name = property.getName();
				String description = JSModelFormatter.getDescription(property, projectURI);
				Image image = JSModelFormatter.getImage(property);
				String[] userAgents = property.getUserAgentNames().toArray(new String[0]);

				addProposal(proposals, name, image, description, userAgents, location, offset);
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
		FunctionElement function = getFunctionElement(viewer, offset);

		if (function != null)
		{
			List<ParameterElement> params = function.getParameters();
			int index = getArgumentIndex(offset);

			if (0 <= index && index < params.size())
			{
				ParameterElement param = params.get(index);

				for (String type : param.getTypes())
				{
					List<PropertyElement> properties = _indexHelper.getTypeProperties(getIndex(), type);

					for (PropertyElement property : properties)
					{
						String name = property.getName();
						String description = JSModelFormatter.getDescription(property, getProjectURI());
						Image image = JSModelFormatter.getImage(property);
						List<String> userAgentNameList = property.getUserAgentNames();
						String[] userAgentNames = userAgentNameList.toArray(new String[userAgentNameList.size()]);
						String owningType = JSModelFormatter.getTypeDisplayName(property.getOwningType());

						addProposal(proposals, name, image, description, userAgentNames, owningType, offset);
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
		List<PropertyElement> projectGlobals = _indexHelper.getProjectGlobals(getIndex());

		if (projectGlobals != null && !projectGlobals.isEmpty())
		{
			String[] userAgentNames = getActiveUserAgentIds();
			URI projectURI = getProjectURI();

			for (PropertyElement property : projectGlobals)
			{
				String name = property.getName();
				String description = JSModelFormatter.getDescription(property, projectURI);
				Image image = JSModelFormatter.getImage(property);
				List<String> documents = property.getDocuments();
				String location = (documents != null && documents.size() > 0) ? JSModelFormatter
						.getDocumentDisplayName(documents.get(0)) : null;

				addProposal(proposals, name, image, description, userAgentNames, location, offset);
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
		JSGetPropertyNode node = ParseUtil.getGetPropertyNode(_targetNode, _statementNode);
		List<String> types = getParentObjectTypes(node, offset);

		// add all properties of each type to our proposal list
		for (String type : types)
		{
			addTypeProperties(proposals, type, offset);
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
			addProposal(proposals, name, JS_KEYWORD, description, getActiveUserAgentIds(),
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
			String[] userAgentIds, String fileLocation, int offset)
	{
		addProposal(proposals, name, name, image, description, userAgentIds, fileLocation, offset);
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
			String description, String[] userAgentIds, String fileLocation, int offset)
	{
		if (isActiveByUserAgent(userAgentIds))
		{
			int length = insertionText.length();

			// calculate what text will be replaced
			int replaceLength = 0;

			if (_replaceRange != null)
			{
				offset = _replaceRange.getStartingOffset(); // $codepro.audit.disable questionableAssignment
				replaceLength = _replaceRange.getLength();
			}

			// build proposal
			IContextInformation contextInfo = null;
			Image[] userAgents = UserAgentManager.getInstance().getUserAgentImages(getNatureIds(), userAgentIds);

			CommonCompletionProposal proposal = new CommonCompletionProposal(insertionText, offset, replaceLength,
					length, image, displayName, contextInfo, description);
			proposal.setFileLocation(fileLocation);
			proposal.setUserAgentImages(userAgents);
			proposal.setTriggerCharacters(getProposalTriggerCharacters());

			// add the proposal to the list
			proposals.add(proposal);
		}
	}

	/**
	 * addSymbolsInScope
	 * 
	 * @param proposals
	 */
	protected void addSymbolsInScope(Set<ICompletionProposal> proposals, int offset)
	{
		if (_targetNode != null)
		{
			JSScope globalScope = ParseUtil.getGlobalScope(_targetNode);

			if (globalScope != null)
			{
				JSScope localScope = globalScope.getScopeAtOffset(offset);

				if (localScope != null)
				{
					String fileLocation = getFilename();
					String[] userAgentNames = getActiveUserAgentIds();
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

						addProposal(proposals, name, image, description, userAgentNames, fileLocation, offset);
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
		Index index = getIndex();

		// grab all ancestors of the specified type
		List<String> allTypes = _indexHelper.getTypeAncestorNames(index, typeName);

		// include the type in the list as well
		allTypes.add(0, typeName);

		// add properties and methods
		List<PropertyElement> properties = _indexHelper.getTypeMembers(index, allTypes);

		for (PropertyElement property : properties)
		{
			String name = property.getName();
			String description = JSModelFormatter.getDescription(property, getProjectURI());
			Image image = JSModelFormatter.getImage(property);
			List<String> userAgentNameList = property.getUserAgentNames();
			String[] userAgentNames = userAgentNameList.toArray(new String[userAgentNameList.size()]);
			String owningType = JSModelFormatter.getTypeDisplayName(property.getOwningType());

			addProposal(proposals, name, image, description, userAgentNames, owningType, offset);
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
		FunctionElement function = getFunctionElement(viewer, offset);

		if (function != null)
		{
			JSArgumentsNode node = getArgumentsNode(offset);

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
	ILexemeProvider<JSTokenType> createLexemeProvider(IDocument document, int offset)
	{
		Scanner scanner = new JSFlexScanner();
		ILexemeProvider<JSTokenType> result;

		// NOTE: use active range temporarily until we get proper partitions for JS inside of HTML
		if (_activeRange != null)
		{
			result = new JSFlexLexemeProvider(document, _activeRange, scanner);
		}
		else if (_statementNode != null)
		{
			result = new JSFlexLexemeProvider(document, _statementNode, scanner);
		}
		else
		{
			result = new JSFlexLexemeProvider(document, offset, scanner);
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
		LocationType location = getLocation(document, offset);

		switch (location)
		{
			case IN_PROPERTY_NAME:
				addProperties(result, offset);
				break;

			case IN_VARIABLE_NAME:
			case IN_GLOBAL:
			case IN_CONSTRUCTOR:
				addKeywords(result, offset);
				addCoreGlobals(result, offset);
				addProjectGlobals(result, offset);
				addSymbolsInScope(result, offset);
				break;

			case IN_OBJECT_LITERAL_PROPERTY:
				addObjectLiteralProperties(result, viewer, offset);
				break;

			default:
				break;
		}

		ICompletionProposal[] resultList = result.toArray(new ICompletionProposal[result.size()]);

		// select the current proposal based on the range
		if (_replaceRange != null)
		{
			try
			{
				String prefix = document.get(_replaceRange.getStartingOffset(), _replaceRange.getLength());
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
		// (possibly) force a parse
		FileService fs = editor.getFileService();

		fs.addListener(getParseListener());
		fs.parse(new NullProgressMonitor());
		fs.removeListener(getParseListener());

		IParseNode ast = getAST();
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
		JSArgumentsNode arguments = getArgumentsNode(offset);
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
		IParseNode node = getActiveASTNode(offset);
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
		JSArgumentsNode node = getArgumentsNode(offset);
		FunctionElement result = null;

		// process arguments node as long as we're not to the left of the opening parenthesis
		if (node != null)
		{
			// save current replace range. A bit hacky but better than adding a flag into getLocation's signature
			IRange range = _replaceRange;

			// grab the content assist location type for the symbol before the arguments list
			int functionOffset = node.getStartingOffset();
			LocationType location = getLocation(viewer.getDocument(), functionOffset);

			// restore replace range
			_replaceRange = range;

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
					JSGetPropertyNode propertyNode = ParseUtil.getGetPropertyNode(node,
							((JSNode) node).getContainingStatementNode());
					List<String> types = getParentObjectTypes(propertyNode, offset);

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
				PropertyElement property = _indexHelper.getTypeMember(getIndex(), typeName, methodName);

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
		JSLocationIdentifier identifier = new JSLocationIdentifier(offset, getActiveASTNode(offset - 1));
		LocationType result = identifier.getType();

		_targetNode = identifier.getTargetNode();
		_statementNode = identifier.getStatementNode();
		_replaceRange = identifier.getReplaceRange();

		// if we couldn't determine the location type with the AST, then
		// fallback to using lexemes
		if (result == LocationType.UNKNOWN)
		{
			// NOTE: this method call sets replaceRange as a side-effect
			result = getLocationByLexeme(document, offset);
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
		ILexemeProvider<JSTokenType> lexemeProvider = createLexemeProvider(document, offset);

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
		return ParseUtil.getParentObjectTypes(getIndex(), getURI(), _targetNode, node, offset);
	}

	/**
	 * Expose replace range field for unit tests
	 * 
	 * @return
	 */
	IRange getReplaceRange()
	{
		return _replaceRange;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#triggerAdditionalAutoActivation(char, int,
	 * org.eclipse.jface.text.IDocument, int)
	 */
	public boolean isValidAutoActivationLocation(char c, int keyCode, IDocument document, int offset)
	{
		// NOTE: If auto-activation logic changes it may be necessary to change this logic
		// to continue walking backwards through partitions until a) a valid activation character
		// or b) a non-whitespace non-valid activation character is encountered. That implementation
		// would need to skip partitions that are effectively whitespace, for example, comment
		// partitions
		boolean result = false;

		try
		{
			ITypedRegion partition = document.getPartition(offset);

			if (partition != null && AUTO_ACTIVATION_PARTITION_TYPES.contains(partition.getType()))
			{
				int start = partition.getOffset();
				int index = offset - 1;

				while (index >= start)
				{
					char candidate = document.getChar(index);

					if (candidate == ',' || candidate == '(')
					{
						result = true;
						break;
					}
					else if (!Character.isWhitespace(candidate))
					{
						break;
					}

					index--;
				}
			}
		}
		catch (BadLocationException e)
		{
			// ignore
		}

		return result;
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

	/**
	 * getParseListener
	 * 
	 * @return
	 */
	protected IParseListener getParseListener()
	{
		if (_parseListener == null)
		{
			_parseListener = new IParseListener()
			{
				public void beforeParse(IParseState parseState)
				{
					// NOTE: We turn off all comment processing if we have a JSParseState associated with this file's
					// FileService.
					// If a previous parse included comment processing, that's fine as well
					if (parseState instanceof JSParseState)
					{
						JSParseState jsParseState = (JSParseState) parseState;

						// save old settings
						jsParseState.pushCommentContext();

						// turn off all comment processing
						jsParseState.setAttachComments(false);
						jsParseState.setCollectComments(false);
					}
				}

				public void parseCompletedSuccessfully()
				{
				}

				public void afterParse(IParseState parseState)
				{
					if (parseState instanceof JSParseState)
					{
						JSParseState jsParseState = (JSParseState) parseState;

						jsParseState.popCommentContext();
					}
				}
			};
		}

		return _parseListener;
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
