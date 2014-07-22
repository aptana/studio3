/*******************************************************************************
 * Copyright (c) 2006, 2009 Cloudsmith Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 * 	Cloudsmith Inc - initial API and implementation
 * 	IBM Corporation - ongoing development
 *  Appcelerator - deadlock fix 2012
 ******************************************************************************/
package com.aptana.core.epl.downloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.filetransfer.FileTransferJob;
import org.eclipse.ecf.filetransfer.IFileRangeSpecification;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IIncomingFileTransfer;
import org.eclipse.ecf.filetransfer.IRetrieveFileTransferContainerAdapter;
import org.eclipse.ecf.filetransfer.IncomingFileTransferException;
import org.eclipse.ecf.filetransfer.events.IFileTransferConnectStartEvent;
import org.eclipse.ecf.filetransfer.events.IFileTransferEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDataEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDoneEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent;
import org.eclipse.ecf.filetransfer.identity.FileCreateException;
import org.eclipse.ecf.filetransfer.identity.FileIDFactory;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransferFactory;
import org.eclipse.ecf.provider.filetransfer.util.ProxySetupHelper;
import org.eclipse.osgi.util.NLS;

import com.aptana.core.epl.CoreEPLPlugin;

/**
 * FileReader is an ECF FileTransferJob implementation.
 */
public class FileReader extends FileTransferJob implements IFileTransferListener
{
	private static IFileReaderProbe testProbe;
	private boolean closeStreamWhenFinished = false;
	private Exception exception;
	private FileInfo fileInfo;
	private long lastProgressCount;
	private long lastStatsCount;
	protected IProgressMonitor theMonitor;
	private OutputStream theOutputStream;
	private ProgressStatistics statistics;
	private final int connectionRetryCount;
	private final long connectionRetryDelay;
	private final IConnectContext connectContext;
	private URI requestUri;
	protected IFileTransferConnectStartEvent connectEvent;
	private Job cancelJob;
	private boolean monitorStarted;

	/**
	 * Create a new FileReader that will retry failed connection attempts and sleep some amount of time between each
	 * attempt.
	 */
	public FileReader(IConnectContext aConnectContext)
	{
		super(Messages.FileReader_fileTrasportReader); // job label

		// Hide this job.
		setSystem(true);
		setUser(false);
		connectionRetryCount = 1;
		connectionRetryDelay = 200L;
		connectContext = aConnectContext;
	}

	public FileInfo getLastFileInfo()
	{
		return fileInfo;
	}

	/**
	 * A job to handle cancellation when trying to establish a socket connection. At this point we don't have a transfer
	 * job running yet, so we need a separate job to monitor for cancellation.
	 */
	protected class CancelHandler extends Job
	{
		private boolean done = false;

		protected CancelHandler()
		{
			super(Messages.FileReader_cancelHandler);
			setSystem(true);
		}

