package com.aptana.editor.ruby;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * Wraps the an IPartitionSTokencanner and merges consecutive tokens with the same data/partition marking. So if we have 10
 * default tokens in a row, this will eat up the ten and return a token that spans all of them.
 * 
 * @author Chris Williams
 */
public class MergingPartitionScanner implements IPartitionTokenScanner
{

	private IPartitionTokenScanner fScanner;
	private int fOffset;
	private int fLength;
	private int newOffset = 0;
	private int newLength = 0;
	private IToken lastToken;

	public MergingPartitionScanner(IPartitionTokenScanner wrappedScanner)
	{
		fScanner = wrappedScanner;
	}

	public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset)
	{
		clear();
		fScanner.setPartialRange(document, offset, length, contentType, partitionOffset);
	}

	public int getTokenLength()
	{
		return fLength;
	}

	public int getTokenOffset()
	{
		return fOffset;
	}

	public IToken nextToken()
	{
		setLength(newLength);
		setOffset(newOffset);
		if (lastToken != null && lastToken.isEOF())
		{
			return lastToken;
		}

		IToken token = null;
		while (!(token = fScanner.nextToken()).isEOF())
		{
			if (lastToken != null && token.getData().equals(lastToken.getData()))
			{
				continue;
			}
			if (lastToken == null)
			{
				lastToken = token;
				setOffset(fScanner.getTokenOffset());
				setLength(fScanner.getTokenLength());
				continue;
			}
			return wrapUp(token);
		}
		if (lastToken == null)
		{
			return Token.EOF;
		}
		return wrapUp(token);
	}

	private IToken wrapUp(IToken token)
	{
		setLength(fScanner.getTokenOffset() - fOffset);
		newOffset = fScanner.getTokenOffset();
		newLength = fScanner.getTokenLength();
		Assert.isTrue(newLength >= 0);
		IToken returnToken = lastToken; // make a copy of the last token
		lastToken = token; // save new token
		return returnToken;
	}

	private void setOffset(int tokenOffset)
	{
		fOffset = tokenOffset;
	}

	private void setLength(int tokenLength)
	{
		Assert.isTrue(tokenLength >= 0);
		fLength = tokenLength;
	}

	public void setRange(IDocument document, int offset, int length)
	{
		clear();
		fScanner.setRange(document, offset, length);
	}

	private void clear()
	{
		lastToken = null;
		fLength = 0;
		fOffset = 0;
		newLength = 0;
		newOffset = 0;
	}

}
