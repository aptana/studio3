/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.css.core.index.CSSIndexQueryHelper;
import com.aptana.css.core.index.ICSSIndexConstants;
import com.aptana.css.core.model.ElementElement;
import com.aptana.css.core.model.PropertyElement;
import com.aptana.css.core.model.PseudoClassElement;
import com.aptana.css.core.model.PseudoElementElement;
import com.aptana.css.core.model.ValueElement;
import com.aptana.css.core.parsing.CSSTokenType;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.CompletionProposalComparator;
import com.aptana.editor.common.contentassist.ILexemeProvider;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.internal.text.CSSModelFormatter;
import com.aptana.editor.css.parsing.CSSTokenScanner;
import com.aptana.editor.css.parsing.lexer.CSSLexemeProvider;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;

/**
 * Supplies proposals for content assist in the CSS editor.
 * 
 * @author Kevin Lindsey
 * @author Chris Williams
 */
public class CSSContentAssistProcessor extends CommonContentAssistProcessor
{
	private static final Image ELEMENT_ICON = CSSPlugin.getImage("/icons/element.png"); //$NON-NLS-1$
	private static final Image PROPERTY_ICON = CSSPlugin.getImage("/icons/property.png"); //$NON-NLS-1$

	// @formatter:off
	private static final Set<String> COLOR_PROPERTY_NAMES = CollectionsUtil.newSet(
		"background", //$NON-NLS-1$
		"border-bottom", //$NON-NLS-1$
		"border-left", //$NON-NLS-1$
		"border-right", //$NON-NLS-1$
		"border-top", //$NON-NLS-1$
		"border", //$NON-NLS-1$
		"color", //$NON-NLS-1$
		"background-color", //$NON-NLS-1$
		"border-color", //$NON-NLS-1$
		"border-top-color", //$NON-NLS-1$
		"border-right-color", //$NON-NLS-1$
		"border-bottom-color", //$NON-NLS-1$
		"border-left-color", //$NON-NLS-1$
		"outline-color" //$NON-NLS-1$
	);
	// @formatter:on

	private IContextInformationValidator _validator;
	private CSSIndexQueryHelper _queryHelper;
	private Lexeme<CSSTokenType> _currentLexeme;
	private IRange _replaceRange;

	// NOTE: temp (I hope) until we get proper partitions for CSS inside of HTML
	private IRange _activeRange;

	/**
	 * CSSContentAssistProcessor
	 * 
	 * @param editor
	 */
	public CSSContentAssistProcessor(AbstractThemeableEditor editor)
	{
		super(editor);
		this._queryHelper = new CSSIndexQueryHelper();
	}

	/**
	 * CSSContentAssistProcessor
	 * 
	 * @param editor
	 */
	public CSSContentAssistProcessor(AbstractThemeableEditor editor, IRange activeRange)
	{
		this(editor);
		this._activeRange = activeRange;
	}

	/**
	 * getElementProposals
	 * 
	 * @param proposals
	 * @param offset
	 */
	protected void addAllElementProposals(List<ICompletionProposal> proposals, int offset)
	{
		List<ElementElement> elements = this._queryHelper.getElements();

		if (elements != null)
		{
			for (ElementElement element : elements)
			{
				String description = CSSModelFormatter.ADDITIONAL_INFO.getDocumentation(element);
				List<String> userAgentIdList = element.getUserAgentNames();
				String[] userAgentIds = userAgentIdList.toArray(new String[userAgentIdList.size()]);

				addProposal(proposals, element.getName(), ELEMENT_ICON, description, userAgentIds, offset);
			}
		}
	}

	/**
	 * getAllPropertyProposals
	 * 
	 * @param proposals
	 * @param offset
	 */
	protected void addAllPropertyProposals(List<ICompletionProposal> proposals,
			ILexemeProvider<CSSTokenType> lexemeProvider, int offset)
	{
		List<PropertyElement> properties = this._queryHelper.getProperties();

		if (properties != null)
		{
			String postfix = ": "; //$NON-NLS-1$
			if (this._currentLexeme != null)
			{
				int index = lexemeProvider.getLexemeCeilingIndex(offset);
				Lexeme<CSSTokenType> nextLexeme = lexemeProvider.getLexeme(index + 1);
				if (nextLexeme != null && nextLexeme.getType() == CSSTokenType.COLON)
				{
					postfix = ""; //$NON-NLS-1$
				}
				// don't replace the semicolon when inserting a new property name
				switch (this._currentLexeme.getType())
				{
					case COLON:
						this._replaceRange = this._currentLexeme = lexemeProvider.getLexemeFromOffset(offset - 1);
						postfix = ""; //$NON-NLS-1$
						break;

					case SEMICOLON:
					case LCURLY:
					case RCURLY:
						this._replaceRange = this._currentLexeme = null;
						break;

					default:
						if (!this._currentLexeme.contains(offset)
								&& this._currentLexeme.getEndingOffset() != offset - 1)
						{
							this._replaceRange = this._currentLexeme = null;
						}
						break;
				}
			}

			for (PropertyElement property : properties)
			{
				String description = CSSModelFormatter.ADDITIONAL_INFO.getDocumentation(property);
				List<String> userAgentIdList = property.getUserAgentNames();
				String[] userAgentIds = userAgentIdList.toArray(new String[userAgentIdList.size()]);

				addProposal(proposals, property.getName(), property.getName() + postfix, PROPERTY_ICON, description,
						userAgentIds, offset);
			}
		}
	}

