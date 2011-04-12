/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.validator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.css.css.StyleReport;
import org.w3c.css.css.StyleReportFactory;
import org.w3c.css.css.StyleSheet;
import org.w3c.css.css.StyleSheetParser;
import org.w3c.css.properties.PropertiesLoader;
import org.w3c.css.util.ApplContext;
import org.w3c.css.util.Utf8Properties;

import com.aptana.core.util.URLEncoder;
import com.aptana.editor.common.validator.IValidationItem;
import com.aptana.editor.common.validator.IValidationManager;
import com.aptana.editor.common.validator.IValidator;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.parsing.ICSSParserConstants;

public class CSSValidator implements IValidator
{

	private static final String APTANA_PROFILE = "AptanaProfile"; //$NON-NLS-1$
	private static final String CONFIG_FILE = "AptanaCSSConfig.properties"; //$NON-NLS-1$
	private static final String PROFILES_CONFIG_FILE = "AptanaCSSProfiles.properties"; //$NON-NLS-1$

	/**
	 * error pattern
	 */
	private static final Pattern ERROR_PATTERN = Pattern.compile(
			"<(error)>(.*?)</\\1>", Pattern.MULTILINE | Pattern.DOTALL); //$NON-NLS-1$
	/**
	 * warning pattern
	 */
	private static final Pattern WARNING_PATTERN = Pattern.compile(
			"<(warning)>(.*?)</\\1>", Pattern.MULTILINE | Pattern.DOTALL); //$NON-NLS-1$

	/**
	 * properties pattern
	 */
	private static final Pattern PROPERTIES_PATTERN = Pattern.compile(
			"<([-A-Za-z0-9_:]+)>(.*?)</\\1>", Pattern.MULTILINE | Pattern.DOTALL); //$NON-NLS-1$

	// CSS3 properties that the validator doesn't recognize yet and need to be ignored
	@SuppressWarnings("nls")
	private static final String[] CSS3_PROPERTIES = { "box-shadow", "column-count", "column-width", "column-gap",
			"column-rule", "border-radius", "background-clip", "background-origin", "border-top-right-radius",
			"border-bottom-right-radius", "border-bottom-left-radius", "border-top-left-radius", "resize" };

	public CSSValidator()
	{
		loadAptanaCSSProfile();
	}

	public List<IValidationItem> validate(String source, URI path, IValidationManager manager)
	{
		List<IValidationItem> items = new ArrayList<IValidationItem>();
		String report = getReport(source, path);
		processErrorsInReport(report, path, manager, items);
		processWarningsInReport(report, path, manager, items);
		return items;
	}

	private void processErrorsInReport(String report, URI sourceUri, IValidationManager manager,
			List<IValidationItem> items)
	{
		int offset = 0;
		String elementName = "errorlist"; //$NON-NLS-1$
		String startTag = MessageFormat.format("<{0}>", elementName); //$NON-NLS-1$
		String endTag = MessageFormat.format("</{0}>", elementName); //$NON-NLS-1$
		String sourcePath = sourceUri.toString();

		while (offset < report.length())
		{
			int errorListStart = report.indexOf(startTag, offset);
			if (errorListStart == -1)
			{
				break;
			}

			// advances past the start tag
			errorListStart += startTag.length();

			// gets the URI
			int uriStart = report.indexOf("<uri>", errorListStart) + "<uri>".length(); //$NON-NLS-1$ //$NON-NLS-2$
			int uriEnd = report.indexOf("</uri>", uriStart); //$NON-NLS-1$
			String uri = new String(report.substring(uriStart, uriEnd));

			// finds the end of this list
			int errorListEnd = report.indexOf(endTag, errorListStart);

			// checks if the uri matches the source
			if (uri != null && URLEncoder.encode(uri, null, null).equals(sourcePath))
			{
				// extracts the error list
				String listString = new String(report.substring(errorListStart, errorListEnd));
				// finds the errors
				String[] errors = getContent(ERROR_PATTERN, listString);
				// add errors
				addErrors(errors, sourceUri, manager, items);
			}

			// advances past the current error list
			offset = errorListEnd + endTag.length();
		}
	}

