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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.preferences.IPreferenceConstants;

/**
 * CommonOccurrenceUpdater
 */
public class CommonOccurrencesUpdater implements IPropertyChangeListener {
	private class CancelerJob implements IDocumentListener, ITextInputListener {

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
		 */
		public void documentAboutToBeChanged(DocumentEvent event) {
			if (findOccurrencesJob != null) {
				findOccurrencesJob.cancel();
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
		 */
		public void documentChanged(DocumentEvent event) {
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.text.ITextInputListener#inputDocumentAboutToBeChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
		 */
		public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
			if (oldInput != null) {
				oldInput.removeDocumentListener(this);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.text.ITextInputListener#inputDocumentChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
		 */
		public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
			if (newInput != null) {
				newInput.addDocumentListener(this);
			}
		}

		/**
		 * install
		 */
		public void install() {
			ISourceViewer sourceViewer = getSourceViewer();
			IDocument document = getDocument();

			if (sourceViewer != null) {
				sourceViewer.addTextInputListener(this);
			}

			if (document != null) {
				document.addDocumentListener(this);
			}
		}

		/**
		 * uninstall
		 */
		public void uninstall() {
			ISourceViewer sourceViewer = getSourceViewer();
			IDocument document = getDocument();

			if (sourceViewer != null) {
				sourceViewer.removeTextInputListener(this);
			}

			if (document != null) {
				document.removeDocumentListener(this);
			}
		}

	}

	private class FindOccurrencesJob extends Job {
		private IDocument document;
		private ITextSelection selection;
		private IAnnotationModel model;

		public FindOccurrencesJob(IDocument document, ITextSelection selection, IAnnotationModel model) {
			super(Messages.CommonOccurrencesUpdater_Mark_Word_Occurrences);

			this.document = document;
			this.selection = selection;
			this.model = model;
		}

		/**
		 * getWord
		 * 
		 * @return
		 */
		protected String getWord() {
			String result = null;

			try {
				int offset = selection.getOffset();
				int length = document.getLength();
				int start = offset;

				// find starting character, if we're on a valid character already
				if (Character.isUnicodeIdentifierPart(document.getChar(offset))) {
					while (offset >= 0) {
						char c = document.getChar(offset);

						if (Character.isUnicodeIdentifierPart(c)) {
							start = offset;
							offset--;
						} else {
							break;
						}
					}
				}

				// find ending character, if we're on a valid character already
				offset = selection.getOffset() + selection.getLength();

				if (Character.isUnicodeIdentifierPart(document.getChar(offset - 1))) {
					while (offset < length) {
						char c = document.getChar(offset);

						if (Character.isUnicodeIdentifierPart(c)) {
							offset++;
						} else {
							break;
						}
					}
				}

				// grab result, as long as it is on one line only
				if (document.getLineOfOffset(start) == document.getLineOfOffset(offset)) {
					result = document.get(start, offset - start);
				}
			} catch (BadLocationException e) {
			}

			return (result != null) ? result.trim() : result;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			IStatus status = Status.OK_STATUS;

			// start with an empty map in case we need to delete existing markers later
			Map<Annotation, Position> annotationMap = new HashMap<Annotation, Position>();

			if (editor.isMarkingOccurrences()) {
				// find a "word" to search using the current selection
				String word = getWord();

				if (word != null && word.length() > 0) {
					String source = document.get();
					Pattern wordPattern = createWordPattern(word);
					Matcher matcher = wordPattern.matcher(source);

					while (matcher.find()) {
						if (monitor.isCanceled()) {
							status = Status.CANCEL_STATUS;
							break;
						}

						int start = matcher.start();
						int length = matcher.end() - start;

						// @formatter:off
						annotationMap.put(new Annotation(ANNOTION_ID, false, ANNOTION_DESCRIPTION), new Position(start, length));
						// @formatter:on
					}
				}
			}

			if (status == Status.OK_STATUS) {
				// NOTE: We always update the annotation model even if we didn't find a word so we can clear the
				// current occurrences
				synchronized (getAnnotationModelLock(model)) {
					if (model instanceof IAnnotationModelExtension) {
						// @formatter:off
						((IAnnotationModelExtension) model).replaceAnnotations(annotations, annotationMap);
						// @formatter:on
					}

					annotations = annotationMap.keySet().toArray(new Annotation[annotationMap.keySet().size()]);
				}
			}

			return status;
		}
	}

	private static final String ANNOTION_ID = "com.aptana.editor.common.occurrence"; //$NON-NLS-1$
	private static final String ANNOTION_DESCRIPTION = Messages.CommonOccurrencesUpdater_Word_Occurrence_Description;

	private AbstractThemeableEditor editor;
	private ISelectionListener selectionListener;
	private IPreferenceStore preferenceStore;
	private Annotation[] annotations;
	private FindOccurrencesJob findOccurrencesJob;
	private CancelerJob cancelerJob;

	/**
	 * CommonOccurrencesUpdater
	 * 
	 * @param editor
	 */
	CommonOccurrencesUpdater(AbstractThemeableEditor editor) {
		this.editor = editor;
	}

	/**
	 * createWordPattern
	 * 
	 * @param text
	 * @return
	 */
	private Pattern createWordPattern(String text) {
		String regexSource = StringUtil.EMPTY;

		if (Character.isUnicodeIdentifierPart(text.charAt(0))) {
			regexSource += "\\b"; //$NON-NLS-1$
		}

		regexSource += Pattern.quote(text);

		if (Character.isUnicodeIdentifierPart(text.charAt(text.length() - 1))) {
			regexSource += "\\b"; //$NON-NLS-1$
		}

		return Pattern.compile(regexSource);
	}

	/**
	 * getAnnotationModel
	 * 
	 * @return
	 */
	private IAnnotationModel getAnnotationModel() {
		IDocumentProvider documentProvider = getDocumentProvider();
		IEditorInput editorInput = getEditorInput();
		IAnnotationModel result = null;

		if (documentProvider != null && editorInput != null) {
			result = documentProvider.getAnnotationModel(editorInput);
		}

		return result;
	}

	/**
	 * getAnnotationModelLock
	 * 
	 * @param annotationModel
	 * @return
	 */
	private Object getAnnotationModelLock(IAnnotationModel annotationModel) {
		Object result = annotationModel;

		if (annotationModel instanceof ISynchronizable) {
			Object lock = ((ISynchronizable) annotationModel).getLockObject();

			if (lock != null) {
				result = lock;
			}
		}

		return result;
	}

	/**
	 * getDocument
	 * 
	 * @return
	 */
	private IDocument getDocument() {
		ISourceViewer sourceViewer = getSourceViewer();
		IDocument result = null;

		if (sourceViewer != null) {
			result = sourceViewer.getDocument();
		}

		return result;
	}

	/**
	 * getDocumentProvider
	 * 
	 * @return
	 */
	private IDocumentProvider getDocumentProvider() {
		return editor.getDocumentProvider();
	}

	/**
	 * getEditorInput
	 * 
	 * @return
	 */
	private IEditorInput getEditorInput() {
		return editor.getEditorInput();
	}

	/**
	 * getSelectionListener
	 * 
	 * @return
	 */
	private ISelectionListener getSelectionListener() {
		if (selectionListener == null) {
			selectionListener = new ISelectionListener() {
				public void selectionChanged(IWorkbenchPart part, ISelection selection) {
					if (part == editor) {
						updateAnnotations(selection);
					}
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
	private ISelectionService getSelectionService() {
		return editor.getSite().getWorkbenchWindow().getSelectionService();
	}

	/**
	 * getSourceViewer
	 * 
	 * @return
	 */
	private ISourceViewer getSourceViewer() {
		return editor.getISourceViewer();
	}

	/**
	 * initialize
	 * 
	 * @param store
	 */
	void initialize(IPreferenceStore store) {
		preferenceStore = store;
		if (editor.isMarkingOccurrences()) {
			install();
			updateAnnotations(getSelectionService().getSelection());
		}

		preferenceStore.addPropertyChangeListener(this);
	}

	void dispose() {
		if (preferenceStore != null) {
			preferenceStore.removePropertyChangeListener(this);
			preferenceStore = null;
		}
		uninstall();
	}

	/**
	 * install
	 */
	protected void install() {
		getSelectionService().addPostSelectionListener(getSelectionListener());

		if (cancelerJob == null) {
			cancelerJob = new CancelerJob();
			cancelerJob.install();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		final String property = event.getProperty();

		if (IPreferenceConstants.EDITOR_MARK_OCCURRENCES.equals(property)) {
			boolean newBooleanValue = false;
			Object newValue = event.getNewValue();

			if (newValue != null) {
				newBooleanValue = Boolean.valueOf(newValue.toString()).booleanValue();
			}

			if (newBooleanValue) {
				install();
			} else {
				uninstall();
			}

			// force update
			if (editor != null) {
				ISelectionProvider selectionProvider = editor.getSelectionProvider();

				if (selectionProvider != null) {
					updateAnnotations(selectionProvider.getSelection());
				}
			}
		}
	}

	/**
	 * uninstall
	 */
	protected void uninstall() {
		if (selectionListener != null) {
			getSelectionService().removePostSelectionListener(selectionListener);
			selectionListener = null;
		}

		if (findOccurrencesJob != null) {
			findOccurrencesJob.cancel();
			findOccurrencesJob = null;
		}

		if (cancelerJob != null) {
			cancelerJob.uninstall();
			cancelerJob = null;
		}
	}

	/**
	 * updateAnnotations
	 * 
	 * @param selection
	 */
	private void updateAnnotations(ISelection selection) {
		if (findOccurrencesJob != null) {
			findOccurrencesJob.cancel();
		}

		if (selection instanceof ITextSelection) {
			ITextSelection textSelection = (ITextSelection) selection;
			IDocument document = getDocument();
			IAnnotationModel annotationModel = getAnnotationModel();

			if (document != null && annotationModel != null) {
				findOccurrencesJob = new FindOccurrencesJob(document, textSelection, annotationModel);
				findOccurrencesJob.schedule();
			}
		}
	}
}