	/**
	 * addClasses
	 * 
	 * @param proposals
	 * @param offset
	 */
	protected void addClasses(List<ICompletionProposal> proposals, int offset)
	{
		Map<String, String> classes = this._queryHelper.getClasses(this.getIndex());

		if (classes != null)
		{
			UserAgentManager manager = UserAgentManager.getInstance();
			String[] userAgentIds = manager.getActiveUserAgentIDs(getProject()); // classes can be used by all user
																					// agents

			for (Entry<String, String> entry : classes.entrySet())
			{
				String name = "." + entry.getKey(); //$NON-NLS-1$
				String location = CSSModelFormatter.getDocumentDisplayName(entry.getValue());

				addProposal(proposals, name, ELEMENT_ICON, null, userAgentIds, location, offset);
			}
		}
	}

	/**
	 * addIDs
	 * 
	 * @param result
	 * @param offset
	 */
	protected void addIDs(List<ICompletionProposal> proposals, int offset)
	{
		Map<String, String> ids = this._queryHelper.getIDs(this.getIndex());

		if (ids != null)
		{
			UserAgentManager manager = UserAgentManager.getInstance();
			String[] userAgentIds = manager.getActiveUserAgentIDs(getProject()); // classes can be used by all user
																					// agents

			for (Entry<String, String> entry : ids.entrySet())
			{
				String name = "#" + entry.getKey(); //$NON-NLS-1$
				String location = CSSModelFormatter.getDocumentDisplayName(entry.getValue());

				addProposal(proposals, name, ELEMENT_ICON, null, userAgentIds, location, offset);
			}
		}
	}

	/**
	 * addInsideRuleProposals
	 * 
	 * @param proposals
	 * @param document
	 * @param offset
	 */
	private void addInsideRuleProposals(List<ICompletionProposal> proposals,
			ILexemeProvider<CSSTokenType> lexemeProvider, int offset)
	{
		LocationType location = this.getInsideLocationType(lexemeProvider, offset);

		// NOTE: The following is a hack to cover CSS in empty attributes in HTML. That's the only time we can both be
		// inside of a rule while having an empty lexeme list
		if (location == LocationType.ERROR && lexemeProvider.size() == 0)
		{
			location = LocationType.INSIDE_PROPERTY;
		}

		switch (location)
		{
			case INSIDE_PROPERTY:
				this.addAllPropertyProposals(proposals, lexemeProvider, offset);
				break;

			case INSIDE_VALUE:
				this.addPropertyValues(proposals, lexemeProvider, offset);
				break;

			default:
				break;
		}
	}

