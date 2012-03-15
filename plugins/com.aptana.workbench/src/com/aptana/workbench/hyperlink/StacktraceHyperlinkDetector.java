/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.workbench.hyperlink;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import com.aptana.terminal.hyperlink.IHyperlinkDetector;

public class StacktraceHyperlinkDetector implements IHyperlinkDetector
{

	private static Pattern OPEN_TRACE_LINE_PATTERN = Pattern.compile("\\s*(\\S.+?):(\\d+):\\s+"); //$NON-NLS-1$

	public IHyperlink[] detectHyperlinks(String contents)
	{
		Matcher m = OPEN_TRACE_LINE_PATTERN.matcher(contents);
		if (m.find())
		{
			String filepath = m.group(1);
			int lineNumber = Integer.parseInt(m.group(2));
			int start = m.start(1);
			int parenIndex = filepath.indexOf("("); //$NON-NLS-1$
			if (parenIndex != -1)
			{
				filepath = filepath.substring(parenIndex + 1);
				start += parenIndex + 1;
			}
			int length = m.end(2) - start;
			return new IHyperlink[] { new EditorLineHyperlink(new Region(start, length), filepath, lineNumber) };
		}
		return new IHyperlink[0];
	}
}
