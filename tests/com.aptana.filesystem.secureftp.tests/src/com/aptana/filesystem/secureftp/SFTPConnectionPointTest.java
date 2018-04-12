/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.secureftp;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com.aptana.core.epl.IMemento;
import com.aptana.core.epl.XMLMemento;
import com.aptana.filesystem.ftp.IFTPConstants;

/**
 * @author Max Stepanov
 *
 */
public class SFTPConnectionPointTest {

	private static final String name = "My SFTP Site";
	private static final String host = "127.0.0.1";
	private static final int port = 2222;
	private static final String login = "user";
	private static final char[] password = "password".toCharArray();
	private static final IPath path = Path.fromPortableString("/home/user");
	private static final String encoding = "UTF-8";
	private static final String compression = ISFTPConstants.COMPRESSION_AUTO;
	private static final IPath keyFilePath = null;
	private static final String transferType = IFTPConstants.TRANSFER_TYPE_BINARY;


	@Test
	public void testPersistance() {
		createAndTestConnectionPoint(name, host, port, login, password, path, encoding, compression, keyFilePath, transferType);
	}
	
	@Test
	public void testCompression() {
		createAndTestConnectionPoint(name, host, port, login, password, path, encoding, ISFTPConstants.COMPRESSION_AUTO, keyFilePath, transferType);
		createAndTestConnectionPoint(name, host, port, login, password, path, encoding, ISFTPConstants.COMPRESSION_NONE, keyFilePath, transferType);
		createAndTestConnectionPoint(name, host, port, login, password, path, encoding, ISFTPConstants.COMPRESSION_ZLIB, keyFilePath, transferType);
	}
	
	@Test
	public void testTransferTypes() {
		createAndTestConnectionPoint(name, host, port, login, password, path, encoding, compression, keyFilePath, IFTPConstants.TRANSFER_TYPE_ASCII);
		createAndTestConnectionPoint(name, host, port, login, password, path, encoding, compression, keyFilePath, IFTPConstants.TRANSFER_TYPE_BINARY);
		createAndTestConnectionPoint(name, host, port, login, password, path, encoding, compression, keyFilePath, IFTPConstants.TRANSFER_TYPE_AUTO);
	}

	@Test
	public void testKeyFilePath() {
		createAndTestConnectionPoint(name, host, port, login, password, path, encoding, compression, null, transferType);
		createAndTestConnectionPoint(name, host, port, login, password, path, encoding, compression, Path.fromPortableString("/home/user/id_rsa"), transferType);
	}

	private static void createAndTestConnectionPoint(String name, String host, int port, String login, char[] password, IPath path, String encoding, String compression, IPath keyFilePath, String transferType) {
		SFTPConnectionPoint cp = new SFTPConnectionPoint();
		cp.setName(name);
		cp.setHost(host);
		cp.setPort(port);
		cp.setLogin(login);
		cp.setPassword(password);
		cp.setCompression(compression);
		cp.setPath(path);
		cp.setKeyFilePath(keyFilePath);
		cp.setTransferType(transferType);
		cp.setEncoding(encoding);
		
		XMLMemento root = XMLMemento.createWriteRoot("root");
		IMemento memento = root.createChild("item");
		cp.saveState(memento);
		
		cp = new SFTPConnectionPoint();
		cp.loadState(memento);
		assertEquals("Name doesn't match", name, cp.getName());
		assertEquals("Host doesn't match", host, cp.getHost());
		assertEquals("Port doesn't match", port, cp.getPort());
		assertEquals("Login doesn't match", login, cp.getLogin());
		assertEquals("Password should not be persistent", null, cp.getPassword());
		assertEquals("Compression mode doesn't match", compression, cp.getCompression());
		assertEquals("Path doesn't match", path, cp.getPath());
		assertEquals("Key file path doesn't match", keyFilePath, cp.getKeyFilePath());
		assertEquals("Transfer type doesn't match", transferType, cp.getTransferType());
		assertEquals("Encoding doesn't match", encoding, cp.getEncoding());
	}
}
