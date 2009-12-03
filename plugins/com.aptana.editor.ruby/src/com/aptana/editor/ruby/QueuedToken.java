/**
 * 
 */
package com.aptana.editor.ruby;

import java.text.MessageFormat;

import org.eclipse.jface.text.rules.IToken;

/**
 * Stores an IToken along with it's offset and length so that we can do lookaheads and queue up tokens along with their
 * relative positions.
 * 
 * @author cwilliams
 */
class QueuedToken
{
	private IToken token;
	private int length;
	private int offset;

	QueuedToken(IToken token, int offset, int length)
	{
		this.token = token;
		this.length = length;
		this.offset = offset;
	}

	public int getLength()
	{
		return length;
	}

	public int getOffset()
	{
		return offset;
	}

	public IToken getToken()
	{
		return token;
	}

	@Override
	public String toString()
	{
		return MessageFormat.format("{0}: offset: {1}, length: {2}", getToken().getData(), getOffset(), getLength()); //$NON-NLS-1$
	}
}