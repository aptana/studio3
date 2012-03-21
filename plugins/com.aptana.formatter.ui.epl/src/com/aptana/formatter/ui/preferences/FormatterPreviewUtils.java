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
package com.aptana.formatter.ui.preferences;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.formatter.IDebugScopes;
import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ui.CodeFormatterConstants;
import com.aptana.formatter.ui.FormatterException;
import com.aptana.formatter.ui.FormatterSyntaxProblemException;
import com.aptana.formatter.ui.epl.FormatterUIEplPlugin;
import com.aptana.formatter.ui.util.Util;

public class FormatterPreviewUtils
{
	// for substituting strings like {0}, {1}, etc.
	private static final Pattern SUBSTITUTION_PATTERN = Pattern.compile("\\{\\s*\\d+\\s*\\}"); //$NON-NLS-1$
	private static final String LINE_SEPARATOR = "\n"; //$NON-NLS-1$
	private static final String ENCODING = "ISO-8859-1"; //$NON-NLS-1$

	public static void updatePreview(ISourceViewer viewer, URL previewContent, String[] substitutions,
			IScriptFormatterFactory factory, Map<String, String> preferences)
	{
		String content = null;
		if (previewContent != null)
		{
			try
			{
				final String c = new String(Util.getInputStreamAsCharArray(previewContent.openConnection()
						.getInputStream(), -1, ENCODING));
				content = Util.concatenate(Util.splitLines(c), LINE_SEPARATOR);
				if (content != null && substitutions != null && substitutions.length > 0)
				{
					content = substitute(content, substitutions);
				}
			}
			catch (IOException e)
			{
				IdeLog.logError(FormatterUIEplPlugin.getDefault(), e, IDebugScopes.DEBUG);
				disablePreview(viewer);
				return;
			}
		}

		updatePreview(viewer, content, substitutions, factory, preferences);
	}

	public static void updatePreview(ISourceViewer viewer, String previewContent, String[] substitutions,
			IScriptFormatterFactory factory, Map<String, String> preferences)
	{
		if (previewContent != null)
		{
			String content = substitute(previewContent, substitutions);
			IScriptFormatter formatter = factory.createFormatter(LINE_SEPARATOR, preferences);
			int tabSize = formatter.getTabSize();
			String indentType = formatter.getIndentType();
			if (CodeFormatterConstants.EDITOR.equals(indentType))
			{
				tabSize = formatter.getEditorSpecificTabWidth();
			}
			if (tabSize != viewer.getTextWidget().getTabs())
			{
				viewer.getTextWidget().setTabs(tabSize);
			}
			viewer.getTextWidget().setEnabled(true);
			try
			{
				TextEdit textEdit = formatter.format(content, 0, content.length(), 0, false, null, ""); //$NON-NLS-1$
				if (textEdit != null)
				{
					IDocument document = new Document(content);
					textEdit.apply(document);
					viewer.getDocument().set(document.get());
					return;
				}
			}
			catch (BadLocationException e)
			{
				IdeLog.logError(FormatterUIEplPlugin.getDefault(), e, IDebugScopes.DEBUG);
			}
			catch (MalformedTreeException e)
			{
				IdeLog.logError(FormatterUIEplPlugin.getDefault(), e, IDebugScopes.DEBUG);
			}
			catch (FormatterSyntaxProblemException e)
			{
				// skip
			}
			catch (FormatterException e)
			{
				IdeLog.logError(FormatterUIEplPlugin.getDefault(), e, IDebugScopes.DEBUG);
			}
			// TODO indicate error/warning state
			viewer.getDocument().set(content);
		}
		else
		{
			disablePreview(viewer);
		}
	}

	private static void disablePreview(ISourceViewer viewer)
	{
		viewer.getTextWidget().setEnabled(false);
		viewer.getDocument().set(StringUtil.EMPTY);
	}

	/**
	 * Do a content substitution by looking at the array size and looking for {0}...{n} strings and replace them with
	 * the array's content.<br>
	 * (Note - we use this method and not the NLS.bind() because it does not handle well code blocks existence)
	 * 
	 * @param content
	 * @param substitutions
	 * @return A string, substituted with the array's content.
	 */
	private static String substitute(String content, String[] substitutions)
	{
		StringBuilder buffer = new StringBuilder(content);
		Matcher matcher = SUBSTITUTION_PATTERN.matcher(content);
		int offset = 0;
		while (matcher.find())
		{
			MatchResult matchResult = matcher.toMatchResult();
			int beginIndex = matchResult.start();
			int endIndex = matchResult.end();
			int index = Integer.parseInt(content.substring(beginIndex + 1, endIndex - 1));
			if (index >= 0 && index < substitutions.length)
			{
				String replacement = substitutions[index];
				int matchLength = endIndex - beginIndex;
				buffer.replace(offset + beginIndex, offset + endIndex, replacement);
				offset += (replacement.length() - matchLength);
			}
		}
		return buffer.toString();
	}
}
