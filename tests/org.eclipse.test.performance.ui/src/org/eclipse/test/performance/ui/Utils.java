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
package org.eclipse.test.performance.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.test.internal.performance.PerformanceTestPlugin;
import org.eclipse.test.internal.performance.db.Variations;
import org.eclipse.test.internal.performance.results.utils.Util;
import org.osgi.framework.Bundle;


public class Utils {

	public final static double STANDARD_ERROR_THRESHOLD = 0.03; // 3%
	static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance();
	static {
		PERCENT_FORMAT.setMaximumFractionDigits(1);
	}
	static final DecimalFormat DEVIATION_FORMAT = (DecimalFormat) NumberFormat.getPercentInstance();
	static {
		DEVIATION_FORMAT.setMaximumFractionDigits(1);
		DEVIATION_FORMAT.setMinimumFractionDigits(1);
		DEVIATION_FORMAT.setPositivePrefix("+");
		DEVIATION_FORMAT.setNegativePrefix("- ");
	}
	static final DecimalFormat STDERR_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance();
	static {
		STDERR_FORMAT.setMaximumFractionDigits(1);
		STDERR_FORMAT.setMinimumFractionDigits(1);
		STDERR_FORMAT.setMultiplier(100);
	}
	public final static String STANDARD_ERROR_THRESHOLD_STRING = PERCENT_FORMAT.format(STANDARD_ERROR_THRESHOLD);

	// Image files
	public final static String UNKNOWN_IMAGE="images/Unknown.gif";
	public final static String OK_IMAGE="images/OK.gif";
	public final static String OK_IMAGE_WARN="images/OK_caution.gif";
	public final static String FAIL_IMAGE="images/FAIL.gif";
	public final static String FAIL_IMAGE_WARN="images/FAIL_caution.gif";
	public final static String FAIL_IMAGE_EXPLAINED="images/FAIL_greyed.gif";
	public final static String LIGHT="images/light.gif";
	public final static String WARNING_OBJ="images/warning_obj.gif";

	// Java script files
	public final static String TOOLTIP_SCRIPT = "scripts/ToolTip.js";
	public final static String TOOLTIP_STYLE = "scripts/ToolTip.css";
	public final static String FINGERPRINT_SCRIPT = "scripts/Fingerprints.js";

	// Doc files
	public final static String HELP = "doc/help.html";

	// Status
	public final static int OK = 0;
	public final static int NAN = 0x1;
	public final static int ERR = 0x2;

	/**
	 * Return &lt;html&gt;&lt;head&gt;&lt;meta http-equiv="Content-Type"
	 *         content="text/html; charset=iso-8859-1"&gt;
	 */
	public final static String HTML_OPEN = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n";

	/**
	 * Return "&lt;/html&gt;".
	 */
	public final static String HTML_CLOSE = "</html>\n";

	/**
	 * Default style-sheet used on eclipse.org
	 */
	public final static String HTML_DEFAULT_CSS = "<style type=\"text/css\">" + "p, table, td, th {  font-family: arial, helvetica, geneva; font-size: 10pt}\n"
			+ "pre {  font-family: \"Courier New\", Courier, mono; font-size: 10pt}\n" + "h2 { font-family: arial, helvetica, geneva; font-size: 18pt; font-weight: bold ; line-height: 14px}\n"
			+ "code {  font-family: \"Courier New\", Courier, mono; font-size: 10pt}\n" + "sup {  font-family: arial,helvetica,geneva; font-size: 10px}\n"
			+ "h3 {  font-family: arial, helvetica, geneva; font-size: 14pt; font-weight: bold}\n" + "li {  font-family: arial, helvetica, geneva; font-size: 10pt}\n"
			+ "h1 {  font-family: arial, helvetica, geneva; font-size: 28px; font-weight: bold}\n"
			+ "body {  font-family: arial, helvetica, geneva; font-size: 10pt; clip:   rect(   ); margin-top: 5mm; margin-left: 3mm}\n"
			+ ".indextop { font-size: x-large;; font-family: Verdana, Arial, Helvetica, sans-serif; font-weight: bold}\n"
			+ ".indexsub { font-size: xx-small;; font-family: Arial, Helvetica, sans-serif; color: #8080FF}\n" + "</style>\n\n";

	/**
	 * Creates a Variations object using build id pattern, config and jvm.
	 *
	 * @param buildIdPattern
	 * @param config
	 * @param jvm
	 */
	public static Variations getVariations(String buildIdPattern, String config, String jvm) {
		String buildIdPatterns = buildIdPattern.replace(',', '%');
		Variations variations = new Variations();
		variations.put(PerformanceTestPlugin.CONFIG, config);
		variations.put(PerformanceTestPlugin.BUILD, buildIdPatterns);
		variations.put("jvm", jvm);
		return variations;
	}

