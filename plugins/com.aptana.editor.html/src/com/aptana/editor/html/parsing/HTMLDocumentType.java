package com.aptana.editor.html.parsing;

public interface HTMLDocumentType
{
	/**
	 * Unrecognized document type
	 */
	public static final int OTHER = 0;

	/**
	 * HTML 2.0
	 */
	public static final int HTML_2_0 = 1;

	/**
	 * HTML 3.2
	 */
	public static final int HTML_3_2 = 2;

	/**
	 * HTML 4.0.1 strict
	 */
	public static final int HTML_4_0_1_STRICT = 3;

	/**
	 * HTML 4.0.1 transitional
	 */
	public static final int HTML_4_0_1_TRANSITIONAL = 4;

	/**
	 * HTML 4.0.1 frameset
	 */
	public static final int HTML_4_0_1_FRAMESET = 5;

	/**
	 * HTML 5.0
	 */
	public static final int HTML_5_0 = 6;

	/**
	 * XHTML 1.0 strict
	 */
	public static final int XHTML_1_0_STRICT = 7;

	/**
	 * XHTML 1.0 transitional
	 */
	public static final int XHTML_1_0_TRANSITIONAL = 8;

	/**
	 * XHTML 1.0 frameset
	 */
	public static final int XHTML_1_0_FRAMESET = 9;

	/**
	 * XHTML 1.1 strict document type
	 */
	public static final int XHTML_1_1_STRICT = 10;
}