	private void processWarningsInReport(String report, URI sourceUri, IValidationManager manager,
			List<IValidationItem> items)
	{
		int offset = 0;
		String elementName = "warninglist"; //$NON-NLS-1$
		String startTag = MessageFormat.format("<{0}>", elementName); //$NON-NLS-1$
		String endTag = MessageFormat.format("</{0}>", elementName); //$NON-NLS-1$
		String sourcePath = sourceUri.toString();

		while (offset < report.length())
		{
			int warningListStart = report.indexOf(startTag, offset);
			if (warningListStart == -1)
			{
				break;
			}

			// advances past the start tag
			warningListStart += startTag.length();

			// gets the URI
			int uriStart = report.indexOf("<uri>", warningListStart) + "<uri>".length(); //$NON-NLS-1$ //$NON-NLS-2$
			int uriEnd = report.indexOf("</uri>", uriStart); //$NON-NLS-1$
			String uri = new String(report.substring(uriStart, uriEnd));

			// finds the end of this list
			int warningListEnd = report.indexOf(endTag, warningListStart);

			if (uri != null && URLEncoder.encode(uri, null, null).equals(sourcePath))
			{
				// extracts the warning list
				String listString = new String(report.substring(warningListStart, warningListEnd));
				// finds the warnings
				String[] warnings = getContent(WARNING_PATTERN, listString);
				// adds errors
				addWarnings(warnings, sourceUri, manager, items);
			}

			// advance past the current warning list
			offset = warningListEnd + endTag.length();
		}
	}

	/**
	 * Loads our CSS profile.
	 * 
	 * @throws IOException
	 *             if profile loading fails
	 */
	private void loadAptanaCSSProfile()
	{
		InputStream configStream = getClass().getResourceAsStream(CONFIG_FILE);
		InputStream profilesStream = getClass().getResourceAsStream(PROFILES_CONFIG_FILE);

		try
		{
			// loads our config
			PropertiesLoader.config.load(configStream);

			// loads our profile
			Utf8Properties profiles = new Utf8Properties();
			profiles.load(profilesStream);
			// a hack, but no other way since PropertiesLoader provides no public access to stored profiles
			Field field = PropertiesLoader.class.getDeclaredField("profiles"); //$NON-NLS-1$
			field.setAccessible(true);
			field.set(null, profiles);
		}
		catch (Exception e)
		{
			CSSPlugin.logError(Messages.CSSValidator_ERR_FailToLoadProfile, e);
		}
		finally
		{
			try
			{
				configStream.close();
			}
			catch (IOException e)
			{
			}
			try
			{
				profilesStream.close();
			}
			catch (IOException e)
			{
			}
		}
	}

	/**
	 * Adds the CSS errors.
	 * 
	 * @param errors
	 *            the array of errors
	 * @param sourcePath
	 *            the source path
	 * @param manager
	 *            the validation manager
	 * @param items
	 *            the list that stores the added validation items
	 */
	private static void addErrors(String[] errors, URI sourcePath, IValidationManager manager,
			List<IValidationItem> items)
	{
		Map<String, String> map;
		for (String error : errors)
		{
			map = getProperties(error);

			int lineNumber = Integer.parseInt(map.get("line")); //$NON-NLS-1$
			String message = map.get("message"); //$NON-NLS-1$
			String context = map.get("context"); //$NON-NLS-1$
			String property = map.get("property"); //$NON-NLS-1$
			String skippedstring = map.get("skippedstring"); //$NON-NLS-1$
			String errorsubtype = map.get("errorsubtype"); //$NON-NLS-1$
			if (message == null)
			{
				if (property == null)
				{
					property = context;
				}
				if (skippedstring.equals("[empty string]")) //$NON-NLS-1$
				{
					// alters the text a bit
					skippedstring = "no properties defined"; //$NON-NLS-1$
				}
				message = MessageFormat.format("{0} : {1} for {2}", errorsubtype, skippedstring, property); //$NON-NLS-1$
			}
			message = StringEscapeUtils.unescapeHtml(message);
			message = message.replaceAll("\\s+", " "); //$NON-NLS-1$ //$NON-NLS-2$

			if (!manager.isIgnored(message, ICSSParserConstants.LANGUAGE) && !containsCSS3Property(message))
			{
				// there is no info on the line offset or the length of the errored text
				items.add(manager.addError(message, lineNumber, 0, 0, sourcePath));
			}
		}
	}

