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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.w3c.tidy.Tidy;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.validator.IValidationItem;
import com.aptana.editor.common.validator.IValidationManager;
import com.aptana.editor.common.validator.IValidator;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.IHTMLConstants;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.parsing.ast.IParseNode;

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
	private static final String[] FILTERED = { "lacks \"type\" attribute", "replacing illegal character code" };

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
				addParseErrors(source, path, manager, items);
				while ((line = reader.readLine()) != null)
				{
					if (line.startsWith("line")) //$NON-NLS-1$
					{
						parseTidyOutput(line, path, manager, items, source);
					}
				}
			}
			catch (Exception e)
			{
				IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLTidyValidator_ERR_ParseErrors, e);
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
			tidy.parse(new ByteArrayInputStream(source.getBytes("UTF-8")), null); //$NON-NLS-1$
		}
		catch (Exception e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLTidyValidator_ERR_Tidy, e);
		}
		out.flush();

		return bout.toString();
	}

	private static void addParseErrors(String source, URI path, IValidationManager manager, List<IValidationItem> items)
	{
		addParseErrors(source, path, manager, items, manager.getAST());
	}

	private static void addParseErrors(String source, URI path, IValidationManager manager,
			List<IValidationItem> items, IParseNode ast)
	{
		IParseNode[] children = ast.getChildren();
		IDocument document = new Document(source);
		int line, column;

		if (children == null || children.length == 0)
		{
			return;
		}

		// Go through the AST and find if there are invalid self closing elements
		for (IParseNode node : children)
		{
			if (node instanceof HTMLElementNode)
			{
				if (((HTMLElementNode) node).isSelfClosing()
						&& !HTMLParseState.isEndForbiddenOrEmptyTag((node.getNameNode().getName())))
				{
					try
					{
						line = document.getLineOfOffset(node.getStartingOffset());
						column = node.getStartingOffset() - document.getLineOffset(line);
						if (column > 0)
						{
							// Need to add +1 to line since we start from 1 (not 0) in the editor
							items.add(manager.addError(
									Messages.HTMLTidyValidator_self_closing_syntax_on_non_void_element_error, line + 1,
									column, 0, path));
						}
					}
					catch (BadLocationException e)
					{
						IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLTidyValidator_ast_errors, e);
					}
				}
			}
			if (node.hasChildren())
			{
				addParseErrors(source, path, manager, items, node);
			}
		}

	}

	private static void parseTidyOutput(String report, URI path, IValidationManager manager,
			List<IValidationItem> items, String source) throws BadLocationException
	{
		Matcher matcher = PATTERN.matcher(report);
		IDocument document = new Document(source);

		while (matcher.find())
		{
			int lineNumber = Integer.parseInt(matcher.group(1));
			int column = Integer.parseInt(matcher.group(2));
			String type = matcher.group(3);
			String message = patchMessage(matcher.group(4));
			boolean hasErrorOnCurrentLine = false;

			// If we already have an error on the same line and offset, we don't add the tidy errors
			for (IValidationItem item : items)
			{
				if (item.getLineNumber() == lineNumber
						&& item.getOffset() == (column + document.getLineOffset(lineNumber - 1) - 1))
				{
					hasErrorOnCurrentLine = true;
					break;
				}
			}

			if (message != null && !manager.isIgnored(message, IHTMLConstants.CONTENT_TYPE_HTML)
					&& !containsHTML5Element(message) && !isAutoFiltered(message) && !hasErrorOnCurrentLine)
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
