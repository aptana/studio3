/**
 * 
 */
package com.aptana.formatter.ui.profile;

import org.eclipse.osgi.util.NLS;

/**
 * @author Yuri Strot
 *
 */
public class ProfilesMessages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.formatter.ui.profile.ProfilesMessages"; //$NON-NLS-1$
	public static String ProfileStore_noValueForKey;
	public static String ProfileStore_readingProblems;
	public static String ProfileStore_serializingProblems;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, ProfilesMessages.class);
	}

	private ProfilesMessages() {
	}
}
