package com.aptana.editor.html.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.html.Activator;
import com.aptana.editor.html.HTMLScopeScanner;
import com.aptana.editor.html.HTMLSourceConfiguration;
import com.aptana.editor.html.contentassist.index.HTMLIndexConstants;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;

public class HTMLContentAssistProcessor extends CommonContentAssistProcessor
{
	private static enum Location
	{
		ERROR, IN_OPEN_TAG, IN_CLOSE_TAG, IN_DOCTYPE, IN_COMMENT, IN_TEXT, // coarse locations
		IN_ELEMENT_NAME,
		IN_ATTRIBUTE_NAME,
		IN_ATTRIBUTE_VALUE
	};

	private static final Image ELEMENT_ICON = Activator.getImage("/icons/element.gif");
	private static final Image ATTRIBUTE_ICON = Activator.getImage("/icons/attribute.gif");
	private static final Image EVENT_ICON = Activator.getImage("/icons/event.gif");
	private static final Map<String, Location> locationMap;

	private HTMLIndexQueryHelper _queryHelper;
	private IContextInformationValidator _validator;
	private Lexeme<HTMLTokenType> _currentLexeme;
	private IRange _replaceRange;

	/**
	 * static initializer
	 */
	static
	{
		locationMap = new HashMap<String, Location>();
		locationMap.put(HTMLSourceConfiguration.DEFAULT, Location.IN_TEXT);
		locationMap.put(HTMLSourceConfiguration.HTML_COMMENT, Location.IN_COMMENT);
		locationMap.put(HTMLSourceConfiguration.HTML_DOCTYPE, Location.IN_DOCTYPE);

		locationMap.put(HTMLSourceConfiguration.HTML_SCRIPT, Location.IN_OPEN_TAG);
		locationMap.put(HTMLSourceConfiguration.HTML_STYLE, Location.IN_OPEN_TAG);
		locationMap.put(HTMLSourceConfiguration.HTML_TAG, Location.IN_OPEN_TAG);
	}

	/**
	 * HTMLIndexContentAssistProcessor
	 * 
	 * @param editor
	 */
	public HTMLContentAssistProcessor(AbstractThemeableEditor editor)
	{
		super(editor);

		this._queryHelper = new HTMLIndexQueryHelper();
	}

