package com.aptana.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.CorePlugin;
import com.aptana.core.ICorePreferenceConstants;
import com.aptana.core.IDebugScopes;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.logging.IdeLog.StatusLevel;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;

public class IdeLogTest
{
	public static String LOG_MESSAGE = "IdeLogTest";
	private LogListener listener;

	@Before
	public void setUp() throws Exception
	{
		listener = new LogListener();
		CorePlugin.getDefault().getLog().addLogListener(listener);
		IdeLog.flushCache();
	}

	@After
	public void tearDown() throws Exception
	{
		if (listener != null)
		{
			CorePlugin.getDefault().getLog().removeLogListener(listener);
		}
	}

	/**
	 * Get message
	 * 
	 * @param severity
	 * @return
	 */
	private String getCustomMesssage(IdeLog.StatusLevel severity)
	{
		return LOG_MESSAGE + Long.toHexString(Double.doubleToLongBits(Math.random()));
	}

	/**
	 * Test to see if items are logged with correct severity
	 */
	@Test
	public void testSeverityLogging()
	{
		boolean isDebugging = Platform.inDebugMode();
		if (isDebugging)
		{
			// Have to turn off platform debugging for a moment
			EclipseUtil.setPlatformDebugging(false);
		}

		IdeLog.StatusLevel currentSeverityPref = IdeLog.getCurrentSeverity();

		Plugin plugin = CorePlugin.getDefault();

		// We should no messages logged
		IdeLog.setCurrentSeverity(IdeLog.StatusLevel.OFF);
		listener.reset();

		assertFalse(IdeLog.isErrorEnabled(plugin, null));
		assertFalse(IdeLog.isWarningEnabled(plugin, null));
		assertFalse(IdeLog.isInfoEnabled(plugin, null));

		IdeLog.logError(plugin, getCustomMesssage(IdeLog.StatusLevel.ERROR));
		IdeLog.logWarning(plugin, getCustomMesssage(IdeLog.StatusLevel.WARNING));
		IdeLog.logInfo(plugin, getCustomMesssage(IdeLog.StatusLevel.INFO));
		assertEquals("[OFF] should find 0 messages. Found " + StringUtil.join(",", listener.getMessages()), 0,
				listener.getMessageCount());

		// We should see errors logged
		IdeLog.setCurrentSeverity(IdeLog.StatusLevel.ERROR);
		listener.reset();

		assertTrue(IdeLog.isErrorEnabled(plugin, null));
		assertFalse(IdeLog.isWarningEnabled(plugin, null));
		assertFalse(IdeLog.isInfoEnabled(plugin, null));

		IdeLog.logError(plugin, getCustomMesssage(IdeLog.StatusLevel.ERROR), (String) null);
		IdeLog.logWarning(plugin, getCustomMesssage(IdeLog.StatusLevel.WARNING), (String) null);
		IdeLog.logInfo(plugin, getCustomMesssage(IdeLog.StatusLevel.INFO), null);
		assertEquals("[OFF] should find 1 messages. Found " + StringUtil.join(",", listener.getMessages()), 1,
				listener.getMessageCount());

		// We should see errors and warnings logged
		IdeLog.setCurrentSeverity(IdeLog.StatusLevel.WARNING);
		listener.reset();

		assertTrue(IdeLog.isErrorEnabled(plugin, null));
		assertTrue(IdeLog.isWarningEnabled(plugin, null));
		assertFalse(IdeLog.isInfoEnabled(plugin, null));

		IdeLog.logError(plugin, getCustomMesssage(IdeLog.StatusLevel.ERROR), null, null);
		IdeLog.logWarning(plugin, getCustomMesssage(IdeLog.StatusLevel.WARNING), null, null);
		IdeLog.logInfo(plugin, getCustomMesssage(IdeLog.StatusLevel.INFO), null, null);
		assertEquals("[OFF] should find 2 messages. Found " + StringUtil.join(",", listener.getMessages()), 2,
				listener.getMessageCount());

		// We should see errors, warnings, and infos logged
		IdeLog.setCurrentSeverity(IdeLog.StatusLevel.INFO);
		listener.reset();

		assertTrue(IdeLog.isErrorEnabled(plugin, null));
		assertTrue(IdeLog.isWarningEnabled(plugin, null));
		assertTrue(IdeLog.isInfoEnabled(plugin, null));

		IdeLog.logError(plugin, getCustomMesssage(IdeLog.StatusLevel.ERROR));
		IdeLog.logWarning(plugin, getCustomMesssage(IdeLog.StatusLevel.WARNING));
		IdeLog.logInfo(plugin, getCustomMesssage(IdeLog.StatusLevel.INFO));
		assertEquals("[OFF] should find 3 messages. Found " + StringUtil.join(",", listener.getMessages()), 3,
				listener.getMessageCount());

		IdeLog.setCurrentSeverity(currentSeverityPref);
		EclipseUtil.setPlatformDebugging(isDebugging);
	}

