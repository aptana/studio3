package com.aptana.editor.js.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.contentassist.JSASTQueryHelper.Classification;
import com.aptana.editor.js.contentassist.index.JSIndexConstants;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.parsing.JSTokenScanner;
import com.aptana.editor.js.parsing.ast.JSAssignmentNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
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

	static enum Location
	{
		ERROR, IN_GLOBAL, IN_PARAMETERS, IN_CONSTRUCTOR, IN_PROPERTY_NAME, IN_VARIABLE_NAME
	};

	private static final Image JS_FUNCTION = Activator.getImage("/icons/js_function.gif"); //$NON-NLS-1$
	private static final Image JS_PROPERTY = Activator.getImage("/icons/js_property.gif"); //$NON-NLS-1$

	private JSIndexQueryHelper _indexHelper;
	private JSASTQueryHelper _astHelper;
	private IContextInformationValidator _validator;
	private Lexeme<JSTokenType> _currentLexeme;
	private IParseNode _targetNode;
	private IParseNode _statementNode;

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
	 * addCoreFunctions
	 * 
	 * @param proposals
	 * @param offset
	 */
	private void addCoreFunctions(Set<ICompletionProposal> proposals, int offset)
	{
		List<PropertyElement> globals = this._indexHelper.getCoreGlobals();
		
		for (PropertyElement property : globals)
		{
			boolean isFunction = (property instanceof FunctionElement);
			
			if (isFunction)
			{
				// grab the interesting parts
				String name = JSModelFormatter.getName(property);
				String description = JSModelFormatter.getDescription(property);
				Image image = JS_FUNCTION;
				String[] userAgentNames = property.getUserAgentNames();
				Image[] userAgents = UserAgentManager.getInstance().getUserAgentImages(userAgentNames);
				
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
		List<PropertyElement> globals = this._indexHelper.getCoreGlobals();

		for (PropertyElement property : globals)
		{
			// slightly change behavior if this is a function
			boolean isFunction = (property instanceof FunctionElement);

			// grab the interesting parts
			String name = JSModelFormatter.getName(property);
			//int length = isFunction ? name.length() - 1 : name.length();
			String description = JSModelFormatter.getDescription(property);
			Image image = isFunction ? JS_FUNCTION : JS_PROPERTY;
			String[] userAgentNames = property.getUserAgentNames();
			Image[] userAgents = UserAgentManager.getInstance().getUserAgentImages(userAgentNames);
			
			this.addProposal(proposals, name, image, description, userAgents, offset);
		}
	}
	
	/**
	 * addAllGlobals
	 */
	protected void addAllGlobals(Set<ICompletionProposal> proposals, int offset)
	{
		// add globals from core
		this.addCoreGlobals(proposals, offset);
		this.addProjectGlobalFunctions(proposals, offset);
		this.addProjectVariables(proposals, offset);
		this.addLocalGlobalFunctions(proposals, offset);
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
	private void addProjectGlobalFunctions(Set<ICompletionProposal> proposals, int offset)
	{
		Index index = this.getIndex();
		Map<String,List<String>> projectGlobals = this._indexHelper.getProjectGlobals(index);
		
		if (projectGlobals != null)
		{
			for (Entry<String,List<String>> entry : projectGlobals.entrySet())
			{
				List<String> files = entry.getValue();
				boolean hasFiles = (files != null && files.size() > 0);
				
				String name = entry.getKey() + PARENS;
				String description = (hasFiles) ? JSModelFormatter.getDescription(Messages.JSContentAssistProcessor_Referencing_Files, files) : null;
				Image image = JS_FUNCTION;
				Image[] userAgents = this.getAllUserAgentIcons();
				String location = (hasFiles) ? files.get(0) : null;
				
				this.addProposal(proposals, name, image, description, userAgents, location, offset);
			}
		}
	}
	
	/**
	 * addProjectVariables
	 * 
	 * @param proposals
	 * @param offset
	 */
	private void addProjectVariables(Set<ICompletionProposal> proposals, int offset)
	{
		Index index = this.getIndex();
		Map<String,List<String>> projectVariables = this._indexHelper.getProjectVariables(index);
		
		if (projectVariables != null)
		{
			for (Entry<String,List<String>> entry : projectVariables.entrySet())
			{
				List<String> files = entry.getValue();
				boolean hasFiles = (files != null && files.size() > 0);
				
				String name = entry.getKey();
				String description = (hasFiles) ? JSModelFormatter.getDescription(Messages.JSContentAssistProcessor_Referencing_Files, files) : null;;
				Image image = JS_PROPERTY;
				Image[] userAgents = this.getAllUserAgentIcons();
				String location = (hasFiles) ? files.get(0) : null;
				
				this.addProposal(proposals, name, image, description, userAgents, location, offset);
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

		if (this._currentLexeme != null)
		{
			offset = this._currentLexeme.getStartingOffset();
			replaceLength = this._currentLexeme.getLength();
		}

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
			
			if (Platform.inDevelopmentMode())
			{
				IParseNode ast = this.getAST();
				
				if (ast instanceof JSParseRootNode)
				{
					JSParseRootNode root = (JSParseRootNode) ast;
					Scope<JSNode> globalScope = root.getGlobalScope();
					
					if (globalScope != null)
					{
						Scope<JSNode> currentScope = globalScope.getScopeAtOffset(offset);
						
						if (currentScope != null)
						{
							List<String> symbols = currentScope.getSymbolNames();
							
							for (String symbol : symbols)
							{
								boolean isFunction = false;
								List<JSNode> nodes = currentScope.getSymbol(symbol);
								
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
			else
			{
				IParseNode node = (this._targetNode.contains(offset)) ? this._targetNode : this.getAST();
				Map<String,Classification> args = this._astHelper.getSymbolsInScope(node);
				
				for (Entry<String,Classification> entry : args.entrySet())
				{
					boolean isFunction = (entry.getValue() == Classification.FUNCTION);
					String name = (isFunction) ? entry.getKey() + PARENS : entry.getKey();
					String description = null;
					Image image = (isFunction) ? JS_FUNCTION : JS_PROPERTY;
					Image[] userAgents = this.getAllUserAgentIcons();
					
					this.addProposal(proposals, name, image, description, userAgents, fileLocation, offset);
				}
			}
		}
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
		Location location = this.getLocation(lexemeProvider, offset);

		switch (location)
		{
			case IN_PROPERTY_NAME:
				//System.out.println("Property");
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
				this.addProjectGlobalFunctions(result, offset);
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
		
		if (this._targetNode != null)
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
		return new char[] { /*'.'*/ };
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
	 * getLocationByAST
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	Location getLocationByAST(LexemeProvider<JSTokenType> lexemeProvider, int offset)
	{
		Lexeme<JSTokenType> lexeme;
		IParseNode node;
		Location result = Location.ERROR;
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
									result = Location.IN_GLOBAL;
								}
								break;
								
							case RPAREN:
								node = this._targetNode.getNodeAtOffset(offset - 1);
								
								if (node != null)
								{
									switch (node.getNodeType())
									{
										case JSNodeTypes.IDENTIFIER:
											result = Location.IN_GLOBAL;
											this._currentLexeme = lexemeProvider.getLexemeFromOffset(node.getStartingOffset());
											break;
											
										default:
											if (node == this._targetNode)
											{
												result = Location.IN_GLOBAL;
											}
											break;
									}
								}
								break;
								
							default:
								result = Location.IN_GLOBAL;
								break;
						}
					}
					else
					{
						this._currentLexeme = null;
						result = Location.IN_GLOBAL;
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
						result = Location.IN_CONSTRUCTOR;
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
							result = Location.IN_GLOBAL;
							this._currentLexeme = null;
						}
					}
					break;
					
				case JSNodeTypes.FOR:
					lexeme = lexemeProvider.getLexemeFromOffset(offset - 1);
					
					if (lexeme != null && lexeme.getType() == JSTokenType.IDENTIFIER)
					{
						result = Location.IN_GLOBAL;
						this._currentLexeme = lexeme;
					}
					break;
					
				case JSNodeTypes.FOR_IN:
					break;
					
				case JSNodeTypes.GET_ELEMENT:
				case JSNodeTypes.GET_PROPERTY:
					result = Location.IN_GLOBAL;
					
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
									result = Location.IN_GLOBAL;
								}
								break;
							
							default:
								result = Location.IN_GLOBAL;
								break;
						}
					}
					break;
					
				case JSNodeTypes.IF:
				case JSNodeTypes.WHILE:
					if (this._currentLexeme.getType() == JSTokenType.RPAREN && this._currentLexeme.getStartingOffset() == offset)
					{
						result = Location.IN_GLOBAL;
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
						result = Location.IN_GLOBAL;
					}
					break;
					
				case JSNodeTypes.NAME_VALUE_PAIR:
					if (this._currentLexeme.getType() == JSTokenType.COLON && this._currentLexeme.getEndingOffset() < offset)
					{
						result = Location.IN_GLOBAL;
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
							result = Location.IN_GLOBAL;
						}
					}
					break;
					
				case JSNodeTypes.PARAMETERS:
					result = Location.IN_PARAMETERS;
					break;
					
				case JSNodeTypes.VAR:
					if (this._targetNode.contains(offset) == false)
					{
						result = Location.IN_GLOBAL;
					}
					else
					{
						node = this._targetNode.getNodeAtOffset(offset - 1);
						
						if (node != null && node.getNodeType() == JSNodeTypes.IDENTIFIER)
						{
							result = Location.IN_GLOBAL;
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
										result = Location.IN_VARIABLE_NAME;
									}
									break;
									
								case IDENTIFIER:
									result = Location.IN_VARIABLE_NAME;
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
									result = Location.IN_GLOBAL;
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
	Location getLocationByLexeme(LexemeProvider<JSTokenType> lexemeProvider, int offset)
	{
		Location result = Location.ERROR;
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
					result = Location.IN_PROPERTY_NAME;
					break;
					
				case SEMICOLON:
					if (index > 0)
					{
						Lexeme<JSTokenType> previousLexeme = lexemeProvider.getLexeme(index - 1);
						
						switch (previousLexeme.getType())
						{
							case IDENTIFIER:
								result = Location.IN_GLOBAL;
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
							result = Location.IN_GLOBAL;
						}
					}
					break;
					
				case RPAREN:
					if (offset == lexeme.getStartingOffset())
					{
						result = Location.IN_GLOBAL;
					}
					break;
					
				case IDENTIFIER:
					if (index > 0)
					{
						Lexeme<JSTokenType> previousLexeme = lexemeProvider.getLexeme(index - 1);
						
						switch (previousLexeme.getType())
						{
							case DOT:
								result = Location.IN_PROPERTY_NAME;
								break;
								
							case NEW:
								result = Location.IN_CONSTRUCTOR;
								break;
								
							default:
								result = Location.IN_VARIABLE_NAME;
								break;
						}
					}
					else
					{
						result = Location.IN_VARIABLE_NAME;
					}
					break;
					
				default:
					break;
			}
		}
		else if (lexemeProvider.size() == 0)
		{
			result = Location.IN_GLOBAL;
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
	Location getLocation(LexemeProvider<JSTokenType> lexemeProvider, int offset)
	{
		Location result = this.getLocationByAST(lexemeProvider, offset);
		
//		if (result == Location.ERROR)
//		{
//			result = this.getLocationByLexeme(lexemeProvider, offset);
//		}
		
		return result;
	}
}
