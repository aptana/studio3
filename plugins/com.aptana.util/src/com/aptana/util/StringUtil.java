package com.aptana.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public abstract class StringUtil
{
	/**
	 * Given a raw input string template, this will do a mass search and replace for the map of variables to values.
	 * Acts like {@link String#replaceAll(String, String)}
	 * 
	 * @param template
	 * @param variables
	 * @return
	 */
	public static String replaceAll(String template, Map<String, String> variables)
	{
		// TODO Point here from GitHistoryPage lines 274-281
		// TODO Point here from DiffFormatter lines 112-113?
		if (variables == null || variables.isEmpty())
			return template;
		for (Map.Entry<String, String> entry : variables.entrySet())
		{
			template = template.replaceAll(entry.getKey(), entry.getValue());
		}
		return template;
	}

	/**
	 * Runs the input through a StringTokenizer and gathers up all the tokens.
	 * 
	 * @param inputString
	 * @param delim
	 * @return
	 */
	public static List<String> tokenize(String inputString, String delim)
	{
		// TODO Replace Git Core's StringUtil class and method with this
		List<String> tokens = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(inputString, delim);
		while (tokenizer.hasMoreTokens())
			tokens.add(tokenizer.nextToken());
		return tokens;
	}

	/**
	 * Generate an MD5 hash of a string.
	 * 
	 * @param lowerCase
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public static String md5(String lowerCase) throws UnsupportedEncodingException, NoSuchAlgorithmException
	{
		// TODO point here from GitHistoryPage lines 317 - 344
		byte[] bytesOfMessage = lowerCase.getBytes("UTF-8"); //$NON-NLS-1$
		MessageDigest md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
		byte[] thedigest = md.digest(bytesOfMessage);
		BigInteger bigInt = new BigInteger(1, thedigest);
		String hashtext = bigInt.toString(16);
		// Now we need to zero pad it if you actually want the full 32 chars.
		while (hashtext.length() < 32)
		{
			hashtext = "0" + hashtext; //$NON-NLS-1$
		}
		return hashtext;
	}
}
