/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.tidy.Tidy;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.validator.IValidationItem;
import com.aptana.editor.common.validator.IValidationManager;
import com.aptana.editor.common.validator.IValidator;
import com.aptana.editor.html.Activator;

public class HTMLTidyValidator implements IValidator
{

	private static final Pattern PATTERN = Pattern
			.compile("\\s*line\\s+(\\d+)\\s*column\\s+(\\d+)\\s*-\\s*(Warning|Error):\\s*(.+)$"); //$NON-NLS-1$

	public List<IValidationItem> validate(String source, URI path, IValidationManager manager)
	{
		String report = parseWithTidy(source);
		if (StringUtil.isEmpty(report))
		{
			return Collections.emptyList();
		}

		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new StringReader(report));
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (line.startsWith("line")) //$NON-NLS-1$
				{
					parseTidyOutput(line, path, manager);
				}
			}
		}
		catch (Exception e)
		{
			Activator.logError(Messages.HTMLTidyValidator_ERR_ParseErrors, e);
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
		return manager.getItems();
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
			Activator.logError(Messages.HTMLTidyValidator_ERR_Tidy, e);
		}
		out.flush();

		return bout.toString();
	}

	private static void parseTidyOutput(String report, URI path, IValidationManager manager)
	{
		Matcher matcher = PATTERN.matcher(report);

		while (matcher.find())
		{
			int lineNumber = Integer.parseInt(matcher.group(1));
			int column = Integer.parseInt(matcher.group(2));
			String type = matcher.group(3);
			String message = patchMessage(matcher.group(4));

			if (message != null)
			{
				if (type.startsWith("Error")) //$NON-NLS-1$
				{
					manager.addError(message, lineNumber, column, 0, path);
				}
				else if (type.startsWith("Warning")) //$NON-NLS-1$
				{
					manager.addWarning(message, lineNumber, column, 0, path);
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
}
