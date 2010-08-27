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
