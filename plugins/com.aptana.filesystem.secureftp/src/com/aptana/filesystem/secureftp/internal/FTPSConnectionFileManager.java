/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable closeWhereCreated
// $codepro.audit.disable variableDeclaredInLoop
// $codepro.audit.disable questionableAssignment
// $codepro.audit.disable exceptionUsage.exceptionCreation

package com.aptana.filesystem.secureftp.internal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PerformanceStats;
import org.eclipse.core.runtime.Status;

import com.aptana.core.logging.IdeLog;
import com.aptana.filesystem.ftp.FTPPlugin;
import com.aptana.filesystem.ftp.IFTPConstants;
import com.aptana.filesystem.ftp.Policy;
import com.aptana.filesystem.ftp.internal.FTPClientPool;
import com.aptana.filesystem.ftp.internal.FTPConnectionFileManager;
import com.aptana.filesystem.secureftp.IFTPSConnectionFileManager;
import com.aptana.filesystem.secureftp.IFTPSConstants;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPClientInterface;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.ssl.SSLFTPCertificateException;
import com.enterprisedt.net.ftp.ssl.SSLFTPClient;
import com.enterprisedt.net.ftp.ssl.SSLFTPClient.ConfigFlags;

/**
 * @author Max Stepanov
 *
 */
public class FTPSConnectionFileManager extends FTPConnectionFileManager implements IFTPSConnectionFileManager {
	
	private boolean validateCertificate;
	private boolean noSSLSessionResumption;
	private String securityMechanism;
	
	/* (non-Javadoc)
	 * @see com.aptana.filesystem.secureftp.FTPConnectionFileManager#init(java.lang.String, int, org.eclipse.core.runtime.IPath, java.lang.String, char[], boolean, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void init(String host, int port, IPath basePath, String login, char[] password, boolean passive, String transferType, String encoding, String timezone) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.aptana.filesystem.secureftp.IFTPSConnectionFileManager#init(java.lang.String, int, org.eclipse.core.runtime.IPath, java.lang.String, char[], boolean, boolean, java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
	 */
	public void init(String host, int port, IPath basePath, String login, char[] password, boolean explicit, boolean passive, String transferType, String encoding, String timezone, boolean validateCertificate, boolean noSSLSessionResumption) {
		Assert.isTrue(ftpClient == null, Messages.FTPSConnectionFileManager_ConnectionHasBeenInitiated);
		try {
			this.pool = new FTPClientPool(this);
			ftpClient = newClient();
			this.host = host;
			this.port = port;
			this.login = login;
			this.password = (password == null) ? EMPTY_PASSWORD : password;
			this.basePath = (basePath != null) ? basePath : Path.ROOT;
			this.authId = Policy.generateAuthId("FTPS", login, host, port); //$NON-NLS-1$
			this.transferType = transferType;
			this.timezone = (timezone != null && timezone.length() == 0) ? null : timezone;
			this.validateCertificate = validateCertificate;
			this.noSSLSessionResumption = noSSLSessionResumption;
			initFTPSClient((SSLFTPClient) ftpClient, explicit, passive, encoding, validateCertificate, noSSLSessionResumption);
		} catch (Exception e) {
			SecureFTPPlugin.log(new Status(IStatus.WARNING, SecureFTPPlugin.PLUGIN_ID, Messages.FTPSConnectionFileManager_ConnectionInitializationFailed, e));
			ftpClient = null;
		}
	}

	protected static void initFTPSClient(SSLFTPClient ftpsClient, boolean explicit, boolean passive, String encoding, boolean validateCertificate, boolean noSSLSessionResumption) throws IOException, FTPException {
		initFTPClient(ftpsClient, passive, encoding);
		ftpsClient.setImplicitFTPS(true);
		ftpsClient.setCustomValidator(new SSLHostValidator());
		ftpsClient.setValidateServer(validateCertificate);
		try {
			ftpsClient.getRootCertificateStore().importDefaultKeyStore();
		} catch (Exception e) {
			IdeLog.logWarning(SecureFTPPlugin.getDefault(), "Loading default root certificates failed.", e); //$NON-NLS-1$
		}
		ftpsClient.setImplicitFTPS(!explicit);
		ftpsClient.setConfigFlags(ConfigFlags.START_WITH_CLEAR_DATA_CHANNELS | (noSSLSessionResumption ? ConfigFlags.DISABLE_SESSION_RESUMPTION : 0));
	}

