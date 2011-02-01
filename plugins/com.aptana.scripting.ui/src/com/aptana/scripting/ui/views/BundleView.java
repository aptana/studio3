/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.LoadCycleListener;
import com.aptana.theme.ColorManager;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

public class BundleView extends ViewPart
{
	TreeViewer _treeViewer;
	BundleViewContentProvider _contentProvider;
	BundleViewLabelProvider _labelProvider;
	LoadCycleListener _loadCycleListener;
	IPropertyChangeListener _fontChangeListener;
	IPreferenceChangeListener _themeChangeListener;

	/**
	 * BundleView
	 */
	public BundleView()
	{
	}

	/**
	 * addListeners
	 */
	private void addListeners()
	{
		this.listenForFontChanges();
		this.listenForThemeChanges();
		this.listenForScriptChanges();
	}

	/**
	 * applyTheme
	 */
	private void applyTheme()
	{
		ThemePlugin plugin = ThemePlugin.getDefault();
		ColorManager colorManager = plugin.getColorManager();
		IThemeManager themeManager = plugin.getThemeManager();
		Theme currentTheme = themeManager.getCurrentTheme();

		this._treeViewer.refresh();
		this._treeViewer.getTree().setBackground(colorManager.getColor(currentTheme.getBackground()));
	}

