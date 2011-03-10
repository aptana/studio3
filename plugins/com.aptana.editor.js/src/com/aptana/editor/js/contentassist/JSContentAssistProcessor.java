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
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.index.JSIndexConstants;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.inferencing.JSNodeTypeInferrer;
import com.aptana.editor.js.inferencing.JSPropertyCollection;
import com.aptana.editor.js.inferencing.JSScope;
import com.aptana.editor.js.inferencing.JSTypeMapper;
import com.aptana.editor.js.inferencing.JSTypeUtil;
import com.aptana.editor.js.parsing.JSTokenScanner;
import com.aptana.editor.js.parsing.ast.JSArgumentsNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.editor.js.parsing.ast.JSObjectNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.editor.js.parsing.lexer.JSLexemeProvider;
import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.editor.js.preferences.IPreferenceConstants;
import com.aptana.index.core.Index;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;

public class JSContentAssistProcessor extends CommonContentAssistProcessor
{
	private static final Image JS_FUNCTION = JSPlugin.getImage("/icons/js_function.png"); //$NON-NLS-1$
	private static final Image JS_PROPERTY = JSPlugin.getImage("/icons/js_property.png"); //$NON-NLS-1$
	private static final EnumSet<LocationType> IGNORED_TYPES = EnumSet.of(LocationType.UNKNOWN, LocationType.NONE);

	private JSIndexQueryHelper _indexHelper;
	private IParseNode _targetNode;
	private IParseNode _statementNode;
	private IRange _replaceRange;

