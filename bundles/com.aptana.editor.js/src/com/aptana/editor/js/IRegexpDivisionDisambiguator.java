/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

/*
 * @author cwilliams
 */
public interface IRegexpDivisionDisambiguator
{
	/**
	 * Determine if the scanner's position is valid for division (to escape matching it as a regular expression).
	 * Generically, we're determining lexer state to see if we're mid-expression or at the beginning of one.
	 * 
	 * @return
	 */
	public boolean isValidDivisionStart();
}