	/**
	 * Adds the CSS warnings.
	 * 
	 * @param warnings
	 *            the array of warnings
	 * @param sourcePath
	 *            the source path
	 * @param manager
	 *            the validation manager
	 * @param items
	 *            the list that stores the added validation items
	 */
	private static void addWarnings(String[] warnings, URI sourcePath, IValidationManager manager,
			List<IValidationItem> items)
	{
		Map<String, String> map;
		String last = ""; //$NON-NLS-1$
		for (String warning : warnings)
		{
			map = getProperties(warning);

			int lineNumber = Integer.parseInt(map.get("line")); //$NON-NLS-1$
			String level = map.get("level"); //$NON-NLS-1$
			String message = MessageFormat.format("{0} (level {1})", map.get("message"), level); //$NON-NLS-1$ //$NON-NLS-2$
			String context = map.get("context"); //$NON-NLS-1$

			String hash = MessageFormat.format("{0}:{1}:{2}:{3}", lineNumber, level, message, context); //$NON-NLS-1$
			// guards against duplicate warnings
			if (!last.equals(hash) && !manager.isIgnored(message, ICSSParserConstants.LANGUAGE))
			{
				items.add(manager.addWarning(message, lineNumber, 0, 0, sourcePath));
			}

			last = hash;
		}
	}

	/**
	 * Gets the validation report from the validator.
	 * 
	 * @param source
	 *            the source text
	 * @param path
	 *            the source path
	 * @return the report
	 */
	private static String getReport(String source, URI path)
	{
		StyleSheetParser parser = new StyleSheetParser();
		ApplContext ac = new ApplContext("en"); //$NON-NLS-1$
		ac.setProfile(APTANA_PROFILE);
		try
		{
			parser.parseStyleElement(ac, new ByteArrayInputStream(source.getBytes()), null, null, path.toURL(), 0);
		}
		catch (MalformedURLException e)
		{
			CSSPlugin.logError(MessageFormat.format(Messages.CSSValidator_ERR_InvalidPath, path), e);
		}

		StyleSheet stylesheet = parser.getStyleSheet();
		stylesheet.findConflicts(ac);
		StyleReport report = StyleReportFactory.getStyleReport(ac, "Title", stylesheet, "soap12", 2); //$NON-NLS-1$ //$NON-NLS-2$
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		report.print(new PrintWriter(out));
		return out.toString().replaceAll("m:", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Gets the list of contents in a source text that matches the specific pattern.
	 * 
	 * @param pattern
	 *            the pattern to match
	 * @param source
	 *            the source text
	 * @return the matching contents in an array
	 */
	private static String[] getContent(Pattern pattern, String source)
	{
		Matcher matcher = pattern.matcher(source);
		List<String> result = new ArrayList<String>();
		while (matcher.find())
		{
			result.add(matcher.group(2));
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Gets the properties map from a source text.
	 * 
	 * @param source
	 *            the source text
	 * @return the properties map
	 */
	private static Map<String, String> getProperties(String source)
	{
		Matcher matcher = PROPERTIES_PATTERN.matcher(source);
		Map<String, String> result = new HashMap<String, String>();
		String key, value;
		while (matcher.find())
		{
			key = matcher.group(1);
			value = matcher.group(2);
			if (value != null)
			{
				value = value.trim();
			}
			result.put(key, value);
		}
		return result;
	}

	private static boolean containsCSS3Property(String message)
	{
		for (String property : CSS3_PROPERTIES)
		{
			if (message.indexOf(property) > -1)
			{
				return true;
			}
		}
		return false;
	}
}
