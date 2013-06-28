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
package org.eclipse.test.performance.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.test.internal.performance.results.db.ConfigResults;
import org.eclipse.test.internal.performance.results.db.PerformanceResults;
import org.eclipse.test.internal.performance.results.db.ScenarioResults;
import org.eclipse.test.internal.performance.results.utils.Util;
import org.osgi.framework.Bundle;

/**
 * Main class to generate performance results of all scenarios matching a given
 * pattern in one HTML page per component.
 * 
 * @see #printUsage() method to see a detailed parameters usage
 */
public class GenerateResults {

	/**
	 * Prefix of baseline builds displayed in data graphs. This field is set
	 * using <b>-baseline.prefix</b> argument.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * -baseline.prefix 3.2_200606291905
	 * </pre>
	 * 
	 * @see #currentBuildPrefixes
	 */
	String baselinePrefix = null;

	/**
	 * Root directory where all files are generated. This field is set using
	 * <b>-output</b> argument.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * -output / releng / results / I20070615 - 1200 / performance
	 * </pre>
	 */
	File outputDir;

	/**
	 * Root directory where all data are locally stored to speed-up generation.
	 * This field is set using <b>-dataDir</b> argument.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * -dataDir / tmp
	 * </pre>
	 */
	File dataDir;

	/**
	 * Arrays of 2 strings which contains config information: name and
	 * description. This field is set using <b>-config</b> and/or
	 * <b>-config.properties</b> arguments.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * 	-config eclipseperflnx3_R3.3,eclipseperfwin2_R3.3,eclipseperflnx2_R3.3,eclipseperfwin1_R3.3,eclipseperflnx1_R3.3
	 * 	-config.properties
	 * 		"eclipseperfwin1_R3.3,Win XP Sun 1.4.2_08 (2 GHz 512 MB);
	 * 		eclipseperflnx1_R3.3,RHEL 3.0 Sun 1.4.2_08 (2 GHz 512 MB);
	 * 		eclipseperfwin2_R3.3,Win XP Sun 1.4.2_08 (3 GHz 2 GB);
	 * 		eclipseperflnx2_R3.3,RHEL 3.0 Sun 1.4.2_08 (3 GHz 2 GB);
	 * 		eclipseperflnx3_R3.3,RHEL 4.0 Sun 1.4.2_08 (3 GHz 2.5 GB)"
	 * </pre>
	 * 
	 * Note that:
	 * <ul>
	 * <li>if only <b>-config</b> is set, then configuration name is used for
	 * description</li>
	 * <li>if only <b>-config.properties</b> is set, then all configurations
	 * defined with this argument are generated
	 * <li>if both arguments are defined, then only configurations defined by
	 * <b>-config</b> argument are generated, <b>-config.properties</b> argument
	 * is only used to set the configuration description.</li>
	 * </ul>
	 */
	String[][] configDescriptors;

	/**
	 * Scenario pattern used to generate performance results. This field is set
	 * using <b>-scenarioPattern</b> argument.
	 * <p>
	 * Note that this pattern uses SQL conventions, not RegEx ones, which means
	 * that '%' is used to match several consecutive characters and '_' to match
	 * a single character.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * -scenario.pattern org.eclipse.%.test
	 * </pre>
	 */
	String scenarioPattern;

	/**
	 * A list of prefixes for builds displayed in data graphs. This field is set
	 * using <b>-currentPrefix</b> argument.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * -current.prefix N, I
	 * </pre>
	 * 
	 * @see #baselinePrefix
	 */
	List currentBuildPrefixes;

	/**
	 * A list of prefixes of builds to highlight in displayed data graphs. This
	 * field is set using <b>-highlight</b> and/or <b>-highlight.latest</b>
	 * arguments.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * -higlight 3_2
	 * </pre>
	 */
	List pointsOfInterest;

	/**
	 * Tells whether only fingerprints has to be generated. This field is set to
	 * <code>true</code> if <b>-fingerprints</b> argument is specified.
	 * <p>
	 * Default is <code>false</code> which means that scenario data will also be
	 * generated.
	 * 
	 * @see #genData
	 * @see #genAll
	 */
	boolean genFingerPrints = false;

	/**
	 * Tells whether only fingerprints has to be generated. This field is set to
	 * <code>true</code> if <b>-data</b> argument is specified.
	 * <p>
	 * Default is <code>false</code> which means that fingerprints will also be
	 * generated.
	 * 
	 * @see #genFingerPrints
	 * @see #genAll
	 */
	boolean genData = false;

	/**
	 * Tells whether only fingerprints has to be generated. This field is set to
	 * <code>false</code> if <b>-fingerprints</b> or <b>-data</b> argument is
	 * specified.
	 * <p>
	 * Default is <code>true</code> which means that scenario data will also be
	 * generated.
	 * 
	 * @see #genData
	 * @see #genFingerPrints
	 */
	boolean genAll = true;