	/* (non-Javadoc)
	 * @see com.aptana.filesystem.secureftp.FTPConnectionFileManager#connect(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void connect(IProgressMonitor monitor) throws CoreException {
		Assert.isTrue(ftpClient != null, Messages.FTPSConnectionFileManager_ConnectionNotInitialized);
		SSLFTPClient ftpsClient = (SSLFTPClient) ftpClient;
		monitor = Policy.monitorFor(monitor);
		try {
			cwd = null;
			cleanup();

			ConnectionContext context = CoreIOPlugin.getConnectionContext(this);
			
			if (messageLogWriter == null) {
				if (context != null) {
					Object object = context.get(ConnectionContext.COMMAND_LOG);
					if (object instanceof PrintWriter) {
						messageLogWriter = (PrintWriter) object;
					} else if (object instanceof OutputStream) {
						messageLogWriter = new PrintWriter((OutputStream) object);
					}
				}
				if (messageLogWriter == null) {
					messageLogWriter = FTPPlugin.getDefault().getFTPLogWriter();
				}
				if (messageLogWriter != null) {
					messageLogWriter.println(MessageFormat.format("---------- FTPS {0} ----------", host)); //$NON-NLS-1$
					setMessageLogger(ftpClient, messageLogWriter);
				}
			} else {
				messageLogWriter.println(MessageFormat.format("---------- RECONNECTING - FTPS {0} ----------", host)); //$NON-NLS-1$
			}

			monitor.beginTask(Messages.FTPSConnectionFileManager_EstablishingConnection, IProgressMonitor.UNKNOWN);
			ftpClient.setRemoteHost(host);
			ftpClient.setRemotePort(port);
			while (true) {
				monitor.subTask(Messages.FTPSConnectionFileManager_Connecting);
				connectFTPSClient(ftpsClient);
				if (!ftpsClient.isImplicitFTPS()) {
					final String[] supportedMechanisms = new String[] {
							SSLFTPClient.AUTH_TLS,
							SSLFTPClient.AUTH_TLS_C,
							SSLFTPClient.AUTH_SSL								
					};
					boolean supportsPBSZ = true;
					boolean supportsPROT = true;
					try {
						String[] features = ftpClient.features();
						if (features != null && features.length > 0) {
							for (int i = 0; i < features.length; ++i) {
								features[i] = features[i].trim();
							}
							List<String> featuresList = Arrays.asList(features);
							for (int i = 0; i < supportedMechanisms.length; ++i) {
								if (securityMechanism == null && featuresList.contains("AUTH " + supportedMechanisms[i])) { //$NON-NLS-1$
									securityMechanism = supportedMechanisms[i];
									break;
								}
							}
							supportsPBSZ = featuresList.contains("PBSZ"); //$NON-NLS-1$
							supportsPROT = featuresList.contains("PROT"); //$NON-NLS-1$
						}

					} catch (Exception e) {
						e.getCause();
					}
					if (securityMechanism != null) {
						ftpsClient.auth(securityMechanism);
					} else {
						// server didn't indicate its supported auth mechanism, try them one-by-one
						for (String auth : supportedMechanisms) {
							try {
								ftpsClient.auth(auth);
								securityMechanism = auth;
								break;
							} catch (SSLFTPCertificateException e) {
								throw e;
							} catch (FTPException e) {
								e.getCause();
							}
						}
					}
					try {
						if (supportsPBSZ) {
							ftpsClient.pbsz(0);
						}
						if (supportsPROT) {
							ftpsClient.prot(SSLFTPClient.PROT_PRIVATE);
						}
					} catch (SSLFTPCertificateException e) {
						throw e;
					} catch (FTPException e) {
						if (supportsPROT) {
							ftpsClient.prot(SSLFTPClient.PROT_CLEAR);
						}
					}
				}
				if (password.length == 0 && !IFTPConstants.LOGIN_ANONYMOUS.equals(login) && (context == null || !context.getBoolean(ConnectionContext.NO_PASSWORD_PROMPT))) {
                    getOrPromptPassword(MessageFormat.format(Messages.FTPSConnectionFileManager_FTPSAuthentication, host),
                            Messages.FTPSConnectionFileManager_SpecifyPassword);
				}
				Policy.checkCanceled(monitor);
				monitor.subTask(Messages.FTPSConnectionFileManager_Authenticating);
				try {
					ftpClient.login(login, String.copyValueOf(password));
				} catch (FTPException e) {
					Policy.checkCanceled(monitor);
					if ("331".equals(ftpClient.getLastValidReply().getReplyCode())) { //$NON-NLS-1$
						if (context != null && context.getBoolean(ConnectionContext.NO_PASSWORD_PROMPT)) {
							throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID, MessageFormat.format(Messages.FTPSConnectionFileManager_FailedAuthenticate, e.getLocalizedMessage()), e));
						}
						promptPassword(MessageFormat.format(Messages.FTPSConnectionFileManager_FTPSAuthentication, host), Messages.FTPSConnectionFileManager_PasswordNotAccepted);
						safeQuit();
						continue;
					}
					throw e;
				}
				break;
			}
			
			Policy.checkCanceled(monitor);
			if (ftpsClient.isImplicitFTPS()) {
				ftpsClient.auth(SSLFTPClient.PROT_PRIVATE);
			}
			Policy.checkCanceled(monitor);
			changeCurrentDir(basePath);

			ftpClient.setType(IFTPConstants.TRANSFER_TYPE_ASCII.equals(transferType)
					? FTPTransferType.ASCII : FTPTransferType.BINARY);

			if ((hasServerInfo || (context != null && context.getBoolean(ConnectionContext.QUICK_CONNECT)))
					&& !(context != null && context.getBoolean(ConnectionContext.DETECT_TIMEZONE))) {
				return;
			}
			getherServerInfo(context, monitor);
			
		} catch (OperationCanceledException e) {
			safeQuit();
			throw e;
		} catch (CoreException e) {
			safeQuit();
			throw e;
		} catch (UnknownHostException e) {
			safeQuit();
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID, Messages.FTPSConnectionFileManager_HostNameNotFound+e.getLocalizedMessage(), e));
		} catch (SSLFTPCertificateException e) {
			safeQuit();
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID, Messages.FTPSConnectionFileManager_ServerSertificateError+e.getLocalizedMessage(), e));
		} catch (FileNotFoundException e) {
			safeQuit();
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID, Messages.FTPSConnectionFileManager_RemoteFolderNotFound+e.getLocalizedMessage(), e));			
		} catch (Exception e) {
			safeQuit();
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID, Messages.FTPSConnectionFileManager_FailedEstablishConnection+e.getLocalizedMessage(), e));
		} finally {
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.internal.FTPConnectionFileManager#newClient()
	 */
	@Override
	public FTPClient newClient() {
		try {
			return new SSLFTPClient();
		} catch (FTPException e) {
			SecureFTPPlugin.log(new Status(IStatus.ERROR, SecureFTPPlugin.PLUGIN_ID, "", e)); //$NON-NLS-1$
		}
		return null;
	}

