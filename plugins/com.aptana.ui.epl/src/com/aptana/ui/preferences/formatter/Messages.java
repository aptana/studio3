package com.aptana.ui.preferences.formatter;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author 
 *
 */
public class Messages {
	private static final String BUNDLE_NAME = "com.aptana.ide.ui.editors.preferences.formatter.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	/**
	 * @param key
	 * @return String
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
