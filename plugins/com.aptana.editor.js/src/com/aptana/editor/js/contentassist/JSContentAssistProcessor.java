package com.aptana.editor.js.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
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
import com.aptana.editor.js.contentassist.index.JSIndexConstants;
import com.aptana.editor.js.contentassist.model.FieldSelector;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.parsing.JSTokenScanner;
import com.aptana.editor.js.parsing.ast.JSAssignmentNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.index.core.Index;
import com.aptana.parsing.Scope;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;
import com.aptana.parsing.lexer.Lexeme;

public class JSContentAssistProcessor extends CommonContentAssistProcessor
{
	private static final String PARENS = "()"; //$NON-NLS-1$
	private static final Image JS_FUNCTION = Activator.getImage("/icons/js_function.gif"); //$NON-NLS-1$
	private static final Image JS_PROPERTY = Activator.getImage("/icons/js_property.gif"); //$NON-NLS-1$
	private static final EnumSet<FieldSelector> CORE_GLOBAL_FIELDS = EnumSet.of(FieldSelector.NAME, FieldSelector.DESCRIPTION, FieldSelector.USER_AGENTS, FieldSelector.TYPES, FieldSelector.RETURN_TYPES);
	private static final EnumSet<FieldSelector> PROJECT_GLOBAL_FIELDS = EnumSet.of(FieldSelector.NAME, FieldSelector.DESCRIPTION, FieldSelector.TYPES, FieldSelector.RETURN_TYPES);
	private static final EnumSet<FieldSelector> PROPERTY_FIELDS = EnumSet.of(FieldSelector.NAME, FieldSelector.DESCRIPTION, FieldSelector.USER_AGENTS);

	private JSIndexQueryHelper _indexHelper;
	private JSASTQueryHelper _astHelper;
	private IContextInformationValidator _validator;
	private Lexeme<JSTokenType> _currentLexeme;
	private IParseNode _targetNode;
	private IParseNode _statementNode;
	private Scope<JSNode> _globals;

	/**
	 * JSIndexContentAssitProcessor
	 * 
	 * @param editor
	 */
	public JSContentAssistProcessor(AbstractThemeableEditor editor)
	{
		super(editor);

		this._indexHelper = new JSIndexQueryHelper();
		this._astHelper = new JSASTQueryHelper();
	}
	
	/**
	 * addAllGlobals
	 */
	protected void addAllGlobals(Set<ICompletionProposal> proposals, int offset)
	{
		// add globals from core
		this.addCoreGlobals(proposals, offset);
		this.addProjectGlobals(proposals, offset);
		this.addLocalGlobalFunctions(proposals, offset);
	}
	
	/**
	 * addCoreFunctions
	 * 
	 * @param proposals
	 * @param offset
	 */
	private void addCoreFunctions(Set<ICompletionProposal> proposals, int offset)
	{
		List<PropertyElement> globals = this._indexHelper.getCoreGlobals(CORE_GLOBAL_FIELDS);
		
		for (PropertyElement property : globals)
		{
			boolean isFunction = (property instanceof FunctionElement);
			
			if (isFunction)
			{
				// grab the interesting parts
				String name = JSModelFormatter.getName(property);
				String description = JSModelFormatter.getDescription(property);
				Image image = JS_FUNCTION;
				Image[] userAgents = this.getUserAgentImages(property.getUserAgentNames());
				
				this.addProposal(proposals, name, image, description, userAgents, offset);
			}
		}
	}

	/**
	 * addCoreGlobals
	 * 
	 * @param proposals
	 * @param offset
	 */
	private void addCoreGlobals(Set<ICompletionProposal> proposals, int offset)
	{
		List<PropertyElement> globals = this._indexHelper.getCoreGlobals(CORE_GLOBAL_FIELDS);

		for (PropertyElement property : globals)
		{
			// slightly change behavior if this is a function
			boolean isFunction = (property instanceof FunctionElement);

			// grab the interesting parts
			String name = JSModelFormatter.getName(property);
			String description = JSModelFormatter.getDescription(property);
			Image image = isFunction ? JS_FUNCTION : JS_PROPERTY;
			Image[] userAgents = this.getUserAgentImages(property.getUserAgentNames());
			
			this.addProposal(proposals, name, image, description, userAgents, offset);
		}
	}
	