	/**
	 * addOutsideRuleProposals
	 * 
	 * @param proposals
	 * @param document
	 * @param offset
	 */
	private void addOutsideRuleProposals(List<ICompletionProposal> proposals,
			ILexemeProvider<CSSTokenType> lexemeProvider, int offset)
	{
		if (this._currentLexeme != null)
		{
			switch (this._currentLexeme.getType())
			{
				case COMMA:
					this._replaceRange = this._currentLexeme = lexemeProvider.getLexemeFromOffset(offset);
					break;

				case COLON:
					this._replaceRange = null;
					break;

				case GREATER:
					this._replaceRange = null;
					break;

				case LCURLY:
				case RCURLY:
					this._replaceRange = this._currentLexeme = null;
					offset++;
					break;

				case ELEMENT:
				case IDENTIFIER:
					if (offset == this._currentLexeme.getStartingOffset())
					{
						this._replaceRange = this._currentLexeme = null;
					}
					break;
				case RPAREN:
					this._replaceRange = null;
					if (offset <= this._currentLexeme.getStartingOffset())
					{
						this._currentLexeme = lexemeProvider.getLexemeFromOffset(this._currentLexeme
								.getStartingOffset() - 1);
						// case where there is a prefix inside two parens, i.e. html:lang(f|)
						if (this._currentLexeme != null && this._currentLexeme.getType() == CSSTokenType.IDENTIFIER)
						{
							this._replaceRange = this._currentLexeme;
						}
					}
					break;

				default:
					break;
			}
		}

		if (this._currentLexeme != null)
		{
			Lexeme<CSSTokenType> switchLexeme = this._currentLexeme;

			// if we have an identifier, it's likely it's a prefix of a partially-completed proposal
			// find the true position and proposals
			if (switchLexeme.getType() == CSSTokenType.IDENTIFIER)
			{
				Lexeme<CSSTokenType> previous = lexemeProvider.getLexemeFromOffset(this._currentLexeme
						.getStartingOffset() - 1);
				if (previous != null
						&& (previous.getType() == CSSTokenType.COLON || previous.getType() == CSSTokenType.LPAREN))
				{
					switchLexeme = previous;
				}
			}

			switch (switchLexeme.getType())
			{
				case CLASS:
					this.addClasses(proposals, offset);
					break;

				case ID:
					this.addIDs(proposals, offset);
					break;

				case COLON:
					// If previous is also a colon, it's syntax for pseudo elements. One colon means pseudo classes.
					Lexeme<CSSTokenType> previous = lexemeProvider
							.getLexemeFromOffset(switchLexeme.getStartingOffset() - 1);
					if (previous != null && previous.getType() == CSSTokenType.COLON)
					{
						this.addPseudoElementProposals(proposals, offset);
					}
					else
					{
						if (this._currentLexeme.getEndingOffset() < offset)
						{
							this.addPseudoClassProposals(proposals, offset);
						}
						else
						{
							this.addAllElementProposals(proposals, offset);
						}
					}
					break;

				case LPAREN:
					// Back up one, grab identifier as the pseudo-class name
					String pseudoClassName = null;
					Lexeme<CSSTokenType> lex = lexemeProvider.getLexemeFromOffset(switchLexeme.getStartingOffset() - 1);
					if (lex.getType() == CSSTokenType.IDENTIFIER)
					{
						pseudoClassName = lex.getText();
					}
					this.addPseudoClassArguments(pseudoClassName, proposals, offset);
					break;

				case RPAREN:
					break;

				default:
					this.addAllElementProposals(proposals, offset);
					break;
			}
		}
		else
		{
			this.addAllElementProposals(proposals, offset);
		}
	}

	/**
	 * addPropertyValues
	 * 
	 * @param proposals
	 * @param lexemeProvider
	 * @param offset
	 */
	private void addPropertyValues(List<ICompletionProposal> proposals, ILexemeProvider<CSSTokenType> lexemeProvider,
			int offset)
	{
		// get property name
		String propertyName = this.getPropertyName(lexemeProvider, offset);

		if (!StringUtil.isEmpty(propertyName))
		{
			this.setPropertyValueRange(lexemeProvider, offset);

			// lookup value list for property
			PropertyElement property = this._queryHelper.getProperty(propertyName);

			if (property != null)
			{
				List<String> userAgentIdList = property.getUserAgentNames();
				String[] userAgentIds = userAgentIdList.toArray(new String[userAgentIdList.size()]);

				// build proposals from value list
				for (ValueElement value : property.getValues())
				{
					addProposal(proposals, value.getName(), PROPERTY_ICON, value.getDescription(), userAgentIds, offset);
				}
			}

			// If this is a property that supports colors, suggest colors already used in the project
			if (supportsColorValues(property))
			{
				Set<String> colors = this._queryHelper.getColors(getIndex());
				if (colors != null && !colors.isEmpty())
				{
					String[] userAgentIds = getActiveUserAgentIds();

					for (String color : colors)
					{
						ImageRegistry reg = CSSPlugin.getDefault().getImageRegistry();
						Image img = reg.get(color);
						if (img == null)
						{
							// Generate an image from the color value!
							// FIXME Handle colors that aren't 7 chars hex values? Or will they always be normalized to
							// this format?
							String s = color.substring(1, 3);
							int r = Integer.parseInt(s, 16);
							s = color.substring(3, 5);
							int g = Integer.parseInt(s, 16);
							s = color.substring(5, 7);
							int b = Integer.parseInt(s, 16);
							RGB rgb = new RGB(r, g, b);
							PaletteData pd = new PaletteData(new RGB[] { rgb });
							ImageData data = new ImageData(16, 16, 1, pd);
							img = new Image(Display.getCurrent(), data);
							reg.put(color, img);
						}

						addProposal(proposals, color, img, null, userAgentIds, offset);
					}
				}
			}
		}
	}

