package com.aptana.ide.core.io.downloader;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ecf.core.security.IConnectContext;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.epl.downloader.FileReader;

public class ContentDownloadRequestTest
{

	private ContentDownloadRequest cdr;
	private Mockery context;
	private IConnectContext cc;
	private File saveTo;
	private FileReader reader;
	private OutputStream out;
	private URI uri;

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
		saveTo = context.mock(File.class);
		reader = context.mock(FileReader.class);
		out = context.mock(OutputStream.class);
		uri = URI.create("http://example.com/index.html");
		cdr = new ContentDownloadRequest(uri, saveTo, cc)
		{
			@Override
			protected OutputStream createOutputStream(File dest) throws FileNotFoundException
			{
				return out;
			}

			@Override
			protected FileReader createReader()
			{
				return reader;
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		cdr = null;
		cc = null;
		saveTo = null;
		context = null;
		reader = null;
		out = null;
		uri = null;
	}

	@Test
	public void testDownload() throws FileNotFoundException, ProtocolException, CoreException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(reader).readInto(uri, out, 0, null);

				// FIXME there's problems with us overriding/expecting calls on final methods
				// oneOf(reader).getResult();
				// will(returnValue(Status.OK_STATUS));
			}
		});

		cdr.execute(null);
		assertTrue(cdr.getResult().isOK());
		context.assertIsSatisfied();
	}

}
