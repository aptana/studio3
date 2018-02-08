/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.snippets.ui.views;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.IExtendedPartitioner;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.scripting.snippets.SnippetTemplateUtil;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.NullSubPartitionScanner;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ScriptFormatterManager;
import com.aptana.formatter.preferences.profile.IProfileManager;
import com.aptana.formatter.preferences.profile.ProfileManager;
import com.aptana.formatter.ui.preferences.FormatterPreviewUtils;
import com.aptana.formatter.ui.preferences.ScriptSourcePreviewerUpdater;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.BundlePrecedence;
import com.aptana.scripting.model.SnippetElement;
import com.aptana.scripting.model.TriggerType;
import com.aptana.scripting.ui.ScriptingUIPlugin;
import com.aptana.theme.ColorManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;
import com.aptana.ui.util.UIUtils;
import com.aptana.workbench.commands.EditBundleJob;

/**
 * PopupDialog that displays the contents of a snippets, formatted and colored based on the preferences
 * 
 * @author nle
 */
public class SnippetPopupDialog extends PopupDialog
{
	private static final String SNIPPETS_POPUP_SETTINGS = "snippets.popup.settings"; //$NON-NLS-1$
	private ToolBar toolbar;
	private Control positionTarget;
	private List<Image> toolbarImages = new ArrayList<Image>();
	private SnippetElement snippet;
	private ColorManager colorManager;
	private Point popupSize = null;
	private String tabChar;

	/**
	 * The pixel offset of the popup from the bottom corner of the control.
	 */
	private static final int POPUP_OFFSET = 3;

	/**
	 * Indicates that a chosen proposal should be inserted into the field.
	 */
	public static final int PROPOSAL_INSERT = 1;
	private QualifiedContentType translatedQualifiedType;
	private Composite toolbarComp;
	private ISourceViewer snippetViewer;
	private Composite mainComp;
	private Composite snippetComp;

	public SnippetPopupDialog(Shell shell, SnippetElement snippet, Control positionTarget)
	{
		super(shell, PopupDialog.INFOPOPUP_SHELLSTYLE, true, true, false, false, false, snippet
				.getDisplayName(), null);
		this.positionTarget = positionTarget;
		this.snippet = snippet;
		colorManager = new ColorManager();
		tabChar = Platform.getOS().equals(Platform.OS_MACOSX) ? "\u21E5" : "\u00bb"; //$NON-NLS-1$ //$NON-NLS-2$ 
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		composite.setLayout(GridLayoutFactory.fillDefaults().create());

		return super.createContents(composite);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		mainComp = (Composite) super.createDialogArea(parent);

		snippetComp = new Composite(mainComp, SWT.NONE);
		snippetComp.setLayout(new FillLayout());
		snippetComp.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		snippetViewer = createSnippetViewer(snippetComp);

		Label separator = new Label(mainComp, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		toolbarComp = new Composite(mainComp, SWT.NONE);
		toolbarComp.setBackground(null);
		toolbarComp.setLayout(GridLayoutFactory.fillDefaults().margins(2, 2).create());
		toolbarComp.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		toolbar = new ToolBar(toolbarComp, SWT.HORIZONTAL);
		toolbar.setBackground(null);
		toolbar.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		ToolItem openSnippetItem = new ToolItem(toolbar, SWT.PUSH);
		Image navigateImage = ScriptingUIPlugin.getImageDescriptor("/icons/full/elcl16/nav_snippet_tsk.png") //$NON-NLS-1$
				.createImage();
		toolbarImages.add(navigateImage);
		openSnippetItem.setImage(navigateImage);
		openSnippetItem.setToolTipText(Messages.SnippetPopupDialog_Open_Snippet_Source_desc);
		openSnippetItem.addSelectionListener(new SelectionAdapter()
		{
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// We have to create it if it's still pre-packaged
				final BundleElement bundle = snippet.getOwningBundle();
				BundlePrecedence bundlePrecedence = bundle.getBundlePrecedence();
				if (bundlePrecedence != BundlePrecedence.PROJECT && bundlePrecedence != BundlePrecedence.USER)
				{
					final EditBundleJob job = new EditBundleJob(bundle);
					job.addJobChangeListener(new JobChangeAdapter()
					{
						public void done(IJobChangeEvent event)
						{
							openBundleSnippet(bundle);
						}
					});

					job.schedule();
				}
				else
				{
					openBundleSnippet(bundle);
				}

				close();
			}

			private void openBundleSnippet(BundleElement bundle)
			{
				String path = snippet.getPath();
				List<SnippetElement> bundleSnippets = BundleManager.getInstance().getBundleSnippets(
						bundle.getDisplayName());
				for (SnippetElement element : bundleSnippets)
				{
					if (element.getDisplayName().equals(snippet.getDisplayName())
							&& element.getExpansion().equals(snippet.getExpansion()))
					{
						path = element.getPath();
						break;
					}
				}

				final File file = new File(path);
				Display.getDefault().asyncExec(new Runnable()
				{

					public void run()
					{
						IFile[] foundFiles = ResourcesPlugin.getWorkspace().getRoot()
								.findFilesForLocationURI(file.toURI());
						if (!ArrayUtil.isEmpty(foundFiles))
						{
							EditorUtil.openInEditor(new File(foundFiles[0].getLocationURI()));
						}
						else if (file.exists())
						{
							EditorUtil.openInEditor(file);
						}
					}
				});

			}
		});

		return mainComp;
	}

