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
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.index.JSIndexConstants;
import com.aptana.editor.js.contentassist.model.ContentSelector;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.inferencing.JSNodeTypeInferrer;
import com.aptana.editor.js.inferencing.JSPropertyCollection;
import com.aptana.editor.js.inferencing.JSScope;
import com.aptana.editor.js.inferencing.JSSymbolCollector;
import com.aptana.editor.js.parsing.JSTokenScanner;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.editor.js.parsing.lexer.JSLexemeProvider;
import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.index.core.Index;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;

public class JSContentAssistProcessor extends CommonContentAssistProcessor
{
	private static final Image JS_FUNCTION = Activator.getImage("/icons/js_function.gif"); //$NON-NLS-1$
	private static final Image JS_PROPERTY = Activator.getImage("/icons/js_property.gif"); //$NON-NLS-1$

	private static final EnumSet<ContentSelector> CORE_GLOBAL_SELECTOR = EnumSet.of(ContentSelector.NAME, //
		ContentSelector.DESCRIPTION, //
		ContentSelector.EXAMPLES, //
		ContentSelector.PARAMETERS, //
		ContentSelector.RETURN_TYPES, //
		ContentSelector.SINCE, //
		ContentSelector.TYPES, //
		ContentSelector.USER_AGENTS //
		);
	private static final EnumSet<ContentSelector> PROJECT_GLOBAL_SELECTOR = EnumSet.of(ContentSelector.NAME, //
		ContentSelector.DESCRIPTION, //
		ContentSelector.DOCUMENTS, //
		ContentSelector.EXAMPLES, //
		ContentSelector.PARAMETERS, //
		ContentSelector.RETURN_TYPES, //
		ContentSelector.TYPES //
		);
	private static final EnumSet<ContentSelector> TYPE_PROPERTY_SELECTOR = EnumSet.of(ContentSelector.NAME, //
		ContentSelector.DESCRIPTION, //
		ContentSelector.DOCUMENTS, //
		ContentSelector.EXAMPLES, //
		ContentSelector.PARAMETERS, //
		ContentSelector.PARENT_TYPES, //
		ContentSelector.RETURN_TYPES, //
		ContentSelector.SINCE, //
		ContentSelector.TYPES, //
		ContentSelector.USER_AGENTS //
		);

	private static final EnumSet<LocationType> IGNORED_TYPES = EnumSet.of(LocationType.UNKNOWN, LocationType.NONE);

	private JSIndexQueryHelper _indexHelper;
	private IContextInformationValidator _validator;
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
		List<PropertyElement> globals = this._indexHelper.getCoreGlobals(CORE_GLOBAL_SELECTOR);

