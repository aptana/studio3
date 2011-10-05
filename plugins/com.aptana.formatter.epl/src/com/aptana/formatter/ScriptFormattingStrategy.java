/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package com.aptana.formatter;

import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

import com.aptana.core.logging.IdeLog;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.ui.FormatterException;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.FormatterSyntaxProblemException;
import com.aptana.formatter.ui.ScriptFormattingContextProperties;
import com.aptana.ui.util.StatusLineMessageTimerManager;

/**
 * Formatting strategy for a source code.
 * 
 * @since 3.0
 */
@SuppressWarnings("restriction")
public class ScriptFormattingStrategy extends ContextBasedFormattingStrategy
{

	private final String contentType;

	private static class FormatJob
	{
		final IDocument document;
		final TypedPosition partition;
		final IProject project;
		final String formatterId;
		final boolean isSlave;
		final boolean isSelection;
		IRegion selectedRegion;
		final IFormattingContext context;
		final boolean canConsumeIndentation;

		/**
		 * @param context
		 * @param document
		 * @param partition
		 * @param project
		 * @param formatterId
		 * @param isSlave
		 * @param canConsumeIndentation
		 * @param isSelection
		 * @param selectedRegion
		 *            - Should be valid when isSelection is true.
		 */
		public FormatJob(IFormattingContext context, IDocument document, TypedPosition partition, IProject project,
				String formatterId, Boolean isSlave, Boolean canConsumeIndentation, Boolean isSelection,
				IRegion selectedRegion)
		{
			this.context = context;
			this.document = document;
			this.partition = partition;
			this.project = project;
			this.formatterId = formatterId;
			this.canConsumeIndentation = canConsumeIndentation;
			this.isSlave = (isSlave != null) ? isSlave : false;
			this.isSelection = (isSelection != null) ? isSelection : false;
			this.selectedRegion = selectedRegion;
		}

	}

	/** Jobs to be formatted by this strategy */
	private final LinkedList<FormatJob> fJobs = new LinkedList<FormatJob>();

	/**
	 * Creates a new script formatting strategy.
	 */
	public ScriptFormattingStrategy(String contentType)
	{
		this.contentType = contentType;
	}

