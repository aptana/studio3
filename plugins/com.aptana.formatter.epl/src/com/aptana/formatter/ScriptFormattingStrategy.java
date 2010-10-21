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

		/**
		 * @param document
		 * @param partition
		 * @param project
		 * @param isSlave
		 */
		public FormatJob(IDocument document, TypedPosition partition, IProject project, String formatterId,
				Boolean isSlave)
		{
			this.document = document;
			this.partition = partition;
			this.project = project;
			this.formatterId = formatterId;
			this.isSlave = (isSlave != null) ? isSlave : false;
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
	@SuppressWarnings( { "unchecked", "rawtypes" })
	protected void doFormat(final FormatJob job)
	{
		final IDocument document = job.document;
		final TypedPosition partition = job.partition;

		if (document != null && partition != null)
		{
			Map partitioners = null;
			try
			{
				int offset = partition.getOffset();

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
					final int indentationLevel = (offset != 0) ? formatter.detectIndentationLevel(document, offset) : 0;
					int length = partition.getLength();
					if (job.isSlave)
					{
						for (; length + offset > partition.offset; length--)
						{
							char c = document.getChar(offset + length - 1);
							if (c == ' ' || c == '\t')
							{
								continue;
							}
							break;
						}
					}
					final TextEdit edit = formatter.format(document.get(), offset, length, indentationLevel);
					if (edit != null)
					{
						if (edit.getChildrenSize() > 20)
						{
							partitioners = TextUtilities.removeDocumentPartitioners(document);
						}
						edit.apply(document);
					}
				}
			}
			catch (FormatterSyntaxProblemException e)
			{
				final IWorkbench workbench = PlatformUI.getWorkbench();
				WorkbenchWindow window = (WorkbenchWindow) workbench.getActiveWorkbenchWindow();
				if (window != null && window.getStatusLineManager() != null)
				{
					window
							.getStatusLineManager()
							.setErrorMessage(
									NLS
											.bind(
													FormatterMessages.ScriptFormattingStrategy_unableToFormatSourceContainingSyntaxError,
													e.getMessage()));
				}
				workbench.getDisplay().beep();
			}
			catch (MalformedTreeException e)
			{
				FormatterPlugin.warn(FormatterMessages.ScriptFormattingStrategy_formattingError, e);
			}
			catch (BadLocationException e)
			{
				// Can only happen on concurrent document modification
				FormatterPlugin.warn(FormatterMessages.ScriptFormattingStrategy_formattingError, e);
			}
			catch (FormatterException fe)
			{
				StatusLineMessageTimerManager.setErrorMessage(fe.getMessage(), 3000L, true);
			}
			catch (Exception e)
			{
				final String msg = NLS.bind(FormatterMessages.ScriptFormattingStrategy_unexpectedFormatterError, e
						.toString());
				FormatterPlugin.logError(msg, e);
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
		fJobs.addLast(new FormatJob(document, partition, project, formatterId, isSlave));
	}

	@Override
	public void formatterStops()
	{
		super.formatterStops();
		fJobs.clear();
	}
}
