package com.aptana.core.epl.downloader;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URI;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.filetransfer.IFileRangeSpecification;
import org.eclipse.ecf.filetransfer.identity.FileIDFactory;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransfer;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransferFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileReaderTest
{

	private Mockery context;
	private IConnectContext cc;
	private FileReader reader;
	private OutputStream out;
	private URI uri;
	private IJobManager jobManager;
	private FileIDFactory fileIDFactory;
	private Proxy proxy;
	private IRetrieveFileTransferFactory fileTransferFactory;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		cc = context.mock(IConnectContext.class);
		// proxy = context.mock(Proxy.class);
		proxy = null;
		fileIDFactory = context.mock(FileIDFactory.class);
		fileTransferFactory = context.mock(IRetrieveFileTransferFactory.class);
		out = context.mock(OutputStream.class);
		uri = URI.create("http://example.com/index.html");
		jobManager = context.mock(IJobManager.class);
		reader = new FileReader(null, cc)
		{
			@Override
			protected IRetrieveFileTransferFactory getRetrieveFileTransferFactory()
			{
				return fileTransferFactory;
			}

			@Override
			protected Proxy getProxy(URI uri)
			{
				return proxy;
			}

			@Override
			protected FileIDFactory getFileIDFactory()
			{
				return fileIDFactory;
			}

			@Override
			protected IJobManager getTheJobManager()
			{
				return jobManager;
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		cc = null;
		context = null;
		reader = null;
		out = null;
		uri = null;
		jobManager = null;
	}

	@Test
	public void testDownload() throws FileNotFoundException, ProtocolException, CoreException, InterruptedException
	{
		final IRetrieveFileTransfer adapter = context.mock(IRetrieveFileTransfer.class);
		final IFileID fileID = context.mock(IFileID.class);
		final Namespace ns = context.mock(Namespace.class);

		context.checking(new Expectations()
		{
			{
				// sendRetrieveRequest
				oneOf(fileTransferFactory).newInstance();
				will(returnValue(adapter));

				oneOf(adapter).setConnectContextForAuthentication(cc);

				oneOf(adapter).setProxy(proxy);

				oneOf(adapter).getRetrieveNamespace();
				will(returnValue(ns));

				oneOf(fileIDFactory).createFileID(ns, uri.toString());
				will(returnValue(fileID));

				oneOf(adapter).sendRetrieveRequest(with(fileID), with(any(IFileRangeSpecification.class)),
						with(reader), with(aNull(Map.class)));

				// readInto
				oneOf(jobManager).join(with(reader), with(any(IProgressMonitor.class)));
			}
		});

		reader.readInto(uri, out, 0, null);
		context.assertIsSatisfied();
	}
}
