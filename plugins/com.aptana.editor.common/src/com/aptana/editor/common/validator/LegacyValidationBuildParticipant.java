/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.validator;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.osgi.util.NLS;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.Problem;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.IParseError;

/**
 * This hooks up the existing validators to the build/reconcile process through the {@link IBuildParticipant}
 * infrastructure.
 * 
 * @author cwilliams
 */
public class LegacyValidationBuildParticipant extends AbstractBuildParticipant implements IValidationManager
{
	
	private static final Pattern fgFilterExpressionDelimiter = Pattern.compile("####"); //$NON-NLS-1$

	private Document fDocument;
	private BuildContext fContext;

	private List<ValidatorReference> getValidatorRefs(String contentType)
	{
		List<ValidatorReference> result = new ArrayList<ValidatorReference>();

		List<ValidatorReference> validatorRefs = ValidatorLoader.getInstance().getValidators(contentType);
		String list = getPreferenceStore().getString(getSelectedValidatorsPrefKey(contentType));
		if (StringUtil.isEmpty(list))
		{
			// by default uses the first validator that supports the content type
			if (validatorRefs.size() > 0)
			{
				result.add(validatorRefs.get(0));
			}
		}
		else
		{
			String[] selectedValidators = list.split(","); //$NON-NLS-1$
			Set<String> selectedValidatorNames = new HashSet<String>(Arrays.asList(selectedValidators));
			for (ValidatorReference validator : validatorRefs)
			{
				if (selectedValidatorNames.contains(validator.getName()))
				{
					result.add(validator);
					break;
				}
			}

		}
		return result;
	}

	private String getSelectedValidatorsPrefKey(String language)
	{
		return MessageFormat.format("{0}:{1}", language, IPreferenceConstants.SELECTED_VALIDATORS); //$NON-NLS-1$
	}

	private String getFilterExpressionsPrefKey(String language)
	{
		return MessageFormat.format("{0}:{1}", language, IPreferenceConstants.FILTER_EXPRESSIONS); //$NON-NLS-1$
	}

	private String getParseErrorEnabledPrefKey(String language)
	{
		return MessageFormat.format("{0}:{1}", language, IPreferenceConstants.PARSE_ERROR_ENABLED); //$NON-NLS-1$
	}

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		try
		{
			fContext = context;

			List<ValidatorReference> validatorRefs = getValidatorRefs(fContext.getContentType());
			if (CollectionsUtil.isEmpty(validatorRefs))
			{
				return;
			}

			String contents = fContext.getContents();
			URI uri = fContext.getURI();
			fDocument = new Document(contents);

			Map<String, List<IProblem>> allItems = new HashMap<String, List<IProblem>>();
			for (ValidatorReference validatorRef : validatorRefs)
			{
				List<IProblem> newItems = validatorRef.getValidator().validate(contents, uri, this);
				String type = validatorRef.getMarkerType();
				List<IProblem> items = allItems.get(type);
				if (items == null)
				{
					items = Collections.synchronizedList(new ArrayList<IProblem>());
					allItems.put(type, items);
				}
				items.addAll(newItems);

				// FIXME We need to handle nested languages here....
				// for (String nestedLanguage : fNestedLanguages)
				// {
				// processNestedLanguage(nestedLanguage, allItems);
				// }
			}

			// Now stick the generated problems into the context
			for (Map.Entry<String, List<IProblem>> entry : allItems.entrySet())
			{
				fContext.putProblems(entry.getKey(), entry.getValue());
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
		finally
		{
			fDocument = null;
			fContext = null;
		}
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		try
		{
			List<ValidatorReference> validatorRefs = getValidatorRefs(context.getContentType());
			if (!CollectionsUtil.isEmpty(validatorRefs))
			{
				for (ValidatorReference validatorRef : validatorRefs)
				{
					context.removeProblems(validatorRef.getMarkerType());
				}
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
	}

	public IProblem createError(String message, int lineNumber, int lineOffset, int length, URI sourcePath)
	{
		return createProblem(IMarker.SEVERITY_ERROR, message, lineNumber, lineOffset, length, sourcePath);
	}

	public IProblem createWarning(String message, int lineNumber, int lineOffset, int length, URI sourcePath)
	{
		return createProblem(IMarker.SEVERITY_WARNING, message, lineNumber, lineOffset, length, sourcePath);
	}

	private IProblem createProblem(int severity, String message, int lineNumber, int lineOffset, int length,
			URI sourcePath)
	{
		int charLineOffset = 0;
		if (fDocument != null)
		{
			try
			{
				charLineOffset = fDocument.getLineOffset(lineNumber - 1);
			}
			catch (BadLocationException e)
			{
				IdeLog.logError(CommonEditorPlugin.getDefault(),
						MessageFormat.format("Failed to determine offset of line {0}", lineNumber), e); //$NON-NLS-1$
			}
		}
		int offset = charLineOffset + lineOffset;
		return new Problem(severity, message, offset, length, lineNumber, sourcePath.toString());
	}

	public void addNestedLanguage(String language)
	{
		// no-op, we don't handle nested languages properly.
	}

	public boolean isIgnored(String message, String language)
	{
		String list = getPreferenceStore().getString(getFilterExpressionsPrefKey(language));
		if (StringUtil.isEmpty(list))
		{
			return false;
		}
		
		
		String[] expressions = fgFilterExpressionDelimiter.split(list);
		for (String expression : expressions)
		{
			if (message.matches(expression))
			{
				return true;
			}
		}
		return false;
	}

	public void addParseErrors(List<IProblem> items, String language)
	{
		if (fContext == null || fDocument == null || !getPreferenceStore().getBoolean(getParseErrorEnabledPrefKey(language)))
		{
			return;
		}

		for (IParseError parseError : fContext.getParseErrors())
		{
			try
			{
				int severity = (parseError.getSeverity() == IParseError.Severity.ERROR) ? IMarker.SEVERITY_ERROR
						: IMarker.SEVERITY_WARNING;
				items.add(createProblem(severity, parseError.getMessage(),
						fDocument.getLineOfOffset(parseError.getOffset()) + 1, parseError.getOffset(), 0,
						fContext.getURI()));
			}
			catch (BadLocationException e)
			{
				IdeLog.logError(CommonEditorPlugin.getDefault(),
						NLS.bind("Error finding line on given offset : {0}", parseError.getOffset() + 1), e); //$NON-NLS-1$
			}
		}
	}

	protected IPreferenceStore getPreferenceStore()
	{
		return CommonEditorPlugin.getDefault().getPreferenceStore();
	}

}