	private static void connectFTPSClient(FTPClient ftpClient) throws IOException, FTPException {
		PerformanceStats stats = PerformanceStats.getStats("com.aptana.filesystem.ftp/perf/connect", FTPSConnectionFileManager.class.getName()); //$NON-NLS-1$
		stats.startRun(ftpClient.getRemoteHost());
		try {
			ftpClient.connect();
		} finally {
			stats.endRun();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.filesystem.secureftp.FTPConnectionFileManager#initAndAuthFTPClient(com.enterprisedt.net.ftp.FTPClient, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initAndAuthFTPClient(FTPClientInterface newFtpClient, IProgressMonitor monitor) throws IOException, FTPException {
		if (newFtpClient.connected()) {
			return;
		}
		SSLFTPClient newFtpsClient = (SSLFTPClient) newFtpClient;
		initFTPSClient(newFtpsClient, !((SSLFTPClient) ftpClient).isImplicitFTPS(), FTPConnectMode.PASV.equals(ftpClient.getConnectMode()), ftpClient.getControlEncoding(), validateCertificate, noSSLSessionResumption);
		newFtpClient.setRemoteHost(host);
		newFtpClient.setRemotePort(port);
		Policy.checkCanceled(monitor);
		connectFTPSClient(newFtpsClient);
		monitor.worked(1);
		Policy.checkCanceled(monitor);
		if (!newFtpsClient.isImplicitFTPS()) {
			newFtpsClient.auth(securityMechanism);
		}
		try {
			if (serverSupportsFeature("PBSZ")) { //$NON-NLS-1$
				newFtpsClient.pbsz(0);
			}
			if (serverSupportsFeature("PROT")) { //$NON-NLS-1$
				newFtpsClient.prot(SSLFTPClient.PROT_PRIVATE);
			}
		} catch (FTPException e) {
			if (serverSupportsFeature("PROT")) { //$NON-NLS-1$
				newFtpsClient.prot(SSLFTPClient.PROT_CLEAR);
			}
		}
		newFtpsClient.login(login, String.copyValueOf(password));
		monitor.worked(1);
	}

	/* (non-Javadoc)
	 * @see com.aptana.filesystem.secureftp.FTPConnectionFileManager#getRootCanonicalURI()
	 */
	@Override
	protected URI getRootCanonicalURI() {
		try {
			return new URI("ftps", login, host, (port != IFTPSConstants.FTP_PORT_DEFAULT && port != IFTPSConstants.FTPS_IMPLICIT_PORT) ? port : -1, Path.ROOT.toPortableString(), null, null); //$NON-NLS-1$
		} catch (URISyntaxException e) {
			return null;
		}
	}
}
