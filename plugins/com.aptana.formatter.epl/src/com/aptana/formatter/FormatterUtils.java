/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.compare.contentmergeviewer.TokenComparator;
import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.compare.rangedifferencer.RangeDifferencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.core.logging.IdeLog;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.nodes.IFormatterNode;
import com.aptana.formatter.nodes.IFormatterTextNode;

@SuppressWarnings("restriction")
public class FormatterUtils
{
	public static boolean isSpace(char c)
	{
		return c == '\t' || c == ' ';
	}

	public static boolean isLineSeparator(char c)
	{
		return c == '\r' || c == '\n';
	}

	public static boolean isNewLine(IFormatterNode node)
	{
		if (node instanceof IFormatterTextNode)
		{
			final IFormatterTextNode textNode = (IFormatterTextNode) node;
			final IFormatterDocument document = node.getDocument();
			int start = textNode.getStartOffset();
			if (start < textNode.getEndOffset())
			{
				if (document.charAt(start) == '\n')
				{
					++start;
				}
				else if (document.charAt(start) == '\r')
				{
					++start;
					if (start < textNode.getEndOffset() && document.charAt(start) == '\n')
					{
						++start;
					}
				}
				else
				{
					return false;
				}
			}
			while (start < textNode.getEndOffset())
			{
				if (!isSpace(document.charAt(start)))
				{
					return false;
				}
				++start;
			}
			return true;
		}
		return false;
	}

