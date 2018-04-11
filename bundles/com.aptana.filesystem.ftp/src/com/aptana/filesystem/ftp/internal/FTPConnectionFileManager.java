/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable closeWhereCreated
// $codepro.audit.disable questionableAssignment
// $codepro.audit.disable unnecessaryExceptions
// $codepro.audit.disable emptyCatchClause
// $codepro.audit.disable variableDeclaredInLoop

package com.aptana.filesystem.ftp.internal;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PerformanceStats;
import org.eclipse.core.runtime.Status;

import com.aptana.core.io.vfs.ExtendedFileInfo;
import com.aptana.core.io.vfs.IExtendedFileStore;
import com.aptana.core.util.ExpiringMap;
import com.aptana.filesystem.ftp.FTPPlugin;
import com.aptana.filesystem.ftp.IFTPConnectionFileManager;
import com.aptana.filesystem.ftp.IFTPConstants;
import com.aptana.filesystem.ftp.Policy;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.PermissionDeniedException;
import com.aptana.ide.core.io.preferences.PermissionDirection;
import com.aptana.ide.core.io.preferences.PreferenceUtils;
import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPClientInterface;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPConnectionClosedException;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.net.ftp.FTPFileFactory;
import com.enterprisedt.net.ftp.FTPInputStream;
import com.enterprisedt.net.ftp.FTPMessageListener;
import com.enterprisedt.net.ftp.FTPOutputStream;
import com.enterprisedt.net.ftp.FTPReply;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.MalformedReplyException;
import com.enterprisedt.net.ftp.pro.ProFTPClient;

/**
 * @author Max Stepanov
 */