	/**
	 * JSIndexContentAssitProcessor
	 * 
	 * @param editor
	 */
	public JSContentAssistProcessor(AbstractThemeableEditor editor)
	{
		super(editor);

		this._indexHelper = new JSIndexQueryHelper();
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
			String location = JSIndexConstants.CORE;

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

		if (projectGlobals != null && projectGlobals.isEmpty() == false)
		{
			Image[] userAgents = this.getAllUserAgentIcons();
			URI projectURI = this.getProjectURI();

			for (PropertyElement property : projectGlobals)
			{
				String name = property.getName();
				String description = JSModelFormatter.getDescription(property, projectURI);
				Image image = JSModelFormatter.getImage(property);
				List<String> documents = property.getDocuments();
				String location = (documents != null && documents.size() > 0) ? JSModelFormatter.getDocumentDisplayName(documents.get(0)) : null;

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
		JSGetPropertyNode node = this.getGetPropertyNode(this._targetNode, this._statementNode);
		List<String> types = this.getParentObjectTypes(node, offset);

		// add all properties of each type to our proposal list
		for (String type : types)
		{
			this.addTypeProperties(proposals, type, offset);
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
			offset = this._replaceRange.getStartingOffset();
			replaceLength = this._replaceRange.getLength();
		}

		// build proposal
		IContextInformation contextInfo = null;

		CommonCompletionProposal proposal = new CommonCompletionProposal(insertionText, offset, replaceLength, length,
				image, displayName, contextInfo, description);
		proposal.setFileLocation(fileLocation);
		proposal.setUserAgentImages(userAgents);

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
			JSScope globalScope = this.getGlobalScope();

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

		typeName = JSModelFormatter.getTypeDisplayName(typeName);

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
				if (inObjectLiteral == false)
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

		if (this._statementNode != null)
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

		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>(result);

		// sort by display name
		Collections.sort(proposals, new Comparator<ICompletionProposal>()
		{
			public int compare(ICompletionProposal o1, ICompletionProposal o2)
			{
				return o1.getDisplayString().compareToIgnoreCase(o2.getDisplayString());
			}
		});

		// select the current proposal based on the range
		if (this._replaceRange != null)
		{
			try
			{
				String prefix = document.get(this._replaceRange.getStartingOffset(), this._replaceRange.getLength());

				this.setSelectedProposal(prefix, proposals);
			}
			catch (BadLocationException e)
			{
				// ignore
			}
		}

		return proposals.toArray(new ICompletionProposal[proposals.size()]);
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

			// We won't get a current node if the cursor is after the last position
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
		while (node instanceof JSNode && node.getNodeType() != JSNodeTypes.ARGUMENTS)
		{
			node = node.getParent();
		}

		// process arguments node as long as we're not to the left of the opening parenthesis
		if (node instanceof JSNode && node.getNodeType() == JSNodeTypes.ARGUMENTS && node.getStartingOffset() != offset)
		{
			result = (JSArgumentsNode) node;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	@Override
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		String chars = Platform.getPreferencesService().getString( //
			JSPlugin.PLUGIN_ID, //
			IPreferenceConstants.JS_ACTIVATION_CHARACTERS, //
			"", //$NON-NLS-1$
			null //
			);

		return (chars != null) ? chars.toCharArray() : null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	@Override
	public char[] getContextInformationAutoActivationCharacters()
	{
		return new char[] { '(', ',' };
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
					JSGetPropertyNode propertyNode = this.getGetPropertyNode(node, ((JSNode) node).getContainingStatementNode());
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
	 * getGetPropertyNode
	 * 
	 * @param targetNode
	 * @param statementNode
	 * @return
	 */
	private JSGetPropertyNode getGetPropertyNode(IParseNode targetNode, IParseNode statementNode)
	{
		JSGetPropertyNode propertyNode = null;

		if (targetNode != null)
		{
			if (targetNode.getNodeType() == JSNodeTypes.GET_PROPERTY)
			{
				propertyNode = (JSGetPropertyNode) targetNode;
			}
			else
			{
				IParseNode parentNode = targetNode.getParent();

				if (parentNode != null && parentNode.getNodeType() == JSNodeTypes.GET_PROPERTY)
				{
					propertyNode = (JSGetPropertyNode) parentNode;
				}
			}
		}

		if (propertyNode == null && statementNode != null)
		{
			if (statementNode.getNodeType() == JSNodeTypes.GET_PROPERTY)
			{
				propertyNode = (JSGetPropertyNode) statementNode;
			}
			else
			{
				IParseNode child = statementNode.getFirstChild();

				if (child != null && child.getNodeType() == JSNodeTypes.GET_PROPERTY)
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
	 * @return
	 */
	protected JSScope getGlobalScope()
	{
		JSScope result = null;

		if (this._targetNode != null)
		{
			IParseNode root = this._targetNode;

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
	 * getLocation
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	LocationType getLocation(IDocument document, int offset)
	{
		LocationType result = LocationType.UNKNOWN;

		// set up references to AST nodes around the current offset
		this._targetNode = this.getActiveASTNode(offset - 1);
		this._statementNode = null;
		IParseNode ast = null;

		if (this._targetNode instanceof JSParseRootNode)
		{
			this._statementNode = this._targetNode;
			ast = this._targetNode;
		}
		else if (this._targetNode instanceof JSNode)
		{
			// set containing statement
			this._statementNode = ((JSNode) this._targetNode).getContainingStatementNode();

			// NOTE: We can't simply grab the AST since this will fail with JS
			// is embedded in other languages. In those cases, we'll get the
			// root node for the host language and not for JS

			// find JS root node
			IParseNode current = this._targetNode;

			while (current != null)
			{
				if (current instanceof JSParseRootNode)
				{
					ast = current;
					break;
				}
				else
				{
					current = current.getParent();
				}
			}
		}

		// try to determine the current offset's CA type via the AST
		if (ast == null)
		{
			result = LocationType.IN_GLOBAL;

			this._replaceRange = new Range(offset, offset - 1);
		}
		else if (ast instanceof JSParseRootNode)
		{
			JSLocationIdentifier typeWalker = new JSLocationIdentifier(offset);

			((JSParseRootNode) ast).accept(typeWalker);

			result = typeWalker.getType();

			if (IGNORED_TYPES.contains(result) == false)
			{
				JSRangeFinder rangeWalker = new JSRangeFinder(offset);

				((JSParseRootNode) ast).accept(rangeWalker);

				this._replaceRange = rangeWalker.getRange();
			}
		}

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
		List<String> result = new ArrayList<String>();

		if (node != null)
		{
			JSScope localScope = this.getScopeAtOffset(offset);

			if (localScope != null)
			{
				List<String> typeList = Collections.emptyList();

				// lookup in current file
				IParseNode lhs = node.getLeftHandSide();

				if (lhs instanceof JSNode)
				{
					JSNodeTypeInferrer typeWalker = new JSNodeTypeInferrer(localScope, this.getIndex(), this.getURI());

					typeWalker.visit((JSNode) lhs);

					typeList = typeWalker.getTypes();
				}

				// TEMP: Show types for debugging info
				if (Platform.inDevelopmentMode())
				{
					System.out.println("types: " + StringUtil.join(", ", typeList)); //$NON-NLS-1$ //$NON-NLS-2$
				}

				// add all properties of each type to our proposal list
				for (String type : typeList)
				{
					// Fix up type names as might be necessary
					type = JSTypeMapper.getInstance().getMappedType(type);

					if (JSTypeUtil.isFunctionPrefix(type))
					{
						String functionType = JSTypeUtil.getFunctionSignatureType(type);

						result.add(functionType);
					}
					else if (type.startsWith(JSTypeConstants.GENERIC_ARRAY_OPEN))
					{
						result.add(JSTypeConstants.ARRAY_TYPE);
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
	 * @param offset
	 * @return
	 */
	protected JSScope getScopeAtOffset(int offset)
	{
		JSScope result = null;

		// grab global scope
		JSScope global = this.getGlobalScope();

		if (global != null)
		{
			JSScope candidate = global.getScopeAtOffset(offset);

			result = (candidate != null) ? candidate : global;
		}

		return result;
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
	public boolean triggerAdditionalAutoActivation(char c, int keyCode, IDocument document, int offset)
	{
		LexemeProvider<JSTokenType> lexemeProvider = this.createLexemeProvider(document, offset);
		int index = lexemeProvider.getLexemeFloorIndex(offset);
		boolean result = false;

		// NOTE: This is not foolproof. Basically, we assume object literals in function invocations will not
		// have function values. This should be a valid assumption since we only show non-function properties
		// in object-literal CA. If assumption is true, then we can look for the opening curly or stop when he
		// hit certain keywords or punctuation. A robust implementation would use the AST.
		if (index != -1)
		{
			Lexeme<JSTokenType> currentLexeme = lexemeProvider.getLexeme(index);

			boolean isIdentifier = (currentLexeme.getType() == JSTokenType.IDENTIFIER);
			boolean inObjectLiteral = false;
			index--;

			LOOP: while (index >= 0)
			{
				currentLexeme = lexemeProvider.getLexeme(index);

				switch (currentLexeme.getType())
				{
					case LCURLY:
						inObjectLiteral = true;
						break LOOP;

					// abort on some keywords
					case BREAK:
					case CASE:
					case CONTINUE:
					case DEFAULT:
					case DO:
					case ELSE:
					case FINALLY:
					case FUNCTION:
					case FOR:
					case IF:
					case IN:
					case RETURN:
					case SWITCH:
					case THROW:
					case TRY:
					case WHILE:
						// and abort on some punctuation
					case COLON:
					case RCURLY:
					case RPAREN:
					case SEMICOLON:
					case VAR:
						break LOOP;
				}

				index--;
			}

			result = (inObjectLiteral == false && isIdentifier);
		}

		return result;
	}
}
