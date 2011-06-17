package com.aptana.core.tests;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.debug.DebugOptions;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.aptana.core.CorePlugin;
import com.aptana.core.IDebugScopes;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.StringUtil;

public class IdeLogTest extends TestCase
{
	public static String LOG_MESSAGE = "IdeLogTest";
	private LogListener listener;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		// TODO Auto-generated method stub
		super.setUp();
		
		listener = new LogListener();
		CorePlugin.getDefault().getLog().addLogListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		// TODO Auto-generated method stub
		super.tearDown();

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
	private String getCustomMesssage(int severity)
	{
		return LOG_MESSAGE + Long.toHexString(Double.doubleToLongBits(Math.random()));
	}

	/**
	 * Test to see if items are logged with correct severity
	 */
	public void testSeverityLogging()
	{
		boolean isDebugging = Platform.inDebugMode();
		if (isDebugging)
		{
			// Have to turn off platform debugging for a moment
			PlatformUtil.setPlatformDebugging(false);
		}
		
		int currentSeverityPref = IdeLog.getSeverityPreference();

		// We should no messages logged
		IdeLog.setSeverityPreference(IdeLog.OFF);
		listener.reset();
		IdeLog.logError(CorePlugin.getDefault(), getCustomMesssage(IdeLog.ERROR), null, null);
		IdeLog.logWarning(CorePlugin.getDefault(), getCustomMesssage(IdeLog.WARNING), null, null);
		IdeLog.logInfo(CorePlugin.getDefault(), getCustomMesssage(IdeLog.INFO), null, null);
		assertEquals("[OFF] should find 0 messages. Found " + StringUtil.join(",", listener.getMessages()), 0,
				listener.getMessageCount());

		// We should see errors logged
		IdeLog.setSeverityPreference(IdeLog.ERROR);
		listener.reset();
		IdeLog.logError(CorePlugin.getDefault(), getCustomMesssage(IdeLog.ERROR), null, null);
		IdeLog.logWarning(CorePlugin.getDefault(), getCustomMesssage(IdeLog.WARNING), null, null);
		IdeLog.logInfo(CorePlugin.getDefault(), getCustomMesssage(IdeLog.INFO), null, null);
		assertEquals("[OFF] should find 1 messages. Found " + StringUtil.join(",", listener.getMessages()), 1,
				listener.getMessageCount());

		// We should see errors and warnings logged
		IdeLog.setSeverityPreference(IdeLog.WARNING);
		listener.reset();
		IdeLog.logError(CorePlugin.getDefault(), getCustomMesssage(IdeLog.ERROR), null, null);
		IdeLog.logWarning(CorePlugin.getDefault(), getCustomMesssage(IdeLog.WARNING), null, null);
		IdeLog.logInfo(CorePlugin.getDefault(), getCustomMesssage(IdeLog.INFO), null, null);
		assertEquals("[OFF] should find 2 messages. Found " + StringUtil.join(",", listener.getMessages()), 2,
				listener.getMessageCount());

		// We should see errors, warnings, and infos logged
		IdeLog.setSeverityPreference(IdeLog.INFO);
		listener.reset();
		IdeLog.logError(CorePlugin.getDefault(), getCustomMesssage(IdeLog.ERROR), null, null);
		IdeLog.logWarning(CorePlugin.getDefault(), getCustomMesssage(IdeLog.WARNING), null, null);
		IdeLog.logInfo(CorePlugin.getDefault(), getCustomMesssage(IdeLog.INFO), null, null);
		assertEquals("[OFF] should find 3 messages. Found " + StringUtil.join(",", listener.getMessages()), 3,
				listener.getMessageCount());

		IdeLog.setSeverityPreference(currentSeverityPref);
		PlatformUtil.setPlatformDebugging(isDebugging);
	}

