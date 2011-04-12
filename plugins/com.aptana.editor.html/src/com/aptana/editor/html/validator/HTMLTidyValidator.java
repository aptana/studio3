/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.validator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.tidy.Tidy;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.validator.IValidationItem;
import com.aptana.editor.common.validator.IValidationManager;
import com.aptana.editor.common.validator.IValidator;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.parsing.IHTMLParserConstants;

public class HTMLTidyValidator implements IValidator
{

	private static final Pattern PATTERN = Pattern
			.compile("\\s*line\\s+(\\d+)\\s*column\\s+(\\d+)\\s*-\\s*(Warning|Error):\\s*(.+)$"); //$NON-NLS-1$

	@SuppressWarnings("nls")
	private static final String[] HTML5_ELEMENTS = { "article>", "aside>", "audio>", "canvas>", "command>",
			"datalist>", "details>", "embed>", "figcaption>", "figure>", "footer>", "header>", "hgroup>", "keygen>",
			"mark>", "meter>", "nav>", "output>", "progress>", "rp>", "rt>", "\"role\"", "ruby>", "section>",
			"source>", "summary>", "time>", "video>", "wbr>" };

	public List<IValidationItem> validate(String source, URI path, IValidationManager manager)
	{
		List<IValidationItem> items = new ArrayList<IValidationItem>();
		String report = parseWithTidy(source);
		if (!StringUtil.isEmpty(report))
		{
			BufferedReader reader = null;
			try
			{
				reader = new BufferedReader(new StringReader(report));
				String line;
				while ((line = reader.readLine()) != null)
				{
					if (line.startsWith("line")) //$NON-NLS-1$
					{
						parseTidyOutput(line, path, manager, items);
					}
				}
			}
			catch (Exception e)
			{
				HTMLPlugin.logError(Messages.HTMLTidyValidator_ERR_ParseErrors, e);
			}
			finally
			{
				if (reader != null)
				{
					try
					{
						reader.close();
					}
					catch (IOException e)
					{
						// ignores
					}
				}
			}
		}
		return items;
	}

	private static String parseWithTidy(String source)
	{
		Tidy tidy = new Tidy();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(bout);
		tidy.setErrout(out);
		try
		{
			tidy.parse(new ByteArrayInputStream(source.getBytes()), null);
		}
		catch (Exception e)
		{
			HTMLPlugin.logError(Messages.HTMLTidyValidator_ERR_Tidy, e);
		}
		out.flush();

		return bout.toString();
	}

	private static void parseTidyOutput(String report, URI path, IValidationManager manager, List<IValidationItem> items)
	{
		Matcher matcher = PATTERN.matcher(report);

		while (matcher.find())
		{
			int lineNumber = Integer.parseInt(matcher.group(1));
			int column = Integer.parseInt(matcher.group(2));
			String type = matcher.group(3);
			String message = patchMessage(matcher.group(4));

			if (message != null && !manager.isIgnored(message, IHTMLParserConstants.LANGUAGE)
					&& !containsHTML5Element(message))
			{
				if (type.startsWith("Error")) //$NON-NLS-1$
				{
					items.add(manager.addError(message, lineNumber, column, 0, path));
				}
				else if (type.startsWith("Warning")) //$NON-NLS-1$
				{
					items.add(manager.addWarning(message, lineNumber, column, 0, path));
				}
			}
		}
	}

	private static String patchMessage(String message)
	{
		if (message == null)
		{
			return null;
		}
		message = message.replaceFirst("discarding", "should discard"); //$NON-NLS-1$ //$NON-NLS-2$
		message = message.replaceFirst("inserting", "should insert"); //$NON-NLS-1$ //$NON-NLS-2$
		message = message.replaceFirst("trimming", "should trim"); //$NON-NLS-1$ //$NON-NLS-2$
		return message;
	}

	private static boolean containsHTML5Element(String message)
	{
		for (String element : HTML5_ELEMENTS)
		{
			if (message.indexOf(element) > -1)
			{
				return true;
			}
		}
		return false;
	}
}