	/**
	 * Test to see if items are logged with correct severity
	 */
	@Test
	public void testSeverityLoggingDebuggerOn()
	{
		// even if debugging is on, this should not affect messages with no scopes attached
		boolean isDebugging = Platform.inDebugMode();
		if (!isDebugging)
		{
			// turn on debugging
			EclipseUtil.setPlatformDebugging(true);
		}

		IdeLog.StatusLevel currentSeverityPref = IdeLog.getCurrentSeverity();

		Plugin plugin = CorePlugin.getDefault();

		// We should no messages logged
		IdeLog.setCurrentSeverity(IdeLog.StatusLevel.OFF);
		listener.reset();

		assertFalse(IdeLog.isErrorEnabled(plugin, null));
		assertFalse(IdeLog.isWarningEnabled(plugin, null));
		assertFalse(IdeLog.isInfoEnabled(plugin, null));

		IdeLog.logError(plugin, getCustomMesssage(IdeLog.StatusLevel.ERROR), null, null);
		IdeLog.logWarning(plugin, getCustomMesssage(IdeLog.StatusLevel.WARNING), null, null);
		IdeLog.logInfo(plugin, getCustomMesssage(IdeLog.StatusLevel.INFO), null, null);
		assertEquals("[OFF] should find 0 messages. Found " + StringUtil.join(",", listener.getMessages()), 0,
				listener.getMessageCount());

		// We should see errors logged
		IdeLog.setCurrentSeverity(IdeLog.StatusLevel.ERROR);
		listener.reset();

		assertTrue(IdeLog.isErrorEnabled(plugin, null));
		assertFalse(IdeLog.isWarningEnabled(plugin, null));
		assertFalse(IdeLog.isInfoEnabled(plugin, null));

		IdeLog.logError(plugin, getCustomMesssage(IdeLog.StatusLevel.ERROR), null, null);
		IdeLog.logWarning(plugin, getCustomMesssage(IdeLog.StatusLevel.WARNING), null, null);
		IdeLog.logInfo(plugin, getCustomMesssage(IdeLog.StatusLevel.INFO), null, null);
		assertEquals("[ERROR] should find 1 message. Found " + StringUtil.join(",", listener.getMessages()), 1,
				listener.getMessageCount());

		// We should see errors and warnings logged
		IdeLog.setCurrentSeverity(IdeLog.StatusLevel.WARNING);
		listener.reset();

		assertTrue(IdeLog.isErrorEnabled(plugin, null));
		assertTrue(IdeLog.isWarningEnabled(plugin, null));
		assertFalse(IdeLog.isInfoEnabled(plugin, null));

		IdeLog.logError(plugin, getCustomMesssage(IdeLog.StatusLevel.ERROR), null, null);
		IdeLog.logWarning(plugin, getCustomMesssage(IdeLog.StatusLevel.WARNING), null, null);
		IdeLog.logInfo(plugin, getCustomMesssage(IdeLog.StatusLevel.INFO), null, null);
		assertEquals("[WARNING] should find 2 messages. Found " + StringUtil.join(",", listener.getMessages()), 2,
				listener.getMessageCount());

		// We should see errors, warnings, and infos logged
		IdeLog.setCurrentSeverity(IdeLog.StatusLevel.INFO);
		listener.reset();

		assertTrue(IdeLog.isErrorEnabled(plugin, null));
		assertTrue(IdeLog.isWarningEnabled(plugin, null));
		assertTrue(IdeLog.isInfoEnabled(plugin, null));

		IdeLog.logError(plugin, getCustomMesssage(IdeLog.StatusLevel.ERROR), null, null);
		IdeLog.logWarning(plugin, getCustomMesssage(IdeLog.StatusLevel.WARNING), null, null);
		IdeLog.logInfo(plugin, getCustomMesssage(IdeLog.StatusLevel.INFO), null, null);
		assertEquals("[INFO] should find 3 messages. Found " + StringUtil.join(",", listener.getMessages()), 3,
				listener.getMessageCount());

		IdeLog.setCurrentSeverity(currentSeverityPref);
		EclipseUtil.setPlatformDebugging(isDebugging);
	}

