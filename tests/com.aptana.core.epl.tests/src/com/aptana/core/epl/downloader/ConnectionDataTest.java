package com.aptana.core.epl.downloader;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConnectionDataTest
{

	@Test
	public void test()
	{
		ConnectionData data = new ConnectionData(3, 1000);
		assertEquals(3, data.getRetryCount());
		assertEquals(1000, data.getRetryDelay());
	}

}
