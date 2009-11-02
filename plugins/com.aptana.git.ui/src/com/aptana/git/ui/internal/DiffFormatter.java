package com.aptana.git.ui.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.git.ui.GitUIPlugin;

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
	 * @param diff
	 * @return
	 */
	public static String toHTML(String diff)
	{
		if (diff == null)
		{
			return "";
		}
		if (!diff.startsWith("diff"))
		{
			return "<pre>" + diff + "</pre>";
		}
		String title = ""; // FIXME Grab the filename!
		StringBuilder html = new StringBuilder();
		html.append("<div class=\"file\">");
		html.append("<div class=\"fileHeader\">").append(title).append("</div>");
		html.append("<div class=\"diffContent\">");
		String[] lines = diff.split("\r|\n|\r\n");
		StringBuilder diffContent = new StringBuilder();
		StringBuilder line1 = new StringBuilder();
		StringBuilder line2 = new StringBuilder();
		int hunkStartLine1 = 0;
		int hunkStartLine2 = 0;
		for (int i = 4; i < lines.length; i++)
		{
			String line = lines[i];
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
					line1.append("..\n");
					line2.append("..\n");
					diffContent.append("<div class=\"hunkheader\">").append(sanitizeHTML(line)).append("</div>");
					break;

				case '+':
					// Highlight trailing whitespace
					line = line.replaceFirst("\\s+$", "<span class=\"whitespace\">$0</span>");
					line1.append("\n");
					line2.append(++hunkStartLine2).append("\n");
					diffContent.append("<div class=\"addline\">").append(sanitizeHTML(line)).append("</div>");
					break;

				case ' ':
					line1.append(++hunkStartLine1).append("\n");
					line2.append(++hunkStartLine2).append("\n");
					diffContent.append("<div class=\"noopline\">").append(sanitizeHTML(line)).append("</div>");
					break;

				case '-':
					line1.append(++hunkStartLine1).append("\n");
					line2.append("\n");
					diffContent.append("<div class=\"delline\">").append(sanitizeHTML(line)).append("</div>");
					break;

				default:
					break;
			}

		}
		html.append("<div class=\"lineno\">").append(line1).append("</div>");
		html.append("<div class=\"lineno\">").append(line2).append("</div>");
		html.append("<div class=\"lines\">").append(diffContent).append("</div>");
		html.append("</div>").append("</div>");

		InputStream stream = null;
		try
		{
			stream = FileLocator.openStream(GitUIPlugin.getDefault().getBundle(), new Path("templates")
					.append("diff.html"), false);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder template = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				template.append(line).append("\n");
			}			
			// Sanitize to remove $ so Java doesn't think I'm referring to groups for our replacement here
			String sanitizedHTML = html.toString().replace('$', (char) 1);
			return template.toString().replaceFirst("\\{diff\\}", sanitizedHTML).replace((char) 1, '$');
		}
		catch (Exception e)
		{
			GitUIPlugin.logError(e.getMessage(), e);
			return html.toString();
		}
		finally
		{
			try
			{
				if (stream != null)
					stream.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}

	private static String sanitizeHTML(String line)
	{
		return line.replaceAll("&", "&amp;").replaceAll("<", "&lt;");
	}

}
