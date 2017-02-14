package com.aptana.core.epl.downloader;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

public class FileInfoTest
{

	private FileInfo info;

	@After
	public void tearDown() throws Exception
	{
		info = null;
	}

	@Test
	public void test()
	{
		info = new FileInfo();
		assertEquals(FileInfo.UNKNOWN_RATE, info.getAverageSpeed());
		assertEquals("", info.getContentType());
		assertEquals(0, info.getLastModified());
		assertEquals("", info.getRemoteName());
		assertEquals(-1, info.getSize()); // apparently -1 is also UNKNOWN_SIZE?
		
		info.setSize(1000);
		assertEquals(1000, info.getSize());

		info.setContentType("text/html");
		assertEquals("text/html", info.getContentType());
		
		info.setAverageSpeed(1000L);
		assertEquals(1000L, info.getAverageSpeed());

		long timestamp = System.currentTimeMillis();
		info.setLastModified(timestamp);
		assertEquals(timestamp, info.getLastModified());
		
		info.setName("name.html");
		assertEquals("name.html", info.getRemoteName());
	}

}
