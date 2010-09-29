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
package com.aptana.editor.common.scripting.snippets;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateTranslator;

public class SnippetTemplateTranslator extends TemplateTranslator
{

	private static final String ZERO = "0"; //$NON-NLS-1$
	private static final String SINGLE_QUOTE = "'"; //$NON-NLS-1$
	private static final String OR = "|"; //$NON-NLS-1$
	private static final String COLON = ":"; //$NON-NLS-1$
	private static final String DOLLAR_LEFT_BRACE = "${"; //$NON-NLS-1$
	private static final String RIGHT_BRACE = "}"; //$NON-NLS-1$
	private static final String EMPTY_DEFAULT_VALUE = "('')"; //$NON-NLS-1$

	private static final String CURSOR = DOLLAR_LEFT_BRACE + "cursor" + RIGHT_BRACE; //$NON-NLS-1$

	private static final String SPACES = "\\s*+"; //$NON-NLS-1$

	private static final String NON_CURLY_BRACE_SNIPPET_VARIABLE_PATTERN_STRING = "\\$([\\p{Alnum}_]+)"; //$NON-NLS-1$
	private static final Pattern NON_CURLY_BRACE_SNIPPET_VARIABLE_PATTERN = Pattern
			.compile(NON_CURLY_BRACE_SNIPPET_VARIABLE_PATTERN_STRING);
	
	private static final String CURLY_BRACE_SNIPPET_VARIABLE_PATTERN_STRING = "\\$\\{" + SPACES + "([\\p{Alnum}_]+)" + SPACES + "(:((?:\\\\.|[^}])+))?\\}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final Pattern CURLY_BRACE_SNIPPET_VARIABLE_PATTERN = Pattern
			.compile(CURLY_BRACE_SNIPPET_VARIABLE_PATTERN_STRING);

	private static final String REVERSE_TICK_PATTERN_STRING = "`[^`]*`"; //$NON-NLS-1$

	private static final Pattern SNIPPET_VARIABLE_COMMANDS_AND_ESCAPES = Pattern.compile(
			  "\\\\`" //$NON-NLS-1$
			+ OR
			+ "\\\\\\$" //$NON-NLS-1$
			+ OR
			+ REVERSE_TICK_PATTERN_STRING
			+ OR
			+ NON_CURLY_BRACE_SNIPPET_VARIABLE_PATTERN_STRING
			+ OR
			+ CURLY_BRACE_SNIPPET_VARIABLE_PATTERN_STRING);

	@Override
	public TemplateBuffer translate(Template template) throws TemplateException
	{
		return super.translate(processExpansion(template.getPattern()));
	}

	static String processExpansion(String string)
	{

		final StringBuffer buffer = new StringBuffer(string.length());
		final Matcher matcher = SNIPPET_VARIABLE_COMMANDS_AND_ESCAPES.matcher(string);

		Set<String> seenVariables = new HashSet<String>();

		int complete = 0;
		while (matcher.find())
		{
			// append any verbatim text
			buffer.append(string.substring(complete, matcher.start()));

			String matched = matcher.group();
			if ("\\`".equals(matched)) //$NON-NLS-1$
			{
				// escaped reverse tick
				buffer.append("`"); //$NON-NLS-1$
			}
			else if ("\\$".equals(matched)) //$NON-NLS-1$
			{
				// escaped $
				buffer.append("$$"); //$NON-NLS-1$
			}
			else if (matched.startsWith("`")) //$NON-NLS-1$
			{
				// reverse quoted command
				// TODO evaluate as shell command and append the output
				buffer.append(matcher.group());
			}
			else
			{
				// Snippet variable with ${ syntax
				if (matched.startsWith(DOLLAR_LEFT_BRACE))
				{
					Matcher m = CURLY_BRACE_SNIPPET_VARIABLE_PATTERN.matcher(matched);
					m.find();
					String name = m.group(1);
					// the end caret position avriable
					if (ZERO.equals(name))
					{
						buffer.append(CURSOR);
					}
					else
					{
						buffer.append(DOLLAR_LEFT_BRACE);
						if (seenVariables.add(name))
						{
							// Seeing the variable first time
							try
							{
								Integer.parseInt(name);
								// It's a tab stop
								buffer.append(name + COLON + TabStopVariableResolver.VARIABLE_TYPE);
							}
							catch (NumberFormatException nfe)
							{
								// It is an environment variable
								buffer.append(name + COLON + EnvironmentVariableVariableResolver.VARIABLE_TYPE);
							}
							String defaultValues = m.group(2);
							if (defaultValues != null)
							{
								if (defaultValues.startsWith(":")) //$NON-NLS-1$
									defaultValues = defaultValues.substring(1);
								buffer.append("("); //$NON-NLS-1$
								boolean first = true;
								// We want to split on non-escaped '/'
								String[] values = defaultValues.split("(?<!\\\\)/"); //$NON-NLS-1$
								for (String value : values)
								{
									if (first)
									{
										first = false;
									}
									else
									{
										buffer.append(","); //$NON-NLS-1$
									}
									buffer.append(SINGLE_QUOTE);
									buffer.append(value.replaceAll(Pattern.quote("\\$"), Matcher.quoteReplacement("$")).replaceAll("\\\\/", "/").replaceAll("\\\\}", "\\}").replaceAll("'", "''").replaceAll("\\\\`", "`")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
									buffer.append(SINGLE_QUOTE);
								}
								buffer.append(")"); //$NON-NLS-1$
							}
							else
							{
								buffer.append(EMPTY_DEFAULT_VALUE);
							}
						}
						else
						{
							// Seeing the variable again - just record the name
							buffer.append(name);
						}
						buffer.append(RIGHT_BRACE);
					}
				}
				else
				{
					Matcher m = NON_CURLY_BRACE_SNIPPET_VARIABLE_PATTERN.matcher(matched);
					m.find();
					String name = m.group(1);
					// the end caret position variable
					if (ZERO.equals(name))
					{
						buffer.append(CURSOR);
					}
					else
					{
						buffer.append(DOLLAR_LEFT_BRACE);
						if (seenVariables.add(name))
						{
							// Seeing the variable first time
							try
							{
								Integer.parseInt(name);
								// It's a tab stop
								buffer.append(name + COLON + TabStopVariableResolver.VARIABLE_TYPE);
							}
							catch (NumberFormatException nfe)
							{
								// It is an environment variable
								buffer.append(name + COLON + EnvironmentVariableVariableResolver.VARIABLE_TYPE);
							}
							buffer.append(EMPTY_DEFAULT_VALUE);
						}
						else
						{
							// Seeing the variable again - just record the name
							buffer.append(name);
						}
						buffer.append(RIGHT_BRACE);
					}
				}
			}
			complete = matcher.end();
		}
		// append remaining verbatim text
		buffer.append(string.substring(complete));
		// handle escaped '/' or '\'
		String result = buffer.toString();
		result = result.replaceAll("\\\\/", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		result = result.replaceAll(Pattern.quote("\\\\"), Matcher.quoteReplacement("\\")); //$NON-NLS-1$ //$NON-NLS-2$
		return result;
	}

}
