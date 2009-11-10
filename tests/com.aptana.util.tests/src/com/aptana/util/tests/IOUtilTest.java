package com.aptana.util.tests;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import com.aptana.util.IOUtil;

public class IOUtilTest extends TestCase
{
	private Mockery context = new Mockery()
	{
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	public void testRead() throws Exception
	{
		final byte[] b = new byte[8192];
		final int off = 0;
		final int len = 8192;
		final InputStream stream = context.mock(InputStream.class);
		context.checking(new Expectations()
		{
			{
				oneOf(stream).read(b, off, len);
				will(throwException(new IOException("")));
				oneOf(stream).close();
			}
		});
		IOUtil.read(stream);
		context.assertIsSatisfied();
	}

	public void testReadWithNullInputReturnsNull() throws Exception
	{
		assertNull(IOUtil.read(null));
	}

}
