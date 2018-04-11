/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter;

import java.util.List;

import org.eclipse.jface.text.IRegion;

import com.aptana.formatter.ExcludeRegionList.EXCLUDE_STRATEGY;

public interface IFormatterWriter
{

	void ensureLineStarted(IFormatterContext context);

	void write(IFormatterContext context, int startOffset, int endOffset);

	/**
	 * Writes specified text at the current position. Ideally text should not contain line breaks characters.<br>
	 * This call will remove any previous spaces.
	 * 
	 * @param text
	 */
	void writeText(IFormatterContext context, String text);

	/**
	 * Writes specified text at the current position. Ideally text should not contain line breaks characters.
	 * 
	 * @param text
	 * @param removePreviousSpaces
	 */
	void writeText(IFormatterContext context, String text, boolean removePreviousSpaces);

	/**
	 * Writes line break at the current position.
	 * 
	 * @param context
	 * @throws Exception
	 */
	void writeLineBreak(IFormatterContext context);

	void writeIndent(IFormatterContext context);

	void skipNextLineBreaks(IFormatterContext context);

	void appendToPreviousLine(IFormatterContext context, String text);

	void disableAppendToPreviousLine();

	void excludeRegion(IRegion region, EXCLUDE_STRATEGY strategy);

	void excludeRegions(List<IRegion> regions, EXCLUDE_STRATEGY strategy);

	void addNewLineCallback(IFormatterCallback callback);

	/**
	 * Returns true if the current buffer in the writer is ending with a new-line terminator.
	 * 
	 * @return True, if the buffer ends with a line-terminator; False, otherwise.
	 */
	boolean endsWithNewLine();

	boolean isPreserveSpaces();

	void setPreserveSpaces(boolean preserve);

	/**
	 * @return true if the visitor is currently in a line that contains only whitespace characters (till the new line
	 *         chars).
	 */
	boolean isInBlankLine();

}
