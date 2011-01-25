/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;

import com.aptana.editor.common.text.rules.SourceConfigurationPartitionScanner;

public class JSSourcePartitionScanner extends SourceConfigurationPartitionScanner implements IJSTokenScanner
{
	private static final Pattern DIVISION_START = Pattern.compile("^.*[-+$_a-zA-Z0-9/'\"')\\]]\\s*$"); //$NON-NLS-1$
	private static final int PREVIEW_LENGTH = 80;

	/**
	 * JSSourcePartitionScanner
	 */
	public JSSourcePartitionScanner()
	{
		super(JSSourceConfiguration.getDefault());
	}

	/**
	 * hasDivisionStart
	 * 
	 * @return
	 */
	public boolean hasDivisionStart()
	{
		boolean result = false;
		int offsetStart = Math.max(0, fOffset - PREVIEW_LENGTH);
		int offsetEnd = Math.min(offsetStart + PREVIEW_LENGTH, Math.min(fOffset, fDocument.getLength()));

		if (offsetStart < offsetEnd)
		{
			String source = null;

			try
			{
				source = fDocument.get(offsetStart, offsetEnd - offsetStart);

				Matcher m = DIVISION_START.matcher(source);

				result = m.matches();
			}
			catch (BadLocationException e)
			{
			}
		}

		return result;
	}
}
