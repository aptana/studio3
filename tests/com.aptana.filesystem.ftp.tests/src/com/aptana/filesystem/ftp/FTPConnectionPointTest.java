/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.ftp;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.aptana.core.epl.IMemento;
import com.aptana.core.epl.XMLMemento;
import com.aptana.testing.categories.IntegrationTests;

/**
 * @author Max Stepanov
 */
@Category({ IntegrationTests.class })
public class FTPConnectionPointTest
{

	private static final String name = "My FTP Site";
	private static final String host = "127.0.0.1";
	private static final int port = 2222;
	private static final String login = "user";
	private static final char[] password = "password".toCharArray();
	private static final IPath path = Path.fromPortableString("/home/user");
	private static final String encoding = "UTF-8";
	private static final boolean passiveMode = true;
	private static final String timezone = "GMT";
	private static final String transferType = IFTPConstants.TRANSFER_TYPE_BINARY;

	@Test
	public void testPersistance()
	{
		createAndTestConnectionPoint(name, host, port, login, password, path, encoding, passiveMode, timezone,
				transferType);
	}

	@Test
	public void testPassiveMode()
	{
		createAndTestConnectionPoint(name, host, port, login, password, path, encoding, true, timezone, transferType);
		createAndTestConnectionPoint(name, host, port, login, password, path, encoding, false, timezone, transferType);
	}

	@Test
	public void testTransferTypes()
	{
		createAndTestConnectionPoint(name, host, port, login, password, path, encoding, passiveMode, timezone,
				IFTPConstants.TRANSFER_TYPE_ASCII);
		createAndTestConnectionPoint(name, host, port, login, password, path, encoding, passiveMode, timezone,
				IFTPConstants.TRANSFER_TYPE_BINARY);
		createAndTestConnectionPoint(name, host, port, login, password, path, encoding, passiveMode, timezone,
				IFTPConstants.TRANSFER_TYPE_AUTO);
	}

	private static void createAndTestConnectionPoint(String name, String host, int port, String login, char[] password,
			IPath path, String encoding, boolean passiveMode, String timezone, String transferType)
	{
		FTPConnectionPoint cp = new FTPConnectionPoint();
		cp.setName(name);
		cp.setHost(host);
		cp.setPort(port);
		cp.setLogin(login);
		cp.setPassword(password);
		cp.setPassiveMode(passiveMode);
		cp.setPath(path);
		cp.setTimezone(timezone);
		cp.setTransferType(transferType);
		cp.setEncoding(encoding);

		XMLMemento root = XMLMemento.createWriteRoot("root");
		IMemento memento = root.createChild("item");
		cp.saveState(memento);

		cp = new FTPConnectionPoint();
		cp.loadState(memento);
		assertEquals("Name doesn't match", name, cp.getName());
		assertEquals("Host doesn't match", host, cp.getHost());
		assertEquals("Port doesn't match", port, cp.getPort());
		assertEquals("Login doesn't match", login, cp.getLogin());
		assertEquals("Password should not be persistent", null, cp.getPassword());
		assertEquals("Passive mode doesn't match", passiveMode, cp.isPassiveMode());
		assertEquals("Path doesn't match", path, cp.getPath());
		assertEquals("Timezone doesn't match", timezone, cp.getTimezone());
		assertEquals("Transfer type doesn't match", transferType, cp.getTransferType());
		assertEquals("Encoding doesn't match", encoding, cp.getEncoding());
	}
}