	/**
	 * Tells whether information should be displayed in the console while
	 * generating. This field is set to <code>true</code> if <b>-print</b>
	 * argument is specified.
	 * <p>
	 * Default is <code>false</code> which means that nothing is print during
	 * the generation.
	 */
	PrintStream printStream = null;

	/**
	 * Tells what should be the failure percentage threshold.
	 * <p>
	 * Default is 10%.
	 */
	int failure_threshold = 10; // PerformanceTestPlugin.getDBLocation().startsWith("net://");

	PerformanceResults performanceResults;

	public GenerateResults() {
	}

	public GenerateResults(boolean fingerprints, File data) {
		this.dataDir = data;
		this.genFingerPrints = fingerprints;
		this.genAll = !fingerprints;
		this.printStream = System.out;
	}

	/*
	 * Parse the command arguments and create corresponding performance results
	 * object.
	 */
	private void parse(String[] args) {
		StringBuffer buffer = new StringBuffer(
				"Parameters used to generate performance results (");
		buffer.append(new SimpleDateFormat().format(new Date(System
				.currentTimeMillis())));
		buffer.append("):\n");
		int i = 0;
		int argsLength = args.length;
		if (argsLength == 0) {
			printUsage();
		}

		String currentBuildId = null;
		String baseline = null;
		String javaVersion = null;
		this.configDescriptors = null;

		while (i < argsLength) {
			String arg = args[i];
			if (!arg.startsWith("-")) {
				i++;
				continue;
			}
			if (argsLength == i + 1 && i != argsLength - 1) {
				System.out.println("Missing value for last parameter");
				printUsage();
			}
			if (arg.equals("-baseline")) {
				baseline = args[i + 1];
				if (baseline.startsWith("-")) {
					System.out.println("Missing value for " + arg
							+ " parameter");
					printUsage();
				}
				buffer.append("	-baseline = " + baseline + '\n');
				i++;
				continue;
			}
			if (arg.equals("-baseline.prefix")) {
				this.baselinePrefix = args[i + 1];
				if (this.baselinePrefix.startsWith("-")) {
					System.out.println("Missing value for " + arg
							+ " parameter");
					printUsage();
				}
				buffer.append("	").append(arg).append(" = ")
						.append(this.baselinePrefix).append('\n');
				i++;
				continue;
			}
			if (arg.equals("-current.prefix")) {
				String idPrefixList = args[i + 1];
				if (idPrefixList.startsWith("-")) {
					System.out.println("Missing value for " + arg
							+ " parameter");
					printUsage();
				}
				buffer.append("	").append(arg).append(" = ");
				String[] ids = idPrefixList.split(",");
				this.currentBuildPrefixes = new ArrayList();
				for (int j = 0; j < ids.length; j++) {
					this.currentBuildPrefixes.add(ids[j]);
					buffer.append(ids[j]);
				}
				buffer.append('\n');
				i++;
				continue;
			}
			if (arg.equals("-highlight") || arg.equals("-highlight.latest")) {
				if (args[i + 1].startsWith("-")) {
					System.out.println("Missing value for " + arg
							+ " parameter");
					printUsage();
				}
				buffer.append("	").append(arg).append(" = ");
				String[] ids = args[i + 1].split(",");
				this.pointsOfInterest = new ArrayList();
				for (int j = 0; j < ids.length; j++) {
					this.pointsOfInterest.add(ids[j]);
					buffer.append(ids[j]);
				}
				buffer.append('\n');
				i++;
				continue;
			}
			if (arg.equals("-current")) {
				currentBuildId = args[i + 1];
				if (currentBuildId.startsWith("-")) {
					System.out.println("Missing value for " + arg
							+ " parameter");
					printUsage();
				}
				buffer.append("	").append(arg).append(" = ")
						.append(currentBuildId).append('\n');
				i++;
				continue;
			}
			if (arg.equals("-javaVersion")) {
				javaVersion = args[i + 1];
				if (javaVersion.startsWith("-")) {
					System.out.println("Missing value for " + arg
							+ " parameter");
					printUsage();
				}
				buffer.append("	").append(arg).append(" = ").append(javaVersion)
						.append('\n');
				i++;
				continue;
			}
			if (arg.equals("-output")) {
				String dir = args[++i];
				if (dir.startsWith("-")) {
					System.out.println("Missing value for " + arg
							+ " parameter");
					printUsage();
				}
				this.outputDir = new File(dir);
				if (!this.outputDir.exists() && !this.outputDir.mkdirs()) {
					System.err.println("Cannot create directory " + dir
							+ " to write results in!");
					System.exit(2);
				}
				buffer.append("	").append(arg).append(" = ").append(dir)
						.append('\n');
				continue;
			}
			if (arg.equals("-dataDir")) {
				String dir = args[++i];
				if (dir.startsWith("-")) {
					System.out.println("Missing value for " + arg
							+ " parameter");
					printUsage();
				}
				this.dataDir = new File(dir);
				if (!this.dataDir.exists() && !this.dataDir.mkdirs()) {
					System.err.println("Cannot create directory " + dir
							+ " to save data locally!");
					System.exit(2);
				}
				buffer.append("	").append(arg).append(" = ").append(dir)
						.append('\n');
				continue;
			}
			if (arg.equals("-config")) {
				String configs = args[i + 1];
				if (configs.startsWith("-")) {
					System.out.println("Missing value for " + arg
							+ " parameter");
					printUsage();
				}
				String[] names = configs.split(",");
				int length = names.length;
				buffer.append("	").append(arg).append(" = ");
				for (int j = 0; j < length; j++) {
					if (j > 0)
						buffer.append(',');
					buffer.append(names[j]);
				}
				if (this.configDescriptors == null) {
					this.configDescriptors = new String[length][2];
					for (int j = 0; j < length; j++) {
						this.configDescriptors[j][0] = names[j];
						this.configDescriptors[j][1] = names[j];
					}
				} else {
					int confLength = this.configDescriptors[0].length;
					int newLength = confLength;
					mainLoop: for (int j = 0; j < confLength; j++) {
						for (int k = 0; k < length; k++) {
							if (this.configDescriptors[j][0].equals(names[k])) {
								continue mainLoop;
							}
						}
						this.configDescriptors[j][0] = null;
						this.configDescriptors[j][1] = null;
						newLength--;
					}
					if (newLength < confLength) {
						String[][] newDescriptors = new String[newLength][2];
						for (int j = 0, c = 0; j < newLength; j++) {
							if (this.configDescriptors[c] != null) {
								newDescriptors[j][0] = this.configDescriptors[c][0];
								newDescriptors[j][1] = this.configDescriptors[c][1];
							} else {
								c++;
							}
						}
						this.configDescriptors = newDescriptors;
					}
				}
				buffer.append('\n');
				i++;
				continue;
			}
			if (arg.equals("-config.properties")) {
				String configProperties = args[i + 1];
				if (configProperties.startsWith("-")) {
					System.out.println("Missing value for " + arg
							+ " parameter");
					printUsage();
				}
				if (this.configDescriptors == null) {
					System.out.println("Missing -config parameter");
					printUsage();
				}
				int length = this.configDescriptors.length;
				StringTokenizer tokenizer = new StringTokenizer(
						configProperties, ";");
				buffer.append('\t').append(arg).append(" = '")
						.append(configProperties).append("' splitted in ")
						.append(length).append(" configs:");
				while (tokenizer.hasMoreTokens()) {
					String labelDescriptor = tokenizer.nextToken();
					String[] elements = labelDescriptor.trim().split(",");
					for (int j = 0; j < length; j++) {
						if (elements[0].equals(this.configDescriptors[j][0])) {
							this.configDescriptors[j][1] = elements[1];
							buffer.append("\n\t\t+ ");
							buffer.append(elements[0]);
							buffer.append(" -> ");
							buffer.append(elements[1]);
						}
					}
				}
				buffer.append('\n');
				i++;
				continue;
			}
			if (arg.equals("-scenario.filter")
					|| arg.equals("-scenario.pattern")) {
				this.scenarioPattern = args[i + 1];
				if (this.scenarioPattern.startsWith("-")) {
					System.out.println("Missing value for " + arg
							+ " parameter");
					printUsage();
				}
				buffer.append("	").append(arg).append(" = ")
						.append(this.scenarioPattern).append('\n');
				i++;
				continue;
			}
			if (arg.equals("-fingerprints")) {
				this.genFingerPrints = true;
				this.genAll = false;
				buffer.append("	").append(arg).append('\n');
				i++;
				continue;
			}
			if (arg.equals("-data")) {
				this.genData = true;
				this.genAll = false;
				buffer.append("	").append(arg).append('\n');
				i++;
				continue;
			}
			if (arg.equals("-print")) {
				this.printStream = System.out; // default is to print to console
				buffer.append("	").append(arg);
				i++;
				String printFile = i == argsLength ? null : args[i];
				if (printFile == null || printFile.startsWith("-")) {
					buffer.append(" (to the console)").append('\n');
				} else {
					try {
						this.printStream = new PrintStream(
								new BufferedOutputStream(new FileOutputStream(
										printFile)));
					} catch (FileNotFoundException fnfe) {
						// use the console if the output file cannot be created
					}
					buffer.append(" (to file: ").append(printFile)
							.append(")\n");
				}
				continue;
			}
			if (arg.equals("-failure.threshold")) {
				String value = args[i + 1];
				try {
					this.failure_threshold = Integer.parseInt(value);
					if (this.failure_threshold < 0) {
						System.out.println("Value for " + arg
								+ " parameter must be positive.");
						printUsage();
					}
				} catch (NumberFormatException nfe) {
					System.out.println("Invalid value for " + arg
							+ " parameter");
					printUsage();
				}
				buffer.append("	").append(arg).append(" = ").append(value)
						.append('\n');
				i++;
				continue;
			}
			i++;
		}
		if (this.printStream != null) {
			this.printStream.print(buffer.toString());
		}

		// Stop if some mandatory parameters are missing
		if (this.outputDir == null || this.configDescriptors == null
				|| javaVersion == null) {
			printUsage();
		}

		// Set performance results
		setPerformanceResults(currentBuildId, baseline);
	}

