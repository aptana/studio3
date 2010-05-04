package com.aptana.editor.html.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import com.aptana.editor.html.Activator;
import com.aptana.editor.html.HTMLScopeScanner;
import com.aptana.editor.html.contentassist.index.HTMLIndexConstants;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.parsing.lexer.Lexeme;

public class HTMLContentAssistProcessor extends CommonContentAssistProcessor
{
	private static enum Location
	{
		ERROR, IN_OPEN_TAG, IN_ATTRIBUTE_NAME, IN_ATTRIBUTE_VALUE, IN_TEXT
	};

	private static final Image ELEMENT_ICON = Activator.getImage("/icons/element.gif");
	private static final Image ATTRIBUTE_ICON = Activator.getImage("/icons/attribute.gif");
	private static final Image EVENT_ICON = Activator.getImage("/icons/event.gif");

	private HTMLIndexQueryHelper _queryHelper;
	private IContextInformationValidator _validator;
	private Lexeme<HTMLTokenType> _currentLexeme;

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
	 * @param result
	 * @param offset
	 */
	protected void addAttributeAndEventProposals(List<ICompletionProposal> proposals, LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		String elementName = this.getElementName(lexemeProvider, offset);
		ElementElement element = this._queryHelper.getElement(elementName);

		if (element != null)
		{
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
	protected void addElementProposals(List<ICompletionProposal> proposals, int offset)
	{
		List<ElementElement> elements = this._queryHelper.getElements();

		if (elements != null)
		{
			for (ElementElement element : elements)
			{
				String[] userAgents = element.getUserAgentNames();
				Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);
				
				this.addProposal(proposals, element.getName(), ELEMENT_ICON, element.getDescription(), userAgentIcons, offset);
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
	private void addProposal(List<ICompletionProposal> proposals, String name, Image image, String description, Image[] userAgents, int offset)
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
		this._currentLexeme = lexemeProvider.getFloorLexeme(offset);

		// first step is to determine if we're inside an open tag, close tag, text, etc.
		Location location = this.getLocation(lexemeProvider, offset);

		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

		switch (location)
		{
			case IN_OPEN_TAG:
				this.addElementProposals(result, offset);
				break;

			case IN_ATTRIBUTE_NAME:
				this.addAttributeAndEventProposals(result, lexemeProvider, offset);
				break;

			case IN_ATTRIBUTE_VALUE:
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

		return result.toArray(new ICompletionProposal[result.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	@Override
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		return new char[] { '<', '\'', '"' };
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
	private Location getLocation(LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		Location result = Location.ERROR;

		int index = lexemeProvider.getLexemeFloorIndex(offset);

		LOOP: while (index >= 0)
		{
			Lexeme<HTMLTokenType> lexeme = lexemeProvider.getLexeme(index);

			switch (lexeme.getType())
			{
				case BLOCK_TAG:
				case INLINE_TAG:
				case STRUCTURE_TAG:
				case TAG_START:
					result = Location.IN_OPEN_TAG;
					break LOOP;

				case ATTRIBUTE:
				case ID:
				case CLASS:
				case EQUAL:
					result = Location.IN_ATTRIBUTE_NAME;
					break LOOP;

				case SINGLE_QUOTED_STRING:
				case DOUBLE_QUOTED_STRING:
					result = Location.IN_ATTRIBUTE_VALUE;
					break LOOP;

				default:
					break;
			}

			index--;
		}

		return result;
	}
}
