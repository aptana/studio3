/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

public class CommonCompletionProposal implements ICommonCompletionProposal, ICompletionProposalExtension,
		ICompletionProposalExtension2, ICompletionProposalExtension3, Comparable<ICompletionProposal>
{
	protected String _replacementString;
	protected int _replacementOffset;
	protected int _replacementLength;
	protected int _cursorPosition;
	protected Image _image;
	private String _displayString;
	private IContextInformation _contextInformation;
	private String _additionalProposalInformation;
	protected String _fileLocation;
	private int _hash;

	private Image[] _userAgentImages;
	private char[] _triggerChars;

	/** @deprecated Use _relevance instead */
	private boolean _isDefaultSelection;
	/** @deprecated Use _relevance instead */
	private boolean _isSuggestedSelection;

	private int _relevance;

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
		this._replacementString = (replacementString == null) ? StringUtil.EMPTY : replacementString;
		this._replacementOffset = replacementOffset;
		this._replacementLength = replacementLength;
		this._cursorPosition = cursorPosition;
		this._image = image;
		this._displayString = (displayString == null) ? StringUtil.EMPTY : displayString;
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

			// @formatter:off
			result =
					ObjectUtil.areEqual(_replacementString, that._replacementString)
				&&	_replacementOffset == that._replacementOffset
				&&	_replacementLength == that._replacementLength
				&&	_cursorPosition == that._cursorPosition
				&&	ObjectUtil.areEqual(_image, that._image)
				&&	ObjectUtil.areEqual(_displayString, that._displayString)
				&&	ObjectUtil.areEqual(_contextInformation, that._contextInformation)
				&&	ObjectUtil.areEqual(_additionalProposalInformation, that._additionalProposalInformation)
				&&	ObjectUtil.areEqual(_fileLocation, that._fileLocation);
			// @formatter:on
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
		if (_hash == 0)
		{
			// @formatter:off
			_hash = _hash * 31 + ((_replacementString != null) ? _replacementString.hashCode() : 0);
			_hash = _hash * 31 + _replacementOffset;
			_hash = _hash * 31 + _replacementLength;
			_hash = _hash * 31 + _cursorPosition;
			_hash = _hash * 31 + ((getImage() != null) ? getImage().hashCode() : 0);
			_hash = _hash * 31 + ((getDisplayString() != null) ? getDisplayString().hashCode() : 0);
			_hash = _hash * 31 + ((getContextInformation() != null) ? getContextInformation().hashCode() : 0);
			_hash = _hash * 31 + ((getAdditionalProposalInfo() != null) ? getAdditionalProposalInfo().hashCode() : 0);
			_hash = _hash * 31 + ((getFileLocation() != null) ? getFileLocation().hashCode() : 0);
			// @formatter:on
		}

		return _hash;
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
		return (this._fileLocation != null) ? this._fileLocation : StringUtil.EMPTY;
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
	 * @deprecated Use getRelevance instead
	 */
	public boolean isDefaultSelection()
	{
		return this._isDefaultSelection;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ICommonCompletionProposal#isSuggestedSelection()
	 * @deprecated Use getRelevance instead
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
	 * @deprecated Use getRelevance instead
	 */
	public void setIsDefaultSelection(boolean value)
	{
		this._isDefaultSelection = value;
	}

	/**
	 * setIsSuggstedSelection
	 * 
	 * @param value
	 * @deprecated Use getRelevance instead
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
		String prefix = getPrefix(document, offset);
		boolean validPrefix = isValidPrefix(prefix, getDisplayString(), true);
		boolean prefixMatchesReplacementExactly = isValidPrefix(prefix, _replacementString, false);

		boolean addedTrigger = false;
		char[] triggers = getTriggerCharacters();
		if (triggers != null)
		{
			String str = new String(triggers);
			if (str.indexOf(trigger) >= 0 && this._replacementString != null
					&& this._replacementString.indexOf(trigger) < 0)
			{
				this._replacementString += trigger;
				addedTrigger = true;
			}
		}

		// FIXME If we've modified the replacement string to add optional leading/trailing characters (like quotes) this
		// breaks insertion behavior
		// This is happening in HTMLAttributeValueProposal and TSSElementSelectorProposal when we complete a partial
		// prefix value without a leading quote

		// It seems plausible this logic could be simplified
		int shift = 0;
		if (validPrefix && prefixMatchesReplacementExactly)
		{
			shift = offset - this._replacementOffset;
		}

		if (shift < this._replacementString.length())
		{
			int length = Math.max(0, this._replacementLength - shift);
			String toReplace = this._replacementString.substring(shift);

			if (!validPrefix || validPrefix && !prefixMatchesReplacementExactly)
			{
				offset = this._replacementOffset;
			}

			if (addedTrigger)
			{
				this._cursorPosition += 1;
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
		{
			return false;
		}

		int overlapIndex = getDisplayString().length() - _replacementString.length();
		overlapIndex = Math.max(0, overlapIndex);
		String endPortion = getDisplayString().substring(overlapIndex);
		boolean validated = isValidPrefix(getPrefix(document, offset), endPortion);

		if (validated && event != null)
		{
			// make sure that we change the replacement length as the document content changes
			int delta = ((event.fText == null) ? 0 : event.fText.length()) - event.fLength;
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
		return StringUtil.EMPTY;
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
	 * @param ignoreCase
	 *            Do we ignore the case of the prefix during comparisons?
	 */
	private boolean isValidPrefix(String prefix, String displayString, boolean ignoreCase)
	{
		if (prefix == null || displayString == null || prefix.length() > displayString.length())
			return false;
		String start = displayString.substring(0, prefix.length());
		if (ignoreCase)
		{
			return start.equalsIgnoreCase(prefix);
		}
		else
		{
			return start.equals(prefix);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ICommonCompletionProposal#getExtraInfo()
	 */
	public String getExtraInfo()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ICompletionProposal o)
	{
		if (this == o)
		{
			return 0;
		}

		// not yet sorting on relevance
		return StringUtil.compareCaseInsensitive(this.getDisplayString(), o.getDisplayString());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ICommonCompletionProposal#getRelevance()
	 */
	public int getRelevance()
	{
		return _relevance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ICommonCompletionProposal#setRelevance(int)
	 */
	public void setRelevance(int relevance)
	{
		_relevance = relevance;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension#apply(org.eclipse.jface.text.IDocument,
	 * char, int)
	 */
	public void apply(IDocument document, char trigger, int offset)
	{
		// evidently not called anymore
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposalExtension#isValidFor(org.eclipse.jface.text.IDocument,
	 * int)
	 */
	public boolean isValidFor(IDocument document, int offset)
	{
		// evidently not called anymore
		return false;
	}

	/**
	 * Sets the defaults set of trigger characters used to trigger insertion/completion of a proposal
	 * 
	 * @param chars
	 *            an array of characters
	 * @return
	 */
	public void setTriggerCharacters(char[] chars)
	{
		_triggerChars = chars;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension#getTriggerCharacters()
	 */
	public char[] getTriggerCharacters()
	{
		return _triggerChars;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension#getContextInformationPosition()
	 */
	public int getContextInformationPosition()
	{
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.contentassist.ICommonCompletionProposal#isTriggerEnabled(org.eclipse.jface.text.IDocument
	 * , int)
	 */
	public boolean validateTrigger(IDocument document, int offset, KeyEvent keyEvent)
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getDisplayString();
	}
}
