package com.aptana.filesystem.ftp.tests;

import org.eclipse.core.runtime.CoreException;

import com.aptana.core.io.tests.CommonConnectionTest;
import com.aptana.filesystem.ftp.FTPConnectionPoint;
import com.aptana.filesystem.ftp.IFTPConstants;

public abstract class FTPCommonConnectionTest extends CommonConnectionTest
{
	@Override
	protected void setUp() throws Exception
	{
		FTPConnectionPoint ftpcp = getConnectionPoint();
		ftpcp.setHost(getConfig().getProperty("ftp.host", "10.10.1.60")); //$NON-NLS-1$ //$NON-NLS-2$
		ftpcp.setLogin(getConfig().getProperty("ftp.username", "ftpuser")); //$NON-NLS-1$ //$NON-NLS-2$
		ftpcp.setPassword(getConfig().getProperty("ftp.password", //$NON-NLS-1$
				String.valueOf(new char[] { 'l', 'e', 't', 'm', 'e', 'i', 'n' })).toCharArray());
		supportsSetModificationTime = Boolean.valueOf(getConfig()
				.getProperty("ftp.supportsSetModificationTime", "true"));
		supportsChangeGroup = Boolean.valueOf(getConfig().getProperty("ftp.supportsChangeGroup", "false"));
		supportsChangePermissions = Boolean.valueOf(getConfig().getProperty("ftp.supportsChangePermissions", "true"));
		remoteFileDirectory = getConfig().getProperty("ftp.remoteFileDirectory", null);
		cp = ftpcp;
		super.setUp();
	}

	public abstract FTPConnectionPoint getConnectionPoint();

	public final void testIncorrectTimezone() throws CoreException
	{
		FTPConnectionPoint ftpcp = (FTPConnectionPoint) cp;
		String timezone = ftpcp.getTimezone();

		// Erroneous timezone will set timezone as GMT
		ftpcp.setTimezone("ERROR");
		ftpcp.connect(null);
		assertTrue(ftpcp.getTimezone().equals("ERROR"));

		ftpcp.setTimezone(timezone);
	}

	public final void testIncorrectTransferType() throws CoreException
	{
		FTPConnectionPoint ftpcp = (FTPConnectionPoint) cp;

		// Should be set to BINARY by default
		assertTrue(ftpcp.getTransferType().equals(IFTPConstants.TRANSFER_TYPE_BINARY));

		// set to ASCII
		ftpcp.setTransferType(IFTPConstants.TRANSFER_TYPE_ASCII);
		assertTrue(ftpcp.getTransferType().equals(IFTPConstants.TRANSFER_TYPE_ASCII));

		// set to ERROR, which should set to ERROR after checking (internal is set to BINARY, but unable to confirm
		// through API)
		ftpcp.setTransferType("ERROR");
		ftpcp.connect(null);
		ftpcp.getRoot().fetchInfo();
		assertTrue(ftpcp.getTransferType().equals("ERROR"));
	}

	public final void testIncorrectEncoding() throws CoreException
	{
		FTPConnectionPoint ftpcp = (FTPConnectionPoint) cp;
		String encoding = ftpcp.getEncoding();

		try
		{
			ftpcp.setEncoding(null);
			ftpcp.connect(null);
			fail();
		}
		catch (CoreException e)
		{

		}

		ftpcp.setEncoding(encoding);
	}
}
