package com.aptana.terminal.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.Platform;

/**
 * @author Kevin Lindsey
 */
public class HttpServer extends Thread
{
	private static final int DEFAULT_PORT = 8181;
	private static final String LOCALHOST = "127.0.0.1"; //$NON-NLS-1$
	private static HttpServer instance;

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static synchronized HttpServer getInstance()
	{
		if (instance == null)
		{
			instance = new HttpServer();
		}
		
		return instance;
	}
	protected int port;
	protected ServerSocket serverSocket;
	protected Thread runningThread;
	protected ExecutorService threadPool;
	protected boolean isRunning;

	protected Map<String, ProcessWrapper> processById;

	/**
	 * HttpServer
	 */
	public HttpServer()
	{
		this.threadPool = Executors.newFixedThreadPool(5);
		this.isRunning = true;
		this.processById = new HashMap<String, ProcessWrapper>();

		this.start();
	}

	/**
	 * createProcess
	 * 
	 * @param id
	 */
	public void createProcess(String id)
	{
		this.createProcess(id, null);
	}
	
	/**
	 * createProcess
	 * 
	 * @param id
	 * @param startingDirectory
	 */
	public void createProcess(String id, String startingDirectory)
	{
		if (this.processById.containsKey(id) == false)
		{
			ProcessWrapper wrapper = new ProcessWrapper(startingDirectory);
			
			wrapper.start();
			
			this.processById.put(id, wrapper);
		}
		else
		{
			throw new RuntimeException(Messages.HttpServer_Process_ID_Already_In_Use + id);
		}
	}

	/**
	 * getHost
	 * 
	 * @return
	 */
	public String getHost()
	{
		String result = LOCALHOST;
		
		if (this.serverSocket != null && Platform.OS_WIN32.equals(Platform.getOS()) == false)
		{
			result = this.serverSocket.getInetAddress().getHostAddress();
		}
		
		return result;
	}

	/**
	 * getPort
	 * 
	 * @return
	 */
	public int getPort()
	{
		int result = DEFAULT_PORT;
		
		if (this.serverSocket != null)
		{
			result = this.serverSocket.getLocalPort();
		}
		
		return result;
	}

	/**
	 * getProcess
	 * 
	 * @param id
	 * @return
	 */
	public ProcessWrapper getProcess(String id)
	{
		return this.processById.get(id);
	}

	/**
	 * openServerSocket
	 */
	private void openServerSocket()
	{
		try
		{
			this.serverSocket = new ServerSocket(0);
			this.serverSocket.setReuseAddress(true);
			
			// emit server port which is useful for debugging purposes
			System.out.println(Messages.Terminal_View_Server_Running_On_Port0 + this.getPort());
		}
		catch (IOException e)
		{
			throw new RuntimeException(Messages.HttpServer_Unable_To_Open_Port, e);
		}
	}

	/**
	 * removeProcess
	 * 
	 * @param id
	 */
	public void removeProcess(String id)
	{
		if (this.processById.containsKey(id))
		{
			ProcessWrapper wrapper = this.processById.get(id);
			
			wrapper.stop();
			
			this.processById.remove(id);
		}
		else
		{
			throw new RuntimeException(Messages.HttpServer_Process_ID_Does_Not_Exist + id);
		}
	}

	/**
	 * run
	 */
	public void run()
	{
		synchronized (this)
		{
			this.runningThread = Thread.currentThread();
		}

		this.openServerSocket();

		while (this.isRunning)
		{
			Socket clientSocket = null;

			try
			{
				clientSocket = this.serverSocket.accept();

				this.threadPool.execute(new HttpWorker(this, clientSocket));
			}
			catch (IOException e)
			{
				throw new RuntimeException(Messages.HttpServer_Client_Accept_Error, e);
			}
		}
	}
}
