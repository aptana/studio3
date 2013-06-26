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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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
import org.eclipse.test.internal.performance.results.model.BuildResultsElement;
import org.eclipse.test.internal.performance.results.model.ComponentResultsElement;
import org.eclipse.test.internal.performance.results.model.ConfigResultsElement;
import org.eclipse.test.internal.performance.results.model.ResultsElement;
import org.eclipse.test.internal.performance.results.model.ScenarioResultsElement;
import org.eclipse.test.internal.performance.results.utils.IPerformancesConstants;
import org.eclipse.test.internal.performance.results.utils.Util;


/**
 * Tab to display all performances results numbers for a configuration (i.e a performance machine).
 */
public class ConfigTab {

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
	String configBox, configName;
	ComponentResultsElement results;
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
public ConfigTab(String name, String box) {
    this.configName = name;
    int idx = box.indexOf(" (");
	this.configBox = idx > 0 ? box.substring(0, idx) : box;
	this.preferences = new InstanceScope().getNode(IPerformancesConstants.PLUGIN_ID);
}

/**
 * Creates the tab folder page.
 *
 * @param tabFolder org.eclipse.swt.widgets.TabFolder
 * @param fullSelection Tells whether the table should have a full line selection or not
 * @return the new page for the tab folder
 */
Composite createTabFolderPage (ComponentResultsElement componentResultsElement, CTabFolder tabFolder, boolean fullSelection) {
	// Cache the shell and display.
	this.shell = tabFolder.getShell();
	this.display = this.shell.getDisplay();

	// Remove old table is present
	boolean initResources = this.table == null;
	if (this.table != null) {
		disposeTable();
	}

	// Store results
	this.results = componentResultsElement;

	// Create the "children" table
	int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL;
	if (fullSelection) style |= SWT.FULL_SELECTION;
	this.table = new Table(tabFolder, style);
	this.table.setLinesVisible (true);
	this.table.setHeaderVisible (true);
	GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
	gridData.heightHint = 150;
	this.table.setLayoutData (gridData);
	this.gc = new GC(this.table);

	// Init resources
	if (initResources) initResources();

	// Add columns to the table
	boolean fingerprints = this.preferences.getBoolean(IPerformancesConstants.PRE_FILTER_ADVANCED_SCENARIOS, IPerformancesConstants.DEFAULT_FILTER_ADVANCED_SCENARIOS);
	String [] columnHeaders = getLayoutDataFieldNames(fingerprints);
	int length = columnHeaders.length;
	for (int i = 0; i < length; i++) {
		TableColumn column = new TableColumn(this.table, SWT.CENTER);
		column.setText (columnHeaders [i]);
	}

	// Add lines to the table
	this.toolTips = new HashMap();
	fillTableLines(fingerprints);

	// Updated columns
	for (int i=0; i<length; i++) {
		TableColumn column = this.table.getColumn(i);
		column.setWidth(i==0?120:100);
		if (i > 0) {
			String text = (String) this.firstLine.get(i);
			column.setToolTipText(text);
		}
	}

	// Store table info
	this.columnsCount = length;

	// Listen to mouse events to select the corresponding build in the components view
	// when a click is done in the table cell.
	final ComponentsView componentsView = (ComponentsView) PerformancesView.getWorkbenchView("org.eclipse.test.internal.performance.results.ui.ComponentsView");
	MouseListener mouseListener = new MouseListener() {
		public void mouseUp(MouseEvent e) {
		}
		public void mouseDown(MouseEvent e) {
			Point cellPosition = currentCellPosition(e.x, e.y);
			Table tabTable = ConfigTab.this.table;
			componentsView.select(ConfigTab.this.results, ConfigTab.this.configName, (String) ConfigTab.this.firstLine.get(cellPosition.x), tabTable.getItem(cellPosition.y).getText());
		}
		public void mouseDoubleClick(MouseEvent e) {
		}
	};
	this.table.addMouseListener(mouseListener);

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
				ToolTip tooltip = (ToolTip) ConfigTab.this.toolTips.get(cellPosition);
				if (tooltip != null) {
					Point location = ConfigTab.this.table.toDisplay(new Point(e.x, e.y));
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
private void fillTableLines(boolean fingerprints) {

	// Get preferences information
	boolean onlyMilestones = this.preferences.getBoolean(IPerformancesConstants.PRE_FILTER_OLD_BUILDS, IPerformancesConstants.DEFAULT_FILTER_OLD_BUILDS);
	boolean skipNightlyBuilds = this.preferences.getBoolean(IPerformancesConstants.PRE_FILTER_NIGHTLY_BUILDS, IPerformancesConstants.DEFAULT_FILTER_NIGHTLY_BUILDS);

	// Get model information
	if (this.results == null) return;
	List differences = this.results.getConfigNumbers(this.configName, fingerprints);
	if (differences == null) return;

	// Store first information line which are the scenarios full names
	Iterator lines = differences.iterator();
	this.firstLine = (List) lines.next();

	// Read each information line (one line per build results)
	Object[] scenarios = this.results.getChildren(null);
	ConfigResultsElement[] configs = new ConfigResultsElement[scenarios.length];
	int row = 0;
	while (lines.hasNext()) {
		List line = (List) lines.next();
		int size = line.size();

		// The first column is the build name
		String buildName = (String) line.get(0);
		String milestoneName = Util.getMilestoneName(buildName);
		TableItem item = null;

		// Set item if the line is not filtered
		Font italic;
		if (milestoneName != null) {
			item = new TableItem (this.table, SWT.NONE);
			item.setText(milestoneName + " - " + buildName);
			item.setFont(0, this.boldFont);
			if (!onlyMilestones) item.setBackground(this.blueref);
			italic = this.boldItalicFont;
		} else {
			if ((onlyMilestones && Util.getNextMilestone(buildName) != null) ||
				(skipNightlyBuilds && buildName.charAt(0) == 'N')) {
				// skip line
				continue;
			}
			item = new TableItem (this.table, SWT.NONE);
			item.setText(0, buildName);
			italic = this.italicFont;
		}

		// Read each column value
		String baselineName = null;
		for (int col=1; col<size; col++) {

			// Reset tooltip info
			String toolTipText = null;
			String toolTipMessage = null;
			int toolTipStyle = SWT.BALLOON;

			// Otherwise get values for a scenario
			Font italic2 = italic;
			ScenarioResultsElement scenarioResultsElement = (ScenarioResultsElement) scenarios[col-1];
			if (milestoneName != null || (!fingerprints && scenarioResultsElement.hasSummary())) {
				italic2 = this.boldItalicFont;
				item.setFont(col, this.boldFont);
			}
			// Otherwise get values for a scenario
			double[] values = (double[]) line.get(col);
			if (values == AbstractResults.NO_BUILD_RESULTS) {
				item.setText(col, "Missing");
				item.setForeground(col, GRAY);
				item.setFont(col, italic2);
			} else if (values == AbstractResults.INVALID_RESULTS) {
				item.setText(col, "Invalid");
				item.setForeground(col, RED);
				item.setFont(col, italic2);
			} else {
				// Get values array
				double buildValue = values[AbstractResults.BUILD_VALUE_INDEX];
				double baselineValue = values[AbstractResults.BASELINE_VALUE_INDEX];
				double delta = values[AbstractResults.DELTA_VALUE_INDEX];
				double error = values[AbstractResults.DELTA_ERROR_INDEX];

				// Set text with delta value
				item.setText(col, Util.PERCENTAGE_FORMAT.format(delta));

				// Compute the tooltip to display on the cell
				if (error > 0.03) {
					// error is over the 3% threshold
					item.setImage(col, ResultsElement.WARN_IMAGE);
					item.setForeground(col, this.darkyellow);
					toolTipText = "May be not reliable";
					toolTipMessage = "The error on this result is "+Util.PERCENTAGE_FORMAT.format(error)+", hence it may be not reliable";
					toolTipStyle |= SWT.ICON_WARNING;
				}
				if (delta < -0.1) {
					// delta < -10%: failure shown by an red-cross icon + text in red
					item.setImage(col, ResultsElement.ERROR_IMAGE);
					item.setForeground(col, RED);
				} else if (delta < -0.05) {
					// negative delta over 5% shown in red
					item.setForeground(col, RED);
				} else if (delta < 0) {
					// negative delta shown in magenta
					item.setForeground(col, MAGENTA);
				} else if (delta > 0.2) {
					// positive delta over 20% shown in green
					item.setForeground(col, DARK_GREEN);
				} else if (delta > 0.1) {
					// positive delta between 10% and 20% shown in blue
					item.setForeground(col, BLUE);
				}

				// Moderate the status if the build value or the difference is small
				if (delta < 0) {
					double diff = Math.abs(baselineValue - buildValue);
					if (buildValue < 100 || diff < 100) {
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
						if (buildValue < 100) {
							toolTipMessage += "This test has a small value ("+buildValue+"ms)";
						} else {
							toolTipMessage += "This test variation has a small value ("+diff+"ms)";
						}
						toolTipMessage +=  ", hence it may not be necessary to spend time on fixing this possible regression";
						// set the text in italic
						item.setFont(col, italic2);
						toolTipStyle |= SWT.ICON_INFORMATION;
					}
				}

				// Add information in tooltip when history shows big variation
				ConfigResultsElement configResultsElement = configs[col-1];
				if (configResultsElement == null) {
					configResultsElement = (ConfigResultsElement) scenarioResultsElement.getResultsElement(this.configName);
					configs[col-1] = configResultsElement;
				}
				double deviation = configResultsElement == null ? 0 : configResultsElement.getStatistics()[3];
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
					item.setFont(col, italic2);
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
					item.setFont(col, italic2);
					if (toolTipStyle == SWT.BALLOON && delta >= -0.1) {
						toolTipStyle |= SWT.ICON_INFORMATION;
					} else {
						// reduce icon severity from error to warning
						toolTipStyle |= SWT.ICON_WARNING;
					}
				}
			}

			// Set tooltip
			if (toolTipText != null) {
				createToolTip(toolTipText, toolTipMessage, toolTipStyle, new Point(col, row));
			}

			// Baseline name
			ConfigResultsElement configResultsElement = (ConfigResultsElement) scenarioResultsElement.getResultsElement(this.configName);
			if (configResultsElement != null) {
				String configBaselineName = configResultsElement.getBaselineBuildName(buildName);
				if (baselineName == null) {
					baselineName = configBaselineName;
				} else if (baselineName.indexOf(configBaselineName) < 0) {
					baselineName += ", " +configBaselineName;
				}
			}
		}

		// Set the tooltip over the build name
		if (baselineName != null) {
			createToolTip(buildName, "Baseline: "+baselineName, SWT.BALLOON | SWT.ICON_INFORMATION, new Point(0, row));
		}

		// Increment row counter
		row++;
	}
	this.rowsCount = row;
}

/*
 * Get the columns name.
 */
private String[] getLayoutDataFieldNames(boolean fingerprints) {
	if (this.results == null) {
		return new String[0];
	}
	List labels = this.results.getScenariosLabels(fingerprints);
	labels.add(0, "Build");
	String[] names = new String[labels.size()];
	labels.toArray(names);
	return names;
}

/*
 * The tab text is the full machine name.
 */
public String getTabText() {
	return this.configBox;
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
