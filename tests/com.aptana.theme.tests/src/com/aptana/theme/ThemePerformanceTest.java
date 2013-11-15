/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.test.performance.GlobalTimePerformanceTestCase;

import com.aptana.core.epl.util.LRUCache;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.scope.ScopeSelector;
import com.aptana.theme.internal.ThemeManager;

public class ThemePerformanceTest extends GlobalTimePerformanceTestCase
{
	// @formatter:off
	/**
	 * 'com.aptana.theme.ThemePerformanceTest#testLoadingUserBundlesWithCache()' (average over 25 samples):
	 * 
	 * Initial Scenario:
	 *   Elapsed Process:         231ms  (95% in [226ms, 237ms])
	 * 
	 * After caching parse in ScopeSelector constructor / making DelayedTextAttribute fields public/final instead of using accessors:
	 *   Elapsed Process:         156ms  (95% in [151ms, 161ms])
	 *   
	 * After creating class ThemeGetTextAttribute (caching non-separator rules) / not using dont.match
	 *   Elapsed Process:         113ms  (95% in [108ms, 118ms])
	 *   
	 * After caching DelayedTextAttribute
	 *   Elapsed Process:          47ms  (95% in [44ms, 51ms])
	 * 
	 * @throws Exception
	 */
	// @formatter:on
	@SuppressWarnings("rawtypes")
	public void testLoadingUserBundlesWithCache() throws Exception
	{
		// Note: scopes_performance.txt was generated from opening the js editor using google.html
		// attached to https://jira.appcelerator.org/browse/APSTUD-4015.

		URL url = FileLocator.find(ThemePlugin.getDefault().getBundle(),
				Path.fromPortableString("scopes_performance.txt"), null);
		String contents = IOUtil.read(new FileInputStream(new File(FileLocator.toFileURL(url).toURI())));
		List<String> split = StringUtil.split(contents.replace("\r\n", "\n").replace('\r', '\n'), '\n');

		Theme theme = ThemeManager.instance().getTheme("Twilight");

		Method method = Theme.class.getDeclaredMethod("wipeCache");
		method.setAccessible(true);

		Field field = ScopeSelector.class.getDeclaredField("cacheParse");
		field.setAccessible(true);
		LRUCache lru = (LRUCache) field.get(ScopeSelector.class);

		for (int i = 0; i < 25; i++)
		{
			// Clear caches for first open.
			method.invoke(theme);
			lru.flush();

			startMeasuring();
			for (String str : split)
			{
				theme.getTextAttribute(str);
			}
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}
}
