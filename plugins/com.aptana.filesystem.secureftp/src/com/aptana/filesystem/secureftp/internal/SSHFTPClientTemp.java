package com.aptana.filesystem.secureftp.internal;

import java.lang.reflect.Field;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.enterprisedt.net.ftp.ssh.SSHFTPClient;
import com.enterprisedt.net.j2ssh.SftpClient;

/**
 * Temporary wrapper class to fix SFTP timeout error. Will be removed once new version of class is available from EDT
 * (fixed in 3.4.x).
 * 
 * @author Ingo Muschenetz
 */
public class SSHFTPClientTemp extends SSHFTPClient
{
	public boolean connected()
	{
		try
		{
			// SSHFTPClient is obfuscated, so internal private SftpClient property is strangely named.
			Field f = SSHFTPClient.class.getDeclaredField("€"); //$NON-NLS-1$
			f.setAccessible(true);
			SftpClient sftp = (SftpClient) f.get(this);
			return sftp == null ? false : !sftp.isClosed();
		}
		catch (Exception e)
		{
			SecureFTPPlugin.log(new Status(IStatus.ERROR, SecureFTPPlugin.PLUGIN_ID, MessageFormat.format(
					"Unable to access interior SFTP property. Error: {0}", e.getLocalizedMessage())));
			return false;
		}
	}
}
