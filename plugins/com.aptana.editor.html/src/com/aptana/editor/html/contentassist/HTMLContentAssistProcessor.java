package com.aptana.editor.html.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.html.HTMLScopeScanner;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.parsing.lexer.Lexeme;

public class HTMLContentAssistProcessor extends CommonContentAssistProcessor
{
	private static enum Location
	{
		ERROR, IN_OPEN_TAG, IN_ATTIBUTE_NAME, IN_ATTRIBUTE_VALUE, IN_CLOSE_TAG, IN_TEXT
	};

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
	 * addElementProposals
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
				String openTag = "<" + element.getName() + ">";
				String closeTag = "</" + element.getName() + ">";
				String insertText = openTag + closeTag;
				int length = openTag.length();
				String displayName = element.getName();
				String description = element.getDescription();
				Image image = null;
				IContextInformation contextInfo = null;

				// build proposal
				CompletionProposal proposal = new CompletionProposal(insertText, offset, 0, length, image, displayName, contextInfo, description);

				// add it to the list
				proposals.add(proposal);
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
				break;

			case IN_CLOSE_TAG:
				break;

			case IN_ATTIBUTE_NAME:
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
		this.setSelectedProposal(this._currentLexeme.getText(), result);

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
	 * getLocation
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	private Location getLocation(LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		return Location.ERROR;
	}
}
