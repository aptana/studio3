/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Stepanov
 */
public final class TimeZoneUtil {

	private static final List<String> commonTimeZones = new ArrayList<String>();

	static {
		commonTimeZones.add("EST"); //$NON-NLS-1$
		commonTimeZones.add("CST"); //$NON-NLS-1$
		commonTimeZones.add("MST"); //$NON-NLS-1$
		commonTimeZones.add("PST"); //$NON-NLS-1$
		commonTimeZones.add("PDT"); //$NON-NLS-1$
	}

	/**
	 * 
	 */
	private TimeZoneUtil() {
	}

	public static String getCommonTimeZone(String[] timezones) {
		if (timezones == null || timezones.length == 0) {
			return StringUtil.EMPTY;
		}

		for (String i : timezones) {
			if (commonTimeZones.contains(i)) {
				return i;
			}
		}
		for (String i : timezones) {
			if (i.startsWith("GMT")) { //$NON-NLS-1$
				return i;
			}
		}
		for (String i : timezones) {
			if (i.startsWith("Etc/GMT")) { //$NON-NLS-1$
				return i;
			}
		}
		return timezones[0];
	}
}
