/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.contentassist;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Image;

import com.aptana.core.IMap;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.ILexemeProvider;
import com.aptana.editor.xml.TagUtil;
import com.aptana.editor.xml.XMLPlugin;
import com.aptana.editor.xml.XMLSourceConfiguration;
import com.aptana.editor.xml.XMLTagScanner;
import com.aptana.editor.xml.internal.XMLLexemeProvider;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;
import com.aptana.xml.core.index.IXMLIndexConstants;
import com.aptana.xml.core.index.XMLIndexQueryHelper;
import com.aptana.xml.core.model.AttributeElement;
import com.aptana.xml.core.model.ElementElement;
import com.aptana.xml.core.model.ValueElement;
import com.aptana.xml.core.parsing.XMLTokenType;

public class XMLContentAssistProcessor extends CommonContentAssistProcessor
{
	private final class AttributeProposalMapper implements IMap<String, ICompletionProposal>
	{
		private final String suffix;
		private final ElementElement element;
		private final int length;
		private final int theOffset;

		private AttributeProposalMapper(String suffix, ElementElement element, int length, int theOffset)
		{
			this.suffix = suffix;
			this.element = element;
			this.length = length;
			this.theOffset = theOffset;
		}

		public ICompletionProposal map(String attribute)
		{
			String replaceString = attribute + suffix;
			int[] positions;
			if (suffix.length() == 0)
			{
				positions = new int[] { replaceString.length() };
			}
			else
			{
				positions = new int[] { replaceString.length() - 1, replaceString.length() };
			}
			AttributeElement attr = _queryHelper.getAttribute(element.getName(), attribute);
			return new XMLAttributeProposal(attr, replaceString, theOffset, length, positions);
		}
	}

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

