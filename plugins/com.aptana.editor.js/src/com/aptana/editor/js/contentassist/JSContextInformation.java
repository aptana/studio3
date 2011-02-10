/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationExtension;
import org.eclipse.swt.graphics.Image;

/**
 * @author klindsey
 *
 */
public class JSContextInformation implements IContextInformation, IContextInformationExtension
{
	private String _contextString;
	private String _infoString;
	private int _offset;
	
	public JSContextInformation(String contextDisplayString, String informationDisplayString, int informationPosition)
	{
		this._contextString = contextDisplayString;
		this._infoString = informationDisplayString;
		this._offset = informationPosition;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContextInformationExtension#getContextInformationPosition()
	 */
	public int getContextInformationPosition()
	{
		return this._offset;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContextInformation#getContextDisplayString()
	 */
	public String getContextDisplayString()
	{
		return this._contextString;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContextInformation#getImage()
	 */
	public Image getImage()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContextInformation#getInformationDisplayString()
	 */
	public String getInformationDisplayString()
	{
		return this._infoString;
	}
}