		public IStatus run(IProgressMonitor jobMonitor)
		{
			while (!done && !jobMonitor.isCanceled())
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					return Status.CANCEL_STATUS;
				}
				if (theMonitor != null && theMonitor.isCanceled())
				{
					return Status.CANCEL_STATUS;
				}
			}
			return Status.OK_STATUS;
		}

		protected void canceling()
		{
			// wake up from sleep in run method
			Thread t = getThread();
			if (t != null)
				t.interrupt();
		}

	}

	public synchronized void handleTransferEvent(IFileTransferEvent event)
	{
		if (event instanceof IFileTransferConnectStartEvent)
		{
			// keep the connect event to be able to cancel the transfer
			connectEvent = (IFileTransferConnectStartEvent) event;
			cancelJob = new CancelHandler();
			// schedule with a delay to avoid the overhead of an extra job on a fast connection
			cancelJob.schedule(500);
		}
		else if (event instanceof IIncomingFileTransferReceiveStartEvent)
		{
			// we no longer need the cancel handler because we are about to fork the transfer job
			if (cancelJob != null)
				cancelJob.cancel();
			IIncomingFileTransfer source = ((IIncomingFileTransferEvent) event).getSource();
			try
			{
				FileInfo fi = new FileInfo();
				Date lastModified = source.getRemoteLastModified();
				if (lastModified != null)
					fi.setLastModified(lastModified.getTime());
				fi.setName(source.getRemoteFileName());
				fi.setSize(source.getFileLength());
				fileInfo = fi;

				((IIncomingFileTransferReceiveStartEvent) event).receive(theOutputStream, this);
			}
			catch (IOException e)
			{
				exception = e;
				return;
			}
			long fileLength = source.getFileLength();
			ProgressStatistics stats = new ProgressStatistics(requestUri, source.getRemoteFileName(), fileLength);
			setStatistics(stats);

			if (theMonitor != null)
			{
				theMonitor.beginTask(null, 1000);
				monitorStarted = true;
				theMonitor.subTask(stats.report());
				lastStatsCount = 0;
				lastProgressCount = 0;
			}
			onStart(source);
		}
		else if (event instanceof IIncomingFileTransferReceiveDataEvent)
		{
			IIncomingFileTransfer source = ((IIncomingFileTransferEvent) event).getSource();
			if (theMonitor != null)
			{
				if (theMonitor.isCanceled())
				{
					completeTransfer(event);
					return;
				}

				long br = source.getBytesReceived();
				long count = br - lastStatsCount;
				lastStatsCount = br;
				ProgressStatistics stats = getStatistics();
				if (stats != null)
				{
					stats.increase(count);
					fileInfo.setAverageSpeed(stats.getAverageSpeed());
					if (stats.shouldReport())
					{
						count = br - lastProgressCount;
						lastProgressCount = br;
						theMonitor.subTask(stats.report());
						theMonitor.worked((int) (1000 * count / stats.getTotal()));
					}
				}
			}
			onData(source);
		}
		else if (event instanceof IIncomingFileTransferReceiveDoneEvent)
		{
			completeTransfer(event);
		}
	}

	protected void completeTransfer(IFileTransferEvent event)
	{
		if (closeStreamWhenFinished)
		{
			hardClose(theOutputStream);
		}

		if (exception == null && event instanceof IIncomingFileTransferReceiveDoneEvent)
		{
			exception = ((IIncomingFileTransferReceiveDoneEvent) event).getException();
		}

		onDone(((IIncomingFileTransferEvent) event).getSource());
	}

	public InputStream read(URI url, final IProgressMonitor monitor) throws CoreException, IOException
	{
		final PipedInputStream input = new PipedInputStream();
		PipedOutputStream output = new PipedOutputStream(input);
		sendRetrieveRequest(url, output, null, true, monitor);

		return new InputStream()
		{
			public int available() throws IOException
			{
				checkException();
				return input.available();
			}

			public void close() throws IOException
			{
				hardClose(input);
				checkException();
			}

			public void mark(int readlimit)
			{
				input.mark(readlimit);
			}

			public boolean markSupported()
			{
				return input.markSupported();
			}

			public int read() throws IOException
			{
				checkException();
				return input.read();
			}

			public int read(byte b[]) throws IOException
			{
				checkException();
				return input.read(b);
			}

			public int read(byte b[], int off, int len) throws IOException
			{
				checkException();
				return input.read(b, off, len);
			}

			public void reset() throws IOException
			{
				checkException();
				input.reset();
			}

			public long skip(long n) throws IOException
			{
				checkException();
				return input.skip(n);
			}

			private void checkException() throws IOException
			{
				if (getException() == null)
					return;

				IOException e;
				Throwable t = unwind(getException());
				if (t instanceof IOException)
					e = (IOException) t;
				else
				{
					e = new IOException(t.getMessage());
					e.initCause(t);
				}
				throw e;
			}

			@SuppressWarnings({ "rawtypes" })
			private Throwable unwind(Throwable t)
			{
				for (;;)
				{
					Class tc = t.getClass();

					// We don't use instanceof operator since we want
					// the explicit class, not subclasses.
					//
					if (tc != RuntimeException.class && tc != InvocationTargetException.class
							&& tc != IOException.class)
						break;

					Throwable cause = t.getCause();
					if (cause == null)
						break;

					String msg = t.getMessage();
					if (msg != null && !msg.equals(cause.toString()))
						break;

					t = cause;
				}
				return t;
			}
		};
	}

	public void readInto(URI uri, OutputStream anOutputStream, IProgressMonitor monitor) //
			throws CoreException, FileNotFoundException, ProtocolException
	{
		readInto(uri, anOutputStream, -1, monitor);
	}

	public boolean belongsTo(Object family)
	{
		return family == this;
	}

	/**
	 * Read the content into the given output stream.
	 * 
	 * @param uri
	 * @param anOutputStream
	 * @param startPos
	 * @param monitor
	 *            - A progress monitor. It's up to the caller to call done on this one.
	 * @throws CoreException
	 * @throws FileNotFoundException
	 * @throws ProtocolException
	 */
	public void readInto(URI uri, OutputStream anOutputStream, long startPos, IProgressMonitor monitor) //
			throws CoreException, FileNotFoundException, ProtocolException
	{
		if (monitor == null)
		{
			monitor = new NullProgressMonitor();
		}
		try
		{
			sendRetrieveRequest(uri, anOutputStream, (startPos != -1 ? new DownloadRange(startPos) : null), true,
					monitor);
			getTheJobManager().join(this, new SubProgressMonitor(monitor, 0));
			if (monitor.isCanceled() && connectEvent != null)
			{
				connectEvent.cancel();
			}
			// check and throw exception if received in callback
			checkException(uri, connectionRetryCount);
		}
		catch (InterruptedException e)
		{
			monitor.setCanceled(true);
			throw new OperationCanceledException();
		}
		finally
		{
			// kill the cancelJob, if there is one
			if (cancelJob != null)
			{
				cancelJob.cancel();
				cancelJob = null;
			}
			// If monitor was never started, make sure it is balanced
			if (!monitorStarted)
			{
				monitor.beginTask(null, 1);
			}
			monitorStarted = false;
			// monitor.done();
		}
	}

	protected IJobManager getTheJobManager()
	{
		return getJobManager();
	}

	protected synchronized void sendRetrieveRequest(URI uri, OutputStream outputStream, DownloadRange range,
			boolean closeStreamOnFinish, //
			IProgressMonitor monitor) throws CoreException, FileNotFoundException, ProtocolException
	{

		IRetrieveFileTransferFactory factory = getRetrieveFileTransferFactory();
		if (factory == null)
		{
			throw new CoreException(new Status(IStatus.ERROR, CoreEPLPlugin.PLUGIN_ID,
					Messages.FileReader_initializationError));
		}
		IRetrieveFileTransferContainerAdapter adapter = factory.newInstance();

		adapter.setConnectContextForAuthentication(connectContext);

		// Set the proxy settings for download if Studio is configured with proxy.
		Proxy proxy = getProxy(uri);
		adapter.setProxy(proxy);

		this.exception = null;
		this.closeStreamWhenFinished = closeStreamOnFinish;
		this.fileInfo = null;
		this.statistics = null;
		this.lastProgressCount = 0L;
		this.lastStatsCount = 0L;
		this.theMonitor = monitor;
		this.monitorStarted = false;
		this.theOutputStream = outputStream;
		this.requestUri = uri;

		for (int retryCount = 0;; retryCount++)
		{
			if (monitor != null && monitor.isCanceled())
			{
				throw new OperationCanceledException();
			}

			try
			{
				IFileID fileID = getFileIDFactory().createFileID(adapter.getRetrieveNamespace(), uri.toString());
				if (range != null)
				{
					adapter.sendRetrieveRequest(fileID, range, this, null);
				}
				else
				{
					adapter.sendRetrieveRequest(fileID, this, null);
				}
			}
			catch (IncomingFileTransferException e)
			{
				exception = e;
			}
			catch (FileCreateException e)
			{
				exception = e;
			}
			catch (Throwable t)
			{
				if (exception != null)
				{
					exception.printStackTrace();
				}
			}
			if (checkException(uri, retryCount))
			{
				break;
			}
		}
	}

	protected Proxy getProxy(URI uri)
	{
		return ProxySetupHelper.getProxy(uri.toASCIIString());
	}

	protected FileIDFactory getFileIDFactory()
	{
		return FileIDFactory.getDefault();
	}

	protected IRetrieveFileTransferFactory getRetrieveFileTransferFactory()
	{
		return CoreEPLPlugin.getDefault().getRetrieveFileTransferFactory();
	}

	/**
	 * Utility method to check exception condition and determine if retry should be done. If there was an exception it
	 * is translated into one of the specified exceptions and thrown.
	 * 
	 * @param uri
	 *            the URI being read - used for logging purposes
	 * @param attemptCounter
	 *            - the current attempt number (start with 0)
	 * @return true if the exception is an IOException and attemptCounter < connectionRetryCount, false otherwise
	 * @throws CoreException
	 * @throws FileNotFoundException
	 * @throws AuthenticationFailedException
	 */
	private boolean checkException(URI uri, int attemptCounter) throws CoreException, FileNotFoundException,
			ProtocolException
	{
		// note that 'exception' could have been captured in a callback
		if (exception != null)
		{
			// check if HTTP client needs to be changed
			RepositoryStatusHelper.checkJREHttpClientRequired(exception);

			// if this is an 'authentication failure' - it is not meaningful to continue
			RepositoryStatusHelper.checkPermissionDenied(exception);

			// if this is a 'file not found' - it is not meaningful to continue
			RepositoryStatusHelper.checkFileNotFound(exception, uri);

			Throwable t = RepositoryStatusHelper.unwind(exception);
			if (t instanceof CoreException)
				throw RepositoryStatusHelper.unwindCoreException((CoreException) t);

			// not meaningful to try 'timeout again' - if a server is that busy, we
			// need to wait for quite some time before retrying- it is not likely it is
			// just a temporary network thing.
			if (t instanceof SocketTimeoutException)
				throw RepositoryStatusHelper.wrap(t);

			if (t instanceof IOException && attemptCounter < connectionRetryCount)
			{
				// TODO: Retry only certain exceptions or filter out
				// some exceptions not worth retrying
				//
				exception = null;
				try
				{
					CoreEPLPlugin.log(new Status(IStatus.WARNING, CoreEPLPlugin.PLUGIN_ID, NLS.bind(
							Messages.FileReader_connectionRetryMeggage, new String[] { uri.toString(), t.getMessage(),
									String.valueOf(attemptCounter) }), t));

					Thread.sleep(connectionRetryDelay);
					return false;
				}
				catch (InterruptedException e)
				{
					return false;
				}
			}
			throw RepositoryStatusHelper.wrap(exception);
		}
		return true;
	}

	protected Exception getException()
	{
		return exception;
	}

	/**
	 * Closes input and output streams
	 * 
	 * @param aStream
	 */
	public static void hardClose(Object aStream)
	{
		if (aStream != null)
		{
			try
			{
				if (aStream instanceof OutputStream)
				{
					OutputStream stream = (OutputStream) aStream;
					stream.flush();
					stream.close();
				}
				else if (aStream instanceof InputStream)
				{
					((InputStream) aStream).close();
				}
			}
			catch (IOException e)
			{ /* ignore */
			}
		}
	}

	private static class DownloadRange implements IFileRangeSpecification
	{

		private long startPosition;

		public DownloadRange(long startPos)
		{
			startPosition = startPos;
		}

		public long getEndPosition()
		{
			return -1;
		}

		public long getStartPosition()
		{
			return startPosition;
		}

	}

	private void onDone(IIncomingFileTransfer source)
	{
		if (testProbe != null)
			testProbe.onDone(this, source, theMonitor);
	}

	private void onStart(IIncomingFileTransfer source)
	{
		if (testProbe != null)
			testProbe.onStart(this, source, theMonitor);
	}

	private void onData(IIncomingFileTransfer source)
	{
		if (testProbe != null)
			testProbe.onData(this, source, theMonitor);
	}

	/**
	 * Sets a testing probe that can intercept events on the file reader for testing purposes. This method should only
	 * ever be called from automated test suites.
	 */
	public static void setTestProbe(IFileReaderProbe probe)
	{
		testProbe = probe;
	}

	/**
	 * Sets the progress statistics. This method is synchronized because the field is accessed from both the transfer
	 * thread and the thread initiating the transfer and we need to ensure field values are consistent across threads.
	 * 
	 * @param statistics
	 *            the statistics to set, or <code>null</code>
	 */
	private synchronized void setStatistics(ProgressStatistics statistics)
	{
		this.statistics = statistics;
	}

	/**
	 * Returns the progress statistics. This method is synchronized because the field is accessed from both the transfer
	 * thread and the thread initiating the transfer and we need to ensure field values are consistent across threads.
	 * 
	 * @return the statistics, or <code>null</code>
	 */
	private synchronized ProgressStatistics getStatistics()
	{
		return statistics;
	}

	/**
	 * An interface to allow automated tests to hook into file reader events
	 * 
	 * @see #setTestProbe
	 */
	public interface IFileReaderProbe
	{
		public void onStart(FileReader reader, IIncomingFileTransfer source, IProgressMonitor monitor);

		public void onData(FileReader reader, IIncomingFileTransfer source, IProgressMonitor monitor);

		public void onDone(FileReader reader, IIncomingFileTransfer source, IProgressMonitor monitor);
	}
}
