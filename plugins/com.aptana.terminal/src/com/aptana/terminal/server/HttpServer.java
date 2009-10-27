package com.aptana.terminal.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Kevin Lindsey
 */
public class HttpServer extends Thread
{
	static private HttpServer instance;

	protected int port;
	protected ServerSocket serverSocket;
	protected Thread runningThread;
	protected ExecutorService threadPool;
	protected boolean isRunning;
	protected Map<String, ProcessWrapper> processById;

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static synchronized HttpServer getInstance()
	{
		if (instance == null)
		{
			// TODO: add logic to try a range of ports, in case
			// a port is already being used
			instance = new HttpServer(8181);
		}

		return instance;
	}

	/**
	 * HttpServer
	 * 
	 * @param s
	 */
	public HttpServer(int port)
	{
		this.port = port;
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
		if (this.processById.containsKey(id) == false)
		{
			ProcessWrapper wrapper = new ProcessWrapper();
			
			wrapper.start();
			
			this.processById.put(id, wrapper);
		}
		else
		{
			throw new RuntimeException(Messages.HttpServer_Process_ID_Already_In_Use + id);
		}
	}

	/**
	 * getPort
	 * 
	 * @return
	 */
	public int getPort()
	{
		return this.port;
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

	/**
	 * openServerSocket
	 */
	private void openServerSocket()
	{
		try
		{
			this.serverSocket = new ServerSocket(this.port);
		}
		catch (IOException e)
		{
			throw new RuntimeException(Messages.HttpServer_Unable_To_Open_Port + this.port, e);
		}
	}
}
