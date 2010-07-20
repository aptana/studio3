package com.aptana.editor.html.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.html.Activator;
import com.aptana.editor.html.HTMLScopeScanner;
import com.aptana.editor.html.HTMLSourceConfiguration;
import com.aptana.editor.html.contentassist.index.HTMLIndexConstants;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
import com.aptana.editor.html.contentassist.model.ValueElement;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;

public class HTMLContentAssistProcessor extends CommonContentAssistProcessor
{
	/**
	 * LocationType
	 */
	static enum LocationType
	{
		// coarse-grain locations
		ERROR,
		IN_OPEN_TAG,
		IN_CLOSE_TAG,
		IN_DOCTYPE,
		IN_COMMENT,
		IN_TEXT,
		IN_ELEMENT_NAME,

		// fine-grain locations
		IN_ATTRIBUTE_NAME,
		IN_ATTRIBUTE_VALUE
	};

	private static final Image ELEMENT_ICON = Activator.getImage("/icons/element.gif"); //$NON-NLS-1$
	private static final Image ATTRIBUTE_ICON = Activator.getImage("/icons/attribute.gif"); //$NON-NLS-1$
	private static final Image EVENT_ICON = Activator.getImage("/icons/event.gif"); //$NON-NLS-1$
	private static final Map<String, LocationType> locationMap;

	private HTMLIndexQueryHelper _queryHelper;
	private IContextInformationValidator _validator;
	private Lexeme<HTMLTokenType> _currentLexeme;
	private IRange _replaceRange;

