/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.net.URI;

import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationExtension;
import org.eclipse.swt.graphics.Image;

import com.aptana.js.core.model.FunctionElement;

/**
 * @author klindsey
 */
public class JSContextInformation implements IContextInformation, IContextInformationExtension
{

	public static final String DESCRIPTION_DELIMITER = "\ufeff"; //$NON-NLS-1$

	private String _contextString;
	private String _infoString;
	private int _offset;
	private FunctionElement _function;
	private URI _uri;

	/**
	 * JSContextInformation
	 * 
	 * @param function
	 * @param uri
	 * @param informationPosition
	 */
	public JSContextInformation(FunctionElement function, URI uri, int informationPosition)
	{
		this._function = function;
		this._uri = uri;
		this._offset = informationPosition;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContextInformation#getContextDisplayString()
	 */
	public String getContextDisplayString()
	{
		lazyLoad();
		return this._contextString;
	}

	private synchronized void lazyLoad()
	{
		if (this._contextString == null)
		{
			this._contextString = JSModelFormatter.CONTEXT_INFO.getHeader(this._function, this._uri);
			this._infoString = JSModelFormatter.CONTEXT_INFO.getDocumentation(this._function);
		}
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
		lazyLoad();
		return this._infoString;
	}
}
