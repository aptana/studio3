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

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.test.internal.performance.results.model.BuildResultsElement;
import org.eclipse.test.internal.performance.results.model.ComponentResultsElement;
import org.eclipse.test.internal.performance.results.model.ConfigResultsElement;
import org.eclipse.test.internal.performance.results.model.DimResultsElement;
import org.eclipse.test.internal.performance.results.model.PerformanceResultsElement;
import org.eclipse.test.internal.performance.results.model.ResultsElement;
import org.eclipse.test.internal.performance.results.model.ScenarioResultsElement;
import org.eclipse.test.internal.performance.results.utils.IPerformancesConstants;
import org.eclipse.test.internal.performance.results.utils.Util;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * This view show for each performance machine the results of all builds
 * run since the beginning of the current version development.
 * <p>
 * Each machine results are show in a separate tab.
 * </p><p>
 * There's no real action available action from this view, only the possibility
 * to change how is displayed the line selection (full or only first column)
 * and also filter the results:
 * 	<ul>
 *	<li>Filter for builds:
 *		<ul>
 *		<li>Filter nightly:	hide the nightly builds (starting with 'N')</li>
 *		<li>Filter non-important builds:	hide all non-important builds, which means non-milestone builds and those after the last milestone</li>
 *		</ul>
 *	</li>
 *	</li>Filter for scenarios:
 *		<ul>
 *		<li>Filter non-fingerprints: hide the scenarios which are not in the fingerprints</li>
 *		</ul>
 *	</li>
 *	</ul>
 * </p><p>
 * Note that these filters are synchronized with the ones applied in the
 * {@link ComponentsView Components view}.
 * </p>
 *
 * @see ConfigTab Folder tab containing all the results for a configuration.
 */
public class ComponentResultsView extends ViewPart implements ISelectionChangedListener, IPreferenceChangeListener {

	// SWT resources
	CTabFolder tabFolder;

	// Model information
	ConfigTab[] tabs;
	ComponentResultsElement componentResultsElement;

	// Action
	Action fullLineSelection;
	Action filterAdvancedScenarios;
	Action filterOldBuilds;
	Action filterNightlyBuilds;
	ImageDescriptor fullSelectionImageDescriptor;

	// Views
	IMemento viewState;