	/**
	 * addAttributeAndEventProposals
	 * 
	 * @param offset
	 * @param result
	 */
	protected void addAttributeAndEventProposals(List<ICompletionProposal> proposals, LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		String elementName = this.getElementName(lexemeProvider, offset);
		ElementElement element = this._queryHelper.getElement(elementName);

		if (element != null)
		{
			if (this._currentLexeme.getType() == HTMLTokenType.EQUAL)
			{
				int index = lexemeProvider.getLexemeFloorIndex(offset);

				this._replaceRange = this._currentLexeme = lexemeProvider.getLexeme(index - 1);
			}

			String[] userAgents = element.getUserAgentNames();
			Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);

			for (String attribute : element.getAttributes())
			{
				this.addProposal(proposals, attribute, ATTRIBUTE_ICON, null, userAgentIcons, offset);
			}

			for (String event : element.getEvents())
			{
				this.addProposal(proposals, event, EVENT_ICON, null, userAgentIcons, offset);
			}
		}
	}

	/**
	 * addOpenTagProposals
	 * 
	 * @param proposals
	 * @param offset
	 */
	protected void addElementProposals(List<ICompletionProposal> proposals, LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		List<ElementElement> elements = this._queryHelper.getElements();

		if (elements != null)
		{
			if (this._currentLexeme.getType() == HTMLTokenType.TAG_END)
			{
				int index = lexemeProvider.getLexemeCeilingIndex(offset);

				this._replaceRange = this._currentLexeme = lexemeProvider.getLexeme(index - 1);
			}

			for (ElementElement element : elements)
			{
				String[] userAgents = element.getUserAgentNames();
				Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);

				this.addProposal(proposals, element.getName(), ELEMENT_ICON, element.getDescription(), userAgentIcons, offset);
			}
		}

	}

	/**
	 * addEntityProposals
	 * 
	 * @param result
	 * @param offset
	 */
	private void addEntityProposals(List<ICompletionProposal> proposals, LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		List<EntityElement> entities = this._queryHelper.getEntities();

		if (entities != null)
		{
			this.setEntityRange(lexemeProvider, offset);

			for (EntityElement entity : entities)
			{
				UserAgentManager manager = UserAgentManager.getInstance();
				String[] userAgents = manager.getActiveUserAgentIDs();
				Image[] userAgentIcons = manager.getUserAgentImages(userAgents);

				this.addProposal(proposals, entity.getName(), ELEMENT_ICON, entity.getDescription(), userAgentIcons, offset);
			}
		}
	}

	/**
	 * addOpenTagProposals
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @param result
	 */
	private void addOpenTagPropsals(List<ICompletionProposal> proposals, LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		Location location = this.getOpenTagLocation(lexemeProvider, offset);

		switch (location)
		{
			case IN_ELEMENT_NAME:
				this.addElementProposals(proposals, lexemeProvider, offset);
				break;

			case IN_ATTRIBUTE_NAME:
				this.addAttributeAndEventProposals(proposals, lexemeProvider, offset);
				break;

			case IN_ATTRIBUTE_VALUE:
				break;

			default:
				break;
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
		int length = name.length();
		String displayName = name;
		IContextInformation contextInfo = null;

		// TEMP:
		int replaceLength = 0;

		if (this._replaceRange != null)
		{
			offset = this._replaceRange.getStartingOffset();
			replaceLength = this._replaceRange.getLength();
		}

		// build proposal
		CommonCompletionProposal proposal = new CommonCompletionProposal(name, offset, replaceLength, length, image, displayName, contextInfo, description);
		proposal.setFileLocation(HTMLIndexConstants.CORE);
		proposal.setUserAgentImages(userAgents);

		// add it to the list
		proposals.add(proposal);
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
		// tokenize the current document
		IDocument document = viewer.getDocument();
		LexemeProvider<HTMLTokenType> lexemeProvider = new LexemeProvider<HTMLTokenType>(document, offset, new HTMLScopeScanner())
		{
			@Override
			protected HTMLTokenType getTypeFromName(String name)
			{
				return HTMLTokenType.get(name);
			}
		};

		// store a reference to the lexeme at the current position
		this._replaceRange = this._currentLexeme = lexemeProvider.getFloorLexeme(offset);

		// first step is to determine if we're inside an open tag, close tag, text, etc.
		Location location = this.getLocation(document, lexemeProvider, offset);

		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

		switch (location)
		{
			case IN_OPEN_TAG:
				this.addOpenTagPropsals(result, lexemeProvider, offset);
				break;

			case IN_CLOSE_TAG:
				break;

			case IN_TEXT:
				this.addEntityProposals(result, lexemeProvider, offset);
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
		if (this._replaceRange != null)
		{
			try
			{
				String text = document.get(this._replaceRange.getStartingOffset(), this._replaceRange.getLength());
				
				this.setSelectedProposal(text, result);
			}
			catch (BadLocationException e)
			{
			}
		}

		return result.toArray(new ICompletionProposal[result.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	@Override
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		return new char[] { '<', '\'', '"', '&' };
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
			this._validator = new HTMLContextInformationValidator();
		}

		return this._validator;
	}

	/**
	 * getElementName
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	private String getElementName(LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		String result = null;
		int index = lexemeProvider.getLexemeFloorIndex(offset);

		LOOP: for (int i = index; i >= 0; i--)
		{
			Lexeme<HTMLTokenType> lexeme = lexemeProvider.getLexeme(i);

			switch (lexeme.getType())
			{
				case BLOCK_TAG:
				case INLINE_TAG:
				case STRUCTURE_TAG:
				case TAG_START:
					result = lexeme.getText();
					break LOOP;
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
	private Location getLocation(IDocument document, LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		Location result = Location.ERROR;

		try
		{
			ITypedRegion partition = document.getPartition(offset);
			String type = partition.getType();

			if (locationMap.containsKey(type))
			{
				result = locationMap.get(type);

				// We tag all html_tag-like partitions as an open_tag location;
				// however, we refine that here based on the first token in the
				// partition
				if (result == Location.IN_OPEN_TAG)
				{
					Lexeme<HTMLTokenType> lexeme = lexemeProvider.getLexeme(0);

					if (lexeme.getStartingOffset() == offset)
					{
						result = Location.IN_TEXT;
					}
					else if ("</".equals(lexeme.getText()))
					{
						result = Location.IN_CLOSE_TAG;
					}
				}
			}
		}
		catch (BadLocationException e)
		{
		}

		return result;
	}

	/**
	 * getOpenTagLocation
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	private Location getOpenTagLocation(LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		Location result = Location.ERROR;

		switch (this._currentLexeme.getType())
		{
			case ATTRIBUTE:
			case EQUAL:
			case TAG_END:
				result = Location.IN_ATTRIBUTE_NAME;
				break;

			case BLOCK_TAG:
			case STRUCTURE_TAG:
			case INLINE_TAG:
				result = Location.IN_ELEMENT_NAME;
				break;

			case SINGLE_QUOTED_STRING:
			case DOUBLE_QUOTED_STRING:
				result = (this._currentLexeme.getEndingOffset() == offset) ? Location.IN_ATTRIBUTE_NAME : Location.IN_ATTRIBUTE_VALUE;
				break;

			default:
				break;
		}

		return result;
	}

	/**
	 * setEntityRange
	 * 
	 * @param lexemeProvider
	 * @param offset
	 */
	private void setEntityRange(LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		int index = lexemeProvider.getLexemeFloorIndex(offset);
		Lexeme<HTMLTokenType> endingLexeme = lexemeProvider.getLexeme(index);
		Lexeme<HTMLTokenType> startingLexeme = endingLexeme;

		// find starting location
		for (int i = index; i >= 0; i--)
		{
			startingLexeme = lexemeProvider.getLexeme(i);
			
			if ("&".equals(startingLexeme.getText()))
			{
				break;
			}
		}
		
		// check ending location
		index++;
		
		if (index < lexemeProvider.size())
		{
			Lexeme<HTMLTokenType> candidate = lexemeProvider.getLexeme(index);
			
			if (";".equals(candidate.getText()))
			{
				endingLexeme = candidate;
			}
		}
		
		this._replaceRange = new Range(startingLexeme.getStartingOffset(), endingLexeme.getEndingOffset() - 1);
	}
}
