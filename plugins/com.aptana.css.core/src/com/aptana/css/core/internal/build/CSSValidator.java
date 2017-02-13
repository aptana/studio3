/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.build;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.w3c.css.css.StyleSheet;
import org.w3c.css.css.StyleSheetParser;
import org.w3c.css.parser.CssError;
import org.w3c.css.parser.CssErrorToken;
import org.w3c.css.parser.Errors;
import org.w3c.css.properties.PropertiesLoader;
import org.w3c.css.util.ApplContext;
import org.w3c.css.util.Utf8Properties;
import org.w3c.css.util.Warning;
import org.w3c.css.util.Warnings;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.css.core.CSSCorePlugin;
import com.aptana.css.core.ICSSConstants;
import com.aptana.index.core.build.BuildContext;

/**
 * @author cwilliams
 */
public class CSSValidator extends AbstractBuildParticipant
{

	public static final String ID = "com.aptana.css.core.W3CValidator"; //$NON-NLS-1$

	private static final String APTANA_PROFILE = "AptanaProfile"; //$NON-NLS-1$
	private static final String CONFIG_FILE = "AptanaCSSConfig.properties"; //$NON-NLS-1$
	private static final String PROFILES_CONFIG_FILE = "AptanaCSSProfiles.properties"; //$NON-NLS-1$

	/**
	 * properties pattern
	 */
	private static final Pattern PROPERTIES_PATTERN = Pattern.compile("<([-A-Za-z0-9_:]+)>(.*?)</\\1>", //$NON-NLS-1$
			Pattern.MULTILINE | Pattern.DOTALL);

	// CSS3 properties that the validator doesn't recognize yet and need to be ignored
	@SuppressWarnings("nls")
	private static final String[] CSS3_PROPERTIES = { "behavior", "box-shadow", "box-sizing", "column-count",
			"column-width", "column-gap", "column-rule", "border-radius", "background-clip", "background-origin",
			"background-quantity", "background-size", "border-top-right-radius", "border-bottom-right-radius",
			"border-bottom-left-radius", "border-top-left-radius", "font-family", "font-weight", "font-style",
			"outline-offset", "resize", "size", "src", "transform", "transition", "user-select", "word-break" };

	@SuppressWarnings("nls")
	private static final String[] CSS3_AT_RULES = { "@namespace" };

	// other messages that should be filtered automatically
	@SuppressWarnings("nls")
	private static final String[] FILTERED_MESSAGES = { "unrecognized media only", "linear-gradient" };

	@SuppressWarnings("nls")
	public static final String[] DEFAULT_FILTERS = new String[] { ".*Unknown pseudo-element.*",
			"Property\\s*[-_].*doesn't exist.*", ".*-moz-.*", ".*-o-*", ".*opacity.*", ".*overflow-.*",
			".*accelerator.*", ".*background-position-.*", ".*filter.*", ".*ime-mode.*", ".*layout-.*",
			".*line-break.*", ".*page.*", ".*ruby-.*", ".*scrollbar-.*", ".*text-align-.*", ".*text-justify.*",
			".*text-overflow.*", ".*text-shadow.*", ".*text-underline-position.*", ".*word-spacing.*", ".*word-wrap.*",
			".*writing-mode.*", ".*zoom.*", ".*Parse Error.*", ".*-webkit-.*", ".*rgba.*is not a .* value.*",
			".*Too many values or values are not recognized.*", Pattern.quote(
					"Value Error : background (http://www.w3.org/TR/REC-CSS2/colors.html#propdef-background) , is an incorrect operator :") };

	static
	{
		loadAptanaCSSProfile();
	}

	private void processErrorsInReport(StyleSheet sheet, String sourcePath, List<IProblem> items, List<String> filters)
	{
		Errors errors = sheet.getErrors();
		CssError[] cssErrors = errors.getErrors();
		for (CssError cssError : cssErrors)
		{
			Throwable t = cssError.getException();
			String message = (t == null) ? "" : t.getMessage(); //$NON-NLS-1$
			if (cssError instanceof CssErrorToken)
			{
				CssErrorToken cet = (CssErrorToken) cssError;
				message = cet.getErrorDescription();
			}
			if (message == null) {
				System.out.println("Null message!");
			}
			int lineNumber = cssError.getLine();
			if (!isIgnored(message, filters) && !containsCSS3Property(message) && !containsCSS3AtRule(message)
					&& !isFiltered(message))
			{
				items.add(createError(message, lineNumber, 0, 0, sourcePath));
			}
		}
	}

