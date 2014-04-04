package com.aptana.usage.internal;

import static org.junit.Assert.assertEquals;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.usage.AnalyticsEvent;
import com.aptana.usage.AnalyticsLogger;
import com.aptana.usage.IAnalyticsUser;
import com.aptana.usage.IAnalyticsUserManager;
import com.aptana.usage.IUsageSystemProperties;

public class DefaultAnalyticsEventHandlerTest
{

	private Mockery context;
	private DefaultAnalyticsEventHandler handler;
	private IAnalyticsUserManager userManager;
	private AnalyticsLogger logger;
	private IAnalyticsUser user;
	private HttpURLConnection connection;
	private DataOutputStream outputStream;

	@Before
	public void setup() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
				setThreadingPolicy(new Synchroniser());
			}
		};
		userManager = context.mock(IAnalyticsUserManager.class);
		logger = context.mock(AnalyticsLogger.class);
		user = context.mock(IAnalyticsUser.class);
		connection = context.mock(HttpURLConnection.class);
		outputStream = context.mock(DataOutputStream.class);
		handler = new DefaultAnalyticsEventHandler()
		{
			@Override
			protected IAnalyticsUserManager getUserManager()
			{
				return userManager;
			}

			@Override
			protected AnalyticsLogger getAnalyticsLogger()
			{
				return logger;
			}

			@Override
			protected HttpURLConnection createConnection(URL url) throws IOException
			{
				return connection;
			}

			@Override
			protected DataOutputStream createOutputStream(HttpURLConnection connection, String data) throws IOException
			{
				return outputStream;
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		context = null;
		handler = null;
		logger = null;
		userManager = null;
		user = null;
		connection = null;
		outputStream = null;
		System.clearProperty(IUsageSystemProperties.ANALYTICS_URL);
	}

	@Test
	public void testGetDefaultAnalyticsURL()
	{
		assertEquals(DefaultAnalyticsEventHandler.DEFAULT_URL, handler.getAnalyticsURL());
	}

	@Test
	public void testAnalyticsURLCanBeOverridenBySystemProperty()
	{
		final String newURL = "http://example.com";
		System.setProperty(IUsageSystemProperties.ANALYTICS_URL, newURL);
		assertEquals(newURL, new DefaultAnalyticsEventHandler().getAnalyticsURL());
	}

	@Test
	public void testGetDefaultTimeout()
	{
		assertEquals(DefaultAnalyticsEventHandler.DEFAULT_TIMEOUT, handler.getTimeout());
	}

	@Test
	public void testSendEvent() throws Exception
	{
		final AnalyticsEvent event = new AnalyticsEvent("ti.start", "ti.start", null);
		context.checking(new Expectations()
		{
			{
				allowing(userManager).getUser();
				will(returnValue(user));

				allowing(user).getCookie();
				will(returnValue("cookie"));

				allowing(user).getGUID();
				will(returnValue("guid"));

				allowing(user).isOnline();
				will(returnValue(true));

				oneOf(connection).setRequestProperty("Cookie", "cookie; uid=guid");
				oneOf(connection).setRequestProperty("User-Agent", AnalyticsEvent.getUserAgent());
				oneOf(connection).setDoOutput(true);
				oneOf(connection).setReadTimeout(DefaultAnalyticsEventHandler.DEFAULT_TIMEOUT);
				oneOf(connection).setConnectTimeout(DefaultAnalyticsEventHandler.DEFAULT_TIMEOUT);
				oneOf(connection).setRequestMethod("POST");

				// TODO Add some verification of the payload sent!

				oneOf(connection).getResponseCode();
				will(returnValue(200));

				oneOf(outputStream).close();

				oneOf(connection).disconnect();

				never(logger).logEvent(event);

				allowing(logger).getEvents();
				will(returnValue(Collections.emptyList()));
			}
		});
		handler.sendEvent(event);
		context.assertIsSatisfied();
	}

	@Test
	public void testSendEventWithOfflineUser() throws Exception
	{
		final AnalyticsEvent event = new AnalyticsEvent("ti.start", "ti.start", null);
		context.checking(new Expectations()
		{
			{
				allowing(userManager).getUser();
				will(returnValue(user));

				allowing(user).isOnline();
				will(returnValue(false));

				// User is offline, so we log event for later
				oneOf(logger).logEvent(event);

				// Never invoke anything to send!
				never(user).getCookie();
				will(returnValue("cookie"));

				never(user).getGUID();
				will(returnValue("guid"));

				never(connection).setRequestProperty("Cookie", "cookie; uid=guid");
				never(connection).setRequestProperty("User-Agent", AnalyticsEvent.getUserAgent());
				never(connection).setDoOutput(true);
				never(connection).setReadTimeout(DefaultAnalyticsEventHandler.DEFAULT_TIMEOUT);
				never(connection).setConnectTimeout(DefaultAnalyticsEventHandler.DEFAULT_TIMEOUT);
				never(connection).setRequestMethod("POST");

				never(connection).getResponseCode();
				will(returnValue(200));

				never(outputStream).close();

				never(connection).disconnect();
			}
		});
		handler.sendEvent(event);
		context.assertIsSatisfied();
	}

	@Test
	public void testSendEventWithNullUser() throws Exception
	{
		final AnalyticsEvent event = new AnalyticsEvent("ti.start", "ti.start", null);
		context.checking(new Expectations()
		{
			{
				allowing(userManager).getUser();
				will(returnValue(null));

				// User is offline, so we log event for later
				oneOf(logger).logEvent(event);

				// Never invoke anything to send!
				never(connection).setRequestProperty("Cookie", "cookie; uid=guid");
				never(connection).setRequestProperty("User-Agent", AnalyticsEvent.getUserAgent());
				never(connection).setDoOutput(true);
				never(connection).setReadTimeout(DefaultAnalyticsEventHandler.DEFAULT_TIMEOUT);
				never(connection).setConnectTimeout(DefaultAnalyticsEventHandler.DEFAULT_TIMEOUT);
				never(connection).setRequestMethod("POST");

				never(connection).getResponseCode();
				will(returnValue(200));

				never(outputStream).close();

				never(connection).disconnect();
			}
		});
		handler.sendEvent(event);
		context.assertIsSatisfied();
	}

	@Test
	public void testSendEventAnonymouslyWithNoUserManager() throws Exception
	{
		userManager = null;
		final AnalyticsEvent event = new AnalyticsEvent("ti.start", "ti.start", null);
		context.checking(new Expectations()
		{
			{
				never(connection).setRequestProperty("Cookie", "cookie; uid=guid");
				oneOf(connection).setRequestProperty("User-Agent", AnalyticsEvent.getUserAgent());
				oneOf(connection).setDoOutput(true);
				oneOf(connection).setReadTimeout(DefaultAnalyticsEventHandler.DEFAULT_TIMEOUT);
				oneOf(connection).setConnectTimeout(DefaultAnalyticsEventHandler.DEFAULT_TIMEOUT);
				oneOf(connection).setRequestMethod("POST");

				// TODO Add some verification of the payload sent!

				oneOf(connection).getResponseCode();
				will(returnValue(200));

				oneOf(outputStream).close();

				oneOf(connection).disconnect();

				never(logger).logEvent(event);
			}
		});
		handler.sendEvent(event);
		context.assertIsSatisfied();
	}

	// TODO Add test where we have queued up events, they get sent and removed from logger!
}
