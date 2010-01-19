package com.aptana.git.core.model;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IPreferenceConstants;

import junit.framework.TestCase;

public class GitExecutableTest extends TestCase
{
	
	// TODO Test that it picks up pref value for location above all else
	public void testUsesPrefLocationFirst()
	{
		// TODO Need to include some executable that looks like git that we can point to!
		IEclipsePreferences prefs = new InstanceScope().getNode(GitPlugin.getPluginId());
		prefs.put(IPreferenceConstants.GIT_EXECUTABLE_PATH, "");
		
		GitExecutable executable = GitExecutable.instance();
		assertEquals("", executable.path());
	}
	// TODO Test that it reacts to changes in pref location
	// TODO Check that it only handles binaries with version 1.6+
	// TODO Check running basic commands with executable

}
