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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.test.internal.performance.results.db.AbstractResults;
import org.eclipse.test.internal.performance.results.db.BuildResults;
import org.eclipse.test.internal.performance.results.db.ConfigResults;
import org.eclipse.test.internal.performance.results.db.PerformanceResults;
import org.eclipse.test.internal.performance.results.db.ScenarioResults;
import org.eclipse.test.internal.performance.results.model.BuildResultsElement;
import org.eclipse.test.internal.performance.results.model.PerformanceResultsElement;
import org.eclipse.test.internal.performance.results.model.ResultsElement;
import org.eclipse.test.internal.performance.results.utils.IPerformancesConstants;
import org.eclipse.test.internal.performance.results.utils.Util;


/**
 * Tab to display all performances results numbers for a configuration (i.e a performance machine).
 */
public class BuildsComparisonTab {

	// Colors
	static final Display DEFAULT_DISPLAY = Display.getDefault();
	static final Color BLUE= DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_BLUE);
	static final Color DARK_GREEN= DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_DARK_GREEN);
	static final Color GRAY = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_GRAY);
	static final Color MAGENTA = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_MAGENTA);
	static final Color RED = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_RED);

	// SWT resources
	Shell shell;
	Display display;
	Table table;
	private GC gc;
	private Color lightred;
	private Color lightyellow;
	private Color darkyellow;
	private Color blueref;
	private Font boldFont;
	private Font italicFont;
	private Font boldItalicFont;
	Map toolTips;

	// Information
	String componentName;
	double[][] allValues;
	double[][] allErrors;

	// Cells management
	Point tableOrigin, tableSize;
	int columnsCount, rowsCount;
	List firstLine;

	// Eclipse preferences
	private IEclipsePreferences preferences;

/*
 * Default constructor.
 */
public BuildsComparisonTab(String name) {
    this.componentName = name;
	this.preferences = new InstanceScope().getNode(IPerformancesConstants.PLUGIN_ID);
}

/**
 * Creates the tab folder page.
 *
 * @param view The view on which to create the tab folder page
 * @return the new page for the tab folder
 */
Composite createTabFolderPage (BuildsComparisonView view) {
	// Cache the shell and display.
	this.shell = view.tabFolder.getShell();
	this.display = this.shell.getDisplay();

	// Remove old table if present
	boolean initResources = this.table == null;
	if (this.table != null) {
		disposeTable();
	}

	// Create the "children" table
	int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;
	this.table = new Table(view.tabFolder, style);
	this.table.setLinesVisible (true);
	this.table.setHeaderVisible (true);
	GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
	gridData.heightHint = 150;
	this.table.setLayoutData (gridData);
	this.gc = new GC(this.table);

	// Init resources
	if (initResources) initResources();

	// Add columns to the table
	TableColumn firstColumn = new TableColumn(this.table, SWT.CENTER);
	firstColumn.setText("");
	PerformanceResultsElement results = view.getResults();
	String [] configDescriptions = results.getConfigDescriptions();
	int length = configDescriptions.length;
	for (int i = 0; i < length; i++) {
		TableColumn column = new TableColumn(this.table, SWT.CENTER);
		column.setText(configDescriptions[i]);
	}

	// Add lines to the table
	this.toolTips = new HashMap();
	boolean fingerprints = this.preferences.getBoolean(IPerformancesConstants.PRE_FILTER_ADVANCED_SCENARIOS, IPerformancesConstants.DEFAULT_FILTER_ADVANCED_SCENARIOS);
	fillTable(results, view.currentBuild, view.referenceBuild, fingerprints);

	// Updated columns
	for (int i=0; i<=length; i++) {
		TableColumn column = this.table.getColumn(i);
		column.setWidth(i==0?120:100);
	}

	// Store table info
	this.columnsCount = length;

	// Listen to mouse track events to display the table cell corresponding tooltip.
	MouseTrackListener mouseTrackListener = new MouseTrackListener() {
		ToolTip currentTooltip;
		public void mouseHover(MouseEvent e) {
			if (this.currentTooltip != null) {
				this.currentTooltip.setVisible(false);
				this.currentTooltip = null;
			}
			Point cellPosition = currentCellPosition(e.x, e.y);
			if (cellPosition != null) {
				ToolTip tooltip = (ToolTip) BuildsComparisonTab.this.toolTips.get(cellPosition);
				if (tooltip != null) {
					Point location = BuildsComparisonTab.this.table.toDisplay(new Point(e.x, e.y));
					tooltip.setLocation(location);
					tooltip.setVisible(true);
					this.currentTooltip = tooltip;
				}
			}
		}
		public void mouseEnter(MouseEvent e) {
		}
		public void mouseExit(MouseEvent e) {
		}
	};
	this.table.addMouseTrackListener(mouseTrackListener);

	// Select the first line by default (as this is the current build)
	this.table.select(0);

	// Return the built composite
	return this.table;
}