	@Test
	public void testScopesDebuggerOff()
	{
		// If debugging is off, we write out messages independent of scopes
		// even if debugging is on, this should not affect messages with no scopes attached
		boolean isDebugging = Platform.inDebugMode();
		if (!isDebugging)
		{
			// turn on debugging
			EclipseUtil.setPlatformDebugging(false);
		}

		IdeLog.StatusLevel currentSeverityPref = IdeLog.getCurrentSeverity();

		Plugin plugin = CorePlugin.getDefault();

		// We should see all messages logged
		IdeLog.setCurrentSeverity(IdeLog.StatusLevel.INFO);
		listener.reset();

		assertTrue(IdeLog.isErrorEnabled(plugin, IDebugScopes.BUILDER));
		assertTrue(IdeLog.isWarningEnabled(plugin, null));
		assertTrue(IdeLog.isInfoEnabled(plugin, IDebugScopes.SHELL));

		IdeLog.logError(plugin, getCustomMesssage(IdeLog.StatusLevel.ERROR), null, IDebugScopes.BUILDER);
		IdeLog.logWarning(plugin, getCustomMesssage(IdeLog.StatusLevel.WARNING), null, null);
		IdeLog.logInfo(plugin, getCustomMesssage(IdeLog.StatusLevel.INFO), null, IDebugScopes.SHELL);
		assertEquals("Debugging off should find 3 messages. Found " + StringUtil.join(",", listener.getMessages()), 3,
				listener.getMessageCount());

		IdeLog.setCurrentSeverity(currentSeverityPref);
		EclipseUtil.setPlatformDebugging(isDebugging);
	}

	@Test
	public void testScopesDebuggerOn()
	{
		// If debugging is on, we write out messages is the scope is null or there is a match

		// save current scope setting
		// set scope for CorePlugin on
		// write out messages with no scope
		// write out messages with wrong scope
		// write out messages with correct scope

		// If debugging is off, we write out messages independent of scopes
		// even if debugging is on, this should not affect messages with no scopes attached
		boolean isDebugging = Platform.inDebugMode();
		if (!isDebugging)
		{
			// turn on debugging
			EclipseUtil.setPlatformDebugging(true);
		}

		IdeLog.StatusLevel currentSeverityPref = IdeLog.getCurrentSeverity();

		CorePlugin plugin = CorePlugin.getDefault();

		EclipseUtil.setBundleDebugOptions(new String[] { IDebugScopes.SHELL }, false);
		EclipseUtil.setBundleDebugOptions(new String[] { IDebugScopes.BUILDER }, true);

		// We should see all messages logged
		IdeLog.setCurrentSeverity(IdeLog.StatusLevel.INFO);
		listener.reset();

		assertTrue(IdeLog.isErrorEnabled(plugin, IDebugScopes.BUILDER));
		assertTrue(IdeLog.isWarningEnabled(plugin, null));
		assertFalse(IdeLog.isInfoEnabled(plugin, IDebugScopes.SHELL));

		IdeLog.logError(plugin, getCustomMesssage(IdeLog.StatusLevel.ERROR), null, IDebugScopes.BUILDER);
		IdeLog.logWarning(plugin, getCustomMesssage(IdeLog.StatusLevel.WARNING), null, null);
		IdeLog.logInfo(plugin, getCustomMesssage(IdeLog.StatusLevel.INFO), null, IDebugScopes.SHELL);
		assertEquals("Debugging off should find 2 messages. Found " + StringUtil.join(",", listener.getMessages()), 2,
				listener.getMessageCount());

		IdeLog.setCurrentSeverity(currentSeverityPref);
		EclipseUtil.setPlatformDebugging(isDebugging);
	}

	@Test
	public void testPreferenceChange()
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(CorePlugin.PLUGIN_ID);
		prefs.put(ICorePreferenceConstants.PREF_DEBUG_LEVEL, StatusLevel.INFO.toString());
		assertEquals(StatusLevel.INFO, IdeLog.getCurrentSeverity());

		prefs.put(ICorePreferenceConstants.PREF_DEBUG_LEVEL, StatusLevel.ERROR.toString());
		assertEquals(StatusLevel.ERROR, IdeLog.getCurrentSeverity());
	}

	private static class LogListener implements ILogListener
	{
		List<String> logMessages = new ArrayList<String>();

		public LogListener()
		{
		}

		public String[] getMessages()
		{
			return logMessages.toArray(new String[logMessages.size()]);
		}

		public int getMessageCount()
		{
			return logMessages.size();
		}

		public void reset()
		{
			logMessages = new ArrayList<String>();
		}

		public void logging(IStatus status, String plugin)
		{
			if (status.getMessage().contains(LOG_MESSAGE))
			{
				logMessages.add(status.getMessage());
			}
		}
	}
}
