package com.aptana.js.core.parsing.antlr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.WritableToken;

/**
 * This represents a fixed-size token buffer/window. We add tokens to an internal collection, removing the head element
 * first if we would have expanded beyond capacity. This makes it difficult to determine the exact number of tokens we
 * could lookbehind or ahead, based on how we're called. It is conceivable we could fill the collection only with
 * lookaheads and not be able to lookbehind, but is unlikely.
 * 
 * @author cwilliams
 */
class TokenWindow
{
	private final TokenSource tokenSource;
	private final ArrayList<Token> tokens;
	private final Map<Integer, Integer> markers;
	private final int maxSize;
	private boolean sawEOF;
	private int currentTokenIndex = 0; // Used to communicate the minimum token index we should retain when we release
										// and trim!
	private int lastTokenIndex = 0; // keep track of token indices as we add them
	private int markerMaker = 0;

	TokenWindow(TokenSource tokenSource, int fixedSize)
	{
		this.tokenSource = tokenSource;
		this.maxSize = fixedSize;
		this.tokens = new ArrayList<Token>(fixedSize);
		this.markers = new HashMap<Integer, Integer>();
		sawEOF = false;
		// fetch some tokens to initialize!
		fetch();
	}

	private Token fetch()
	{
		if (sawEOF)
		{
			throw new IllegalStateException(
					"Already saw EOF, but still attempting to fetch tokens from the source. Don't do that!");
		}
		Token t = tokenSource.nextToken();
		add(t);
		// Mark if this was EOF to guard against trying to fetch tokens in the future!
		if (t.getType() == Token.EOF)
		{
			sawEOF = true;
		}
		return t;
	}

	private void add(Token t)
	{
		// If we have markers, don't enforce size! Let it grow indefinitely big (ugh).
		if (markers.isEmpty() && size() == maxSize)
		{
			// TODO If we have markers we could potentially do some trimming here, but it's not straightforward
			// We could re-use #trimBuffer potentially with some changes. We'd need to ask for the min value of all
			// markers and current token index as the potential first index
			this.tokens.remove(0);
		}
		if (t instanceof WritableToken)
		{
			((WritableToken) t).setTokenIndex(lastTokenIndex++);
		}
		this.tokens.add(t);
	}

	int size()
	{
		return this.tokens.size();
	}

	int tokenStartIndex()
	{
		Token startToken;
		// TODO Keep track in a field so we don't need to pull a token to do this?
		if (isEmpty())
		{
			startToken = fetch();
		}
		else
		{
			startToken = this.tokens.get(0);
		}
		if (startToken == null)
		{
			throw new IllegalStateException();
		}
		return startToken.getTokenIndex();
	}

	private boolean isEmpty()
	{
		return size() == 0;
	}

	Token getToken(int tokenIndex)
	{
		int startIndex = tokenStartIndex();
		int relativeIndex = tokenIndex - startIndex;
		return tokens.get(relativeIndex); // sanity check with the token?
	}

	int nextTokenIndexOnChannel(int startIndex, int channel)
	{
		int tokenWindowStartIndex = tokenStartIndex();
		int relativeIndex = startIndex - tokenWindowStartIndex;
		for (int i = relativeIndex;; i++)
		{
			Token t;
			if (i > size() - 1)
			{
				if (sawEOF)
				{ // never look past EOF
					return lastTokenIndex - 1; // This *should* be EOF's index...
				}
				t = fetch();
			}
			else
			{
				t = tokens.get(i);
			}
			// t should never be null...
			if (t.getChannel() == channel || t.getType() == Token.EOF)
			{
				return i + tokenWindowStartIndex;
			}
		}
	}

	int previousTokenIndexOnChannel(int startIndex, int defaultChannel)
	{
		int tokenWindowStartIndex = tokenStartIndex();
		int relativeIndex = startIndex - tokenWindowStartIndex;
		if (relativeIndex < 0)
		{
			// If we're looking before the buffer/window, return -1 here, and guard for that to return null in #LT
			return -1;
		}
		for (int i = relativeIndex; i >= 0; i--)
		{
			Token t = tokens.get(i);
			if (t == null)
			{
				// This should *never* happen
				throw new IllegalStateException("tried to find previous non-hidden token from index: " + startIndex
						+ ", and ran into a null token first!");
			}
			if (t.getChannel() == defaultChannel)
			{
				return i + tokenWindowStartIndex;
			}
		}
		// Uh-oh, we didn't find one. That's BAD! We likely should have retained more tokens on release! But it's
		// possible we're doing a predicate check (like for line terminators) and we haven't yet consume a default token
		// (i.e. we tend to have lots of comments/whitespace to start a file)
		return -1;
		// throw new IllegalStateException("tried to find previous non-hidden token from index: " + startIndex
		// + ", and none of our previous tokens matched!");
	}

	public int mark(int tokenIndex)
	{
		// System.out.println("Marking token index: " + tokenIndex);
		markers.put(markerMaker++, tokenIndex);
		return markerMaker - 1;

	}

	public void release(int markerNumber)
	{
		int index = markers.remove(markerNumber);
		// System.out.println("Releasing marked token index: " + index);
		if (!markers.isEmpty())
		{
			return;
		}

		trimBuffer(this.currentTokenIndex - 1); // We try to keep 1 default token lookback
	}

	/**
	 * Given a potential first token index to retain, Trims the token buffer down to size, then ensures max capacity.
	 * 
	 * @param potentialFirstIndex
	 *            The index of the token we'd like to be first in our buffer. This may change if that token is
	 *            non-default. We'll search backwards to find first default token to retain for lookbacks
	 */
	private void trimBuffer(int potentialFirstIndex)
	{
		int startIndex = tokenStartIndex();
		// If the potential single lookback is not on default channel, look backwards to make sure we retain one
		int lastDefaultTokenIndex = previousTokenIndexOnChannel(potentialFirstIndex, Token.DEFAULT_CHANNEL);
		if (lastDefaultTokenIndex == -1)
		{
			// System.out.println("No default tokens found in lookback from current index (" + this.currentTokenIndex
			// + "), doing no trimming.");
			return;
		}
		int relativeIndex = lastDefaultTokenIndex - startIndex; // this is the relative index in the list to the
																// last default token
		int numToTrim = relativeIndex - 1;
		if (numToTrim < 0)
		{
			// System.out.println("current token index is: " + this.currentTokenIndex + ". Last default token is: "
			// + lastDefaultTokenIndex + ". Not removing any tokens, since start index is " + startIndex);
			return;
		}
		// System.out.println("current token index is: " + this.currentTokenIndex + ". Last default token is: "
		// + lastDefaultTokenIndex + ". Removing tokens at index: " + startIndex + ", to "
		// + (numToTrim + startIndex));
		int beforeSize = tokens.size();
		while (numToTrim >= 0)
		{
			tokens.remove(0);
			numToTrim--;
		}
		// Only fiddle with trimming arraylist if we expanded past the max capacity we wanted
		if (beforeSize > maxSize)
		{
			tokens.trimToSize();
			tokens.ensureCapacity(maxSize);
		}
	}

	public void setCurrentIndex(int currentTokenIndex)
	{
		this.currentTokenIndex = currentTokenIndex;
	}

}