/*
 * Create and store a tooltip with the given information and at the given position.
 */
void createToolTip(String toolTipText, String toolTipMessage, int toolTipStyle, Point position) {
	ToolTip toolTip = new ToolTip(this.table.getShell(), toolTipStyle);
	toolTip.setAutoHide(true);
	toolTip.setText(toolTipText);
	toolTip.setMessage(/*"("+col+","+row+") "+*/toolTipMessage);
	this.toolTips.put(position, toolTip);
}

/*
 * Get the current cell position (column, row) from a point position.
 */
Point currentCellPosition(int x, int y) {

	// Compute the origin of the visible area
	if (this.tableOrigin == null) {
		this.tableOrigin = new Point(0, this.table.getHeaderHeight());
	}

	// Increment width until over current y position
	int height= this.tableOrigin.y;
	int row = this.table.getTopIndex();
	while (row<this.rowsCount && height<y) {
		height += this.table.getItemHeight();
		row++;
	}
	if (height < y) {
		// return when position is over the last line
		return null;
	}
	row--;

	// Increment width until being over current x position
	int col = 0;
	TableItem tableItem = this.table.getItem(row);
	Rectangle bounds = tableItem.getBounds(col);
	while (col<this.columnsCount) {
		int max = bounds.x + bounds.width + this.table.getGridLineWidth();
		if (x <= max) break;
		if (col == this.columnsCount) {
			// return when position is over the last column
			return null;
		}
		col++;
		bounds = tableItem.getBounds(col);
	}

	// Return the found table cell position
	return new Point(col, row);
}

/*
 * Dispose all SWT resources.
 */
public void dispose() {
	if (this.boldFont != null) {
		this.boldFont.dispose();
	}
	if (this.italicFont != null) {
		this.italicFont.dispose();
	}
	if (this.boldItalicFont != null) {
		this.boldItalicFont.dispose();
	}
	if (this.darkyellow != null) {
		this.darkyellow.dispose();
	}
	if (this.lightyellow != null) {
		this.lightyellow.dispose();
	}
	if (this.lightred != null) {
		this.lightred.dispose();
	}
	if (this.blueref != null) {
		this.blueref.dispose();
	}
	disposeTable();
}

/*
 * Dispose all SWT controls associated with the table.
 */
private void disposeTable() {
	if (this.toolTips != null) {
		Iterator cells = this.toolTips.keySet().iterator();
		while (cells.hasNext()) {
			ToolTip toolTip = (ToolTip) this.toolTips.get(cells.next());
			toolTip.dispose();
		}
	}
	this.table.dispose();
	this.tableOrigin = null;
	this.firstLine = null;
}

/*
 * Fill the lines of the tables.
 * Get all the information from the model which are returned in a list (lines) of lists (columns).
 */
