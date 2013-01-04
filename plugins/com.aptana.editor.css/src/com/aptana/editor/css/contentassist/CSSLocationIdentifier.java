/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import com.aptana.css.core.parsing.ast.CSSTreeWalker;

/**
 * CSSLocationIdentifier
 */
@SuppressWarnings("unused")
public class CSSLocationIdentifier extends CSSTreeWalker
{
	private int _offset;
	private LocationType _type;
	
	public CSSLocationIdentifier(int offset)
	{
		offset--;
		
		this._offset = offset;
		this._type = LocationType.UNKNOWN;
	}
}
