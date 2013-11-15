/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
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
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.test.internal.performance.results.model.BuildResultsElement;
import org.eclipse.test.internal.performance.results.model.PerformanceResultsElement;
import org.eclipse.test.internal.performance.results.utils.IPerformancesConstants;
import org.eclipse.test.internal.performance.results.utils.Util;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * This view shows the difference between two selected builds.
 * <p>
 * Each component are shown in a separate tab.
 * </p><p>
 * The displayed results can be written in a file, either all component or only
 * the current one.
 * </p><p>
 * It's also possible to filter the scenario to display only the fingerprint ones.
 * <br>
 * Note that this filter is synchronized with the one applied in the
 * {@link ComponentsView Components view}.
 * </p>
 *
 * @see ConfigTab Folder tab containing all the results for a configuration.
 */
public class BuildsComparisonView extends ViewPart implements ISelectionChangedListener, IPreferenceChangeListener {

	// SWT resources
	Shell shell;
	CTabFolder tabFolder;

	// Model information
	BuildsComparisonTab[] tabs;
	PerformanceResultsElement results;

	// Action
	Action filterAdvancedScenarios;
	Action writeComparison;

	// Write Status
	static int WRITE_STATUS;

	// Views
	BuildsView buildsView;
	IMemento viewState;

	// Eclipse preferences
	IEclipsePreferences preferences;

