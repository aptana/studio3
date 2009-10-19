package com.aptana.shell.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
	
//	private StringBuffer _buffer;
	
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
		this.threadPool = Executors.newFixedThreadPool(10);
		this.isRunning = true;
		
//		this._buffer = new StringBuffer();
		
		this.start();
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
			
			this.threadPool.execute(new HttpWorker(clientSocket));
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