	/**
	 * addGlobalFunctions
	 * 
	 * @param proposals
	 * @param offset
	 */
	private void addLocalGlobalFunctions(Set<ICompletionProposal> proposals, int offset)
	{
		String fileLocation = this.getFilename();
		
		// add globals from current file
		IParseNode node = (this._targetNode.contains(offset)) ? this._targetNode : this.getAST();
		
		List<String> globalFunctions = this._astHelper.getChildFunctions(node);
		
		if (globalFunctions != null)
		{
			for (String function : globalFunctions)
			{
				String name = function + PARENS;
				String description = null;
				Image image = JS_FUNCTION;
				Image[] userAgents = this.getAllUserAgentIcons();
				
				this.addProposal(proposals, name, image, description, userAgents, fileLocation, offset);
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
		Index index = this.getIndex();
		List<PropertyElement> projectGlobals = this._indexHelper.getProjectGlobals(index, PROJECT_GLOBAL_FIELDS);
		
		if (projectGlobals != null)
		{
			for (PropertyElement property : projectGlobals)
			{
				boolean isFunction = (property instanceof FunctionElement);
				String name = (isFunction) ? property.getName() + PARENS : property.getName();
				String description = JSModelFormatter.getDescription(property);
				Image image = (isFunction) ? JS_FUNCTION : JS_PROPERTY;
				Image[] userAgents = this.getAllUserAgentIcons();
				String location = null;
				
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
		if (this._targetNode != null && this._targetNode.getNodeType() == JSNodeTypes.GET_PROPERTY)
		{
			JSGetPropertyNode node = (JSGetPropertyNode) this._targetNode;
			Scope<JSNode> localScope = this.getScopeAtOffset(offset);
			
			if (localScope != null)
			{
				List<String> typeList = null;
				
				// lookup in current file
				IParseNode lhs = node.getLeftHandSide();
				
				if (lhs instanceof JSNode)
				{
					JSTypeWalker typeWalker = new JSTypeWalker(localScope, this.getIndex());
					
					typeWalker.visit((JSNode) lhs);
					
					typeList = typeWalker.getTypes();
				}
				
				if (typeList != null)
				{
					// TEMP: Show types for debugging info
					if (Platform.inDevelopmentMode())
					{
						System.out.println("types: " + StringUtil.join(", ", typeList));
					}
					
					// add all properties of each type to our proposal list
					for (String type : typeList)
					{
						this.addTypeProperties(proposals, type, offset);
					}
				}
				else
				{
					// TEMP: Show types for debugging info
					if (Platform.inDevelopmentMode())
					{
						System.out.println("types: ");
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
	 * @param icon
	 * @param userAgents
	 * @param offset
	 */
	private void addProposal(Set<ICompletionProposal> proposals, String name, Image image, String description, Image[] userAgents, int offset)
	{
		this.addProposal(proposals, name, image, description, userAgents, JSIndexConstants.CORE, offset);
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
	private void addProposal(Set<ICompletionProposal> proposals, String name, Image image, String description, Image[] userAgents, String fileLocation, int offset)
	{
		String displayName = name;
		int length = name.length();

		// back up one if we end with parentheses
		if (name.endsWith(PARENS))
		{
			displayName = name.substring(0, name.length() - PARENS.length());
			length--;
		}
		
		IContextInformation contextInfo = null;
		int replaceLength = 0;

//		if (this._currentLexeme != null)
//		{
//			offset = this._currentLexeme.getStartingOffset();
//			replaceLength = this._currentLexeme.getLength();
//		}

		// build proposal
		CommonCompletionProposal proposal = new CommonCompletionProposal(name, offset, replaceLength, length, image, displayName, contextInfo, description);
		proposal.setFileLocation(fileLocation);
		proposal.setUserAgentImages(userAgents);

		// add it to the list
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
			Scope<JSNode> globalScope = this.getGlobalScope();
			
			if (globalScope != null)
			{
				Scope<JSNode> localScope = globalScope.getScopeAtOffset(offset);
				
				if (localScope != null)
				{
					List<String> symbols = localScope.getSymbolNames();
					
					for (String symbol : symbols)
					{
						boolean isFunction = false;
						List<JSNode> nodes = localScope.getSymbol(symbol);
						
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
						
						String name = (isFunction) ? symbol + PARENS : symbol;
						String description = null;
						Image image = (isFunction) ? JS_FUNCTION : JS_PROPERTY;
						Image[] userAgents = this.getAllUserAgentIcons();
						
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
		// add properties and methods
		List<PropertyElement> properties = this._indexHelper.getTypeMembers(this.getIndex(), typeName, PROPERTY_FIELDS);
		
		for (PropertyElement property : properties)
		{
			boolean isFunction = (property instanceof FunctionElement);
			String name = (isFunction) ? property.getName() + PARENS : property.getName();
			String description = property.getDescription();
			Image image = (isFunction) ? JS_FUNCTION : JS_PROPERTY;
			String[] userAgentNames = property.getUserAgentNames();
			Image[] userAgents = getUserAgentImages(userAgentNames);
			
			this.addProposal(proposals, name, image, description, userAgents, typeName, offset);
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
		
		this._targetNode = this.getActiveASTNode(offset);
		this._statementNode = null;
		
		if (this._targetNode != null && this._targetNode instanceof JSNode)
		{
			this._statementNode = ((JSNode) this._targetNode).getContainingStatementNode();
			
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
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#doComputeCompletionProposals(org.eclipse.jface.text.ITextViewer, int, char, boolean)
	 */
	@Override
	protected ICompletionProposal[] doComputeCompletionProposals(ITextViewer viewer, int offset, char activationChar, boolean autoActivated)
	{
		IDocument document = viewer.getDocument();
		LexemeProvider<JSTokenType> lexemeProvider = this.createLexemeProvider(document, offset);
		Set<ICompletionProposal> result = new HashSet<ICompletionProposal>();

		// store a reference to the lexeme at the current position
		this._currentLexeme = lexemeProvider.getLexemeFromOffset(offset);
		
		if (this._currentLexeme == null)
		{
			this._currentLexeme = lexemeProvider.getLexemeFromOffset(offset - 1);
		}

		// first step is to determine where we are
		LocationType location = this.getLocation(lexemeProvider, offset);
		
		switch (location)
		{
			case IN_PROPERTY_NAME:
				this.addProperties(result, offset);
				break;
				
			case IN_VARIABLE_NAME:
				//System.out.println("Variable");
				break;
				
			case IN_GLOBAL:
				this.addAllGlobals(result, offset);
				this.addSymbolsInScope(result, offset);
				break;
				
			case IN_CONSTRUCTOR:
				this.addCoreFunctions(result, offset);
				this.addProjectGlobals(result, offset);
				this.addLocalGlobalFunctions(result, offset);
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

		// select the current proposal based on the current lexeme
		if (this._currentLexeme != null)
		{
			this.setSelectedProposal(this._currentLexeme.getText(), proposals);
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
	protected Scope<JSNode> getGlobalScope()
	{
		if (this._globals == null)
		{
			IParseNode root = this.getAST();
			
			if (root instanceof JSParseRootNode)
			{
				JSSymbolCollector s = new JSSymbolCollector();
				
				((JSParseRootNode) root).accept(s);
				
				this._globals = s.getScope();
			}
			
		}
		
		return this._globals;
	}

	/**
	 * getLocation
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	LocationType getLocation(LexemeProvider<JSTokenType> lexemeProvider, int offset)
	{
		LocationType result = LocationType.UNKNOWN;
		IParseNode ast = this.getAST();
		
		if (ast instanceof JSParseRootNode)
		{
			JSLocationWalker walker = new JSLocationWalker(offset);
			
			((JSParseRootNode) ast).accept(walker);
			
			result = walker.getType();
		}
		
//		if (result == Location.UNKNOWN)
//		{
//			result = this.getLocationByLexeme(lexemeProvider, offset);
//		}
		
		return result;
	}
	
	/**
	 * getLocationByAST
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	LocationType getLocationByAST(LexemeProvider<JSTokenType> lexemeProvider, int offset)
	{
		Lexeme<JSTokenType> lexeme;
		IParseNode node;
		LocationType result = LocationType.UNKNOWN;
		short type;
		
		if (this._targetNode != null)
		{
			switch (this._targetNode.getNodeType())
			{
				case JSNodeTypes.ARGUMENTS:
					lexeme = lexemeProvider.getLexemeFromOffset(offset);
					
					if (lexeme != null)
					{
						switch (lexeme.getType())
						{
							case COMMA:
							case LPAREN:
								lexeme = lexemeProvider.getLexemeFromOffset(offset - 1);
								
								if (lexeme != null && lexeme.getType() == JSTokenType.IDENTIFIER)
								{
									this._currentLexeme = lexeme;
									result = LocationType.IN_GLOBAL;
								}
								break;
								
							case RPAREN:
								node = this._targetNode.getNodeAtOffset(offset - 1);
								
								if (node != null)
								{
									switch (node.getNodeType())
									{
										case JSNodeTypes.IDENTIFIER:
											result = LocationType.IN_GLOBAL;
											this._currentLexeme = lexemeProvider.getLexemeFromOffset(node.getStartingOffset());
											break;
											
										default:
											if (node == this._targetNode)
											{
												result = LocationType.IN_GLOBAL;
											}
											break;
									}
								}
								break;
								
							default:
								result = LocationType.IN_GLOBAL;
								break;
						}
					}
					else
					{
						this._currentLexeme = null;
						result = LocationType.IN_GLOBAL;
					}
					break;
					
				case JSNodeTypes.CONSTRUCT:
					lexeme = lexemeProvider.getLexemeFromOffset(offset);
					
					if (lexeme == null)
					{
						// see if we're touching
						lexeme = lexemeProvider.getLexemeFromOffset(offset - 1);
					}
					
					if (lexeme == null || lexeme.getType() != JSTokenType.NEW)
					{
						result = LocationType.IN_CONSTRUCTOR;
					}
					break;
					
				case JSNodeTypes.DECLARATION:
					// ignore declarations in for-statements for now
					type = this._statementNode.getNodeType();
					
					if (type == JSNodeTypes.FOR || type == JSNodeTypes.FOR_IN)
					{
						break;
					}
					// else fall through
					
				case JSNodeTypes.EMPTY:
				case JSNodeTypes.STATEMENTS:
					if (this._targetNode.contains(offset) || this._targetNode.getEndingOffset() < offset)
					{
						if (this._targetNode.getStartingOffset() != offset)
						{
							result = LocationType.IN_GLOBAL;
							this._currentLexeme = null;
						}
					}
					break;
					
				case JSNodeTypes.FOR:
					lexeme = lexemeProvider.getLexemeFromOffset(offset - 1);
					
					if (lexeme != null && lexeme.getType() == JSTokenType.IDENTIFIER)
					{
						result = LocationType.IN_GLOBAL;
						this._currentLexeme = lexeme;
					}
					break;
					
				case JSNodeTypes.FOR_IN:
					break;
					
				case JSNodeTypes.GET_ELEMENT:
				case JSNodeTypes.GET_PROPERTY:
					result = LocationType.IN_GLOBAL;
					
					lexeme = lexemeProvider.getLexemeFromOffset(offset - 1);
					
					if (lexeme != null)
					{
						this._currentLexeme = lexeme;
					}
					break;
					
				case JSNodeTypes.IDENTIFIER:
					// ignore for-statements for now
					 type = this._statementNode.getNodeType();
					
					if (type != JSNodeTypes.FOR && type != JSNodeTypes.FOR_IN)
					{
						node = this._targetNode.getParent();
						
						switch (node.getNodeType())
						{
							case JSNodeTypes.DECLARATION:
							case JSNodeTypes.FUNCTION:
							case JSNodeTypes.PARAMETERS:
								break;
								
							case JSNodeTypes.GET_PROPERTY:
								if (node.getChild(0) == this._targetNode)
								{
									result = LocationType.IN_GLOBAL;
								}
								break;
							
							default:
								result = LocationType.IN_GLOBAL;
								break;
						}
					}
					break;
					
				case JSNodeTypes.IF:
				case JSNodeTypes.WHILE:
					if (this._currentLexeme.getType() == JSTokenType.RPAREN && this._currentLexeme.getStartingOffset() == offset)
					{
						result = LocationType.IN_GLOBAL;
						lexeme = lexemeProvider.getLexemeFromOffset(offset - 1);
						
						if (lexeme != null & lexeme.getType() == JSTokenType.IDENTIFIER)
						{
							this._currentLexeme = lexeme;
						}
						else
						{
							this._currentLexeme = null;
						}
					}
					break;
					
				case JSNodeTypes.INVOKE:
					lexeme = lexemeProvider.getLexemeFromOffset(offset);
					
					if (lexeme == null)
					{
						// see if we're touching
						lexeme = lexemeProvider.getLexemeFromOffset(offset - 1);
					}
					
					if (lexeme != null && lexeme.getType() == JSTokenType.SEMICOLON && lexeme.getEndingOffset() < offset)
					{
						result = LocationType.IN_GLOBAL;
					}
					break;
					
				case JSNodeTypes.NAME_VALUE_PAIR:
					if (this._currentLexeme.getType() == JSTokenType.COLON && this._currentLexeme.getEndingOffset() < offset)
					{
						result = LocationType.IN_GLOBAL;
						this._currentLexeme = null;
					}
					break;
					
				case JSNodeTypes.OBJECT_LITERAL:
					lexeme = lexemeProvider.getFloorLexeme(offset - 1);
					
					if (lexeme != null)
					{
						node = this._statementNode.getNodeAtOffset(lexeme.getStartingOffset());
						
						if (node != null && node.getNodeType() == JSNodeTypes.IDENTIFIER && node.getParent().getParent() == this._targetNode)
						{
							result = LocationType.IN_GLOBAL;
						}
					}
					break;
					
				case JSNodeTypes.PARAMETERS:
					result = LocationType.IN_PARAMETERS;
					break;
					
				case JSNodeTypes.VAR:
					if (this._targetNode.contains(offset) == false)
					{
						result = LocationType.IN_GLOBAL;
					}
					else
					{
						node = this._targetNode.getNodeAtOffset(offset - 1);
						
						if (node != null && node.getNodeType() == JSNodeTypes.IDENTIFIER)
						{
							result = LocationType.IN_GLOBAL;
						}
					}
					break;
					
				// assignment nodes
				case JSNodeTypes.ASSIGN:
				case JSNodeTypes.ADD_AND_ASSIGN:
				case JSNodeTypes.ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN:
				case JSNodeTypes.BITWISE_AND_AND_ASSIGN:
				case JSNodeTypes.BITWISE_OR_AND_ASSIGN:
				case JSNodeTypes.BITWISE_XOR_AND_ASSIGN:
				case JSNodeTypes.DIVIDE_AND_ASSIGN:
				case JSNodeTypes.MOD_AND_ASSIGN:
				case JSNodeTypes.MULTIPLY_AND_ASSIGN:
				case JSNodeTypes.SHIFT_LEFT_AND_ASSIGN:
				case JSNodeTypes.SHIFT_RIGHT_AND_ASSIGN:
				case JSNodeTypes.SUBTRACT_AND_ASSIGN:
					if (this._targetNode instanceof JSAssignmentNode)
					{
						lexeme = lexemeProvider.getFloorLexeme(offset);
						
						if (lexeme != null)
						{
							switch (lexeme.getType())
							{
								case AMPERSAND_EQUAL:
								case CARET_EQUAL:
								case EQUAL:
								case FORWARD_SLASH_EQUAL:
								case GREATER_GREATER_EQUAL:
								case GREATER_GREATER_GREATER_EQUAL:
								case LESS_LESS_EQUAL:
								case MINUS_EQUAL:
								case PERCENT_EQUAL:
								case PIPE_EQUAL:
								case PLUS_EQUAL:
								case STAR_EQUAL:
									if (offset == lexeme.getStartingOffset())
									{
										result = LocationType.IN_VARIABLE_NAME;
									}
									break;
									
								case IDENTIFIER:
									result = LocationType.IN_VARIABLE_NAME;
									break;
									
								default:
									break;
							}
						}
					}
					break;
					
				default:
					if (this._targetNode instanceof ParseRootNode)
					{
						lexeme = lexemeProvider.getFloorLexeme(offset);
						
						if (lexeme != null)
						{
							switch (lexeme.getType())
							{
								case RCURLY:
								case SEMICOLON:
									result = LocationType.IN_GLOBAL;
									this._currentLexeme = null;
									break;
									
								default:
									break;
							}
						}
					}
					break;
			}
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
	LocationType getLocationByLexeme(LexemeProvider<JSTokenType> lexemeProvider, int offset)
	{
		LocationType result = LocationType.UNKNOWN;
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
	protected Scope<JSNode> getScopeAtOffset(int offset)
	{
		Scope<JSNode> result = null;
		
		// grab global scope
		Scope<JSNode> global = this.getGlobalScope();
		
		if (global != null)
		{
			Scope<JSNode> candidate = global.getScopeAtOffset(offset);
			
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
	protected Image[] getUserAgentImages(String[] userAgentNames)
	{
		return UserAgentManager.getInstance().getUserAgentImages(userAgentNames);
	}
}