	/**
	 * @param node
	 * @return
	 */
	public static boolean isEmptyText(IFormatterNode node)
	{
		if (node instanceof IFormatterTextNode)
		{
			final String text = ((IFormatterTextNode) node).getText();
			for (int i = 0; i < text.length(); ++i)
			{
				char c = text.charAt(i);
				if (!Character.isWhitespace(c))
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @since 2.0
	 */
	public static IFormatterNode[] toTextNodeArray(List<IFormatterNode> list)
	{
		if (list != null)
		{
			return list.toArray(new IFormatterNode[list.size()]);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Returns the editor's tab width as set in the given editor's-specific preferences. In case a value is not found,
	 * or the preferences are null, the workspace settings for the editor's tab-width will be returned.
	 * 
	 * @param preferenceStore
	 *            The editor's preferences store; Null, in case a workspace setting is needed.
	 * @return The editor's tab-width
	 * @deprecated Use EditorUtil.getSpaceIndentSize(preferencesQualifier)
	 */
	public static int getEditorTabWidth(IPreferenceStore preferenceStore)
	{
		IPreferenceStore prefs = getChainedPreferences(preferenceStore, EditorsPlugin.getDefault().getPreferenceStore());
		return prefs.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
	}

	/**
	 * Returns the editor's setting for 'Insert Spaces for Tabs'. This setting exists in the general 'Text Editors'
	 * preference page.
	 * 
	 * @param preferenceStore
	 *            The editor's preferences store; Null, in case a workspace setting is needed.
	 * @return The value for the generic editor's 'Insert Spaces for Tabs'
	 */
	public static boolean isInsertSpacesForTabs(IPreferenceStore preferenceStore)
	{
		IPreferenceStore prefs = getChainedPreferences(preferenceStore, EditorsPlugin.getDefault().getPreferenceStore());
		return prefs.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
	}

	/**
	 * Generate OFF-ON regions for the given map of comments, using the on-pattern and the off-pattern.<br>
	 * The method will traverse the comments map and return a {@link java.util.Set} of valid regions that should be
	 * skipped in case the Off/On was enabled.
	 * 
	 * @param commentsMap
	 *            A {@link LinkedHashMap} of comments, ordered by their appearance in the document. The map key is the
	 *            start offset of the comment-string that it's mapping to.
	 * @param onPattern
	 *            The 'ON' pattern (as defined in the formatter preferences).
	 * @param offPattern
	 *            The 'OFF' pattern (as defined in the formatter preferences).
	 * @param documentEndOffset
	 *            The end offset of the document.
	 * @return A {@link List} of {@link IRegion} instances.
	 */
	public static List<IRegion> resolveOnOffRegions(LinkedHashMap<Integer, String> commentsMap, Pattern onPattern,
			Pattern offPattern, int documentEndOffset)
	{
		List<IRegion> regions = new ArrayList<IRegion>();
		boolean isOn = true;
		int start = -1;
		int end = -1;
		int startCommentBeginOffset = -1;
		for (Integer offset : commentsMap.keySet())
		{
			String comment = commentsMap.get(offset);
			if (isOn)
			{
				// Look for an 'OFF' pattern
				Matcher matcher = offPattern.matcher(comment);
				if (matcher.find())
				{
					start = matcher.start();
					startCommentBeginOffset = offset;
					isOn = false;
				}
			}
			else
			{
				// Look for an 'ON' pattern
				Matcher matcher = onPattern.matcher(comment);
				if (matcher.find())
				{
					end = matcher.end();
					int regionStart = startCommentBeginOffset + start;
					regions.add(new Region(regionStart, offset + end - regionStart));
					// reset the values
					isOn = true;
					start = -1;
					end = -1;
					startCommentBeginOffset = -1;
				}
			}
		}
		if (start > -1 && end < 0)
		{
			// We need to add a region that will go all the way to the end of the document
			start = startCommentBeginOffset + start;
			regions.add(new Region(start, documentEndOffset - start));
		}
		return regions;
	}

	/**
	 * Replace the output OFF/ON formatting regions with the original content from the input.
	 * 
	 * @param input
	 * @param output
	 * @param inputOffOnRegions
	 *            A non null list of OFF/ON regions that were found on the input content.
	 * @param outputOffOnRegions
	 *            A list of OFF/ON regions that were found on the output content. This list may be null in case of an
	 *            error.
	 * @return A new output string that contains the original regions content from the input string between the Off and
	 *         On formatter tags.
	 * @throws CoreException
	 *             In case the given outputOnOffRegions was null, or in case the size of the output-regions does not
	 *             match the size of the input-regions.
	 */
	public static String applyOffOnRegions(String input, String output, List<IRegion> inputOffOnRegions,
			List<IRegion> outputOffOnRegions) throws CoreException
	{
		// Validate the inputs...
		if (outputOffOnRegions == null || inputOffOnRegions.size() != outputOffOnRegions.size())
		{
			IdeLog.logError(FormatterPlugin.getDefault(), outputOffOnRegions == null ? "Output OFF/ON regions was null" //$NON-NLS-1$
					: "Output OFF/ON regions do not match in size to the input regions", IDebugScopes.DEBUG); //$NON-NLS-1$
			throw new CoreException(new Status(IStatus.ERROR, FormatterPlugin.PLUGIN_ID,
					"Error applying the formatter ON-OFF regions")); //$NON-NLS-1$
		}
		StringBuilder outputBuffer = new StringBuilder(output);
		for (int i = inputOffOnRegions.size() - 1; i >= 0; i--)
		{
			IRegion inputRegion = inputOffOnRegions.get(i);
			IRegion outputRegion = outputOffOnRegions.get(i);
			String originalString = input.substring(inputRegion.getOffset(),
					inputRegion.getOffset() + inputRegion.getLength());
			outputBuffer.replace(outputRegion.getOffset(), outputRegion.getOffset() + outputRegion.getLength(),
					originalString);
		}
		return outputBuffer.toString();
	}

	/**
	 * Compute and return an array of {@link RangeDifference}s for two given strings.
	 * 
	 * @param left
	 * @param right
	 * @return An array of {@link RangeDifference}s.
	 */
	public static RangeDifference[] getDiff(String left, String right)
	{
		IRangeComparator leftRangeComparator = new TokenComparator(left);
		IRangeComparator rightRangeComparator = new TokenComparator(right);
		return RangeDifferencer.findRanges(leftRangeComparator, rightRangeComparator);
	}

	/**
	 * A utility function to log the first difference area that two strings have.<br>
	 * The function scans char-by-char, and on the first difference it try to log 10 chars before and 40 chars after the
	 * diff.
	 * 
	 * @param input
	 *            - Expected content
	 * @param output
	 *            - Matched content
	 */
	public static void logDiff(String input, String output)
	{
		// find the first offset that has a change and log it.
		int length = Math.min(input.length(), output.length());
		int offset = 0;
		for (; offset < length; offset++)
		{
			if (input.charAt(offset) != output.charAt(offset))
			{
				// Found a change
				break;
			}
		}
		// log 10 characters back and 40 ahead
		StringBuilder message = new StringBuilder(100);
		message.append("Formatted content differ around position "); //$NON-NLS-1$
		message.append(offset);
		message.append("\nINPUT:\n"); //$NON-NLS-1$
		int start = Math.max(0, offset - 10);
		int end = Math.min(offset + 40, input.length());
		message.append(input.substring(start, end));
		message.append("\nOUTPUT:\n"); //$NON-NLS-1$
		start = Math.max(0, offset - 10);
		end = Math.min(offset + 40, output.length());
		message.append(output.substring(start, end));
		IdeLog.logError(FormatterPlugin.getDefault(), message.toString());
	}

	/**
	 * @param optionalStore
	 *            - An optional preference store that may contain a searched key (can be null)
	 * @param defaultStore
	 *            - A non-null preference store that will be used in case the optional store is null or does not contain
	 *            the searched key
	 * @return A chained preference store for the given preferences stores. In case the optional store was null, the
	 *         given default store is returned.
	 */
	private static IPreferenceStore getChainedPreferences(IPreferenceStore optionalStore, IPreferenceStore defaultStore)
	{
		IPreferenceStore prefs;
		if (optionalStore == null)
		{
			prefs = defaultStore;
		}
		else
		{
			IPreferenceStore[] preferenceStores = new IPreferenceStore[] { optionalStore, defaultStore };
			prefs = new ChainedPreferenceStore(preferenceStores);
		}
		return prefs;
	}
}
