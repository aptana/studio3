/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.xml.XMLPlugin;
import com.aptana.editor.xml.XMLSourceConfiguration;
import com.aptana.editor.xml.contentassist.index.XMLIndexConstants;
import com.aptana.editor.xml.contentassist.model.AttributeElement;
import com.aptana.editor.xml.contentassist.model.ElementElement;
import com.aptana.editor.xml.contentassist.model.ValueElement;
import com.aptana.editor.xml.parsing.XMLParserScanner;
import com.aptana.editor.xml.parsing.lexer.XMLTokenType;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;

public class XMLContentAssistProcessor extends CommonContentAssistProcessor
{
	/**
	 * LocationType
	 */
	public static enum LocationType
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

	private static final Image ELEMENT_ICON = XMLPlugin.getImage("/icons/element.png"); //$NON-NLS-1$
	private static final Image ATTRIBUTE_ICON = XMLPlugin.getImage("/icons/attribute.png"); //$NON-NLS-1$

	protected Map<String, LocationType> locationMap;

	private XMLIndexQueryHelper _queryHelper;
	private Lexeme<XMLTokenType> _currentLexeme;
	private IRange _replaceRange;
	private IDocument _document;

	/**
	 * HTMLIndexContentAssistProcessor
	 * 
	 * @param editor
	 */
	public XMLContentAssistProcessor(AbstractThemeableEditor editor)
	{
		super(editor);

		this._queryHelper = new XMLIndexQueryHelper();

		this.buildLocationMap();
	}

	/**
	 * addAttributeAndEventProposals
	 * 
	 * @param lexemeProvider
	 * @param offset
	 */
	protected List<ICompletionProposal> addAttributeProposals(LexemeProvider<XMLTokenType> lexemeProvider, int offset)
	{
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		String elementName = this.getElementName(lexemeProvider, offset);
		ElementElement element = this._queryHelper.getElement(elementName);

		if (element != null)
		{
			int length = 2;

			String postfix = "=\"\""; //$NON-NLS-1$

			switch (this._currentLexeme.getType())
			{
				case EQUAL:
					int index = lexemeProvider.getLexemeFloorIndex(offset);

					if (index > 0)
					{
						this._replaceRange = this._currentLexeme = lexemeProvider.getLexeme(index - 1);
						postfix = ""; //$NON-NLS-1$
						length = 0;
					}
					break;

				case END_TAG:
					this._replaceRange = null;
					break;

				default:
					index = lexemeProvider.getLexemeFloorIndex(offset);
					Lexeme<XMLTokenType> nextlexeme = lexemeProvider.getLexeme(index + 1);

					if (nextlexeme != null && nextlexeme.getType() == XMLTokenType.EQUAL)
					{
						postfix = ""; //$NON-NLS-1$
						length = 0;
					}
					break;
			}

			for (String attribute : element.getAttributes())
			{
				proposals.add(createProposal(attribute, attribute + postfix, ATTRIBUTE_ICON, null, null, this.getCoreLocation(), offset, attribute.length()
					+ length));
			}
		}

		return proposals;
	}

	/**
	 * addAttributeValueProposals
	 * 
	 * @param offset
	 * @param elementName
	 * @param attributeName
	 */
	private List<ICompletionProposal> addAttributeValueProposals(int offset, String elementName, String attributeName)
	{
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		AttributeElement attribute = this._queryHelper.getAttribute(elementName, attributeName);

		if (attribute != null)
		{
			for (ValueElement value : attribute.getValues())
			{
				String name = value.getName();
				Image icon = ATTRIBUTE_ICON;
				String description = value.getDescription();
				Image[] userAgentIcons = this.getAllUserAgentIcons();

				this.addProposal(proposals, name, icon, description, userAgentIcons, offset);
			}
		}

		return proposals;
	}

