package com.aptana.index.core;

public abstract class SearchPattern
{
	public static final int EXACT_MATCH = 0;
	public static final int PREFIX_MATCH = 0x0001;
	public static final int PATTERN_MATCH = 0x0002;
	public static final int CASE_SENSITIVE = 0x0008;
	public static final int REGEX_MATCH = 0x0010;

}