	// Comparison
	String currentBuild;
	String referenceBuild;

/*
 * Default constructor:
 * 	- create the image descriptor
 * 	- register the view as a properties listener
 */
public BuildsComparisonView() {
	this.preferences = new InstanceScope().getNode(IPerformancesConstants.PLUGIN_ID);
	this.preferences.addPreferenceChangeListener(this);
	// TODO should be done only once!
	Util.initMilestones(this.preferences);
}

/*
 * Contribute the local tools bar and the pull-down menu to the action bars.
 */
void contributeToActionBars() {
	IActionBars bars = getViewSite().getActionBars();
	fillLocalPullDown(bars.getMenuManager());
	fillLocalToolBar(bars.getToolBarManager());
}

/*
 * (non-Javadoc)
 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
 */
public void createPartControl(Composite parent) {

	// Create the tab folder
	this.shell = parent.getShell ();
	this.tabFolder = new CTabFolder(parent, SWT.BORDER);

	// Add results view as listener to viewer selection changes
	Display.getDefault().asyncExec(new Runnable() {
		public void run() {
			PerformancesView performancesView = (PerformancesView) PerformancesView.getWorkbenchView("org.eclipse.test.internal.performance.results.ui.ComponentsView");
			if (performancesView != null) {
				performancesView.viewer.addSelectionChangedListener(BuildsComparisonView.this);
			}
		}
	});

	// Set actions
	PlatformUI.getWorkbench().getHelpSystem().setHelp(this.tabFolder, "org.eclipse.test.performance.ui.results");
	makeActions();
	contributeToActionBars();

	// Restore state
	restoreState();

	// Create tabs
	createTabs();

	// Set selections (tab and line)
	this.tabFolder.setSimple(false);
}

/*
 * Create the tab folder pages. There's one tab per performance machine.
 * The list of these machines is got from the DB_Results contants.
 */
void createTabs() {
	if (this.currentBuild == null || this.referenceBuild == null) return;
	PerformanceResultsElement performanceResultsElement = getBuildsView().results;
	String[] components = performanceResultsElement.getComponents();
	int length = components.length;
	this.tabs = new BuildsComparisonTab[length];
	for (int i=0; i<length; i++) {
		this.tabs[i] = new BuildsComparisonTab(components[i]);
	}
	for (int i=0; i<this.tabs.length; i++) {
		CTabItem item = new CTabItem (this.tabFolder, SWT.NONE);
		item.setText (this.tabs[i].getTabText ());
		item.setControl (this.tabs[i].createTabFolderPage(this));
		item.setData (this.tabs[i]);
	}
	this.tabFolder.setSelection(0);
}

/*
 * (non-Javadoc)
 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
 */
public void dispose() {
	this.tabFolder.dispose();
	int length = this.tabs==null ? 0 : this.tabs.length;
	for (int i=0; i<length; i++) {
		this.tabs[i].dispose();
	}
	super.dispose();
}

/*
 * Fill the filters drop-down menu with:
 * 	- filter nightly builds
 * 	- filter non-milestone builds
 *	- filter non-fingerprint scenarios
 */
void fillFiltersDropDown(IMenuManager manager) {
	manager.add(this.filterAdvancedScenarios);
}

/*
 * Fill the local pull down menu.
 */
void fillLocalPullDown(IMenuManager manager) {
	MenuManager filtersManager= new MenuManager("Filters");
	fillFiltersDropDown(filtersManager);
	manager.add(filtersManager);
}

/*
 * Fill the local tool bar with:
 * 	- change line selection display
 */
void fillLocalToolBar(IToolBarManager manager) {
	manager.add(this.writeComparison);
}

/*
 * Return the components results view.
 */
PerformanceResultsElement getResults() {
	if (this.results == null) {
		this.results = getBuildsView().results;
	}
	return this.results;
}

/*
 * Return the components results view.
 */
BuildsView getBuildsView() {
	if (this.buildsView == null) {
		this.buildsView = (BuildsView) PerformancesView.getWorkbenchView("org.eclipse.test.internal.performance.results.ui.BuildsView");
	}
	return this.buildsView;
}

/*
 * (non-Javadoc)
 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
 */
public void init(IViewSite site, IMemento memento) throws PartInitException {
	super.init(site, memento);
	this.viewState = memento;
}

/*
 * Make the actions of the view:
 * 	- change table line selection display
 * 	- filter nightly builds
 * 	- filter non-milestone builds
 *	- filter non-fingerprint scenarios
 */
void makeActions() {

	// Filter non-fingerprints action
	this.filterAdvancedScenarios = new Action("Advanced &Scenarios", IAction.AS_CHECK_BOX) {
		public void run() {
			BuildsComparisonView.this.preferences.putBoolean(IPerformancesConstants.PRE_FILTER_ADVANCED_SCENARIOS, isChecked());
			resetTabFolders(false/*refresh*/);
        }
	};
	this.filterAdvancedScenarios.setChecked(true);
	this.filterAdvancedScenarios.setToolTipText("Filter advanced scenarios (i.e. not fingerprint ones)");

	// Write comparison
	this.writeComparison = new Action("Write comparison") {
		public void run() {

			// Get write directory
			BuildsView bView = getBuildsView();
			String filter = (bView.resultsDir == null) ? null : bView.resultsDir.getPath();
			final File writeDir = bView.changeDir(filter, "Select a directory to write the comparison between two builds");
			if (writeDir != null) {
				writeComparison(writeDir);
			}
        }
	};
	this.writeComparison.setEnabled(false);
	this.writeComparison.setToolTipText("Write comparison between two builds");
}

/* (non-Javadoc)
 * @see org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener#preferenceChange(org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent)
 */
public void preferenceChange(PreferenceChangeEvent event) {
	String propertyName = event.getKey();
	Object newValue = event.getNewValue();

	// Filter non-fingerprints change
	if (propertyName.equals(IPerformancesConstants.PRE_FILTER_ADVANCED_SCENARIOS)) {
		boolean checked = newValue == null ? IPerformancesConstants.DEFAULT_FILTER_ADVANCED_SCENARIOS : "true".equals(newValue);
		this.filterAdvancedScenarios.setChecked(checked);
		resetTabFolders(false/*refresh*/);
	}
}

/*
 * Reset the table tab folders by re-create all the pages.
 * Selections are set onto the first found error if this is the first tab creation (typically on a component change event from the ComponentsView)
 * or to the previous one if this is just a refresh.
 */
void resetTabFolders(boolean init) {

	// Store current indexes
	int tabIndex = this.tabFolder.getSelectionIndex();
	int lineIndex = tabIndex<0 ? -1 : this.tabs[tabIndex].table.getSelectionIndex();

	// Create tab folders
	CTabItem[] tabItems = this.tabFolder.getItems();
	int length = tabItems.length;
	if (length == 0) {
		createTabs();
	} else {
		for (int i=0; i<length; i++) {
			tabItems[i].setControl(this.tabs [i].createTabFolderPage(this));
		}
	}

	// Set part name
	setPartName(this.currentBuild+" vs. "+this.referenceBuild);

	// Set the selection
	if (tabIndex >= 0 && lineIndex >= 0) {
		this.tabFolder.setSelection(tabIndex);
		Table table = this.tabs[tabIndex].table;
		table.setSelection(lineIndex);
	}
}

/*
 * Restore the view state from the memento information.
 */
void restoreState() {

	// Filter non fingerprints action state
	boolean checked = this.preferences.getBoolean(IPerformancesConstants.PRE_FILTER_ADVANCED_SCENARIOS, IPerformancesConstants.DEFAULT_FILTER_ADVANCED_SCENARIOS);
	this.filterAdvancedScenarios.setChecked(checked);
}

/*
 * (non-Javadoc)
 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
 */
public void saveState(IMemento memento) {
	super.saveState(memento);
}

/*
 * (non-Javadoc)
 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
 */
public void selectionChanged(SelectionChangedEvent event) {
	final Object[] selection = ((TreeSelection) event.getSelection()).toArray();
	String firstBuildResults = null;
	String secondBuildResults = null;
	if (selection.length == 2) {
		this.tabFolder.setVisible(true);
		if (selection[0] instanceof BuildResultsElement) {
			firstBuildResults = ((BuildResultsElement) selection[0]).getName();
		}
		if (selection[1] instanceof BuildResultsElement) {
			secondBuildResults = ((BuildResultsElement) selection[1]).getName();
		}
		if (!firstBuildResults.equals(this.currentBuild) || !secondBuildResults.equals(this.referenceBuild)) {
			this.currentBuild = firstBuildResults;
			this.referenceBuild = secondBuildResults;
			resetTabFolders(true);
		}
		this.writeComparison.setEnabled(true);
	} else {
		this.writeComparison.setEnabled(false);
		this.tabFolder.setVisible(false);
	}
}

/*
 * (non-Javadoc)
 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
 */
public void setFocus() {
	// do nothing
}

protected void writeComparison(File writeDir) {
	getBuildsView().resultsDir = writeDir;
	writeDir = new File(writeDir, "values");
	if (this.preferences.getBoolean(IPerformancesConstants.PRE_FILTER_ADVANCED_SCENARIOS, false)) {
		writeDir = new File(writeDir, "fingerprints");
	} else {
		writeDir = new File(writeDir, "all");
	}
	writeDir.mkdirs();
	String buildDate = this.currentBuild.substring(1);
	String buildPrefix = "Comparison" + buildDate + "_" + this.currentBuild.charAt(0);
	File resultsFile = new File(writeDir, buildPrefix+".html");
	if (resultsFile.exists()) {
		int i=0;
		File saveDir = new File(writeDir, "save");
		saveDir.mkdir();
		while (true) {
			String newFileName = buildPrefix+"_";
			if (i<10) newFileName += "0";
			newFileName += i;
			File renamedFile = new File(saveDir, newFileName+".html");
			if (resultsFile.renameTo(renamedFile)) {
				break;
			}
			i++;
		}
	}
	this.results.writeComparison(resultsFile, this.currentBuild, this.referenceBuild);
}

}
