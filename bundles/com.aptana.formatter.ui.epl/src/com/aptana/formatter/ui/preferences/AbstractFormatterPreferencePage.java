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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.IExtendedPartitioner;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.NullSubPartitionScanner;
import com.aptana.formatter.ContributionExtensionManager;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ScriptFormatterManager;
import com.aptana.formatter.preferences.profile.IProfileManager;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;
import com.aptana.formatter.ui.util.IStatusChangeListener;
import com.aptana.theme.ColorManager;
import com.aptana.theme.ThemePlugin;

public abstract class AbstractFormatterPreferencePage extends AbstractConfigurationBlockPropertyAndPreferencePage
{

	public static final String ID = "com.aptana.formatter.common"; //$NON-NLS-1$

	private static final Job[] NO_BUILD_JOBS = new Job[0];

	protected class FormatterSelectionBlock extends AbstractFormatterSelectionBlock
	{

		private ISharedTextColors fColorManager;

		public FormatterSelectionBlock(IStatusChangeListener context, IProject project,
				IWorkbenchPreferenceContainer container)
		{
			super(context, project, container);
			fColorManager = new ColorManager();
		}

		public void dispose()
		{
			fColorManager.dispose();
			super.dispose();
		}

		protected ContributionExtensionManager getExtensionManager()
		{
			return ScriptFormatterManager.getInstance();
		}

		protected IFormatterModifyDialogOwner createDialogOwner(IScriptFormatterFactory formatter)
		{
			return new FormatterModifyDialogOwner(formatter);
		}

		private class FormatterModifyDialogOwner implements IFormatterModifyDialogOwner
		{

			private final IScriptFormatterFactory formatter;

			public FormatterModifyDialogOwner(IScriptFormatterFactory formatter)
			{
				this.formatter = formatter;
			}

			public ISourceViewer createPreview(Composite composite)
			{
				return FormatterSelectionBlock.this.createSourcePreview(composite, formatter);
			}

			public Shell getShell()
			{
				return AbstractFormatterPreferencePage.this.getShell();
			}

			public IDialogSettings getDialogSettings()
			{
				return AbstractFormatterPreferencePage.this.getDialogSettings();
			}

			/*
			 * (non-Javadoc)
			 * @see com.aptana.formatter.ui.IFormatterModifyDialogOwner#getProject()
			 */
			public IProject getProject()
			{
				return fProject;
			}
		}

		/**
		 * @param composite
		 */
		public SourceViewer createSourcePreview(Composite composite, IScriptFormatterFactory factory)
		{
			IPreferenceStore generalTextStore = EditorsUI.getPreferenceStore();
			// TODO - Note that we pass the factory's preferences store and not calling to this.getPrefereceStore.
			// In case we decide to unify the preferences into the this plugin, we might need to change this.
			IPreferenceStore store = new ChainedPreferenceStore(new IPreferenceStore[] { factory.getPreferenceStore(),
					generalTextStore });
			SourceViewer fPreviewViewer = createPreviewViewer(composite, null, null, false, SWT.V_SCROLL | SWT.H_SCROLL
					| SWT.BORDER, store);
			if (fPreviewViewer == null)
			{
				return null;
			}
			SourceViewerConfiguration configuration = (SourceViewerConfiguration) factory
					.createSimpleSourceViewerConfiguration(fColorManager, store, null, false);
			fPreviewViewer.configure(configuration);
			if (fPreviewViewer.getTextWidget().getTabs() == 0)
			{
				fPreviewViewer.getTextWidget().setTabs(4);
			}
			new ScriptSourcePreviewerUpdater(fPreviewViewer, configuration, store);
			fPreviewViewer.setEditable(false);
			IDocument document = new Document();
			fPreviewViewer.setDocument(document);
			IPartitioningConfiguration partitioningConfiguration = (IPartitioningConfiguration) factory
					.getPartitioningConfiguration();
			CompositePartitionScanner partitionScanner = new CompositePartitionScanner(
					partitioningConfiguration.createSubPartitionScanner(), new NullSubPartitionScanner(),
					new NullPartitionerSwitchStrategy());
			IDocumentPartitioner partitioner = new ExtendedFastPartitioner(partitionScanner,
					partitioningConfiguration.getContentTypes());
			partitionScanner.setPartitioner((IExtendedPartitioner) partitioner);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
			return fPreviewViewer;
		}

		/**
		 * @param parent
		 * @param verticalRuler
		 * @param overviewRuler
		 * @param showAnnotationsOverview
		 * @param styles
		 * @param store
		 * @return
		 */
		private ProjectionViewer createPreviewViewer(Composite parent, IVerticalRuler verticalRuler,
				IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int styles, IPreferenceStore store)
		{
			ProjectionViewer viewer = new ProjectionViewer(parent, verticalRuler, overviewRuler,
					showAnnotationsOverview, styles);
			ThemePlugin.getDefault().getControlThemerFactory().apply(viewer);
			return viewer;
		}

		protected String getPreferenceLinkMessage()
		{
			return FormatterMessages.FormatterPreferencePage_settingsLink;
		}

		protected void updatePreview()
		{
			if (fSelectedPreviewViewer != null)
			{
				IScriptFormatterFactory factory = getSelectedFormatter();
				IProfileManager manager = getProfileManager();
				FormatterPreviewUtils.updatePreview(fSelectedPreviewViewer, factory.getPreviewContent(), null, factory,
						manager.getSelected(fProject).getSettings());
			}
		}

		@Override
		protected Job[] createBuildJobs(IProject project)
		{
			return NO_BUILD_JOBS;
		}
	}

	protected AbstractOptionsBlock createOptionsBlock(IStatusChangeListener newStatusChangedListener, IProject project,
			IWorkbenchPreferenceContainer container)
	{
		return new FormatterSelectionBlock(newStatusChangedListener, project, container);
	}

	protected abstract IDialogSettings getDialogSettings();

	protected String getHelpId()
	{
		return null;
	}

	protected void setDescription()
	{
		// empty
	}

	protected String getPreferencePageId()
	{
		return ID;
	}

	protected String getProjectHelpId()
	{
		return null;
	}

	protected String getPropertyPageId()
	{
		return null;
	}
}
