/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class JSContextInformationValidator implements IContextInformationValidator, IContextInformationPresenter
{
	private IContextInformation _contextInformation;
	private ITextViewer _viewer;
	private int _offset;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContextInformationValidator#install(org.eclipse.jface.text.contentassist.IContextInformation, org.eclipse.jface.text.ITextViewer, int)
	 */
	public void install(IContextInformation info, ITextViewer viewer, int offset)
	{
		this._contextInformation = info;
		this._viewer = viewer;
		this._offset = offset;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContextInformationValidator#isContextInformationValid(int)
	 */
	public boolean isContextInformationValid(int offset)
	{
		boolean result = false;
		
		if (offset >= this._offset)
		{
			IDocument document = this._viewer.getDocument();
			
			try
			{
				IRegion line = document.getLineInformationOfOffset(this._offset);
				
				if (line.getOffset() <= offset && offset < document.getLength())
				{
					result = true;
				}
			}
			catch (BadLocationException e)
			{
			}
			
		}
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#updatePresentation(int, org.eclipse.jface.text.TextPresentation)
	 */
	public boolean updatePresentation(int offset, TextPresentation presentation)
	{
		boolean result = false;
		
		try
		{
			String source = this._viewer.getDocument().get(this._offset, offset);
			
			result = true;
		}
		catch (BadLocationException e)
		{
		}
		
		return result;
	}
}
