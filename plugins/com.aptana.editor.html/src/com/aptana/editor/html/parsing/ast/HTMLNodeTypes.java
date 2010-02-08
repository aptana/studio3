package com.aptana.editor.html.parsing.ast;

public interface HTMLNodeTypes
{
	public static final short ERROR = -1;

	public static final short UNKNOWN = 0;

	public static final short DECLARATION = 1;

	public static final short ELEMENT = 2;

	/**
	 * Used to indicate a transition to another language
	 */
	public static final short SPECIAL = 3;
}
