/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.views;

import java.io.File;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.eclipse.ui.part.ViewPart;

import com.aptana.ide.ui.io.navigator.actions.EditorUtils;
import com.aptana.samples.SamplesPlugin;
import com.aptana.samples.handlers.ISamplePreviewHandler;
import com.aptana.samples.model.SampleEntry;
import com.aptana.samples.model.SamplesReference;
import com.aptana.samples.ui.SamplesUIPlugin;
import com.aptana.samples.ui.project.SampleProjectCreator;
import com.aptana.theme.ColorManager;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;
import com.aptana.ui.util.UIUtils;

/**
 * @author Kevin Lindsey
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Michael Xia
 */
@SuppressWarnings("restriction")
public class SamplesView extends ViewPart
{

	// the view id
	public static final String ID = "com.aptana.samples.ui.SamplesView"; //$NON-NLS-1$

	private static final String ICON_IMPORT = "icons/import_wiz.gif"; //$NON-NLS-1$
	private static final String ICON_PREVIEW = "icons/preview.gif"; //$NON-NLS-1$
	private static final String ICON_HELP = "icons/book_open.png"; //$NON-NLS-1$

	private TreeViewer treeViewer;

	private Action importAction;
	private Action viewPreviewAction;
	private Action viewHelpAction;
	private Action collapseAllAction;
	private Action doubleClickAction;

	private IPreferenceChangeListener themeChangeListener;

