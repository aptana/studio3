package com.aptana.js.internal.core.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
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
import com.aptana.core.util.ProcessStatus;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodeJS;

public class NodePackageManagerTest
{

	private static final String NPM_ON_PATH = "/path/to/bin/npm";
	private Mockery context;
	private INodeJS node;
	private IProcessRunner runner;
	private NodePackageManager npm;
	private IProgressMonitor monitor;
	private char[] password = new char[0];
	private IPath userHome;
	private IPath path;
	private File file;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};

		String userHomePath = PlatformUtil.expandEnvironmentStrings("~"); //$NON-NLS-1$
		userHome = Path.fromOSString(userHomePath);

		node = context.mock(INodeJS.class);
		runner = context.mock(IProcessRunner.class);
		path = context.mock(IPath.class);
		file = context.mock(File.class);
		monitor = new NullProgressMonitor();
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

			@Override
			protected IPath findNPMOnPATH(IPath possible)
			{
				return Path.fromPortableString(NPM_ON_PATH);
			}
		};

		context.checking(new Expectations()
		{
			{
				// first time it's called, return the mock path so we can play with it below and toggle whether
				// co-located npm exists
				oneOf(node).getPath();
				will(returnValue(path));

				// all the other times, return an actual IPath for node.
				allowing(node).getPath();
				will(returnValue(Path.fromPortableString("/usr/bin/node")));

				// Building path to potential NPM file...
				oneOf(path).removeLastSegments(1);
				will(returnValue(path));

				oneOf(path).append("npm");
				will(returnValue(path));

				oneOf(path).toFile();
				will(returnValue(file));

				// Now each test will say whether the co-located NPM exists or not by specifying expectation for
				// file.exists()

				// Ok, if we're not returning npm from PATH, use /usr/bin/npm
				allowing(path).toOSString();
				will(returnValue("/usr/bin/npm"));

				// Anytime we ask if NPM exists after we've resolved the path to it, return true
				allowing(file).isFile();
				will(returnValue(true));
			}
		});
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
		path = null;
		file = null;
	}

	@Test
	public void testCleanNpmCacheUnderSudoWithNoPassword()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(file).exists();
				will(returnValue(true));

				// Don't add arg for passing password to stdin, add arg forcing non-interactive so if we do get prompted
				// it exits
				oneOf(runner).run(userHome, ShellExecutable.getEnvironment(), password,
						CollectionsUtil.newList("sudo", "-n", "--", "/usr/bin/node", "/usr/bin/npm", "cache", "clean"),
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
				oneOf(file).exists();
				will(returnValue(true));

				// pass password in on stdin
				oneOf(runner)
						.run(userHome,
								ShellExecutable.getEnvironment(), password, CollectionsUtil.newList("sudo", "-p",
										"password:", "-S", "--", "/usr/bin/node", "/usr/bin/npm", "cache", "clean"),
						monitor);
				will(returnValue(Status.OK_STATUS));
			}
		});

		IStatus returned = npm.cleanNpmCache(password, true, monitor);
		assertTrue(returned.isOK());
		context.assertIsSatisfied();
	}

	@Test
	public void testInstallGlobally()
	{
		password = new char[] { 'p', 'a', 's', 's' };
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

			@Override
			protected IPath findNPMOnPATH(IPath possible)
			{
				return Path.fromPortableString(NPM_ON_PATH);
			}

			@Override
			public synchronized IPath getConfigPrefixPath() throws CoreException
			{
				return null;
			}
		};
		context.checking(new Expectations()
		{
			{
				oneOf(file).exists();
				will(returnValue(true));

				// pass password in on stdin
				oneOf(runner).run(with(aNull(IPath.class)), with(ShellExecutable.getEnvironment()), with(password), // sudo
																													// password
						// Run under sudo, force sudo to retain home (-H), use -g global flag to npm
						with(CollectionsUtil.newList("sudo", "-p", "password:", "-S", "-H", "--", "/usr/bin/node",
								"/usr/bin/npm", "-g", "install", "titanium", "--color", "false")),
						with(any(IProgressMonitor.class)));
				will(returnValue(Status.OK_STATUS));
			}
		});

		IStatus returned = npm.install("titanium", "Titanium CLI", true, password, monitor);
		assertTrue(returned.isOK());
		context.assertIsSatisfied();
	}

	@Test
	public void testInstallLocally()
	{
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

			@Override
			protected IPath findNPMOnPATH(IPath possible)
			{
				return Path.fromPortableString(NPM_ON_PATH);
			}

			@Override
			public synchronized IPath getConfigPrefixPath() throws CoreException
			{
				return null;
			}
		};
		context.checking(new Expectations()
		{
			{
				oneOf(file).exists();
				will(returnValue(true));

				// pass password in on stdin
				oneOf(runner).run(with(aNull(IPath.class)), with(ShellExecutable.getEnvironment()),
						with(aNull(char[].class)), // no password
						// don't run under sudo, no -g global flag
						with(CollectionsUtil.newList("/usr/bin/node", "/usr/bin/npm", "install", "titanium", "--color",
								"false")),
						with(any(IProgressMonitor.class)));
				will(returnValue(Status.OK_STATUS));
			}
		});

		// FIXME Should we require a working directory for a local install? I think we should!
		IStatus returned = npm.install("titanium", "Titanium CLI", false, null, monitor);
		assertTrue(returned.isOK());
		context.assertIsSatisfied();
	}

	@Test
	public void testCleanNpmCacheWithoutSudo()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(file).exists();
				will(returnValue(true));

				oneOf(runner).run(userHome, ShellExecutable.getEnvironment(), null,
						CollectionsUtil.newList("/usr/bin/node", "/usr/bin/npm", "cache", "clean"), monitor);
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
		final IStatus status = new Status(IStatus.OK, JSCorePlugin.PLUGIN_ID,
				"{\n  \"dependencies\": {\n    \"titanium\": {\n      \"version\": \"3.3.0\",\n      \"from\": \"titanium@*\",\n      \"resolved\": \"https://registry.npmjs.org/titanium/-/titanium-3.3.0.tgz\"\n    }\n  }\n}");
		context.checking(new Expectations()
		{
			{
				oneOf(file).exists();
				will(returnValue(true));

				oneOf(node).runInBackground(userHome, ShellExecutable.getEnvironment(), CollectionsUtil
						.newList("/usr/bin/npm", "ls", "titanium", "-s", "--color", "false", "--json", "true", "-g"));
				will(returnValue(status));
			}
		});

		String returned = npm.getInstalledVersion("titanium", true, userHome);
		assertEquals("3.3.0", returned);
		context.assertIsSatisfied();
	}

	@Test
	public void testGetInstalledPrePatchVersion() throws CoreException, IOException
	{
		URL url = FileLocator.find(JSCorePlugin.getDefault().getBundle(), new Path("resources"), null);
		userHome = Path.fromOSString(FileLocator.resolve(url).getPath());

		// For prepatch npm versions such as 4.2.1-5, npm -g ls <appcelerator> doesn't return the version number.
		context.checking(new Expectations()
		{
			{
				oneOf(file).exists();
				will(returnValue(true));

				oneOf(node).runInBackground(userHome, ShellExecutable.getEnvironment(), CollectionsUtil.newList(
						"/usr/bin/npm", "ls", "appcelerator", "-s", "--color", "false", "--json", "true", "-g"));
				will(returnValue(new Status(IStatus.OK, JSCorePlugin.PLUGIN_ID, "{}")));

			}
		});

		String returned = npm.getInstalledVersion("appcelerator", true, userHome);
		assertEquals("4.2.1-5", returned);
		context.assertIsSatisfied();
	}

	@Test
	public void testGetInstalledVersionWithUnmetDependencies() throws CoreException
	{
		final IStatus status = new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID,
				"{\n \"problems\": [ \n \"invalid: titanium@3.4.1 /usr/local/lib/node_modules/titanium\" \n ], \n \"dependencies\": {\n    \"titanium\": {\n      \"version\": \"3.4.1\",\n      \"from\": \"titanium@*\",\n      \"invalid\": true,\n  \"problems\": [ \n \"invalid: titanium@3.4.1 /usr/local/lib/node_modules/titanium\" \n ] \n  }\n  }\n}");
		context.checking(new Expectations()
		{
			{
				oneOf(file).exists();
				will(returnValue(true));

				oneOf(node).runInBackground(userHome, ShellExecutable.getEnvironment(), CollectionsUtil
						.newList("/usr/bin/npm", "ls", "titanium", "-s", "--color", "false", "--json", "true", "-g"));
				will(returnValue(status));
			}
		});

		String returned = npm.getInstalledVersion("titanium", true, userHome);
		assertEquals("3.4.1", returned);
		context.assertIsSatisfied();
	}

	@Test
	public void testGetInstalledVersionIsNotInstalled() throws CoreException
	{
		final IStatus status = new Status(IStatus.OK, JSCorePlugin.PLUGIN_ID, "{}");
		context.checking(new Expectations()
		{
			{
				oneOf(file).exists();
				will(returnValue(true));

				oneOf(node).runInBackground(userHome, ShellExecutable.getEnvironment(), CollectionsUtil
						.newList("/usr/bin/npm", "ls", "titanium", "-s", "--color", "false", "--json", "true", "-g"));
				will(returnValue(status));
			}
		});

		String returned = npm.getInstalledVersion("titanium", true, userHome);
		assertNull(returned);
		context.assertIsSatisfied();
	}

	@Test
	public void testGetInstalledVersionThrowsCoreException() throws CoreException
	{
		final IStatus status = new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, "blah");
		context.checking(new Expectations()
		{
			{
				oneOf(file).exists();
				will(returnValue(true));

				oneOf(node).runInBackground(userHome, ShellExecutable.getEnvironment(), CollectionsUtil
						.newList("/usr/bin/npm", "ls", "titanium", "-s", "--color", "false", "--json", "true", "-g"));
				will(returnValue(status));
			}
		});

		String version = npm.getInstalledVersion("titanium", true, userHome);
		assertNull(version);
		context.assertIsSatisfied();
	}

	@Test
	public void testIsInstalledCheckWhenNotInstalled() throws CoreException
	{
		final IStatus status = new Status(IStatus.OK, JSCorePlugin.PLUGIN_ID, "{}");
		context.checking(new Expectations()
		{
			{
				oneOf(file).exists();
				will(returnValue(true));

				oneOf(node).runInBackground(null, ShellExecutable.getEnvironment(), CollectionsUtil
						.newList("/usr/bin/npm", "ls", "titanium", "-s", "--color", "false", "--json", "true", "-g"));
				will(returnValue(status));

				oneOf(node).runInBackground(null, null,
						CollectionsUtil.newList("/usr/bin/npm", "config", "get", "prefix"));
				will(returnValue(new Status(IStatus.OK, JSCorePlugin.PLUGIN_ID, "/usr/local/lib")));
			}
		});

		assertFalse(npm.isInstalled("titanium"));
		context.assertIsSatisfied();
	}

	@Test
	public void testIsInstalledCheckWhenIsInstalled() throws CoreException
	{
		final IStatus status = new Status(IStatus.OK, JSCorePlugin.PLUGIN_ID,
				"{\n  \"dependencies\": {\n    \"titanium\": {\n      \"version\": \"3.3.0\",\n      \"from\": \"titanium@*\",\n      \"resolved\": \"https://registry.npmjs.org/titanium/-/titanium-3.3.0.tgz\"\n    }\n  }\n}");
		context.checking(new Expectations()
		{
			{
				oneOf(file).exists();
				will(returnValue(true));

				oneOf(node).runInBackground(null, ShellExecutable.getEnvironment(), CollectionsUtil
						.newList("/usr/bin/npm", "ls", "titanium", "-s", "--color", "false", "--json", "true", "-g"));
				will(returnValue(status));
			}
		});

		assertTrue(npm.isInstalled("titanium"));
		context.assertIsSatisfied();
	}

	@Test
	public void testListProcessReturnsExitCodeBecauseDependenciesAreBusted() throws CoreException
	{
		final ProcessStatus listStatus = new ProcessStatus(1, "/usr/lib\n" + "/usr/lib/node_modules/alloy\n"
				+ "/mnt/jenkins/jenkins-data/jobs/titanium-nightly-development/workspace/eclipse/node_modules/colors\n"
				+ "/mnt/jenkins/jenkins-data/jobs/titanium-nightly-development/workspace/eclipse/node_modules/commander\n"
				+ "/mnt/jenkins/jenkins-data/jobs/titanium-nightly-development/workspace/eclipse/node_modules/jsonlint\n"
				+ "/mnt/jenkins/jenkins-data/jobs/titanium-nightly-development/workspace/eclipse/node_modules/pkginfo\n"
				+ "/mnt/jenkins/jenkins-data/jobs/titanium-nightly-development/workspace/eclipse/node_modules/source-map\n"
				+ "/mnt/jenkins/jenkins-data/jobs/titanium-nightly-development/workspace/eclipse/node_modules/uglify-js\n"
				+ "/mnt/jenkins/jenkins-data/jobs/titanium-nightly-development/workspace/eclipse/node_modules/wrench\n"
				+ "/mnt/jenkins/jenkins-data/jobs/titanium-nightly-development/workspace/eclipse/node_modules/xml2tss\n"
				+ "/mnt/jenkins/jenkins-data/jobs/titanium-nightly-development/workspace/eclipse/node_modules/xmldom\n"
				+ "/usr/lib/node_modules/npm\n" + "/usr/lib/node_modules/npm/node_modules/abbrev\n"
				+ "/usr/lib/node_modules/npm/node_modules/ansi\n"
				+ "/usr/lib/node_modules/npm/node_modules/ansicolors\n"
				+ "/usr/lib/node_modules/npm/node_modules/ansistyles\n"
				+ "/usr/lib/node_modules/npm/node_modules/archy\n"
				+ "/usr/lib/node_modules/npm/node_modules/block-stream\n"
				+ "/usr/lib/node_modules/npm/node_modules/child-process-close\n"
				+ "/usr/lib/node_modules/npm/node_modules/chmodr\n" + "/usr/lib/node_modules/npm/node_modules/chownr\n"
				+ "/usr/lib/node_modules/npm/node_modules/cmd-shim\n"
				+ "/usr/lib/node_modules/npm/node_modules/columnify\n"
				+ "/usr/lib/node_modules/npm/node_modules/editor\n" + "/usr/lib/node_modules/npm/node_modules/fstream\n"
				+ "/usr/lib/node_modules/npm/node_modules/fstream-npm\n"
				+ "/usr/lib/node_modules/npm/node_modules/fstream-npm/node_modules/fstream-ignore\n"
				+ "/usr/lib/node_modules/npm/node_modules/github-url-from-git\n"
				+ "/usr/lib/node_modules/npm/node_modules/github-url-from-username-repo\n"
				+ "/usr/lib/node_modules/npm/node_modules/glob\n"
				+ "/usr/lib/node_modules/npm/node_modules/graceful-fs\n"
				+ "/usr/lib/node_modules/npm/node_modules/inherits\n" + "/usr/lib/node_modules/npm/node_modules/ini\n"
				+ "/usr/lib/node_modules/npm/node_modules/init-package-json\n"
				+ "/usr/lib/node_modules/npm/node_modules/init-package-json/node_modules/promzard\n"
				+ "/usr/lib/node_modules/npm/node_modules/lockfile\n"
				+ "/usr/lib/node_modules/npm/node_modules/lru-cache\n"
				+ "/usr/lib/node_modules/npm/node_modules/minimatch\n"
				+ "/usr/lib/node_modules/npm/node_modules/minimatch/node_modules/sigmund\n"
				+ "/usr/lib/node_modules/npm/node_modules/mkdirp\n"
				+ "/usr/lib/node_modules/npm/node_modules/node-gyp\n" + "/usr/lib/node_modules/npm/node_modules/nopt\n"
				+ "/usr/lib/node_modules/npm/node_modules/npm-registry-client\n"
				+ "/usr/lib/node_modules/npm/node_modules/npm-registry-client/node_modules/couch-login\n"
				+ "/usr/lib/node_modules/npm/node_modules/npm-user-validate\n"
				+ "/usr/lib/node_modules/npm/node_modules/npmconf\n"
				+ "/usr/lib/node_modules/npm/node_modules/npmconf/node_modules/config-chain\n"
				+ "/usr/lib/node_modules/npm/node_modules/npmconf/node_modules/config-chain/node_modules/proto-list\n"
				+ "/usr/lib/node_modules/npm/node_modules/npmlog\n" + "/usr/lib/node_modules/npm/node_modules/once\n"
				+ "/usr/lib/node_modules/npm/node_modules/opener\n" + "/usr/lib/node_modules/npm/node_modules/osenv\n"
				+ "/usr/lib/node_modules/npm/node_modules/path-is-inside\n"
				+ "/usr/lib/node_modules/npm/node_modules/read\n"
				+ "/usr/lib/node_modules/npm/node_modules/read/node_modules/mute-stream\n"
				+ "/usr/lib/node_modules/npm/node_modules/read-installed\n"
				+ "/usr/lib/node_modules/npm/node_modules/read-package-json\n"
				+ "/usr/lib/node_modules/npm/node_modules/read-package-json/node_modules/normalize-package-data\n"
				+ "/usr/lib/node_modules/npm/node_modules/request\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/aws-sign2\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/forever-agent\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/form-data\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/form-data/node_modules/async\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/form-data/node_modules/combined-stream\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/form-data/node_modules/combined-stream/node_modules/delayed-stream\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/hawk\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/hawk/node_modules/boom\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/hawk/node_modules/cryptiles\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/hawk/node_modules/hoek\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/hawk/node_modules/sntp\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/http-signature\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/http-signature/node_modules/asn1\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/http-signature/node_modules/assert-plus\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/http-signature/node_modules/ctype\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/json-stringify-safe\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/mime\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/node-uuid\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/oauth-sign\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/qs\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/tough-cookie\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/tough-cookie/node_modules/punycode\n"
				+ "/usr/lib/node_modules/npm/node_modules/request/node_modules/tunnel-agent\n"
				+ "/usr/lib/node_modules/npm/node_modules/retry\n" + "/usr/lib/node_modules/npm/node_modules/rimraf\n"
				+ "/usr/lib/node_modules/npm/node_modules/semver\n" + "/usr/lib/node_modules/npm/node_modules/sha\n"
				+ "/usr/lib/node_modules/npm/node_modules/sha/node_modules/readable-stream\n"
				+ "/usr/lib/node_modules/npm/node_modules/slide\n" + "/usr/lib/node_modules/npm/node_modules/tar\n"
				+ "/usr/lib/node_modules/npm/node_modules/text-table\n"
				+ "/usr/lib/node_modules/npm/node_modules/uid-number\n"
				+ "/usr/lib/node_modules/npm/node_modules/which\n" + "/usr/lib/node_modules/titanium\n"
				+ "/usr/lib/node_modules/titanium/node_modules/async\n"
				+ "/usr/lib/node_modules/titanium/node_modules/colors\n"
				+ "/usr/lib/node_modules/titanium/node_modules/fields\n"
				+ "/usr/lib/node_modules/titanium/node_modules/fields/node_modules/keypress\n"
				+ "/usr/lib/node_modules/titanium/node_modules/humanize\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/character-parser\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/commander\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/constantinople\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/constantinople/node_modules/uglify-js\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/constantinople/node_modules/uglify-js/node_modules/optimist\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/constantinople/node_modules/uglify-js/node_modules/optimist/node_modules/wordwrap\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/constantinople/node_modules/uglify-js/node_modules/source-map\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/constantinople/node_modules/uglify-js/node_modules/source-map/node_modules/amdefine\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/constantinople/node_modules/uglify-js/node_modules/uglify-to-browserify\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/mkdirp\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/monocle\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/monocle/node_modules/readdirp\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/monocle/node_modules/readdirp/node_modules/minimatch\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/monocle/node_modules/readdirp/node_modules/minimatch/node_modules/lru-cache\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/monocle/node_modules/readdirp/node_modules/minimatch/node_modules/sigmund\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/transformers\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/transformers/node_modules/css\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/transformers/node_modules/css/node_modules/css-parse\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/transformers/node_modules/css/node_modules/css-stringify\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/transformers/node_modules/promise\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/transformers/node_modules/promise/node_modules/is-promise\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/transformers/node_modules/uglify-js\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/transformers/node_modules/uglify-js/node_modules/optimist\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/transformers/node_modules/uglify-js/node_modules/optimist/node_modules/wordwrap\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/transformers/node_modules/uglify-js/node_modules/source-map\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/transformers/node_modules/uglify-js/node_modules/source-map/node_modules/amdefine\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/with\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/with/node_modules/uglify-js\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/with/node_modules/uglify-js/node_modules/optimist\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/with/node_modules/uglify-js/node_modules/optimist/node_modules/wordwrap\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/with/node_modules/uglify-js/node_modules/source-map\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/with/node_modules/uglify-js/node_modules/source-map/node_modules/amdefine\n"
				+ "/usr/lib/node_modules/titanium/node_modules/jade/node_modules/with/node_modules/uglify-js/node_modules/uglify-to-browserify\n"
				+ "/usr/lib/node_modules/titanium/node_modules/longjohn\n"
				+ "/usr/lib/node_modules/titanium/node_modules/moment\n"
				+ "/usr/lib/node_modules/titanium/node_modules/node-appc\n"
				+ "/usr/lib/node_modules/titanium/node_modules/node-appc/node_modules/adm-zip\n"
				+ "/usr/lib/node_modules/titanium/node_modules/node-appc/node_modules/diff\n"
				+ "/usr/lib/node_modules/titanium/node_modules/node-appc/node_modules/dox\n"
				+ "/usr/lib/node_modules/titanium/node_modules/node-appc/node_modules/dox/node_modules/commander\n"
				+ "/usr/lib/node_modules/titanium/node_modules/node-appc/node_modules/dox/node_modules/github-flavored-markdown\n"
				+ "/usr/lib/node_modules/titanium/node_modules/node-appc/node_modules/node-uuid\n"
				+ "/usr/lib/node_modules/titanium/node_modules/node-appc/node_modules/semver\n"
				+ "/usr/lib/node_modules/titanium/node_modules/node-appc/node_modules/uglify-js\n"
				+ "/usr/lib/node_modules/titanium/node_modules/node-appc/node_modules/uglify-js/node_modules/optimist\n"
				+ "/usr/lib/node_modules/titanium/node_modules/node-appc/node_modules/uglify-js/node_modules/optimist/node_modules/wordwrap\n"
				+ "/usr/lib/node_modules/titanium/node_modules/node-appc/node_modules/uglify-js/node_modules/source-map\n"
				+ "/usr/lib/node_modules/titanium/node_modules/node-appc/node_modules/uglify-js/node_modules/source-map/node_modules/amdefine\n"
				+ "/usr/lib/node_modules/titanium/node_modules/node-appc/node_modules/xmldom\n"
				+ "/usr/lib/node_modules/titanium/node_modules/optimist\n"
				+ "/usr/lib/node_modules/titanium/node_modules/optimist/node_modules/minimist\n"
				+ "/usr/lib/node_modules/titanium/node_modules/optimist/node_modules/wordwrap\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/aws-sign\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/cookie-jar\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/forever-agent\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/form-data\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/form-data/node_modules/combined-stream\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/form-data/node_modules/combined-stream/node_modules/delayed-stream\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/hawk\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/hawk/node_modules/boom\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/hawk/node_modules/cryptiles\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/hawk/node_modules/hoek\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/hawk/node_modules/sntp\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/http-signature\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/http-signature/node_modules/asn1\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/http-signature/node_modules/assert-plus\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/http-signature/node_modules/ctype\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/json-stringify-safe\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/mime\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/node-uuid\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/oauth-sign\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/qs\n"
				+ "/usr/lib/node_modules/titanium/node_modules/request/node_modules/tunnel-agent\n"
				+ "/usr/lib/node_modules/titanium/node_modules/semver\n"
				+ "/usr/lib/node_modules/titanium/node_modules/sprintf\n"
				+ "/usr/lib/node_modules/titanium/node_modules/temp\n"
				+ "/usr/lib/node_modules/titanium/node_modules/temp/node_modules/osenv\n"
				+ "/usr/lib/node_modules/titanium/node_modules/temp/node_modules/rimraf\n"
				+ "/usr/lib/node_modules/titanium/node_modules/temp/node_modules/rimraf/node_modules/graceful-fs\n"
				+ "/usr/lib/node_modules/titanium/node_modules/winston\n"
				+ "/usr/lib/node_modules/titanium/node_modules/winston/node_modules/async\n"
				+ "/usr/lib/node_modules/titanium/node_modules/winston/node_modules/cycle\n"
				+ "/usr/lib/node_modules/titanium/node_modules/winston/node_modules/eyes\n"
				+ "/usr/lib/node_modules/titanium/node_modules/winston/node_modules/pkginfo\n"
				+ "/usr/lib/node_modules/titanium/node_modules/winston/node_modules/request\n"
				+ "/usr/lib/node_modules/titanium/node_modules/winston/node_modules/stack-trace\n"
				+ "/usr/lib/node_modules/titanium/node_modules/wrench",
				"npm ERR! missing: colors@0.6.0-1, required by alloy@1.3.1\n"
						+ "npm ERR! missing: pkginfo@0.2.2, required by alloy@1.3.1\n"
						+ "npm ERR! missing: commander@0.6.1, required by alloy@1.3.1\n"
						+ "npm ERR! missing: wrench@1.3.9, required by alloy@1.3.1\n"
						+ "npm ERR! missing: xmldom@0.1.13, required by alloy@1.3.1\n"
						+ "npm ERR! missing: jsonlint@1.5.1, required by alloy@1.3.1\n"
						+ "npm ERR! missing: uglify-js@2.2.5, required by alloy@1.3.1\n"
						+ "npm ERR! missing: source-map@0.1.9, required by alloy@1.3.1\n"
						+ "npm ERR! missing: xml2tss@0.0.5, required by alloy@1.3.1\n" + "npm ERR! not ok code 0");
		context.checking(new Expectations()
		{
			{
				oneOf(file).exists();
				will(returnValue(true));

				// ask for list, it will return exit code of 1, but usable output
				oneOf(node).runInBackground(null, null, CollectionsUtil.newList("/usr/bin/npm", "-g", "-p", "list"));
				will(returnValue(listStatus));
			}
		});
		// Verify that despite an error exit code, if we have usable output we'll use it!
		Set<String> installed = npm.list(true);
		assertTrue(installed.contains("titanium"));
		assertTrue(installed.contains("npm"));
		assertFalse(installed.contains("madeupmodule"));
		context.assertIsSatisfied();
	}

	@Test
	public void testNPMCoLocatedWithNode() throws CoreException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(file).exists();
				will(returnValue(true));
			}
		});

		assertEquals("/usr/bin/npm", npm.getPath().toOSString());
		context.assertIsSatisfied();
	}

	@Test
	public void testAllowsForNPMNotCoInstalledAtSamePathAsNode() throws CoreException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(file).exists();
				will(returnValue(false));
			}
		});

		assertEquals(Path.fromPortableString(NPM_ON_PATH), npm.getPath());
		context.assertIsSatisfied();
	}
}