	/*
	 * Print component PHP file
	 */
	private void printComponent(
			/* PerformanceResults performanceResults, */String component)
			throws FileNotFoundException {
		if (this.printStream != null)
			this.printStream.print(".");
		File outputFile = new File(this.outputDir, component + ".php");
		PrintStream stream = new PrintStream(new BufferedOutputStream(
				new FileOutputStream(outputFile)));

		// Print header
		boolean isGlobal = component.startsWith("global");
		if (isGlobal) {
			File globalFile = new File(this.outputDir, "global.php");
			PrintStream gStream = new PrintStream(new BufferedOutputStream(
					new FileOutputStream(globalFile)));
			gStream.print(Utils.HTML_OPEN);
			gStream.print("</head>\n");
			gStream.print("<body>\n");
			gStream.print("<?php\n");
			gStream.print("	include(\"global_fp.php\");\n");
			gStream.print("?>\n");
			gStream.print("<table border=0 cellpadding=2 cellspacing=5 width=\"100%\">\n");
			gStream.print("<tbody><tr> <td colspan=3 align=\"left\" bgcolor=\"#0080c0\" valign=\"top\"><b><font color=\"#ffffff\" face=\"Arial,Helvetica\">\n");
			gStream.print("Detailed performance data grouped by scenario prefix</font></b></td></tr></tbody></table>\n");
			gStream.print("<a href=\"org.eclipse.ant.php?\">org.eclipse.ant*</a><br>\n");
			gStream.print("<a href=\"org.eclipse.compare.php?\">org.eclipse.compare*</a><br>\n");
			gStream.print("<a href=\"org.eclipse.core.php?\">org.eclipse.core*</a><br>\n");
			gStream.print("<a href=\"org.eclipse.jdt.core.php?\">org.eclipse.jdt.core*</a><br>\n");
			gStream.print("<a href=\"org.eclipse.jdt.debug.php?\">org.eclipse.jdt.debug*</a><br>\n");
			gStream.print("<a href=\"org.eclipse.jdt.text.php?\">org.eclipse.jdt.text*</a><br>\n");
			gStream.print("<a href=\"org.eclipse.jdt.ui.php?\">org.eclipse.jdt.ui*</a><br>\n");
			gStream.print("<a href=\"org.eclipse.jface.php?\">org.eclipse.jface*</a><br>\n");
			gStream.print("<a href=\"org.eclipse.osgi.php?\">org.eclipse.osgi*</a><br>\n");
			gStream.print("<a href=\"org.eclipse.pde.api.tools.php?\">org.eclipse.pde.api.tools*</a><br>\n");
			gStream.print("<a href=\"org.eclipse.pde.ui.php?\">org.eclipse.pde.ui*</a><br>\n");
			gStream.print("<a href=\"org.eclipse.swt.php?\">org.eclipse.swt*</a><br>\n");
			gStream.print("<a href=\"org.eclipse.team.php?\">org.eclipse.team*</a><br>\n");
			gStream.print("<a href=\"org.eclipse.ua.php?\">org.eclipse.ua*</a><br>\n");
			gStream.print("<a href=\"org.eclipse.ui.php?\">org.eclipse.ui*</a><br><p><br><br>\n");
			gStream.print("</body>\n");
			gStream.print(Utils.HTML_CLOSE);
			gStream.close();
		} else {
			stream.print(Utils.HTML_OPEN);
		}
		stream.print("<link href=\"" + Utils.TOOLTIP_STYLE
				+ "\" rel=\"stylesheet\" type=\"text/css\">\n");
		stream.print("<script src=\"" + Utils.TOOLTIP_SCRIPT + "\"></script>\n");
		stream.print("<script src=\"" + Utils.FINGERPRINT_SCRIPT
				+ "\"></script>\n");
		stream.print(Utils.HTML_DEFAULT_CSS);

		// Print title
		stream.print("<body>");
		printComponentTitle(/* performanceResults, */component, isGlobal,
				stream);

		// print the html representation of fingerprint for each config
		Display display = Display.getDefault();
		if (this.genFingerPrints || this.genAll) {
			final FingerPrint fingerprint = new FingerPrint(component, stream,
					this.outputDir);
			display.syncExec(new Runnable() {
				public void run() {
					try {
						fingerprint
								.print(GenerateResults.this.performanceResults);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
		}
		// FingerPrint fingerprint = new FingerPrint(component, stream,
		// this.outputDir);
		// fingerprint.print(performanceResults);

		// print scenario status table
		if (!isGlobal) {
			// print the component scenario status table beneath the fingerprint
			final ScenarioStatusTable sst = new ScenarioStatusTable(component,
					stream);
			display.syncExec(new Runnable() {
				public void run() {
					try {
						sst.print(GenerateResults.this.performanceResults);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			// ScenarioStatusTable sst = new ScenarioStatusTable(component,
			// stream);
			// sst.print(performanceResults);
		}

		stream.print(Utils.HTML_CLOSE);
		stream.close();
	}

	private void printComponentTitle(
			/* PerformanceResults performanceResults, */String component,
			boolean isGlobal, PrintStream stream) {
		String baselineName = this.performanceResults.getBaselineName();
		String currentName = this.performanceResults.getName();

		// Print title line
		stream.print("<h3>Performance of ");
		if (!isGlobal) {
			stream.print(component);
			stream.print(": ");
		}
		stream.print(currentName);
		stream.print(" relative to ");
		int index = baselineName.indexOf('_');
		if (index > 0) {
			stream.print(baselineName.substring(0, index));
			stream.print(" (");
			index = baselineName.lastIndexOf('_');
			stream.print(baselineName.substring(index + 1,
					baselineName.length()));
			stream.print(')');
		} else {
			stream.print(baselineName);
		}
		stream.print("</h3>\n");

		// Print reference to global results
		if (!isGlobal) {
			stream.print("<?php\n");
			stream.print("	$type=$_SERVER['QUERY_STRING'];\n");
			stream.print("	if ($type==\"\") {\n");
			stream.print("		$type=\"fp_type=0\";\n");
			stream.print("	}\n");
			stream.print("	$href=\"<a href=\\\"performance.php?\";\n");
			stream.print("	$href=$href . $type . \"\\\">Back to global results</a><br><br>\";\n");
			stream.print("	echo $href;\n");
			stream.print("?>\n");
		}
	}

	/*
	 * Print summary of coefficient of variation for each scenario of the given
	 * pattern both for baseline and current builds.
	 */
	private void printSummary(/* PerformanceResults performanceResults */) {
		long start = System.currentTimeMillis();
		if (this.printStream != null)
			this.printStream.print("Print scenarios variations summary...");
		File outputFile = new File(this.outputDir, "cvsummary.html");
		PrintStream stream = null;
		try {
			stream = new PrintStream(new BufferedOutputStream(
					new FileOutputStream(outputFile)));
			printSummaryPresentation(stream);
			// List scenarioNames = DB_Results.getScenarios();
			// int size = scenarioNames.size();
			String[] components = this.performanceResults.getComponents();
			int componentsLength = components.length;
			printSummaryColumnsTitle(stream/* , performanceResults */);
			String[] configs = this.performanceResults
					.getConfigNames(true/* sorted */);
			int configsLength = configs.length;
			for (int i = 0; i < componentsLength; i++) {
				String componentName = components[i];
				List scenarioNames = this.performanceResults
						.getComponentScenarios(componentName);
				int size = scenarioNames.size();
				for (int s = 0; s < size; s++) {
					String scenarioName = ((ScenarioResults) scenarioNames
							.get(s)).getName();
					if (scenarioName == null)
						continue;
					ScenarioResults scenarioResults = this.performanceResults
							.getScenarioResults(scenarioName);
					if (scenarioResults != null) {
						stream.print("<tr>\n");
						for (int j = 0; j < 2; j++) {
							for (int c = 0; c < configsLength; c++) {
								printSummaryScenarioLine(j, configs[c],
										scenarioResults, stream);
							}
						}
						stream.print("<td>");
						stream.print(scenarioName);
						stream.print("</td></tr>\n");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			stream.print("</table></body></html>\n");
			stream.flush();
			stream.close();
		}
		if (this.printStream != null)
			this.printStream.println("done in "
					+ (System.currentTimeMillis() - start) + "ms");
	}

	/*
	 * Print summary presentation (eg. file start and text presenting the
	 * purpose of this file contents)..
	 */
	private void printSummaryPresentation(PrintStream stream) {
		stream.print(Utils.HTML_OPEN);
		stream.print(Utils.HTML_DEFAULT_CSS);
		stream.print("<title>Summary of Elapsed Process Variation Coefficients</title></head>\n");
		stream.print("<body><h3>Summary of Elapsed Process Variation Coefficients</h3>\n");
		stream.print("<p> This table provides a bird's eye view of variability in elapsed process times\n");
		stream.print("for baseline and current build stream performance scenarios.");
		stream.print(" This summary is provided to facilitate the identification of scenarios that should be examined due to high variability.");
		stream.print("The variability for each scenario is expressed as a <a href=\"http://en.wikipedia.org/wiki/Coefficient_of_variation\">coefficient\n");
		stream.print("of variation</a> (CV). The CV is calculated by dividing the <b>standard deviation\n");
		stream.print("of the elapse process time over builds</b> by the <b>average elapsed process\n");
		stream.print("time over builds</b> and multiplying by 100.\n");
		stream.print("</p><p>High CV values may be indicative of any of the following:<br></p>\n");
		stream.print("<ol><li> an unstable performance test. </li>\n");
		stream.print("<ul><li>may be evidenced by an erratic elapsed process line graph.<br><br></li></ul>\n");
		stream.print("<li>performance regressions or improvements at some time in the course of builds.</li>\n");
		stream.print("<ul><li>may be evidenced by plateaus in elapsed process line graphs.<br><br></li></ul>\n");
		stream.print("<li>unstable testing hardware.\n");
		stream.print("<ul><li>consistent higher CV values for one test configuration as compared to others across");
		stream.print(" scenarios may be related to hardward problems.</li></ul></li></ol>\n");
		stream.print("<p> Scenarios are listed in alphabetical order in the far right column. A scenario's\n");
		stream.print("variation coefficients (CVs) are in columns to the left for baseline and current\n");
		stream.print("build streams for each test configuration. Scenarios with CVs > 10% are highlighted\n");
		stream.print("in yellow (10%<CV>&lt;CV<20%) and orange(CV>20%). </p>\n");
		stream.print("<p> Each CV value links to the scenario's detailed results to allow viewers to\n");
		stream.print("investigate the variability.</p>\n");
	}

	/*
	 * Print columns titles of the summary table.
	 */
	private void printSummaryColumnsTitle(PrintStream stream/*
															 * ,
															 * PerformanceResults
															 * performanceResults
															 */) {
		String[] configBoxes = this.performanceResults
				.getConfigBoxes(true/* sorted */);
		int length = configBoxes.length;
		stream.print("<table border=\"1\"><tr><td colspan=\"");
		stream.print(length);
		stream.print("\"><b>Baseline CVs</b></td><td colspan=\"");
		stream.print(length);
		stream.print("\"><b>Current Build Stream CVs</b></td><td rowspan=\"2\"><b>Scenario Name</b></td></tr>\n");
		stream.print("<tr>");
		for (int n = 0; n < 2; n++) {
			for (int c = 0; c < length; c++) {
				stream.print("<td>");
				stream.print(configBoxes[c]);
				stream.print("</td>");
			}
		}
		stream.print("</tr>\n");
	}

	/*
	 * Print a scenario line in the summary table.
	 */
	private void printSummaryScenarioLine(int i, String config,
			ScenarioResults scenarioResults, PrintStream stream) {
		ConfigResults configResults = scenarioResults.getConfigResults(config);
		if (configResults == null || !configResults.isValid()) {
			stream.print("<td>n/a</td>");
			return;
		}
		String url = config + "/" + scenarioResults.getFileName() + ".html";
		double[] stats = null;
		if (i == 0) { // baseline results
			List baselinePrefixes;
			if (this.baselinePrefix == null) {
				baselinePrefixes = Util.BASELINE_BUILD_PREFIXES;
			} else {
				baselinePrefixes = new ArrayList();
				baselinePrefixes.add(this.baselinePrefix);
			}
			stats = configResults.getStatistics(baselinePrefixes);
		} else {
			stats = configResults.getStatistics(this.currentBuildPrefixes);
		}
		double variation = stats[3];
		if (variation > 0.1 && variation < 0.2) {
			stream.print("<td bgcolor=\"yellow\">");
		} else if (variation >= 0.2) {
			stream.print("<td bgcolor=\"FF9900\">");
		} else {
			stream.print("<td>");
		}
		stream.print("<a href=\"");
		stream.print(url);
		stream.print("\"/>");
		stream.print(Util.PERCENTAGE_FORMAT.format(variation));
		stream.print("</a></td>");
	}

	/*
	 * Print usage in case one of the argument of the line was incorrect. Note
	 * that calling this method ends the program run due to final System.exit()
	 */
	private void printUsage() {
		System.out
				.println("Usage:\n\n"
						+ "-baseline\n"
						+ "	Build id against which to compare results.\n"
						+ "	Same as value specified for the \"build\" key in the eclipse.perf.config system property.\n\n"
						+

						"[-baseline.prefix]\n"
						+ "	Optional.  Build id prefix used in baseline test builds and reruns.  Used to plot baseline historical data.\n"
						+ "	A common prefix used for the value of the \"build\" key in the eclipse.perf.config system property when rerunning baseline tests.\n\n"
						+

						"-current\n"
						+ "	build id for which to generate results.  Compared to build id specified in -baseline parameter above.\n"
						+ "	Same as value specified for the \"build\" key in the eclipse.perf.config system property. \n\n"
						+

						"[-current.prefix]\n"
						+ "	Optional.  Comma separated list of build id prefixes used in current build stream.\n"
						+ "	Used to plot current build stream historical data.  Defaults to \"N,I\".\n"
						+ "	Prefixes for values specified for the \"build\" key in the eclipse.perf.config system property. \n\n"
						+

						"-javaVersion\n"
						+ "	Value specified in \"javaVersion\" key in eclipse.perf.config system property for current build.\n\n"
						+

						"-config\n"
						+ "	Comma separated list of config names for which to generate results.\n"
						+ "	Same as values specified in \"config\" key in eclipse.perf.config system property.\n\n"
						+

						"-output\n"
						+ "	Path to default output directory.\n\n"
						+

						"[-config.properties]\n"
						+ "	Optional.  Used by scenario status table to provide the following:\n"
						+ "		alternate descriptions of config values to use in columns.\n"
						+ "	The value should be specified in the following format:\n"
						+ "	name1,description1;name2,description2;etc..\n\n"
						+

						"[-highlight]\n"
						+ "	Optional.  Comma-separated list of build Id prefixes used to find most recent matching for each entry.\n"
						+ "	Result used to highlight points in line graphs.\n\n"
						+

						"[-scenario.pattern]\n"
						+ "	Optional.  Scenario prefix pattern to query database.  If not specified,\n"
						+ "	default of % used in query.\n\n"
						+

						"[-fingerprints]\n"
						+ "	Optional.  Use to generate fingerprints only.\n\n"
						+

						"[-data]\n"
						+ "	Optional.  Generates table of scenario reference and current data with line graphs.\n\n"
						+

						"[-print]\n"
						+ "	Optional.  Display output in the console while generating.\n"
						+

						"[-nophp]\n"
						+ "	Optional.  Generate files for non-php server.\n"
						+

						"[-failure.threshold]\n"
						+ "	Optional.  Set the failure percentage threshold (default is 10%).\n");

		System.exit(1);
	}

	/**
	 * Run the generation from a list of arguments. Typically used to generate
	 * results from an application.
	 */
	public IStatus run(String[] args) {
		parse(args);
		return generate(null);
	}

	/**
	 * Run the generation.
	 */
	public IStatus run(PerformanceResults results, String buildName,
			String baseline, File output, final IProgressMonitor monitor) {
		this.performanceResults = results;
		this.outputDir = output;
		setDefaults(buildName, baseline);
		return generate(monitor);
	}

	/*
	 * Note that all necessary information to generate properly must be set
	 * before calling this method
	 */
	private IStatus generate(final IProgressMonitor monitor) {
		long begin = System.currentTimeMillis();
		int work = 1100;
		int dataWork = 1000 * this.performanceResults.getConfigBoxes(false).length;
		if (this.genAll || this.genData) {
			work += dataWork;
		}
		SubMonitor subMonitor = SubMonitor.convert(monitor, work);
		subMonitor.setTaskName("Generate perf results for build "
				+ this.performanceResults.getName());
		try {

			// Print whole scenarios summary
			if (this.printStream != null)
				this.printStream.println();
			printSummary(/* performanceResults */);

			// Copy images and scripts to output dir
			Bundle bundle = UiPlugin.getDefault().getBundle();
			// URL images = bundle.getEntry("images");
			// if (images != null) {
			// images = FileLocator.resolve(images);
			// Utils.copyImages(new File(images.getPath()), this.outputDir);
			// }
			/*
			 * New way to get images File content =
			 * FileLocator.getBundleFile(bundle); BundleFile bundleFile; if
			 * (content.isDirectory()) { bundleFile = new
			 * DirBundleFile(content);
			 * Utils.copyImages(bundleFile.getFile("images", true),
			 * this.outputDir); } else { bundleFile = new ZipBundleFile(content,
			 * null); Enumeration imageFiles = bundle.findEntries("images",
			 * "*.gif", false); while (imageFiles.hasMoreElements()) { URL url =
			 * (URL) imageFiles.nextElement();
			 * Utils.copyFile(bundleFile.getFile("images"+File.separator+,
			 * true), this.outputDir); } }
			 */
			// Copy bundle files
			Utils.copyBundleFiles(bundle, "images", "*.gif", this.outputDir); // images
			Utils.copyBundleFiles(bundle, "scripts", "*.js", this.outputDir); // java
																				// scripts
			Utils.copyBundleFiles(bundle, "scripts", "*.css", this.outputDir); // styles
			Utils.copyBundleFiles(bundle, "doc", "*.html", this.outputDir); // doc
			Utils.copyBundleFiles(bundle, "doc/images", "*.png", this.outputDir); // images
																					// for
																					// doc
			/*
			 * URL doc = bundle.getEntry("doc"); if (doc != null) { doc =
			 * FileLocator.resolve(doc); File docDir = new File(doc.getPath());
			 * FileFilter filter = new FileFilter() { public boolean accept(File
			 * pathname) { return !pathname.getName().equals("CVS"); } }; File[]
			 * docFiles = docDir.listFiles(filter); for (int i=0;
			 * i<docFiles.length; i++) { File file = docFiles[i]; if
			 * (file.isDirectory()) { File subdir = new File(this.outputDir,
			 * file.getName()); subdir.mkdir(); File[] subdirFiles =
			 * file.listFiles(filter); for (int j=0; j<subdirFiles.length; j++)
			 * { if (subdirFiles[i].isDirectory()) { // expect only one
			 * sub-directory } else { Util.copyFile(subdirFiles[j], new
			 * File(subdir, subdirFiles[j].getName())); } } } else {
			 * Util.copyFile(file, new File(this.outputDir, file.getName())); }
			 * } }
			 */

			// Print HTML pages and all linked files
			if (this.printStream != null) {
				this.printStream
						.println("Print performance results HTML pages:");
				this.printStream.print("	- components main page");
			}
			long start = System.currentTimeMillis();
			// subMonitor.setTaskName("Write fingerprints: 0%");
			// subMonitor.subTask("Global...");
			subMonitor.subTask("Write fingerprints: global (0%)...");
			printComponent(/* performanceResults, */"global_fp");
			subMonitor.worked(100);
			if (subMonitor.isCanceled())
				throw new OperationCanceledException();
			String[] components = this.performanceResults.getComponents();
			int length = components.length;
			int step = 1000 / length;
			int progress = 0;
			for (int i = 0; i < length; i++) {
				int percentage = (int) ((progress / ((double) length)) * 100);
				// subMonitor.setTaskName("Write fingerprints: "+percentage+"%");
				// subMonitor.subTask(components[i]+"...");
				subMonitor.subTask("Write fingerprints: " + components[i]
						+ " (" + percentage + "%)...");
				printComponent(/* performanceResults, */components[i]);
				subMonitor.worked(step);
				if (subMonitor.isCanceled())
					throw new OperationCanceledException();
				progress++;
			}
			if (this.printStream != null) {
				String duration = Util.timeString(System.currentTimeMillis()
						- start);
				this.printStream.println(" done in " + duration);
			}

			// Print the scenarios data
			if (this.genData || this.genAll) {
				start = System.currentTimeMillis();
				if (this.printStream != null)
					this.printStream.println("	- all scenarios data:");
				ScenarioData data = new ScenarioData(this.baselinePrefix,
						this.pointsOfInterest, this.currentBuildPrefixes,
						this.outputDir);
				try {
					data.print(this.performanceResults, this.printStream,
							subMonitor.newChild(dataWork));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				if (this.printStream != null) {
					String duration = Util.timeString(System
							.currentTimeMillis() - start);
					this.printStream.println("	=> done in " + duration);
				}
			}
			if (this.printStream != null) {
				long time = System.currentTimeMillis();
				this.printStream.println("End of generation: "
						+ new SimpleDateFormat("H:mm:ss")
								.format(new Date(time)));
				String duration = Util.timeString(System.currentTimeMillis()
						- begin);
				this.printStream.println("=> done in " + duration);
			}
			return new Status(IStatus.OK, UiPlugin.getDefault().toString(),
					"Everything is OK");
		} catch (OperationCanceledException oce) {
			return new Status(IStatus.OK, UiPlugin.getDefault().toString(),
					"Generation was cancelled!");
		} catch (Exception ex) {
			return new Status(IStatus.ERROR, UiPlugin.getDefault().toString(),
					"An unexpected exception occurred!", ex);
		} finally {
			if (this.printStream != null) {
				this.printStream.flush();
				if (this.printStream != System.out) {
					this.printStream.close();
				}
			}
		}
	}

	private void setDefaults(String buildName, String baseline) {
		if (buildName == null) {
			buildName = this.performanceResults.getName();
		}

		// Set default output dir if not set
		if (this.outputDir.getPath().indexOf(buildName) == -1) {
			File dir = new File(this.outputDir, buildName);
			if (dir.exists() || dir.mkdir()) {
				this.outputDir = dir;
				if (this.printStream != null) {
					this.printStream.println("	+ changed output dir to: "
							+ dir.getPath());
				}
			}
		}

		// Verify that build is known
		String[] builds = this.performanceResults.getAllBuildNames();
		if (builds == null || builds.length == 0) {
			System.err
					.println("Cannot connect to database to generate results build '"
							+ buildName + "'");
			System.exit(1);
		}
		if (Arrays.binarySearch(builds, buildName, Util.BUILD_DATE_COMPARATOR) < 0) {
			throw new RuntimeException("No results in database for build '"
					+ buildName + "'");
		}
		if (this.printStream != null) {
			this.printStream.println();
			this.printStream.flush();
		}

		// Init baseline prefix if not set
		if (this.baselinePrefix == null) {
			int index = baseline.lastIndexOf('_');
			if (index > 0) {
				this.baselinePrefix = baseline.substring(0, index);
			} else {
				// this.baselinePrefix = DB_Results.getDbBaselinePrefix();
				this.baselinePrefix = baseline;
			}
		}

		// Init current build prefixes if not set
		if (this.currentBuildPrefixes == null) {
			this.currentBuildPrefixes = new ArrayList();
			if (buildName.charAt(0) == 'M') {
				this.currentBuildPrefixes.add("M");
			} else {
				this.currentBuildPrefixes.add("N");
			}
			this.currentBuildPrefixes.add("I");
		}
	}

	private void setPerformanceResults(String buildName, String baselineName) {

		// Set performance results
		this.performanceResults = new PerformanceResults(buildName,
				baselineName, this.baselinePrefix, this.printStream);

		// Set defaults
		setDefaults(buildName, this.performanceResults.getBaselineName());

		// Read performance results data
		this.performanceResults.readAll(buildName, this.configDescriptors,
				this.scenarioPattern, this.dataDir, this.failure_threshold,
				null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		// Do nothing
	}

}