		if (globals != null)
		{
			URI projectURI = this.getProjectURI();
			String location = JSIndexConstants.CORE;

			for (PropertyElement property : globals)
			{
				String name = property.getName();
				String description = JSModelFormatter.getDescription(property, projectURI);
				Image image = (property instanceof FunctionElement) ? JS_FUNCTION : JS_PROPERTY;
				Image[] userAgents = this.getUserAgentImages(property.getUserAgentNames());

				this.addProposal(proposals, name, image, description, userAgents, location, offset);
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
		List<PropertyElement> projectGlobals = this._indexHelper.getProjectGlobals(this.getIndex(), PROJECT_GLOBAL_SELECTOR);

		if (projectGlobals != null)
		{
			Image[] userAgents = this.getAllUserAgentIcons();
			URI projectURI = this.getProjectURI();

			for (PropertyElement property : projectGlobals)
			{
				String name = property.getName();
				String description = JSModelFormatter.getDescription(property, projectURI);
				Image image = (property instanceof FunctionElement) ? JS_FUNCTION : JS_PROPERTY;
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
		IParseNode propertyNode = null;

		if (this._targetNode != null && this._targetNode.getNodeType() == JSNodeTypes.GET_PROPERTY)
		{
			propertyNode = this._targetNode;
		}
		else if (this._statementNode != null)
		{
			if (this._statementNode.getNodeType() == JSNodeTypes.GET_PROPERTY)
			{
				propertyNode = this._statementNode;
			}
			else
			{
				IParseNode child = this._statementNode.getFirstChild();

				if (child != null && child.getNodeType() == JSNodeTypes.GET_PROPERTY)
				{
					propertyNode = child;
				}
			}
		}

		if (propertyNode != null)
		{
			JSGetPropertyNode node = (JSGetPropertyNode) propertyNode;
			JSScope localScope = this.getScopeAtOffset(offset);

			if (localScope != null)
			{
				List<String> typeList = null;

				// lookup in current file
				IParseNode lhs = node.getLeftHandSide();

				if (lhs instanceof JSNode)
				{
					JSNodeTypeInferrer typeWalker = new JSNodeTypeInferrer(localScope, this.getIndex(), this.getURI());

					typeWalker.visit((JSNode) lhs);

					typeList = typeWalker.getTypes();
				}

				if (typeList != null)
				{
					// TEMP: Show types for debugging info
					if (Platform.inDevelopmentMode())
					{
						System.out.println("types: " + StringUtil.join(", ", typeList)); //$NON-NLS-1$ //$NON-NLS-2$
					}

					// add all properties of each type to our proposal list
					for (String type : typeList)
					{
						if (type.startsWith(JSTypeConstants.FUNCTION_SIGNATURE_PREFIX))
						{
							this.addTypeProperties(proposals, JSTypeConstants.FUNCTION_TYPE, offset);
						}
						else if (type.startsWith(JSTypeConstants.GENERIC_ARRAY_OPEN))
						{
							this.addTypeProperties(proposals, JSTypeConstants.ARRAY_TYPE, offset);
						}
						else
						{
							this.addTypeProperties(proposals, type, offset);
						}
					}
				}
				else
				{
					// TEMP: Show types for debugging info
					if (Platform.inDevelopmentMode())
					{
						System.out.println("types: "); //$NON-NLS-1$
					}
				}
			}
		}
	}

	/**
	 * addProposal
	 * 
	 * @param proposals
	 * @param name
	 * @param image
	 * @param description
	 * @param userAgents
	 * @param fileLocation
	 * @param offset
	 */
	private void addProposal(Set<ICompletionProposal> proposals, String name, Image image, String description, Image[] userAgents, String fileLocation,
		int offset)
	{
		String displayName = name;
		int length = name.length();

		// calculate what text will be replaced
		int replaceLength = 0;

		if (this._replaceRange != null)
		{
			offset = this._replaceRange.getStartingOffset();
			replaceLength = this._replaceRange.getLength();
		}

		// build proposal
		IContextInformation contextInfo = null;

		CommonCompletionProposal proposal = new CommonCompletionProposal(name, offset, replaceLength, length, image, displayName, contextInfo, description);
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
			String fileLocation = this.getFilename();
			JSScope globalScope = this.getGlobalScope();

			if (globalScope != null)
			{
				JSScope localScope = globalScope.getScopeAtOffset(offset);

				if (localScope != null)
				{
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
		List<PropertyElement> properties = this._indexHelper.getTypeMembers(index, allTypes, TYPE_PROPERTY_SELECTOR);

		typeName = JSModelFormatter.getTypeDisplayName(typeName);

		for (PropertyElement property : properties)
		{
			boolean isFunction = (property instanceof FunctionElement);
			String name = property.getName();
			String description = JSModelFormatter.getDescription(property, this.getProjectURI());
			Image image = (isFunction) ? JS_FUNCTION : JS_PROPERTY;
			List<String> userAgentNames = property.getUserAgentNames();
			Image[] userAgents = getUserAgentImages(userAgentNames);
			String owningType = JSModelFormatter.getTypeDisplayName(property.getOwningType());

			this.addProposal(proposals, name, image, description, userAgents, owningType, offset);
		}
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
	protected ICompletionProposal[] doComputeCompletionProposals(ITextViewer viewer, int offset, char activationChar, boolean autoActivated)
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

			default:
				break;
		}

		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>(result);

		// sort by display name
		Collections.sort(proposals, new Comparator<ICompletionProposal>()
		{
			@Override
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	@Override
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		return new char[] { '.' };
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getContextInformationValidator()
	 */
	@Override
	public IContextInformationValidator getContextInformationValidator()
	{
		if (this._validator == null)
		{
			this._validator = new JSContextInformationValidator();
		}

		return this._validator;
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
					break;
				}
				else
				{
					root = root.getParent();
				}
			}

			if (root != null)
			{
				JSSymbolCollector s = new JSSymbolCollector();

				((JSParseRootNode) root).accept(s);

				result = s.getScope();
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
		this._targetNode = this.getActiveASTNode(offset);
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
			// root node for the host langauge and not for JS

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
			JSLocationWalker typeWalker = new JSLocationWalker(offset);

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
}
