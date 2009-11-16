package com.aptana.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

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
		if (variables == null || variables.isEmpty())
			return template;
		for (Map.Entry<String, String> entry : variables.entrySet())
		{
			String value = entry.getValue();
			if (value == null)
				value = ""; //$NON-NLS-1$
			else
				value = value.replace('$', (char) 1); // To avoid illegal group reference issues if the text has
														// dollars!
			template = template.replaceAll(entry.getKey(), value).replace((char) 1, '$');
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
	 * @return null if an exception occurs.
	 */
	public static String md5(String lowerCase)
	{
		if (lowerCase == null)
			return null;
		try
		{
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
		catch (Exception e)
		{
			UtilPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, UtilPlugin.PLUGIN_ID, e.getMessage(), e));
		}
		return null;
	}

	/**
	 * Sanitizes raw HTML to escape '&', '<' and '>' so that it is suitable for embedding into HTML.
	 * 
	 * @param raw
	 * @return
	 */
	public static String sanitizeHTML(String raw)
	{
		return raw.replaceAll("&", "&amp;").replaceAll("<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
}