	@Override
	public void createPartControl(Composite parent)
	{
		treeViewer = createTreeViewer(parent);

		createActions();
		hookContextMenu();
		hookToolbarActions();

		treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{

			public void doubleClick(DoubleClickEvent event)
			{
				doubleClickAction.run();
			}
		});
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				updateActionState();
			}
		});

		updateActionState();
		applyTheme();
		addListeners();
	}

	@Override
	public void setFocus()
	{
	}

	@Override
	public void dispose()
	{
		removeListeners();
		super.dispose();
	}

	protected TreeViewer createTreeViewer(Composite parent)
	{
		TreeViewer treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewer.setContentProvider(new SamplesViewContentProvider());
		treeViewer.setLabelProvider(new SamplesViewLabelProvider());
		treeViewer.setInput(SamplesPlugin.getDefault().getSamplesManager());
		treeViewer.setComparator(new ViewerComparator());

		return treeViewer;
	}

	private void createActions()
	{
		createImportAction();
		createViewPreviewAction();
		createViewHelpAction();
		createCollapseAllAction();
		createDoubleClickAction();
	}

	private void createImportAction()
	{
		importAction = new Action(Messages.SamplesView_LBL_ImportSample)
		{

			@Override
			public void run()
			{
				ISelection selection = treeViewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();

				if (firstElement instanceof SamplesReference)
				{
					SamplesReference samplesRef = (SamplesReference) firstElement;
					if (samplesRef.isRemote())
					{
						// imports from git
						SampleProjectCreator.createSampleProject(samplesRef);
					}
				}
				SampleEntry sample = null;
				if (firstElement instanceof SampleEntry)
				{
					sample = getRootSample((SampleEntry) firstElement);
				}
				if (sample != null)
				{
					SampleProjectCreator.createSampleProject(sample);
				}
			}
		};
		importAction.setToolTipText(Messages.SamplesView_LBL_ImportSample);
		importAction.setImageDescriptor(SamplesUIPlugin.getImageDescriptor(ICON_IMPORT));
	}

	private void createViewPreviewAction()
	{
		viewPreviewAction = new Action(Messages.SamplesView_LBL_PreviewSample)
		{

			@Override
			public void run()
			{
				ISelection selection = treeViewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();

				SampleEntry sample = null;
				if (firstElement instanceof SampleEntry)
				{
					sample = getRootSample((SampleEntry) firstElement);
				}
				if (sample != null)
				{
					ISamplePreviewHandler handler = ((SamplesReference) sample.getParent()).getPreviewHandler();
					if (handler != null)
					{
						handler.previewRequested(sample);
					}
				}
			}
		};
		viewPreviewAction.setToolTipText(Messages.SamplesView_TTP_PreviewSample);
		viewPreviewAction.setImageDescriptor(SamplesUIPlugin.getImageDescriptor(ICON_PREVIEW));
	}

	private void createViewHelpAction()
	{
		viewHelpAction = new Action(Messages.SamplesView_LBL_ViewHelp)
		{

			@Override
			public void run()
			{
				ISelection selection = treeViewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();

				SamplesReference samplesRef = null;
				if (firstElement instanceof SamplesReference)
				{
					samplesRef = (SamplesReference) firstElement;
				}
				else if (firstElement instanceof SampleEntry)
				{
					samplesRef = getParentSamplesRef((SampleEntry) firstElement);
				}
				if (samplesRef != null)
				{
					try
					{
						String infoFile = samplesRef.getInfoFile();
						if (infoFile != null)
						{
							URL url = (new File(infoFile)).toURL();
							WebBrowserEditorInput input = new WebBrowserEditorInput(url);
							IWorkbenchPage page = UIUtils.getActivePage();
							if (page != null)
							{
								page.openEditor(input, WebBrowserEditor.WEB_BROWSER_EDITOR_ID);
							}
						}

					}
					catch (Exception e)
					{
						SamplesUIPlugin.logError(Messages.SamplesView_ERR_UnableToOpenHelp, e);
					}
				}
			}

		};
		viewHelpAction.setToolTipText(Messages.SamplesView_LBL_ViewHelp);
		viewHelpAction.setImageDescriptor(SamplesUIPlugin.getImageDescriptor(ICON_HELP));
	}

	private void createCollapseAllAction()
	{
		collapseAllAction = new Action(Messages.SamplesView_LBL_CollapseAll)
		{

			@Override
			public void run()
			{
				treeViewer.collapseAll();
			}
		};
		collapseAllAction.setToolTipText(Messages.SamplesView_LBL_CollapseAll);
		collapseAllAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
		collapseAllAction.setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL_DISABLED));
	}

	private void createDoubleClickAction()
	{
		doubleClickAction = new Action(Messages.SamplesView_LBL_Open)
		{

			@Override
			public void run()
			{
				ISelection selection = treeViewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();

				if (firstElement instanceof SampleEntry)
				{
					File file = ((SampleEntry) firstElement).getFile();

					if (file != null && file.isFile())
					{
						try
						{
							EditorUtils.openFileInEditor(EFS.getStore(file.toURI()));
						}
						catch (CoreException e)
						{
							SamplesUIPlugin.logError(Messages.SamplesView_ERR_UnableToOpenFile, e);
						}
					}
				}
			}
		};
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{

			public void menuAboutToShow(IMenuManager manager)
			{
				ISelection selection = treeViewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();
				fillContextMenu(manager, firstElement);
			}
		});

		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, treeViewer);
	}

	private void fillContextMenu(IMenuManager manager, Object element)
	{
		if (element instanceof SamplesReference)
		{
			manager.add(viewHelpAction);
			SamplesReference samplesRef = (SamplesReference) element;
			if (samplesRef.isRemote())
			{
				manager.add(importAction);
			}
		}
		else if (element instanceof SampleEntry)
		{
			manager.add(importAction);

			SampleEntry entry = (SampleEntry) element;
			SamplesReference samplesRef = getParentSamplesRef(entry);
			if (samplesRef != null)
			{
				ISamplePreviewHandler previewHandler = samplesRef.getPreviewHandler();
				if (previewHandler != null)
				{
					manager.add(viewPreviewAction);
				}
			}
			File file = ((SampleEntry) element).getFile();
			if (file != null && file.isFile())
			{
				manager.add(doubleClickAction);
			}
		}
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void hookToolbarActions()
	{
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager manager = bars.getToolBarManager();
		manager.add(importAction);
		manager.add(viewPreviewAction);
		manager.add(viewHelpAction);
		manager.add(collapseAllAction);
	}

	private void addListeners()
	{
		themeChangeListener = new IPreferenceChangeListener()
		{
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					applyTheme();
				}
			}
		};
		(new InstanceScope().getNode(ThemePlugin.PLUGIN_ID)).addPreferenceChangeListener(themeChangeListener);
	}

	private void removeListeners()
	{
		(new InstanceScope().getNode(ThemePlugin.PLUGIN_ID)).removePreferenceChangeListener(themeChangeListener);
	}

	private void applyTheme()
	{
		ColorManager colorManager = ThemePlugin.getDefault().getColorManager();
		Theme currentTheme = ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
		treeViewer.getTree().setBackground(colorManager.getColor(currentTheme.getBackground()));
		treeViewer.getTree().setForeground(colorManager.getColor(currentTheme.getForeground()));
	}

	private void updateActionState()
	{
		ISelection selection = treeViewer.getSelection();
		Object firstElement = ((IStructuredSelection) selection).getFirstElement();
		SamplesReference samplesRef = null;
		SampleEntry entry = null;
		if (firstElement instanceof SamplesReference)
		{
			samplesRef = (SamplesReference) firstElement;
		}
		if (firstElement instanceof SampleEntry)
		{
			entry = (SampleEntry) firstElement;
			samplesRef = getParentSamplesRef(entry);
		}

		importAction.setEnabled(entry != null || samplesRef != null);
		viewPreviewAction.setEnabled(entry != null && samplesRef != null && samplesRef.getPreviewHandler() != null);
		viewHelpAction.setEnabled(samplesRef != null && samplesRef.getInfoFile() != null);
	}

	private static SamplesReference getParentSamplesRef(SampleEntry entry)
	{
		Object parent = entry.getParent();
		while (parent instanceof SampleEntry)
		{
			parent = ((SampleEntry) parent).getParent();
		}
		return (parent instanceof SamplesReference) ? (SamplesReference) parent : null;
	}

	private static SampleEntry getRootSample(SampleEntry entry)
	{
		Object parent = entry.getParent();
		while (parent instanceof SampleEntry)
		{
			entry = (SampleEntry) parent;
			parent = entry.getParent();
		}
		return entry;
	}
}