	/**
	 * Test to see if items are logged with correct severity
	 */
	public void testSeverityLoggingDebuggerOn()
	{
		// even if debugging is on, this should not affect messages with no scopes attached
		boolean isDebugging = Platform.inDebugMode();
		if (!isDebugging)
		{
			// turn on debugging
			PlatformUtil.setPlatformDebugging(true);
		}

		int currentSeverityPref = IdeLog.getSeverityPreference();

		// We should no messages logged
		IdeLog.setSeverityPreference(IdeLog.OFF);
		listener.reset();
		IdeLog.logError(CorePlugin.getDefault(), getCustomMesssage(IdeLog.ERROR), null, null);
		IdeLog.logWarning(CorePlugin.getDefault(), getCustomMesssage(IdeLog.WARNING), null, null);
		IdeLog.logInfo(CorePlugin.getDefault(), getCustomMesssage(IdeLog.INFO), null, null);
		assertEquals("[OFF] should find 0 messages. Found " + StringUtil.join(",", listener.getMessages()), 0,
				listener.getMessageCount());

		// We should see errors logged
		IdeLog.setSeverityPreference(IdeLog.ERROR);
		listener.reset();
		IdeLog.logError(CorePlugin.getDefault(), getCustomMesssage(IdeLog.ERROR), null, null);
		IdeLog.logWarning(CorePlugin.getDefault(), getCustomMesssage(IdeLog.WARNING), null, null);
		IdeLog.logInfo(CorePlugin.getDefault(), getCustomMesssage(IdeLog.INFO), null, null);
		assertEquals("[ERROR] should find 1 message. Found " + StringUtil.join(",", listener.getMessages()), 1,
				listener.getMessageCount());

		// We should see errors and warnings logged
		IdeLog.setSeverityPreference(IdeLog.WARNING);
		listener.reset();
		IdeLog.logError(CorePlugin.getDefault(), getCustomMesssage(IdeLog.ERROR), null, null);
		IdeLog.logWarning(CorePlugin.getDefault(), getCustomMesssage(IdeLog.WARNING), null, null);
		IdeLog.logInfo(CorePlugin.getDefault(), getCustomMesssage(IdeLog.INFO), null, null);
		assertEquals("[WARNING] should find 2 messages. Found " + StringUtil.join(",", listener.getMessages()), 2,
				listener.getMessageCount());

		// We should see errors, warnings, and infos logged
		IdeLog.setSeverityPreference(IdeLog.INFO);
		listener.reset();
		IdeLog.logError(CorePlugin.getDefault(), getCustomMesssage(IdeLog.ERROR), null, null);
		IdeLog.logWarning(CorePlugin.getDefault(), getCustomMesssage(IdeLog.WARNING), null, null);
		IdeLog.logInfo(CorePlugin.getDefault(), getCustomMesssage(IdeLog.INFO), null, null);
		assertEquals("[INFO] should find 3 messages. Found " + StringUtil.join(",", listener.getMessages()), 3,
				listener.getMessageCount());

		IdeLog.setSeverityPreference(currentSeverityPref);
		PlatformUtil.setPlatformDebugging(isDebugging);
	}

	public void testScopesDebuggerOff()
	{

		// If debugging is off, we write out messages independent of scopes
		// even if debugging is on, this should not affect messages with no scopes attached
		boolean isDebugging = Platform.inDebugMode();
		if (!isDebugging)
		{
			// turn on debugging
			PlatformUtil.setPlatformDebugging(false);
		}

		int currentSeverityPref = IdeLog.getSeverityPreference();

		// We should see all messages logged
		IdeLog.setSeverityPreference(IdeLog.INFO);
		listener.reset();
		IdeLog.logError(CorePlugin.getDefault(), getCustomMesssage(IdeLog.ERROR), IDebugScopes.INDEXER, null);
		IdeLog.logWarning(CorePlugin.getDefault(), getCustomMesssage(IdeLog.WARNING), null, null);
		IdeLog.logInfo(CorePlugin.getDefault(), getCustomMesssage(IdeLog.INFO), IDebugScopes.SHELL, null);
		assertEquals("Debugging off should find 3 messages. Found " + StringUtil.join(",", listener.getMessages()), 3,
				listener.getMessageCount());

		IdeLog.setSeverityPreference(currentSeverityPref);
		PlatformUtil.setPlatformDebugging(isDebugging);

	}

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
			PlatformUtil.setPlatformDebugging(false);
		}

		int currentSeverityPref = IdeLog.getSeverityPreference();

		BundleContext context = CorePlugin.getDefault().getContext();
		ServiceReference sRef = context.getServiceReference(DebugOptions.class.getName());
		DebugOptions options = (DebugOptions) context.getService(sRef);

		options.setOption(IDebugScopes.INDEXER, Boolean.toString(true));
		options.setOption(IDebugScopes.SHELL, Boolean.toString(false));

		// We should see all messages logged
		IdeLog.setSeverityPreference(IdeLog.INFO);
		listener.reset();
		IdeLog.logWarning(CorePlugin.getDefault(), getCustomMesssage(IdeLog.WARNING), null, null);
		IdeLog.logInfo(CorePlugin.getDefault(), getCustomMesssage(IdeLog.INFO), IDebugScopes.SHELL, null);
		IdeLog.logError(CorePlugin.getDefault(), getCustomMesssage(IdeLog.ERROR), IDebugScopes.INDEXER, null);
		assertEquals("Debugging off should find 2 messages. Found " + StringUtil.join(",", listener.getMessages()), 3,
				listener.getMessageCount());

		IdeLog.setSeverityPreference(currentSeverityPref);
		PlatformUtil.setPlatformDebugging(isDebugging);

	}

	class LogListener implements ILogListener
	{
		ArrayList<String> logMessages = new ArrayList<String>();

		public LogListener()
		{
		}

		public boolean foundMessage(String message)
		{
			return logMessages.contains(message);
		}

		public String[] getMessages()
		{
			return logMessages.toArray(new String[0]);
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
	};

}
