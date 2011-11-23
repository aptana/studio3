/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.validator;

import java.net.URI;
import java.util.List;

import com.aptana.core.build.IProblem;

public interface IValidationManager
{

	/**
	 * Adds a validation error.
	 * 
	 * @param message
	 *            the error message
	 * @param lineNumber
	 *            the line number
	 * @param lineOffset
	 *            the line offset for where the errored text begins
	 * @param length
	 *            the length of the errored text
	 * @param sourcePath
	 *            the source path
	 * @return the validation item added
	 */
	public IProblem createError(String message, int lineNumber, int lineOffset, int length, URI sourcePath);

	/**
	 * Adds a validation warning.
	 * 
	 * @param message
	 *            the warning message
	 * @param lineNumber
	 *            the line number
	 * @param lineOffset
	 *            the line offset for where the warning text begins
	 * @param length
	 *            the length of the warning text
	 * @param sourcePath
	 *            the source path
	 * @return the validation item added
	 */
	public IProblem createWarning(String message, int lineNumber, int lineOffset, int length, URI sourcePath);

	/**
	 * Indicates the source contains the specified nested language so those blocks will be validated by its own
	 * validator.
	 * 
	 * @param language
	 *            the nested language (e.g. CSS/JS for HTML)
	 */
	public void addNestedLanguage(String language);

	/**
	 * Checks if the validation message should be ignored for the particular language.
	 * 
	 * @param message
	 *            the validation message
	 * @param language
	 *            the language type
	 * @return true if the message needs to be ignored, false otherwise
	 */
	public boolean isIgnored(String message, String language);

	/**
	 * Adds parse errors to the given list of validation items
	 * 
	 * @param items
	 * @param language
	 */
	public void addParseErrors(List<IProblem> items, String language);

}
