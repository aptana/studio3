/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.editor.css.contentassist;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.parsing.lexer.TokenLexeme;

public class CSSContentAssistProcessor implements IContentAssistProcessor
{
	/**
	 * Location
	 */
	private static enum Location
	{
		OUTSIDE_RULE, INSIDE_RULE, ARG_ASSIST, ERROR
	};

	private IContextInformationValidator _validator;
	private final AbstractThemeableEditor _editor;
	private CSSContentAssistHelper _helper;

	/**
	 * getTokenLexemeAtOffset
	 * 
	 * @param tokenLexems
	 * @param offset
	 * @return
	 */
	private static TokenLexeme getTokenLexemeAtOffset(List<TokenLexeme> tokenLexems, int offset)
	{
		int size = tokenLexems.size();
		
		if (size > 0)
		{
			for (TokenLexeme tokenLexeme : tokenLexems)
			{
				if (offset >= tokenLexeme.getStartingOffset() && offset <= tokenLexeme.getEndingOffset())
				{
					return tokenLexeme;
				}
			}
		}
		
		return null;
	}

	/**
	 * getLocation
	 * 
	 * @param tokenLexems
	 * @param offset
	 * @return
	 */
	private static Location getLocation(List<TokenLexeme> tokenLexems, int offset)
	{
		if (offset == 0)
		{
			return Location.OUTSIDE_RULE;
		}
		
		TokenLexeme tokenLexemeAtOffset = getTokenLexemeAtOffset(tokenLexems, offset);
		
		if (tokenLexemeAtOffset != null)
		{
			// Compute the location
		}
		
		return Location.ERROR;
	}

	/**
	 * CSSContentAssistProcessor
	 * 
	 * @param abstractThemeableEditor
	 */
	public CSSContentAssistProcessor(AbstractThemeableEditor abstractThemeableEditor)
	{
		this._editor = abstractThemeableEditor;
		this._helper = new CSSContentAssistHelper();
	}

	/*
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
	{
		IDocument document = abstractThemeableEditor.getDocumentProvider().getDocument(abstractThemeableEditor.getEditorInput());
		CSSScopeScanner scanner = new CSSScopeScanner();

		scanner.setRange(viewer.getDocument(), 0, viewer.getDocument().getLength());

		List<TokenLexeme> tokenLexemes = new ArrayList<TokenLexeme>();
		IToken token;

		// Build the TokenLexeme
		while (true)
		{
			token = scanner.nextToken();

			if (token == Token.EOF)
			{
				break;
			}

			try
			{
				int tokenOffset = scanner.getTokenOffset();
				int tokenLength = scanner.getTokenLength();
				int endingOffset = tokenOffset + tokenLength;
				String text = document.get(tokenOffset, tokenLength);

				tokenLexemes.add(new TokenLexeme(text, tokenOffset, endingOffset, token));
			}
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		}

		// Use the language metadata and location and indexing information to build the
		// proposals
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		Location location = getLocation(tokenLexemes, offset);
		
		return proposals.toArray(new ICompletionProposal[proposals.size()]);
	}
	*/
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
	{
		List<ICompletionProposal> proposals = new LinkedList<ICompletionProposal>();
		List<ElementElement> elements = this._helper.getElements();
		
		if (elements != null)
		{
			for (ElementElement element : elements)
			{
				String name = element.getName();
				int length = name.length();
				String description = element.getDescription();
				Image image = null;
				IContextInformation contextInfo = null;
				
				// build a proposal
				CompletionProposal proposal = new CompletionProposal(name, offset, 0, length, image, name, contextInfo, description);
				
				// add it to the list
				proposals.add(proposal);
			}
		}
		
		return proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
	 */
	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	@Override
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		return new char[] { ':', '\t', '{', ';' };
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	@Override
	public char[] getContextInformationAutoActivationCharacters()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	@Override
	public IContextInformationValidator getContextInformationValidator()
	{
		if (_validator == null)
		{
			_validator = new CSSContextInformationValidator();
		}
		
		return _validator;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	@Override
	public String getErrorMessage()
	{
		return null;
	}
}
