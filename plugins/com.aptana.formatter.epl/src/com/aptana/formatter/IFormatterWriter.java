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

import org.eclipse.jface.text.IRegion;

public interface IFormatterWriter {

	void ensureLineStarted(IFormatterContext context);

	void write(IFormatterContext context, int startOffset, int endOffset);

	/**
	 * Writes specified text at the current position. Ideally text should not
	 * contain line breaks characters.
	 * 
	 * @param text
	 */
	void writeText(IFormatterContext context, String text);

	/**
	 * Writes line break at the current position.
	 * 
	 * @param context
	 * @throws Exception
	 */
	void writeLineBreak(IFormatterContext context);

	void skipNextLineBreaks(IFormatterContext context);

	void appendToPreviousLine(IFormatterContext context, String text);

	void disableAppendToPreviousLine();

	void excludeRegion(IRegion region);

	void addNewLineCallback(IFormatterCallback callback);

}
