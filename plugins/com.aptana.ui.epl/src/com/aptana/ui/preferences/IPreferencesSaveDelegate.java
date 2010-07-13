package com.aptana.ui.preferences;

public interface IPreferencesSaveDelegate
{
	void setString(String qualifier, String key, String value);

	void setInt(String qualifier, String key, int value);

	void setBoolean(String qualifier, String key, boolean value);
}
