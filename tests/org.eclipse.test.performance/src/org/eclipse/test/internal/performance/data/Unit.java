/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.data;

import java.text.NumberFormat;


/**
 * @since 3.1
 */
public class Unit {

	public static final Unit SECOND= new Unit("s", "second", false);  //$NON-NLS-1$ //$NON-NLS-2$
	public static final Unit BYTE= new Unit("byte", "byte", true);  //$NON-NLS-1$ //$NON-NLS-2$
	public static final Unit CARDINAL= new Unit("", "", false);  //$NON-NLS-1$ //$NON-NLS-2$
	public static final Unit INVOCATION= new Unit("invoc.", "invocation", false);  //$NON-NLS-1$ //$NON-NLS-2$

	private static final int T_DECIMAL= 1000;
	private static final int T_BINARY= 1024;
	
	//protected static final String[] PREFIXES= new String[] { "y", "z", "a", "f", "p", "n", "u", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y" };
	//protected static final String[] FULL_PREFIXES= new String[] { "yocto", "zepto", "atto", "femto", "pico", "nano", "micro", "milli", "", "kilo", "mega", "giga", "tera", "peta", "exa", "zetta", "yotta" };
	//protected static final String[] BINARY_PREFIXES= new String[] { "", "", "", "", "", "", "", "", "", "ki", "Mi", "Gi", "Ti", "Pi", "Ei", "Zi", "Yi" };
	//protected static final String[] BINARY_FULL_PREFIXES= new String[] { "", "", "", "", "", "", "", "", "", "kibi", "mebi", "gibi", "tebi", "pebi", "exbi", "zebi", "yobi" };
	
	private final String fShortName;
	private final String fFullName;
	private final boolean fIsBinary;
	//private final int fPrecision= 2;
	
	public Unit(String shortName, String fullName, boolean binary) {
		fShortName= shortName;
		fFullName= fullName;
		fIsBinary= binary;
	}
	
	public String getShortName() {
		return fShortName;
	}
	
	public String getFullName() {
		return fFullName;
	}
	
	public String getDisplayValue1(long magnitudel, int multiplier) {
	    
	    //return getDisplayValue1(magnitudel / multiplier);
	    //return Long.toString((double)(magnitudel / multiplier));
	    return getDisplayValue1(magnitudel / multiplier);
	}

	public String getDisplayValue1(double magnitude) {
	    
	    if (this == SECOND)
	        return formatedTime((long) (magnitude*1000.0));
	    return formatEng((long) (magnitude));
	    
	    /*
		int div= fIsBinary ? T_BINARY : T_DECIMAL;
		boolean negative= magnitude < 0;
		double mag= Math.abs(magnitude), ratio= mag / div;
		int divs= PREFIXES.length / 2;
		while (ratio >= 1) {
			mag= ratio;
			divs++;
			ratio= mag / div;
		}
		ratio= mag * div;
		while (ratio > 0.0 && ratio < div) {
			mag= ratio;
			divs--;
			ratio= mag * div;
		}
		
		if (negative)
			mag= -mag;
		
		String[] prefixes= fIsBinary ? BINARY_PREFIXES : PREFIXES;
		NumberFormat format= NumberFormat.getInstance();
		format.setMaximumFractionDigits(fPrecision);
		if (divs > 0 && divs <= prefixes.length)
			return prefixes[divs] + getShortName() + format.format(mag);
		else
			return getShortName() + magnitude;
		*/
	}
	
	public String toString() {
		return "Unit [" + getShortName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Answer a formatted string for the elapsed time (minutes, hours or days) 
	 * that is appropriate for the scale of the time.
	 * 
	 * @param diff time in milliseconds
	 * 
	 * I copied this from karasiuk.utility.TimeIt
	 * @return the formatted time
	 */
	public static String formatedTime(long diff) {
		long sign= diff < 0 ? -1 : 1;
		diff = Math.abs(diff);
		
		if (diff < 1000)
			return String.valueOf(sign * diff) + "ms"; //$NON-NLS-1$
		
		NumberFormat nf= NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		double d = diff / 1000.0;	
		if (d < 60)
			return nf.format(sign * d) + "s"; //$NON-NLS-1$
		
		d = d / 60.0;
		if (d < 60.0)
			return nf.format(sign * d) + "m"; //$NON-NLS-1$
	
		d = d / 60.0;
		if (d < 24.0)
			return nf.format(sign * d) + "h"; //$NON-NLS-1$
	
		d = d / 24.0;
		return nf.format(sign * d) + "d"; //$NON-NLS-1$
	}
	
	/**
	 * Answer a number formatted using engineering conventions, K thousands, M millions,
	 * G billions and T trillions.
	 * 
	 * I copied this method from karasiuk.utility.Misc.
	 * @param n the number to format
	 * @return the formatted number
	 */
	public String formatEng(long n) {
		long sign= n < 0 ? -1 : 1;
		n = Math.abs(n);
	    int TSD= fIsBinary ? T_BINARY : T_DECIMAL;
		if (n < TSD)
			return String.valueOf(sign*n);
		double d = ((double)n) / TSD;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		if (d < TSD)
			return nf.format(sign * d) + "K"; //$NON-NLS-1$
		
		d = d / TSD;
		if ( d < TSD)
			return nf.format(sign * d) + "M"; //$NON-NLS-1$
		
		d = d / TSD;
		if ( d < TSD)
			return nf.format(sign * d) + "G"; //$NON-NLS-1$
		
		d = d / TSD;
		return nf.format(sign * d) + "T"; //$NON-NLS-1$
	}

}
