/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.ast;

/**
 * @author ayeung FIXME Combine with IProblem?
 */
public interface IParseError // $codepro.audit.disable consistentSuffixUsage
{
	public enum Severity
	{
		WARNING(1), ERROR(2);

		int code;

		Severity(int code)
		{
			this.code = code;
		}

		public int getCode()
		{
			return code;
		}
	}

	/**
	 * The starting offset where the error is located
	 * 
	 * @return the starting offset of the error
	 */
	public int getOffset();

	/**
	 * The length of the error
	 * 
	 * @return the length of the error
	 */
	public int getLength();

	/**
	 * The message for the parse error
	 * 
	 * @return the error message
	 */
	public String getMessage();

	/**
	 * Gets the severity of the error (Either warning or error)
	 * 
	 * @return the severity of the error
	 */
	public Severity getSeverity();

	/**
	 * The language/content type where this error originated.
	 * 
	 * @return
	 */
	public String getLangauge();
}
