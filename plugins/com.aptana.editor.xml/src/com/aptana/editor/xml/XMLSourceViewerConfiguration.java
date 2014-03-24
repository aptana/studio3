/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml;

import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.quickassist.QuickAssistAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IQuickFixProcessorsRegistry;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.SimpleSourceViewerConfiguration;
import com.aptana.editor.common.text.RubyRegexpAutoIndentStrategy;

public class XMLSourceViewerConfiguration extends SimpleSourceViewerConfiguration
{
	/**
	 * XMLSourceViewerConfiguration
	 * 
	 * @param preferences
	 * @param editor
	 */
	public XMLSourceViewerConfiguration(IPreferenceStore preferences, AbstractThemeableEditor editor)
	{
		super(preferences, editor);
	}

	@SuppressWarnings("restriction")
	@Override
	public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer)
	{
		QuickAssistAssistant assistant = new QuickAssistAssistant();
		IQuickAssistProcessor quickFixProcessor = getQuickAssistProcessor();
		if (quickFixProcessor != null)
		{
			assistant.setQuickAssistProcessor(quickFixProcessor);
		}

		assistant.setRestoreCompletionProposalSize(EditorsPlugin.getDefault().getDialogSettingsSection(
				"quick_assist_proposal_size")); //$NON-NLS-1$
		assistant.setInformationControlCreator(getQuickAssistAssistantInformationControlCreator());

		return assistant;
	}

	private IQuickAssistProcessor getQuickAssistProcessor()
	{
		IQuickFixProcessorsRegistry registry = getQuickFixRegistry();
		return registry.getQuickFixProcessor(getEditor().getContentType());
	}

	protected IQuickFixProcessorsRegistry getQuickFixRegistry()
	{
		return CommonEditorPlugin.getDefault().getQuickFixProcessorRegistry();
	}

	private IInformationControlCreator getQuickAssistAssistantInformationControlCreator()
	{
		return new IInformationControlCreator()
		{
			public IInformationControl createInformationControl(Shell parent)
			{
				return new DefaultInformationControl(parent, EditorsPlugin.getAdditionalInfoAffordanceString());
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.SimpleSourceViewerConfiguration#getSourceViewerConfiguration()
	 */
	@Override
	public ISourceViewerConfiguration getSourceViewerConfiguration()
	{
		return XMLSourceConfiguration.getDefault();
	}

	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
	{
		return new IAutoEditStrategy[] { new RubyRegexpAutoIndentStrategy(contentType, this, sourceViewer, XMLPlugin
				.getDefault().getPreferenceStore()) };
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer)
	{
		Map targets = super.getHyperlinkDetectorTargets(sourceViewer);

		targets.put("com.aptana.editor.xml.sourceCode", getEditor()); //$NON-NLS-1$

		return targets;
	}

}