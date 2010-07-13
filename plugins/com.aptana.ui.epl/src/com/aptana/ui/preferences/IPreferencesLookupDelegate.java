package com.aptana.ui.preferences;

public interface IPreferencesLookupDelegate {

	String getString(String qualifier, String key);

	int getInt(String qualifier, String key);

	boolean getBoolean(String qualifier, String key);

}
