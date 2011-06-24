/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.editor.common.preferences.IPreferenceConstants;

/**
 * CommonOccurrenceUpdater
 */
public class CommonOccurrencesUpdater implements IPropertyChangeListener
{
	private AbstractThemeableEditor editor;
	private ISelectionListener selectionListener;
	private Annotation[] annotations;

	/**
	 * CommonOccurrencesUpdater
	 * 
	 * @param editor
	 */
	public CommonOccurrencesUpdater(AbstractThemeableEditor editor)
	{
		this.editor = editor;
	}

	/**
	 * getAnnotationModel
	 * 
	 * @return
	 */
	protected IAnnotationModel getAnnotationModel()
	{
		IDocumentProvider documentProvider = getDocumentProvider();
		IEditorInput editorInput = getEditorInput();
		IAnnotationModel result = null;

		if (documentProvider != null && editorInput != null)
		{
			result = documentProvider.getAnnotationModel(editorInput);
		}

		return result;
	}

	/**
	 * getDocumentProvider
	 * 
	 * @return
	 */
	protected IDocumentProvider getDocumentProvider()
	{
		return editor.getDocumentProvider();
	}

	/**
	 * getEditorInput
	 * 
	 * @return
	 */
	protected IEditorInput getEditorInput()
	{
		return editor.getEditorInput();
	}

	/**
	 * getSelectionListener
	 * 
	 * @return
	 */
	protected ISelectionListener getSelectionListener()
	{
		if (selectionListener == null)
		{
			selectionListener = new ISelectionListener()
			{
				public void selectionChanged(IWorkbenchPart part, ISelection selection)
				{
					updateAnnotations(selection);
				}
			};
		}

		return selectionListener;
	}

	/**
	 * getSelectionService
	 * 
	 * @return
	 */
	protected ISelectionService getSelectionService()
	{
		return editor.getSite().getWorkbenchWindow().getSelectionService();
	}

	/**
	 * getSourceViewer
	 * 
	 * @return
	 */
	protected ISourceViewer getSourceViewer()
	{
		return editor.getISourceViewer();
	}

	/**
	 * getWord
	 * 
	 * @param document
	 * @param selection
	 * @return
	 */
	protected String getWord(IDocument document, ITextSelection selection)
	{
		String result = null;

		int start, end;
		int offset = selection.getOffset();
		int length = document.getLength();

		try
		{
			start = offset;

			// find starting character
			while (offset >= 0)
			{
				char c = document.getChar(offset);

				if (Character.isUnicodeIdentifierPart(c))
				{
					start = offset;
					offset--;
				}
				else
				{
					break;
				}
			}

			// find ending character
			offset = selection.getOffset() + selection.getLength();

			while (offset < length)
			{
				char c = document.getChar(offset);

				if (Character.isUnicodeIdentifierPart(c))
				{
					offset++;
				}
				else
				{
					break;
				}
			}

			end = offset;

			// grab result
			result = document.get(start, end - start);
		}
		catch (BadLocationException e)
		{
		}

		return result;
	}

	/**
	 * initialize
	 * 
	 * @param store
	 */
	public void initialize(IPreferenceStore store)
	{
		if (editor.isMarkingOccurrences())
		{
			installOccurrencesFinder();
			updateAnnotations(getSelectionService().getSelection());
		}
	}

	/**
	 * installOccurrencesFinder
	 */
	protected void installOccurrencesFinder()
	{
		getSelectionService().addPostSelectionListener(getSelectionListener());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		final String property = event.getProperty();

		if (IPreferenceConstants.EDITOR_MARK_OCCURRENCES.equals(property))
		{
			boolean newBooleanValue = false;
			Object newValue = event.getNewValue();

			if (newValue != null)
			{
				newBooleanValue = Boolean.valueOf(newValue.toString()).booleanValue();
			}

			if (newBooleanValue)
			{
				installOccurrencesFinder();
			}
			else
			{
				uninstallOccurrencesFinder();
			}
		}
	}

	/**
	 * uninstallOccurrencesFinder
	 */
	protected void uninstallOccurrencesFinder()
	{
		getSelectionService().removePostSelectionListener(getSelectionListener());
	}

	private Object getAnnotationModelLock(IAnnotationModel annotationModel)
	{
		Object result = annotationModel;

		if (annotationModel instanceof ISynchronizable)
		{
			Object lock = ((ISynchronizable) annotationModel).getLockObject();

			if (lock != null)
			{
				result = lock;
			}
		}

		return result;
	}

	/**
	 * updateAnnotations
	 * 
	 * @param selection
	 */
	protected void updateAnnotations(ISelection selection)
	{
		if (selection instanceof ITextSelection)
		{
			ITextSelection textSelection = (ITextSelection) selection;
			ISourceViewer sourceViewer = getSourceViewer();

			if (sourceViewer != null)
			{
				IDocument document = sourceViewer.getDocument();
				IAnnotationModel annotationModel = getAnnotationModel();

				if (annotationModel != null)
				{
					String text = getWord(document, textSelection);

					if (text != null && text.length() > 0)
					{
						System.out.println(text);
						String source = document.get();
						String regexSource = "\\b" + Pattern.quote(text) + "\\b";
						Pattern wordPattern = Pattern.compile(regexSource);
						Matcher matcher = wordPattern.matcher(source);
						Map<Annotation, Position> annotationMap = new HashMap<Annotation, Position>();

						while (matcher.find())
						{
							int start = matcher.start();
							int end = matcher.end();
							Annotation annotation = new Annotation("com.aptana.editor.common.occurrence", false,
									"some description");
							Position position = new Position(start, end - start);

							annotationMap.put(annotation, position);
						}

						synchronized (getAnnotationModelLock(annotationModel))
						{
							// @formatter:off
								((IAnnotationModelExtension) annotationModel).replaceAnnotations(
									annotations,
									annotationMap
								);
								// @formatter:on
						}

						annotations = annotationMap.keySet().toArray(new Annotation[annotationMap.keySet().size()]);
					}
				}
			}
		}
	}
}
