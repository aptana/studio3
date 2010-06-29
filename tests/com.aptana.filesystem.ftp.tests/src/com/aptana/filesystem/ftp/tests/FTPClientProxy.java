package com.aptana.filesystem.ftp.tests;

import java.io.BufferedInputStream;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import com.enterprisedt.net.ftp.DataChannelCallback;
import com.enterprisedt.net.ftp.DirectoryEmptyStrings;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPControlSocket;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.net.ftp.FTPFileFactory;
import com.enterprisedt.net.ftp.FTPMessageListener;
import com.enterprisedt.net.ftp.FTPProgressMonitor;
import com.enterprisedt.net.ftp.FTPProgressMonitorEx;
import com.enterprisedt.net.ftp.FTPReply;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.FileNotFoundStrings;
import com.enterprisedt.net.ftp.TransferCompleteStrings;
import com.enterprisedt.net.ftp.pro.ProFTPClient;
import com.enterprisedt.util.proxy.ProxySettings;

public class FTPClientProxy extends ProFTPClient
{
	protected boolean throwIOException;
	protected boolean throwFTPException; // Exception
	protected boolean throwOperationCanceledException; // RuntimeException
	protected boolean throwCoreException; // Exception
	protected boolean throwUnknownHostException; //IOException
	protected boolean throwFileNotFoundException; // IOException

	public void setIOException(boolean value) {
		throwIOException = value;
	}

	public boolean getIOException() {
		return throwIOException;
	}

	public void setFTPException(boolean value) {
		throwFTPException = value;
	}
	
	public boolean getFTPException() {
		return throwFTPException;
	}

	public void setOperationCanceledException(boolean value) {
		throwOperationCanceledException = value;
	}
	
	public boolean getOperationCanceledException() {
		return throwOperationCanceledException;
	}

	public void setCoreException(boolean value) {
		throwCoreException = value;
	}
	
	public boolean getCoreException() {
		return throwCoreException;
	}

	public void setUnknownHostException(boolean value) {
		throwUnknownHostException = value;
	}
	
	public boolean getUnknownHostException() {
		return throwUnknownHostException;
	}

	public void setFileNotFoundException(boolean value) {
		throwFileNotFoundException = value;
	}
	
	public boolean getFileNotFoundException() {
		return throwFileNotFoundException;
	}

	@Override
	public void connect() throws IOException, FTPException
	{
		if(throwIOException) {
			throw new IOException();
		}

		if(throwFTPException) {
			throw new FTPException("Forced exception");
		}

		// TODO Auto-generated method stub
		super.connect();
	}

