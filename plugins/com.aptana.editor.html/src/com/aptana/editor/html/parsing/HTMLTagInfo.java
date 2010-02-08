package com.aptana.editor.html.parsing;

public interface HTMLTagInfo
{
	/**
	 * tag info not defined
	 */
	public static final int UNKNOWN = 0;

	/**
	 * The close tag is required
	 */
	public static final int END_REQUIRED = 1;

	/**
	 * The close tag is optional
	 */
	public static final int END_OPTIONAL = 2;

	/**
	 * The close tag is forbidden
	 */
	public static final int END_FORBIDDEN = 4;

	/**
	 * Mask used to isolate the end tag info
	 */
	public static final int END_MASK = 7;

	/**
	 * Content of tag must be empty
	 */
	public static final int EMPTY = 8;
}
