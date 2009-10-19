package com.aptana.shell.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.shell.Activator;

public class HttpWorker implements Runnable
{
	protected Socket clientSocket;
	
	/**
	 * HttpWorker
	 * 
	 * @param clientSocket
	 */
	public HttpWorker(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
	}
	
	/**
	 * emitFile
	 * 
	 * @param output
	 * @param p
	 */
	private void emitFile(DataOutputStream output, String p)
	{
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(p), null);
		
		try
		{
			URL fileURL = FileLocator.toFileURL(url);
			File file = new File(fileURL.toURI());
			
			if (file.exists() && file.canRead())
			{
				int length = (int) file.length();
				byte[] bytes = new byte[length];
				
				new FileInputStream(file).read(bytes);
				
				output.writeBytes("HTTP/1.0 200 OK\nContent-Length:" + length + "\n\n");
				output.write(bytes, 0, length);
			}
			else
			{
				output.writeBytes("HTTP/1.0 404 ERROR\n\n");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * run
	 */
	@Override
	public void run()
	{
		try
		{
			BufferedReader input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			DataOutputStream output = new DataOutputStream(this.clientSocket.getOutputStream());
			
			try
			{
				String s;
				
				while ((s = input.readLine()).length() > 0)
				{
					if (s.startsWith("GET "))
					{
						this.processGet(s, input, output);
					}
					else if (s.startsWith("POST "))
					{
						this.processPost(s, input, output);
					}
				}
			}
			catch (Exception e)
			{
				output.writeBytes("HTTP/1.0 404 ERROR\n\n");
			}
			
			output.close();
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	 * processGet
	 * 
	 * @param get
	 * @param input
	 * @param output
	 */
	private void processGet(String get, BufferedReader input, DataOutputStream output)
	{
		String p = (get.split(" "))[1];
		
		if ("/stream".equals(p))
		{
			processGetStream(output);
		}
		else
		{
			p = ("." + (p.endsWith("/") ? p + "index.html" : p)).replace('/', File.separatorChar);
			
			emitFile(output, p);
		}
	}
	
	/**
	 * processStream
	 * 
	 * @param output
	 */
	private void processGetStream(DataOutputStream output)
	{
		String text = null;
		
		// do stream thing
//		synchronized (this._buffer)
//		{
//			if (this._buffer.length() > 0)
//			{
//				text = this._buffer.toString();
//				
//				this._buffer.setLength(0);
//			}
//		}
		
		if (text != null)
		{
			try
			{
				byte[] bytes = text.getBytes();
				int length = bytes.length;
				
				output.writeBytes("HTTP/1.0 200 OK\nContent-Length:" + length + "\n\n");
				output.write(bytes, 0, length);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * processPost
	 * 
	 * @param post
	 * @param input
	 * @param output
	 */
	private void processPost(String post, BufferedReader input, DataOutputStream output)
	{
		String p = (post.split(" "))[1];
		
		if ("/stream".equals(p))
		{
			processPostStream(input, output);
		}
		else
		{
			System.out.println("Unrecognized POST URL: " + p);
		}
	}
	
	/**
	 * processPostStream
	 * 
	 * @param input
	 * @param output
	 */
	private void processPostStream(BufferedReader input, DataOutputStream output)
	{
		try
		{
			do
			{
				String line = input.readLine();
				
				if (line.indexOf("Content-Length:") != -1)
				{
					String contentLengthString = line.split(" ")[1];
					int contentLength = Integer.parseInt(contentLengthString);
					
					while (true)
					{
						line = input.readLine();
						
						if (line.length() == 0)
						{
							break;
						}
					}
					
					char[] chars = new char[contentLength];
					input.read(chars);
					
//					synchronized (this._buffer)
//					{
//						this._buffer.append(chars);
//					}
					
					output.writeBytes("HTTP/1.0 200 OK\nContent-Length: 0\n\n");
				}
			}
			while (input.ready());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
