/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;

import com.aptana.debug.core.preferences.IJSDebugPreferenceNames;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("deprecation")
public final class JSDetailFormattersManager implements IPropertyChangeListener {
	/**
	 * DETAIL_FORMATTER_IS_ENABLED
	 */
	public static final String DETAIL_FORMATTER_IS_ENABLED = IJSDebugPreferenceNames.DETAIL_FORMATTER_IS_ENABLED;

	/**
	 * DETAIL_FORMATTER_IS_DISABLED
	 */
	public static final String DETAIL_FORMATTER_IS_DISABLED = IJSDebugPreferenceNames.DETAIL_FORMATTER_IS_DISABLED;

	private static JSDetailFormattersManager fgDefault;

	private ListenerList changeListeners = new ListenerList();

	/**
	 * Map of types to the associated formatter (code snippet). (
	 * <code>String</code> -> <code>String</code>)
	 */
	private HashMap<String, DetailFormatter> fDetailFormattersMap;

	private JSDetailFormattersManager() {
		populateDetailFormattersMap();
		JSDebugPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(this);
	}

	/**
	 * getDefault
	 * 
	 * @return JSDetailFormattersManager
	 */
	public static JSDetailFormattersManager getDefault() {
		if (fgDefault == null) {
			fgDefault = new JSDetailFormattersManager();
		}
		return fgDefault;
	}

	/**
	 * Populate the detail formatters map with data from preferences.
	 */
	private void populateDetailFormattersMap() {
		String[] detailFormattersList = JSDebugOptionsManager.parseList(JSDebugPlugin.getDefault()
				.getPluginPreferences().getString(IJSDebugPreferenceNames.PREF_DETAIL_FORMATTERS_LIST));
		fDetailFormattersMap = new HashMap<String, DetailFormatter>(detailFormattersList.length / 3);
		for (int i = 0, length = detailFormattersList.length; i < length;) {
			String typeName = detailFormattersList[i++];
			String snippet = detailFormattersList[i++].replace('\u0000', ',');
			boolean enabled = !DETAIL_FORMATTER_IS_DISABLED.equals(detailFormattersList[i++]);
			fDetailFormattersMap.put(typeName, new DetailFormatter(typeName, snippet, enabled));
		}
	}

	/**
	 * getDetailFormatters
	 * 
	 * @return Collection
	 */
	public Collection<DetailFormatter> getDetailFormatters() {
		return fDetailFormattersMap.values();
	}

	/**
	 * setDetailFormatters
	 * 
	 * @param formatters
	 */
	public void setDetailFormatters(Collection<DetailFormatter> formatters) {
		fDetailFormattersMap.clear();
		for (Iterator<DetailFormatter> i = formatters.iterator(); i.hasNext();) {
			DetailFormatter formatter = (DetailFormatter) i.next();
			fDetailFormattersMap.put(formatter.getTypeName(), formatter);
		}
		savePreference();
	}

	/**
	 * setAssociatedDetailFormatter
	 * 
	 * @param detailFormatter
	 */
	public void setAssociatedDetailFormatter(DetailFormatter detailFormatter) {
		fDetailFormattersMap.put(detailFormatter.getTypeName(), detailFormatter);
		savePreference();
	}

	/**
	 * removeAssociatedDetailFormatter
	 * 
	 * @param detailFormatter
	 */
	public void removeAssociatedDetailFormatter(DetailFormatter detailFormatter) {
		fDetailFormattersMap.remove(detailFormatter.getTypeName());
		savePreference();
	}

	/**
	 * hasAssociatedDetailFormatter
	 * 
	 * @param typeName
	 * @return boolean
	 */
	public boolean hasAssociatedDetailFormatter(String typeName) {
		return fDetailFormattersMap.containsKey(typeName);
	}

	/**
	 * getAssociatedDetailFormatter
	 * 
	 * @param typeName
	 * @return DetailFormatter
	 */
	public DetailFormatter getAssociatedDetailFormatter(String typeName) {
		return (DetailFormatter) fDetailFormattersMap.get(typeName);
	}

	/**
	 * savePreference
	 */
	private void savePreference() {
		Collection<DetailFormatter> valuesList = fDetailFormattersMap.values();
		String[] values = new String[valuesList.size() * 3];
		int i = 0;
		for (Iterator<DetailFormatter> iter = valuesList.iterator(); iter.hasNext();) {
			DetailFormatter detailFormatter = (DetailFormatter) iter.next();
			values[i++] = detailFormatter.getTypeName();
			values[i++] = detailFormatter.getSnippet().replace(',', '\u0000');
			values[i++] = detailFormatter.isEnabled() ? DETAIL_FORMATTER_IS_ENABLED : DETAIL_FORMATTER_IS_DISABLED;
		}
		String pref = JSDebugOptionsManager.serializeList(values);
		JSDebugPlugin.getDefault().getPluginPreferences().setValue(IJSDebugPreferenceNames.PREF_DETAIL_FORMATTERS_LIST,
				pref);
		JSDebugPlugin.getDefault().savePluginPreferences();
	}

	/**
	 * addChangeListener
	 * 
	 * @param listener
	 */
	public void addChangeListener(IDetailFormattersChangeListener listener) {
		changeListeners.add(listener);
	}

	/**
	 * removeChangeListener
	 * 
	 * @param listener
	 */
	public void removeChangeListener(IDetailFormattersChangeListener listener) {
		changeListeners.remove(listener);
	}

	/**
	 * notifyChangeListeners
	 */
	private void notifyChangeListeners() {
		Object[] listeners = changeListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((IDetailFormattersChangeListener) listeners[i]).detailFormattersChanged();
		}
	}

	/**
	 * @param event
	 * 
	 *            see
	 *            org.eclipse.core.runtime.Preferences$IPropertyChangeListener
	 *            #propertyChange
	 *            (org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (IJSDebugPreferenceNames.PREF_DETAIL_FORMATTERS_LIST.equals(event.getProperty())) {
			populateDetailFormattersMap();
			notifyChangeListeners();
		}
	}
}