	// Eclipse preferences
	IEclipsePreferences preferences;

/*
 * Default constructor:
 * 	- create the image descriptor
 * 	- register the view as a properties listener
 */
public ComponentResultsView() {
	this.fullSelectionImageDescriptor = ImageDescriptor.createFromFile(getClass(), "icallout_obj.gif");
	this.preferences = new InstanceScope().getNode(IPerformancesConstants.PLUGIN_ID);
	this.preferences.addPreferenceChangeListener(this);
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
	this.tabFolder = new CTabFolder(parent, SWT.BORDER);

	// Add results view as listener to viewer selection changes
	Display.getDefault().asyncExec(new Runnable() {
		public void run() {
			PerformancesView performancesView = (PerformancesView) PerformancesView.getWorkbenchView("org.eclipse.test.internal.performance.results.ui.ComponentsView");
			if (performancesView != null) {
				performancesView.viewer.addSelectionChangedListener(ComponentResultsView.this);
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
	if (this.componentResultsElement == null) return;
	PerformanceResultsElement performanceResultsElement = (PerformanceResultsElement) this.componentResultsElement.getParent(null);
	String[] configNames = performanceResultsElement.getConfigs();
	String[] configDescriptions = performanceResultsElement.getConfigDescriptions();
	int length = configNames.length;
	this.tabs = new ConfigTab[length];
	for (int i=0; i<length; i++) {
		this.tabs[i] = new ConfigTab(configNames[i], configDescriptions[i]);
	}
	for (int i=0; i<this.tabs.length; i++) {
		CTabItem item = new CTabItem (this.tabFolder, SWT.NONE);
		item.setText (this.tabs[i].getTabText ());
		item.setControl (this.tabs[i].createTabFolderPage(this.componentResultsElement, this.tabFolder, this.fullLineSelection.isChecked()));
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
	JFaceResources.getResources().destroyImage(this.fullSelectionImageDescriptor);
	super.dispose();
}

/*
 * Fill the filters drop-down menu with:
 * 	- filter nightly builds
 * 	- filter non-milestone builds
 *	- filter non-fingerprint scenarios
 */
void fillFiltersDropDown(IMenuManager manager) {
	manager.add(this.filterNightlyBuilds);
	manager.add(this.filterOldBuilds);
	manager.add(new Separator());
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
	manager.add(this.fullLineSelection);
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

	// Full line selection action
	this.fullLineSelection = new Action("", IAction.AS_CHECK_BOX) {
		public void run() {
			resetTabFolders(false/*refresh*/);
		}
	};
	this.fullLineSelection.setImageDescriptor(this.fullSelectionImageDescriptor);
	this.fullLineSelection.setToolTipText("Full line selection");
//	this.fullLineSelection.setChecked(true);

	// Filter non-fingerprints action
	this.filterAdvancedScenarios = new Action("Advanced &Scenarios", IAction.AS_CHECK_BOX) {
		public void run() {
			ComponentResultsView.this.preferences.putBoolean(IPerformancesConstants.PRE_FILTER_ADVANCED_SCENARIOS, isChecked());
			resetTabFolders(false/*refresh*/);
        }
	};
	this.filterAdvancedScenarios.setChecked(true);
	this.filterAdvancedScenarios.setToolTipText("Filter advanced scenarios (i.e. not fingerprint ones)");

	// Filter non-important builds action
	this.filterOldBuilds = new Action("&Old Builds", IAction.AS_CHECK_BOX) {
		public void run() {
			ComponentResultsView.this.preferences.putBoolean(IPerformancesConstants.PRE_FILTER_OLD_BUILDS, isChecked());
			resetTabFolders(false/*refresh*/);
		}
	};
	this.filterOldBuilds.setChecked(false);
	this.filterOldBuilds.setToolTipText("Filter old builds (i.e. before last milestone) but keep all previous milestones)");

	// Filter nightly action
	this.filterNightlyBuilds = new Action("&Nightly", IAction.AS_CHECK_BOX) {
		public void run() {
			ComponentResultsView.this.preferences.putBoolean(IPerformancesConstants.PRE_FILTER_NIGHTLY_BUILDS, isChecked());
			resetTabFolders(false/*refresh*/);
		}
	};
	this.filterNightlyBuilds.setToolTipText("Filter nightly builds");
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

	// Filter non-milestone change
	if (propertyName.equals(IPerformancesConstants.PRE_FILTER_OLD_BUILDS)) {
		boolean checked = newValue == null ? IPerformancesConstants.DEFAULT_FILTER_OLD_BUILDS : "true".equals(newValue);
		this.filterOldBuilds.setChecked(checked);
		resetTabFolders(false/*refresh*/);
	}

	// Filter nightly builds change
	if (propertyName.equals(IPerformancesConstants.PRE_FILTER_NIGHTLY_BUILDS)) {
		boolean checked = newValue == null ? IPerformancesConstants.DEFAULT_FILTER_NIGHTLY_BUILDS : "true".equals(newValue);
		this.filterNightlyBuilds.setChecked(checked);
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
			tabItems[i].setControl(this.tabs [i].createTabFolderPage(this.componentResultsElement, this.tabFolder, this.fullLineSelection.isChecked()));
		}
	}

	// Set the part name when possible
	if (this.componentResultsElement != null) {
		setPartName(Util.componentDisplayName(this.componentResultsElement.getName()));
	}

	// If this is the first display then look for the first error to set the selection on it
	if (init)  {
		if (this.componentResultsElement != null) {
			// If the component has
			if (this.componentResultsElement.hasError()) {
				ResultsElement[] children = this.componentResultsElement.getChildren(); // get scenarios
				int childrenLength = children.length;
				for (int s=0; s<childrenLength; s++) {
					if (children[s].hasError()) {
						children = children[s].getChildren(); // get configs
						for (int c=0; c<childrenLength; c++) {
							if (children[c].hasError()) {
								tabIndex = c;
								break;
							}
						}
						break;
					}
				}
			}
		}
		lineIndex = 0;
	}

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

	// Filter baselines action state
	if (this.viewState != null) {
		Boolean state = this.viewState.getBoolean(IPerformancesConstants.PRE_FULL_LINE_SELECTION);
		boolean fullLine = state == null ? true : state.booleanValue();
		this.fullLineSelection.setChecked(fullLine);
	}

	// Filter non fingerprints action state
	boolean checked = this.preferences.getBoolean(IPerformancesConstants.PRE_FILTER_ADVANCED_SCENARIOS, IPerformancesConstants.DEFAULT_FILTER_ADVANCED_SCENARIOS);
	this.filterAdvancedScenarios.setChecked(checked);

	// Filter nightly builds action
	checked = this.preferences.getBoolean(IPerformancesConstants.PRE_FILTER_NIGHTLY_BUILDS, IPerformancesConstants.DEFAULT_FILTER_NIGHTLY_BUILDS);
	this.filterNightlyBuilds.setChecked(checked);

	// Filter non important builds action state
	checked = this.preferences.getBoolean(IPerformancesConstants.PRE_FILTER_OLD_BUILDS, IPerformancesConstants.DEFAULT_FILTER_OLD_BUILDS);
	this.filterOldBuilds.setChecked(checked);
}

/*
 * (non-Javadoc)
 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
 */
public void saveState(IMemento memento) {
	super.saveState(memento);
	memento.putBoolean(IPerformancesConstants.PRE_FULL_LINE_SELECTION, this.fullLineSelection.isChecked());
}

/*
 * (non-Javadoc)
 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
 */
public void selectionChanged(SelectionChangedEvent event) {
	ResultsElement selectedElement = (ResultsElement) ((TreeSelection) event.getSelection()).getFirstElement();
	ComponentResultsElement componentElement = null;
	ScenarioResultsElement scenarioResultsElement = null;
	ConfigResultsElement configResultsElement = null;
	BuildResultsElement buildResultsElement = null;
	if (selectedElement instanceof ComponentResultsElement) {
		componentElement = (ComponentResultsElement) selectedElement;
	} else if (selectedElement instanceof ScenarioResultsElement) {
		scenarioResultsElement = (ScenarioResultsElement) selectedElement;
		componentElement = (ComponentResultsElement) scenarioResultsElement.getParent(null);
	} else if (selectedElement instanceof ConfigResultsElement) {
		configResultsElement = (ConfigResultsElement) selectedElement;
		scenarioResultsElement = (ScenarioResultsElement) configResultsElement.getParent(null);
		componentElement = (ComponentResultsElement) scenarioResultsElement.getParent(null);
	} else if (selectedElement instanceof BuildResultsElement) {
		buildResultsElement = (BuildResultsElement) selectedElement;
		configResultsElement = (ConfigResultsElement) buildResultsElement.getParent(null);
		scenarioResultsElement = (ScenarioResultsElement) configResultsElement.getParent(null);
		componentElement = (ComponentResultsElement) scenarioResultsElement.getParent(null);
	} else if (selectedElement instanceof DimResultsElement) {
		buildResultsElement = (BuildResultsElement) selectedElement.getParent(null);
		configResultsElement = (ConfigResultsElement) buildResultsElement.getParent(null);
		scenarioResultsElement = (ScenarioResultsElement) configResultsElement.getParent(null);
		componentElement = (ComponentResultsElement) scenarioResultsElement.getParent(null);
	}
	if (componentElement != this.componentResultsElement) {
		this.componentResultsElement = componentElement;
		if (componentElement == null || this.componentResultsElement.getChildren(null).length > 0) {
			resetTabFolders(true);
		}
	}
	if (configResultsElement != null) {
		ConfigTab configTab = this.tabs[this.tabFolder.getSelectionIndex()];
		if (!configResultsElement.getName().equals(configTab.configName)) {
			int length = this.tabs.length;
			for (int i=0; i<length; i++) {
				if (this.tabs[i].configName.equals(configResultsElement.getName())) {
					this.tabFolder.setSelection(i);
				}
			}
		}
		if (buildResultsElement != null) {
			configTab = this.tabs[this.tabFolder.getSelectionIndex()];
			configTab.select(buildResultsElement);
		}
	}
}

/*
 * (non-Javadoc)
 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
 */
public void setFocus() {
	// do nothing
}

/*
 * Set the name of the part.
 * This name is built from the name of the component selected in the Components view.
 * The rules to build the name are:
 * 	1. If the component name does not start then the part name is just "'<component name>' results"
 * 	2. Otherwise, remove "org.eclipse." form the component name and count the tokens separated by a dot ('.')
 * 		a. if there's only one remaining token, then the part name is "Platform/"
 * 			+ "<token uppercased>" if token is less than 3 characters,"<token with first char uppercased>" otherwise
 * 		b. otherwise then the part name is "<first token uppercased>"
 * 			+ for each followed additional token:
 * 				"<token uppercased>" if token is less than 3 characters,"<token with first char uppercased>" otherwise
 * 			+ " results"
 * E.g.
 * 	- org.eclipse.ui -> "Platform/UI"
 * 	- org.eclipse.swt -> "Platform/SWT"
 * 	- org.eclipse.team -> "Platform/Team"
 * 	- org.eclipse.jdt.ui -> "JDT/UI"
 * 	- org.eclipse.jdt.core -> "JDT/Core"
 * 	- org.eclipse.pde.api.tools -> "PDE/API Tools"
 *
protected void setPartName() {
	String componentName = this.componentResultsElement.getName();
	String partName;
	StringBuffer buffer = null;
	if (componentName.startsWith(ORG_ECLIPSE)) {
		partName = componentName.substring(ORG_ECLIPSE.length());
		StringTokenizer tokenizer = new StringTokenizer(partName, ".");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (buffer == null) {
				if (tokenizer.hasMoreTokens()) {
					buffer = new StringBuffer("'"+token.toUpperCase());
					buffer.append('/');
				} else {
					buffer = new StringBuffer("'Platform/");
					if (token.length() > 3) {
						buffer.append(Character.toUpperCase(token.charAt(0)));
						buffer.append(token.substring(1));
					} else {
						buffer.append(token.toUpperCase());
					}
				}
			} else {
				if (token.length() > 3) {
					buffer.append(Character.toUpperCase(token.charAt(0)));
					buffer.append(token.substring(1));
				} else {
					buffer.append(token.toUpperCase());
				}
				if (tokenizer.hasMoreTokens()) buffer.append(' ');
			}
		}
	} else {
		buffer = new StringBuffer("'");
		buffer.append(componentName);
		buffer.append("'");
	}
	buffer.append("' results");
	setPartName(buffer.toString());
}
*/

}