	private void processWarningsInReport(StyleSheet sheet, String sourcePath, List<IProblem> items,
			List<String> filters)
	{
		Warnings warnings = sheet.getWarnings();
		Warning[] warning = warnings.getWarnings();
		for (Warning w : warning)
		{
			String message = w.getWarningMessage();
			int lineNumber = w.getLine();
			if (!isIgnored(message, filters) && !containsCSS3Property(message) && !containsCSS3AtRule(message)
					&& !isFiltered(message))
			{
				items.add(createError(message, lineNumber, 0, 0, sourcePath));
			}
		}
	}

	/**
	 * Loads our CSS profile.
	 * 
	 * @throws IOException
	 *             if profile loading fails
	 */
	private static void loadAptanaCSSProfile()
	{
		InputStream configStream = CSSValidator.class.getResourceAsStream(CONFIG_FILE);
		InputStream profilesStream = CSSValidator.class.getResourceAsStream(PROFILES_CONFIG_FILE);

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
			IdeLog.logError(CSSCorePlugin.getDefault(), Messages.CSSValidator_ERR_FailToLoadProfile, e);
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
	private void addErrors(String[] errors, String sourcePath, List<IProblem> items, List<String> filters)
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

			// Don't attempt to add errors if there are already errors on this line
			if (hasErrorOrWarningOnLine(items, lineNumber))
			{
				continue;
			}

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
			message = StringEscapeUtils.unescapeHtml4(message);
			message = message.replaceAll("\\s+", " "); //$NON-NLS-1$ //$NON-NLS-2$

			if (!isIgnored(message, filters) && !containsCSS3Property(message) && !containsCSS3AtRule(message)
					&& !isFiltered(message))
			{
				// there is no info on the line offset or the length of the errored text
				items.add(createError(message, lineNumber, 0, 0, sourcePath));
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
	 * @param items
	 *            the list that stores the added validation items
	 * @param filters
	 */
	private void addWarnings(String[] warnings, String sourcePath, List<IProblem> items, List<String> filters)
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

			// Don't attempt to add warnings if there are already errors on this line
			if (hasErrorOrWarningOnLine(items, lineNumber))
			{
				continue;
			}

			String hash = MessageFormat.format("{0}:{1}:{2}:{3}", lineNumber, level, message, context); //$NON-NLS-1$
			// guards against duplicate warnings
			if (!last.equals(hash) && !isIgnored(message, filters))
			{
				items.add(createWarning(message, lineNumber, 0, 0, sourcePath));
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
	private static StyleSheet getReport(String source, URI path)
	{
		StyleSheetParser parser = new StyleSheetParser();
		ApplContext ac = new ApplContext("en"); //$NON-NLS-1$
		ac.setProfile(APTANA_PROFILE);
		try
		{
			parser.parseStyleElement(ac, new ByteArrayInputStream(source.getBytes(IOUtil.UTF_8)), null, null,
					path.toURL(), 0);
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(CSSCorePlugin.getDefault(),
					MessageFormat.format(Messages.CSSValidator_ERR_InvalidPath, path), e);
		}
		catch (UnsupportedEncodingException e)
		{
			IdeLog.logError(CSSCorePlugin.getDefault(), e);
		}

		StyleSheet stylesheet = parser.getStyleSheet();
		stylesheet.findConflicts(ac);
		return stylesheet;
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
			if (message.indexOf("Property " + property) > -1) //$NON-NLS-1$
			{
				return true;
			}
		}
		return false;
	}

	private static boolean containsCSS3AtRule(String message)
	{
		for (String rule : CSS3_AT_RULES)
		{
			if (message.indexOf(MessageFormat.format("the at-rule {0} is not implemented", rule)) > -1) //$NON-NLS-1$
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isFiltered(String message)
	{
		for (String filtered : FILTERED_MESSAGES)
		{
			if (message.indexOf(filtered) > -1)
			{
				return true;
			}
		}
		return false;
	}

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		List<IProblem> problems = new ArrayList<IProblem>();

		String source = context.getContents();
		URI uri = context.getURI();
		String path = uri.toString();

		StyleSheet sheet = getReport(source, uri);
		List<String> filters = getFilters();
		processErrorsInReport(sheet, path, problems, filters);
		processWarningsInReport(sheet, path, problems, filters);

		context.putProblems(ICSSConstants.W3C_PROBLEM, problems);
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		context.removeProblems(ICSSConstants.W3C_PROBLEM);
	}
}