private void fillTable(PerformanceResultsElement results, String currentBuild, String referenceBuild, boolean fingerprints) {

	// Get all the scenario for the component
	final PerformanceResults performanceResults = results.getPerformanceResults();
	List scenarios = performanceResults.getComponentScenarios(this.componentName);
	int size = scenarios.size();

	// Get thresholds
	int failurePref = this.preferences.getInt(IPerformancesConstants.PRE_COMPARISON_THRESHOLD_FAILURE, IPerformancesConstants.DEFAULT_COMPARISON_THRESHOLD_FAILURE);
	int errorPref = this.preferences.getInt(IPerformancesConstants.PRE_COMPARISON_THRESHOLD_ERROR, IPerformancesConstants.DEFAULT_COMPARISON_THRESHOLD_ERROR);
	int improvementPref = this.preferences.getInt(IPerformancesConstants.PRE_COMPARISON_THRESHOLD_IMPROVEMENT, IPerformancesConstants.DEFAULT_COMPARISON_THRESHOLD_IMPROVEMENT);
	final double failureThreshold = -failurePref / 100.0;
	final double errorThreshold = errorPref / 100.0;
	final double improvementThreshold = improvementPref / 100.0;

	// Build the map made of one line per scenario results
	final String[] configs = performanceResults.getConfigNames(true/*sort*/);
	for (int i=0; i<size; i++) {
		ScenarioResults scenarioResults = (ScenarioResults) scenarios.get(i);
		if (!scenarioResults.isValid()) continue;
		final boolean fingerprint = scenarioResults.hasSummary();
		if (!fingerprints || fingerprint) {

			// The first column is the scenario name
			String scenarioName = scenarioResults.getShortName();
			TableItem item = new TableItem (this.table, SWT.NONE);
			item.setText(scenarioName);

			// Bold font and blue ref background color if this is a fingerprint test
			Font italic;
			if (fingerprint) {
				item.setFont(0, this.boldFont);
				item.setBackground(this.blueref);
				italic = this.boldItalicFont;
			} else {
				italic = this.italicFont;
			}

			// Fill config columns
			int length = configs.length;
			for (int j=0; j<length; j++) {

				// See whether there's available numbers
				int col = j+1;
				final ConfigResults configResults = scenarioResults.getConfigResults(configs[j]);
				if (configResults == null || !configResults.isValid()) {
					item.setText(col, "Invalid");
					item.setForeground(col, GRAY);
					item.setFont(col, italic);
					continue;
				}
				final BuildResults buildResults = configResults.getBuildResults(currentBuild);
				if (buildResults == null) {
					item.setText(col, "Missing results");
					item.setForeground(col, GRAY);
					item.setFont(col, italic);
					continue;
				}
				final BuildResults referenceResults = configResults.getBuildResults(referenceBuild);
				if (referenceResults == null) {
					item.setText(col, "Missing ref");
					item.setForeground(col, GRAY);
					item.setFont(col, italic);
					continue;
				}

				// Get numbers and infos
				double[] values = configResults.getNumbers(buildResults, referenceResults);

				// Reset tooltip info
				String toolTipText = null;
				String toolTipMessage = null;
				int toolTipStyle = SWT.BALLOON;

				// Get values array
				final double buildValue = values[AbstractResults.BUILD_VALUE_INDEX];
				final double referenceValue = values[AbstractResults.BASELINE_VALUE_INDEX];
				final double delta = values[AbstractResults.DELTA_VALUE_INDEX];
				final double error = values[AbstractResults.DELTA_ERROR_INDEX];

				// Set text with delta value
				final StringBuffer address = new StringBuffer("http://fullmoon.ottawa.ibm.com/downloads/drops/");
				address.append(currentBuild);
				address.append("/performance/");
				address.append(configResults.getName());
				address.append('/');
				address.append(scenarioResults.getFileName());
				address.append(".html");
				StringBuffer buffer = new StringBuffer("<a href=\"");
				buffer.append(address);
				buffer.append("\">");
				final String itemText = Util.PERCENTAGE_FORMAT.format(delta);
				buffer.append(itemText);
				buffer.append("</a>");

				// Simple text
				item.setText(col, itemText);

				/* Link + Editor
				Link link = new Link(this.table, SWT.CENTER);
				link.setText(buffer.toString());
				link.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						Program.launch(address.toString());
					}
				});
				final TableEditor editor = new TableEditor(this.table);
				editor.grabHorizontal = editor.grabVertical = true;
				editor.verticalAlignment = SWT.CENTER;
				editor.horizontalAlignment = SWT.CENTER;
				editor.setEditor(link, item, col);
				*/

				// Compute the tooltip to display on the cell
				if (error > errorThreshold) {
					// error is over the threshold
					item.setForeground(col, this.darkyellow);
					toolTipText = "May be not reliable";
					toolTipMessage = "The error on this result is "+Util.PERCENTAGE_FORMAT.format(error)+", hence it may be not reliable";
					toolTipStyle |= SWT.ICON_WARNING;
				}
				if (delta < -0.1) {
					// delta < -10%: failure shown by an red-cross icon + text in red
					item.setImage(col, ResultsElement.ERROR_IMAGE);
				}
				if (delta < failureThreshold) {
					// negative delta over the threshold shown in red
					item.setForeground(col, RED);
				} else if (delta > improvementThreshold) {
					// positive delta over the threshold are shown in green
					item.setForeground(col, DARK_GREEN);
				}

				// Moderate the status if the build value or the difference is small
				if (buildValue < 100 || referenceValue < 100) {
					if (toolTipText == null) {
						toolTipText = "";
					} else {
						toolTipText += ", ";
					}
					toolTipText += "Small value";
					if (toolTipMessage == null) {
						toolTipMessage = "";
					} else {
						toolTipMessage += ".\n";
					}
					toolTipMessage += "This test has a small value ("+buildValue+"ms)";
					toolTipStyle |= SWT.ICON_WARNING;
					item.setImage(col, ResultsElement.WARN_IMAGE);
				}

				// Add information in tooltip when history shows big variation
				double deviation = configResults.getStatistics(Util.BASELINE_BUILD_PREFIXES)[3];
				if (deviation > 0.2) {
					// deviation is over 20% over the entire history
					if (toolTipText == null) {
						toolTipText = "";
					} else {
						toolTipText += ", ";
					}
					toolTipText += "History shows erratic values";
					if (toolTipMessage == null) {
						toolTipMessage = "";
					} else {
						toolTipMessage += ".\n";
					}
					toolTipMessage += "The results history shows that the variation of its delta is over 20% ("+Util.PERCENTAGE_FORMAT.format(deviation)+"), hence its result is surely not reliable";
					// set the text in italic
					item.setFont(col, italic);
					toolTipStyle |= SWT.ICON_INFORMATION;
				} else if (deviation > 0.1) { // moderate the status when the test
					// deviation is between 10% and 20% over the entire history
					if (toolTipText == null) {
						toolTipText = "";
					} else {
						toolTipText += ", ";
					}
					toolTipText += "History shows unstable values";
					if (toolTipMessage == null) {
						toolTipMessage = "";
					} else {
						toolTipMessage += ".\n";
					}
					toolTipMessage += "The results history shows that the variation of its delta is between 10% and 20% ("+Util.PERCENTAGE_FORMAT.format(deviation)+"), hence its result may not be really reliable";
					// set the text in italic
					item.setFont(col, italic);
					if (toolTipStyle == SWT.BALLOON && delta >= -0.1) {
						toolTipStyle |= SWT.ICON_INFORMATION;
					} else {
						// reduce icon severity from error to warning
						toolTipStyle |= SWT.ICON_WARNING;
					}
				}

				// Set tooltip
				if (toolTipText != null) {
					createToolTip(toolTipText, toolTipMessage, toolTipStyle, new Point(col, i));
				}
			}
		}
	}
	this.rowsCount = size;
}