	@Override
	public void enableModeZCompression() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.enableModeZCompression();
	}

	@Override
	public int getCountBeforeSleep()
	{
		// TODO Auto-generated method stub
		return super.getCountBeforeSleep();
	}

	@Override
	protected InputStream getInputStream() throws IOException
	{
		// TODO Auto-generated method stub
		return super.getInputStream();
	}

	@Override
	public int getLocalCRC(byte[] arg0) throws IOException
	{
		// TODO Auto-generated method stub
		return super.getLocalCRC(arg0);
	}

	@Override
	public int getLocalCRC(String arg0) throws IOException
	{
		// TODO Auto-generated method stub
		return super.getLocalCRC(arg0);
	}

	@Override
	public int getMaxTransferRate()
	{
		// TODO Auto-generated method stub
		return super.getMaxTransferRate();
	}

	@Override
	protected OutputStream getOutputStream() throws IOException
	{
		// TODO Auto-generated method stub
		return super.getOutputStream();
	}

	@Override
	public ProxySettings getProxySettings()
	{
		// TODO Auto-generated method stub
		return super.getProxySettings();
	}

	@Override
	public String getRemoteCRC(String arg0) throws FTPException, IOException
	{
		// TODO Auto-generated method stub
		return super.getRemoteCRC(arg0);
	}

	@Override
	public int getSleepTime()
	{
		// TODO Auto-generated method stub
		return super.getSleepTime();
	}

	@Override
	protected void integrityCheckTransferCRC(byte[] arg0, String arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.integrityCheckTransferCRC(arg0, arg1);
	}

	@Override
	protected void integrityCheckTransferCRC(String arg0, String arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.integrityCheckTransferCRC(arg0, arg1);
	}

	@Override
	protected void integrityCheckTransferSize(long arg0, String arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.integrityCheckTransferSize(arg0, arg1);
	}

	@Override
	public boolean isModeZCompressionEnabled()
	{
		// TODO Auto-generated method stub
		return super.isModeZCompressionEnabled();
	}

	@Override
	public boolean isSleepEnabled()
	{
		// TODO Auto-generated method stub
		return super.isSleepEnabled();
	}

	@Override
	public boolean isTransferIntegrityCheck()
	{
		// TODO Auto-generated method stub
		return super.isTransferIntegrityCheck();
	}

	@Override
	public void mdelete(FileFilter arg0) throws IOException, FTPException, ParseException
	{
		// TODO Auto-generated method stub
		super.mdelete(arg0);
	}

	@Override
	public void mdelete(String arg0, FileFilter arg1, boolean arg2) throws IOException, FTPException, ParseException
	{
		// TODO Auto-generated method stub
		super.mdelete(arg0, arg1, arg2);
	}

	@Override
	public void mdelete(String arg0, String arg1, boolean arg2) throws IOException, FTPException, ParseException
	{
		// TODO Auto-generated method stub
		super.mdelete(arg0, arg1, arg2);
	}

	@Override
	public void mdelete(String arg0) throws IOException, FTPException, ParseException
	{
		// TODO Auto-generated method stub
		super.mdelete(arg0);
	}

	@Override
	public void mget(String arg0, FileFilter arg1) throws IOException, FTPException, ParseException
	{
		// TODO Auto-generated method stub
		super.mget(arg0, arg1);
	}

	@Override
	public void mget(String arg0, String arg1, FileFilter arg2, boolean arg3) throws FTPException, IOException,
			ParseException
	{
		// TODO Auto-generated method stub
		super.mget(arg0, arg1, arg2, arg3);
	}

	@Override
	public void mget(String arg0, String arg1, String arg2, boolean arg3) throws FTPException, IOException,
			ParseException
	{
		// TODO Auto-generated method stub
		super.mget(arg0, arg1, arg2, arg3);
	}

	@Override
	public void mget(String arg0, String arg1) throws IOException, FTPException, ParseException
	{
		// TODO Auto-generated method stub
		super.mget(arg0, arg1);
	}

	@Override
	public void mput(String arg0, FileFilter arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.mput(arg0, arg1);
	}

	@Override
	public void mput(String arg0, String arg1, FileFilter arg2, boolean arg3) throws FTPException, IOException
	{
		// TODO Auto-generated method stub
		super.mput(arg0, arg1, arg2, arg3);
	}

	@Override
	public void mput(String arg0, String arg1, String arg2, boolean arg3) throws FTPException, IOException
	{
		// TODO Auto-generated method stub
		super.mput(arg0, arg1, arg2, arg3);
	}

	@Override
	public void mput(String arg0, String arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.mput(arg0, arg1);
	}

	@Override
	protected void postTransferChecks(byte[] arg0, String arg1, FTPTransferType arg2, boolean arg3)
			throws FTPException, IOException
	{
		// TODO Auto-generated method stub
		super.postTransferChecks(arg0, arg1, arg2, arg3);
	}

	@Override
	protected void postTransferChecks(String arg0, String arg1, FTPTransferType arg2, boolean arg3)
			throws FTPException, IOException
	{
		// TODO Auto-generated method stub
		super.postTransferChecks(arg0, arg1, arg2, arg3);
	}

	@Override
	public void rmdir(String arg0, boolean arg1) throws IOException, FTPException, ParseException
	{
		// TODO Auto-generated method stub
		super.rmdir(arg0, arg1);
	}

	@Override
	public void setCountBeforeSleep(int arg0)
	{
		// TODO Auto-generated method stub
		super.setCountBeforeSleep(arg0);
	}

	@Override
	public void setMaxTransferRate(int arg0)
	{
		// TODO Auto-generated method stub
		super.setMaxTransferRate(arg0);
	}

	@Override
	public void setSleepEnabled(boolean arg0)
	{
		// TODO Auto-generated method stub
		super.setSleepEnabled(arg0);
	}

	@Override
	public void setSleepTime(int arg0)
	{
		// TODO Auto-generated method stub
		super.setSleepTime(arg0);
	}

	@Override
	public void setTransferIntegrityCheck(boolean arg0)
	{
		// TODO Auto-generated method stub
		super.setTransferIntegrityCheck(arg0);
	}

	@Override
	protected void abort() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.abort();
	}

	@Override
	public void account(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.account(arg0);
	}

	@Override
	public void cancelResume() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.cancelResume();
	}

	@Override
	public void cancelTransfer()
	{
		// TODO Auto-generated method stub
		super.cancelTransfer();
	}

	@Override
	public void cdup() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.cdup();
	}

	@Override
	public void chdir(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.chdir(arg0);
	}

	@Override
	protected void checkConnection(boolean arg0) throws FTPException
	{
		// TODO Auto-generated method stub
		super.checkConnection(arg0);
	}

	@Override
	protected FTPTransferType chooseTransferMode(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.chooseTransferMode(arg0);
	}

	@Override
	protected void closeDataSocket(InputStream arg0)
	{
		// TODO Auto-generated method stub
		super.closeDataSocket(arg0);
	}

	@Override
	protected void closeDataSocket(OutputStream arg0)
	{
		// TODO Auto-generated method stub
		super.closeDataSocket(arg0);
	}

	@Override
	public boolean connected()
	{
		// TODO Auto-generated method stub
		return super.connected();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void debugResponses(boolean arg0)
	{
		// TODO Auto-generated method stub
		super.debugResponses(arg0);
	}

	@Override
	public void delete(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.delete(arg0);
	}

	@Override
	public String[] dir() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.dir();
	}

	@Override
	public String[] dir(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.dir(arg0);
	}

	@Override
	public String[] dir(String arg0, boolean arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.dir(arg0, arg1);
	}

	@Override
	public FTPFile[] dirDetails(String arg0) throws IOException, FTPException, ParseException
	{
		// TODO Auto-generated method stub
		return super.dirDetails(arg0);
	}

	@Override
	public String executeCommand(String arg0) throws FTPException, IOException
	{
		// TODO Auto-generated method stub
		return super.executeCommand(arg0);
	}

	@Override
	public boolean exists(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.exists(arg0);
	}

	@Override
	public String[] features() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.features();
	}

	@Override
	public FTPFile fileDetails(String arg0) throws IOException, FTPException, ParseException
	{
		// TODO Auto-generated method stub
		return super.fileDetails(arg0);
	}

	@Override
	protected void forceResumeOff()
	{
		// TODO Auto-generated method stub
		super.forceResumeOff();
	}

	@Override
	public byte[] get(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.get(arg0);
	}

	@Override
	public void get(String arg0, String arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.get(arg0, arg1);
	}

	@Override
	public void get(OutputStream arg0, String arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.get(arg0, arg1);
	}

	@Override
	public int getActiveHighPort()
	{
		// TODO Auto-generated method stub
		return super.getActiveHighPort();
	}

	@Override
	public String getActiveIPAddress()
	{
		// TODO Auto-generated method stub
		return super.getActiveIPAddress();
	}

	@Override
	public int getActiveLowPort()
	{
		// TODO Auto-generated method stub
		return super.getActiveLowPort();
	}

	@Override
	public FTPConnectMode getConnectMode()
	{
		// TODO Auto-generated method stub
		return super.getConnectMode();
	}

	@Override
	public String getControlEncoding()
	{
		// TODO Auto-generated method stub
		return super.getControlEncoding();
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getControlPort()
	{
		// TODO Auto-generated method stub
		return super.getControlPort();
	}

	@Override
	public int getDataReceiveBufferSize()
	{
		// TODO Auto-generated method stub
		return super.getDataReceiveBufferSize();
	}

	@Override
	public int getDataSendBufferSize()
	{
		// TODO Auto-generated method stub
		return super.getDataSendBufferSize();
	}

	@Override
	public int getDeleteCount()
	{
		// TODO Auto-generated method stub
		return super.getDeleteCount();
	}

	@Override
	public boolean getDetectTransferMode()
	{
		// TODO Auto-generated method stub
		return super.getDetectTransferMode();
	}

	@Override
	public DirectoryEmptyStrings getDirectoryEmptyMessages()
	{
		// TODO Auto-generated method stub
		return super.getDirectoryEmptyMessages();
	}

	@Override
	public int getDownloadCount()
	{
		// TODO Auto-generated method stub
		return super.getDownloadCount();
	}

	@Override
	public FileNotFoundStrings getFileNotFoundMessages()
	{
		// TODO Auto-generated method stub
		return super.getFileNotFoundMessages();
	}

	@Override
	public String getId()
	{
		// TODO Auto-generated method stub
		return super.getId();
	}

	@Override
	public FTPReply getLastReply()
	{
		// TODO Auto-generated method stub
		return super.getLastReply();
	}

	@Override
	public FTPReply getLastValidReply()
	{
		// TODO Auto-generated method stub
		return super.getLastValidReply();
	}

	@Override
	public boolean getListenOnAllInterfaces()
	{
		// TODO Auto-generated method stub
		return super.getListenOnAllInterfaces();
	}

	@Override
	public FTPMessageListener getMessageListener()
	{
		// TODO Auto-generated method stub
		return super.getMessageListener();
	}

	@Override
	public long getMonitorInterval()
	{
		// TODO Auto-generated method stub
		return super.getMonitorInterval();
	}

	@Override
	public FTPProgressMonitor getProgressMonitor()
	{
		// TODO Auto-generated method stub
		return super.getProgressMonitor();
	}

	@Override
	public FTPProgressMonitorEx getProgressMonitorEx()
	{
		// TODO Auto-generated method stub
		return super.getProgressMonitorEx();
	}

	@Override
	public InetAddress getRemoteAddr()
	{
		// TODO Auto-generated method stub
		return super.getRemoteAddr();
	}

	@Override
	public String getRemoteHost()
	{
		// TODO Auto-generated method stub
		return super.getRemoteHost();
	}

	@Override
	public int getRemotePort()
	{
		// TODO Auto-generated method stub
		return super.getRemotePort();
	}

	@Override
	public int getRetryCount()
	{
		// TODO Auto-generated method stub
		return super.getRetryCount();
	}

	@Override
	public int getRetryDelay()
	{
		// TODO Auto-generated method stub
		return super.getRetryDelay();
	}

	@Override
	public int getServerWakeupInterval()
	{
		// TODO Auto-generated method stub
		return super.getServerWakeupInterval();
	}

	@Override
	public int getTimeout()
	{
		// TODO Auto-generated method stub
		return super.getTimeout();
	}

	@Override
	public int getTransferBufferSize()
	{
		// TODO Auto-generated method stub
		return super.getTransferBufferSize();
	}

	@Override
	public TransferCompleteStrings getTransferCompleteMessages()
	{
		// TODO Auto-generated method stub
		return super.getTransferCompleteMessages();
	}

	@Override
	public FTPTransferType getType()
	{
		// TODO Auto-generated method stub
		return super.getType();
	}

	@Override
	public int getUploadCount()
	{
		// TODO Auto-generated method stub
		return super.getUploadCount();
	}

	@Override
	public String help(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.help(arg0);
	}

	@Override
	protected void initGet(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.initGet(arg0);
	}

	@Override
	protected String initPut(String arg0, boolean arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.initPut(arg0, arg1);
	}

	@Override
	protected void initialize(FTPControlSocket arg0) throws IOException
	{
		// TODO Auto-generated method stub
		super.initialize(arg0);
	}

	@Override
	public boolean isAutoPassiveIPSubstitution()
	{
		// TODO Auto-generated method stub
		return super.isAutoPassiveIPSubstitution();
	}

	@Override
	public boolean isDeleteOnFailure()
	{
		// TODO Auto-generated method stub
		return super.isDeleteOnFailure();
	}

	@Override
	public boolean isStrictReturnCodes()
	{
		// TODO Auto-generated method stub
		return super.isStrictReturnCodes();
	}

	@Override
	public boolean isTransferCancelled()
	{
		// TODO Auto-generated method stub
		return super.isTransferCancelled();
	}

	@Override
	public void keepAlive() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.keepAlive();
	}

	@SuppressWarnings("deprecation")
	@Override
	public String list(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.list(arg0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String list(String arg0, boolean arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.list(arg0, arg1);
	}

	@Override
	public void login(String arg0, String arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.login(arg0, arg1);
	}

	@Override
	public void login(String arg0, String arg1, String arg2) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.login(arg0, arg1, arg2);
	}

	@Override
	public void mkdir(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.mkdir(arg0);
	}

	@Override
	public Date modtime(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.modtime(arg0);
	}

	@Override
	public void noOperation() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.noOperation();
	}

	@Override
	public void password(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.password(arg0);
	}

	@Override
	public String put(String arg0, String arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.put(arg0, arg1);
	}

	@Override
	public String put(InputStream arg0, String arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.put(arg0, arg1);
	}

	@Override
	public String put(byte[] arg0, String arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.put(arg0, arg1);
	}

	@Override
	public String put(InputStream arg0, String arg1, boolean arg2) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.put(arg0, arg1, arg2);
	}

	@Override
	public String put(String arg0, String arg1, boolean arg2) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.put(arg0, arg1, arg2);
	}

	@Override
	public String put(byte[] arg0, String arg1, boolean arg2) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.put(arg0, arg1, arg2);
	}

	@Override
	public String pwd() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.pwd();
	}

	@Override
	public void quit() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.quit();
	}

	@Override
	public void quitImmediately() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.quitImmediately();
	}

	@Override
	public String quote(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.quote(arg0);
	}

	@Override
	public String quote(String arg0, String[] arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.quote(arg0, arg1);
	}

	@Override
	protected int readChar(LineNumberReader arg0) throws IOException
	{
		// TODO Auto-generated method stub
		return super.readChar(arg0);
	}

	@Override
	public int readChunk(BufferedInputStream arg0, byte[] arg1, int arg2) throws IOException
	{
		// TODO Auto-generated method stub
		return super.readChunk(arg0, arg1, arg2);
	}

	@Override
	protected String readLine(LineNumberReader arg0) throws IOException
	{
		// TODO Auto-generated method stub
		return super.readLine(arg0);
	}

	@Override
	protected void reconnect(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.reconnect(arg0);
	}

	@Override
	public void rename(String arg0, String arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.rename(arg0, arg1);
	}

	@Override
	public void resetDeleteCount()
	{
		// TODO Auto-generated method stub
		super.resetDeleteCount();
	}

	@Override
	public void resetDownloadCount()
	{
		// TODO Auto-generated method stub
		super.resetDownloadCount();
	}

	@Override
	public void resetTransferMode(FTPTransferType arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.resetTransferMode(arg0);
	}

	@Override
	public void resetUploadCount()
	{
		// TODO Auto-generated method stub
		super.resetUploadCount();
	}

	@Override
	public void restart(long arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.restart(arg0);
	}

	@Override
	public void resume() throws FTPException
	{
		// TODO Auto-generated method stub
		super.resume();
	}

	@Override
	public void rmdir(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.rmdir(arg0);
	}

	@Override
	public FTPReply sendCommand(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.sendCommand(arg0);
	}

	@Override
	public void sendServerWakeup() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.sendServerWakeup();
	}

	@Override
	public void setActiveIPAddress(String arg0) throws FTPException
	{
		// TODO Auto-generated method stub
		super.setActiveIPAddress(arg0);
	}

	@Override
	public void setActivePortRange(int arg0, int arg1) throws FTPException
	{
		// TODO Auto-generated method stub
		super.setActivePortRange(arg0, arg1);
	}

	@Override
	public void setAutoPassiveIPSubstitution(boolean arg0)
	{
		// TODO Auto-generated method stub
		super.setAutoPassiveIPSubstitution(arg0);
	}

	@Override
	public void setConnectMode(FTPConnectMode arg0)
	{
		// TODO Auto-generated method stub
		super.setConnectMode(arg0);
	}

	@Override
	public void setControlEncoding(String arg0) throws FTPException
	{
		// TODO Auto-generated method stub
		super.setControlEncoding(arg0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setControlPort(int arg0) throws FTPException
	{
		// TODO Auto-generated method stub
		super.setControlPort(arg0);
	}

	@Override
	public void setDataChannelCallback(DataChannelCallback arg0)
	{
		// TODO Auto-generated method stub
		super.setDataChannelCallback(arg0);
	}

	@Override
	public void setDataReceiveBufferSize(int arg0)
	{
		// TODO Auto-generated method stub
		super.setDataReceiveBufferSize(arg0);
	}

	@Override
	public void setDataSendBufferSize(int arg0)
	{
		// TODO Auto-generated method stub
		super.setDataSendBufferSize(arg0);
	}

	@Override
	public void setDeleteOnFailure(boolean arg0)
	{
		// TODO Auto-generated method stub
		super.setDeleteOnFailure(arg0);
	}

	@Override
	public void setDetectTransferMode(boolean arg0)
	{
		// TODO Auto-generated method stub
		super.setDetectTransferMode(arg0);
	}

	@Override
	public void setDirectoryEmptyMessages(DirectoryEmptyStrings arg0)
	{
		// TODO Auto-generated method stub
		super.setDirectoryEmptyMessages(arg0);
	}

	@Override
	public void setFTPFileFactory(FTPFileFactory arg0)
	{
		// TODO Auto-generated method stub
		super.setFTPFileFactory(arg0);
	}

	@Override
	public void setFileNotFoundMessages(FileNotFoundStrings arg0)
	{
		// TODO Auto-generated method stub
		super.setFileNotFoundMessages(arg0);
	}

	@Override
	public void setForceUniqueNames(boolean arg0)
	{
		// TODO Auto-generated method stub
		super.setForceUniqueNames(arg0);
	}

	@Override
	public void setId(String arg0)
	{
		// TODO Auto-generated method stub
		super.setId(arg0);
	}

	@Override
	public void setListenOnAllInterfaces(boolean arg0)
	{
		// TODO Auto-generated method stub
		super.setListenOnAllInterfaces(arg0);
	}

	@Override
	public void setMessageListener(FTPMessageListener arg0)
	{
		// TODO Auto-generated method stub
		super.setMessageListener(arg0);
	}

	@Override
	public void setModTime(String arg0, Date arg1) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.setModTime(arg0, arg1);
	}

	@Override
	public void setMonitorInterval(long arg0)
	{
		// TODO Auto-generated method stub
		super.setMonitorInterval(arg0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setPORTIP(String arg0) throws FTPException
	{
		// TODO Auto-generated method stub
		super.setPORTIP(arg0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setParserLocale(Locale arg0)
	{
		// TODO Auto-generated method stub
		super.setParserLocale(arg0);
	}

	@Override
	public void setParserLocales(Locale[] arg0)
	{
		// TODO Auto-generated method stub
		super.setParserLocales(arg0);
	}

	@Override
	public void setProgressMonitor(FTPProgressMonitor arg0)
	{
		// TODO Auto-generated method stub
		super.setProgressMonitor(arg0);
	}

	@Override
	public void setProgressMonitor(FTPProgressMonitor arg0, long arg1)
	{
		// TODO Auto-generated method stub
		super.setProgressMonitor(arg0, arg1);
	}

	@Override
	public void setProgressMonitorEx(FTPProgressMonitorEx arg0)
	{
		// TODO Auto-generated method stub
		super.setProgressMonitorEx(arg0);
	}

	@Override
	public void setRemoteAddr(InetAddress arg0) throws FTPException
	{
		// TODO Auto-generated method stub
		super.setRemoteAddr(arg0);
	}

	@Override
	public void setRemoteHost(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.setRemoteHost(arg0);
	}

	@Override
	public void setRemotePort(int arg0) throws FTPException
	{
		// TODO Auto-generated method stub
		super.setRemotePort(arg0);
	}

	@Override
	public void setRetryCount(int arg0)
	{
		// TODO Auto-generated method stub
		super.setRetryCount(arg0);
	}

	@Override
	public void setRetryDelay(int arg0)
	{
		// TODO Auto-generated method stub
		super.setRetryDelay(arg0);
	}

	@Override
	public void setServerWakeupInterval(int arg0)
	{
		// TODO Auto-generated method stub
		super.setServerWakeupInterval(arg0);
	}

	@Override
	public void setStrictReturnCodes(boolean arg0)
	{
		// TODO Auto-generated method stub
		super.setStrictReturnCodes(arg0);
	}

	@Override
	public void setTimeout(int arg0) throws IOException
	{
		// TODO Auto-generated method stub
		super.setTimeout(arg0);
	}

	@Override
	public void setTransferBufferSize(int arg0)
	{
		// TODO Auto-generated method stub
		super.setTransferBufferSize(arg0);
	}

	@Override
	public void setTransferCompleteMessages(TransferCompleteStrings arg0)
	{
		// TODO Auto-generated method stub
		super.setTransferCompleteMessages(arg0);
	}

	@Override
	public void setType(FTPTransferType arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.setType(arg0);
	}

	@Override
	protected void setupDataSocket() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.setupDataSocket();
	}

	@Override
	public boolean site(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.site(arg0);
	}

	@Override
	public long size(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.size(arg0);
	}

	@Override
	public String stat() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.stat();
	}

	@Override
	public String system() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		return super.system();
	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return super.toString();
	}

	@Override
	public void user(String arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.user(arg0);
	}

	@Override
	public void validateReply(FTPReply arg0, String arg1) throws FTPException
	{
		// TODO Auto-generated method stub
		super.validateReply(arg0, arg1);
	}

	@Override
	public void validateReply(FTPReply arg0, String[] arg1) throws FTPException
	{
		// TODO Auto-generated method stub
		super.validateReply(arg0, arg1);
	}

	@Override
	public void validateTransfer() throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.validateTransfer();
	}

	@Override
	protected void validateTransferOnError(IOException arg0) throws IOException, FTPException
	{
		// TODO Auto-generated method stub
		super.validateTransferOnError(arg0);
	}

}
