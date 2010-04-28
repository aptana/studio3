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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.css.CSSScopeScanner;
import com.aptana.parsing.lexer.TokenLexeme;
import com.aptana.parsing.metadata.MetadataEnvironment;

public class CSSContentAssistProcessor implements IContentAssistProcessor
{
	/**
	 * Location
	 */
	private static enum Location
	{
		OUTSIDE_RULE, INSIDE_RULE, ARG_ASSIST, ERROR
	};

	private IContextInformationValidator fValidator;
	private static MetadataEnvironment metadataEnvironment;
	private final AbstractThemeableEditor abstractThemeableEditor;
	
	/**
	 * getMetadataEnvironment
	 * 
	 * @return
	 */
	private static MetadataEnvironment getMetadataEnvironment()
	{
		if (metadataEnvironment == null)
		{
			InputStream input = CSSContentAssistProcessor.class.getResourceAsStream("CSSMetadata.bin"); //$NON-NLS-1$
			
			metadataEnvironment = new MetadataEnvironment();
			metadataEnvironment = MetadataEnvironment.getMetadataFromResource(input, metadataEnvironment);
		}

		return metadataEnvironment;
	}

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
		this.abstractThemeableEditor = abstractThemeableEditor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 */
	@Override
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
		MetadataEnvironment metadataEnvironment = getMetadataEnvironment();
		
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
		if (fValidator == null)
		{
			fValidator = new CSSContextInformationValidator();
		}
		
		return fValidator;
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
