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