	/**
	 * Copy all bundle files contained in the given path
	 */
	public static void copyBundleFiles(Bundle bundle, String path, String pattern, File output) {
		Enumeration imageFiles = bundle.findEntries(path, pattern, false);
		while (imageFiles.hasMoreElements()) {
			URL url = (URL) imageFiles.nextElement();
			try {
				File outputFile = new File(output, url.getFile());
				if (!outputFile.getParentFile().exists()) {
					outputFile.getParentFile().mkdirs();
				}
				Util.copyStream(url.openStream(), outputFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Downsample Image to 8 bit depth format so that the resulting image data
	 * can be saved to GIF. Note. If the source image contains photo quality
	 * content with more than 256 colours, resulting data will look very poor.
	 */
	static int closest(RGB[] rgbs, int n, RGB rgb) {
		int minDist = 256 * 256 * 3;
		int minIndex = 0;
		for (int i = 0; i < n; ++i) {
			RGB rgb2 = rgbs[i];
			int da = rgb2.red - rgb.red;
			int dg = rgb2.green - rgb.green;
			int db = rgb2.blue - rgb.blue;
			int dist = da * da + dg * dg + db * db;
			if (dist < minDist) {
				minDist = dist;
				minIndex = i;
			}
		}
		return minIndex;
	}

	static class ColorCounter implements Comparable {
		RGB rgb;

		int count;

		public int compareTo(Object o) {
			return ((ColorCounter) o).count - this.count;
		}
	}

	public static ImageData downSample(Image image) {
		ImageData data = image.getImageData();
		if (!data.palette.isDirect && data.depth <= 8)
			return data;

		// compute a histogram of color frequencies
		HashMap freq = new HashMap();
		int width = data.width;
		int[] pixels = new int[width];
		int[] maskPixels = new int[width];
		for (int y = 0, height = data.height; y < height; ++y) {
			data.getPixels(0, y, width, pixels, 0);
			for (int x = 0; x < width; ++x) {
				RGB rgb = data.palette.getRGB(pixels[x]);
				ColorCounter counter = (ColorCounter) freq.get(rgb);
				if (counter == null) {
					counter = new ColorCounter();
					counter.rgb = rgb;
					freq.put(rgb, counter);
				}
				counter.count++;
			}
		}

		// sort colors by most frequently used
		ColorCounter[] counters = new ColorCounter[freq.size()];
		freq.values().toArray(counters);
		Arrays.sort(counters);

		// pick the most frequently used 256 (or fewer), and make a palette
		ImageData mask = null;
		if (data.transparentPixel != -1 || data.maskData != null) {
			mask = data.getTransparencyMask();
		}
		int n = Math.min(256, freq.size());
		RGB[] rgbs = new RGB[n + (mask != null ? 1 : 0)];
		for (int i = 0; i < n; ++i)
			rgbs[i] = counters[i].rgb;
		if (mask != null) {
			rgbs[rgbs.length - 1] = data.transparentPixel != -1 ? data.palette.getRGB(data.transparentPixel) : new RGB(255, 255, 255);
		}
		PaletteData palette = new PaletteData(rgbs);

		// create a new image using the new palette:
		// for each pixel in the old image, look up the best matching
		// index in the new palette
		ImageData newData = new ImageData(width, data.height, 8, palette);
		if (mask != null)
			newData.transparentPixel = rgbs.length - 1;
		for (int y = 0, height = data.height; y < height; ++y) {
			data.getPixels(0, y, width, pixels, 0);
			if (mask != null)
				mask.getPixels(0, y, width, maskPixels, 0);
			for (int x = 0; x < width; ++x) {
				if (mask != null && maskPixels[x] == 0) {
					pixels[x] = rgbs.length - 1;
				} else {
					RGB rgb = data.palette.getRGB(pixels[x]);
					pixels[x] = closest(rgbs, n, rgb);
				}
			}
			newData.setPixels(0, y, width, pixels, 0);
		}
		return newData;
	}

	/**
	 * Returns the date/time from the build id in format yyyymmddhm
	 *
	 * @param buildId
	 * @return date/time in format YYYYMMDDHHMM, ie. 200504060010
	 */
	public static long getDateFromBuildID(String buildId) {
		return getDateFromBuildID(buildId, false);
	}

	public static long getDateFromBuildID(String buildId, boolean matchLast) {
		Calendar calendar = Calendar.getInstance();

		if (buildId.indexOf('_') != -1) {
			String[] buildIdParts = buildId.split("_");

			int buildIdSegment = 1;
			if (matchLast)
				buildIdSegment = buildIdParts.length - 1;
			// if release build, expect <release>_<release date and
			// timestamp>_<date and timestamp test ran>
			// use test date and time for plotting
			int year = Integer.parseInt(buildIdParts[buildIdSegment].substring(0, 4));
			int month = Integer.parseInt(buildIdParts[buildIdSegment].substring(4, 6)) - 1;
			int date = Integer.parseInt(buildIdParts[buildIdSegment].substring(6, 8));
			int hours = Integer.parseInt(buildIdParts[buildIdSegment].substring(8, 10));
			int min = Integer.parseInt(buildIdParts[buildIdSegment].substring(10, 12));

			calendar.set(year, month, date, hours, min);
			return calendar.getTimeInMillis();

		} else if (buildId.indexOf('-') != -1) {
			// if regular build, expect <buildType><date>-<time> format
			String[] buildIdParts = buildId.split("-");
			int year = Integer.parseInt(buildIdParts[0].substring(1, 5));
			int month = Integer.parseInt(buildIdParts[0].substring(5, 7)) - 1;
			int date = Integer.parseInt(buildIdParts[0].substring(7, 9));
			int hours = Integer.parseInt(buildIdParts[1].substring(0, 2));
			int min = Integer.parseInt(buildIdParts[1].substring(2, 4));
			calendar.set(year, month, date, hours, min);

			return calendar.getTimeInMillis();
		}

		return -1;
	}

	/**
	 * Returns a message corresponding to given statistics.
	 *
	 * @param resultStats The value with its standard error
	 * @param full
	 * @return The failure message. May be empty if stats are good...
	 */
	public static String failureMessage(double[] resultStats, boolean full) {
		StringBuffer buffer = new StringBuffer();
		int level = confidenceLevel(resultStats);
//		boolean isWarn = (level & WARN) != 0;
		boolean isErr = (level & ERR) != 0;
		if (full) {
			if (isErr) {
				buffer.append("*** WARNING ***  ");
	 			buffer.append(Messages.bind(Messages.standardError, PERCENT_FORMAT.format(resultStats[1]), STANDARD_ERROR_THRESHOLD_STRING));
			}
			return buffer.toString();
		}
		if (resultStats != null) {
			double deviation = resultStats[0];
			buffer.append("<font color=\"#0000FF\" size=\"1\">");
			if (Double.isNaN(deviation) || Double.isInfinite(deviation)) {
	 			buffer.append(" [n/a]");
 			} else {
				double stderr = resultStats[1];
				deviation = Math.abs(deviation)<0.001 ? 0 : -deviation;
	 			if (Double.isNaN(stderr) || Double.isInfinite(stderr)) {
		 			buffer.append(DEVIATION_FORMAT.format(deviation));
					buffer.append("</font><font color=\"#DDDD00\" size=\"1\"> ");
		 			buffer.append(" [n/a]");
	 			} else {
		 			buffer.append(DEVIATION_FORMAT.format(deviation));
	 				buffer.append(" [&#177;");
	 				buffer.append(STDERR_FORMAT.format(Math.abs(stderr)));
	 				buffer.append(']');
	 			}
 			}
			buffer.append("</font>");
		}
		return buffer.toString();
	}

	/**
	 * Returns the confidence level for given statistics:
	 * <ul>
	 * <li>{@link #NAN}: if the value is infinite or not a number</li>
	 * <li>{@link #ERR}: if the standard error is over the expected threshold ({@link #STANDARD_ERROR_THRESHOLD})</li>
	 * <li>{@link #OK}: in all other cases</li>
	 * </ul>
	 *
	 * @param resultStats array of 2 doubles, the former is the average value and
	 * 	the latter is the standard error made while computing the average.
	 * @return a value telling caller the level of confidence of the provided value
	 */
	public static int confidenceLevel(double[] resultStats) {
		int level = OK;
 		if (resultStats != null){
			if (Double.isNaN(resultStats[0]) || Double.isInfinite(resultStats[0])) {
				level = NAN;
 			} else {
//	 			if (resultStats[1] >= (STANDARD_ERROR_THRESHOLD/2)) { // warns standard error higher than the half of authorized threshold
//	 				level |= WARN;
//	 			}
	 			if (resultStats[1] >= STANDARD_ERROR_THRESHOLD) { // standard error higher than the authorized threshold
	 				level = ERR;
	 			}
 			}
 		}
		return level;
	}

	/**
	 * Get an icon image corresponding to a given level of confidence and explanation.
	 *
	 * @param confidence the confiden level
	 * @param hasExplanation flags indicates whether the confidence may be tempered by an explanation
	 * @return Corresponding image
	 */
	public static String getImage(int confidence, boolean scenarioFailed, boolean hasExplanation) {
	    String image = null;

	    if (scenarioFailed) {
	    	if (hasExplanation) {
		    	image = FAIL_IMAGE_EXPLAINED;
		    } else if ((confidence & ERR) != 0) {
    			image = FAIL_IMAGE_WARN;
		    } else {
    			image = FAIL_IMAGE;
		    }
	    } else if ((confidence & NAN) != 0) {
			image = UNKNOWN_IMAGE;
	    } else if ((confidence & ERR) != 0) {
	   		image = OK_IMAGE_WARN;
	    } else {
   			image = OK_IMAGE;
	    }
	    return image;
    }

/**
 * @param outputFile
 * @param image
 */
public static void saveImage(File outputFile, Image image) {
	// Save image
	ImageData data = downSample(image);
	ImageLoader imageLoader = new ImageLoader();
	imageLoader.data = new ImageData[] { data };

	OutputStream out = null;
	try {
		out = new BufferedOutputStream(new FileOutputStream(outputFile));
		imageLoader.save(out, SWT.IMAGE_GIF);
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} finally {
		image.dispose();
		if (out != null) {
			try {
				out.close();
			} catch (IOException e1) {
				// silently ignored
			}
		}
	}
}

}