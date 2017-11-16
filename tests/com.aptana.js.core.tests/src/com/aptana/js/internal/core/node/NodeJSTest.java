package com.aptana.js.internal.core.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.service.datalocation.Location;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.IProcessRunner;
import com.aptana.core.util.ProcessStatus;
import com.aptana.ide.core.io.downloader.DownloadManager;

public class NodeJSTest
{

	private NodeJS node;
	private Mockery context;
	private IProcessRunner runner;
	private Location location;
	private File file;
	private IPath path;
	private DownloadManager dm;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		runner = context.mock(IProcessRunner.class);
		location = context.mock(Location.class);
		path = context.mock(IPath.class);
		file = context.mock(File.class);
		dm = context.mock(DownloadManager.class);
		node = new NodeJS(Path.fromPortableString("/path/to/node"))
		{
			@Override
			protected String getSourcePathFromPrefs()
			{
				return null;
			}

			@Override
			protected IProcessRunner createProcessRunner()
			{
				return runner;
			}

			@Override
			protected Location getConfigurationLocation()
			{
				return location;
			}

			@Override
			protected Location getUserLocation()
			{
				return location;
			}

			@Override
			protected File toFile(URL locationURL) throws URISyntaxException
			{
				return file;
			}

			@Override
			protected DownloadManager createDownloadManager()
			{
				return dm;
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		dm = null;
		path = null;
		file = null;
		location = null;
		runner = null;
		context = null;
		node = null;
	}

	@Test
	public void testGetPath()
	{
		assertEquals(Path.fromPortableString("/path/to/node"), node.getPath());
	}

	@Test
	public void testGetVersion()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(runner).runInBackground("/path/to/node", "-v"); //$NON-NLS-1$
				will(returnValue(new ProcessStatus(0, "v0.10.30", "")));
			}
		});
		assertEquals("v0.10.30", node.getVersion());
		context.assertIsSatisfied();
	}

	@Test
	public void testExists()
	{
		node = new NodeJS(path);
		context.checking(new Expectations()
		{
			{
				oneOf(path).toFile();
				will(returnValue(file));

				oneOf(file).isFile();
				will(returnValue(true));
			}
		});
		assertTrue(node.exists());
		context.assertIsSatisfied();
	}

	@Test
	public void testDoesntExist()
	{
		node = new NodeJS(path);
		context.checking(new Expectations()
		{
			{
				oneOf(path).toFile();
				will(returnValue(file));

				oneOf(file).isFile();
				will(returnValue(false));
			}
		});
		assertFalse(node.exists());
		context.assertIsSatisfied();
	}

	@Test
	public void testDownloadSource() throws CoreException, IOException
	{
		node = new NodeJS(Path.fromPortableString("/path/to/node"))
		{
			@Override
			protected IProcessRunner createProcessRunner()
			{
				return runner;
			}

			@Override
			protected Location getConfigurationLocation()
			{
				return location;
			}

			@Override
			protected DownloadManager createDownloadManager()
			{
				return dm;
			}

			@Override
			protected File toFile(URL locationURL) throws URISyntaxException
			{
				return file;
			}

			@Override
			public IPath getSourcePath()
			{
				return null;
			}

			@Override
			protected IStatus extractTGZFile(List<IPath> files, File locationFile)
			{
				return Status.OK_STATUS;
			}
		};
		context.checking(new Expectations()
		{
			{
				// getVersion
				oneOf(runner).runInBackground("/path/to/node", "-v"); //$NON-NLS-1$
				will(returnValue(new ProcessStatus(0, "v0.10.30", "")));

				oneOf(dm).addURI(URI.create("http://nodejs.org/dist/v0.10.30/node-v0.10.30.tar.gz"));

				oneOf(dm).start(with(any(IProgressMonitor.class)));
				will(returnValue(Status.OK_STATUS));

				oneOf(dm).getContentsLocations();
				will(returnValue(CollectionsUtil.newList(path)));

				oneOf(location).isReadOnly();
				will(returnValue(false));

				oneOf(location).lock();
				will(returnValue(true));

				oneOf(location).getDataArea("com.aptana.js.core/node");
				will(returnValue(new URL("file:///path/to/config/com.aptana.js.core/node")));

				oneOf(file).mkdirs();
				will(returnValue(true));

				oneOf(location).release();
			}
		});
		IStatus status = node.downloadSource(new NullProgressMonitor());
		assertTrue(status.isOK());
		context.assertIsSatisfied();
	}

	@Test
	public void testValidate()
	{
		node = new NodeJS(path)
		{
			@Override
			protected IProcessRunner createProcessRunner()
			{
				return runner;
			}
		};
		context.checking(new Expectations()
		{
			{
				// exists()
				oneOf(path).toFile();
				will(returnValue(file));

				oneOf(file).isFile();
				will(returnValue(true));

				// getVersion
				oneOf(path).toOSString();
				will(returnValue("/path/to/node"));

				oneOf(runner).runInBackground("/path/to/node", "-v"); //$NON-NLS-1$
				will(returnValue(new ProcessStatus(0, "v8.0", "")));
			}
		});
		IStatus status = node.validate();
		assertTrue(status.isOK());
		context.assertIsSatisfied();
	}

	@Test
	public void testValidateFailsIfDoesntExist()
	{
		node = new NodeJS(path)
		{
			@Override
			protected IProcessRunner createProcessRunner()
			{
				return runner;
			}
		};
		context.checking(new Expectations()
		{
			{
				// exists()
				oneOf(path).toFile();
				will(returnValue(file));

				oneOf(file).isFile();
				will(returnValue(false));

				// getVersion
				never(runner).runInBackground("/path/to/node", "-v"); //$NON-NLS-1$
			}
		});
		IStatus status = node.validate();
		assertFalse(status.isOK());
		context.assertIsSatisfied();
	}

	@Test
	public void testValidateFailsIfBelowMinVersion()
	{
		node = new NodeJS(path)
		{
			@Override
			protected IProcessRunner createProcessRunner()
			{
				return runner;
			}
		};
		context.checking(new Expectations()
		{
			{
				// exists()
				oneOf(path).toFile();
				will(returnValue(file));

				oneOf(file).isFile();
				will(returnValue(true));

				// getVersion
				oneOf(path).toOSString();
				will(returnValue("/path/to/node"));

				oneOf(runner).runInBackground("/path/to/node", "-v"); //$NON-NLS-1$
				will(returnValue(new ProcessStatus(0, "v0.6.0", "")));
			}
		});
		IStatus status = node.validate();
		assertFalse(status.isOK());
		context.assertIsSatisfied();
	}

	@Test
	public void testGetSourcePath() throws IOException
	{
		context.checking(new Expectations()
		{
			{
				// getVersion
				oneOf(runner).runInBackground("/path/to/node", "-v"); //$NON-NLS-1$
				will(returnValue(new ProcessStatus(0, "v0.10.30", "")));

				// config location
				oneOf(location).isReadOnly();
				will(returnValue(false));

				oneOf(location).getDataArea("com.aptana.js.core/node/node-v0.10.30");
				will(returnValue(new URL("file:///path/to/config/com.aptana.js.core/node/node-v0.10.30")));

				oneOf(file).isDirectory();
				will(returnValue(true));

				oneOf(file).getAbsolutePath();
				will(returnValue("/path/to/config/com.aptana.js.core/node/node-v0.10.30"));
			}
		});
		IPath sourcePath = node.getSourcePath();
		assertNotNull(sourcePath);
		assertEquals(Path.fromPortableString("/path/to/config/com.aptana.js.core/node/node-v0.10.30"), sourcePath);
		context.assertIsSatisfied();
	}

}
