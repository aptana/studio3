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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.Document;
import org.w3c.tidy.Tidy;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.validator.IValidationItem;
import com.aptana.editor.common.validator.IValidationManager;
import com.aptana.editor.common.validator.IValidator;
import com.aptana.editor.common.validator.ValidationManager;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.IHTMLConstants;

public class HTMLTidyValidator implements IValidator
{

	private static final Pattern PATTERN = Pattern
			.compile("\\s*line\\s+(\\d+)\\s*column\\s+(\\d+)\\s*-\\s*(Warning|Error):\\s*(.+)$"); //$NON-NLS-1$

	@SuppressWarnings("nls")
	private static final String[] HTML5_ELEMENTS = { "article>", "aside>", "audio>", "canvas>", "command>",
			"datalist>", "details>", "embed>", "figcaption>", "figure>", "footer>", "header>", "hgroup>", "keygen>",
			"mark>", "meter>", "nav>", "output>", "progress>", "rp>", "rt>", "\"role\"", "ruby>", "section>",
			"source>", "summary>", "time>", "video>", "wbr>" };
	@SuppressWarnings("nls")
	private static final String[] FILTERED = { "lacks \"type\" attribute", "lacks \"summary\" attribute",
			"replacing illegal character code" };

	public List<IValidationItem> validate(String source, URI path, IValidationManager manager)
	{
		List<IValidationItem> items = new ArrayList<IValidationItem>();
		manager.addParseErrors(items, IHTMLConstants.CONTENT_TYPE_HTML);
		if (!StringUtil.isEmpty(source))
		{
			runTidy(path, manager, source, items);
		}
		return items;
	}

	private List<IValidationItem> runTidy(URI path, IValidationManager manager, final String source,
			List<IValidationItem> items)
	{
		final int numberOfLines = new Document(source).getNumberOfLines();

		// Set up our pipes
		PipedInputStream inPipe = new PipedInputStream();
		BufferedReader reader = null;
		try
		{
			final PrintWriter out = new PrintWriter(new PipedOutputStream(inPipe), true);
			reader = new BufferedReader(new InputStreamReader(inPipe));

			// Now set up tidy
			final Tidy tidy = new Tidy();
			tidy.setErrout(out);

			// parse via tidy in another Thread, so that we can parse it's output in main thread as it's running
			new Thread(new Runnable()
			{
				public void run()
				{
					ByteArrayInputStream in = null;
					try
					{
						in = new ByteArrayInputStream(source.getBytes("UTF-8")); //$NON-NLS-1$
						tidy.parse(in, null);
					}
					catch (UnsupportedEncodingException e)
					{
						// ignore, shouldn't even happen...
					}
					finally
					{
						if (in != null)
						{
							try
							{
								in.close();
							}
							catch (IOException e)
							{
								// ignore
							}
						}
						if (out != null)
						{
							out.close();
						}
					}
				}
			}).start();

			// Parse the output stream as we get it!
			String line = null;
			while ((line = reader.readLine()) != null) // $codepro.audit.disable assignmentInCondition
			{
				parseTidyOutput(line, path, manager, items, numberOfLines);
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLTidyValidator_ERR_Tidy, e);
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
					// ignore
				}
			}
		}
		return items;
	}

	// FIXME This takes in a collection and adds elements to it, which is bad! But necessary for now since we skip
	// errors based on it's contents right now...
	// Probably should return a collection of IValidationItems and then post-filter in caller!
	private static void parseTidyOutput(String report, URI path, IValidationManager manager,
			List<IValidationItem> items, int numberOfLines)
	{
		if (StringUtil.isEmpty(report) || !report.startsWith("line")) //$NON-NLS-1$
		{
			return;
		}

		Matcher matcher = PATTERN.matcher(report);
		while (matcher.find())
		{
			int lineNumber = Integer.parseInt(matcher.group(1));
			int column = Integer.parseInt(matcher.group(2));
			String type = matcher.group(3);
			String message = patchMessage(matcher.group(4));

			// Don't attempt to add errors or warnings if there are already errors on this line

			// We also squash errors on the last line, since it is normally a repeat of a parse error. For example
			// "missing </div>" will appear on the last line, which is normally caught by the parser and displayed on a
			// different line
			if (ValidationManager.hasErrorOrWarningOnLine(items, lineNumber)
					|| (numberOfLines == lineNumber && !items.isEmpty()))
			{
				continue;
			}

			if (message != null && !containsHTML5Element(message) && !isAutoFiltered(message)
					&& !manager.isIgnored(message, IHTMLConstants.CONTENT_TYPE_HTML))
			{
				if (type.startsWith("Error")) //$NON-NLS-1$
				{
					items.add(manager.createError(message, lineNumber, column, 0, path));
				}
				else if (type.startsWith("Warning")) //$NON-NLS-1$
				{
					items.add(manager.createWarning(message, lineNumber, column, 0, path));
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

	private static boolean isAutoFiltered(String message)
	{
		for (String element : FILTERED)
		{
			if (message.indexOf(element) > -1)
			{
				return true;
			}
		}
		return false;
	}
}