	/*
	 * @see ContextBasedFormattingStrategy#format()
	 */
	@Override
	public void format()
	{
		super.format();
		final FormatJob job = fJobs.removeFirst();
		BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), new Runnable()
		{
			public void run()
			{
				doFormat(job);
			}
		});
	}

	/**
	 * @since 2.0
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doFormat(final FormatJob job)
	{
		final IDocument document = job.document;
		final TypedPosition partition = job.partition;
		final IRegion region = job.selectedRegion;
		if (document != null && partition != null)
		{
			Map partitioners = null;
			try
			{
				int offset = 0;
				int length = 0;
				if (job.isSelection && !job.isSlave)
				{
					offset = region.getOffset();
					length = region.getLength();
				}
				else
				{
					offset = partition.getOffset();
					length = partition.getLength();
				}

				final IScriptFormatterFactory formatterFactory = selectFormatterFactory(job);
				if (formatterFactory != null)
				{
					final String lineDelimiter = TextUtilities.getDefaultLineDelimiter(document);
					final Map prefs = getPreferences();
					final IScriptFormatter formatter = formatterFactory.createFormatter(lineDelimiter, prefs);
					if (formatter instanceof IScriptFormatterExtension)
					{
						((IScriptFormatterExtension) formatter).initialize(job.project);
					}
					formatter.setIsSlave(job.isSlave);
					final int indentationLevel = (offset != 0) ? formatter.detectIndentationLevel(document, offset,
							job.isSelection, job.context) : 0;
					StringBuilder consumedIndent = new StringBuilder();
					if (job.isSlave)
					{
						for (; length + offset > offset; length--)
						{
							char c = document.getChar(offset + length - 1);
							if (c == ' ' || c == '\t')
							{
								// in case the formatter job can consume the existing indent
								// we save the string that may get consumed.
								if (job.canConsumeIndentation)
								{
									consumedIndent.append(c);
								}
								continue;
							}
							break;
						}
					}
					// revert the length to what we had before we collected the indent suffix that
					// can be consumed.
					if (job.canConsumeIndentation)
					{
						length += consumedIndent.length();
					}
					final TextEdit edit = formatter.format(document.get(), offset, length, indentationLevel,
							job.isSelection, job.context, consumedIndent.reverse().toString());
					if (edit != null)
					{
						if (edit.getChildrenSize() > 20)
						{
							partitioners = TextUtilities.removeDocumentPartitioners(document);
						}
						edit.apply(document);
						if (job.isSelection)
						{
							IRegion updatedRegion = edit.getRegion();
							if (edit.getLength() > 0)
							{
								job.context.setProperty(FormattingContextProperties.CONTEXT_REGION, updatedRegion);
								job.selectedRegion = updatedRegion;
							}
						}
					}
				}
			}
			catch (FormatterSyntaxProblemException e)
			{
				final IWorkbench workbench = PlatformUI.getWorkbench();
				WorkbenchWindow window = (WorkbenchWindow) workbench.getActiveWorkbenchWindow();
				if (window != null && window.getStatusLineManager() != null)
				{
					window.getStatusLineManager()
							.setErrorMessage(
									NLS.bind(
											FormatterMessages.ScriptFormattingStrategy_unableToFormatSourceContainingSyntaxError,
											e.getMessage()));
				}
				workbench.getDisplay().beep();
			}
			catch (MalformedTreeException e)
			{
				IdeLog.logWarning(FormatterPlugin.getDefault(),
						FormatterMessages.ScriptFormattingStrategy_formattingError, e, IDebugScopes.DEBUG);
			}
			catch (BadLocationException e)
			{
				// Can only happen on concurrent document modification
				IdeLog.logWarning(FormatterPlugin.getDefault(),
						FormatterMessages.ScriptFormattingStrategy_formattingError, e, IDebugScopes.DEBUG);
			}
			catch (FormatterException fe)
			{
				StatusLineMessageTimerManager.setErrorMessage(fe.getMessage(), 3000L, true);
			}
			catch (Exception e)
			{
				String msg = NLS
						.bind(FormatterMessages.ScriptFormattingStrategy_unexpectedFormatterError, e.toString());
				IdeLog.logError(FormatterPlugin.getDefault(), msg, e, IDebugScopes.DEBUG);
			}
			finally
			{
				if (partitioners != null)
					TextUtilities.addDocumentPartitioners(document, partitioners);
			}
		}
	}

	protected IScriptFormatterFactory selectFormatterFactory(FormatJob job)
	{
		IScriptFormatterFactory factory = (IScriptFormatterFactory) ScriptFormatterManager.getInstance()
				.getContributionById(job.formatterId);
		if (factory != null)
		{
			return factory;
		}
		return ScriptFormatterManager.getSelected(contentType);
	}

	@Override
	public void formatterStarts(final IFormattingContext context)
	{
		super.formatterStarts(context);
		final IDocument document = (IDocument) context.getProperty(FormattingContextProperties.CONTEXT_MEDIUM);
		final TypedPosition partition = (TypedPosition) context
				.getProperty(FormattingContextProperties.CONTEXT_PARTITION);
		final IProject project = (IProject) context.getProperty(ScriptFormattingContextProperties.CONTEXT_PROJECT);
		final String formatterId = (String) context.getProperty(ScriptFormattingContextProperties.CONTEXT_FORMATTER_ID);
		final Boolean isSlave = (Boolean) context
				.getProperty(ScriptFormattingContextProperties.CONTEXT_FORMATTER_IS_SLAVE);
		final Boolean canConsumeIndentation = isSlave != null
				&& isSlave
				&& (Boolean) context
						.getProperty(ScriptFormattingContextProperties.CONTEXT_FORMATTER_CAN_CONSUME_INDENTATION);
		final Boolean isSelection = !(Boolean) context.getProperty(FormattingContextProperties.CONTEXT_DOCUMENT);
		IRegion selectionRegion = null;
		if (isSelection != null && isSelection)
		{
			selectionRegion = (IRegion) context.getProperty(FormattingContextProperties.CONTEXT_REGION);
		}
		fJobs.addLast(new FormatJob(context, document, partition, project, formatterId, isSlave, canConsumeIndentation,
				isSelection, selectionRegion));
	}

	@Override
	public void formatterStops()
	{
		super.formatterStops();
		fJobs.clear();
	}
}