	/**
	 * addAttributeValueProposals
	 * 
	 * @param proposals
	 * @param lexemeProvider
	 * @param offset
	 */
	private List<ICompletionProposal> addAttributeValueProposals(LexemeProvider<XMLTokenType> lexemeProvider, int offset)
	{
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
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

			String elementName = this.getElementName(lexemeProvider, offset);

			proposals.addAll(this.addAttributeValueProposals(offset, elementName, attributeName));
		}
		return proposals;
	}

	/**
	 * addCloseTagProposals
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @param result
	 */
	private List<ICompletionProposal> addCloseTagProposals(LexemeProvider<XMLTokenType> lexemeProvider, int offset)
	{
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		Set<String> unclosedElements = getUnclosedTagNames(offset);

		// First see if there are any unclosed tags, suggest them first
		if (unclosedElements != null && !unclosedElements.isEmpty())
		{
			for (String unclosedElement : unclosedElements)
			{
				ElementElement element = this._queryHelper.getElement(unclosedElement);

				proposals.add(createCloseTagProposal(element, offset));
			}

			if (!proposals.isEmpty())
			{
				return proposals;
			}
		}

		// Looks like no unclosed tags that make sense. Suggest every non-self-closing tag.
		List<ElementElement> elements = this._queryHelper.getElements();

		if (elements != null)
		{
			for (ElementElement element : elements)
			{
				proposals.add(createCloseTagProposal(element, offset));
			}
		}
		return proposals;
	}

	/**
	 * addElementProposals
	 * 
	 * @param lexemeProvider
	 * @param offset
	 */
	protected List<ICompletionProposal> addElementProposals(LexemeProvider<XMLTokenType> lexemeProvider, int offset)
	{
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		List<ElementElement> elements = this._queryHelper.getElements();

		if (elements != null)
		{
			boolean close = true;
			int replaceLength = 0;

			if (this._currentLexeme.getType() == XMLTokenType.END_TAG) // '|>
			{
				replaceLength = 1; // replace the '>'
			}
			else if (this._currentLexeme.getType() != XMLTokenType.START_TAG) // as long as it's not: "<|<"
			{
				// We're on element name, replace it
				int index = lexemeProvider.getLexemeCeilingIndex(_currentLexeme.getEndingOffset() + 1);

				if (index == -1 || index >= lexemeProvider.size())
				{
					index = lexemeProvider.size() - 1;
				}

				Lexeme<XMLTokenType> nextLexeme = lexemeProvider.getLexeme(index);

				if (nextLexeme != null) // && !nextLexeme.equals(_currentLexeme))
				{
					offset = _currentLexeme.getStartingOffset();
					replaceLength = _currentLexeme.getLength();

					if (nextLexeme.equals(this._currentLexeme) == false)
					{
						if (nextLexeme.getType() == XMLTokenType.END_TAG)
						{
							// Followed by '>', so replace spaces plus end
							replaceLength += nextLexeme.getEndingOffset() - _currentLexeme.getEndingOffset();
						}
						else if (nextLexeme.getType() != XMLTokenType.START_TAG)
						{
							// If there's an attribute we don't want to add ">" or close tag!
							close = false;
						}
					}
				}
			}

			for (ElementElement element : elements)
			{
				String replaceString = element.getName();
				int cursorPosition = replaceString.length();

				if (close)
				{
					if (element.getName().charAt(0) == '!') // don't close DOCTYPE with a slash
					{
						replaceString += " >"; //$NON-NLS-1$
						cursorPosition += 1;
					}
					else
					{
						// If the tag doesn't exist in the doc, we get back that it's closed. We need to copy the
						// document and insert the tag into it
						IDocument doc = new Document(_document.get());

						try
						{
							doc.replace(offset, replaceLength, element.getName() + ">"); //$NON-NLS-1$
						}
						catch (BadLocationException e)
						{
							// ignore
						}

						// if (!OpenTagCloser.tagClosed(doc, element.getName()))
						// {
						//							replaceString += "></" + element.getName() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
						// // TODO Depending on the tag, we should add a "tabstop" inside the open part of the tag
						// cursorPosition += 1;
						// }
						// else
						// {
						//							replaceString += ">"; //$NON-NLS-1$
						// cursorPosition += 1;
						// }
					}
				}

				CommonCompletionProposal proposal = new CommonCompletionProposal(replaceString, offset, replaceLength, cursorPosition, ELEMENT_ICON, element
					.getName(), null, element.getDescription());

				proposal.setFileLocation(this.getCoreLocation());
				proposals.add(proposal);
			}
		}
		return proposals;
	}

	/**
	 * addOpenTagProposals
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @param result
	 */
	private void addOpenTagPropsals(List<ICompletionProposal> proposals, LexemeProvider<XMLTokenType> lexemeProvider, int offset)
	{
		LocationType location = this.getOpenTagLocationType(lexemeProvider, offset);

		switch (location)
		{
			case IN_ELEMENT_NAME:
				proposals.addAll(this.addElementProposals(lexemeProvider, offset));
				break;

			case IN_ATTRIBUTE_NAME:
				proposals.addAll(this.addAttributeProposals(lexemeProvider, offset));
				break;

			case IN_ATTRIBUTE_VALUE:
				proposals.addAll(this.addAttributeValueProposals(lexemeProvider, offset));
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
		this.addProposal(proposals, name, image, description, userAgents, this.getCoreLocation(), offset);
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
		CommonCompletionProposal proposal = createProposal(name, image, description, userAgents, fileLocation, offset);
		// add it to the list
		proposals.add(proposal);
	}

	/**
	 * buildLocationMap
	 */
	protected void buildLocationMap()
	{
		locationMap = new HashMap<String, LocationType>();
		locationMap.put(XMLSourceConfiguration.DEFAULT, LocationType.IN_TEXT);
		locationMap.put(XMLSourceConfiguration.COMMENT, LocationType.IN_COMMENT);
		locationMap.put(XMLSourceConfiguration.CDATA, LocationType.IN_TEXT);
		locationMap.put(XMLSourceConfiguration.PRE_PROCESSOR, LocationType.IN_TEXT);
		// locationMap.put(XMLSourceConfiguration.XML_DOCTYPE, LocationType.IN_DOCTYPE);
		locationMap.put(XMLSourceConfiguration.TAG, LocationType.IN_OPEN_TAG);

		locationMap.put(IDocument.DEFAULT_CONTENT_TYPE, LocationType.IN_TEXT);
	}

	/**
	 * createCloseTagProposal
	 * 
	 * @param element
	 * @param offset
	 * @return
	 */
	private CommonCompletionProposal createCloseTagProposal(ElementElement element, int offset)
	{
		String replaceString = element.getName();
		int cursorPosition = replaceString.length();
		int replaceLength = 0;
		CommonCompletionProposal proposal = new CommonCompletionProposal(replaceString, offset, replaceLength, cursorPosition, ELEMENT_ICON, element.getName(),
			null, element.getDescription());

		proposal.setFileLocation(this.getCoreLocation());

		return proposal;
	}

	/**
	 * createLexemeProvider
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	LexemeProvider<XMLTokenType> createLexemeProvider(IDocument document, int offset)
	{
		int documentLength = document.getLength();

		// account for last position returning an empty IDocument default partition
		int lexemeProviderOffset = (offset >= documentLength) ? documentLength - 1 : offset;

		return new LexemeProvider<XMLTokenType>(document, lexemeProviderOffset, new XMLParserScanner())
		{
			@Override
			protected XMLTokenType getTypeFromData(Object data)
			{
				return (XMLTokenType) data;
			}
		};
	}

	/**
	 * createProposal
	 * 
	 * @param name
	 * @param image
	 * @param description
	 * @param userAgents
	 * @param fileLocation
	 * @param offset
	 * @return
	 */
	private CommonCompletionProposal createProposal(String name, Image image, String description, Image[] userAgents, String fileLocation, int offset)
	{
		return createProposal(name, name, image, description, userAgents, fileLocation, offset, name.length());
	}

	/**
	 * createProsal
	 * 
	 * @param displayName
	 * @param name
	 * @param image
	 * @param description
	 * @param userAgents
	 * @param fileLocation
	 * @param offset
	 * @param length
	 * @return
	 */
	protected CommonCompletionProposal createProposal(String displayName, String name, Image image, String description, Image[] userAgents,
		String fileLocation, int offset, int length)
	{
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
		return proposal;
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
		// tokenize the current document
		this._document = viewer.getDocument();

		LexemeProvider<XMLTokenType> lexemeProvider = this.createLexemeProvider(_document, offset);

		// store a reference to the lexeme at the current position
		this._replaceRange = this._currentLexeme = lexemeProvider.getFloorLexeme(offset);

		// first step is to determine if we're inside an open tag, close tag, text, etc.
		LocationType location = this.getCoarseLocationType(this._document, lexemeProvider, offset);

		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

		switch (location)
		{
			case IN_OPEN_TAG:
				this.addOpenTagPropsals(result, lexemeProvider, offset);
				break;

			case IN_CLOSE_TAG:
				result.addAll(this.addCloseTagProposals(lexemeProvider, offset));
				break;

			case IN_TEXT:
				break;

			default:
				break;
		}

		// sort by display name
		Collections.sort(result, new Comparator<ICompletionProposal>()
		{
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
				String text = _document.get(this._replaceRange.getStartingOffset(), this._replaceRange.getLength());

				this.setSelectedProposal(text, result);
			}
			catch (BadLocationException e)
			{
			}
		}

		return result.toArray(new ICompletionProposal[result.size()]);
	}

	/**
	 * getAttributeName
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	private String getAttributeName(LexemeProvider<XMLTokenType> lexemeProvider, int offset)
	{
		String name = null;
		int index = lexemeProvider.getLexemeFloorIndex(offset);

		while (index >= 0)
		{
			Lexeme<XMLTokenType> lexeme = lexemeProvider.getLexeme(index);

			if (lexeme.getType() == XMLTokenType.EQUAL)
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

	/**
	 * This method looks at the partition that contains the specified offset and from that partition type determines if
	 * the offset is: 1. Within an open tag 2. Within a close tag 3. Within a text area If the partition type is
	 * unrecognized, the ERROR location will be returned.
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	LocationType getCoarseLocationType(IDocument document, LexemeProvider<XMLTokenType> lexemeProvider, int offset)
	{
		LocationType result = LocationType.ERROR;

		try
		{
			ITypedRegion partition = document.getPartition(offset);
			String type = partition.getType();

			if (locationMap.containsKey(type))
			{
				result = locationMap.get(type);

				Lexeme<XMLTokenType> firstLexeme = lexemeProvider.getFirstLexeme();
				Lexeme<XMLTokenType> lastLexeme;

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
									// What if the preceding non-whitespace char isn't '>' and it isn't in the lexemes?
									// We should report in open tag still!
									if (offset == 0)
									{
										result = LocationType.IN_TEXT;
									}
									else
									{
										ITypedRegion previousPartition = document.getPartition(offset - 1);
										String src = document.get(previousPartition.getOffset(), previousPartition.getLength()).trim();
										if (src.charAt(src.length() - 1) == '>')
										{
											result = LocationType.IN_TEXT;
										}
									}
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
										case END_TAG:
										case TAG_SELF_CLOSE:
											if (offset <= lastLexeme.getStartingOffset())
											{
												result = LocationType.IN_OPEN_TAG;
											}
											break;
										case META:
											if (lastLexeme.getText().equalsIgnoreCase("DOCTYPE")) //$NON-NLS-1$
											{
												result = LocationType.IN_DOCTYPE;
											}
											else
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
										case END_TAG:
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	@Override
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		return new char[] { '<', '\'', '"', '&' };
	}

	/**
	 * getCoreLocation
	 * 
	 * @return
	 */
	protected String getCoreLocation()
	{
		return XMLIndexConstants.CORE;
	}

	/**
	 * getElementName
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	private String getElementName(LexemeProvider<XMLTokenType> lexemeProvider, int offset)
	{
		String result = null;
		int index = lexemeProvider.getLexemeFloorIndex(offset);

		for (int i = index; i >= 0; i--)
		{
			Lexeme<XMLTokenType> lexeme = lexemeProvider.getLexeme(i);

			if (lexeme.getType() == XMLTokenType.START_TAG)
			{
				result = lexeme.getText();
				break;
			}
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
	LocationType getOpenTagLocationType(LexemeProvider<XMLTokenType> lexemeProvider, int offset)
	{
		LocationType result = LocationType.ERROR;

		int index = lexemeProvider.getLexemeIndex(offset);

		if (index < 0)
		{
			int candidateIndex = lexemeProvider.getLexemeFloorIndex(offset);
			Lexeme<XMLTokenType> lexeme = lexemeProvider.getLexeme(candidateIndex);

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
			Lexeme<XMLTokenType> lexeme = lexemeProvider.getLexeme(index);

			switch (lexeme.getType())
			{
				case ATTRIBUTE:
					result = LocationType.IN_ATTRIBUTE_NAME;
					break;

				case EQUAL:
					result = (offset <= lexeme.getStartingOffset()) ? LocationType.IN_ATTRIBUTE_NAME : LocationType.IN_ATTRIBUTE_VALUE;
					break;

				case START_TAG:
					result = LocationType.IN_ELEMENT_NAME;
					break;

				case END_TAG:
					if (index >= 1)
					{
						Lexeme<XMLTokenType> previous = lexemeProvider.getLexeme(index - 1);

						if (previous.getEndingOffset() < offset - 1)
						{
							result = LocationType.IN_ATTRIBUTE_NAME;
						}
					}
					break;

				case META:
					if (index >= 1)
					{
						Lexeme<XMLTokenType> previous = lexemeProvider.getLexeme(index - 1);

						switch (previous.getType())
						{
							case META:
							case SINGLE_QUOTED_STRING:
							case DOUBLE_QUOTED_STRING:
								this._replaceRange = this._currentLexeme = lexeme;
								result = LocationType.IN_ATTRIBUTE_NAME;
								break;

							case START_TAG:
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
	 * getUnclosedTagNames
	 * 
	 * @param offset
	 * @return
	 */
	protected Set<String> getUnclosedTagNames(int offset)
	{
		Set<String> unclosedElements = new HashSet<String>();

		try
		{
			ITypedRegion[] partitions = _document.computePartitioning(0, offset);

			for (ITypedRegion partition : partitions)
			{
				if (partition.getType().equals(XMLSourceConfiguration.TAG))
				{
					String src = _document.get(partition.getOffset(), partition.getLength());
					int lessThanIndex = src.indexOf('<');

					if (lessThanIndex == -1 || lessThanIndex >= src.length() - 1)
					{
						continue;
					}

					src = src.substring(lessThanIndex + 1).trim();

					String[] parts = src.split("\\W"); //$NON-NLS-1$

					if (parts == null || parts.length == 0)
					{
						continue;
					}

					// String elementName = parts[0].toLowerCase();
					//					
					// if (!unclosedElements.contains(elementName) && !OpenTagCloser.tagClosed(_document, elementName))
					// {
					// unclosedElements.add(elementName);
					// }
				}
			}
		}
		catch (BadLocationException e)
		{
			// ignore
		}

		return unclosedElements;
	}
}