	/**
	 * createPartControl
	 */
	public void createPartControl(Composite parent)
	{
		this._treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		this._contentProvider = new BundleViewContentProvider();
		this._labelProvider = new BundleViewLabelProvider();

		this._treeViewer.setContentProvider(this._contentProvider);
		this._treeViewer.setLabelProvider(_labelProvider);
		this._treeViewer.setInput(BundleManager.getInstance());
		this._treeViewer.setSorter(new ViewerSorter()
		{
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer,
			 * java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(Viewer viewer, Object e1, Object e2)
			{
				String name1 = null;
				String name2 = null;

				if (viewer != null && viewer instanceof ContentViewer)
				{
					IBaseLabelProvider provider = ((ContentViewer) viewer).getLabelProvider();

					if (provider instanceof ILabelProvider)
					{
						ILabelProvider labelProvider = (ILabelProvider) provider;

						name1 = labelProvider.getText(e1);
						name2 = labelProvider.getText(e2);
					}
				}

				if (name1 == null)
				{
					name1 = e1.toString();
				}
				if (name2 == null)
				{
					name2 = e2.toString();
				}

				return name1.compareTo(name2);
			}
		});

		// add selection provider
		this.getSite().setSelectionProvider(this._treeViewer);

		// listen to theme changes
		this.overrideTreeDrawing();
		this.hookContextMenu();
		this.addListeners();
		this.applyTheme();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose()
	{
		// remove selection provider
		this.getSite().setSelectionProvider(null);

		// remove load cycle listener
		BundleManager.getInstance().removeLoadCycleListener(this._loadCycleListener);

		// remove font change listener
		if (this._fontChangeListener != null)
		{
			JFaceResources.getFontRegistry().removeListener(this._fontChangeListener);
		}

		// remove theme change listener
		if (this._themeChangeListener != null)
		{
			new InstanceScope().getNode(ThemePlugin.PLUGIN_ID).removePreferenceChangeListener(this._themeChangeListener);
		}

		super.dispose();
	}

	/**
	 * fillContextMenu
	 * 
	 * @param manager
	 */
	private void fillContextMenu(IMenuManager manager)
	{
		ISelection selection = this._treeViewer.getSelection();

		if (selection instanceof TreeSelection)
		{
			TreeSelection treeSelection = (TreeSelection) selection;
			Object item = treeSelection.getFirstElement();

			if (item instanceof BaseNode)
			{
				BaseNode node = (BaseNode) item;
				Action[] actions = node.getActions();

				if (actions != null)
				{
					for (Action action : actions)
					{
						manager.add(action);
					}
				}
			}
		}
	}

	/**
	 * hookContextMenu
	 */
	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$

		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				fillContextMenu(manager);
			}
		});

		// Create menu.
		Menu menu = menuMgr.createContextMenu(this._treeViewer.getControl());
		this._treeViewer.getControl().setMenu(menu);

		// Register menu for extension.
		getSite().registerContextMenu(menuMgr, this._treeViewer);
	}

	/**
	 * listenForFontChanges
	 */
	private void listenForFontChanges()
	{
		this._fontChangeListener = new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				if (event.getProperty().equals(IThemeManager.VIEW_FONT_NAME))
				{
					IWorkbench workbench = null;

					try
					{
						workbench = PlatformUI.getWorkbench();
					}
					catch (IllegalStateException e)
					{
						ThemePlugin.logError(e);
					}

					if (workbench != null)
					{
						workbench.getDisplay().asyncExec(new Runnable()
						{
							public void run()
							{
								if (_treeViewer != null)
								{
									Tree tree = _treeViewer.getTree();

									_treeViewer.refresh();
									tree.redraw();
									tree.update();
								}

							}
						});
					}
				}
			}
		};

		JFaceResources.getFontRegistry().addListener(this._fontChangeListener);
	}

	/**
	 * listenForScriptChanges
	 */
	private void listenForScriptChanges()
	{
		this._loadCycleListener = new LoadCycleListener()
		{
			public void scriptLoaded(File script)
			{
				refresh();
			}

			public void scriptReloaded(File script)
			{
				refresh();
			}

			public void scriptUnloaded(File script)
			{
				refresh();
			}
		};

		BundleManager.getInstance().addLoadCycleListener(this._loadCycleListener);
	}

	/**
	 * listenForThemeChanges
	 */
	private void listenForThemeChanges()
	{
		this._themeChangeListener = new IPreferenceChangeListener()
		{
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					applyTheme();
				}
			}
		};

		new InstanceScope().getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(this._themeChangeListener);
	}

	/**
	 * overrideTreeDrawing
	 */
	private void overrideTreeDrawing()
	{
		final Tree tree = this._treeViewer.getTree();

		// Override selection color to match what is set in theme
		tree.addListener(SWT.EraseItem, new Listener()
		{
			public void handleEvent(Event event)
			{
				if ((event.detail & SWT.SELECTED) != 0)
				{
					Tree tree = (Tree) event.widget;
					int clientWidth = tree.getClientArea().width;

					GC gc = event.gc;
					Color oldBackground = gc.getBackground();

					ThemePlugin plugin = ThemePlugin.getDefault();
					ColorManager colorManager = plugin.getColorManager();
					IThemeManager themeManager = plugin.getThemeManager();

					gc.setBackground(colorManager.getColor(themeManager.getCurrentTheme().getSelectionAgainstBG()));
					gc.fillRectangle(0, event.y, clientWidth, event.height);
					gc.setBackground(oldBackground);

					event.detail &= ~SWT.SELECTED;
					event.detail &= ~SWT.BACKGROUND;
				}
			}
		});

		// Hack to force a specific row height and width based on font
		tree.addListener(SWT.MeasureItem, new Listener()
		{
			public void handleEvent(Event event)
			{
				Font font = JFaceResources.getFont(IThemeManager.VIEW_FONT_NAME);

				if (font == null)
				{
					font = JFaceResources.getTextFont();
				}

				if (font != null)
				{
					event.gc.setFont(font);

					FontMetrics metrics = event.gc.getFontMetrics();
					int height = metrics.getHeight() + 2;
					TreeItem item = (TreeItem) event.item;
					int width = event.gc.stringExtent(item.getText()).x + 24;
					event.height = height;

					if (width > event.width)
					{
						event.width = width;
					}
				}
			}
		});
	}

	/**
	 * refresh
	 */
	public void refresh()
	{
		UIJob job = new UIJob("Refresh Bundles View") //$NON-NLS-1$
		{
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				_treeViewer.refresh();

				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	/**
	 * setFocus
	 */
	public void setFocus()
	{
	}
}
