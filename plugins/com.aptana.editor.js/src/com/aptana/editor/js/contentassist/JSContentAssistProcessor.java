package com.aptana.editor.js.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import com.aptana.editor.js.JSScopeScanner;
import com.aptana.editor.js.contentassist.index.JSIndexConstants;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.Lexeme;

public class JSContentAssistProcessor extends CommonContentAssistProcessor
{
	static enum Location
	{
		ERROR, IN_GLOBAL, IN_PROPERTY_NAME, IN_VARIABLE_NAME
	};

	private static final Image JS_FUNCTION = Activator.getImage("/icons/js_function.gif"); //$NON-NLS-1$
	private static final Image JS_PROPERTY = Activator.getImage("/icons/js_property.gif"); //$NON-NLS-1$

	private JSIndexQueryHelper _indexHelper;
	private JSASTQueryHelper _astHelper;
	private IContextInformationValidator _validator;
	private Lexeme<JSTokenType> _currentLexeme;
	private IParseNode _currentNode;

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
	 * getGlobals
	 */
	protected void addGlobals(List<ICompletionProposal> proposals, int offset)
	{
		// add globals from core
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
		
		// add globals from current file
		List<String> globalFunctions = this._astHelper.getGlobalFunctions(this._currentNode);
		String fileLocation = this.editor.getEditorInput().getName();
		
		for (String function : globalFunctions)
		{
			String name = function + "()";
			String description = null;
			Image image = JS_FUNCTION;
			Image[] userAgents = this.getAllUserAgentIcons();
			
			this.addProposal(proposals, name, image, description, userAgents, fileLocation, offset);
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
	private void addProposal(List<ICompletionProposal> proposals, String name, Image image, String description, Image[] userAgents, int offset)
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
	private void addProposal(List<ICompletionProposal> proposals, String name, Image image, String description, Image[] userAgents, String fileLocation, int offset)
	{
		int length = name.length();
		String displayName = name;
		IContextInformation contextInfo = null;

		// TEMP:
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
	protected void addSymbolsInScope(List<ICompletionProposal> proposals, int offset)
	{
		if (this._currentNode != null)
		{
			List<String> args = this._astHelper.getSymbolsInScope(this._currentNode);
			
			for (String name : args)
			{
				String description = null;
				Image image = JS_PROPERTY;
				Image[] userAgents = this.getAllUserAgentIcons();
				
				this.addProposal(proposals, name, image, description, userAgents, offset);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.CommonContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer
	 * , int, char, boolean)
	 */
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset, char activationChar, boolean autoActivated)
	{
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		
		if (Platform.inDevelopmentMode())
		{
			IDocument document = viewer.getDocument();
			
			LexemeProvider<JSTokenType> lexemeProvider = this.createLexemeProvider(document, offset);
	
			// store a reference to the lexeme at the current position
			this._currentLexeme = lexemeProvider.getFloorLexeme(offset);
	
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
					this.addGlobals(result, offset);
					this.addSymbolsInScope(result, offset);
					break;
					
				default:
					break;
			}
	
			// sort by display name
			Collections.sort(result, new Comparator<ICompletionProposal>()
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
				this.setSelectedProposal(this._currentLexeme.getText(), result);
			}
		}

		return result.toArray(new ICompletionProposal[result.size()]);
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
		
		this._currentNode = this.getActiveASTNode(offset);
		
		if (this._currentNode != null)
		{
			result = new JSLexemeProvider(document, this._currentNode, new JSScopeScanner());
		}
		else
		{
			result = new JSLexemeProvider(document, offset, new JSScopeScanner());
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
		IParseNode ast = this.editor.getFileService().getParseResult();
		IParseNode result = null;
		
		// TODO: Limit this to only the node types we're interested in
		if (ast != null)
		{
			IParseNode candidate = ast.getNodeAt(offset);
			
			if (candidate != null)
			{
				switch (candidate.getType())
				{
					case JSNodeTypes.GET_PROPERTY:
					case JSNodeTypes.ARGUMENTS:
						result = candidate;
						break;
						
					default:
						break;
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
	 * getLocation
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	Location getLocation(LexemeProvider<JSTokenType> lexemeProvider, int offset)
	{
		Location result = Location.ERROR;
		int index = lexemeProvider.getLexemeIndex(offset);
		
		if (index < 0)
		{
			int candidateIndex = lexemeProvider.getLexemeFloorIndex(offset);
			Lexeme<JSTokenType> lexeme = lexemeProvider.getLexeme(candidateIndex);
			
			if (lexeme != null && lexeme.getEndingOffset() == offset)
			{
				index = candidateIndex;
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
							case SOURCE:
								result = Location.IN_VARIABLE_NAME;
								break;
								
							default:
								break;
						}
					}
					break;
					
				case PARENTHESIS:
					if ("(".equals(lexeme.getText()))
					{
						if (offset == lexeme.getEndingOffset())
						{
							result = Location.IN_GLOBAL;
						}
					}
					else
					{
						if (offset == lexeme.getStartingOffset())
						{
							result = Location.IN_GLOBAL;
						}
					}
					break;
					
				case SOURCE:
					if (index > 0)
					{
						Lexeme<JSTokenType> previousLexeme = lexemeProvider.getLexeme(index - 1);
						
						switch (previousLexeme.getType())
						{
							case DOT:
								result = Location.IN_PROPERTY_NAME;
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
		
		return result;
	}
}
