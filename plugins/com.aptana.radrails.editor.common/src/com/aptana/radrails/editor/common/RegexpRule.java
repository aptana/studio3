package com.aptana.radrails.editor.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class RegexpRule implements IPredicateRule
{

	private Pattern regexp;
	private IToken successToken;

	public RegexpRule(String regexp, IToken successToken)
	{
		this.regexp = Pattern.compile(regexp);
		this.successToken = successToken;
	}

	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		String line = readNextLine(scanner);
		if (line == null)
			return Token.EOF;
		Matcher matcher = regexp.matcher(line);
		if (matcher.find() && matcher.start() == 0)
		{
			// Unread back to end of regexp match!
			String match = matcher.group();
			if (match.length() < line.length())
			{
				int toUnread = line.length() - match.length();
				unread(scanner, toUnread);
			}
			return successToken;
		}
		else
		{
			unread(scanner, line.length());
		}
		return Token.UNDEFINED;
	}

	private void unread(ICharacterScanner scanner, int toUnread)
	{
		while (toUnread > 0)
		{
			scanner.unread();
			toUnread--;
		}
	}

	private String readNextLine(ICharacterScanner scanner)
	{
		char[][] lineDelims = scanner.getLegalLineDelimiters();
		StringBuilder builder = new StringBuilder();
		int c;
		while ((c = scanner.read()) != ICharacterScanner.EOF)
		{
			// FIXME We need to properly handle multi-char line delims!
			if (isLineDelim((char) c, lineDelims))
			{
				break;
			}
			builder.append((char) c);
		}
		if (c == ICharacterScanner.EOF && builder.length() == 0)
			return null;
		scanner.unread();
		return builder.toString();
	}

	private boolean isLineDelim(char c, char[][] lineDelims)
	{
		for (char[] lineDelim : lineDelims)
		{
			if (c == lineDelim[0])
				return true;
		}
		return false;
	}

	public IToken getSuccessToken()
	{
		return successToken;
	}

	public IToken evaluate(ICharacterScanner scanner)
	{
		return evaluate(scanner, false);
	}

}
