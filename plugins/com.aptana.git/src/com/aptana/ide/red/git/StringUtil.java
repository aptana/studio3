package com.aptana.ide.red.git;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class StringUtil
{

	public static List<String> componentsSeparatedByString(String inputString, String delim)
	{
		List<String> tokens = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(inputString, delim);
		while (tokenizer.hasMoreTokens())
			tokens.add(tokenizer.nextToken());
		return tokens;
	}

}
