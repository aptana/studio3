package com.aptana.ui.preferences;

/**
 * Returns the prompt that should be used in the popup box that indicates a
 * build needs to occur.
 */
public interface IPreferenceChangeRebuildPrompt {

	/**
	 * Returns the title
	 * 
	 * @return
	 */
	String getTitle();

	/**
	 * Returns the message
	 * 
	 * @return
	 */
	String getMessage();

}
