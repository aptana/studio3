package com.aptana.git.ui.internal;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.git.ui.GitUIPlugin;
import com.aptana.util.IOUtil;
import com.aptana.util.StringUtil;

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
	public static String toHTML(String diff)
	{
		if (diff == null)
		{
			return ""; //$NON-NLS-1$
		}
		if (!diff.startsWith("diff")) //$NON-NLS-1$
		{
			return "<pre>" + StringUtil.sanitizeHTML(diff) + "</pre>"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		String title = ""; // FIXME Grab the filename! //$NON-NLS-1$
		StringBuilder html = new StringBuilder();
		html.append("<div class=\"file\">"); //$NON-NLS-1$
		html.append("<div class=\"fileHeader\">").append(title).append("</div>"); //$NON-NLS-1$ //$NON-NLS-2$
		html.append("<div class=\"diffContent\">"); //$NON-NLS-1$
		String[] lines = diff.split("\r|\n|\r\n"); //$NON-NLS-1$
		StringBuilder diffContent = new StringBuilder();
		StringBuilder line1 = new StringBuilder();
		StringBuilder line2 = new StringBuilder();
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
					line1.append("..\n"); //$NON-NLS-1$
					line2.append("..\n"); //$NON-NLS-1$
					diffContent.append("<div class=\"hunkheader\">").append(StringUtil.sanitizeHTML(line)).append( //$NON-NLS-1$
							"</div>"); //$NON-NLS-1$
					break;

				case '+':
					if (line.equals("+++ /dev/null")) //$NON-NLS-1$
						continue;
					// Highlight trailing whitespace
					line = StringUtil.sanitizeHTML(line);
					line = line.replaceFirst("\\s+$", "<span class=\"whitespace\">$0</span>"); //$NON-NLS-1$ //$NON-NLS-2$
					line1.append("\n"); //$NON-NLS-1$
					line2.append(++hunkStartLine2).append("\n"); //$NON-NLS-1$
					diffContent.append("<div class=\"addline\">").append(line).append("</div>"); //$NON-NLS-1$ //$NON-NLS-2$
					break;

				case ' ':
					line1.append(++hunkStartLine1).append("\n"); //$NON-NLS-1$
					line2.append(++hunkStartLine2).append("\n"); //$NON-NLS-1$
					diffContent.append("<div class=\"noopline\">").append(StringUtil.sanitizeHTML(line)).append( //$NON-NLS-1$
							"</div>"); //$NON-NLS-1$
					break;

				case '-':
					line1.append(++hunkStartLine1).append("\n"); //$NON-NLS-1$
					line2.append("\n"); //$NON-NLS-1$
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
		html.append("</div>").append("</div>"); //$NON-NLS-1$ //$NON-NLS-2$

		InputStream stream = null;
		try
		{
			stream = FileLocator.openStream(GitUIPlugin.getDefault().getBundle(), new Path("templates") //$NON-NLS-1$
					.append("diff.html"), false); //$NON-NLS-1$
			String template = IOUtil.read(stream);
			Map<String, String> variables = new HashMap<String, String>();
			variables.put("\\{diff\\}", html.toString()); //$NON-NLS-1$
			return StringUtil.replaceAll(template, variables);
		}
		catch (Exception e)
		{
			GitUIPlugin.logError(e.getMessage(), e);
			return html.toString();
		}
	}

}