	/**
	 * static initializer
	 */
	static
	{
		locationMap = new HashMap<String, LocationType>();
		locationMap.put(HTMLSourceConfiguration.DEFAULT, LocationType.IN_TEXT);
		locationMap.put(HTMLSourceConfiguration.HTML_COMMENT, LocationType.IN_COMMENT);
		locationMap.put(HTMLSourceConfiguration.HTML_DOCTYPE, LocationType.IN_DOCTYPE);

		locationMap.put(HTMLSourceConfiguration.HTML_SCRIPT, LocationType.IN_OPEN_TAG);
		locationMap.put(HTMLSourceConfiguration.HTML_STYLE, LocationType.IN_OPEN_TAG);
		locationMap.put(HTMLSourceConfiguration.HTML_TAG, LocationType.IN_OPEN_TAG);

		locationMap.put(JSSourceConfiguration.DEFAULT, LocationType.IN_TEXT);
		locationMap.put(CSSSourceConfiguration.DEFAULT, LocationType.IN_TEXT);
		locationMap.put(IDocument.DEFAULT_CONTENT_TYPE, LocationType.IN_TEXT);
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
			switch (this._currentLexeme.getType())
			{
				case EQUAL:
					int index = lexemeProvider.getLexemeFloorIndex(offset);

					if (index > 0)
					{
						this._replaceRange = this._currentLexeme = lexemeProvider.getLexeme(index - 1);
					}
					break;

				case TAG_END:
					this._replaceRange = null;
					break;

				default:
					break;
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
	 * addAttributeValueProposals
	 * 
	 * @param proposals
	 * @param offset
	 * @param attributeName
	 */
	private void addAttributeValueProposals(List<ICompletionProposal> proposals, int offset, String attributeName)
	{
		// NOTE: the logic for handling an attribute list and duplicate values
		// will go away once the metadata format is fixed so that multiple
		// attributes with the same name can disambiguate what elements they
		// belong to.
		List<AttributeElement> attributes = this._queryHelper.getAttribute(attributeName);

		if (attributes != null)
		{
			Set<String> addedNames = new HashSet<String>();

			for (AttributeElement attribute : attributes)
			{
				for (ValueElement value : attribute.getValues())
				{
					String name = value.getName();

					if (addedNames.contains(name) == false)
					{
						Image[] userAgentIcons = this.getAllUserAgentIcons();

						this.addProposal(proposals, value.getName(), ATTRIBUTE_ICON, value.getDescription(), userAgentIcons, offset);

						addedNames.add(name);
					}
				}
			}
		}
	}

	/**
	 * addAttributeValueProposals
	 * 
	 * @param proposals
	 * @param lexemeProvider
	 * @param offset
	 */
	private void addAttributeValueProposals(List<ICompletionProposal> proposals, LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		String attributeName = this.getAttributeName(lexemeProvider, offset);

		if (attributeName != null && attributeName.length() > 0)
		{
			switch (this._currentLexeme.getType())
			{
				case SINGLE_QUOTED_STRING:
				case DOUBLE_QUOTED_STRING:
					// trim off the quotes
					if (this._currentLexeme.getLength() >= 2)
					{
						int startingOffset = this._currentLexeme.getStartingOffset() + 1;
						int endingOffset = this._currentLexeme.getEndingOffset() - 1;

						this._replaceRange = new Range(startingOffset, endingOffset);
					}
					break;

				case EQUAL:
					this._replaceRange = new Range(offset, offset - 1);
					break;

				default:
					break;
			}

			if (attributeName.equals("id")) //$NON-NLS-1$
			{
				this.addIDProposals(proposals, offset);
			}
			else if (attributeName.equals("class")) //$NON-NLS-1$
			{
				this.addClassProposals(proposals, offset);
			}
			else
			{
				addAttributeValueProposals(proposals, offset, attributeName);
			}
		}
	}

	/**
	 * addClasses
	 * 
	 * @param proposals
	 * @param offset
	 */
	protected void addClassProposals(List<ICompletionProposal> proposals, int offset)
	{
		Map<String, String> classes = this._queryHelper.getClasses(this.getIndex());

		if (classes != null)
		{
			UserAgentManager manager = UserAgentManager.getInstance();
			String[] userAgents = manager.getActiveUserAgentIDs(); // classes can be used by all user agents
			Image[] userAgentIcons = manager.getUserAgentImages(userAgents);

			for (Entry<String, String> entry : classes.entrySet())
			{
				this.addProposal(proposals, entry.getKey(), ATTRIBUTE_ICON, null, userAgentIcons, entry.getValue(), offset);
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

				if (this._currentLexeme.getType() == HTMLTokenType.TAG_START)
				{
					this._replaceRange = this._currentLexeme = null;
				}
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
				Image[] userAgentIcons = this.getAllUserAgentIcons();

				this.addProposal(proposals, entity.getName(), ELEMENT_ICON, entity.getDescription(), userAgentIcons, offset);
			}
		}
	}

	/**
	 * addIDs
	 * 
	 * @param result
	 * @param offset
	 */
	protected void addIDProposals(List<ICompletionProposal> proposals, int offset)
	{
		Map<String, String> ids = this._queryHelper.getIDs(this.getIndex());

		if (ids != null)
		{
			UserAgentManager manager = UserAgentManager.getInstance();
			String[] userAgents = manager.getActiveUserAgentIDs(); // classes can be used by all user agents
			Image[] userAgentIcons = manager.getUserAgentImages(userAgents);

			for (Entry<String, String> entry : ids.entrySet())
			{
				this.addProposal(proposals, entry.getKey(), ATTRIBUTE_ICON, null, userAgentIcons, entry.getValue(), offset);
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
		LocationType location = this.getOpenTagLocationType(lexemeProvider, offset);

		switch (location)
		{
			case IN_ELEMENT_NAME:
				this.addElementProposals(proposals, lexemeProvider, offset);
				break;

			case IN_ATTRIBUTE_NAME:
				this.addAttributeAndEventProposals(proposals, lexemeProvider, offset);
				break;

			case IN_ATTRIBUTE_VALUE:
				this.addAttributeValueProposals(proposals, lexemeProvider, offset);
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
	 * @param image
	 * @param description
	 * @param userAgents
	 * @param offset
	 */
	private void addProposal(List<ICompletionProposal> proposals, String name, Image image, String description, Image[] userAgents, int offset)
	{
		this.addProposal(proposals, name, image, description, userAgents, HTMLIndexConstants.CORE, offset);
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
	private void addProposal(List<ICompletionProposal> proposals, String name, Image image, String description, Image[] userAgents, String fileLocation,
		int offset)
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
		proposal.setFileLocation(fileLocation);
		proposal.setUserAgentImages(userAgents);

		// add it to the list
		proposals.add(proposal);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#doComputeCompletionProposals(org.eclipse.jface.text.ITextViewer, int, char, boolean)
	 */
	@Override
	protected ICompletionProposal[] doComputeCompletionProposals(ITextViewer viewer, int offset, char activationChar, boolean autoActivated)
	{
		// tokenize the current document
		IDocument document = viewer.getDocument();

		LexemeProvider<HTMLTokenType> lexemeProvider = this.createLexemeProvider(document, offset);

		// store a reference to the lexeme at the current position
		this._replaceRange = this._currentLexeme = lexemeProvider.getFloorLexeme(offset);

		// first step is to determine if we're inside an open tag, close tag, text, etc.
		LocationType location = this.getCoarseLocationType(document, lexemeProvider, offset);

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

	/**
	 * createLexemeProvider
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	LexemeProvider<HTMLTokenType> createLexemeProvider(IDocument document, int offset)
	{
		int documentLength = document.getLength();

		// account for last position returning an empty IDocument default partition
		int lexemeProviderOffset = (offset >= documentLength) ? documentLength - 1 : offset;

		return new LexemeProvider<HTMLTokenType>(document, lexemeProviderOffset, new HTMLScopeScanner())
		{
			@Override
			protected HTMLTokenType getTypeFromData(Object data)
			{
				return HTMLTokenType.get((String) data);
			}
		};
	}

	/**
	 * getAttributeName
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	private String getAttributeName(LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		String name = null;
		int index = lexemeProvider.getLexemeFloorIndex(offset);

		while (index >= 0)
		{
			Lexeme<HTMLTokenType> lexeme = lexemeProvider.getLexeme(index);

			if (lexeme.getType() == HTMLTokenType.EQUAL)
			{
				if (index >= 1)
				{
					lexeme = lexemeProvider.getLexeme(index - 1);

					if (lexeme != null)
					{
						name = lexeme.getText();
					}
				}

				break;
			}

			index--;
		}

		return name;
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
	 * This method looks at the partition that contains the specified offset and from that partition type determines if
	 * the offset is: 1. Within an open tag 2. Within a close tag 3. Within a text area If the partition type is
	 * unrecognized, the ERROR location will be returned.
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	LocationType getCoarseLocationType(IDocument document, LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		LocationType result = LocationType.ERROR;

		try
		{
			ITypedRegion partition = document.getPartition(offset);
			String type = partition.getType();

			if (locationMap.containsKey(type))
			{
				result = locationMap.get(type);

				Lexeme<HTMLTokenType> firstLexeme = lexemeProvider.getFirstLexeme();
				Lexeme<HTMLTokenType> lastLexeme;

				if (firstLexeme != null)
				{
					switch (result)
					{
						case IN_OPEN_TAG:
							lastLexeme = lexemeProvider.getLastLexeme();
							
							if (lastLexeme != null && lastLexeme.getEndingOffset() == offset - 1)
							{
								result = LocationType.IN_TEXT;
							}
							else
							{
								if (firstLexeme.getStartingOffset() == offset)
								{
									result = LocationType.IN_TEXT;
								}
								else if ("</".equals(firstLexeme.getText())) //$NON-NLS-1$
								{
									result = LocationType.IN_CLOSE_TAG;
								}
							}
							break;

						case IN_TEXT:
							if (firstLexeme.getStartingOffset() < offset) // && offset <= lastLexeme.getEndingOffset())
							{
								lastLexeme = lexemeProvider.getLastLexeme();

								if ("<".equals(firstLexeme.getText())) //$NON-NLS-1$
								{
									switch (lastLexeme.getType())
									{
										case TAG_END:
										case TAG_SELF_CLOSE:
											if (offset <= lastLexeme.getStartingOffset())
											{
												result = LocationType.IN_OPEN_TAG;
											}
											break;

										default:
											result = LocationType.IN_OPEN_TAG;
											break;
									}
								}
								else if ("</".equals(firstLexeme.getText())) //$NON-NLS-1$
								{
									switch (lastLexeme.getType())
									{
										case TAG_END:
										case TAG_SELF_CLOSE:
											if (offset <= lastLexeme.getStartingOffset())
											{
												result = LocationType.IN_CLOSE_TAG;
											}
											break;

										default:
											result = LocationType.IN_CLOSE_TAG;
											break;
									}
								}
							}
							break;

						default:
							break;
					}
				}
				else
				{
					result = LocationType.IN_TEXT;
				}
			}
		}
		catch (BadLocationException e)
		{
		}

		return result;
	}

	/**
	 * This method further refines a location within an open tag. The following locations types are identified: 1. In an
	 * element name 2. In an attribute name 3. In an attribute value If the location cannot be determined, the ERROR
	 * location is returned
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	LocationType getOpenTagLocationType(LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		LocationType result = LocationType.ERROR;

		int index = lexemeProvider.getLexemeIndex(offset);

		if (index < 0)
		{
			int candidateIndex = lexemeProvider.getLexemeFloorIndex(offset);
			Lexeme<HTMLTokenType> lexeme = lexemeProvider.getLexeme(candidateIndex);

			if (lexeme != null && lexeme.getEndingOffset() == offset - 1)
			{
				index = candidateIndex;
			}
			else
			{
				result = LocationType.IN_ATTRIBUTE_NAME;
			}
		}

		while (index >= 0)
		{
			Lexeme<HTMLTokenType> lexeme = lexemeProvider.getLexeme(index);

			switch (lexeme.getType())
			{
				case ATTRIBUTE:
				case CLASS:
				case ID:
					result = LocationType.IN_ATTRIBUTE_NAME;
					break;

				case EQUAL:
					result = (offset <= lexeme.getStartingOffset()) ? LocationType.IN_ATTRIBUTE_NAME : LocationType.IN_ATTRIBUTE_VALUE;
					break;

				case TAG_START:
					result = LocationType.IN_ELEMENT_NAME;
					break;
					
				case TAG_END:
					if (index >= 1)
					{
						Lexeme<HTMLTokenType> previous = lexemeProvider.getLexeme(index - 1);
						
						if (previous.getEndingOffset() < offset - 1)
						{
							result = LocationType.IN_ATTRIBUTE_NAME;
						}
					}
					break;

				case BLOCK_TAG:
				case STRUCTURE_TAG:
				case INLINE_TAG:
				case META:
					if (index >= 1)
					{
						Lexeme<HTMLTokenType> previous = lexemeProvider.getLexeme(index - 1);

						switch (previous.getType())
						{
							case BLOCK_TAG:
							case STRUCTURE_TAG:
							case INLINE_TAG:
							case META:
							case SINGLE_QUOTED_STRING:
							case DOUBLE_QUOTED_STRING:
								this._replaceRange = this._currentLexeme = lexeme;
								result = LocationType.IN_ATTRIBUTE_NAME;
								break;

							case TAG_START:
								this._replaceRange = this._currentLexeme = lexeme;
								result = LocationType.IN_ELEMENT_NAME;
								break;

							default:
								break;
						}
					}
					else
					{
						result = LocationType.IN_ELEMENT_NAME;
					}
					break;

				case SINGLE_QUOTED_STRING:
				case DOUBLE_QUOTED_STRING:
					if (lexeme.getEndingOffset() < offset)
					{
						result = LocationType.IN_ATTRIBUTE_NAME;
						this._replaceRange = null;
					}
					else
					{
						result = LocationType.IN_ATTRIBUTE_VALUE;
					}
					break;

				default:
					break;
			}

			if (result != LocationType.ERROR)
			{
				break;
			}
			else
			{
				index--;
			}
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

			if ("&".equals(startingLexeme.getText())) //$NON-NLS-1$
			{
				break;
			}
		}

		// check ending location
		index++;

		if (index < lexemeProvider.size())
		{
			Lexeme<HTMLTokenType> candidate = lexemeProvider.getLexeme(index);

			if (";".equals(candidate.getText())) //$NON-NLS-1$
			{
				endingLexeme = candidate;
			}
		}

		if (startingLexeme != null && endingLexeme != null)
		{
			this._replaceRange = new Range(startingLexeme.getStartingOffset(), endingLexeme.getEndingOffset() - 1);
		}
	}
}
