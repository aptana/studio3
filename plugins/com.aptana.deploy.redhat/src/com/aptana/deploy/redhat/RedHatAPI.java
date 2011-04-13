/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.redhat;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import com.aptana.core.ShellExecutable;
import com.aptana.core.util.ExecutableUtil;
import com.aptana.core.util.ProcessUtil;

public class RedHatAPI
{

	/**
	 * App types allowed by Red Hat. TODO How can we generate this list programmatically? I'm assuming new versions of
	 * the service will offer different options!
	 */
	public static final String RACK_1_1_0 = "rack-1.1.0"; //$NON-NLS-1$
	public static final String WSGI_3_2_1 = "wsgi-3.2.1"; //$NON-NLS-1$
	public static final String PHP_5_3_2 = "php-5.3.2"; //$NON-NLS-1$

	/**
	 * The binary scripts we wrap.
	 */
	private static final String USER_INFO_SCRIPT = "rhc-user-info"; //$NON-NLS-1$
	private static final String CREATE_DOMAIN_SCRIPT = "rhc-create-domain"; //$NON-NLS-1$
	private static final String CREATE_APP_SCRIPT = "rhc-create-app"; //$NON-NLS-1$

	/**
	 * Common switches to the scripts
	 */
	private static final String PASSWORD_SWITCH = "-p"; //$NON-NLS-1$
	private static final String LOGIN_SWITCH = "-l"; //$NON-NLS-1$
	/**
	 * Switches for creating an app
	 */
	private static final String REPO_PATH_SWITCH = "-r"; //$NON-NLS-1$
	private static final String APPNAME_SWITCH = "-a"; //$NON-NLS-1$
	private static final String APP_TYPE_SWITCH = "-t"; //$NON-NLS-1$
	/**
	 * Switches for creating a domain
	 */
	private static final String NAMESPACE_SWITCH = "-n"; //$NON-NLS-1$
	/**
	 * Switch to say we want the info for the user, not the app info for the user
	 */
	private static final String USER_INFO_SWITCH = "-i"; //$NON-NLS-1$

	/**
	 * Preference nodes/keys
	 */
	private static final String PREF_NODE_NAME = "red_hat_libra"; //$NON-NLS-1$
	private static final String PASSWORD = "password"; //$NON-NLS-1$
	private static final String RHLOGIN = "rhlogin"; //$NON-NLS-1$

	private String login;
	private String password;

	public RedHatAPI()
	{
		this(null, null);
	}

	public RedHatAPI(String login, String password)
	{
		this.login = login;
		this.password = password;
	}

	public void loadSavedCredentials()
	{
		try
		{
			ISecurePreferences root = SecurePreferencesFactory.getDefault();
			ISecurePreferences node = root.node(PREF_NODE_NAME);
			this.login = node.get(RHLOGIN, null);
			this.password = node.get(PASSWORD, null);
		}
		catch (StorageException e)
		{
			RedHatPlugin.logError(e);
		}
	}

	/**
	 * Returns the user's login
	 * 
	 * @return
	 */
	public String getLogin()
	{
		return login;
	}

	// TODO Return an IStatus?
	public void writeCredentials()
	{
		// Save credentials to secure storage
		try
		{
			ISecurePreferences root = SecurePreferencesFactory.getDefault();
			ISecurePreferences node = root.node(PREF_NODE_NAME);
			node.put(RHLOGIN, login, true);
			node.put(PASSWORD, password, true);
			node.flush();
		}
		catch (Exception e)
		{
			RedHatPlugin.logError(e);
		}
	}

	public IStatus authenticate()
	{
		return run(USER_INFO_SCRIPT, USER_INFO_SWITCH);
	}

	public IStatus createApp(String appName, String type, IPath pathToNewRepo)
	{
		// FIXME If we have an existing project, with an existing repo, we may need to specify -n to not do a git
		// clone/pull
		return run(CREATE_APP_SCRIPT, APPNAME_SWITCH, appName, APP_TYPE_SWITCH, type, REPO_PATH_SWITCH,
				quote(pathToNewRepo.toOSString()));
	}

	public IStatus createDomain(String namespace)
	{
		return run(CREATE_DOMAIN_SCRIPT, NAMESPACE_SWITCH, namespace);
	}

	private IStatus run(String... args)
	{
		// first arg is script
		IPath binScript = ExecutableUtil.find(args[0], false, null);
		// now args are rest of arguments, but we'll insert the common login/password args here
		String[] newArgs = new String[args.length - 1 + 4];
		System.arraycopy(args, 1, newArgs, 4, args.length - 1);
		if (login == null || password == null)
		{
			loadSavedCredentials();
			if (login == null)
			{
				return new Status(IStatus.ERROR, RedHatPlugin.getPluginIdentifier(), Messages.RedHatAPI_LoginEmptyError);
			}
			if (password == null)
			{
				return new Status(IStatus.ERROR, RedHatPlugin.getPluginIdentifier(), Messages.RedHatAPI_PasswordEmptyError);
			}
		}
		newArgs[0] = LOGIN_SWITCH;
		newArgs[1] = login;
		newArgs[2] = PASSWORD_SWITCH;
		newArgs[3] = password;
		IStatus status = ProcessUtil.runInBackground(binScript == null ? args[0] : quote(binScript.toOSString()), null,
				ShellExecutable.getEnvironment(), newArgs);
		return status;
	}

	/**
	 * Wrap a string in double quotes in case it contains spaces
	 * 
	 * @param toQuote
	 * @return
	 */
	private String quote(String toQuote)
	{
		if (toQuote.contains(" ")) //$NON-NLS-1$
		{
			return "\"" + toQuote + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return toQuote;
	}

	/**
	 * Make sure we can find the scripts we need to wrap!
	 * 
	 * @return
	 */
	public IStatus verifyGemInstalled()
	{
		String[] scripts = new String[] { CREATE_DOMAIN_SCRIPT, CREATE_APP_SCRIPT, USER_INFO_SCRIPT };
		for (String scriptName : scripts)
		{
			IPath binScript = ExecutableUtil.find(scriptName, false, null);
			if (binScript == null)
			{
				return new Status(IStatus.ERROR, RedHatPlugin.getPluginIdentifier(), MessageFormat.format(
						Messages.RedHatAPI_UnableToFindBinScriptsError,
						scriptName));
			}
		}
		return Status.OK_STATUS;
	}

}
