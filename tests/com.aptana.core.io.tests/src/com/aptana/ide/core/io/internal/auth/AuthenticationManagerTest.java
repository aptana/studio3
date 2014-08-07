package com.aptana.ide.core.io.internal.auth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aptana.ide.core.io.auth.IAuthenticationPrompt;

public class AuthenticationManagerTest
{

	private AuthenticationManager am;
	private Mockery context;
	private ISecurePreferences secure;
	private IAuthenticationPrompt prompt;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		secure = context.mock(ISecurePreferences.class);
		prompt = context.mock(IAuthenticationPrompt.class);
		am = new AuthenticationManager()
		{
			@Override
			protected ISecurePreferences getSecurePreferences()
			{
				return secure;
			}

			@Override
			protected IAuthenticationPrompt getAuthPrompt()
			{
				return prompt;
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		secure = null;
		prompt = null;
		context = null;
		am = null;
	}

	@Test
	public void testHasPersistent()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(secure).nodeExists("authId");
				will(returnValue(true));

				oneOf(secure).node("authId");
				will(returnValue(secure));

				oneOf(secure).keys();
				will(returnValue(new String[] { "password" }));
			}

		});
		assertTrue(am.hasPersistent("authId"));
		context.assertIsSatisfied();
	}

	@Test
	public void testHasPersistentWithNoPasswordKeyUnderNode()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(secure).nodeExists("authId");
				will(returnValue(true));

				oneOf(secure).node("authId");
				will(returnValue(secure));

				oneOf(secure).keys();
				will(returnValue(new String[] { "something" }));
			}

		});
		assertFalse(am.hasPersistent("authId"));
		context.assertIsSatisfied();
	}

	@Test
	public void testHasPersistentWithNoNodeInPrefs()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(secure).nodeExists("authId");
				will(returnValue(false));
			}

		});
		assertFalse(am.hasPersistent("authId"));
		context.assertIsSatisfied();
	}

	@Test
	public void testGetPassword() throws StorageException
	{
		final String password = "shh!its.a.secret!";
		context.checking(new Expectations()
		{
			{
				oneOf(secure).nodeExists("authId");
				will(returnValue(true));

				oneOf(secure).node("authId");
				will(returnValue(secure)); // throw exception?

				oneOf(secure).get("password", null);
				will(returnValue(password)); // TODO Return null
			}

		});
		Assert.assertArrayEquals(password.toCharArray(), am.getPassword("authId"));
	}

	@Test
	public void testGetPasswordWithNoPasswordInPrefs() throws StorageException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(secure).nodeExists("authId");
				will(returnValue(true));

				oneOf(secure).node("authId");
				will(returnValue(secure)); // throw exception?

				oneOf(secure).get("password", null);
				will(returnValue(null));
			}

		});
		assertNull(am.getPassword("authId"));
	}

	@Test
	public void testGetPasswordWherePrefsThrowStorageException() throws StorageException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(secure).nodeExists("authId");
				will(returnValue(true));

				oneOf(secure).node("authId");
				will(returnValue(secure)); // throw exception?

				oneOf(secure).get("password", null);
				will(throwException(new StorageException(1, "something went wrong")));
			}

		});
		assertNull(am.getPassword("authId"));
	}

	@Test
	public void testGetPasswordWithNoNodeInPrefs()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(secure).nodeExists("authId");
				will(returnValue(false));
			}

		});
		assertNull(am.getPassword("authId"));
		context.assertIsSatisfied();
	}

	@Test
	public void testPromptPassword()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(prompt).promptPassword(am, "authId", "login", "title", "message");
				will(returnValue(true));
				// FIXME Need side effect of changing sessionPasswords map!
			}

		});
		char[] result = am.promptPassword("authId", "login", "title", "message");
		context.assertIsSatisfied();
	}

	@Test
	public void testPromptPasswordWitNoPrompter()
	{
		prompt = null;
		assertNull(am.promptPassword("authId", "login", "title", "message"));
		context.assertIsSatisfied();
	}

	@Test
	public void testPromptPasswordWherePrompterReturnsFalse()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(prompt).promptPassword(am, "authId", "login", "title", "message");
				will(returnValue(false));
			}

		});
		assertNull(am.promptPassword("authId", "login", "title", "message"));
		context.assertIsSatisfied();
	}

	@Test
	public void testResetPassword()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(secure).nodeExists("authId");
				will(returnValue(true)); // TODO Return false

				oneOf(secure).node("authId");
				will(returnValue(secure));

				oneOf(secure).removeNode();
				// TODO Verify we removed from sessionpasswords too!
			}

		});
		am.resetPassword("authId");
		context.assertIsSatisfied();
	}

	@Test
	public void testSetPassword() throws StorageException, IOException
	{
		final String password = "abracadabra";
		context.checking(new Expectations()
		{
			{
				oneOf(secure).node("authId");
				will(returnValue(secure));

				// TODO Verify we remove from sessionpasswords?
				// TODO Verify we stick password into sessionPasswords!

				oneOf(secure).put("password", password, true);

				oneOf(secure).flush();
			}

		});
		am.setPassword("authId", password.toCharArray(), true);
		context.assertIsSatisfied();
	}

	@Test
	public void testSetPasswordNotPersistent() throws StorageException, IOException
	{
		final String password = "abracadabra";
		context.checking(new Expectations()
		{
			{
				oneOf(secure).node("authId");
				will(returnValue(secure));

				oneOf(secure).removeNode(); // For whatever reason, saying not persistent removes from the prefs

				oneOf(secure).flush();
			}

		});
		am.setPassword("authId", password.toCharArray(), false);
		context.assertIsSatisfied();
	}

	@Test
	public void testSetPasswordWithNullPassword() throws StorageException, IOException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(secure).node("authId");
				will(returnValue(secure));

				never(secure).removeNode();

				oneOf(secure).flush();
			}

		});
		am.setPassword("authId", null, true);
		context.assertIsSatisfied();
	}
}
