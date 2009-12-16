package com.aptana.editor.common;

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
	private char firstChar = Character.MIN_VALUE;

	public RegexpRule(String regexp, IToken successToken)
	{
		this(regexp, successToken, false);
	}

	/**
	 * This takes an additional flag to optimize the rule matching.
	 * 
	 * @param regexp
	 * @param successToken
	 * @param matchFirstCharFirst - a value of true is a hint to match the first char of the regexp first before proceeding to to Pattern match. 
	 */
	public RegexpRule(String regexp, IToken successToken, boolean matchFirstCharFirst)
	{
		this.regexp = Pattern.compile(regexp);
		this.successToken = successToken;
		if (matchFirstCharFirst && regexp.length() > 0)
		{ // TODO always use this optimization of the first character is a non-sepcial one? (i.e. not ".*([-\?+{" )
			firstChar = regexp.charAt(0);
		}
	}

	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		// Should we try to match the first char first?
		if (firstChar != Character.MIN_VALUE) {
			int readChar = scanner.read();
			scanner.unread();
			if (readChar == ICharacterScanner.EOF) {
				return Token.EOF;
			}
			if (firstChar != readChar)
			{
				return Token.UNDEFINED;
			}
		}
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
		unread(scanner, line.length());
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
