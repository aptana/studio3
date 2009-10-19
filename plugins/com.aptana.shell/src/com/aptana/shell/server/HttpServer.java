package com.aptana.shell.server;

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
	protected Map<String,StringBuffer> processById;
	
	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static synchronized HttpServer getInstance()
	{
		if (instance == null)
		{
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
		this.processById = new HashMap<String,StringBuffer>();
		
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
			this.processById.put(id, new StringBuffer());
		}
		else
		{
			throw new RuntimeException("Tried to create a process for an id that already exists: " + id);
		}
	}
	
	/**
	 * getProcess
	 * 
	 * @param id
	 * @return
	 */
	public StringBuffer getProcess(String id)
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
			this.processById.remove(id);
		}
		else
		{
			throw new RuntimeException("Tried to remove a process for an id that does not exist: " + id);
		}
	}
	
	/**
	 * run
	 */
	public void run()
	{
		synchronized(this)
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
			}
			catch (IOException e)
			{
				throw new RuntimeException("Error accepting clien connection", e);
			}
			
			this.threadPool.execute(new HttpWorker(this, clientSocket));
		}
	}
	
	private void openServerSocket()
	{
		try
		{
			this.serverSocket = new ServerSocket(this.port);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Unable to open port " + this.port, e);
		}
	}
}