	/**
	 * Add proposal where name is also the display name. The proposal will be marked as coming from the CSS core.
	 * 
	 * @param proposals
	 * @param name
	 * @param image
	 * @param description
	 * @param userAgents
	 * @param offset
	 */
	protected void addProposal(List<ICompletionProposal> proposals, String name, Image image, String description,
			String[] userAgentIds, int offset)
	{
		addProposal(proposals, name, image, description, userAgentIds, ICSSIndexConstants.CORE, offset);
	}

	/**
	 * Add proposal where name is also the display name.
	 * 
	 * @param proposals
	 * @param name
	 * @param image
	 * @param description
	 * @param userAgents
	 * @param fileLocation
	 * @param offset
	 */
	protected void addProposal(List<ICompletionProposal> proposals, String name, Image image, String description,
			String[] userAgentIds, String fileLocation, int offset)
	{
		addProposal(proposals, name, name, image, description, userAgentIds, fileLocation, offset);
	}

	/**
	 * Add proposal where the location will be marked as coming from the CSS core.
	 * 
	 * @param proposals
	 * @param displayName
	 * @param name
	 * @param image
	 * @param description
	 * @param userAgents
	 * @param offset
	 */
	protected void addProposal(List<ICompletionProposal> proposals, String displayName, String name, Image image,
			String description, String[] userAgentIds, int offset)
	{
		addProposal(proposals, displayName, name, image, description, userAgentIds, ICSSIndexConstants.CORE, offset);
	}

	/**
	 * Add proposal
	 * 
	 * @param proposals
	 * @param displayName
	 * @param name
	 * @param image
	 * @param description
	 * @param userAgents
	 * @param fileLocation
	 * @param offset
	 */
	protected void addProposal(List<ICompletionProposal> proposals, String displayName, String name, Image image,
			String description, String[] userAgentIds, String fileLocation, int offset)
	{
		if (isActiveByUserAgent(userAgentIds))
		{
			ICompletionProposal proposal = createProposal(displayName, name, image, description, userAgentIds,
					fileLocation, offset);

			proposals.add(proposal);
		}
	}

	/**
	 * addPseudoClassArguments
	 * 
	 * @param pseudoClassName
	 * @param proposals
	 * @param offset
	 */
	protected void addPseudoClassArguments(String pseudoClassName, List<ICompletionProposal> proposals, int offset)
	{
		if (pseudoClassName == null)
		{
			return;
		}
		List<PseudoClassElement> classes = this._queryHelper.getPseudoClasses();
		if (classes != null)
		{
			for (PseudoClassElement pseudoClass : classes)
			{
				if (!pseudoClass.getName().equals(pseudoClassName))
				{
					continue;
				}
				List<ValueElement> values = pseudoClass.getValues();
				if (values != null)
				{
					for (ValueElement value : values)
					{
						// String description = CSSModelFormatter.getDescription(value);
						List<String> userAgentIdList = pseudoClass.getUserAgentNames();
						String[] userAgentIds = userAgentIdList.toArray(new String[userAgentIdList.size()]);

						addProposal(proposals, value.getName(), ELEMENT_ICON, value.getDescription(), userAgentIds,
								offset);
					}
				}
				break;
			}
		}
	}

	/**
	 * addPseudoClassProposals
	 * 
	 * @param proposals
	 * @param offset
	 */
	protected void addPseudoClassProposals(List<ICompletionProposal> proposals, int offset)
	{
		List<PseudoClassElement> classes = this._queryHelper.getPseudoClasses();
		if (classes != null)
		{
			for (PseudoClassElement pseudoClass : classes)
			{
				String description = CSSModelFormatter.ADDITIONAL_INFO.getDocumentation(pseudoClass);
				List<String> userAgentIdList = pseudoClass.getUserAgentNames();
				String[] userAgentIds = userAgentIdList.toArray(new String[userAgentIdList.size()]);

				addProposal(proposals, pseudoClass.getName(), ELEMENT_ICON, description, userAgentIds, offset);
			}
		}

		List<PseudoElementElement> elements = this._queryHelper.getPseudoElements();
		if (elements != null)
		{
			for (PseudoElementElement pseudoElement : elements)
			{
				if (!pseudoElement.allowPseudoClassSyntax())
				{
					continue;
				}
				String description = CSSModelFormatter.ADDITIONAL_INFO.getDocumentation(pseudoElement);
				List<String> userAgentIdList = pseudoElement.getUserAgentNames();
				String[] userAgentIds = userAgentIdList.toArray(new String[userAgentIdList.size()]);

				addProposal(proposals, pseudoElement.getName(), ELEMENT_ICON, description, userAgentIds, offset);
			}
		}
	}

