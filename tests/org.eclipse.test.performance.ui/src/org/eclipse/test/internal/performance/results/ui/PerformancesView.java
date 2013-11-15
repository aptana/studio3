/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.results.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.test.internal.performance.results.db.DB_Results;
import org.eclipse.test.internal.performance.results.model.BuildResultsElement;
import org.eclipse.test.internal.performance.results.model.PerformanceResultsElement;
import org.eclipse.test.internal.performance.results.utils.IPerformancesConstants;
import org.eclipse.test.internal.performance.results.utils.Util;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Abstract view for performance results.
 */
public abstract class PerformancesView extends ViewPart implements ISelectionChangedListener, IPreferenceChangeListener
{

	// Format
	static final NumberFormat DOUBLE_FORMAT = NumberFormat.getNumberInstance(Locale.US);
	static
	{
		DOUBLE_FORMAT.setMaximumFractionDigits(3);
	}

	// Graphic constants
	static final Display DEFAULT_DISPLAY = Display.getDefault();
	static final Color BLACK = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_BLACK);
	static final Color BLUE = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_BLUE);
	static final Color GREEN = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_GREEN);
	static final Color RED = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_RED);
	static final Color GRAY = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_GRAY);
	static final Color DARK_GRAY = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_DARK_GRAY);
	static final Color YELLOW = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_YELLOW);
	static final Color WHITE = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_WHITE);

	// Viewer filters
	static final ViewerFilter[] NO_FILTER = new ViewerFilter[0];
	final static ViewerFilter FILTER_BASELINE_BUILDS = new ViewerFilter()
	{
		public boolean select(Viewer v, Object parentElement, Object element)
		{
			if (element instanceof BuildResultsElement)
			{
				BuildResultsElement buildElement = (BuildResultsElement) element;
				return !buildElement.getName().startsWith(DB_Results.getDbBaselinePrefix());
			}
			return true;
		}
	};
	public final static ViewerFilter FILTER_NIGHTLY_BUILDS = new ViewerFilter()
	{
		public boolean select(Viewer v, Object parentElement, Object element)
		{
			if (element instanceof BuildResultsElement)
			{
				BuildResultsElement buildElement = (BuildResultsElement) element;
				return buildElement.getName().charAt(0) != 'N';
			}
			return true;
		}
	};
	final static ViewerFilter FILTER_OLD_BUILDS = new ViewerFilter()
	{
		public boolean select(Viewer v, Object parentElement, Object element)
		{
			if (element instanceof BuildResultsElement)
			{
				BuildResultsElement buildElement = (BuildResultsElement) element;
				return buildElement.isImportant();
			}
			return true;
		}
	};
	Set viewFilters = new HashSet();

	// SWT resources
	Shell shell;
	Display display;
	TreeViewer viewer;
	IPropertySheetPage propertyPage;

	// Directories
	File dataDir;
	File resultsDir = null;

	// Views
	IMemento viewState;

	// Results model information
	PerformanceResultsElement results;

	// Actions
	Action changeDataDir;
	Action filterBaselineBuilds;
	Action filterNightlyBuilds;
	Action filterOldBuilds;
	// Action dbConnection;

	// Eclipse preferences
	IEclipsePreferences preferences;

	/**
	 * Get a view from its ID.
	 * 
	 * @param viewId
	 *            The ID of the view
	 * @return The found view or <code>null</null> if not found.
	 */
	static IViewPart getWorkbenchView(String viewId)
	{
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
		int length = windows.length;
		for (int i = 0; i < length; i++)
		{
			IWorkbenchWindow window = windows[i];
			IWorkbenchPage[] pages = window.getPages();
			int pLength = pages.length;
			for (int j = 0; j < pLength; j++)
			{
				IWorkbenchPage page = pages[i];
				IViewPart view = page.findView(viewId);
				if (view != null)
				{
					return view;
				}
			}
		}
		return null;
	}

	/**
	 * The constructor.
	 */
	public PerformancesView()
	{

		// Get preferences
		this.preferences = new InstanceScope().getNode(IPerformancesConstants.PLUGIN_ID);

		// Init db constants
		int eclipseVersion = this.preferences.getInt(IPerformancesConstants.PRE_ECLIPSE_VERSION,
				IPerformancesConstants.DEFAULT_ECLIPSE_VERSION);
		String databaseLocation = this.preferences.get(IPerformancesConstants.PRE_DATABASE_LOCATION,
				IPerformancesConstants.NETWORK_DATABASE_LOCATION);
		boolean connected = this.preferences.getBoolean(IPerformancesConstants.PRE_DATABASE_CONNECTION,
				IPerformancesConstants.DEFAULT_DATABASE_CONNECTION);
		DB_Results.updateDbConstants(connected, eclipseVersion, databaseLocation);
		this.preferences.addPreferenceChangeListener(this);

		// Init tool tip
		setTitleToolTip();

		// Init milestones
		Util.initMilestones(this.preferences);
	}

	File changeDataDir(String lastBuild)
	{
		String localDataDir = this.preferences.get(IPerformancesConstants.PRE_LOCAL_DATA_DIR,
				"/Users/pinnamur/tests_performance");
		String filter = (this.dataDir == null) ? localDataDir : this.dataDir.getPath();
		File dir = this.dataDir;
		this.dataDir = changeDir(filter, "Select directory for data local files");
		boolean refresh = false;
		if (this.dataDir != null)
		{
			this.preferences.put(IPerformancesConstants.PRE_LOCAL_DATA_DIR, this.dataDir.getAbsolutePath());
			if (dir != null && dir.getPath().equals(this.dataDir.getPath()))
			{
				refresh = MessageDialog.openQuestion(this.shell, getTitleToolTip(),
						"Do you want to read local file again?");
			}
			else
			{
				refresh = true;
			}
			if (refresh)
			{
				// Confirm the read when there's a last build set
				if (lastBuild != null)
				{
					if (!MessageDialog.openConfirm(PerformancesView.this.shell, getTitleToolTip(),
							"Only builds before " + lastBuild
									+ " will be taken into account!\nDo you want to continue?"))
					{
						return null;
					}
				}
				refresh(lastBuild);
				return getSiblingView().dataDir = this.dataDir;
			}
		}
		return null;
	}

	/*
	 * Select a directory.
	 */
	File changeDir(String filter, String msg)
	{
		DirectoryDialog dialog = new DirectoryDialog(getSite().getShell(), SWT.OPEN);
		dialog.setText(getTitleToolTip());
		dialog.setMessage(msg);
		if (filter != null)
		{
			dialog.setFilterPath(filter);
		}
		String path = dialog.open();
		if (path != null)
		{
			File dir = new File(path);
			if (dir.exists() && dir.isDirectory())
			{
				return dir;
			}
		}
		return null;
	}

	/*
	 * Contribute actions to bars.
	 */
	void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		// Cache the shell and display.
		this.shell = parent.getShell();
		this.display = this.shell.getDisplay();
	}

	/*
	 * Fill the context menu.
	 */
	void fillContextMenu(IMenuManager manager)
	{
		// no default contextual action
	}

	/*
	 * Fill the filters drop-down menu.
	 */
	void fillFiltersDropDown(IMenuManager manager)
	{
		manager.add(this.filterBaselineBuilds);
		manager.add(this.filterNightlyBuilds);
	}

	/*
	 * Fill the local data drop-down menu
	 */
	void fillLocalDataDropDown(IMenuManager manager)
	{
		manager.add(this.changeDataDir);
	}

	/*
	 * Fill the local pull down menu.
	 */
	void fillLocalPullDown(IMenuManager manager)
	{

		// Filters menu
		MenuManager filtersManager = new MenuManager("Filters");
		fillFiltersDropDown(filtersManager);
		manager.add(filtersManager);

		// Local data menu
		MenuManager localDataManager = new MenuManager("Local data");
		fillLocalDataDropDown(localDataManager);
		manager.add(localDataManager);
	}

	/*
	 * Fill the local toolbar.
	 */
	void fillLocalToolBar(IToolBarManager manager)
	{
		// no default toolbar action
	}

	/*
	 * Filter non milestone builds action run.
	 */
	void filterNightlyBuilds(boolean filter, boolean updatePreference)
	{
		if (filter)
		{
			this.viewFilters.add(FILTER_NIGHTLY_BUILDS);
		}
		else
		{
			this.viewFilters.remove(FILTER_NIGHTLY_BUILDS);
		}
		this.preferences.putBoolean(IPerformancesConstants.PRE_FILTER_NIGHTLY_BUILDS, filter);
		updateFilters();
	}

	/*
	 * Filter non milestone builds action run.
	 */
	void filterOldBuilds(boolean filter, boolean updatePreference)
	{
		if (filter)
		{
			this.viewFilters.add(FILTER_OLD_BUILDS);
		}
		else
		{
			this.viewFilters.remove(FILTER_OLD_BUILDS);
		}
		this.preferences.putBoolean(IPerformancesConstants.PRE_FILTER_OLD_BUILDS, filter);
		updateFilters();
	}

	/*
	 * Finalize the viewer creation
	 */
	void finalizeViewerCreation()
	{
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		restoreState();
		updateFilters();
		this.viewer.setInput(getViewSite());
		this.viewer.addSelectionChangedListener(this);
	}

	/*
	 * (non-Javadoc) Method declared on IAdaptable
	 */
	public Object getAdapter(Class adapter)
	{
		if (adapter.equals(IPropertySheetPage.class))
		{
			return getPropertySheet();
		}
		return super.getAdapter(adapter);
	}

	/**
	 * Returns the property sheet.
	 */
	protected IPropertySheetPage getPropertySheet()
	{
		if (this.propertyPage == null)
		{
			this.propertyPage = new PropertySheetPage();
		}
		return this.propertyPage;
	}

	/*
	 * Get the sibling view (see subclasses).
	 */
	abstract PerformancesView getSiblingView();

	/*
	 * Hook the context menu.
	 */
	void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
		this.viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, this.viewer);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		super.init(site, memento);
		this.viewState = memento;
	}

	/*
	 * Init results
	 */
	void initResults()
	{
		this.results = PerformanceResultsElement.PERF_RESULTS_MODEL;
		if (this.results.isInitialized())
		{
			this.dataDir = getSiblingView().dataDir;
		}
		else
		{
			String localDataDir = this.preferences.get(IPerformancesConstants.PRE_LOCAL_DATA_DIR, null);
			if (localDataDir != null)
			{
				File dir = new File(localDataDir);
				if (dir.exists() && dir.isDirectory())
				{
					this.dataDir = dir;
					readLocalFiles(null);
				}
			}
		}
	}

	/*
	 * Make common actions to performance views.
	 */
	void makeActions()
	{

		// Change data dir action
		this.changeDataDir = new Action("&Read...")
		{
			public void run()
			{
				changeDataDir(null);
			}
		};
		this.changeDataDir.setToolTipText("Change the directory of the local data files");
		// this.changeDataDir.setImageDescriptor(ResultsElement.FOLDER_IMAGE_DESCRIPTOR);

		// Filter baselines action
		this.filterBaselineBuilds = new Action("&Baselines", IAction.AS_CHECK_BOX)
		{
			public void run()
			{
				if (isChecked())
				{
					PerformancesView.this.viewFilters.add(FILTER_BASELINE_BUILDS);
				}
				else
				{
					PerformancesView.this.viewFilters.remove(FILTER_BASELINE_BUILDS);
				}
				updateFilters();
			}
		};
		this.filterBaselineBuilds.setToolTipText("Filter baseline builds");

		// Filter baselines action
		this.filterNightlyBuilds = new Action("&Nightly", IAction.AS_CHECK_BOX)
		{
			public void run()
			{
				filterNightlyBuilds(isChecked(), true/* update preference */);
			}
		};
		this.filterNightlyBuilds.setToolTipText("Filter nightly builds");

		// Filter non-important builds action
		this.filterOldBuilds = new Action("&Old Builds", IAction.AS_CHECK_BOX)
		{
			public void run()
			{
				filterOldBuilds(isChecked(), true/* update preference */);
			}
		};
		this.filterOldBuilds.setChecked(false);
		this.filterOldBuilds
				.setToolTipText("Filter old builds (i.e. before last milestone) but keep all previous milestones)");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener#preferenceChange(org.eclipse
	 * .core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent)
	 */
	public void preferenceChange(PreferenceChangeEvent event)
	{
		// do nothing
	}

	/*
	 * Read local files
	 */
	void readLocalFiles(final String lastBuild)
	{

		// Create runnable to read local files
		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
			{
				try
				{
					monitor.beginTask("Read local files", 1000);
					PerformancesView.this.results.readLocal(PerformancesView.this.dataDir, monitor, lastBuild);
					monitor.done();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};

		// Execute the runnable with progress
		ProgressMonitorDialog readProgress = new ProgressMonitorDialog(getSite().getShell());
		try
		{
			readProgress.run(true, true, runnable);
		}
		catch (InvocationTargetException e)
		{
			// skip
		}
		catch (InterruptedException e)
		{
			// skip
		}
	}

	/*
	 * Refresh the views.
	 */
	void refresh(String lastBuild)
	{

		// Read local files
		readLocalFiles(lastBuild);

		// Refresh views
		refreshInput();
		getSiblingView().refreshInput();
	}

	/*
	 * Refresh the entire view by resetting its input.
	 */
	void refreshInput()
	{
		this.viewer.setInput(getViewSite());
		this.viewer.refresh();
	}

	/*
	 * Clear view content.
	 */
	void resetInput()
	{
		this.results.reset(null);
		this.viewer.setInput(getViewSite());
		this.viewer.refresh();
	}

	/*
	 * Restore the view state from the memento information.
	 */
	void restoreState()
	{

		// Filter baselines action state
		if (this.viewState != null)
		{
			Boolean filterBaselinesState = this.viewState.getBoolean(IPerformancesConstants.PRE_FILTER_BASELINE_BUILDS);
			boolean filterBaselinesValue = filterBaselinesState == null ? false : filterBaselinesState.booleanValue();
			this.filterBaselineBuilds.setChecked(filterBaselinesValue);
			if (filterBaselinesValue)
			{
				this.viewFilters.add(FILTER_BASELINE_BUILDS);
			}
			String dir = this.viewState.getString(IPerformancesConstants.PRE_WRITE_RESULTS_DIR);
			if (dir != null)
			{
				this.resultsDir = new File(dir);
			}
		}

		// Filter nightly builds action
		boolean checked = this.preferences.getBoolean(IPerformancesConstants.PRE_FILTER_NIGHTLY_BUILDS,
				IPerformancesConstants.DEFAULT_FILTER_NIGHTLY_BUILDS);
		this.filterNightlyBuilds.setChecked(checked);
		if (checked)
		{
			this.viewFilters.add(FILTER_NIGHTLY_BUILDS);
		}

		// Filter non important builds action state
		checked = this.preferences.getBoolean(IPerformancesConstants.PRE_FILTER_OLD_BUILDS,
				IPerformancesConstants.DEFAULT_FILTER_OLD_BUILDS);
		this.filterOldBuilds.setChecked(checked);
		if (checked)
		{
			this.viewFilters.add(FILTER_OLD_BUILDS);
		}
	}

	public void saveState(IMemento memento)
	{
		super.saveState(memento);
		memento.putBoolean(IPerformancesConstants.PRE_FILTER_BASELINE_BUILDS, this.filterBaselineBuilds.isChecked());
		try
		{
			this.preferences.flush();
		}
		catch (BackingStoreException e)
		{
			// ignore
		}
		if (this.resultsDir != null)
		{
			memento.putString(IPerformancesConstants.PRE_WRITE_RESULTS_DIR, this.resultsDir.getPath());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent
	 * )
	 */
	public void selectionChanged(SelectionChangedEvent event)
	{
		if (this.propertyPage != null)
		{
			this.propertyPage.selectionChanged(this, event.getSelection());
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		this.viewer.getControl().setFocus();
	}

	/*
	 * Set the view tooltip to reflect the DB connection kind.
	 */
	void setTitleToolTip()
	{
		String title = DB_Results.getDbTitle();
		if (title == null)
		{
			// DB is not connected
			int version = this.preferences.getInt(IPerformancesConstants.PRE_ECLIPSE_VERSION,
					IPerformancesConstants.DEFAULT_ECLIPSE_VERSION);
			title = "Eclipse v" + version + " - DB not connected";
		}
		setTitleToolTip(title);
	}

	/*
	 * Set/unset the database connection. void toogleDbConnection() { // Toogle DB connection and store new state
	 * boolean dbConnected = this.preferences.getBoolean(IPerformancesConstants.PRE_DATABASE_CONNECTION,
	 * IPerformancesConstants.DEFAULT_DATABASE_CONNECTION); DB_Results.DB_CONNECTION = !dbConnected;
	 * getSiblingView().dbConnection.setChecked(DB_Results.DB_CONNECTION);
	 * this.preferences.putBoolean(IPerformancesConstants.PRE_DATABASE_CONNECTION, DB_Results.DB_CONNECTION); // First
	 * close DB connection if (!DB_Results.DB_CONNECTION) { DB_Results.shutdown(); } // Read local files if any if
	 * (this.dataDir != null) { readLocalFiles(); } // Refresh views refreshInput(); getSiblingView().refreshInput(); }
	 */

	/*
	 * Update the filters from the stored list and apply them to the view.
	 */
	final void updateFilters()
	{
		ViewerFilter[] filters = new ViewerFilter[this.viewFilters.size()];
		this.viewFilters.toArray(filters);
		this.viewer.setFilters(filters);
	}

}