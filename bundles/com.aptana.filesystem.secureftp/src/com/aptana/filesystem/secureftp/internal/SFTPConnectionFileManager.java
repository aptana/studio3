/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable closeWhereCreated
// $codepro.audit.disable reusableImmutables
// $codepro.audit.disable disallowSleepInsideWhile
// $codepro.audit.disable questionableAssignment
// $codepro.audit.disable variableDeclaredInLoop
// $codepro.audit.disable unnecessaryExceptions
// $codepro.audit.disable exceptionUsage.exceptionCreation
// $codepro.audit.disable declaredExceptions

package com.aptana.filesystem.secureftp.internal;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import com.aptana.core.io.vfs.ExtendedFileInfo;
import com.aptana.core.io.vfs.IExtendedFileStore;
import com.aptana.core.util.ExpiringMap;
import com.aptana.filesystem.ftp.Policy;
import com.aptana.filesystem.ftp.internal.BaseFTPConnectionFileManager;
import com.aptana.filesystem.secureftp.ISFTPConnectionFileManager;
import com.aptana.filesystem.secureftp.ISFTPConstants;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.PermissionDeniedException;
import com.aptana.ide.core.io.preferences.PermissionDirection;
import com.aptana.ide.core.io.preferences.PreferenceUtils;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.ssh.SSHFTPAlgorithm;
import com.enterprisedt.net.ftp.ssh.SSHFTPClient;
import com.enterprisedt.net.ftp.ssh.SSHFTPException;
import com.enterprisedt.net.ftp.ssh.SSHFTPInputStream;
import com.enterprisedt.net.ftp.ssh.SSHFTPOutputStream;
import com.enterprisedt.net.j2ssh.configuration.SshConnectionProperties;
import com.enterprisedt.net.j2ssh.sftp.SshFxpStatus;
import com.enterprisedt.net.j2ssh.transport.publickey.InvalidSshKeyException;
import com.enterprisedt.net.j2ssh.transport.publickey.SshPrivateKeyFile;

/**
 * @author Max Stepanov
 */
public class SFTPConnectionFileManager extends BaseFTPConnectionFileManager implements ISFTPConnectionFileManager
{

	private SSHFTPClient ftpClient;
	private IPath keyFilePath;
	private String transferType;
	private IPath cwd;
	private Map<IPath, FTPFile> ftpFileCache = new ExpiringMap<IPath, FTPFile>(CACHE_TTL);

	private Thread keepaliveThread;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.secureftp.ISFTPConnectionFileManager#init(java.lang.String, int,
	 * org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IPath, java.lang.String, char[], java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public void init(String host, int port, IPath basePath, IPath keyFilePath, String login, char[] password,
			String transferType, String encoding, String compression)
	{
		Assert.isTrue(ftpClient == null, Messages.SFTPConnectionFileManager_ConnectionHasBeenInitialized);
		try
		{
			ftpClient = new SSHFTPClient();
			this.host = host;
			this.port = port;
			this.keyFilePath = keyFilePath;
			this.login = login;
			this.password = (password == null) ? new char[0] : password;
			this.basePath = (basePath != null) ? basePath : Path.ROOT;
			if (keyFilePath != null)
			{
				this.authId = Policy.generateAuthId("SFTP/PUBLICKEY", login, host, port); //$NON-NLS-1$				
			}
			else
			{
				this.authId = Policy.generateAuthId("SFTP", login, host, port); //$NON-NLS-1$
			}
			this.transferType = transferType;
			initFTPClient(ftpClient, encoding, compression);
		}
		catch (Exception e)
		{
			SecureFTPPlugin.log(new Status(IStatus.WARNING, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_InitializationFailed, e));
			ftpClient = null;
		}
	}

