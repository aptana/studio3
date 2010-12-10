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
package com.aptana.editor.common.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

public class CommonCompletionProposal implements ICommonCompletionProposal, ICompletionProposalExtension2,
		ICompletionProposalExtension3
{
	private String _additionalProposalInformation;
	private IContextInformation _contextInformation;
	private String _displayString;
	private Image _image;
	protected int _cursorPosition;
	protected int _replacementOffset;
	protected int _replacementLength;
	protected String _replacementString;
	private String _fileLocation;
	protected boolean _isDefaultSelection;
	private boolean _isSuggestedSelection;
	private Image[] _userAgentImages;
	private int _hash;

	/**
	 * CommonCompletionProposal
	 * 
	 * @param replacementString
	 * @param replacementOffset
	 * @param replacementLength
	 * @param cursorPosition
	 * @param image
	 * @param displayString
	 * @param contextInformation
	 * @param additionalProposalInfo
	 */
	public CommonCompletionProposal(String replacementString, int replacementOffset, int replacementLength,
			int cursorPosition, Image image, String displayString, IContextInformation contextInformation,
			String additionalProposalInfo)
	{
		this._replacementString = (replacementString == null) ? "" : replacementString; //$NON-NLS-1$
		this._replacementOffset = replacementOffset;
		this._replacementLength = replacementLength;
		this._cursorPosition = cursorPosition;
		this._image = image;
		this._displayString = (displayString == null) ? "" : displayString; //$NON-NLS-1$
		this._contextInformation = contextInformation;
		this._additionalProposalInformation = additionalProposalInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
	 */
	public void apply(IDocument document)
	{
		// not called anymore
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean result = false;

		if (this == obj)
		{
			result = true;
		}
		else if (obj instanceof CommonCompletionProposal)
		{
			CommonCompletionProposal that = (CommonCompletionProposal) obj;

			result = this._replacementString.equals(that._replacementString)
					&& this._replacementOffset == that._replacementOffset
					&& this._replacementLength == that._replacementLength
					&& this._cursorPosition == that._cursorPosition && this._displayString.equals(that._displayString);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		if (this._hash == 0)
		{
			this._hash = this._hash * 31 + this._replacementString.hashCode();
			this._hash = this._hash * 31 + this._replacementOffset;
			this._hash = this._hash * 31 + this._replacementLength;
			this._hash = this._hash * 31 + this._cursorPosition;
			this._hash = this._hash * 31 + this._displayString.hashCode();
		}

		return this._hash;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo()
	{
		return this._additionalProposalInformation;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation()
	{
		return this._contextInformation;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString()
	{
		return this._displayString;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ICommonCompletionProposal#getFileLocation()
	 */
	public String getFileLocation()
	{
		return (this._fileLocation != null) ? this._fileLocation : ""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	public Image getImage()
	{
		return this._image;
	}

	/**
	 * getReplaceRange
	 * 
	 * @return
	 */
	public IRange getReplaceRange()
	{
		return new Range(this._replacementOffset, this._replacementOffset + this._replacementLength - 1);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	public Point getSelection(IDocument document)
	{
		return new Point(this._replacementOffset + this._cursorPosition, 0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ICommonCompletionProposal#getUserAgentImages()
	 */
	public Image[] getUserAgentImages()
	{
		return this._userAgentImages;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ICommonCompletionProposal#isDefaultSelection()
	 */
	public boolean isDefaultSelection()
	{
		return this._isDefaultSelection;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ICommonCompletionProposal#isSuggestedSelection()
	 */
	public boolean isSuggestedSelection()
	{
		return this._isSuggestedSelection;
	}

	/**
	 * setLocation
	 * 
	 * @param location
	 */
	public void setFileLocation(String location)
	{
		this._fileLocation = location;
	}

	/**
	 * setIsDefaultSelection
	 * 
	 * @param value
	 */
	public void setIsDefaultSelection(boolean value)
	{
		this._isDefaultSelection = value;
	}

	/**
	 * setIsSuggstedSelection
	 * 
	 * @param value
	 */
	public void setIsSuggestedSelection(boolean value)
	{
		this._isSuggestedSelection = value;
	}

	/**
	 * setUserAgentImages
	 * 
	 * @param images
	 */
	public void setUserAgentImages(Image[] images)
	{
		this._userAgentImages = images;
	}

	public IInformationControlCreator getInformationControlCreator()
	{
		return null;
	}

	public CharSequence getPrefixCompletionText(IDocument document, int completionOffset)
	{
		return _replacementString;
	}

	public int getPrefixCompletionStart(IDocument document, int completionOffset)
	{
		return _replacementOffset;
	}

	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset)
	{
		IDocument document = viewer.getDocument();
		boolean validPrefix = isValidPrefix(getPrefix(document, offset), getDisplayString(), true);
		boolean validPrefixCaseSensitive = isValidPrefix(getPrefix(document, offset), getDisplayString(), false);

		// It seems plausible this logic could be simplified
		int shift = 0;
		if(validPrefix && validPrefixCaseSensitive)
		{
			shift = offset - this._replacementOffset;
		}
		
		if (shift < this._replacementString.length())
		{
			int length = Math.max(0, this._replacementLength - shift);
			String toReplace = this._replacementString.substring(shift);

			if (!validPrefix || validPrefix && !validPrefixCaseSensitive)
			{
				offset = this._replacementOffset;
			}

			try
			{
				document.replace(offset, length, toReplace);
			}
			catch (BadLocationException x)
			{
				// ignore
			}
		}
	}

	/*
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#selected(org.eclipse.jface.text.ITextViewer,
	 * boolean)
	 */
	public void selected(ITextViewer viewer, boolean smartToggle)
	{
	}

	/*
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#unselected(org.eclipse.jface.text.ITextViewer)
	 */
	public void unselected(ITextViewer viewer)
	{
	}

	/*
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#validate(org.eclipse.jface.text.IDocument,
	 * int, org.eclipse.jface.text.DocumentEvent)
	 */
	public boolean validate(IDocument document, int offset, DocumentEvent event)
	{
		if (offset < this._replacementOffset)
			return false;

		int overlapIndex = getDisplayString().length() - _replacementString.length();
		overlapIndex = Math.max(0, overlapIndex);
		String endPortion = getDisplayString().substring(overlapIndex);
		boolean validated = isValidPrefix(getPrefix(document, offset), endPortion);

		if (validated && event != null)
		{
			// make sure that we change the replacement length as the document content changes
			int delta = (event.fText == null ? 0 : event.fText.length()) - event.fLength;
			final int newLength = Math.max(_replacementLength + delta, 0);
			_replacementLength = newLength;
		}

		return validated;
	}

	/**
	 * Returns the prefix string from the replacement-offset to the given offset. In case the given offset appears
	 * before the replacement offset, we return an empty string.
	 * 
	 * @param document
	 * @param offset
	 */
	protected String getPrefix(IDocument document, int offset)
	{
		try
		{
			int length = offset - _replacementOffset;
			if (length > 0)
				return document.get(_replacementOffset, length);
		}
		catch (BadLocationException x)
		{
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * Returns true if the proposal is still valid as the user types while the content assist popup is visible.
	 * 
	 * @param prefix
	 * @param displayString
	 */
	protected boolean isValidPrefix(String prefix, String displayString)
	{
		return isValidPrefix(prefix, displayString, true);
	}
	
	/**
	 * Returns true if the proposal is still valid as the user types while the content assist popup is visible.
	 * 
	 * @param prefix
	 * @param displayString
	 * @param ignoreCase Do we ignore the case of the prefix during comparisons?
	 */
	protected boolean isValidPrefix(String prefix, String displayString, boolean ignoreCase)
	{
		if (prefix == null || displayString == null || prefix.length() > displayString.length())
			return false;
		String start = displayString.substring(0, prefix.length());
		if(ignoreCase) {
			return start.equalsIgnoreCase(prefix);
		}
		else
		{
			return start.equals(prefix);
		}
	}
}
