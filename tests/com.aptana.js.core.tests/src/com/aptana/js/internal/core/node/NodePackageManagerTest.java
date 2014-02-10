package com.aptana.js.internal.core.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.ShellExecutable;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.IProcessRunner;
import com.aptana.core.util.PlatformUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodeJS;

public class NodePackageManagerTest
{

	private Mockery context;
	private INodeJS node;
	private IProcessRunner runner;
	private NodePackageManager npm;
	private IProgressMonitor monitor;
	private char[] password = new char[0];
	private IPath userHome;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};

		String path = PlatformUtil.expandEnvironmentStrings("~"); //$NON-NLS-1$
		userHome = Path.fromOSString(path);

		node = context.mock(INodeJS.class);
		runner = context.mock(IProcessRunner.class);
		monitor = new NullProgressMonitor();
		context.checking(new Expectations()
		{
			{
				atLeast(1).of(node).getPath();
				will(returnValue(Path.fromPortableString("/usr/bin/node")));
			}
		});
		npm = new NodePackageManager(node)
		{
			@Override
			protected IProcessRunner getProcessRunner()
			{
				return runner;
			}

			@Override
			public boolean exists()
			{
				return true;
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		userHome = null;
		monitor = null;
		npm = null;
		node = null;
		runner = null;
		context = null;
	}

	@Test
	public void testCleanNpmCacheUnderSudoWithNoPassword()
	{
		context.checking(new Expectations()
		{
			{
				// Don't add arg for passing password to stdin, add arg forcing non-interactive so if we do get prompted
				// it exits
				oneOf(runner).run(userHome, ShellExecutable.getEnvironment(), password, CollectionsUtil.newList("sudo", "-n", "--", "/usr/bin/node", "/usr/bin/npm", "cache", "clean"),
						monitor);
				will(returnValue(Status.OK_STATUS));
			}
		});

		IStatus returned = npm.cleanNpmCache(password, true, monitor);
		assertTrue(returned.isOK());
		context.assertIsSatisfied();
	}

	@Test
	public void testCleanNpmCacheUnderSudoWithPassword()
	{
		password = new char[] { 'p', 'a', 's', 's' };

		context.checking(new Expectations()
		{
			{
				// pass password in on stdin
				oneOf(runner).run(userHome, ShellExecutable.getEnvironment(), password, CollectionsUtil.newList("sudo", "-S", "--", "/usr/bin/node", "/usr/bin/npm", "cache", "clean"),
						monitor);
				will(returnValue(Status.OK_STATUS));
			}
		});

		IStatus returned = npm.cleanNpmCache(password, true, monitor);
		assertTrue(returned.isOK());
		context.assertIsSatisfied();
	}

	@Test
	public void testCleanNpmCacheWithoutSudo()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(runner).run(userHome, ShellExecutable.getEnvironment(), null, CollectionsUtil.newList("/usr/bin/node", "/usr/bin/npm", "cache", "clean"),
						monitor);
				will(returnValue(Status.OK_STATUS));
			}
		});

		IStatus returned = npm.cleanNpmCache(null, false, monitor);
		assertTrue(returned.isOK());
		context.assertIsSatisfied();
	}

	@Test
	public void testGetInstalledVersionIsInstalled() throws CoreException
	{
		final IStatus status = new Status(
				IStatus.OK,
				JSCorePlugin.PLUGIN_ID,
				"/usr/local/lib\n└── titanium@3.2.1  (git://github.com/appcelerator/titanium.git#8e262ccbd20b4a049ab2bdffd98ff940592da224)");
		context.checking(new Expectations()
		{
			{
				oneOf(node).runInBackground(userHome, ShellExecutable.getEnvironment(),
						CollectionsUtil.newList("/usr/bin/npm", "ls", "titanium", "--color", "false", "-g"));
				will(returnValue(status));
			}
		});

		String returned = npm.getInstalledVersion("titanium", true, userHome);
		assertEquals("3.2.1", returned);
		context.assertIsSatisfied();
	}

	@Test
	public void testGetInstalledVersionIsNotInstalled() throws CoreException
	{
		final IStatus status = new Status(IStatus.OK, JSCorePlugin.PLUGIN_ID, "/usr/local/lib\n└── (empty)");
		context.checking(new Expectations()
		{
			{
				oneOf(node).runInBackground(userHome, ShellExecutable.getEnvironment(),
						CollectionsUtil.newList("/usr/bin/npm", "ls", "titanium", "--color", "false", "-g"));
				will(returnValue(status));
			}
		});

		String returned = npm.getInstalledVersion("titanium", true, userHome);
		assertNull(returned);
		context.assertIsSatisfied();
	}

	@Test(expected = CoreException.class)
	public void testGetInstalledVersionThrowsCoreException() throws CoreException
	{
		final IStatus status = new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, "blah");
		context.checking(new Expectations()
		{
			{
				oneOf(node).runInBackground(userHome, ShellExecutable.getEnvironment(),
						CollectionsUtil.newList("/usr/bin/npm", "ls", "titanium", "--color", "false", "-g"));
				will(returnValue(status));
			}
		});

		npm.getInstalledVersion("titanium", true, userHome);
		context.assertIsSatisfied();
	}

	@Test
	public void testIsInstalledCheckWhenNotInstalled() throws CoreException
	{
		final IStatus status = new Status(IStatus.OK, JSCorePlugin.PLUGIN_ID, "/usr/local/lib\n└── (empty)");
		context.checking(new Expectations()
		{
			{
				oneOf(node).runInBackground(null, ShellExecutable.getEnvironment(),
						CollectionsUtil.newList("/usr/bin/npm", "ls", "titanium", "--color", "false", "-g"));
				will(returnValue(status));
			}
		});

		assertFalse(npm.isInstalled("titanium"));
		context.assertIsSatisfied();
	}

	@Test
	public void testIsInstalledCheckWhenIsInstalled() throws CoreException
	{
		final IStatus status = new Status(
				IStatus.OK,
				JSCorePlugin.PLUGIN_ID,
				"/usr/local/lib\n└── titanium@3.2.1  (git://github.com/appcelerator/titanium.git#8e262ccbd20b4a049ab2bdffd98ff940592da224)");
		context.checking(new Expectations()
		{
			{
				oneOf(node).runInBackground(null, ShellExecutable.getEnvironment(),
						CollectionsUtil.newList("/usr/bin/npm", "ls", "titanium", "--color", "false", "-g"));
				will(returnValue(status));
			}
		});

		assertTrue(npm.isInstalled("titanium"));
		context.assertIsSatisfied();
	}

	@Test
	public void testIsInstalledCheckWhenLSReturnsError() throws CoreException
	{
		final IStatus status = new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, "blah");
		final IStatus listStatus = new Status(IStatus.OK, JSCorePlugin.PLUGIN_ID,
				"/usr/local/lib/node_modules/titanium\n" + "/usr/local/lib/node_modules/titanium/node_modules/async\n"
						+ "/usr/local/lib/node_modules/titanium/node_modules/colors\n"
						+ "/usr/local/lib/node_modules/titanium/node_modules/wrench");
		context.checking(new Expectations()
		{
			{
				// Pretend that ls fails for some reason
				oneOf(node).runInBackground(null, ShellExecutable.getEnvironment(),
						CollectionsUtil.newList("/usr/bin/npm", "ls", "titanium", "--color", "false", "-g"));
				will(returnValue(status));

				// Falls back to checking list
				oneOf(node).runInBackground(null, null, CollectionsUtil.newList("/usr/bin/npm", "-g", "-p", "list"));
				will(returnValue(listStatus));
			}
		});

		assertTrue(npm.isInstalled("titanium"));
		context.assertIsSatisfied();
	}
}
