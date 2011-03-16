/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.CSSScopeScanner;
import com.aptana.editor.css.CSSSourceEditor;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.contentassist.model.PseudoClassElement;
import com.aptana.editor.css.contentassist.model.PseudoElementElement;
import com.aptana.editor.css.contentassist.model.ValueElement;
import com.aptana.editor.css.parsing.lexer.CSSTokenType;
import com.aptana.editor.css.preferences.IPreferenceConstants;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;

public class CSSContentAssistProcessor extends CommonContentAssistProcessor
{
	private static final Image ELEMENT_ICON = CSSPlugin.getImage("/icons/element.png"); //$NON-NLS-1$
	private static final Image PROPERTY_ICON = CSSPlugin.getImage("/icons/property.png"); //$NON-NLS-1$

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
	public CSSContentAssistProcessor(AbstractThemeableEditor editor, IRange activeRange)
	{
		this(editor);

		this._activeRange = activeRange;
	}

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
				String description = CSSModelFormatter.getDescription(element);
				List<String> userAgents = element.getUserAgentNames();
				Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);

				proposals.add(createProposal(element.getName(), ELEMENT_ICON, description, userAgentIcons, offset));
			}
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
						List<String> userAgents = pseudoClass.getUserAgentNames();
						Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);

						proposals.add(createProposal(value.getName(), ELEMENT_ICON, value.getDescription(),
								userAgentIcons, offset));
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
				String description = CSSModelFormatter.getDescription(pseudoClass);
				List<String> userAgents = pseudoClass.getUserAgentNames();
				Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);

				proposals.add(createProposal(pseudoClass.getName(), ELEMENT_ICON, description, userAgentIcons, offset));
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
				String description = CSSModelFormatter.getDescription(pseudoElement);
				List<String> userAgents = pseudoElement.getUserAgentNames();
				Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);

				proposals
						.add(createProposal(pseudoElement.getName(), ELEMENT_ICON, description, userAgentIcons, offset));
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
				String description = CSSModelFormatter.getDescription(pseudoElement);
				List<String> userAgents = pseudoElement.getUserAgentNames();
				Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);

				proposals
						.add(createProposal(pseudoElement.getName(), ELEMENT_ICON, description, userAgentIcons, offset));
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
			LexemeProvider<CSSTokenType> lexemeProvider, int offset)
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

					case PROPERTY:
						if (offset == this._currentLexeme.getStartingOffset())
						{
							this._replaceRange = this._currentLexeme = null;
						}
						break;

					default:
						if (this._currentLexeme.contains(offset) == false
								&& this._currentLexeme.getEndingOffset() != offset - 1)
						{
							this._replaceRange = this._currentLexeme = null;
						}
						break;
				}
			}

			for (PropertyElement property : properties)
			{
				String description = CSSModelFormatter.getDescription(property);
				List<String> userAgents = property.getUserAgentNames();
				Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);

				proposals.add(createProposal(property.getName(), property.getName() + postfix, PROPERTY_ICON,
						description, userAgentIcons, offset));
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
			String[] userAgents = manager.getActiveUserAgentIDs(); // classes can be used by all user agents
			Image[] userAgentIcons = manager.getUserAgentImages(userAgents);

			for (Entry<String, String> entry : classes.entrySet())
			{
				proposals.add(createProposal("." + entry.getKey(), ELEMENT_ICON, null, userAgentIcons, offset)); //$NON-NLS-1$
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
			String[] userAgents = manager.getActiveUserAgentIDs(); // classes can be used by all user agents
			Image[] userAgentIcons = manager.getUserAgentImages(userAgents);

			for (Entry<String, String> entry : ids.entrySet())
			{
				proposals.add(createProposal("#" + entry.getKey(), ELEMENT_ICON, null, userAgentIcons, offset)); //$NON-NLS-1$
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
			LexemeProvider<CSSTokenType> lexemeProvider, int offset)
	{
		LocationType location = this.getInsideLocationType(lexemeProvider, offset);

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
			LexemeProvider<CSSTokenType> lexemeProvider, int offset)
	{
		if (this._currentLexeme != null)
		{
			switch (this._currentLexeme.getType())
			{
				case COMMA:
					int index = lexemeProvider.getLexemeCeilingIndex(offset);
					this._replaceRange = this._currentLexeme = lexemeProvider.getLexeme(index + 1);
					break;

				case COLON:
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
					this._currentLexeme = lexemeProvider
							.getLexemeFromOffset(this._currentLexeme.getStartingOffset() - 1);
					break;

				default:
					break;
			}
		}

		if (this._currentLexeme != null)
		{
			switch (this._currentLexeme.getType())
			{
				case CLASS:
					this.addClasses(proposals, offset);
					break;

				case ID:
					this.addIDs(proposals, offset);
					break;
				case COLON:
					// If previous is also a colon, it's syntax for pseudo elements. One colon means pseudo classes.
					Lexeme<CSSTokenType> previous = lexemeProvider.getLexemeFromOffset(this._currentLexeme
							.getStartingOffset() - 1);
					if (previous != null && previous.getType() == CSSTokenType.COLON)
					{
						this.addPseudoElementProposals(proposals, offset);
					}
					else
					{
						this.addPseudoClassProposals(proposals, offset);
					}
					break;
				case LPAREN:
					// Back up one, grab identifier as the pseudo-class name
					String pseudoClassName = null;
					Lexeme<CSSTokenType> lex = lexemeProvider.getLexemeFromOffset(this._currentLexeme
							.getStartingOffset() - 1);
					if (lex.getType() == CSSTokenType.IDENTIFIER)
					{
						pseudoClassName = lex.getText();
					}
					this.addPseudoClassArguments(pseudoClassName, proposals, offset);
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
	private void addPropertyValues(List<ICompletionProposal> proposals, LexemeProvider<CSSTokenType> lexemeProvider,
			int offset)
	{
		// get property name
		String propertyName = this.getPropertyName(lexemeProvider, offset);

		if (propertyName != null && propertyName.length() > 0)
		{
			this.setPropertyValueRange(lexemeProvider, offset);

			// lookup value list for property
			PropertyElement property = this._queryHelper.getProperty(propertyName);

			if (property != null)
			{
				Image[] userAgentIcons = UserAgentManager.getInstance()
						.getUserAgentImages(property.getUserAgentNames());

				// build proposals from value list
				for (ValueElement value : property.getValues())
				{
					proposals.add(createProposal(value.getName(), PROPERTY_ICON, value.getDescription(),
							userAgentIcons, offset));
				}
			}

			// If this is a property that supports colors, suggest colors already used in the project
			if (supportsColorValues(property))
			{
				Set<String> colors = this._queryHelper.getColors(getIndex());
				if (colors != null && !colors.isEmpty())
				{
					Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(
							UserAgentManager.getInstance().getActiveUserAgentIDs());
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
						proposals.add(createProposal(color, img, null, userAgentIcons, offset));
					}
				}
			}
		}
	}

	@SuppressWarnings("nls")
	private boolean supportsColorValues(PropertyElement property)
	{
		// FIXME Support multiple types on properties, and use an enum of types. Then we can look for color type for
		// values!
		if (property == null)
			return false;
		String propertyName = property.getName();
		if (propertyName.equals("background") || propertyName.equals("border-bottom")
				|| propertyName.equals("border-left") || propertyName.equals("border-right")
				|| propertyName.equals("border-top") || propertyName.equals("border")
				|| propertyName.equals("column-rule"))
			return true;
		return propertyName.endsWith("color");
	}

	protected CommonCompletionProposal createProposal(String name, Image image, String description, Image[] userAgents,
			int offset)
	{
		return createProposal(name, name, image, description, userAgents, offset);
	}

	protected CommonCompletionProposal createProposal(String displayName, String name, Image image, String description,
			Image[] userAgents, int offset)
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
		CommonCompletionProposal proposal = new CommonCompletionProposal(name, offset, replaceLength, length, image,
				displayName, contextInfo, description);
		proposal.setFileLocation(CSSIndexConstants.CORE);
		proposal.setUserAgentImages(userAgents);
		return proposal;
	}

	protected ICompletionProposal[] doComputeCompletionProposals(ITextViewer viewer, int offset, char activationChar,
			boolean autoActivated)
	{
		// tokenize the current document
		IDocument document = viewer.getDocument();
		LexemeProvider<CSSTokenType> lexemeProvider = this.createLexemeProvider(document, offset);

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

		// select the current proposal based on the current lexeme
		if (this._currentLexeme != null)
		{
			this.setSelectedProposal(this._currentLexeme.getText(), result);
		}

		// return results
		return result.toArray(new ICompletionProposal[result.size()]);
	}

	/**
	 * createLexemeProvider
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	LexemeProvider<CSSTokenType> createLexemeProvider(IDocument document, int offset)
	{
		// NOTE: temp until we get proper partitions for CSS inside of HTML
		if (this._activeRange != null)
		{
			return new LexemeProvider<CSSTokenType>(document, this._activeRange, new CSSScopeScanner())
			{
				@Override
				protected CSSTokenType getTypeFromData(Object data)
				{
					return (CSSTokenType) data;
				}
			};
		}
		else
		{
			return new LexemeProvider<CSSTokenType>(document, offset, new CSSScopeScanner())
			{
				@Override
				protected CSSTokenType getTypeFromData(Object data)
				{
					return (CSSTokenType) data;
				}
			};
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	@Override
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		String chars = Platform.getPreferencesService().getString( //
				CSSPlugin.PLUGIN_ID, //
				IPreferenceConstants.CSS_ACTIVATION_CHARACTERS, //
				"", //$NON-NLS-1$
				null //
				);

		return (chars != null) ? chars.toCharArray() : null;
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
	private int getIndexOfPreviousColon(LexemeProvider<CSSTokenType> lexemeProvider, int offset)
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
	LocationType getInsideLocationType(LexemeProvider<CSSTokenType> lexemeProvider, int offset)
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
								case ID:
									location = LocationType.ERROR;
									break LOOP;

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
					if (index > 0)
					{
						Lexeme<CSSTokenType> previousLexeme = lexemeProvider.getLexeme(index - 1);

						if (previousLexeme.getType() == CSSTokenType.COLON)
						{
							this._replaceRange = this._currentLexeme = lexeme;
							location = LocationType.INSIDE_VALUE;
							break;
						}
					}

					if (lexeme.contains(offset) || lexeme.getEndingOffset() == offset - 1)
					{
						this._replaceRange = this._currentLexeme = lexeme;
					}
					else
					{
						this._replaceRange = this._currentLexeme = null;
					}
					location = LocationType.INSIDE_PROPERTY;
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
	private int getLexemeAfterDelimiter(LexemeProvider<CSSTokenType> lexemeProvider, int offset)
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

				if (this.isValueDelimiter(currentLexeme))
				{
					index = i + 1;
					break;
				}
				else if (previousLexeme.isContiguousWith(currentLexeme) == false)
				{
					// there's a space between this lexeme and the previous lexeme
					// treat the previous lexeme like it is the delimiter
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
	private Lexeme<CSSTokenType> getLexemeBeforeDelimiter(LexemeProvider<CSSTokenType> lexemeProvider, int index)
	{
		Lexeme<CSSTokenType> result = null;

		// get the staring lexeme
		Lexeme<CSSTokenType> startingLexeme = lexemeProvider.getLexeme(index);

		if (startingLexeme != null && this.isValueDelimiter(startingLexeme) == false)
		{
			Lexeme<CSSTokenType> endingLexeme = startingLexeme;

			// advance to next lexeme
			index++;

			while (index < lexemeProvider.size())
			{
				Lexeme<CSSTokenType> candidateLexeme = lexemeProvider.getLexeme(index);

				if (this.isValueDelimiter(candidateLexeme) || endingLexeme.isContiguousWith(candidateLexeme) == false)
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
	 * getLocation
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	LocationType getCoarseLocationType(LexemeProvider<CSSTokenType> lexemeProvider, int offset)
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
					result = LocationType.INSIDE_RULE;
					// Pseudo-classes/elements
					if (index >= 1)
					{
						Lexeme<CSSTokenType> previous = lexemeProvider.getLexeme(index - 1);
						if (previous != null
								&& (previous.getType() == CSSTokenType.RCURLY
										|| previous.getType() == CSSTokenType.ELEMENT
										|| previous.getType() == CSSTokenType.CLASS
										|| previous.getType() == CSSTokenType.ID || previous.getType() == CSSTokenType.COLON))
						{
							result = LocationType.OUTSIDE_RULE;
						}
					}
					else
					{
						result = LocationType.OUTSIDE_RULE;
					}
					break LOOP;
				case PROPERTY:
				case VALUE:
					result = LocationType.INSIDE_RULE;
					break LOOP;

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

	/**
	 * getPropertyName
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	private String getPropertyName(LexemeProvider<CSSTokenType> lexemeProvider, int offset)
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
	 * setPropertyValueRange
	 * 
	 * @param lexemeProvider
	 * @param offset
	 */
	private void setPropertyValueRange(LexemeProvider<CSSTokenType> lexemeProvider, int offset)
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

						if (candidate != null && this.isValueDelimiter(candidate) == false)
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#triggerAdditionalAutoActivation(char, int,
	 * org.eclipse.jface.text.IDocument, int)
	 */
	public boolean triggerAdditionalAutoActivation(char c, int keyCode, IDocument document, int offset)
	{
		LexemeProvider<CSSTokenType> lexemeProvider = this.createLexemeProvider(document, offset);
		Lexeme<CSSTokenType> lexeme = lexemeProvider.getFloorLexeme(offset);
		return (lexeme != null && (lexeme.getType() == CSSTokenType.IDENTIFIER || lexeme.getType() == CSSTokenType.COLON));
	}
}
