/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.IDebugScopes;

/**
 * Used to share common code for formatting Diffs for display.
 * 
 * @author cwilliams
 */
public abstract class DiffFormatter
{

	private static final Pattern gitDiffHeaderRegexp = Pattern.compile("@@ \\-([0-9]+),?\\d* \\+(\\d+),?\\d* @@"); //$NON-NLS-1$

	/**
	 * Generates a colored HTML view of the diff
	 * 
	 * @param diff
	 * @return
	 */
	public static String toHTML(String title, String diff)
	{
		if (diff == null)
		{
			return ""; //$NON-NLS-1$
		}
		return injectIntoTemplate(convertDiff(title, diff));
	}

	private static String convertDiff(String title, String diff)
	{
		if (title == null)
		{
			title = StringUtil.EMPTY;
		}
		StringBuilder html = new StringBuilder();
		html.append("<div class=\"file\">"); //$NON-NLS-1$
		html.append("<div class=\"fileHeader\">").append(title).append("</div>"); //$NON-NLS-1$ //$NON-NLS-2$
		html.append("<div class=\"diffContent\">"); //$NON-NLS-1$
		if (!diff.startsWith("diff")) //$NON-NLS-1$
		{
			// New file, no "diff", all lines are added. TODO Maybe split all lines and pretend like they have a + in
			// front?
			if (diff.length() == 0)
			{
				diff = Messages.DiffFormatter_NoContent;
			}
			html.append("<pre>" + StringUtil.sanitizeHTML(diff) + "</pre>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			String[] lines = diff.split("\r\n|\r|\n"); //$NON-NLS-1$
			StringBuilder diffContent = new StringBuilder();
			StringBuilder line1 = new StringBuilder();
			StringBuilder line2 = new StringBuilder();
			String lineNumberNewline = "\n"; //$NON-NLS-1$
			if (Platform.getOS().equals(Platform.OS_WIN32))
			{
				// BUGFIX Have to force breakread/newlines in HTML for IE...
				lineNumberNewline = "<br />\n"; //$NON-NLS-1$
			}
			int hunkStartLine1 = 0;
			int hunkStartLine2 = 0;
			for (int i = 4; i < lines.length; i++)
			{
				String line = lines[i];
				if (line == null || line.length() == 0)
					continue;
				char c = line.charAt(0);
				switch (c)
				{
					case '@':
						Matcher m = gitDiffHeaderRegexp.matcher(line);
						if (m.find())
						{
							hunkStartLine1 = Integer.parseInt(m.group(1)) - 1;
							hunkStartLine2 = Integer.parseInt(m.group(2)) - 1;
						}
						line1.append("..").append(lineNumberNewline); //$NON-NLS-1$
						line2.append("..").append(lineNumberNewline); //$NON-NLS-1$
						diffContent.append("<div class=\"hunkheader\">").append(StringUtil.sanitizeHTML(line)).append( //$NON-NLS-1$
								"</div>"); //$NON-NLS-1$
						break;

					case '+':
						if (line.equals("+++ /dev/null")) //$NON-NLS-1$
							continue;
						// Highlight trailing whitespace
						line = StringUtil.sanitizeHTML(line);
						line = line.replaceFirst("\\s+$", "<span class=\"whitespace\">$0</span>"); //$NON-NLS-1$ //$NON-NLS-2$
						line1.append(lineNumberNewline);
						line2.append(++hunkStartLine2).append(lineNumberNewline);
						diffContent.append("<div class=\"addline\">").append(line).append("</div>"); //$NON-NLS-1$ //$NON-NLS-2$
						break;

					case ' ':
						line1.append(++hunkStartLine1).append(lineNumberNewline);
						line2.append(++hunkStartLine2).append(lineNumberNewline);
						diffContent.append("<div class=\"noopline\">").append(StringUtil.sanitizeHTML(line)).append( //$NON-NLS-1$
								"</div>"); //$NON-NLS-1$
						break;

					case '-':
						line1.append(++hunkStartLine1).append(lineNumberNewline);
						line2.append(lineNumberNewline);
						diffContent.append("<div class=\"delline\">").append(StringUtil.sanitizeHTML(line)) //$NON-NLS-1$
								.append("</div>"); //$NON-NLS-1$
						break;

					default:
						break;
				}

			}
			html.append("<div class=\"lineno\">").append(line1).append("</div>"); //$NON-NLS-1$ //$NON-NLS-2$
			html.append("<div class=\"lineno\">").append(line2).append("</div>"); //$NON-NLS-1$ //$NON-NLS-2$
			html.append("<div class=\"lines\">").append(diffContent).append("</div>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		html.append("</div>").append("</div>"); //$NON-NLS-1$ //$NON-NLS-2$
		return html.toString();
	}

	private static String injectIntoTemplate(String html)
	{
		InputStream stream = null;
		try
		{
			stream = FileLocator.openStream(GitUIPlugin.getDefault().getBundle(), new Path("templates") //$NON-NLS-1$
					.append("diff.html"), false); //$NON-NLS-1$
			String template = IOUtil.read(stream);
			Map<String, String> variables = new HashMap<String, String>();
			variables.put("\\{diff\\}", Matcher.quoteReplacement(html)); //$NON-NLS-1$
			return StringUtil.replaceAll(template, variables);
		}
		catch (Exception e)
		{
			IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
			return html;
		}
	}

	/**
	 * Pass in a Map of filename/path to diff.
	 * 
	 * @param diffs
	 * @return
	 */
	public static String toHTML(Map<IPath, String> diffs)
	{
		if (diffs == null)
		{
			return StringUtil.EMPTY;
		}
		StringBuilder combined = new StringBuilder();
		for (Map.Entry<IPath, String> diffMap : diffs.entrySet())
		{
			combined.append(convertDiff(diffMap.getKey().toOSString(), diffMap.getValue())).append('\n');
		}
		return injectIntoTemplate(combined.toString());
	}

}
