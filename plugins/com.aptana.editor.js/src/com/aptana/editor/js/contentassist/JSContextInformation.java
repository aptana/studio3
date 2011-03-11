/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.util.List;

import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationExtension;
import org.eclipse.swt.graphics.Image;

import com.aptana.core.util.StringUtil;

/**
 * @author klindsey
 */
public class JSContextInformation implements IContextInformation, IContextInformationExtension
{
	public static final String DESCRIPTION_DELIMITER = "\ufeff"; //$NON-NLS-1$
	private static final String LINE_DELIMITER = "\n" + DESCRIPTION_DELIMITER; //$NON-NLS-1$

	private String _contextString;
	private String _infoString;
	private int _offset;

	/**
	 * JSContextInformation
	 * 
	 * @param contextDisplayString
	 * @param informationDisplayStrings
	 * @param informationPosition
	 */
	public JSContextInformation(String contextDisplayString, List<String> informationDisplayStrings, int informationPosition)
	{
		this._contextString = contextDisplayString;
		this._infoString = StringUtil.join(LINE_DELIMITER, informationDisplayStrings);
		this._offset = informationPosition;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContextInformation#getContextDisplayString()
	 */
	public String getContextDisplayString()
	{
		return this._contextString;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContextInformationExtension#getContextInformationPosition()
	 */
	public int getContextInformationPosition()
	{
		return this._offset;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContextInformation#getImage()
	 */
	public Image getImage()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContextInformation#getInformationDisplayString()
	 */
	public String getInformationDisplayString()
	{
		return this._infoString;
	}
}