	@Override
	protected Control createTitleControl(Composite parent)
	{
		Control control = super.createTitleControl(parent);

		Label subText = new Label(parent, SWT.WRAP);
		String[] prefixes = snippet.getTriggerTypeValues(TriggerType.PREFIX);
		String[] formattedPrefixes = new String[prefixes.length];
		for (int i = 0; i < formattedPrefixes.length; i++)
		{
			formattedPrefixes[i] = MessageFormat.format("{0}{1}", prefixes[i], tabChar); //$NON-NLS-1$
		}

		String scopeString = snippet.getScope();
		if (scopeString == null)
		{
			scopeString = Messages.SnippetPopupDialog_Scope_None;
		}

		subText.setText(MessageFormat.format(Messages.SnippetPopupDialog_Desciption, scopeString,
				StringUtil.join(",", formattedPrefixes))); //$NON-NLS-1$
		subText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		return control;
	}

	private ColorManager getColorManager()
	{
		return ThemePlugin.getDefault().getColorManager();
	}

	private Theme getCurrentTheme()
	{
		return ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
	}

	@Override
	protected Color getBackground()
	{
		return getColorManager().getColor(getCurrentTheme().getBackground());

	}

	@Override
	protected Color getForeground()
	{
		return getColorManager().getColor(getCurrentTheme().getForeground());
	}

	@Override
	protected List getBackgroundColorExclusions()
	{
		List exclusions = super.getBackgroundColorExclusions();
		exclusions.add(toolbar);
		exclusions.add(toolbarComp);
		exclusions.add(mainComp);
		exclusions.add(snippetComp);
		return exclusions;
	}

	private ISourceViewer createSnippetViewer(Composite parent)
	{
		ProjectionViewer viewer = new ProjectionViewer(parent, null, null, false, SWT.V_SCROLL | SWT.H_SCROLL);
		StyledText styledText = viewer.getTextWidget();
		styledText.setFont(JFaceResources.getTextFont());

		IScriptFormatterFactory factory = null;
		String contentType = getContentType();

		if (contentType != null)
		{
			factory = ScriptFormatterManager.getSelected(contentType);
		}

		IPreferenceStore generalTextStore = EditorsUI.getPreferenceStore();
		// TODO - Note that we pass the factory's preferences store and not calling to this.getPrefereceStore.
		// In case we decide to unify the preferences into the this plugin, we might need to change this.

		if (factory != null)
		{
			IPreferenceStore store = new ChainedPreferenceStore(new IPreferenceStore[] { factory.getPreferenceStore(),
					generalTextStore });

			SourceViewerConfiguration configuration = (SourceViewerConfiguration) factory
					.createSimpleSourceViewerConfiguration(colorManager, store, null, false);
			viewer.configure(configuration);
			new ScriptSourcePreviewerUpdater(viewer, configuration, store);
		}

		if (viewer.getTextWidget().getTabs() == 0)
		{
			viewer.getTextWidget().setTabs(4);
		}
		viewer.getTextWidget().setEnabled(false);

		viewer.setEditable(false);
		IDocument document = new Document();
		viewer.setDocument(document);

		String expansion = snippet.getExpansion();

		if (expansion != null)
		{
			expansion = SnippetTemplateUtil.evaluateSnippet(snippet, document, new Position(0));
		}

		if (factory != null)
		{
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

			IProfileManager manager = ProfileManager.getInstance();
			IResource selectedResource = UIUtils.getSelectedResource();
			IProject project = null;

			if (selectedResource == null)
			{
				IEditorPart activeEditor = UIUtils.getActiveEditor();
				if (activeEditor instanceof AbstractThemeableEditor)
				{
					project = EditorUtil.getProject((AbstractThemeableEditor) activeEditor);
				}
			}
			else
			{
				project = selectedResource.getProject();
			}

			if (project != null)
			{
				FormatterPreviewUtils.updatePreview(viewer, expansion, null, factory, manager.getSelected(project)
						.getSettings());
			}
			else
			{
				document.set(expansion);
			}
		}
		else
		{
			document.set(expansion);
		}

		if (translatedQualifiedType != null)
		{
			for (String part : translatedQualifiedType.getParts())
			{
				viewer.removeTextHovers(part);
			}
		}
		return viewer;
	}

