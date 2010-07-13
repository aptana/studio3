package com.aptana.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Interface that acts as a bridge to allow preference pages and shared
 * preference/property control blocks to use the {@link ControlBindingManager}.
 * 
 * <p>
 * When implementing this interface, the <code>key</code> parameter should be
 * cast to one of two objects, depending upon the preference implementation used -
 * interfacing with the {@link IPreferenceStore} directly, or using a
 * {@link PreferenceKey}.
 * </p>
 * 
 * @see AbstractOptionsBlock
 * @see ImprovedAbstractConfigurationBlock
 */
public interface IPreferenceDelegate {

	/**
	 * Returns the string value for the given preference key
	 */
	String getString(Object key);

	/**
	 * Returns the boolean value for the given preference key
	 */
	boolean getBoolean(Object key);

	/**
	 * Set a boolean preference value
	 */
	void setBoolean(Object key, boolean value);

	/**
	 * Set a string preference value
	 */
	void setString(Object key, String value);
}
