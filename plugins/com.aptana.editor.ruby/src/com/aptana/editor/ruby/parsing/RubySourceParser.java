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
package com.aptana.editor.ruby.parsing;

import java.io.Reader;
import java.io.StringReader;

import org.jrubyparser.IRubyWarnings;
import org.jrubyparser.Parser.NullWarnings;
import org.jrubyparser.lexer.LexerSource;
import org.jrubyparser.parser.ParserConfiguration;
import org.jrubyparser.parser.ParserResult;
import org.jrubyparser.parser.ParserSupport;
import org.jrubyparser.parser.Ruby18Parser;

/**
 * @author Chris Williams
 * @author Michael Xia
 */
public class RubySourceParser
{

	private IRubyWarnings warnings;
	private Ruby18Parser parser;
	private ParserConfiguration config;

	RubySourceParser()
	{
		this(new NullWarnings());
	}

	RubySourceParser(IRubyWarnings warnings)
	{
		this.warnings = warnings;
	}

	public ParserResult parse(String source)
	{
		return parse((String) null, source);
	}

	public ParserResult parse(String fileName, String source)
	{
		return parse(fileName, source, false);
	}

	/**
	 * @param fileName
	 *            the name of the file
	 * @param source
	 *            the source text
	 * @param bypassCache
	 *            boolean indicating if to force a parse and bypass any cached results
	 * @return the parse result
	 */
	public ParserResult parse(String fileName, String source, boolean bypassCache)
	{
		if (source == null)
		{
			return new NullParserResult();
		}

		ParserResult ast = null;
		StringReader reader = new StringReader(source);
		try
		{
			ast = parse(fileName, reader);
		}
		catch (Exception e)
		{
		}
		finally
		{
			reader.close();
		}
		if (ast == null)
		{
			return new NullParserResult();
		}
		return ast;
	}

	private ParserResult parse(String fileName, Reader content) throws Exception
	{
		if (fileName == null)
		{
			fileName = ""; //$NON-NLS-1$
		}
		if (parser == null)
		{
			config = getParserConfig();
			ParserSupport support = new ParserSupport();
			support.setConfiguration(config);
			parser = new Ruby18Parser(support);
		}
		parser.setWarnings(warnings);
		LexerSource lexerSource = LexerSource.getSource(fileName, content, config);
		ParserResult result = parser.parse(config, lexerSource);
		postProcessResult(result);
		return result;
	}

	/**
	 * Hook for subclasses to perform extra work on the resulting AST such as doing a pass through comments.
	 * 
	 * @param result
	 */
	protected void postProcessResult(ParserResult result)
	{
		// do nothing
	}

	protected ParserConfiguration getParserConfig()
	{
		return new ParserConfiguration();
	}
}