	/**
	 * addPseudoElementProposals
	 * 
	 * @param proposals
	 * @param offset
	 */
	protected void addPseudoElementProposals(List<ICompletionProposal> proposals, int offset)
	{
		List<PseudoElementElement> elements = this._queryHelper.getPseudoElements();
		if (elements != null)
		{
			for (PseudoElementElement pseudoElement : elements)
			{
				String description = CSSModelFormatter.ADDITIONAL_INFO.getDocumentation(pseudoElement);
				List<String> userAgentIdList = pseudoElement.getUserAgentNames();
				String[] userAgentIds = userAgentIdList.toArray(new String[userAgentIdList.size()]);

				addProposal(proposals, pseudoElement.getName(), ELEMENT_ICON, description, userAgentIds, offset);
			}
		}
	}

	/**
	 * createLexemeProvider
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	ILexemeProvider<CSSTokenType> createLexemeProvider(IDocument document, int offset)
	{
		// NOTE: temp until we get proper partitions for CSS inside of HTML
		if (this._activeRange != null)
		{
			return new CSSLexemeProvider(document, this._activeRange, new CSSTokenScanner());
		}
		else
		{
			IRange range = getLexemeRange(document, offset);

			return new CSSLexemeProvider(document, range, new CSSTokenScanner());
		}
	}

	/**
	 * createProposal
	 * 
	 * @param displayName
	 * @param name
	 * @param image
	 * @param description
	 * @param userAgents
	 * @param fileLocation
	 * @param offset
	 * @return
	 */
	protected CommonCompletionProposal createProposal(String displayName, String name, Image image, String description,
			String[] userAgentIds, String fileLocation, int offset)
	{
		int length = name.length();
		IContextInformation contextInfo = null;
		int replaceLength = 0;

		if (this._replaceRange != null)
		{
			offset = this._replaceRange.getStartingOffset();
			replaceLength = this._replaceRange.getLength();
		}

		// build proposal
		Image[] userAgents = UserAgentManager.getInstance().getUserAgentImages(getProject(), userAgentIds);

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
	protected ICompletionProposal[] doComputeCompletionProposals(ITextViewer viewer, int offset, char activationChar,
			boolean autoActivated)
	{
		// tokenize the current document
		IDocument document = viewer.getDocument();
		ILexemeProvider<CSSTokenType> lexemeProvider = this.createLexemeProvider(document, offset);

		// store a reference to the lexeme at the current position
		this._currentLexeme = lexemeProvider.getLexemeFromOffset(offset);

		// if nothing's there, see if we're touching a lexeme to the left of the
		// offset
		if (this._currentLexeme == null)
		{
			this._currentLexeme = lexemeProvider.getLexemeFromOffset(offset - 1);
		}

		// replace the current lexeme by default. This may be adjusted as the
		// CA context is fine-tuned below
		this._replaceRange = this._currentLexeme;

		// NOTE: Temp until we get proper partitions for CSS inside of HTML
		// @formatter:off
		LocationType location = (this._activeRange == null) ? this.getCoarseLocationType(lexemeProvider, offset) : LocationType.INSIDE_RULE;
		// @formatter:on

		// create proposal container
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

		switch (location)
		{
			case OUTSIDE_RULE:
				this.addOutsideRuleProposals(result, lexemeProvider, offset);
				break;

			case INSIDE_RULE:
				this.addInsideRuleProposals(result, lexemeProvider, offset);
				break;

			case INSIDE_ARG:
				// TODO: lookup specific property and shows its values
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

		ICompletionProposal[] proposals = result.toArray(new ICompletionProposal[result.size()]);

		// select the current proposal based on the current lexeme
		if (this._currentLexeme != null)
		{
			setSelectedProposal(this._currentLexeme.getText(), proposals);
		}

		// return results
		return proposals;
	}

	/**
	 * getLocation
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	LocationType getCoarseLocationType(ILexemeProvider<CSSTokenType> lexemeProvider, int offset)
	{
		LocationType result = LocationType.ERROR;
		int index = lexemeProvider.getLexemeFloorIndex(offset);

		LOOP: while (index >= 0)
		{
			Lexeme<CSSTokenType> lexeme = lexemeProvider.getLexeme(index);

			switch (lexeme.getType())
			{
				case LCURLY:
					if (lexeme.getEndingOffset() < offset)
					{
						result = LocationType.INSIDE_RULE;
						this._replaceRange = this._currentLexeme = null;
					}
					else
					{
						result = LocationType.OUTSIDE_RULE;
						this._replaceRange = this._currentLexeme = lexemeProvider.getLexemeFromOffset(offset - 1);
					}
					break LOOP;

				case RCURLY:
					result = (lexeme.getEndingOffset() < offset) ? LocationType.OUTSIDE_RULE : LocationType.INSIDE_RULE;
					break LOOP;

				case COLON:
					// try walking left
					for (int i = index - 1; i >= 0; i--)
					{
						Lexeme<CSSTokenType> candidate = lexemeProvider.getLexeme(i);

						switch (candidate.getType())
						{
							case COLOR:
							case SEMICOLON:
							case LCURLY:
								result = LocationType.INSIDE_RULE;
								break LOOP;

							case CLASS:
							case ID:
							case RCURLY:
								result = LocationType.OUTSIDE_RULE;
								break LOOP;
						}
					}

					// try walking right
					for (int i = index + 1; i < lexemeProvider.size(); i++)
					{
						Lexeme<CSSTokenType> candidate = lexemeProvider.getLexeme(i);

						switch (candidate.getType())
						{
							case COLOR:
							case SEMICOLON:
								result = LocationType.INSIDE_RULE;
								break LOOP;

							case CLASS:
							case ID:
							case LCURLY:
								result = LocationType.OUTSIDE_RULE;
								break LOOP;
						}
					}
					result = LocationType.OUTSIDE_RULE;
					break LOOP;

				case PROPERTY:
				case VALUE:
					result = LocationType.INSIDE_RULE;
					break LOOP;

				case MINUS:
				case IDENTIFIER:
					if (lexeme.getText().charAt(0) == '-')
					{
						result = LocationType.INSIDE_RULE;
						break LOOP;
					}
					break;

				default:
					break;
			}

			index--;
		}

		if (index < 0 && result == LocationType.ERROR)
		{
			result = LocationType.OUTSIDE_RULE;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	@Override
	public IContextInformationValidator getContextInformationValidator()
	{
		if (this._validator == null)
		{
			this._validator = new CSSContextInformationValidator();
		}

		return this._validator;
	}

	/**
	 * getIndexOfPreviousColon
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	private int getIndexOfPreviousColon(ILexemeProvider<CSSTokenType> lexemeProvider, int offset)
	{
		int index = lexemeProvider.getLexemeFloorIndex(offset);
		int result = -1;

		for (int i = index; i >= 0; i--)
		{
			Lexeme<CSSTokenType> lexeme = lexemeProvider.getLexeme(i);

			if (lexeme.getType() == CSSTokenType.COLON)
			{
				result = i;
				break;
			}
		}

		return result;
	}

	/**
	 * getInsideLocation
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	LocationType getInsideLocationType(ILexemeProvider<CSSTokenType> lexemeProvider, int offset)
	{
		LocationType location = LocationType.ERROR;

		int index = lexemeProvider.getLexemeIndex(offset);

		if (index < 0)
		{
			int candidateIndex = lexemeProvider.getLexemeFloorIndex(offset);
			Lexeme<CSSTokenType> lexeme = lexemeProvider.getLexeme(candidateIndex);

			if (lexeme != null && lexeme.getEndingOffset() == offset - 1)
			{
				index = candidateIndex;
			}
			else
			{
				index = lexemeProvider.getLexemeCeilingIndex(offset);
			}
		}

		LOOP: while (index >= 0)
		{
			Lexeme<CSSTokenType> lexeme = lexemeProvider.getLexeme(index);

			switch (lexeme.getType())
			{
				case LCURLY:
					location = LocationType.INSIDE_PROPERTY;
					break;
				case RCURLY:
					if (index > 0)
					{
						Lexeme<CSSTokenType> previousLexeme = lexemeProvider.getLexeme(index - 1);

						if (previousLexeme.getEndingOffset() == offset - 1)
						{
							switch (previousLexeme.getType())
							{
								case CLASS:
									location = LocationType.ERROR;
									break LOOP;

								case ID:
									location = LocationType.INSIDE_VALUE;
									break;

								case LCURLY:
								case SEMICOLON:
									location = LocationType.INSIDE_PROPERTY;
									break;

								default:
									break;
							}
						}
						else
						{
							switch (previousLexeme.getType())
							{
								case COLON:
									location = LocationType.INSIDE_VALUE;
									break;

								default:
									location = LocationType.INSIDE_PROPERTY;
									break;
							}
						}
					}
					break;

				case ELEMENT:
				case IDENTIFIER:
				case PROPERTY:
					boolean afterColon = false;

					COLON_LOOP: for (int i = index - 1; i >= 0; i--) // $codepro.audit.disable nonCaseLabelInSwitch
					{
						Lexeme<CSSTokenType> candidate = lexemeProvider.getLexeme(i);

						switch (candidate.getType())
						{
							case COLON:
								afterColon = true;
								break COLON_LOOP;

							case SEMICOLON:
							case LCURLY:
							case RCURLY:
								break COLON_LOOP;
						}
					}

					if (!afterColon)
					{
						if (lexeme.contains(offset) || lexeme.getEndingOffset() == offset - 1)
						{
							this._replaceRange = this._currentLexeme = lexeme;
						}
						else
						{
							this._replaceRange = this._currentLexeme = null;
						}

						location = LocationType.INSIDE_PROPERTY;
					}
					break;

				case SEMICOLON:
					location = (lexeme.getEndingOffset() < offset) ? LocationType.INSIDE_PROPERTY
							: LocationType.INSIDE_VALUE;
					break;

				case COLON:
					location = (lexeme.getEndingOffset() < offset) ? LocationType.INSIDE_VALUE
							: LocationType.INSIDE_PROPERTY;
					break;

				// case ARGS:
				case FUNCTION:
				case VALUE:
					location = LocationType.INSIDE_VALUE;
					break;

				default:
					break;
			}

			if (location != LocationType.ERROR)
			{
				break;
			}
			index--;
		}

		return location;
	}

	/**
	 * getLexemeAfterDelimiter
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	private int getLexemeAfterDelimiter(ILexemeProvider<CSSTokenType> lexemeProvider, int offset)
	{
		int index = lexemeProvider.getLexemeIndex(offset);

		if (index >= 0)
		{
			Lexeme<CSSTokenType> currentLexeme = lexemeProvider.getLexeme(index);

			if (currentLexeme.getType() == CSSTokenType.SEMICOLON)
			{
				index--;
				currentLexeme = lexemeProvider.getLexeme(index);
			}

			for (int i = index; i >= 0; i--)
			{
				Lexeme<CSSTokenType> previousLexeme = (i > 0) ? lexemeProvider.getLexeme(i - 1) : null;

				if (this.isValueDelimiter(currentLexeme) || !previousLexeme.isContiguousWith(currentLexeme))
				{
					// the current lexeme is a natural delimiter or there's a space between this lexeme and the previous
					// lexeme, so treat the previous lexeme like it is the delimiter
					index = i;
					break;
				}
				else
				{
					currentLexeme = previousLexeme;
					index = i;
				}
			}
		}

		return index;
	}

	/**
	 * getLexemeBeforeDelimiter
	 * 
	 * @param lexemeProvider
	 * @param index
	 * @return
	 */
	private Lexeme<CSSTokenType> getLexemeBeforeDelimiter(ILexemeProvider<CSSTokenType> lexemeProvider, int index)
	{
		Lexeme<CSSTokenType> result = null;

		// get the staring lexeme
		Lexeme<CSSTokenType> startingLexeme = lexemeProvider.getLexeme(index);

		if (startingLexeme != null && !this.isValueDelimiter(startingLexeme))
		{
			Lexeme<CSSTokenType> endingLexeme = startingLexeme;

			// advance to next lexeme
			index++;

			while (index < lexemeProvider.size())
			{
				Lexeme<CSSTokenType> candidateLexeme = lexemeProvider.getLexeme(index);

				if (this.isValueDelimiter(candidateLexeme) || !endingLexeme.isContiguousWith(candidateLexeme))
				{
					// we've hit a delimiting lexeme or have passed over whitespace, so we're done
					break;
				}
				// still looking so include this in our range
				endingLexeme = candidateLexeme;

				index++;
			}

			if (index >= lexemeProvider.size())
			{
				endingLexeme = lexemeProvider.getLexeme(lexemeProvider.size() - 1);
			}

			result = endingLexeme;
		}

		return result;
	}

	/**
	 * Return the range required to properly select appropriate lexemes
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	protected IRange getLexemeRange(IDocument document, int offset)
	{
		int startOffset = 0;
		try
		{
			int testOffset = document.get(0, offset).lastIndexOf('}', offset);
			// add one because we don't want to include the closing brace
			startOffset = (testOffset < 0) ? 0 : testOffset + 1;
		}
		catch (BadLocationException e)
		{
		}

		int endOffset = offset;
		try
		{
			ITypedRegion region = document.getPartition(offset);
			endOffset = Math.max(startOffset, region.getOffset() + region.getLength() - 1);
		}
		catch (BadLocationException e)
		{
		}

		return new Range(startOffset, endOffset);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getPreferenceNodeQualifier()
	 */
	protected String getPreferenceNodeQualifier()
	{
		return CSSPlugin.PLUGIN_ID;
	}

	/**
	 * getPropertyName
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	private String getPropertyName(ILexemeProvider<CSSTokenType> lexemeProvider, int offset)
	{
		String result = null;
		int index = this.getIndexOfPreviousColon(lexemeProvider, offset);

		if (index > 0)
		{
			Lexeme<CSSTokenType> lexeme = lexemeProvider.getLexeme(index - 1);

			result = lexeme.getText();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#isValidActivationCharacter(char, int)
	 */
	public boolean isValidActivationCharacter(char c, int keyCode)
	{
		return Character.isWhitespace(c);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#triggerAdditionalAutoActivation(char, int,
	 * org.eclipse.jface.text.IDocument, int)
	 */
	public boolean isValidAutoActivationLocation(char c, int keyCode, IDocument document, int offset)
	{
		EnumSet<CSSTokenType> types = EnumSet.of(CSSTokenType.LCURLY, CSSTokenType.COMMA, CSSTokenType.COLON,
				CSSTokenType.SEMICOLON, CSSTokenType.CLASS, CSSTokenType.ID);

		ILexemeProvider<CSSTokenType> lexemeProvider = this.createLexemeProvider(document, offset);
		if (offset > 0)
		{
			Lexeme<CSSTokenType> lexeme = lexemeProvider.getFloorLexeme(offset - 1);
			return (lexeme != null) ? types.contains(lexeme.getType()) : false;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#isValidIdentifier(char, int)
	 */
	public boolean isValidIdentifier(char c, int keyCode)
	{
		return ('A' <= keyCode && keyCode <= 'Z') || ('a' <= keyCode && keyCode <= 'z') || c == '_' || c == '#'
				|| c == '.' || c == '-';
	}

	/**
	 * isValueDelimiter
	 * 
	 * @param lexeme
	 * @return
	 */
	private boolean isValueDelimiter(Lexeme<CSSTokenType> lexeme)
	{
		boolean result = false;

		switch (lexeme.getType())
		{
			case COLON:
			case COMMA:
			case LCURLY:
			case RCURLY:
			case SEMICOLON:
				result = true;
				break;

			default:
				result = false;
				break;
		}

		return result;
	}

	/**
	 * The currently active range
	 * 
	 * @param activeRange
	 */
	public void setActiveRange(IRange activeRange)
	{
		this._activeRange = activeRange;
	}

	/**
	 * setPropertyValueRange
	 * 
	 * @param lexemeProvider
	 * @param offset
	 */
	private void setPropertyValueRange(ILexemeProvider<CSSTokenType> lexemeProvider, int offset)
	{
		int index = this.getLexemeAfterDelimiter(lexemeProvider, offset);

		// get the staring lexeme
		Lexeme<CSSTokenType> endingLexeme = (index >= 0) ? this.getLexemeBeforeDelimiter(lexemeProvider, index) : null;

		if (endingLexeme != null)
		{
			Lexeme<CSSTokenType> startingLexeme = lexemeProvider.getLexeme(index);

			this._replaceRange = new Range(startingLexeme.getStartingOffset(), endingLexeme.getEndingOffset());
		}
		else
		{

			if (this._currentLexeme != null
					&& (this._currentLexeme.contains(offset) || this._currentLexeme.getEndingOffset() == offset - 1))
			{
				switch (this._currentLexeme.getType())
				{
					case COLON:
						this._replaceRange = this._currentLexeme = null;
						break;

					case LCURLY:
						this._replaceRange = this._currentLexeme = null;
						break;

					case RCURLY:
						Lexeme<CSSTokenType> candidate = lexemeProvider.getLexemeFromOffset(offset - 1);

						if (candidate != null && !this.isValueDelimiter(candidate))
						{
							this._replaceRange = this._currentLexeme = lexemeProvider.getLexemeFromOffset(offset - 1);
						}
						else
						{
							this._replaceRange = this._currentLexeme = null;
						}
						break;

					case SEMICOLON:
						this._replaceRange = this._currentLexeme = lexemeProvider.getLexemeFromOffset(offset - 1);
						break;

					default:
						this._replaceRange = this._currentLexeme;
						break;
				}
			}
			else
			{
				this._replaceRange = this._currentLexeme = null;
			}
		}
	}

	/**
	 * Sorts the completion proposals (by default, by display string). This inclusion is temporary as the only reason we
	 * want to interleave proposals in CSS is because the activation characters are poorly constructed.
	 * 
	 * @param proposals
	 */
	protected void sortProposals(ICompletionProposal[] proposals)
	{
		// Sort by relevance first, descending, and then alphabetically, ascending
		Arrays.sort(proposals, CompletionProposalComparator.descending(CompletionProposalComparator.getComparator(
				CompletionProposalComparator.RelevanceSort, CompletionProposalComparator.NameSort)));
	}

	/**
	 * supportsColorValues
	 * 
	 * @param property
	 * @return
	 */
	private boolean supportsColorValues(PropertyElement property)
	{
		// FIXME Support multiple types on properties, and use an enum of types. Then we can look for color type for
		// values!
		if (property != null)
		{
			String propertyName = property.getName();
			return COLOR_PROPERTY_NAMES.contains(propertyName);
		}

		return false;
	}
}