		// fine-grain locations
		IN_ELEMENT_NAME,
		IN_ATTRIBUTE_NAME,
		IN_ATTRIBUTE_VALUE
	};

	public static final Image ELEMENT_ICON = XMLPlugin.getImage("/icons/element.png"); //$NON-NLS-1$
	static final Image ATTRIBUTE_ICON = XMLPlugin.getImage("/icons/attribute.png"); //$NON-NLS-1$

	protected XMLIndexQueryHelper _queryHelper;
	protected Lexeme<XMLTokenType> _currentLexeme;
	private IRange _replaceRange;
	private IDocument _document;

	/**
	 * XMLIndexContentAssistProcessor
	 * 
	 * @param editor
	 */
	public XMLContentAssistProcessor(AbstractThemeableEditor editor)
	{
		super(editor);

		this._queryHelper = createQueryHelper();
	}

	protected XMLIndexQueryHelper createQueryHelper()
	{
		return new XMLIndexQueryHelper();
	}

	/**
	 * addAttributeAndEventProposals
	 * 
	 * @param lexemeProvider
	 * @param offset
	 */
	protected List<ICompletionProposal> addAttributeProposals(ILexemeProvider<XMLTokenType> lexemeProvider, int offset)
	{
		final ElementElement element = getElement(lexemeProvider, offset);
		if (element == null)
		{
			return Collections.emptyList();
		}

		String postfix = "=\"\""; //$NON-NLS-1$
		switch (this._currentLexeme.getType())
		{
			case EQUAL:
				int index = lexemeProvider.getLexemeFloorIndex(offset);

				if (index > 0)
				{
					this._replaceRange = this._currentLexeme = lexemeProvider.getLexeme(index - 1);
					postfix = StringUtil.EMPTY;
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
					postfix = StringUtil.EMPTY;
				}
				break;
		}

		int replaceLength = 0;
		if (this._replaceRange != null)
		{
			offset = this._replaceRange.getStartingOffset();
			replaceLength = this._replaceRange.getLength();
		}
		return CollectionsUtil.map(element.getAttributes(), new AttributeProposalMapper(postfix, element,
				replaceLength, offset));
	}

	protected ElementElement getElement(ILexemeProvider<XMLTokenType> lexemeProvider, final int offset)
	{
		String elementName = getElementName(lexemeProvider, offset);
		return this._queryHelper.getElement(elementName);
	}

	/**
	 * addAttributeValueProposals
	 * 
	 * @param offset
	 * @param elementName
	 * @param attributeName
	 */
	protected List<ICompletionProposal> addAttributeValueProposals(int offset, String elementName, String attributeName)
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
	private List<ICompletionProposal> addAttributeValueProposals(ILexemeProvider<XMLTokenType> lexemeProvider,
			int offset)
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
	private List<ICompletionProposal> addCloseTagProposals(ILexemeProvider<XMLTokenType> lexemeProvider, int offset)
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
	protected List<ICompletionProposal> addElementProposals(ILexemeProvider<XMLTokenType> lexemeProvider, int offset)
	{
		List<ElementElement> elements = getElements(lexemeProvider, offset);
		// return early if no elements
		if (elements == null)
		{
			return Collections.emptyList();
		}

		boolean close = true;
		int replaceLength = 0;
		int replaceOffset = offset;

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
				replaceOffset = _currentLexeme.getStartingOffset();
				replaceLength = _currentLexeme.getLength();

				if (!nextLexeme.equals(this._currentLexeme))
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
		// Track tag names to enforce unique proposals by tag name (no repeats)
		Set<String> uniques = new HashSet<String>(elements.size());

		// TODO If user doesn't want tags closed for them, then don't do it!
		// boolean addCloseTag = XMLPlugin.getDefault().getPreferenceStore()
		// .getBoolean(IPreferenceConstants.XML_AUTO_CLOSE_TAG_PAIRS);
		boolean addCloseTag = true;

		String documentText = _document.get();

		// Generate proposals.
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		for (ElementElement element : elements)
		{
			// Enforce unique proposals for elements (avoid duplicates)
			String tagName = element.getName();
			if (uniques.contains(tagName))
			{
				continue;
			}

			StringBuilder replacement = new StringBuilder(element.getName());
			List<Integer> positions = new ArrayList<Integer>();
			int cursorPosition = replacement.length();
			if (close)
			{
				if (element.getName().charAt(0) == '!') // don't close DOCTYPE with a slash
				{
					cursorPosition += 1;
					// Don't add ">" unless we know we need it! Look at next Lexeme!
					int index = lexemeProvider.getLexemeIndex(_currentLexeme.getStartingOffset());
					Lexeme<XMLTokenType> nextLexeme = lexemeProvider.getLexeme(index + 1);
					if (nextLexeme == null || nextLexeme.getType() == XMLTokenType.START_TAG)
					{
						replacement.append(" >"); //$NON-NLS-1$
					}
				}
				else if (isEmptyTagType(element))
				{
					replacement.append(" />"); //$NON-NLS-1$
					// TODO Depending on tag, we should stick cursor inside the tag or after the end of tag. Right
					// now it's stuck at end of tag
					positions.add(cursorPosition + " />".length());
				}
				else
				{
					// If the tag doesn't exist in the doc, we get back that it's closed. We need to copy the
					// document and insert the tag into it
					IDocument doc = new Document(documentText);
					try
					{
						doc.replace(replaceOffset, replaceLength, element.getName() + ">"); //$NON-NLS-1$
					}
					catch (BadLocationException e)
					{
						IdeLog.logWarning(XMLPlugin.getDefault(), MessageFormat.format(
								"Error replacing document text at offset {0} with text {1}", replaceOffset, //$NON-NLS-1$
								element.getName() + ">"), e); //$NON-NLS-1$
					}
					if (addCloseTag && !TagUtil.tagClosed(doc, element.getName()))
					{
						replacement.append("></").append(element.getName()).append('>'); //$NON-NLS-1$
						positions.add(cursorPosition + 1); // between tags
						positions.add(cursorPosition + 4 + element.getName().length()); // after close tag
					}
					else
					{
						replacement.append('>');
						positions.add(cursorPosition + 1);
					}
				}
			}
			positions.add(0, cursorPosition);
			CommonCompletionProposal proposal = createElementProposal(replaceLength, replaceOffset, element,
					replacement, positions);
			proposals.add(proposal);
			uniques.add(tagName);
		}

		return proposals;
	}

	protected XMLTagProposal createElementProposal(int replaceLength, int replaceOffset, ElementElement element,
			StringBuilder replacement, List<Integer> positions)
	{
		return new XMLTagProposal(replacement.toString(), replaceOffset, replaceLength, element,
				positions.toArray(new Integer[positions.size()]));
	}

	private boolean isEmptyTagType(ElementElement element)
	{
		// TODO Once we have somewhere we can look this info up, do so
		return false;
	}

	protected List<ElementElement> getElements(ILexemeProvider<XMLTokenType> lexemeProvider, int offset)
	{
		return this._queryHelper.getElements();
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
	private void addProposal(List<ICompletionProposal> proposals, String name, Image image, String description,
			Image[] userAgents, int offset)
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
	private void addProposal(List<ICompletionProposal> proposals, String name, Image image, String description,
			Image[] userAgents, String fileLocation, int offset)
	{
		CommonCompletionProposal proposal = createProposal(name, image, description, userAgents, fileLocation, offset);
		// add it to the list
		proposals.add(proposal);
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
		CommonCompletionProposal proposal = new CommonCompletionProposal(replaceString, offset, replaceLength,
				cursorPosition, ELEMENT_ICON, element.getName(), null, element.getDescription());

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
	ILexemeProvider<XMLTokenType> createLexemeProvider(IDocument document, int offset)
	{
		int documentLength = document.getLength();

		// account for last position returning an empty IDocument default partition
		int lexemeProviderOffset = (offset >= documentLength) ? documentLength - 1 : offset;

		return new XMLLexemeProvider(document, lexemeProviderOffset, new XMLTagScanner()
		{
			@Override
			protected IToken createToken(XMLTokenType type)
			{
				return new Token(type);
			}
		});
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
	private CommonCompletionProposal createProposal(String name, Image image, String description, Image[] userAgents,
			String fileLocation, int offset)
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
	protected CommonCompletionProposal createProposal(String displayName, String name, Image image, String description,
			Image[] userAgents, String fileLocation, int offset, int length)
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
		CommonCompletionProposal proposal = new CommonCompletionProposal(name, offset, replaceLength, length, image,
				displayName, contextInfo, description);
		proposal.setFileLocation(fileLocation);
		proposal.setUserAgentImages(userAgents);
		proposal.setTriggerCharacters(getProposalTriggerCharacters());
		return proposal;
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
		// tokenize the current document
		this._document = viewer.getDocument();

		ILexemeProvider<XMLTokenType> lexemeProvider = createLexemeProvider(_document, (offset > 0) ? offset - 1
				: offset);

		// store a reference to the lexeme at the current position
		Lexeme<XMLTokenType> tempLexeme = lexemeProvider.getLexemeFromOffset(offset);
		if (tempLexeme != null)
		{
			this._replaceRange = this._currentLexeme = tempLexeme;
		}
		else
		{
			this._replaceRange = null;
			this._currentLexeme = lexemeProvider.getFloorLexeme(offset);
		}

		// first step is to determine if we're inside an open tag, close tag, text, etc.
		LocationType location = getCoarseLocationType(lexemeProvider, offset);

		List<ICompletionProposal> result = Collections.emptyList();
		switch (location)
		{
			case IN_OPEN_TAG:
				LocationType fineLocation = getFineTagLocationType(lexemeProvider, offset);
				switch (fineLocation)
				{
					case IN_ELEMENT_NAME:
						result = addElementProposals(lexemeProvider, offset);
						break;

					case IN_ATTRIBUTE_NAME:
						result = addAttributeProposals(lexemeProvider, offset);
						break;

					case IN_ATTRIBUTE_VALUE:
						result = addAttributeValueProposals(lexemeProvider, offset);
						break;
				}
				break;

			case IN_CLOSE_TAG:
				result = addCloseTagProposals(lexemeProvider, offset);
				break;

			default:
				return NO_PROPOSALS;
		}

		if (!CollectionsUtil.isEmpty(result))
		{
			// sort by display name
			Collections.sort(result, new Comparator<ICompletionProposal>()
			{
				public int compare(ICompletionProposal o1, ICompletionProposal o2)
				{
					return o1.getDisplayString().compareToIgnoreCase(o2.getDisplayString());
				}
			});

			ICompletionProposal[] proposals = result.toArray(new ICompletionProposal[result.size()]);

			// select the current proposal based on the current lexeme
			if (this._replaceRange != null)
			{
				try
				{
					String text = _document.get(this._replaceRange.getStartingOffset(), this._replaceRange.getLength());
					setSelectedProposal(text, proposals);
				}
				catch (BadLocationException e)
				{
				}
			}

			// return results
			return proposals;
		}
		return NO_PROPOSALS;
	}

	/**
	 * getAttributeName
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	private String getAttributeName(ILexemeProvider<XMLTokenType> lexemeProvider, int offset)
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
	private LocationType getCoarseLocationType(ILexemeProvider<XMLTokenType> lexemeProvider, int offset)
	{
		if (_currentLexeme == null)
		{
			return LocationType.ERROR;
		}

		Lexeme<XMLTokenType> lexeme = _currentLexeme;
		int index = lexemeProvider.getLexemeIndex(lexeme.getStartingOffset());
		while (true)
		{
			switch (lexeme.getType())
			{
				case DOCTYPE:
					return LocationType.IN_DOCTYPE;

				case COMMENT:
					return LocationType.IN_COMMENT;

				case CDATA:
				case TEXT:
					return LocationType.IN_TEXT;

				case START_TAG: // '<' or '</'
					// Could be open or close tag!
					if (lexeme.getText().endsWith("/")) //$NON-NLS-1$
					{
						return LocationType.IN_CLOSE_TAG;
					}
					return LocationType.IN_OPEN_TAG;

				case TAG_SELF_CLOSE: // '/>'
					return LocationType.IN_OPEN_TAG;

				default:
				case TAG_NAME:
				case END_TAG: // '>'
					// Backtrack to see if we're in open or close tag...
					break;
			}

			index--; // go back one lexeme and retry
			if (index < 0)
			{
				break;
			}
			lexeme = lexemeProvider.getLexeme(index);
		}

		return LocationType.ERROR;
	}

	private LocationType getFineTagLocationType(ILexemeProvider<XMLTokenType> lexemeProvider, int offset)
	{
		if (_currentLexeme == null)
		{
			return LocationType.ERROR;
		}

		switch (_currentLexeme.getType())
		{
			case START_TAG: // '<' or '</'
				return LocationType.IN_ELEMENT_NAME;

			case END_TAG: // '>'
			case TAG_SELF_CLOSE: // '/>'
				// we need to backtrack to determine if we're in attribute
				// name/value/element name (or even outside the end of the tag!)
				int index = lexemeProvider.getLexemeIndex(offset);
				Lexeme<XMLTokenType> previous = lexemeProvider.getLexeme(index - 1);
				if (previous == null)
				{
					return LocationType.ERROR;
				}
				switch (previous.getType())
				{
					case SINGLE_QUOTED_STRING:
					case DOUBLE_QUOTED_STRING:
						return LocationType.IN_ATTRIBUTE_NAME;

					case EQUAL:
						return LocationType.IN_ATTRIBUTE_VALUE;

					case START_TAG:
						return LocationType.IN_ELEMENT_NAME;

					case TAG_NAME:
						// If there's no space between previous lexeme and current, it's continuation of element name
						if (previous.isContiguousWith(_currentLexeme))
						{
							// Extend the replacement back?
							this._replaceRange = this._currentLexeme = previous;
							return LocationType.IN_ELEMENT_NAME;
						}
						return LocationType.IN_ATTRIBUTE_NAME;

					case ATTRIBUTE:
						// If there's no space between previous lexeme and current, it's continuation of attribute name
						if (previous.isContiguousWith(_currentLexeme))
						{
							// Extend the replacement back?
							this._replaceRange = this._currentLexeme = previous;
						}
						return LocationType.IN_ATTRIBUTE_NAME;

					default:
						break;
				}
				return LocationType.IN_ATTRIBUTE_NAME;

			case ATTRIBUTE:
				return LocationType.IN_ATTRIBUTE_NAME;
			case EQUAL:
				if (offset == _currentLexeme.getStartingOffset())
				{
					// before equal sign. look back to see if we're on attribute name?
					return LocationType.IN_ATTRIBUTE_NAME;
				}
				return LocationType.IN_ATTRIBUTE_VALUE;
			case SINGLE_QUOTED_STRING:
			case DOUBLE_QUOTED_STRING:
				// Are we past end of string in whitespace?
				if (offset > _currentLexeme.getEndingOffset() + 1)
				{
					return LocationType.IN_ATTRIBUTE_NAME;
				}
				return LocationType.IN_ATTRIBUTE_VALUE;
			case TAG_NAME:
				// Are we past end of element name in whitespace?
				if (offset > _currentLexeme.getEndingOffset() + 1)
				{
					return LocationType.IN_ATTRIBUTE_NAME;
				}
				return LocationType.IN_ELEMENT_NAME;

			default:
			case CDATA:
			case TEXT:
			case DOCTYPE:
			case COMMENT:
				return LocationType.ERROR;
		}

	}

	/**
	 * getCoreLocation
	 * 
	 * @return
	 */
	protected String getCoreLocation()
	{
		return IXMLIndexConstants.CORE;
	}

	/**
	 * getElementName
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	protected String getElementName(ILexemeProvider<XMLTokenType> lexemeProvider, int offset)
	{
		String result = null;
		int index = lexemeProvider.getLexemeFloorIndex(offset);

		for (int i = index; i >= 0; i--)
		{
			Lexeme<XMLTokenType> lexeme = lexemeProvider.getLexeme(i);

			if (lexeme.getType() == XMLTokenType.TAG_NAME)
			{
				result = lexeme.getText();
				break;
			}
		}

		if (result != null)
		{
			if (result.startsWith("<")) //$NON-NLS-1$
			{
				result = result.substring(1);
			}
			if (result.endsWith(">")) //$NON-NLS-1$
			{
				result = result.substring(0, result.length() - 1);
			}
			result = result.trim();
			if (result.indexOf(' ') != -1)
			{
				result = result.substring(0, result.indexOf(' '));
				result = result.trim();
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
				}
			}
		}
		catch (BadLocationException e)
		{
			// ignore
		}

		return unclosedElements;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getPreferenceNodeQualifier()
	 */
	protected String getPreferenceNodeQualifier()
	{
		return XMLPlugin.PLUGIN_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#triggerAdditionalAutoActivation(char, int,
	 * org.eclipse.jface.text.IDocument, int)
	 */
	public boolean isValidAutoActivationLocation(char c, int keyCode, IDocument document, int offset)
	{
		ILexemeProvider<XMLTokenType> lexemeProvider = this.createLexemeProvider(document, offset);

		// first step is to determine if we're inside an open tag, close tag, text, etc.
		LocationType location = getCoarseLocationType(lexemeProvider, offset);

		switch (location)
		{
			case IN_OPEN_TAG:
				// If we are inside an open tag and typing space or tab, assume we're wanting to add attributes
				if (c == ' ' || c == '\t')
				{
					return true;
				}
				// If that's not the case, check if we are actually typing the attribute name
				LocationType fineLocation = getFineTagLocationType(lexemeProvider, offset);
				return (fineLocation == LocationType.IN_ATTRIBUTE_NAME)
						|| (fineLocation == LocationType.IN_ATTRIBUTE_VALUE);
			default:
				return false;
		}
	}

}