public class FTPConnectionFileManager extends BaseFTPConnectionFileManager implements IFTPConnectionFileManager,
		IPoolConnectionManager
{

	private static final String TMP_TIMEZONE_CHECK = "_tmp_tz_check"; //$NON-NLS-1$

	private final static String WINDOWS_STR = "WINDOWS"; //$NON-NLS-1$

	private final static SimpleDateFormat[] UTIME_FORMATS = new SimpleDateFormat[] {
			new SimpleDateFormat("'UTIME' yyyyMMddHHmmss '{0}'"), //$NON-NLS-1$
			new SimpleDateFormat("'UTIME {0}' yyyyMMddHHmmss yyyyMMddHHmmss yyyyMMddHHmmss 'UTC'"), //$NON-NLS-1$
	};

	protected FTPClient ftpClient;
	private List<String> serverFeatures;
	protected String transferType;
	protected String timezone;
	protected IPath cwd;
	private FTPFileFactory fileFactory;
	private Boolean statSupported = null;
	private Boolean listASupported = null;
	private int utimeFormat = -1;
	private Map<IPath, FTPFile> ftpFileCache = new ExpiringMap<IPath, FTPFile>(CACHE_TTL);
	private long serverToLocalTimeZoneShift = Integer.MIN_VALUE;
	protected boolean hasServerInfo;
	protected PrintWriter messageLogWriter;

	protected FTPClientPool pool;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.IFTPConnectionFileManager#init(java.lang.String, int,
	 * org.eclipse.core.runtime.IPath, java.lang.String, char[], boolean, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	public void init(String host, int port, IPath basePath, String login, char[] password, boolean passive,
			String transferType, String encoding, String timezone)
	{
		Assert.isTrue(ftpClient == null, Messages.FTPConnectionFileManager_already_initialized);
		try
		{
			this.pool = new FTPClientPool(this);
			this.ftpClient = new ProFTPClient();
			this.host = host;
			this.port = port;
			this.login = login;
			this.password = (password == null) ? EMPTY_PASSWORD : password;
			this.basePath = (basePath != null) ? basePath : Path.ROOT;
			this.authId = Policy.generateAuthId("FTP", login, host, port); //$NON-NLS-1$
			this.transferType = transferType;
			this.timezone = (timezone != null && timezone.length() == 0) ? null : timezone;
			initFTPClient(ftpClient, passive, encoding);
		}
		catch (Exception e)
		{
			FTPPlugin.log(new Status(IStatus.WARNING, FTPPlugin.PLUGIN_ID,
					Messages.FTPConnectionFileManager_initialization_failed, e));
			ftpClient = null;
		}
	}

	protected static void initFTPClient(FTPClient ftpClient, boolean passive, String encoding) throws IOException,
			FTPException
	{
		ftpClient.setTimeout(TIMEOUT);
		ftpClient.setControlEncoding(encoding);
		ftpClient.setMonitorInterval(1024);
		ftpClient.setConnectMode(passive ? FTPConnectMode.PASV : FTPConnectMode.ACTIVE);
		ftpClient.setRetryCount(RETRY);
		ftpClient.setRetryDelay(RETRY_DELAY);
		ftpClient.setServerWakeupInterval(KEEPALIVE_INTERVAL);
		ftpClient.setDeleteOnFailure(true);
		ftpClient.setTransferBufferSize(TRANSFER_BUFFER_SIZE);
	}

	private static void connectFTPClient(FTPClient ftpClient) throws IOException, FTPException
	{
		PerformanceStats stats = PerformanceStats.getStats(
				"com.aptana.filesystem.ftp/perf/connect", FTPConnectionFileManager.class.getName()); //$NON-NLS-1$
		stats.startRun(ftpClient.getRemoteHost());
		try
		{
			ftpClient.connect();
		}
		finally
		{
			stats.endRun();
		}
	}

	protected void initAndAuthFTPClient(FTPClientInterface clientInterface, IProgressMonitor monitor)
			throws IOException, FTPException
	{
		if (clientInterface.connected())
		{
			return;
		}
		FTPClient newFtpClient = (FTPClient) clientInterface;
		initFTPClient(newFtpClient, FTPConnectMode.PASV.equals(ftpClient.getConnectMode()),
				ftpClient.getControlEncoding());
		newFtpClient.setRemoteHost(host);
		newFtpClient.setRemotePort(port);
		Policy.checkCanceled(monitor);
		connectFTPClient(newFtpClient);
		monitor.worked(1);
		Policy.checkCanceled(monitor);
		newFtpClient.login(login, String.copyValueOf(password));
		monitor.worked(1);
	}

	protected static void setMessageLogger(FTPClient ftpClient, final PrintWriter writer)
	{
		FTPMessageListener listener = null;
		if (writer != null && ftpClient.getMessageListener() == null)
		{
			listener = new FTPMessageListener()
			{
				public void logCommand(String command)
				{
					if (command.startsWith("---> ")) { //$NON-NLS-1$
						command = command.substring(5);
					}
					Matcher matcher = PASS_COMMAND_PATTERN.matcher(command);
					if (matcher.matches())
					{
						command = matcher.replaceFirst("$1********"); //$NON-NLS-1$
					}
					writer.print("ftp> "); //$NON-NLS-1$
					writer.println(command);
					writer.flush();
				}

				public void logReply(String reply)
				{
					writer.println(reply);
					writer.flush();
				}
			};
		}
		ftpClient.setMessageListener(listener);

	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IConnectionFileManager#connect(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void connect(IProgressMonitor monitor) throws CoreException
	{
		Assert.isTrue(ftpClient != null, Messages.FTPConnectionFileManager_not_initialized);
		monitor = Policy.monitorFor(monitor);
		try
		{
			cwd = null;
			cleanup();

			ConnectionContext context = CoreIOPlugin.getConnectionContext(this);

			if (messageLogWriter == null)
			{
				if (context != null)
				{
					Object object = context.get(ConnectionContext.COMMAND_LOG);
					if (object instanceof PrintWriter)
					{
						messageLogWriter = (PrintWriter) object;
					}
					else if (object instanceof OutputStream)
					{
						messageLogWriter = new PrintWriter((OutputStream) object);
					}
				}
				if (messageLogWriter == null)
				{
					messageLogWriter = FTPPlugin.getDefault().getFTPLogWriter();
				}
				if (messageLogWriter != null)
				{
					messageLogWriter.println(MessageFormat.format("---------- FTP {0} ----------", host)); //$NON-NLS-1$
					setMessageLogger(ftpClient, messageLogWriter);
				}
			}
			else
			{
				messageLogWriter.println(MessageFormat.format("---------- RECONNECTING - FTP {0} ----------", host)); //$NON-NLS-1$
			}

			monitor.beginTask(Messages.FTPConnectionFileManager_establishing_connection, IProgressMonitor.UNKNOWN);
			ftpClient.setRemoteHost(host);
			ftpClient.setRemotePort(port);
			while (true)
			{
				monitor.subTask(Messages.FTPConnectionFileManager_connecting);
				connectFTPClient(ftpClient);
				if (password.length == 0 && !IFTPConstants.LOGIN_ANONYMOUS.equals(login)
						&& (context == null || !context.getBoolean(ConnectionContext.NO_PASSWORD_PROMPT)))
				{
					getOrPromptPassword(MessageFormat.format(Messages.FTPConnectionFileManager_ftp_auth, host),
							Messages.FTPConnectionFileManager_specify_password);
				}
				Policy.checkCanceled(monitor);
				monitor.subTask(Messages.FTPConnectionFileManager_authenticating);
				try
				{
					ftpClient.login(login, String.copyValueOf(password));
				}
				catch (FTPException e)
				{
					Policy.checkCanceled(monitor);
					if (ftpClient.getLastValidReply() == null
							|| "331".equals(ftpClient.getLastValidReply().getReplyCode())) { //$NON-NLS-1$
						if (context != null && context.getBoolean(ConnectionContext.NO_PASSWORD_PROMPT))
						{
							throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID, MessageFormat.format(
									Messages.FTPConnectionFileManager_FailedAuthenticate, e.getLocalizedMessage()), e));
						}
						promptPassword(MessageFormat.format(Messages.FTPConnectionFileManager_ftp_auth, host),
								Messages.FTPConnectionFileManager_invalid_password);
						safeQuit();
						continue;
					}
					throw e;
				}
				break;
			}

			Policy.checkCanceled(monitor);
			changeCurrentDir(basePath);

			ftpClient.setType(IFTPConstants.TRANSFER_TYPE_ASCII.equals(transferType) ? FTPTransferType.ASCII
					: FTPTransferType.BINARY);

			if ((hasServerInfo || (context != null && context.getBoolean(ConnectionContext.QUICK_CONNECT)))
					&& !(context != null && context.getBoolean(ConnectionContext.DETECT_TIMEZONE)))
			{
				return;
			}
			getherServerInfo(context, monitor);

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
			throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID,
					Messages.FTPConnectionFileManager_HostNameNotFound + e.getLocalizedMessage(), e));
		}
		catch (FileNotFoundException e)
		{
			safeQuit();
			throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID,
					Messages.FTPConnectionFileManager_RemoteFolderNotFound + e.getLocalizedMessage(), e));
		}
		catch (Exception e)
		{
			safeQuit();
			throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID,
					Messages.FTPConnectionFileManager_connection_failed + e.getLocalizedMessage(), e));
		}
		finally
		{
			monitor.done();
		}
	}

	@SuppressWarnings("deprecation")
	protected void getherServerInfo(ConnectionContext context, IProgressMonitor monitor)
	{
		Policy.checkCanceled(monitor);
		monitor.subTask(Messages.FTPConnectionFileManager_gethering_server_info);
		serverFeatures = null;
		try
		{
			String[] features = ftpClient.features();
			if (features != null && features.length > 0)
			{
				serverFeatures = new ArrayList<String>();
				for (int i = 0; i < features.length; ++i)
				{
					String feature = features[i].trim();
					if (feature.indexOf(' ') > 0)
					{
						feature = feature.substring(0, feature.indexOf(' '));
					}
					serverFeatures.add(feature);
				}
			}
		}
		catch (Exception e)
		{
			e.getCause();
		}
		try
		{
			String[] validCodes = { "214" }; //$NON-NLS-1$
			FTPReply reply = ftpClient.sendCommand("SITE HELP"); //$NON-NLS-1$
			ftpClient.validateReply(reply, validCodes);
			if (serverFeatures == null)
			{
				serverFeatures = new ArrayList<String>();
			}
			String[] data = reply.getReplyData();
			for (int i = 0; i < data.length; ++i)
			{
				String cmd = data[i].trim();
				if (cmd.startsWith("214")) { //$NON-NLS-1$
					continue;
				}
				serverFeatures.add(MessageFormat.format("SITE {0}", cmd)); //$NON-NLS-1$
			}
		}
		catch (Exception e)
		{
			e.getCause();
		}

		Policy.checkCanceled(monitor);
		FTPFile[] rootFiles = null;
		try
		{
			rootFiles = listFiles(Path.ROOT, monitor);
		}
		catch (Exception e)
		{
		}

		if (context != null && context.getBoolean(ConnectionContext.DETECT_TIMEZONE))
		{
			serverToLocalTimeZoneShift = Integer.MIN_VALUE;
		}
		else if (timezone != null)
		{
			TimeZone tz = TimeZone.getTimeZone(timezone);
			if (tz != null)
			{
				long time = new Date().getTime();
				serverToLocalTimeZoneShift = TimeZone.getDefault().getOffset(time) - tz.getOffset(time);
			}
		}
		if (serverToLocalTimeZoneShift == Integer.MIN_VALUE)
		{
			Policy.checkCanceled(monitor);
			try
			{
				changeCurrentDir(Path.ROOT);
				FTPFile file = null;
				if (rootFiles != null)
				{
					for (FTPFile ftpFile : rootFiles)
					{
						if (ftpFile.isFile() && ftpFile.getName() != null
								&& !ftpFile.getName().startsWith(".ht") //$NON-NLS-1$
								&& !(ftpFile.lastModified().getHours() == 0 && ftpFile.lastModified().getMinutes() == 0 && ftpFile
										.lastModified().getSeconds() == 0))
						{
							file = ftpFile;
							break;
						}
					}
				}
				if (file == null && !Path.ROOT.equals(basePath))
				{
					FTPFile[] ftpFiles = listFiles(basePath, monitor);
					for (FTPFile ftpFile : ftpFiles)
					{
						if (ftpFile.isFile() && ftpFile.getName() != null
								&& !ftpFile.getName().startsWith(".ht") //$NON-NLS-1$
								&& !(ftpFile.lastModified().getHours() == 0 && ftpFile.lastModified().getMinutes() == 0 && ftpFile
										.lastModified().getSeconds() == 0))
						{
							file = ftpFile;
							break;
						}
					}
				}
				Date lastModifiedLocal = null;
				if (file == null)
				{
					changeCurrentDir(basePath);
					lastModifiedLocal = new Date();
					ftpClient.put(new ByteArrayInputStream(new byte[] {}), TMP_TIMEZONE_CHECK);
					for (FTPFile ftpFile : listFiles(basePath, monitor))
					{
						if (TMP_TIMEZONE_CHECK.equals(ftpFile.getName()))
						{
							file = ftpFile;
							defaultOwner = ftpFile.getOwner();
							defaultGroup = ftpFile.getGroup();
							break;
						}
					}
				}
				if (file != null)
				{
					Date serverTimeInLocalTZ = file.lastModified();
					if (serverSupportsFeature("MDTM")) { //$NON-NLS-1$
						Date gmtTimeInLocalTZ = ftpClient.modtime(file.getName());
						if (gmtTimeInLocalTZ != null)
						{
							// align to minutes
							long serverToGmtShift = (gmtTimeInLocalTZ.getTime() - gmtTimeInLocalTZ.getTime() % 60000)
									- (serverTimeInLocalTZ.getTime() - serverTimeInLocalTZ.getTime() % 60000);
							Calendar calendar = (Calendar) Calendar.getInstance().clone();
							calendar.setTime(new Date());
							serverToLocalTimeZoneShift = serverToGmtShift - calendar.get(Calendar.ZONE_OFFSET)
									- calendar.get(Calendar.DST_OFFSET);
						}
					}
					if (serverToLocalTimeZoneShift == Integer.MIN_VALUE && lastModifiedLocal != null)
					{
						long serverToLocalShift = (lastModifiedLocal.getTime() - lastModifiedLocal.getTime() % 60000)
								- (serverTimeInLocalTZ.getTime() - serverTimeInLocalTZ.getTime() % 60000);
						// align to 1/4 hour
						long rem = serverToLocalShift % 900000;
						if (rem < 450000)
						{
							serverToLocalShift -= rem;
						}
						else
						{
							serverToLocalShift += (900000 - rem);
						}
						serverToLocalTimeZoneShift = serverToLocalShift;
					}
					if (TMP_TIMEZONE_CHECK.equals(file.getName()))
					{
						ftpClient.delete(file.getName());
					}
					if (context != null && serverToLocalTimeZoneShift != Integer.MIN_VALUE)
					{
						Calendar cal = (Calendar) Calendar.getInstance().clone();
						cal.setTime(new Date());
						int rawOffset = (int) (cal.get(Calendar.ZONE_OFFSET) - serverToLocalTimeZoneShift);
						context.put(ConnectionContext.SERVER_TIMEZONE, TimeZone.getAvailableIDs(rawOffset));
					}
				}
			}
			catch (OperationCanceledException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				FTPPlugin.log(new Status(IStatus.WARNING, FTPPlugin.PLUGIN_ID,
						Messages.FTPConnectionFileManager_server_tz_check, e));
			}
			if (serverToLocalTimeZoneShift == Integer.MIN_VALUE)
			{
				serverToLocalTimeZoneShift = 0;
			}
		}

		hasServerInfo = true;

	}

	protected void safeQuit()
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
			}
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IConnectionFileManager#disconnect(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public synchronized void disconnect(IProgressMonitor monitor) throws CoreException
	{
		if (!isConnected())
		{
			return;
		}
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(Messages.FTPConnectionFileManager_closing_connection, IProgressMonitor.UNKNOWN);
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
			}
		}
		finally
		{
			cwd = null;
			pool.dispose();
			cleanup();
			monitor.done();
		}
	}

	protected boolean serverSupportsFeature(String feature)
	{
		if (serverFeatures != null)
		{
			return serverFeatures.contains(feature);
		}
		return false; // assume doesn't supports be default
	}

	protected void changeCurrentDir(IPath path) throws FTPException, IOException
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
			throwFileNotFound(e, path);
		}
		catch (IOException e)
		{
			cwd = null;
			throw e;
		}
	}

	private static void throwFileNotFound(FTPException e, IPath path) throws FileNotFoundException, FTPException
	{
		int code = e.getReplyCode();
		if (code == 550 || code == 450)
		{
			throw initFileNotFoundException(path, e);
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
	 * @see com.aptana.ide.core.ftp.BaseFTPConnectionFileManager#clearCache(org.eclipse.core.runtime.IPath)
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
	 * @see com.aptana.core.io.vfs.BaseConnectionFileManager#interruptOperation()
	 */
	@Override
	protected void interruptOperation()
	{
		try
		{
			if (ftpClient.connected())
			{
				ftpClient.quitImmediately();
			}
		}
		catch (Exception ignore)
		{
		}
		super.interruptOperation();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.filesystem.ftp.internal.BaseFTPConnectionFileManager#canUseTemporaryFile(org.eclipse.core.runtime.
	 * IPath, com.aptana.core.io.vfs.ExtendedFileInfo, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected boolean canUseTemporaryFile(IPath path, ExtendedFileInfo fileInfo, IProgressMonitor monitor)
	{
		if (super.canUseTemporaryFile(path, fileInfo, monitor))
		{
			if (fileInfo.exists() && !serverSupportsFeature("SITE CHMOD")) { //$NON-NLS-1$
				return false;
			}
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.BaseFTPConnectionFileManager#checkConnected()
	 */
	@Override
	protected void checkConnected() throws Exception
	{ // $codepro.audit.disable declaredExceptions
		if (ftpClient.connected())
		{
			try
			{
				ftpClient.noOperation();
				ftpClient.setType(FTPTransferType.BINARY);
				return;
			}
			catch (FTPConnectionClosedException e)
			{
			}
			catch (FTPException e)
			{
			}
			catch (IOException e)
			{
			}
			ftpClient.quitImmediately();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.BaseFTPConnectionFileManager#getRootCanonicalURI()
	 */
	@Override
	protected URI getRootCanonicalURI()
	{
		try
		{
			return new URI(
					"ftp", login, host, (port != IFTPConstants.FTP_PORT_DEFAULT) ? port : -1, Path.ROOT.toPortableString(), null, null); //$NON-NLS-1$
		}
		catch (URISyntaxException e)
		{
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.BaseFTPConnectionFileManager#fetchFile(org.eclipse.core.runtime.IPath, int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected ExtendedFileInfo fetchFile(IPath path, int options, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException
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
						changeCurrentDir(path);
						fileInfo.setExists(true);
						fileInfo.setDirectory(true);
					}
					catch (FileNotFoundException ignore)
					{
					}
					if (!fileInfo.exists())
					{
						fileInfo.setExists(existsFile(path));
					}
					return fileInfo;
				}
				FTPFile[] ftpFiles = listFiles(dirPath, monitor);
				for (FTPFile ftpFile : ftpFiles)
				{
					Date lastModifiedServerInLocalTZ = ftpFile.lastModified();
					if (serverToLocalTimeZoneShift != 0 && lastModifiedServerInLocalTZ != null)
					{
						ftpFile.setLastModified(new Date(lastModifiedServerInLocalTZ.getTime()
								+ serverToLocalTimeZoneShift));
					}
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
					if (name != null && name.equalsIgnoreCase(fileName))
					{
						result = ftpFile;
					}
				}
			}
			if ((options & IExtendedFileStore.DETAILED) != 0)
			{
				if (result != null && !result.isDir() && name != null && result.lastModified().getSeconds() == 0)
				{
					if (serverSupportsFeature("MDTM")) { //$NON-NLS-1$
						changeCurrentDir(dirPath);
						Policy.checkCanceled(monitor);
						try
						{
							Date lastModifiedLocalTZ = ftpClient.modtime(name);
							if (lastModifiedLocalTZ != null)
							{
								result.setLastModified(lastModifiedLocalTZ);
							}
						}
						catch (FTPException e)
						{
						}
					}
				}
			}
			if (result == null && Path.ROOT.equals(path))
			{
				result = new FTPFile("", path.toPortableString(), 0, true, new Date(0)); //$NON-NLS-1$
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
		catch (OperationCanceledException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID,
					Messages.FTPConnectionFileManager_fetch_failed, e));
		}
		ExtendedFileInfo fileInfo = new ExtendedFileInfo(path.lastSegment());
		fileInfo.setExists(false);
		return fileInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.BaseFTPConnectionFileManager#fetchFiles(org.eclipse.core.runtime.IPath, int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected ExtendedFileInfo[] fetchFiles(IPath path, int options, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException
	{
		monitor = Policy.subMonitorFor(monitor, 1);
		try
		{
			FTPFile[] ftpFiles = listFiles(path, monitor);
			monitor.beginTask(Messages.FTPConnectionFileManager_gethering_file_details, ftpFiles.length);
			List<ExtendedFileInfo> list = new ArrayList<ExtendedFileInfo>();
			for (FTPFile ftpFile : ftpFiles)
			{
				String fileName = ftpFile.getName();
				if (fileName == null || ".".equals(fileName) || "..".equals(fileName)) { //$NON-NLS-1$ //$NON-NLS-2$
					monitor.worked(1);
					continue;
				}
				Date lastModifiedServerInLocalTZ = ftpFile.lastModified();
				if (serverToLocalTimeZoneShift != 0 && lastModifiedServerInLocalTZ != null)
				{
					ftpFile.setLastModified(new Date(lastModifiedServerInLocalTZ.getTime() + serverToLocalTimeZoneShift));
				}
				if ((options & IExtendedFileStore.DETAILED) != 0)
				{
					if (!ftpFile.isDir() && ftpFile.lastModified().getSeconds() == 0)
					{
						if (serverSupportsFeature("MDTM")) { //$NON-NLS-1$
							changeCurrentDir(path);
							Policy.checkCanceled(monitor);
							try
							{
								Date lastModifiedLocalTZ = ftpClient.modtime(fileName);
								if (lastModifiedLocalTZ != null)
								{
									ftpFile.setLastModified(lastModifiedLocalTZ);
								}
							}
							catch (FTPException e)
							{
							}
						}
					}
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
		catch (OperationCanceledException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID,
					Messages.FTPConnectionFileManager_fetching_directory_failed, e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.BaseFTPConnectionFileManager#listDirectory(org.eclipse.core.runtime.IPath,
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
			throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID,
					Messages.FTPConnectionFileManager_listing_directory_failed, e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.BaseFTPConnectionFileManager#readFile(org.eclipse.core.runtime.IPath,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected InputStream readFile(IPath path, IProgressMonitor monitor) throws CoreException, FileNotFoundException
	{
		monitor.beginTask(Messages.FTPConnectionFileManager_initiating_download, 4);
		FTPClient downloadFtpClient = (FTPClient) pool.checkOut();
		try
		{
			initAndAuthFTPClient(downloadFtpClient, monitor);
			Policy.checkCanceled(monitor);
			setMessageLogger(downloadFtpClient, messageLogWriter);
			downloadFtpClient.setType(IFTPConstants.TRANSFER_TYPE_ASCII.equals(transferType) ? FTPTransferType.ASCII
					: FTPTransferType.BINARY);
			try
			{
				downloadFtpClient.chdir(path.removeLastSegments(1).toPortableString());
			}
			catch (FTPException e)
			{
				throwFileNotFound(e, path.removeLastSegments(1));
			}
			monitor.worked(1);
			Policy.checkCanceled(monitor);
			try
			{
				return new FTPFileDownloadInputStream(pool, downloadFtpClient, new FTPInputStream(downloadFtpClient,
						path.lastSegment()));
			}
			catch (FTPException e)
			{
				throwFileNotFound(e, path);
				return null;
			}
		}
		catch (Exception e)
		{
			setMessageLogger(downloadFtpClient, null);
			pool.checkIn(downloadFtpClient);
			if (e instanceof OperationCanceledException)
			{
				throw (OperationCanceledException) e;
			}
			else if (e instanceof FileNotFoundException)
			{
				throw (FileNotFoundException) e;
			}
			throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID,
					Messages.FTPConnectionFileManager_opening_file_read_failed, e));
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
		monitor.beginTask(Messages.FTPConnectionFileManager_initiating_file_upload, 4);
		FTPClient uploadFtpClient = (FTPClient) pool.checkOut();
		try
		{
			initAndAuthFTPClient(uploadFtpClient, monitor);
			Policy.checkCanceled(monitor);
			setMessageLogger(uploadFtpClient, messageLogWriter);
			uploadFtpClient.setType(IFTPConstants.TRANSFER_TYPE_ASCII.equals(transferType) ? FTPTransferType.ASCII
					: FTPTransferType.BINARY);
			IPath dirPath = path.removeLastSegments(1);
			try
			{
				uploadFtpClient.chdir(dirPath.toPortableString());
			}
			catch (FTPException e)
			{
				throwFileNotFound(e, dirPath);
			}
			monitor.worked(1);
			Policy.checkCanceled(monitor);
			return new FTPFileUploadOutputStream(pool, uploadFtpClient, new FTPOutputStream(uploadFtpClient,
					useTemporary ? generateTempFileName(path.lastSegment()) : path.lastSegment()),
					useTemporary ? path.lastSegment() : null, null, permissions, new Runnable()
					{
						public void run()
						{
							clearCacheAbsolute(path);
						}
					});
		}
		catch (Exception e)
		{
			setMessageLogger(uploadFtpClient, null);
			pool.checkIn(uploadFtpClient);
			if (e instanceof OperationCanceledException)
			{
				throw (OperationCanceledException) e;
			}
			else if (e instanceof FileNotFoundException)
			{
				throw (FileNotFoundException) e;
			}
			else if (e instanceof FTPException)
			{
				if (((FTPException) e).getReplyCode() == 553)
				{
					throw initFileNotFoundException(path, e);
				}
			}
			throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID,
					Messages.FTPConnectionFileManager_opening_file_write_failed, e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.BaseFTPConnectionFileManager#deleteDirectory(org.eclipse.core.runtime.IPath,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void deleteDirectory(IPath path, IProgressMonitor monitor) throws CoreException, FileNotFoundException
	{
		MultiStatus status = new MultiStatus(FTPPlugin.PLUGIN_ID, 0, null, null);
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
				MultiStatus multiStatus = new MultiStatus(FTPPlugin.PLUGIN_ID, 0,
						Messages.FTPConnectionFileManager_deleting_directory_failed, e);
				multiStatus.addAll(status);
			}
			else
			{
				throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID,
						Messages.FTPConnectionFileManager_deleting_directory_failed, e));
			}
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.BaseFTPConnectionFileManager#deleteFile(org.eclipse.core.runtime.IPath,
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
				if (e.getReplyCode() == 532)
				{
					throw new PermissionDeniedException(path.toPortableString(), e);
				}
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
			throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID, MessageFormat.format(
					Messages.FTPConnectionFileManager_deleting_failed, path), e));
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
				if (e.getReplyCode() == 532)
				{
					throw new PermissionDeniedException(path.toPortableString(), e);
				}
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
			throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID, MessageFormat.format(
					Messages.FTPConnectionFileManager_CreateFile0Failed, path), e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.BaseFTPConnectionFileManager#createDirectory(org.eclipse.core.runtime.IPath,
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
				throwFileNotFound(e, path);
			}
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID,
					Messages.FTPConnectionFileManager_creating_directory_failed, e));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.BaseFTPConnectionFileManager#renameFile(org.eclipse.core.runtime.IPath,
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
			try
			{
				ftpClient.rename(sourcePath.toPortableString(), destinationPath.toPortableString());
			}
			catch (FTPException e)
			{
				throwFileNotFound(e, sourcePath);
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
			throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID,
					Messages.FTPConnectionFileManager_renaming_failed, e));
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
		renameFile(sourcePath, destinationPath, monitor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.BaseFTPConnectionFileManager#setModificationTime(org.eclipse.core.runtime.IPath,
	 * long, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void setModificationTime(IPath path, long modificationTime, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException
	{
		if (!serverSupportsFeature("MFMT") && !serverSupportsFeature("SITE UTIME")) { //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		try
		{
			IPath dirPath = path.removeLastSegments(1);
			changeCurrentDir(dirPath);
			Policy.checkCanceled(monitor);
			if (serverSupportsFeature("MFMT")) { //$NON-NLS-1$
				ftpClient.setModTime(path.lastSegment(), new Date(modificationTime));
			}
			else if (serverSupportsFeature("SITE UTIME")) { //$NON-NLS-1$
				Calendar cal = Calendar.getInstance();
				long localTimezoneShift = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
				Date date = new Date(modificationTime - localTimezoneShift);
				if (utimeFormat == -1)
				{
					for (utimeFormat = 0; utimeFormat < UTIME_FORMATS.length; ++utimeFormat)
					{
						String format = UTIME_FORMATS[utimeFormat].format(date);
						FTPReply reply = ftpClient
								.sendCommand("SITE " + MessageFormat.format(format, path.lastSegment())); //$NON-NLS-1$
						if (!"500".equals(reply.getReplyCode()) && !"501".equals(reply.getReplyCode())) { //$NON-NLS-1$ //$NON-NLS-2$
							break;
						}
					}
				}
				else if (utimeFormat >= 0 && utimeFormat < UTIME_FORMATS.length)
				{
					String format = UTIME_FORMATS[utimeFormat].format(date);
					ftpClient.site(MessageFormat.format(format, path.lastSegment()));
				}
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
			throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID,
					Messages.FTPConnectionFileManager_set_modification_time_failed, e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.BaseFTPConnectionFileManager#changeFilePermissions(org.eclipse.core.runtime.IPath,
	 * long, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void changeFilePermissions(IPath path, long permissions, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException
	{
		if (!serverSupportsFeature("SITE CHMOD")) { //$NON-NLS-1$
			return;
		}
		try
		{
			IPath dirPath = path.removeLastSegments(1);
			changeCurrentDir(dirPath);
			Policy.checkCanceled(monitor);
			ftpClient.site(MessageFormat.format("CHMOD {0} {1}", Long.toOctalString(permissions), path.lastSegment())); //$NON-NLS-1$
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
			throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID,
					Messages.FTPConnectionFileManager_FailedSetPermissions, e));
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.BaseFTPConnectionFileManager#changeFileGroup(org.eclipse.core.runtime.IPath,
	 * java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void changeFileGroup(IPath path, String group, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException
	{
		if (!serverSupportsFeature("SITE CHGRP")) { //$NON-NLS-1$
			return;
		}
		try
		{
			IPath dirPath = path.removeLastSegments(1);
			changeCurrentDir(dirPath);
			Policy.checkCanceled(monitor);
			ftpClient.site(MessageFormat.format("CHGRP {0} {1}", group, path.lastSegment())); //$NON-NLS-1$
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
			throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID,
					Messages.FTPConnectionFileManager_FailedSetGroup, e));
		}
		finally
		{
			monitor.done();
		}
	}

	private boolean existsFile(IPath filePath) throws IOException, FTPException
	{
		return ftpClient.existsFile(filePath.toPortableString());
	}

	private FTPFile[] ftpSTAT(String dirname) throws IOException, FTPException, ParseException
	{
		setupFileFactory();
		String[] validCodes = { "211", "212", "213" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		FTPReply reply = ftpClient.sendCommand("STAT " + dirname); //$NON-NLS-1$
		ftpClient.validateReply(reply, validCodes);
		String[] data = reply.getReplyData();
		if (data == null)
		{
			return null;
		}
		for (int i = 0; i < data.length; ++i)
		{
			data[i] = data[i].trim();
		}
		FTPFile[] ftpFiles = fileFactory.parse(data);
		for (FTPFile ftpFile : ftpFiles)
		{
			String name = ftpFile.getName();
			if (name != null && name.indexOf('/') != -1)
			{
				ftpFile.setName(Path.fromPortableString(name).lastSegment());
			}
		}
		return ftpFiles;
	}

	private FTPFile[] ftpLIST(IPath dirPath, IProgressMonitor monitor) throws IOException, ParseException, FTPException
	{
		setupFileFactory();
		changeCurrentDir(dirPath);
		Policy.checkCanceled(monitor);
		if (!Boolean.FALSE.equals(listASupported))
		{
			try
			{
				FTPFile[] ftpFiles = fileFactory.parse(ftpClient.dir("-a", true)); //$NON-NLS-1$
				listASupported = Boolean.TRUE;
				return ftpFiles;
			}
			catch (FTPException e)
			{
				if (listASupported == null && e.getReplyCode() >= 500)
				{
					listASupported = Boolean.FALSE;
				}
				else
				{
					throw e;
				}
			}
		}
		return fileFactory.parse(ftpClient.dir(".", true)); //$NON-NLS-1$
	}

	private void setupFileFactory() throws IOException, FTPException
	{
		if (fileFactory == null)
		{
			try
			{
				fileFactory = new FTPFileFactory(ftpClient.system());
			}
			catch (FTPException ex)
			{
				fileFactory = new FTPFileFactory("UNIX"); //$NON-NLS-1$
			}
			fileFactory.setLocales(FTPClient.DEFAULT_LISTING_LOCALES);
		}
	}

	private FTPFile[] listFiles(IPath dirPath, IProgressMonitor monitor) throws IOException, ParseException,
			FTPException
	{
		FTPFile[] ftpFiles = null;
		if (!Boolean.FALSE.equals(statSupported) && dirPath.toPortableString().indexOf(' ') == -1)
		{
			try
			{
				ftpFiles = ftpSTAT(dirPath.addTrailingSeparator().toPortableString());
			}
			catch (MalformedReplyException e)
			{
				statSupported = Boolean.FALSE;
			}
			catch (FTPException e)
			{
				if (e.getReplyCode() == 501 || e.getReplyCode() == 502 || e.getReplyCode() == 504)
				{
					statSupported = null;
				}
				else if (Boolean.TRUE.equals(statSupported) && e.getReplyCode() != 500 && e.getReplyCode() != 550)
				{
					throwFileNotFound(e, dirPath);
				}
			}
			if (ftpFiles == null || ftpFiles.length == 0)
			{
				if (statSupported == null)
				{
					statSupported = Boolean.FALSE;
				}
				return ftpLIST(dirPath, monitor);
			}
			else if (ftpFiles[0].isLink())
			{
				return ftpLIST(dirPath, monitor);
			}
			else if (statSupported == null)
			{
				statSupported = Boolean.TRUE;
			}
		}
		else
		{
			ftpFiles = ftpLIST(dirPath, monitor);
		}
		if (fileFactory.getSystem().toUpperCase().startsWith(WINDOWS_STR) && ftpFiles != null)
		{
			for (FTPFile ftpFile : ftpFiles)
			{
				if (ftpFile.getPermissions() == null)
				{
					ftpFile.setPermissions("-rw-r-----"); //$NON-NLS-1$
				}
			}
		}
		return ftpFiles;
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
					status.add(new Status(IStatus.ERROR, FTPPlugin.PLUGIN_ID, MessageFormat.format(
							Messages.FTPConnectionFileManager_deleting_failed, path.append(name).toPortableString()), e));
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
			status.add(new Status(IStatus.ERROR, FTPPlugin.PLUGIN_ID, MessageFormat.format(
					Messages.FTPConnectionFileManager_deleting_failed, path.toPortableString()), e));
		}
	}

	private static String generateTempFileName(String base)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(base).append(TMP_UPLOAD_SUFFIX);
		String ext = Path.fromPortableString(base).getFileExtension();
		if (ext != null)
		{
			sb.append(ext);
		}
		return sb.toString();
	}

	public FTPClient newClient()
	{
		return new ProFTPClient();
	}
}
