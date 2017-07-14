package com.aptana.js.core.parsing.antlr;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;

/**
 * This token stream uses a fixed-size {@link TokenWindow} to hold tokens. We also filter {@link #LT(int)} and
 * {@link #LA(int)} to only default channel tokens. {@link #get(int)} does not filter, and is a way to look up hidden
 * channel tokens.
 * 
 * @author cwilliams
 */
public class FilteringMaxCapacityTokenStream implements TokenStream
{

	private static final int MAX_TOKENS = 256; // FIXME This is bad! I had to bump to 1024 to get jaxer's
												// ComposerCommands.js to parse. We likely have a bad grammar then

	private TokenSource tokenSource;
	private TokenWindow tokens;
	private int currentTokenIndex = 0;

	public FilteringMaxCapacityTokenStream(TokenSource tokenSource)
	{
		this.tokenSource = tokenSource;
		this.tokens = new TokenWindow(tokenSource, MAX_TOKENS);
	}

	@Override
	public void consume()
	{
		// Increment index to next token
		currentTokenIndex = nextTokenIndex(currentTokenIndex + 1);
		tokens.setCurrentIndex(currentTokenIndex);
	}

	private int nextTokenIndex(int startIndex)
	{
		return tokens.nextTokenIndexOnChannel(startIndex, Token.DEFAULT_CHANNEL);
	}

	private int prevTokenIndex(int startIndex)
	{
		return tokens.previousTokenIndexOnChannel(startIndex, Token.DEFAULT_CHANNEL);
	}

	@Override
	public int mark()
	{
		return tokens.mark(currentTokenIndex);
	}

	@Override
	public void release(int marker)
	{
		tokens.release(marker);
	}

	@Override
	public int index()
	{
		return currentTokenIndex;
	}

	@Override
	public void seek(int index)
	{
		int startIndex = tokens.tokenStartIndex();
		if (index < startIndex)
		{
			throw new IllegalArgumentException(
					"cannot seek to negative index " + index + ", buffer starts at " + startIndex); //$NON-NLS-1$
		}

		currentTokenIndex = nextTokenIndex(index);
		tokens.setCurrentIndex(currentTokenIndex);
	}

	@Override
	public int size()
	{
		throw new UnsupportedOperationException("Unbuffered stream cannot know its size"); //$NON-NLS-1$
	}

	@Override
	public String getSourceName()
	{
		return tokenSource.getSourceName();
	}

	@Override
	public Token LT(int k)
	{
		if (k == 0)
			return null;
		if (k > 0)
		{
			int targetIndex = currentTokenIndex + (k - 1);
			targetIndex = nextTokenIndex(targetIndex); // adjust by filtering to next non-hidden
			return get(targetIndex);
		}

		if (k < 0)
		{
			int targetIndex = currentTokenIndex + k;
			targetIndex = prevTokenIndex(targetIndex); // adjust by filtering to previous non-hidden
			if (targetIndex == -1)
			{ // -1 indicates we looked before our buffer start, so return null
				return null;
			}
			return get(targetIndex);
		}
		return null;
	}

	@Override
	public Token get(int index)
	{
		return tokens.getToken(index);
	}

	@Override
	public int LA(int i)
	{
		return LT(i).getType();
	}

	@Override
	public TokenSource getTokenSource()
	{
		return tokenSource;
	}

	@Override
	public String getText()
	{
		return ""; //$NON-NLS-1$
	}

	@Override
	public String getText(RuleContext ctx)
	{
		return getText(ctx.getSourceInterval());
	}

	@Override
	public String getText(Token start, Token stop)
	{
		return getText(Interval.of(start.getTokenIndex(), stop.getTokenIndex()));
	}

	@Override
	public String getText(Interval interval)
	{
		int bufferStartIndex = tokens.tokenStartIndex();
		int bufferStopIndex = bufferStartIndex + tokens.size() - 1;

		int start = interval.a;
		int stop = interval.b;
		if (start < bufferStartIndex || stop > bufferStopIndex)
		{
			throw new UnsupportedOperationException("interval " + interval + " not in token buffer window: "
					+ bufferStartIndex + ".." + bufferStopIndex);
		}

		StringBuilder buf = new StringBuilder();
		for (int i = start; i <= stop; i++)
		{
			Token t = tokens.getToken(i);
			buf.append(t.getText());
		}

		return buf.toString();
	}

}
