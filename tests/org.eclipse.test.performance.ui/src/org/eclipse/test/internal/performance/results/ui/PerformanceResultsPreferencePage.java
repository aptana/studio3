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
import java.util.Iterator;

import org.osgi.service.prefs.BackingStoreException;

import org.eclipse.test.internal.performance.PerformanceTestPlugin;
import org.eclipse.test.internal.performance.results.db.DB_Results;
import org.eclipse.test.internal.performance.results.utils.IPerformancesConstants;
import org.eclipse.test.internal.performance.results.utils.Util;
import org.eclipse.test.performance.ui.UiPlugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Defines the 'Performances' preferences page.
 */
public class PerformanceResultsPreferencePage extends PreferencePage
	implements IWorkbenchPreferencePage, SelectionListener, ModifyListener, IPerformancesConstants {

	private Button mVersionRadioButton;
	private Button dVersionRadionButton;
	private CCombo databaseLocationCombo;
	private Button dbConnectionCheckBox;
	private Button dbLocalBrowseButton;
	private Button dbRelengRadioButton;
	private Button dbLocalRadioButton;
	private CCombo defaultDimensionCombo;
//	private CCombo lastBuildCombo;
	private List resultsDimensionsList;
	private CCombo milestonesCombo;
	private Label dbLocationLabel;

	// Status SWT objects
	private Button statusValuesCheckBox;
	private Button statusErrorNoneRadioButton;
	private Button statusErrorNoticeableRadioButton;
	private Button statusErrorSuspiciousRadioButton;
	private Button statusErrorWeirdRadioButton;
	private Button statusErrorInvalidRadioButton;
	private Button statusSmallBuildValueCheckBox;
	private Button statusSmallDeltaValueCheckBox;
	private Button statusStatisticNoneRadioButton;
	private Button statusStatisticErraticRadioButton;
	private Button statusStatisticUnstableRadioButton;
	private Text statusBuildsToConfirm;
	private Text comparisonThresholdFailure;
	private Text comparisonThresholdError;
	private Text comparisonThresholdImprovement;

	// TODO See whether config descriptors need to be set as preferences or not...
	// private Table configDescriptorsTable;

	private BuildsView buildsView;

/**
 * Utility method that creates a push button instance and sets the default
 * layout data.
 *
 * @param parent
 *            the parent for the new button
 * @param label
 *            the label for the new button
 * @return the newly-created button
 */
private Button createCheckBox(Composite parent, String label) {
	Button button = new Button(parent, SWT.CHECK);
	button.setText(label);
	button.addSelectionListener(this);
	GridData data = new GridData();
	data.horizontalAlignment = GridData.FILL;
	data.horizontalSpan = 5;
	button.setLayoutData(data);
	return button;
}

/**
 * Create a text field specific for this application
 *
 * @param parent
 *            the parent of the new text field
 * @return the new text field
 */
private CCombo createCombo(Composite parent) {
	CCombo combo= new CCombo(parent, SWT.BORDER);
	combo.addModifyListener(this);
	GridData data = new GridData();
	data.horizontalSpan = 3;
	data.horizontalAlignment = GridData.FILL;
	data.grabExcessHorizontalSpace = true;
	data.verticalAlignment = GridData.CENTER;
	data.grabExcessVerticalSpace = false;
	combo.setLayoutData(data);
	return combo;
}


/**
 * Creates composite control and sets the default layout data.
 *
 * @param parent
 *            the parent of the new composite
 * @param numColumns
 *            the number of columns for the new composite
 * @param hSpan TODO
 * @return the newly-created coposite
 */
private Composite createComposite(Composite parent, int numColumns, int hSpan) {
	Composite composite = new Composite(parent, SWT.NULL);

	// GridLayout
	GridLayout layout = new GridLayout();
	layout.numColumns = numColumns;
	composite.setLayout(layout);

	// GridData
	GridData data = new GridData();
	data.verticalAlignment = GridData.FILL;
	data.horizontalAlignment = GridData.FILL;
	data.horizontalSpan = hSpan;
	composite.setLayoutData(data);
	return composite;
}

/**
 * (non-Javadoc) Method declared on PreferencePage
 */
protected Control createContents(Composite parent) {

	this.buildsView = (BuildsView) PerformancesView.getWorkbenchView("org.eclipse.test.internal.performance.results.ui.BuildsView");
	if (this.buildsView == null) {
		Label errorLabel = createLabel(parent, "No performances preferences can be set because the build view has not been created yet!", false);
		errorLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
	} else {
		// Eclipse version choice
		Composite composite_eclipseVersion = createComposite(parent, 5, 1);
		createLabel(composite_eclipseVersion, "Eclipse version", false);
		Composite composite_versionChoice = createComposite(composite_eclipseVersion, 5, 1);
		this.mVersionRadioButton = createRadioButton(composite_versionChoice, "v"+ECLIPSE_MAINTENANCE_VERSION);
		this.dVersionRadionButton = createRadioButton(composite_versionChoice, "v"+ECLIPSE_DEVELOPMENT_VERSION);

		// Database location
		Composite compositeDatabase = createComposite(parent, 5, 1);
		Group databaseGroup = createGroup(compositeDatabase, "Database", 5);
		Composite compositeDatabaseConnection = createComposite(databaseGroup, 3, 5);
		this.dbConnectionCheckBox = createCheckBox(compositeDatabaseConnection, "Connected");
		this.dbRelengRadioButton = createRadioButton(compositeDatabaseConnection, "Releng");
		this.dbLocalRadioButton = createRadioButton(compositeDatabaseConnection, "Local");
		this.dbLocationLabel = createLabel(databaseGroup, "Location", false);
		this.databaseLocationCombo = createCombo(databaseGroup);
		this.databaseLocationCombo.setEditable(false);
	    this.dbLocalBrowseButton = createPushButton(databaseGroup, "Browse");

		// Status
		Composite compositeStatus = createComposite(parent, 1, 3);
		Group statusGroup = createGroup(compositeStatus, "Status", 1);
		this.statusValuesCheckBox = createCheckBox(statusGroup, "Values");
		this.statusValuesCheckBox.setToolTipText("Include numbers while writing status");
		Group statusErrorGroup = createGroup(statusGroup, "Error level", 5);
		statusErrorGroup.setToolTipText("Exclude from the written status failures depending on their build result error...");
		this.statusErrorNoneRadioButton = createRadioButton(statusErrorGroup, "None");
		this.statusErrorNoneRadioButton.setToolTipText("Do not exclude failures if they have a noticeable error");
		this.statusErrorInvalidRadioButton = createRadioButton(statusErrorGroup, "Invalid");
		this.statusErrorInvalidRadioButton.setToolTipText("Exclude all invalid failures (i.e. result error is over 100%)");
		this.statusErrorWeirdRadioButton = createRadioButton(statusErrorGroup, "Weird");
		this.statusErrorWeirdRadioButton.setToolTipText("Exclude all weird failures (i.e. result error is over 50%)");
		this.statusErrorSuspiciousRadioButton = createRadioButton(statusErrorGroup, "Suspicious");
		this.statusErrorSuspiciousRadioButton.setToolTipText("Exclude all suspicious failures (i.e. result error is over 25%)");
		this.statusErrorNoticeableRadioButton = createRadioButton(statusErrorGroup, "Noticeable");
		this.statusErrorNoticeableRadioButton.setToolTipText("Exclude all failures which have a noticeable error (i.e result error is over 3%)");
		Group statusSmallGroup = createGroup(statusGroup, "Small value", 5);
		statusErrorGroup.setToolTipText("Exclude from the written status failures depending on their value");
		this.statusSmallBuildValueCheckBox = createCheckBox(statusSmallGroup, "Build value");
		this.statusSmallBuildValueCheckBox.setToolTipText("Exclude all failures which have a build result value smaller than 100ms");
		this.statusSmallDeltaValueCheckBox = createCheckBox(statusSmallGroup, "Delta value");
		this.statusSmallDeltaValueCheckBox.setToolTipText("Exclude all failures which have a delta result value smaller than 100ms");
		Group statusStatisticsGroup = createGroup(statusGroup, "Statistics", 5);
		statusStatisticsGroup.setToolTipText("Exclude from the written status failures depending on build results statistics...");
		this.statusStatisticNoneRadioButton = createRadioButton(statusStatisticsGroup, "None");
		this.statusStatisticNoneRadioButton.setToolTipText("Do not exclude failures which have bad baseline results statistics (i.e. variation is over 10%)");
		this.statusStatisticUnstableRadioButton = createRadioButton(statusStatisticsGroup, "Unstable");
		this.statusStatisticUnstableRadioButton.setToolTipText("Exclude all failures which have unstable baseline results statistics (i.e. variation is between 10% and 20%)");
		this.statusStatisticErraticRadioButton = createRadioButton(statusStatisticsGroup, "Erratic");
		this.statusStatisticErraticRadioButton.setToolTipText("Exclude all failures which have erratic baseline results statistics (i.e. variation is over 20%)");
		createLabel(statusGroup, "Builds to confirm:", false);
		this.statusBuildsToConfirm = createTextField(statusGroup);
		this.statusBuildsToConfirm.setToolTipText("The number of previous builds to take into account to confirm a regression");

		// Comparison
		Composite compositeComparison = createComposite(parent, 1, 3);
		Group comparisonGroup = createGroup(compositeComparison, "Comparison", 1);
		Group thresholdsGroup = createGroup(comparisonGroup, "Thresholds", 6);
//		Composite compositeFailureThreshold = createComposite(comparisonGroup, 2, 2);
		createLabel(thresholdsGroup, "Failure:", false);
		this.comparisonThresholdFailure = createTextField(thresholdsGroup);
		this.comparisonThresholdFailure.setToolTipText("The threshold in percentage to report a failure");
		createLabel(thresholdsGroup, "Error:", false);
		this.comparisonThresholdError = createTextField(thresholdsGroup);
		this.comparisonThresholdError.setToolTipText("The threshold in percentage to report an error");
		createLabel(thresholdsGroup, "Improvement:", false);
		this.comparisonThresholdImprovement = createTextField(thresholdsGroup);
		this.comparisonThresholdImprovement.setToolTipText("The threshold in percentage to report an improvement");

		// Milestones
		Composite compositeMilestones = createComposite(parent, 3, 1);
		createLabel(compositeMilestones, "Milestones", false);
		this.milestonesCombo = createCombo(compositeMilestones);
		this.milestonesCombo.setToolTipText("Enter the date of the milestone as yyyymmddHHMM");

		// Default dimension layout
		StringBuffer tooltip = new StringBuffer("Select the default dimension which will be used for performance results\n");
		tooltip.append("When changed, the new selected dimension is automatically added to the dimensions list below...");
		String tooltipText = tooltip.toString();
		Composite compositeDefaultDimension = createComposite(parent, 3, 1);
		createLabel(compositeDefaultDimension, "Default dimension: ", false);
		this.defaultDimensionCombo = createCombo(compositeDefaultDimension);
		this.defaultDimensionCombo.setEditable(false);
		this.defaultDimensionCombo.setToolTipText(tooltipText);

		// Results dimensions layout
		tooltip = new StringBuffer("Select the dimensions which will be used while generating performance results\n");
		tooltip.append("When changed, the default dimension above is automatically added to the new list...");
		tooltipText = tooltip.toString();
		Composite compositeResultsDimensions = createComposite(parent, 3, 1);
		createLabel(compositeResultsDimensions, "Results dimensions: ", true/*beginning*/);
		this.resultsDimensionsList = createList(compositeResultsDimensions);
		this.resultsDimensionsList.setToolTipText(tooltipText);

		// Config descriptors layout
		/* TODO See whether config descriptors need to be set as preferences or not...
		Composite compositeConfigDescriptors = createComposite(parent, 3);
		createLabel(compositeConfigDescriptors, "Config descriptors: ", false);
		this.configDescriptorsTable = createTable(compositeConfigDescriptors);
		TableColumn firstColumn = new TableColumn(this.configDescriptorsTable, SWT.LEFT);
		firstColumn.setText ("Name");
		firstColumn.setWidth(50);
		TableColumn secondColumn = new TableColumn(this.configDescriptorsTable, SWT.FILL | SWT.LEFT);
		secondColumn.setText ("Description");
		secondColumn.setWidth(300);
		*/

		// init values
		initializeValues();
	}

	// font = null;
	Composite contents = new Composite(parent, SWT.NULL);
	contents.pack(true);
	return contents;
}

/**
 * Utility method that creates a label instance and sets the default layout
 * data.
 *
 * @param parent
 *            the parent for the new label
 * @param text
 *            the text for the new label
 * @return the new label
 */
private Group createGroup(Composite parent, String text, int columns) {
	Group group = new Group(parent, SWT.NONE);
	group.setLayout(new GridLayout(columns, false));
	group.setText(text);
	GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
//	data.horizontalSpan = 1;
	group.setLayoutData(data);
	return group;
}

/**
 * Utility method that creates a label instance and sets the default layout
 * data.
 *
 * @param parent
 *            the parent for the new label
 * @param text
 *            the text for the new label
 * @param beginning TODO
 * @return the new label
 */
private Label createLabel(Composite parent, String text, boolean beginning) {
	Label label = new Label(parent, SWT.BEGINNING|SWT.LEFT);
	label.setText(text);
	GridData data = new GridData();
	data.horizontalAlignment = GridData.FILL;
	data.verticalAlignment = beginning ? GridData.BEGINNING : GridData.CENTER;
	label.setLayoutData(data);
	return label;
}

/**
 * Create a text field specific for this application
 *
 * @param parent
 *            the parent of the new text field
 * @return the new text field
 */
private List createList(Composite parent) {
	List list = new List(parent, SWT.MULTI | SWT.BORDER);
	list.addSelectionListener(this);
	GridData data = new GridData();
	data.horizontalSpan = 2;
	data.horizontalAlignment = GridData.FILL;
	data.grabExcessHorizontalSpace = true;
	data.verticalAlignment = GridData.CENTER;
	data.grabExcessVerticalSpace = false;
	list.setLayoutData(data);
	return list;
}

/**
 * Utility method that creates a push button instance and sets the default
 * layout data.
 *
 * @param parent
 *            the parent for the new button
 * @param label
 *            the label for the new button
 * @return the newly-created button
 */
private Button createPushButton(Composite parent, String label) {
	Button button = new Button(parent, SWT.PUSH);
	button.setText(label);
	button.addSelectionListener(this);
	GridData data = new GridData();
	data.horizontalAlignment = SWT.LEFT;
	data.grabExcessHorizontalSpace = true;
//	data.horizontalSpan = 2;
	data.minimumWidth = 100;
	button.setLayoutData(data);
	return button;
}

/**
 * Utility method that creates a radio button instance and sets the default
 * layout data.
 *
 * @param parent
 *            the parent for the new button
 * @param label
 *            the label for the new button
 * @return the newly-created button
 */
private Button createRadioButton(Composite parent, String label) {
	Button button = new Button(parent, SWT.RADIO | SWT.LEFT);
	button.setText(label);
	button.addSelectionListener(this);
	GridData data = new GridData();
	button.setLayoutData(data);
	return button;
}

/*
 * Create a text field specific for this application
 *
 * @param parent
 *            the parent of the new text field
 * @return the new text field
 *
private Table createTable(Composite parent) {
	Table table = new Table(parent, SWT.BORDER);
	table.setLinesVisible (true);
	table.setHeaderVisible (true);
	GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
	gridData.heightHint = 150;
	table.setLayoutData(gridData);
	return table;
}
*/

/*
 * Create a text field specific for this application
 *
 * @param parent
 *            the parent of the new text field
 * @return the new text field
 */
private Text createTextField(Composite parent) {
	Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
	text.addModifyListener(this);
	GridData data = new GridData();
	data.horizontalAlignment = GridData.FILL;
	data.grabExcessHorizontalSpace = true;
	data.verticalAlignment = GridData.CENTER;
	data.grabExcessVerticalSpace = false;
	text.setLayoutData(data);
	return text;
}

/**
 * The <code>ReadmePreferencePage</code> implementation of this
 * <code>PreferencePage</code> method returns preference store that belongs to
 * the our plugin. This is important because we want to store our preferences
 * separately from the workbench.
 */
protected IPreferenceStore doGetPreferenceStore() {
	return UiPlugin.getDefault().getPreferenceStore();
}

String getDialogTitle() {
	String title = DB_Results.getDbTitle();
	if (title == null) {
		// DB is not connected
		int version;
		if (this.mVersionRadioButton.getSelection()) {
			version = ECLIPSE_MAINTENANCE_VERSION;
		} else {
			version = ECLIPSE_DEVELOPMENT_VERSION;
		}
		title = "Eclipse " + version + " - DB not connected";
	}
	return title;
}

/*
 * Get the directory path using the given location as default.
 */
private String getDirectoryPath(String location) {
	DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
	dialog.setText(getDialogTitle());
	dialog.setMessage("Select local database directory:");
	dialog.setFilterPath(location);
	String path = dialog.open();
	if (path != null) {
		File dir = new File(path);
		if (dir.exists() && dir.isDirectory()) {
			return dir.getAbsolutePath();
		}
	}
	return null;
}

/*
 * (non-Javadoc) Method declared on IWorkbenchPreferencePage
 */
public void init(IWorkbench workbench) {
	// do nothing
}

/*
 * Init he contents of the dimensions list controls.
 */
void initDimensionsLists() {
	// Dimensions lists
	java.util.List dimensions = PerformanceTestPlugin.getDimensions();
	Iterator names = dimensions.iterator();
	while (names.hasNext()) {
		String name = (String) names.next();
		this.defaultDimensionCombo.add(name);
		this.resultsDimensionsList.add(name);
	}
}

/**
 * Initializes states of the controls using default values in the preference
 * store.
 */
private void initializeDefaults() {
	IPreferenceStore store = getPreferenceStore();

	// Init default database values
	this.dbConnectionCheckBox.setSelection(store.getDefaultBoolean(PRE_DATABASE_CONNECTION));
	this.dbRelengRadioButton.setSelection(false);
	this.dbLocalRadioButton.setSelection(false);
	final boolean dbLocal = store.getDefaultBoolean(PRE_DATABASE_LOCAL);
	if (dbLocal) {
		this.dbLocalRadioButton.setSelection(true);
	} else {
		this.dbRelengRadioButton.setSelection(true);
	}
	this.databaseLocationCombo.removeAll();
	this.databaseLocationCombo.setText(store.getDefaultString(PRE_DATABASE_LOCATION));
	updateDatabaseGroup();

	// Init default status values
	int writeStatus = store.getDefaultInt(PRE_WRITE_STATUS);
	initStatusValues(writeStatus);

	// Init comparison thresholds
	this.comparisonThresholdFailure.setText(String.valueOf(store.getDefaultInt(PRE_COMPARISON_THRESHOLD_FAILURE)));
	this.comparisonThresholdError.setText(String.valueOf(store.getDefaultInt(PRE_COMPARISON_THRESHOLD_ERROR)));
	this.comparisonThresholdImprovement.setText(String.valueOf(store.getDefaultInt(PRE_COMPARISON_THRESHOLD_IMPROVEMENT)));

	// Init eclipse version
	this.mVersionRadioButton.setSelection(false);
	this.dVersionRadionButton.setSelection(false);
	int version = store.getDefaultInt(PRE_ECLIPSE_VERSION);
	if (version == ECLIPSE_MAINTENANCE_VERSION) {
		this.mVersionRadioButton.setSelection(true);
	} else {
		this.dVersionRadionButton.setSelection(true);
	}
    updateBrowseButtonToolTip(version);

	// Milestones
	this.milestonesCombo.removeAll();
	String prefix = PRE_MILESTONE_BUILDS + "." + version;
	String milestone = store.getDefaultString(prefix + "0");
	int index = 0;
	while (milestone != null && milestone.length() > 0) {
		this.milestonesCombo.add(milestone);
		milestone = store.getDefaultString(prefix + ++index);
	}

	// Init default default dimension
	String defaultDimension = store.getDefaultString(PRE_DEFAULT_DIMENSION);
	this.defaultDimensionCombo.setText(defaultDimension);

	// Init default generated dimensions
	this.resultsDimensionsList.add(store.getDefaultString(PRE_RESULTS_DIMENSION+".0"));
	this.resultsDimensionsList.add(store.getDefaultString(PRE_RESULTS_DIMENSION+".1"));
}

/**
 * Initializes states of the controls from the preference store.
 */
private void initializeValues() {
	IPreferenceStore store = getPreferenceStore();

	// Init database info
	this.dbConnectionCheckBox.setSelection(store.getBoolean(PRE_DATABASE_CONNECTION));
	final boolean dbLocal = store.getBoolean(PRE_DATABASE_LOCAL);
	if (dbLocal) {
		this.dbLocalRadioButton.setSelection(true);
		this.dbRelengRadioButton.setToolTipText("");
	} else {
		this.dbRelengRadioButton.setSelection(true);
		this.dbRelengRadioButton.setToolTipText(NETWORK_DATABASE_LOCATION);
	}
	this.databaseLocationCombo.removeAll();
	this.databaseLocationCombo.setText(store.getString(PRE_DATABASE_LOCATION));
	for (int i = 0; i < 3; i++) {
		String history = store.getString(PRE_DATABASE_LOCATION + "." + i);
		if (history.length() == 0)
			break;
		this.databaseLocationCombo.add(history);
	}
	updateDatabaseGroup();

	// Init status values
	int writeStatus = store.getInt(PRE_WRITE_STATUS);
	initStatusValues(writeStatus);

	// Init comparison thresholds
	this.comparisonThresholdFailure.setText(String.valueOf(store.getInt(PRE_COMPARISON_THRESHOLD_FAILURE)));
	this.comparisonThresholdError.setText(String.valueOf(store.getInt(PRE_COMPARISON_THRESHOLD_ERROR)));
	this.comparisonThresholdImprovement.setText(String.valueOf(store.getInt(PRE_COMPARISON_THRESHOLD_IMPROVEMENT)));

	// Init eclipse version
	int version = store.getInt(PRE_ECLIPSE_VERSION);
	if (version == ECLIPSE_MAINTENANCE_VERSION) {
		this.mVersionRadioButton.setSelection(true);
	} else {
		this.dVersionRadionButton.setSelection(true);
	}
    updateBrowseButtonToolTip(version);

	// Milestones
	String prefix = PRE_MILESTONE_BUILDS + "." + version;
	int index = 0;
	String milestone = store.getString(prefix + index);
	while (milestone != null && milestone.length() > 0) {
		this.milestonesCombo.add(milestone);
		milestone = store.getString(prefix + ++index);
	}

	// Init composite lists
	initDimensionsLists();

	// Init default dimension
	String defaultDimension = store.getString(PRE_DEFAULT_DIMENSION);
	this.defaultDimensionCombo.setText(defaultDimension);

	// Init generated dimensions
	int count = this.resultsDimensionsList.getItemCount();
	int[] indices = new int[count];
	int n = 0;
	String resultsDimension = store.getString(PRE_RESULTS_DIMENSION + "." + n);
	while (resultsDimension.length() > 0) {
		indices[n++] = this.resultsDimensionsList.indexOf(resultsDimension);
		resultsDimension = store.getString(PRE_RESULTS_DIMENSION + "." + n);
	}
	if (n < count) {
		System.arraycopy(indices, 0, indices = new int[n], 0, n);
	}
	this.resultsDimensionsList.select(indices);

	// Init config descriptors
	/* TODO See whether config descriptors need to be set as preferences or not...
	this.configDescriptorsTable.clearAll();
	int d = 0;
	String descriptorName = store.getString(PRE_CONFIG_DESCRIPTOR_NAME + "." + d);
	String descriptorDescription = store.getString(PRE_CONFIG_DESCRIPTOR_DESCRIPTION + "." + d++);
	while (descriptorName.length() > 0) {
		TableItem tableItem = new TableItem (this.configDescriptorsTable, SWT.NONE);
		tableItem.setText (0, descriptorName);
		tableItem.setText (1, descriptorDescription);
		descriptorName = store.getString(PRE_CONFIG_DESCRIPTOR_NAME + "." + d);
		descriptorDescription = store.getString(PRE_CONFIG_DESCRIPTOR_DESCRIPTION + "." + d++);
	}
	*/
}

private void initStatusValues(int writeStatus) {
	this.statusValuesCheckBox.setSelection((writeStatus & STATUS_VALUES) != 0);
	this.statusErrorNoneRadioButton.setSelection(false);
	this.statusErrorNoticeableRadioButton.setSelection(false);
	this.statusErrorSuspiciousRadioButton.setSelection(false);
	this.statusErrorWeirdRadioButton.setSelection(false);
	this.statusErrorInvalidRadioButton.setSelection(false);
	switch (writeStatus & STATUS_ERROR_LEVEL_MASK) {
		case STATUS_ERROR_NONE:
			this.statusErrorNoneRadioButton.setSelection(true);
			break;
		case STATUS_ERROR_NOTICEABLE:
			this.statusErrorNoticeableRadioButton.setSelection(true);
			break;
		case STATUS_ERROR_SUSPICIOUS:
			this.statusErrorSuspiciousRadioButton.setSelection(true);
			break;
		case STATUS_ERROR_WEIRD:
			this.statusErrorWeirdRadioButton.setSelection(true);
			break;
		case STATUS_ERROR_INVALID:
			this.statusErrorInvalidRadioButton.setSelection(true);
			break;
	}
	this.statusSmallBuildValueCheckBox.setSelection(false);
	this.statusSmallDeltaValueCheckBox.setSelection(false);
	switch (writeStatus & STATUS_SMALL_VALUE_MASK) {
		case STATUS_SMALL_VALUE_BUILD:
			this.statusSmallBuildValueCheckBox.setSelection(true);
			break;
		case STATUS_SMALL_VALUE_DELTA:
			this.statusSmallDeltaValueCheckBox.setSelection(true);
			break;
	}
	this.statusStatisticNoneRadioButton.setSelection(false);
	this.statusStatisticErraticRadioButton.setSelection(false);
	this.statusStatisticUnstableRadioButton.setSelection(false);
	switch (writeStatus & STATUS_STATISTICS_MASK) {
		case 0:
			this.statusStatisticNoneRadioButton.setSelection(true);
			break;
		case STATUS_STATISTICS_ERRATIC:
			this.statusStatisticErraticRadioButton.setSelection(true);
			break;
		case STATUS_STATISTICS_UNSTABLE:
			this.statusStatisticUnstableRadioButton.setSelection(true);
			break;
	}
	this.statusBuildsToConfirm.setText(String.valueOf(writeStatus & STATUS_BUILDS_NUMBER_MASK));
}

/**
 * (non-Javadoc) Method declared on ModifyListener
 */
public void modifyText(ModifyEvent event) {

	// Add default dimension to results if necessary
	if (event.getSource() == this.defaultDimensionCombo) {
		String[] resultsDimensions = this.resultsDimensionsList.getSelection();
		int length = resultsDimensions.length;
		String defaultDimension = this.defaultDimensionCombo.getText();
		for (int i = 0; i < length; i++) {
			if (resultsDimensions[i].equals(defaultDimension)) {
				// Default dim is already set as a results dimension, hence nothing has to be done
				return;
			}
		}
		System.arraycopy(resultsDimensions, 0, resultsDimensions = new String[length + 1], 0, length);
		resultsDimensions[length] = defaultDimension;
		this.resultsDimensionsList.setSelection(resultsDimensions);
	}

	// Add default dimension to results if necessary
	if (event.getSource() == this.milestonesCombo) {

		// Verify the only digits are entered
		String milestoneDate = this.milestonesCombo.getText();
		final int mLength = milestoneDate.length();
		if (mLength > 0) {
			for (int i=0; i<mLength; i++) {
				if (!Character.isDigit(milestoneDate.charAt(i))) {
					String[] items = this.milestonesCombo.getItems();
					int length = items.length;
					for (int j=0; j<length; j++) {
						if (items[j].equals(milestoneDate)) {
							return;
						}
					}
					openMilestoneErrorMessage(milestoneDate);
					return;
				}
			}
		}

		// Do not verify further until a complete milestone date is entered
		if (mLength < 12) return;

		// Verify the digits
		try {
			String str = milestoneDate.substring(0, 4);
			int year = Integer.parseInt(str);
			if (year < 2009 || year > 2020) { // 2020 should be enough!
				MessageDialog.openError(getShell(), getDialogTitle(), milestoneDate+": "+str+" is an invalid year, only value between 2009 and 2020 is accepted!");
				return;
			}
			str = milestoneDate.substring(4, 6);
			int month = Integer.parseInt(str);
			if (month <= 0 || month > 12) {
				MessageDialog.openError(getShell(), getDialogTitle(), milestoneDate+": "+str+" is an invalid month, it should be only from 01 to 12!");
				return;
			}
			str = milestoneDate.substring(6, 8);
			int day = Integer.parseInt(str);
			if (day <= 0 || day > 31) {
				// TODO improve this verification
				MessageDialog.openError(getShell(), getDialogTitle(), milestoneDate+": "+str+" is an invalid day, it should be only from 01 to 31!");
				return;
			}
			str = milestoneDate.substring(8, 10);
			int hour = Integer.parseInt(str);
			if (hour < 0 || hour > 23) {
				MessageDialog.openError(getShell(), getDialogTitle(), milestoneDate+": "+str+" is an invalid hour, it should be only from 00 to 23!");
				return;
			}
			str = milestoneDate.substring(10, 12);
			int min = Integer.parseInt(str);
			if (min < 0 || min > 59) {
				MessageDialog.openError(getShell(), getDialogTitle(), milestoneDate+": "+str+" is invalid minutes, it should be only from 00 to 59!");
				return;
			}
		}
		catch (NumberFormatException nfe) {
			openMilestoneErrorMessage(milestoneDate);
		}

		// Get combo info
		String[] milestones = this.milestonesCombo.getItems();
		int length = milestones.length;
		String lastMilestone = length == 0 ? null : milestones[length-1];

		// Verify that the added milestone is valid
		char version = (char) ('0' + (this.mVersionRadioButton.getSelection()
			? ECLIPSE_MAINTENANCE_VERSION
			: ECLIPSE_DEVELOPMENT_VERSION) - 30);

		// Verify that the milestone follow the last one
		String milestoneName;
		if (lastMilestone == null) {
			// No previous last milestone
			milestoneName = "M1";
		} else {
			// Compare with last milestone
			if (lastMilestone.charAt(0) == 'M') {
				char digit = lastMilestone.charAt(1);
				if (digit == '6') {
					// M6 is the last dvpt milestone
					milestoneName = "RC1";
				} else {
					milestoneName = "M" +((char)(digit+1));
				}
			} else if (lastMilestone.startsWith("RC")) {
				char digit = lastMilestone.charAt(2);
				if (digit == '4') {
					// RC4 is the last release candidate milestone
					milestoneName = "R3_"+version;
				} else {
					milestoneName = "RC" +((char)(digit+1));
				}
			} else if (lastMilestone.startsWith("R3_"+version+"-")) {
				milestoneName = "R3_" + version + "_1";
			} else if (lastMilestone.startsWith("R3_"+version+"_")) {
				char digit = lastMilestone.charAt(5);
				milestoneName = "R3_" + version + "_" + ((char)(digit+1));
			} else {
				MessageDialog.openError(getShell(), getDialogTitle(), "Unexpected last milestone name: "+lastMilestone+"!");
				return;
			}

			// Verify the date of the new milestone
			int lastMilestoneDash = lastMilestone.indexOf('-');
			final String lastMilestoneDate = lastMilestone.substring(lastMilestoneDash+1);
			if (milestoneDate.compareTo(lastMilestoneDate) <= 0) {
				// TODO improve this verification
				MessageDialog.openError(getShell(), getDialogTitle(), "Milestone "+milestoneDate+" should be after the last milestone: "+lastMilestoneDate+"!");
				return;
			}
		}

		// Verification are ok, ask to add the milestone
		final String milestone = milestoneName + "-" + milestoneDate;
		if (MessageDialog.openConfirm(getShell(), getDialogTitle(), milestoneDate+" is a valid milestone date.\n\nDo you want to add the milestone '"+milestone+"' to the preferences?")) {
			this.milestonesCombo.add(milestone);
			this.milestonesCombo.setText("");
		}
	}

	// Verify the 'builds to confirm' number
	if (event.getSource() == this.statusBuildsToConfirm) {
		try {
			int number = Integer.parseInt(this.statusBuildsToConfirm.getText());
			if (number < 0) {
				this.statusBuildsToConfirm.setText("0");
			} else {
				int buildsNumber = DB_Results.getBuildsNumber();
				if (number > buildsNumber) {
					this.statusBuildsToConfirm.setText(String.valueOf(buildsNumber));
				}
			}
		}
		catch (NumberFormatException nfe) {
			this.statusBuildsToConfirm.setText("1");
		}
	}
}


/**
 * @param milestone
 */
void openMilestoneErrorMessage(String milestone) {
	MessageDialog.openError(getShell(), getDialogTitle(), milestone+" is an invalid milestone date. Only 'yyyymmddHHMM' format is accepted!");
}

/*
 * (non-Javadoc) Method declared on PreferencePage
 */
protected void performDefaults() {
	super.performDefaults();
	initializeDefaults();
}

/*
 * (non-Javadoc) Method declared on PreferencePage
 */
public boolean performOk() {
	final boolean hasBuildsView = this.buildsView != null;
	if (hasBuildsView) {
		storeValues();
		try {
			IEclipsePreferences preferences = new InstanceScope().getNode(PLUGIN_ID);
			preferences.flush();
			this.buildsView.resetView();
		} catch (BackingStoreException e) {
			e.printStackTrace();
			return false;
		}
	}
	return true;
}

/**
 * Stores the values of the controls back to the preference store.
 */
private void storeValues() {
	IPreferenceStore store = getPreferenceStore();

	// Set version
	int version;
	if (this.mVersionRadioButton.getSelection()) {
		version = ECLIPSE_MAINTENANCE_VERSION;
	} else {
		version = ECLIPSE_DEVELOPMENT_VERSION;
	}
	store.setValue(PRE_ECLIPSE_VERSION, version);

	// Set database values
	store.setValue(PRE_DATABASE_CONNECTION, this.dbConnectionCheckBox.getSelection());
	final boolean dbLocal = this.dbLocalRadioButton.getSelection();
	store.setValue(PRE_DATABASE_LOCAL, dbLocal);
	String location = this.databaseLocationCombo.getText();
	if (dbLocal) {
		store.setValue(PRE_DATABASE_LOCATION, location);
	} else {
		store.setValue(PRE_DATABASE_LOCATION, NETWORK_DATABASE_LOCATION);
	}
	int count = this.databaseLocationCombo.getItemCount();
	for (int i=0; i<count; i++) {
		String item = this.databaseLocationCombo.getItem(i);
		if (item.equals(location)) {
			this.databaseLocationCombo.remove(i);
			break;
		}
	}
	if (dbLocal) {
		this.databaseLocationCombo.add(location, 0);
	}
	int i=0;
	for (; i<count; i++) {
		String item = this.databaseLocationCombo.getItem(i);
		if (item.length() == 0) break;
		store.setValue(PRE_DATABASE_LOCATION+"."+i, item);
	}
	while (store.getString(PRE_DATABASE_LOCATION+"."+i).length() > 0) {
		store.setToDefault(PRE_DATABASE_LOCATION+"."+i);
		i++;
	}

	// Set status values
	int writeStatus = 0;
	if (this.statusValuesCheckBox.getSelection()) {
		writeStatus |= STATUS_VALUES;
	}
	if (this.statusErrorNoneRadioButton.getSelection()) {
		writeStatus |= STATUS_ERROR_NONE;
	} else if (this.statusErrorNoticeableRadioButton.getSelection()) {
		writeStatus |= STATUS_ERROR_NOTICEABLE;
	} else if (this.statusErrorSuspiciousRadioButton.getSelection()) {
		writeStatus |= STATUS_ERROR_SUSPICIOUS;
	} else if (this.statusErrorWeirdRadioButton.getSelection()) {
		writeStatus |= STATUS_ERROR_WEIRD;
	} else if (this.statusErrorInvalidRadioButton.getSelection()) {
		writeStatus |= STATUS_ERROR_INVALID;
	}
	if (this.statusSmallBuildValueCheckBox.getSelection()) {
		writeStatus |= STATUS_SMALL_VALUE_BUILD;
	}
	if (this.statusSmallDeltaValueCheckBox.getSelection()) {
		writeStatus |= STATUS_SMALL_VALUE_DELTA;
	}
	if (this.statusStatisticNoneRadioButton.getSelection()) {
		writeStatus &= ~STATUS_STATISTICS_MASK;
	} else if (this.statusStatisticErraticRadioButton.getSelection()) {
		writeStatus |= STATUS_STATISTICS_ERRATIC;
	} else if (this.statusStatisticUnstableRadioButton.getSelection()) {
		writeStatus |= STATUS_STATISTICS_UNSTABLE;
	}
	writeStatus += Integer.parseInt(this.statusBuildsToConfirm.getText());
	store.setValue(PRE_WRITE_STATUS, writeStatus);

	// Init comparison thresholds
	store.setValue(PRE_COMPARISON_THRESHOLD_FAILURE, Integer.parseInt(this.comparisonThresholdFailure.getText()));
	store.setValue(PRE_COMPARISON_THRESHOLD_ERROR, Integer.parseInt(this.comparisonThresholdError.getText()));
	store.setValue(PRE_COMPARISON_THRESHOLD_IMPROVEMENT, Integer.parseInt(this.comparisonThresholdImprovement.getText()));

	// Set milestones
	String prefix = PRE_MILESTONE_BUILDS + "." + version;
	count  = this.milestonesCombo.getItemCount();
	for (i=0; i<count; i++) {
		store.putValue(prefix + i, this.milestonesCombo.getItem(i));
	}
	Util.setMilestones(this.milestonesCombo.getItems());

	// Unset previous additional milestones
	String milestone = store.getString(prefix + count);
	while (milestone != null && milestone.length() > 0) {
		store.putValue(prefix + count++, "");
		milestone = store.getString(prefix + count);
	}

	// Set default dimension
	String defaultDimension = this.defaultDimensionCombo.getText();
	store.putValue(PRE_DEFAULT_DIMENSION, defaultDimension);
	DB_Results.setDefaultDimension(defaultDimension);

	// Set generated dimensions
	int[] indices = this.resultsDimensionsList.getSelectionIndices();
	int length = indices.length;
	String[] dimensions = new String[length];
	if (length > 0) {
		for (i = 0; i < indices.length; i++) {
			dimensions[i] = this.resultsDimensionsList.getItem(indices[i]);
			store.putValue(PRE_RESULTS_DIMENSION + "." + i, dimensions[i]);
		}
	}
	int currentLength = DB_Results.getResultsDimensions().length;
	if (currentLength > length) {
		for (i = currentLength - 1; i >= length; i--) {
			store.putValue(PRE_RESULTS_DIMENSION + "." + i, ""); // reset extra dimensions
		}
	}
	DB_Results.setResultsDimensions(dimensions);

	// Set config descriptors
	/* TODO See whether config descriptors need to be set as preferences or not...
	TableItem[] items = this.configDescriptorsTable.getItems();
	length = items.length;
	for (int i = 0; i < length; i++) {
		TableItem item = items[i];
		store.putValue(PRE_CONFIG_DESCRIPTOR_NAME + "." + i, item.getText(0));
		store.putValue(PRE_CONFIG_DESCRIPTOR_DESCRIPTION + "." + i, item.getText(1));
	}
	*/
}

/**
 * (non-Javadoc) Method declared on SelectionListener
 */
public void widgetDefaultSelected(SelectionEvent event) {
}

/**
 * (non-Javadoc) Method declared on SelectionListener
 */
public void widgetSelected(SelectionEvent event) {

	// As for directory when 'Local' button is pushed
	final Object source = event.getSource();
	if (source == this.dbLocalBrowseButton) {
		String location = this.databaseLocationCombo.getText();
		String path = getDirectoryPath(location);
		if (path != null) {
			// First verify that the selected dir was correct
			int version;
			if (this.mVersionRadioButton.getSelection()) {
				version = ECLIPSE_MAINTENANCE_VERSION;
			} else {
				version = ECLIPSE_DEVELOPMENT_VERSION;
			}
			File dbDir = new File(path, "perfDb"+version);
			if (!dbDir.exists() || !dbDir.isDirectory()) {
				StringBuffer message = new StringBuffer("Invalid performance database directory\n");
				message.append(path+" should contain 'perfDb");
				message.append(version);
				message.append("' directory and none was found!");
				MessageDialog.openError(getShell(), getDialogTitle(), message.toString());
				return;
			}

			// Look for selected dir in combo box list
			int count = this.databaseLocationCombo.getItemCount();
			int index = -1;
			for (int i = 0; i < count; i++) {
				String item = this.databaseLocationCombo.getItem(i);
				if (item.length() == 0) { // nothing in the combo-box list
					break;
				}
				if (item.equals(path)) {
					index = i;
					break;
				}
			}
			// Set the selected dir the more recent in the previous dirs list
			if (index !=  0) {
				if (index > 0) {
					// the dir was used before, but not recently => remove it from previous dirs list
					this.databaseLocationCombo.remove(index);
				}
				// add the selected dir on the top of the previous dirs list
				this.databaseLocationCombo.add(path, 0);
			}
			// Set combo box text
			this.databaseLocationCombo.setText(path);
			updateLocalDb();
		}
	}

	// Reset dabase location when 'Releng' button is pushed
	if (source == this.dbConnectionCheckBox) {
		updateDatabaseGroup();
	}

	// Reset dabase location when 'Releng' check-box is checked
	if (source == this.dbLocalRadioButton) {
		updateLocalDb();
	}

	// Add default dimension to results if necessary
	if (source == this.resultsDimensionsList) {
		String[] resultsDimensions = this.resultsDimensionsList.getSelection();
		int length = resultsDimensions.length;
		String defaultDimension = this.defaultDimensionCombo.getText();
		for (int i = 0; i < length; i++) {
			if (resultsDimensions[i].equals(defaultDimension)) {
				// Default dim is already set as a results dimension, hence nothing has to be done
				return;
			}
		}
		System.arraycopy(resultsDimensions, 0, resultsDimensions = new String[length + 1], 0, length);
		resultsDimensions[length] = defaultDimension;
		this.resultsDimensionsList.setSelection(resultsDimensions);
	}

//	if (source == this.lastBuildCheckBox) {
//		this.lastBuildCombo.setEnabled(this.lastBuildCheckBox.getSelection());
//	}

	if (source == this.mVersionRadioButton) {
		if (this.mVersionRadioButton.getSelection()) {
		    updateBrowseButtonToolTip(ECLIPSE_MAINTENANCE_VERSION);
		}
	}

	if (source == this.dVersionRadionButton) {
		if (this.dVersionRadionButton.getSelection()) {
		    updateBrowseButtonToolTip(ECLIPSE_DEVELOPMENT_VERSION);
		}
	}
}

/*
 * Update browse tooltip
 */
void updateBrowseButtonToolTip(int version) {
	this.dbLocalBrowseButton.setToolTipText("Select the directory where the database was unzipped\n(i.e. should contain the perfDb"+version+" subdirectory)");
}

/*
 * Update database group controls.
 */
void updateDatabaseGroup() {
	if (this.dbConnectionCheckBox.getSelection()) {
		this.dbRelengRadioButton.setEnabled(true);
		this.dbLocalRadioButton.setEnabled(true);
		updateLocalDb();
	} else {
		this.dbRelengRadioButton.setEnabled(false);
		this.dbLocalRadioButton.setEnabled(false);
		this.databaseLocationCombo.setEnabled(false);
		this.dbLocalBrowseButton.setEnabled(false);
		setValid(true);
	}
}

/*
 * Update database location controls.
 */
void updateLocalDb() {
	if (this.dbLocalRadioButton.getSelection()) {
		this.databaseLocationCombo.setEnabled(true);
		this.dbLocalBrowseButton.setEnabled(true);
		if (this.databaseLocationCombo.getItemCount() == 0) {
			this.databaseLocationCombo.setText("");
			setValid(false);
		} else {
			this.databaseLocationCombo.select(0);
			setValid(true);
		}
		this.dbRelengRadioButton.setToolTipText("");
		this.dbLocationLabel.setEnabled(true);
	} else {
		this.dbRelengRadioButton.setToolTipText(NETWORK_DATABASE_LOCATION);
		this.databaseLocationCombo.setText("");
		this.databaseLocationCombo.setEnabled(false);
		this.dbLocalBrowseButton.setEnabled(false);
		setValid(true);
		this.dbLocationLabel.setEnabled(false);
	}
}

}
