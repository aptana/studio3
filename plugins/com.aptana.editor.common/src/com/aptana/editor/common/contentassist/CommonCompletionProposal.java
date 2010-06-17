package com.aptana.editor.common.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

public class CommonCompletionProposal implements ICommonCompletionProposal, ICompletionProposalExtension3
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
	public CommonCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition, Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo)
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
	@Override
	public void apply(IDocument document)
	{
		try
		{
			document.replace(this._replacementOffset, this._replacementLength, this._replacementString);
		}
		catch (BadLocationException x)
		{
			// ignore
		}
	}

	
	/* (non-Javadoc)
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
			
			result =
					this._replacementString.equals(that._replacementString)
				&&	this._replacementOffset == that._replacementOffset
				&&	this._replacementLength == that._replacementLength
				&&	this._cursorPosition == that._cursorPosition
				&&	this._displayString.equals(that._displayString);
		}
		
		return result;
	}

	/* (non-Javadoc)
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
	@Override
	public String getAdditionalProposalInfo()
	{
		return this._additionalProposalInformation;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
	 */
	@Override
	public IContextInformation getContextInformation()
	{
		return this._contextInformation;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
	 */
	@Override
	public String getDisplayString()
	{
		return this._displayString;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ICommonCompletionProposal#getFileLocation()
	 */
	@Override
	public String getFileLocation()
	{
		return (this._fileLocation != null) ? this._fileLocation : ""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	@Override
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
	@Override
	public Point getSelection(IDocument document)
	{
		return new Point(this._replacementOffset + this._cursorPosition, 0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ICommonCompletionProposal#getUserAgentImages()
	 */
	@Override
	public Image[] getUserAgentImages()
	{
		return this._userAgentImages;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ICommonCompletionProposal#isDefaultSelection()
	 */
	@Override
	public boolean isDefaultSelection()
	{
		return this._isDefaultSelection;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ICommonCompletionProposal#isSuggestedSelection()
	 */
	@Override
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

	@Override
	public IInformationControlCreator getInformationControlCreator()
	{
		return null;
	}

	@Override
	public CharSequence getPrefixCompletionText(IDocument document, int completionOffset)
	{
		return _replacementString;
	}

	@Override
	public int getPrefixCompletionStart(IDocument document, int completionOffset)
	{
		return _replacementOffset;
	}
}
