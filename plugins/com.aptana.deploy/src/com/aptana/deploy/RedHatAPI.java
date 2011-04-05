package com.aptana.deploy;

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

	private void loadSavedCredentials()
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
			Activator.logError(e);
		}
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
			Activator.logError(e);
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
				pathToNewRepo.toOSString());
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
				return new Status(IStatus.ERROR, Activator.getPluginIdentifier(), "Login cannot be null");
			}
			if (password == null)
			{
				return new Status(IStatus.ERROR, Activator.getPluginIdentifier(), "Password cannot be null");
			}
		}
		newArgs[0] = LOGIN_SWITCH;
		newArgs[1] = login;
		newArgs[2] = PASSWORD_SWITCH;
		newArgs[3] = password;
		IStatus status = ProcessUtil.runInBackground(binScript == null ? args[0] : binScript.toOSString(), null,
				ShellExecutable.getEnvironment(), newArgs);
		return status;
	}

}