protected Shell getShell() {
	return this.shell;
}

/*
 * The tab text is the full machine name.
 */
public String getTabText() {
	return Util.componentDisplayName(this.componentName);
}

/*
 * Init the SWT resources
 */
private void initResources() {
	// Fonts
	String fontDataName = this.gc.getFont().getFontData()[0].toString();
	FontData fdItalic = new FontData(fontDataName);
	fdItalic.setStyle(SWT.ITALIC);
	this.italicFont = new Font(this.display, fdItalic);
	FontData fdBold = new FontData(fontDataName);
	fdBold.setStyle(SWT.BOLD);
	this.boldFont = new Font(this.display, fdBold);
	FontData fdBoldItalic = new FontData(fontDataName);
	fdBoldItalic.setStyle(SWT.BOLD | SWT.ITALIC);
	this.boldItalicFont = new Font(this.display, fdBoldItalic);

	// Colors
	this.lightred = new Color(DEFAULT_DISPLAY, 220, 50, 50);
	this.lightyellow = new Color(DEFAULT_DISPLAY, 255, 255, 160);
	this.darkyellow = new Color(DEFAULT_DISPLAY, 160, 160, 0);
	this.blueref = new Color(DEFAULT_DISPLAY, 200, 200, 255);
}

/*
 * Select the line corresponding to the given build.
 */
void select(BuildResultsElement buildResultsElement) {
	int count = this.table.getItemCount();
	String buildName = buildResultsElement.getName();
	TableItem[] items = this.table.getItems();
	for (int i=0; i<count; i++) {
		if (items[i].getText().endsWith(buildName)) {
			this.table.deselect(this.table.getSelectionIndex());
			this.table.select(i);
			return;
		}
	}
}
}
