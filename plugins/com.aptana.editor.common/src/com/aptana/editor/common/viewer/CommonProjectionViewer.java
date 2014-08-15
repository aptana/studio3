/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.viewer;

import java.lang.reflect.Field;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonSourceViewerConfiguration;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.scripting.snippets.ExpandSnippetVerifyKeyListener;
import com.aptana.editor.common.scripting.snippets.SnippetsContentAssistant;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ScriptFormatterManager;
import com.aptana.formatter.preferences.PreferencesLookupDelegate;
import com.aptana.formatter.ui.ScriptFormattingContextProperties;

/**
 * @author Max Stepanov
 */
public class CommonProjectionViewer extends ProjectionViewer implements IAdaptable, IPreferenceChangeListener
{

	private static final int RULER_EDITOR_GAP = 5;

	protected ExpandSnippetVerifyKeyListener fKeyListener;
	protected IContentAssistant fSnippetContentAssistant;

	protected IEclipsePreferences fPreferenceStore;

	/**
	 * @param parent
	 * @param ruler
	 * @param overviewRuler
	 * @param showsAnnotationOverview
	 * @param styles
	 */
	public CommonProjectionViewer(Composite parent, IVerticalRuler ruler, IOverviewRuler overviewRuler,
			boolean showsAnnotationOverview, int styles)
	{
		super(parent, ruler, overviewRuler, showsAnnotationOverview, styles);
	}

	protected Layout createLayout()
	{
		return new RulerLayout(RULER_EDITOR_GAP);
	}

	@Override
	protected void handleDispose()
	{
		// HACK We force the widget command to be nulled out so it can be garbage collected. Might want to
		// report a bug with eclipse to clean this up.
		try
		{
			Field f = TextViewer.class.getDeclaredField("fWidgetCommand"); //$NON-NLS-1$
			if (f != null)
			{
				f.setAccessible(true);
				f.set(this, null);
			}
		}
		catch (Throwable t)
		{
			// ignore
		}
		finally
		{
			super.handleDispose();
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IFormattingContext createFormattingContext()
	{
		final IFormattingContext context = super.createFormattingContext();
		try
		{
			QualifiedContentType contentType = CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.getContentType(getDocument(), 0);
			if (contentType != null && contentType.getPartCount() > 0)
			{
				for (String ct : contentType.getParts())
				{
					String mainContentType = ct;
					// We need to make sure that in case the given content type is actually a nested language in
					// HTML, we look for the HTML formatter factory because it should be the 'Master' formatter.
					if (mainContentType.startsWith(CommonSourceViewerConfiguration.CONTENTTYPE_HTML_PREFIX))
					{
						mainContentType = CommonSourceViewerConfiguration.CONTENTTYPE_HTML_PREFIX;
					}
					final IScriptFormatterFactory factory = ScriptFormatterManager.getSelected(mainContentType);
					if (factory != null)
					{
						// The code above might change the content type that is used to
						// get the formatter, but we still need to save the original content-type so that the
						// IScriptFormatter instance will handle the any required parsing by calling the right
						// IParser.
						factory.setMainContentType(contentType.getParts()[0]);

						ITextEditor textEditor = (ITextEditor) getAdapter(ITextEditor.class);
						if (textEditor != null)
						{
							IResource file = (IResource) textEditor.getEditorInput().getAdapter(IResource.class);
							context.setProperty(ScriptFormattingContextProperties.CONTEXT_FORMATTER_ID, factory.getId());
							IProject project = (file != null) ? file.getProject() : null;
							Map preferences = factory.retrievePreferences(new PreferencesLookupDelegate(project));
							context.setProperty(FormattingContextProperties.CONTEXT_PREFERENCES, preferences);
						}
						break;
					}
				}
			}
		}
		catch (BadLocationException e)
		{
		}
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter)
	{
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.source.SourceViewer#configure(org.eclipse.jface.text.source.SourceViewerConfiguration)
	 */
	@Override
	public void configure(SourceViewerConfiguration configuration)
	{
		super.configure(configuration);

		fSnippetContentAssistant = new SnippetsContentAssistant();
		fSnippetContentAssistant.install(this);
		fKeyListener = new ExpandSnippetVerifyKeyListener((ITextEditor) getAdapter(ITextEditor.class), this,
				fSnippetContentAssistant);
		// add listener to our viewer
		prependVerifyKeyListener(fKeyListener);

		fPreferenceStore = InstanceScope.INSTANCE.getNode(CommonEditorPlugin.PLUGIN_ID);
		if (fPreferenceStore != null)
		{
			fPreferenceStore.addPreferenceChangeListener(this);
			setSnippetProcessorEnablement();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewer#unconfigure()
	 */
	@Override
	public void unconfigure()
	{
		if (fPreferenceStore != null)
		{
			fPreferenceStore.removePreferenceChangeListener(this);
		}

		if (fKeyListener != null)
		{
			removeVerifyKeyListener(fKeyListener);
			fKeyListener = null;
		}
		if (fSnippetContentAssistant != null)
		{
			fSnippetContentAssistant.uninstall();
			fSnippetContentAssistant = null;
		}
		super.unconfigure();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener#preferenceChange(org.eclipse
	 * .core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent)
	 */
	public void preferenceChange(PreferenceChangeEvent event)
	{
		if (IPreferenceConstants.CONTENT_ASSIST_DELAY.equals(event.getKey()))
		{
			setSnippetProcessorEnablement();
		}
	}

	/**
	 * Do we enable or disable the snippet processor?
	 */
	private void setSnippetProcessorEnablement()
	{
		if (fPreferenceStore == null || fKeyListener == null)
		{
			return;
		}

		int delay = fPreferenceStore.getInt(IPreferenceConstants.CONTENT_ASSIST_DELAY,
				CommonSourceViewerConfiguration.DEFAULT_CONTENT_ASSIST_DELAY);
		fKeyListener.setEnabled(delay >= 0);
	}
}