	@SuppressWarnings("deprecation")
	private static void initFTPClient(SSHFTPClient ftpClient, String encoding, String compression) throws IOException,
			FTPException
	{
		ftpClient.setTimeout(TIMEOUT);
		ftpClient.setControlEncoding(encoding);
		ftpClient.setMonitorInterval(1024);
		ftpClient.setSleepEnabled(false);
		if (ISFTPConstants.COMPRESSION_NONE.equals(compression))
		{
			ftpClient.disableAllAlgorithms(SSHFTPAlgorithm.COMPRESSION);
			ftpClient.setAlgorithmEnabled(SSHFTPAlgorithm.COMPRESSION_NONE, true);
		}
		else if (ISFTPConstants.COMPRESSION_ZLIB.equals(compression))
		{
			ftpClient.disableAllAlgorithms(SSHFTPAlgorithm.COMPRESSION);
			ftpClient.setAlgorithmEnabled(SSHFTPAlgorithm.COMPRESSION_ZLIB, true);
		}
		ftpClient.setTransportProvider(SshConnectionProperties.USE_STANDARD_SOCKET);
		ftpClient.setConfigFlags(0);
		ftpClient.setTransferBufferSize(TRANSFER_BUFFER_SIZE);
		ftpClient.setValidator(new SSHHostValidator());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IConnectionFileManager#connect(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void connect(IProgressMonitor monitor) throws CoreException
	{
		Assert.isTrue(ftpClient != null, Messages.SFTPConnectionFileManager_ConnectionNotInitialized);
		monitor = Policy.monitorFor(monitor);
		try
		{
			cwd = null;
			cleanup();

			ConnectionContext context = CoreIOPlugin.getConnectionContext(this);

			monitor.beginTask(Messages.SFTPConnectionFileManager_EstablishingConnection, IProgressMonitor.UNKNOWN);
			ftpClient.setRemoteHost(host);
			ftpClient.setRemotePort(port);
			while (true)
			{
				if (keyFilePath != null)
				{
					SshPrivateKeyFile privateKeyFile;
					try
					{
						privateKeyFile = SshPrivateKeyFile.parse(keyFilePath.toFile());
					}
					catch (InvalidSshKeyException e)
					{
						throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
								MessageFormat.format(Messages.SFTPConnectionFileManager_InvalidPrivateKey,
										keyFilePath.toOSString()), e));
					}
					catch (IOException e)
					{
						throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
								MessageFormat.format(Messages.SFTPConnectionFileManager_UnableToReadPrivateKey,
										keyFilePath.toOSString())));
					}
					if (privateKeyFile.isPassphraseProtected() && password.length == 0)
					{
						if (context != null && context.getBoolean(ConnectionContext.NO_PASSWORD_PROMPT))
						{
							password = new char[0];
						}
						else
						{
							getOrPromptPassword(MessageFormat.format(
									Messages.SFTPConnectionFileManager_PublicKeyAuthentication, new Object[] { host,
											keyFilePath.toOSString() }),
									Messages.SFTPConnectionFileManager_SpecifyPassphrase);
							while (true)
							{
								try
								{
									privateKeyFile.toPrivateKey(String.copyValueOf(password));
								}
								catch (InvalidSshKeyException e)
								{
									if (e.getCause() instanceof NoSuchAlgorithmException)
									{
										SecureFTPPlugin.log(new Status(IStatus.WARNING, SecureFTPPlugin.PLUGIN_ID, e
												.getCause().getMessage()));
									}
									promptPassword(MessageFormat.format(
											Messages.SFTPConnectionFileManager_PublicKeyAuthentication, new Object[] {
													host, keyFilePath.toOSString() }),
											Messages.SFTPConnectionFileManager_PassphraseNotAccepted);
									continue;
								}
								break;
							}
						}
					}
					else if (password == null)
					{
						password = new char[0];
					}
					try
					{
						ftpClient.setAuthentication(keyFilePath.toOSString(), login, String.copyValueOf(password));
					}
					catch (InvalidSshKeyException e)
					{
						if (password.length == 0)
						{
							throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
									MessageFormat.format(Messages.SFTPConnectionFileManager_KeyRequirePassphrase,
											keyFilePath.toOSString()), e));
						}
						throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
								MessageFormat.format(Messages.SFTPConnectionFileManager_InvalidPassphrase,
										keyFilePath.toOSString()), e));
					}
				}
				else
				{
					if (password.length == 0 && !ISFTPConstants.LOGIN_ANONYMOUS.equals(login)
							&& (context == null || !context.getBoolean(ConnectionContext.NO_PASSWORD_PROMPT)))
					{
						getOrPromptPassword(
								MessageFormat.format(Messages.SFTPConnectionFileManager_SFTPAuthentication, host),
								Messages.SFTPConnectionFileManager_SpecifyPassword);
					}
					ftpClient.setAuthentication(login, String.copyValueOf(password));
				}
				Policy.checkCanceled(monitor);
				monitor.subTask(Messages.SFTPConnectionFileManager_Authenticating);
				try
				{
					ftpClient.connect();
				}
				catch (SSHFTPException e)
				{
					Policy.checkCanceled(monitor);
					if (keyFilePath != null)
					{
						throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
								MessageFormat.format(Messages.SFTPConnectionFileManager_FailedAuthenticatePublicKey,
										e.getLocalizedMessage()), e));
					}
					if (context != null && context.getBoolean(ConnectionContext.NO_PASSWORD_PROMPT))
					{
						throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
								MessageFormat.format(Messages.SFTPConnectionFileManager_FailedAuthenticate,
										Messages.SFTPConnectionFileManager_IncorrectLogin), e));
					}
					promptPassword(MessageFormat.format(Messages.SFTPConnectionFileManager_SFTPAuthentication, host),
							Messages.SFTPConnectionFileManager_PasswordNotAccepted);
					safeQuit();
					continue;
				}
				break;
			}

			Policy.checkCanceled(monitor);
			changeCurrentDir(basePath);

			ftpClient.setType(ISFTPConstants.TRANSFER_TYPE_ASCII.equals(transferType) ? FTPTransferType.ASCII
					: FTPTransferType.BINARY);
			initKeepAlive();
		}
		catch (OperationCanceledException e)
		{
			safeQuit();
			throw e;
		}
		catch (CoreException e)
		{
			safeQuit();
			throw e;
		}
		catch (UnknownHostException e)
		{
			safeQuit();
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_HostNameNotFound + e.getLocalizedMessage(), e));
		}
		catch (FileNotFoundException e)
		{
			safeQuit();
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_RemoteFolderNotFound + e.getLocalizedMessage(), e));
		}
		catch (Exception e)
		{
			safeQuit();
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_FailedEstablishConnection + e.getLocalizedMessage(), e));
		}
		finally
		{
			monitor.done();
		}
	}

	private void safeQuit()
	{
		try
		{
			if (ftpClient.connected())
			{
				ftpClient.quit();
			}
		}
		catch (Exception e)
		{
			try
			{
				ftpClient.quitImmediately();
			}
			catch (Exception ignore)
			{
				ignore.getCause();
			}
		}
	}

	private void initKeepAlive()
	{
		Thread thread = keepaliveThread;
		if (thread != null && thread.isAlive())
		{
			thread.interrupt();
			try
			{
				thread.join();
			}
			catch (InterruptedException e)
			{
				return;
			}
		}
		keepaliveThread = new Thread()
		{
			@Override
			public void run()
			{
				while (ftpClient != null && ftpClient.connected())
				{
					try
					{
						ftpClient.keepAlive();
					}
					catch (IOException e)
					{
						if (ftpClient.connected())
						{
							try
							{
								ftpClient.quitImmediately();
							}
							catch (Exception ignore)
							{
								ignore.getCause();
							}
						}
					}
					catch (Exception e)
					{
						SecureFTPPlugin.log(new Status(IStatus.ERROR, SecureFTPPlugin.PLUGIN_ID,
								Messages.SFTPConnectionFileManager_ErrorSendKeepAlive, e));
					}
					try
					{
						Thread.sleep(KEEPALIVE_INTERVAL);
					}
					catch (InterruptedException e)
					{
						break;
					}
				}
				keepaliveThread = null;
			}

		};
		keepaliveThread.start();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IConnectionFileManager#disconnect(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void disconnect(IProgressMonitor monitor) throws CoreException
	{
		try
		{
			checkConnected();
		}
		catch (Exception ignore)
		{
			ignore.getCause();
		}
		if (!isConnected())
		{
			return;
		}
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(Messages.SFTPConnectionFileManager_ClosingConnection, IProgressMonitor.UNKNOWN);
		try
		{
			ftpClient.quit();
		}
		catch (Exception e)
		{
			try
			{
				ftpClient.quitImmediately();
			}
			catch (Exception ignore)
			{
				ignore.getCause();
			}
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_FailedDisconnectConnection, e));
		}
		finally
		{
			cwd = null;
			cleanup();
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IConnectionFileManager#isConnected()
	 */
	public boolean isConnected()
	{
		return ftpClient != null && ftpClient.connected();
	}

	protected void changeCurrentDir(IPath path) throws FTPException, IOException, PermissionDeniedException
	{
		try
		{
			if (cwd == null)
			{
				cwd = new Path(ftpClient.pwd());
			}
			if (!cwd.equals(path))
			{
				ftpClient.chdir(path.toPortableString());
				cwd = path;
			}
		}
		catch (FTPException e)
		{
			throwWrappedException(e, path, SshFxpStatus.STATUS_FX_FAILURE);
		}
		catch (IOException e)
		{
			cwd = null;
			throw e;
		}
	}

	private static void throwWrappedException(FTPException e, IPath path) throws FileNotFoundException, FTPException,
			PermissionDeniedException
	{
		throwWrappedException(e, path, -1);
	}

	private static void throwWrappedException(FTPException e, IPath path, int fileNotFoundErrorCode)
			throws FileNotFoundException, FTPException, PermissionDeniedException
	{
		int reply = e.getReplyCode();
		if (reply == -1 && e.getCause() instanceof FTPException)
		{
			reply = ((FTPException) e.getCause()).getReplyCode();
		}
		if (reply == -1 || reply == SshFxpStatus.STATUS_FX_NO_SUCH_FILE || reply == SshFxpStatus.STATUS_FX_NO_SUCH_PATH
				|| reply == fileNotFoundErrorCode)
		{
			throw initFileNotFoundException(path, e);
		}
		if (reply == SshFxpStatus.STATUS_FX_PERMISSION_DENIED)
		{
			throw new PermissionDeniedException(path.toPortableString(), e);
		}
		throw e;
	}

	private static void fillFileInfo(ExtendedFileInfo fileInfo, FTPFile ftpFile)
	{
		fileInfo.setExists(true);
		fileInfo.setName(ftpFile.getName());
		fileInfo.setDirectory(ftpFile.isDir());
		fileInfo.setLength(ftpFile.size());
		fileInfo.setLastModified((ftpFile.lastModified() != null) ? ftpFile.lastModified().getTime() : 0);
		fileInfo.setOwner(ftpFile.getOwner());
		fileInfo.setGroup(ftpFile.getGroup());
		fileInfo.setPermissions(Policy.permissionsFromString(ftpFile.getPermissions()));
		if (ftpFile.isLink())
		{
			fileInfo.setAttribute(EFS.ATTRIBUTE_SYMLINK, true);
			fileInfo.setStringAttribute(EFS.ATTRIBUTE_LINK_TARGET, ftpFile.getLinkedName().trim());
		}
	}

	private static ExtendedFileInfo createFileInfo(FTPFile ftpFile)
	{
		ExtendedFileInfo fileInfo = new ExtendedFileInfo(ftpFile.getName());
		fillFileInfo(fileInfo, ftpFile);
		return fileInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.BaseFTPConnectionFileManager#clearCache(org.eclipse.core.runtime.IPath)
	 */
	@Override
	protected void clearCache(IPath path)
	{
		super.clearCache(path);
		clearCacheAbsolute(basePath.append(path));
	}

	private void clearCacheAbsolute(IPath path)
	{
		int segments = path.segmentCount();
		for (IPath p : new ArrayList<IPath>(ftpFileCache.keySet()))
		{
			if (p.segmentCount() >= segments && path.matchingFirstSegments(p) == segments)
			{
				ftpFileCache.remove(p);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.BaseFTPConnectionFileManager#checkConnected()
	 */
	@Override
	protected void checkConnected() throws Exception
	{
		if (ftpClient.connected())
		{
			try
			{
				ftpClient.keepAlive();
				return;
			}
			catch (FTPException e)
			{
				return;
			}
			catch (IOException ignore)
			{
				ignore.getCause();
			}
			ftpClient.quitImmediately();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.BaseFTPConnectionFileManager#getRootCanonicalURI()
	 */
	@Override
	protected URI getRootCanonicalURI()
	{
		try
		{
			return new URI(
					"sftp", login, host, (port != ISFTPConstants.SFTP_PORT_DEFAULT) ? port : -1, Path.ROOT.toPortableString(), null, null); //$NON-NLS-1$
		}
		catch (URISyntaxException e)
		{
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.BaseFTPConnectionFileManager#fetchFile(org.eclipse.core.runtime.IPath, int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected ExtendedFileInfo fetchFile(IPath path, int options, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException, PermissionDeniedException
	{
		try
		{
			IPath dirPath = path.removeLastSegments(1);
			String name = path.lastSegment();
			FTPFile result = ftpFileCache.get(path);
			if (result == null)
			{
				if ((options & IExtendedFileStore.EXISTENCE) != 0)
				{
					ExtendedFileInfo fileInfo = new ExtendedFileInfo(path.lastSegment());
					try
					{
						fileInfo.setExists(ftpClient.exists(path.toPortableString()));
						ftpClient.chdir(path.toPortableString());
						fileInfo.setDirectory(true);
					}
					catch (FTPException e)
					{
						try
						{
							throwWrappedException(e, path, SshFxpStatus.STATUS_FX_FAILURE);
						}
						catch (FileNotFoundException ignore)
						{
							ignore.getCause();
						}
					}
					return fileInfo;
				}
				FTPFile[] ftpFiles = listFiles(dirPath, monitor);
				for (FTPFile ftpFile : ftpFiles)
				{
					String fileName = ftpFile.getName();
					if (fileName == null || ".".equals(fileName) || "..".equals(fileName)) { //$NON-NLS-1$ //$NON-NLS-2$
						if (Path.ROOT.equals(path) && ".".equals(fileName)) { //$NON-NLS-1$
							ftpFile.setName(path.toPortableString());
							ftpFileCache.put(path, ftpFile);
							result = ftpFile;
						}
						continue;
					}
					ftpFileCache.put(dirPath.append(fileName), ftpFile);
					if (name != null && name.equals(fileName))
					{
						result = ftpFile;
					}
				}
			}
			if (result != null)
			{
				return createFileInfo(result);
			}
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (PermissionDeniedException e)
		{
			throw e;
		}
		catch (OperationCanceledException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_FailedFetchFileInfo, e));
		}
		ExtendedFileInfo fileInfo = new ExtendedFileInfo(path.lastSegment());
		fileInfo.setExists(false);
		return fileInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.BaseFTPConnectionFileManager#fetchFiles(org.eclipse.core.runtime.IPath, int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected ExtendedFileInfo[] fetchFiles(IPath path, int options, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException, PermissionDeniedException
	{
		monitor = Policy.subMonitorFor(monitor, 1);
		try
		{
			FTPFile[] ftpFiles = listFiles(path, monitor);
			monitor.beginTask(Messages.SFTPConnectionFileManager_GatheringFileDetails, ftpFiles.length);
			List<ExtendedFileInfo> list = new ArrayList<ExtendedFileInfo>();
			for (FTPFile ftpFile : ftpFiles)
			{
				String fileName = ftpFile.getName();
				if (fileName != null && ".".equals(fileName) || "..".equals(fileName)) { //$NON-NLS-1$ //$NON-NLS-2$
					monitor.worked(1);
					continue;
				}
				IPath filePath = path.append(fileName);
				ftpFileCache.put(filePath, ftpFile);

				ExtendedFileInfo fileInfo = createFileInfo(ftpFile);
				list.add(fileInfo);
				monitor.worked(1);
			}
			return list.toArray(new ExtendedFileInfo[list.size()]);
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (PermissionDeniedException e)
		{
			throw e;
		}
		catch (OperationCanceledException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_FailedFetchDirectory, e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.BaseFTPConnectionFileManager#createDirectory(org.eclipse.core.runtime.IPath,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void createDirectory(IPath path, IProgressMonitor monitor) throws CoreException, FileNotFoundException
	{
		try
		{
			try
			{
				try
				{
					changeCurrentDir(path);
					return; // directory exists - return
				}
				catch (FileNotFoundException ignore)
				{
					ignore.getCause();
				}
				ftpClient.mkdir(path.toPortableString());
				if (PreferenceUtils.getUpdatePermissions(PermissionDirection.UPLOAD)
						&& PreferenceUtils.getSpecificPermissions(PermissionDirection.UPLOAD))
				{
					changeFilePermissions(path, PreferenceUtils.getFolderPermissions(PermissionDirection.UPLOAD),
							monitor);
				}
			}
			catch (FTPException e)
			{
				throwWrappedException(e, path);
			}
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (CoreException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_FailedCreateDirectory, e));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.BaseFTPConnectionFileManager#deleteDirectory(org.eclipse.core.runtime.IPath,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void deleteDirectory(IPath path, IProgressMonitor monitor) throws CoreException, FileNotFoundException
	{
		MultiStatus status = new MultiStatus(SecureFTPPlugin.PLUGIN_ID, 0, null, null);
		try
		{
			IPath dirPath = path.removeLastSegments(1);
			changeCurrentDir(dirPath);
			Policy.checkCanceled(monitor);
			recursiveDeleteTree(path, monitor, status);
			changeCurrentDir(dirPath);
			ftpClient.rmdir(path.lastSegment());
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (OperationCanceledException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			if (!status.isOK())
			{
				MultiStatus multiStatus = new MultiStatus(SecureFTPPlugin.PLUGIN_ID, 0,
						Messages.SFTPConnectionFileManager_FailedDeleteDirectory, e);
				multiStatus.addAll(status);
			}
			else
			{
				throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
						Messages.SFTPConnectionFileManager_FailedDeleteDirectory, e));
			}
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.internal.BaseFTPConnectionFileManager#createFile(org.eclipse.core.runtime.IPath,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void createFile(IPath path, IProgressMonitor monitor) throws CoreException, FileNotFoundException,
			PermissionDeniedException
	{
		try
		{
			IPath dirPath = path.removeLastSegments(1);
			changeCurrentDir(dirPath);
			Policy.checkCanceled(monitor);
			try
			{
				ftpClient.put(new ByteArrayInputStream(new byte[] {}), path.lastSegment());
			}
			catch (FTPException e)
			{
				throwWrappedException(e, path);
				throw e;
			}
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (OperationCanceledException e)
		{
			throw e;
		}
		catch (PermissionDeniedException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID, MessageFormat.format(
					Messages.SFTPConnectionFileManager_FailedDeletingFile, path), e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.BaseFTPConnectionFileManager#deleteFile(org.eclipse.core.runtime.IPath,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void deleteFile(IPath path, IProgressMonitor monitor) throws CoreException, FileNotFoundException
	{
		try
		{
			IPath dirPath = path.removeLastSegments(1);
			changeCurrentDir(dirPath);
			Policy.checkCanceled(monitor);
			try
			{
				ftpClient.delete(path.lastSegment());
			}
			catch (FTPException e)
			{
				throwWrappedException(e, path);
				throw e;
			}
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (OperationCanceledException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID, MessageFormat.format(
					Messages.SFTPConnectionFileManager_FailedDeletingFile, path), e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.BaseFTPConnectionFileManager#renameFile(org.eclipse.core.runtime.IPath,
	 * org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void renameFile(IPath sourcePath, IPath destinationPath, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException
	{
		try
		{
			changeCurrentDir(sourcePath.removeLastSegments(1));
			Policy.checkCanceled(monitor);
			if (ftpClient.exists(destinationPath.toPortableString()))
			{
				ftpClient.delete(destinationPath.toPortableString());
			}
			try
			{
				ftpClient.rename(sourcePath.toPortableString(), destinationPath.toPortableString());
			}
			catch (FTPException e)
			{
				throwWrappedException(e, sourcePath);
				SecureFTPPlugin.log(new Status(IStatus.ERROR, SecureFTPPlugin.PLUGIN_ID, MessageFormat.format(
						Messages.SFTPConnectionFileManager_FailedRename, new Object[] { sourcePath, destinationPath }),
						e));
				throw e;
			}
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (PermissionDeniedException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, SecureFTPPlugin.PLUGIN_ID, MessageFormat.format(
					Messages.SFTPConnectionFileManager_PermissionDenied0, sourcePath.toPortableString()), e));
		}
		catch (OperationCanceledException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_FailedRenaming, e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.BaseConnectionFileManager#renameDirectory(org.eclipse.core.runtime.IPath,
	 * org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void renameDirectory(IPath sourcePath, IPath destinationPath, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException
	{
		try
		{
			changeCurrentDir(sourcePath.removeLastSegments(1));
			Policy.checkCanceled(monitor);
			if (ftpClient.exists(destinationPath.toPortableString()))
			{
				changeCurrentDir(destinationPath.removeLastSegments(1));
				ftpClient.rmdir(destinationPath.lastSegment());
			}
			try
			{
				ftpClient.rename(sourcePath.toPortableString(), destinationPath.toPortableString());
			}
			catch (FTPException e)
			{
				throwWrappedException(e, sourcePath);
				SecureFTPPlugin.log(new Status(IStatus.ERROR, SecureFTPPlugin.PLUGIN_ID, MessageFormat.format(
						Messages.SFTPConnectionFileManager_FailedRename, new Object[] { sourcePath, destinationPath }),
						e));
				throw e;
			}
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (PermissionDeniedException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, SecureFTPPlugin.PLUGIN_ID, MessageFormat.format(
					Messages.SFTPConnectionFileManager_PermissionDenied0, sourcePath.toPortableString()), e));
		}
		catch (OperationCanceledException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_FailedRenaming, e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.BaseFTPConnectionFileManager#listDirectory(org.eclipse.core.runtime.IPath,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected String[] listDirectory(IPath path, IProgressMonitor monitor) throws CoreException, FileNotFoundException
	{
		try
		{
			FTPFile[] ftpFiles = listFiles(path, monitor);
			List<String> list = new ArrayList<String>();
			for (FTPFile ftpFile : ftpFiles)
			{
				String name = ftpFile.getName();
				if (name == null || ".".equals(name) || "..".equals(name)) { //$NON-NLS-1$ //$NON-NLS-2$
					continue;
				}
				ftpFileCache.put(path.append(name), ftpFile);
				list.add(name);
			}
			return list.toArray(new String[list.size()]);
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (OperationCanceledException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_FailedListDirectory, e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.BaseFTPConnectionFileManager#readFile(org.eclipse.core.runtime.IPath,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected InputStream readFile(IPath path, IProgressMonitor monitor) throws CoreException, FileNotFoundException
	{
		monitor.beginTask(Messages.SFTPConnectionFileManager_InitiatingFileDownload, 4);
		try
		{
			Policy.checkCanceled(monitor);
			changeCurrentDir(path.removeLastSegments(1));
			monitor.worked(1);
			Policy.checkCanceled(monitor);
			try
			{
				return new SFTPFileDownloadInputStream(new SSHFTPInputStream(ftpClient, path.toPortableString()));
			}
			catch (FTPException e)
			{
				throwWrappedException(e, path);
				return null;
			}
		}
		catch (Exception e)
		{
			if (e instanceof OperationCanceledException)
			{
				throw (OperationCanceledException) e;
			}
			else if (e instanceof FileNotFoundException)
			{
				throw (FileNotFoundException) e;
			}
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_FailedOpeningFile, e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.internal.BaseFTPConnectionFileManager#writeFile(org.eclipse.core.runtime.IPath,
	 * boolean, long, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected OutputStream writeFile(final IPath path, boolean useTemporary, long permissions, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException
	{
		monitor.beginTask(Messages.SFTPConnectionFileManager_FailedInitiatingFile, 4);
		try
		{
			Policy.checkCanceled(monitor);
			changeCurrentDir(path.removeLastSegments(1));
			monitor.worked(1);
			Policy.checkCanceled(monitor);
			return new SFTPFileUploadOutputStream(ftpClient, new SSHFTPOutputStream(ftpClient, useTemporary ? path
					.removeLastSegments(1).append(generateTempFileName(path.lastSegment())).toPortableString()
					: path.toPortableString()), useTemporary ? path.toPortableString() : null, new Date(), permissions,
					new Runnable()
					{
						public void run()
						{
							clearCacheAbsolute(path);
						}
					});
		}
		catch (Exception e)
		{
			if (e instanceof OperationCanceledException)
			{
				throw (OperationCanceledException) e;
			}
			else if (e instanceof FileNotFoundException)
			{
				throw (FileNotFoundException) e;
			}
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_FailedOpeningFile, e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.BaseFTPConnectionFileManager#setModificationTime(org.eclipse.core.runtime.IPath,
	 * long, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void setModificationTime(IPath path, long modificationTime, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException
	{
		try
		{
			IPath dirPath = path.removeLastSegments(1);
			changeCurrentDir(dirPath);
			Policy.checkCanceled(monitor);
			try
			{
				ftpClient.setModTime(path.lastSegment(), new Date(modificationTime));
			}
			catch (FTPException e)
			{
				throwWrappedException(e, path);
			}
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (OperationCanceledException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_FailedSetModificationTime, e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.BaseFTPConnectionFileManager#changeFileGroup(org.eclipse.core.runtime.IPath,
	 * java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void changeFileGroup(IPath path, String group, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException
	{
		try
		{
			IPath dirPath = path.removeLastSegments(1);
			changeCurrentDir(dirPath);
			Policy.checkCanceled(monitor);
			try
			{
				int gid = Integer.parseInt(group);
				ftpClient.changeGroup(gid, path.lastSegment());
			}
			catch (FTPException e)
			{
				throwWrappedException(e, path);
			}
			catch (NumberFormatException e)
			{
				throw new IllegalArgumentException(Messages.SFTPConnectionFileManager_InvalidGroup);
			}
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (OperationCanceledException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_FailedChangeGroup, e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.BaseFTPConnectionFileManager#changeFilePermissions(org.eclipse.core.runtime.IPath,
	 * long, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void changeFilePermissions(IPath path, long permissions, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException
	{
		try
		{
			IPath dirPath = path.removeLastSegments(1);
			changeCurrentDir(dirPath);
			Policy.checkCanceled(monitor);
			ftpClient.changeMode((int) (permissions & 0777), path.lastSegment());
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (OperationCanceledException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID,
					Messages.SFTPConnectionFileManager_FailedSetPermissions, e));
		}
		finally
		{
			monitor.done();
		}
	}

	private FTPFile[] listFiles(IPath dirPath, IProgressMonitor monitor) throws IOException, ParseException,
			FTPException, PermissionDeniedException
	{
		Policy.checkCanceled(monitor);
		try
		{
			return ftpClient.dirDetails(dirPath.toPortableString());
		}
		catch (FTPException e)
		{
			throwWrappedException(e, dirPath);
			return null; // never runs
		}
	}

	private void recursiveDeleteTree(IPath path, IProgressMonitor monitor, MultiStatus status) throws IOException,
			ParseException
	{
		try
		{
			changeCurrentDir(path);
			FTPFile[] ftpFiles = listFiles(path, monitor);
			List<String> dirs = new ArrayList<String>();
			for (FTPFile ftpFile : ftpFiles)
			{
				String name = ftpFile.getName();
				if (name == null || ".".equals(name) || "..".equals(name)) { //$NON-NLS-1$ //$NON-NLS-2$
					continue;
				}
				if (ftpFile.isDir())
				{
					dirs.add(name);
					continue;
				}
				Policy.checkCanceled(monitor);
				monitor.subTask(path.append(name).toPortableString());
				try
				{
					ftpClient.delete(name);
				}
				catch (FTPException e)
				{
					status.add(new Status(IStatus.ERROR, SecureFTPPlugin.PLUGIN_ID, MessageFormat.format(
							Messages.SFTPConnectionFileManager_FailedDeleting, path.append(name).toPortableString()), e));
				}
				monitor.worked(1);
			}
			for (String name : dirs)
			{
				monitor.subTask(path.append(name).toPortableString());
				recursiveDeleteTree(path.append(name), monitor, status);
				Policy.checkCanceled(monitor);
				changeCurrentDir(path);
				Policy.checkCanceled(monitor);
				ftpClient.rmdir(name);
				monitor.worked(1);
			}
		}
		catch (IOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			status.add(new Status(IStatus.ERROR, SecureFTPPlugin.PLUGIN_ID, MessageFormat.format(
					Messages.SFTPConnectionFileManager_FailedDeleting, path.toPortableString()), e));
		}
	}

	private static String generateTempFileName(String base)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(base).append(TMP_UPLOAD_SUFFIX);
		String ext = Path.fromPortableString(base).getFileExtension();
		if (ext != null)
		{
			sb.append(ext);
		}
		return sb.toString();
	}
}