	private String getContentType()
	{
		IEditorPart activeEditor = UIUtils.getActiveEditor();
		if (activeEditor instanceof AbstractThemeableEditor)
		{
			AbstractThemeableEditor abstractThemeableEditor = (AbstractThemeableEditor) activeEditor;

			ISourceViewer sourceViewer = abstractThemeableEditor.getISourceViewer();
			if (sourceViewer != null)
			{
				IDocument document = sourceViewer.getDocument();
				int caretOffset = abstractThemeableEditor.getCaretOffset();
				try
				{

					translatedQualifiedType = CommonEditorPlugin.getDefault().getDocumentScopeManager()
							.getContentType(document, caretOffset);
					if (translatedQualifiedType != null)
					{
						return extractContentType(translatedQualifiedType);
					}
				}
				catch (BadLocationException e)
				{
					IdeLog.logError(ScriptingUIPlugin.getDefault(), MessageFormat.format(
							"Caret offset {0} was out of bounds with a max of {1} for {2}", caretOffset, document.get() //$NON-NLS-1$
									.length(), abstractThemeableEditor.getPartName()),
							com.aptana.editor.common.IDebugScopes.PRESENTATION);
				}
			}
		}

		return null;
	}

	protected String extractContentType(QualifiedContentType qualifiedContentType)
	{
		if (qualifiedContentType == null)
		{
			return null;
		}
		int partCount = qualifiedContentType.getPartCount();
		if (partCount > 2)
		{
			return qualifiedContentType.getParts()[partCount - 2];
		}
		return qualifiedContentType.getParts()[0];
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#getDialogSettings()
	 */
	@Override
	protected IDialogSettings getDialogSettings()
	{
		IDialogSettings dialogSettings = ScriptingUIPlugin.getDefault().getDialogSettings();
		if (dialogSettings != null)
		{
			IDialogSettings section = dialogSettings.getSection(SNIPPETS_POPUP_SETTINGS);
			if (section == null)
			{
				section = dialogSettings.addNewSection(SNIPPETS_POPUP_SETTINGS);
			}

			return section;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#close()
	 */
	@Override
	public boolean close()
	{
		boolean willClose = super.close();
		if (willClose)
		{
			for (Image image : toolbarImages)
			{
				image.dispose();
			}
			colorManager.dispose();
		}

		return willClose;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog.adjustBounds()
	 */
	protected void adjustBounds()
	{
		// Get our control's location in display coordinates.
		Point location = positionTarget.getDisplay()
				.map(positionTarget.getParent(), null, positionTarget.getLocation());
		Point targetSize = positionTarget.getSize();
		Point sizeSize = UIUtils.getActiveWorkbenchWindow().getShell().getSize();
		int initialX = location.x + targetSize.x;
		int initialY = location.y + POPUP_OFFSET;

		if (popupSize == null)
		{
			getShell().pack();
			popupSize = getShell().getSize();
			if (popupSize.x > 500)
			{
				popupSize.x = 500;
			}

			if (popupSize.y > sizeSize.y)
			{
				popupSize.y = sizeSize.y;
			}

			// On OSX, compensate for the always visible horizontal scroll bar
			if (Platform.OS_MACOSX.equals(Platform.getOS()))
			{
				ScrollBar horizontalBar = snippetViewer.getTextWidget().getHorizontalBar();
				if (horizontalBar != null)
				{
					int height = horizontalBar.getSize().y;
					popupSize.y += height;
				}

				ScrollBar verticalBar = snippetViewer.getTextWidget().getVerticalBar();
				if (verticalBar != null)
				{
					int width = verticalBar.getSize().x;
					popupSize.x += width;
				}
			}
		}

		// Constrain to the display
		Rectangle constrainedBounds = getConstrainedShellBounds(new Rectangle(initialX, initialY, popupSize.x,
				popupSize.y));

		// If there has been an adjustment causing the popup to overlap
		// with the control, then put the popup above the control.
		if (constrainedBounds.y < initialY)
		{
			getShell().setBounds(initialX, location.y - popupSize.y, popupSize.x, popupSize.y);
		}
		else
		{
			getShell().setBounds(initialX, initialY, popupSize.x, popupSize.y);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#getFocusControl()
	 */
	@Override
	protected Control getFocusControl()
	{
		snippetViewer.getTextWidget().setEnabled(true);
		return getContents();
	}

}
