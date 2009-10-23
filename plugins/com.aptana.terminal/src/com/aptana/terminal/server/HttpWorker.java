package com.aptana.terminal.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.Point;

import com.aptana.terminal.Activator;
import com.aptana.terminal.Utils;

public class HttpWorker implements Runnable
{
	private HttpServer _server;
	private Socket _clientSocket;
	
	/**
	 * HttpWorker
	 * 
	 * @param clientSocket
	 */
	public HttpWorker(HttpServer server, Socket clientSocket)
	{
		this._server = server;
		this._clientSocket = clientSocket;
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
	
//	/**
//	 * getQuery
//	 * 
//	 * @param URL
//	 * @return
//	 */
//	private Map<String,String> getQuery(String URL)
//	{
//		Map<String,String> result = new HashMap<String,String>();
//		int queryStart = URL.indexOf('?');
//		
//		if (queryStart != -1)
//		{
//			String query = URL.substring(queryStart + 1);
//			String[] keyPairs = query.split("&");
//			
//			for (String keyPair : keyPairs)
//			{
//				String[] parts = keyPair.split("=");
//				
//				result.put(parts[0], parts[1]);
//			}
//		}
//		
//		return result;
//	}
	
	private String getPostContent(BufferedReader input)
	{
		String result = null;
		
		try
		{
			while (input.ready())
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
					
					result = new String(chars);
				}
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
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
		
		if (p.startsWith("/stream?id="))
		{
			String id = p.substring(p.indexOf("=") + 1);
			//System.out.println("processGet for " + id);
			
			processGetStream(id, output);
		}
		else if ("/id".equals(p))
		{
			processGetId(output);
		}
		else if (p.startsWith("/size"))
		{
			processGetSize(output);
		}
		else
		{
			int queryIndex = p.indexOf('?');
			
			if (queryIndex != -1)
			{
				p = p.substring(0, queryIndex);
			}
			
			p = ("." + (p.endsWith("/") ? p + "index.html" : p)).replace('/', File.separatorChar);
			
			emitFile(output, p);
		}
	}
	
	/**
	 * processGetId
	 * 
	 * @param output
	 */
	private void processGetId(DataOutputStream output)
	{
		String id = UUID.randomUUID().toString();
		
		System.out.println("Create process for " + id);
		
		// start process for this id
		this._server.createProcess(id);
		
		// send identifier back to client
		this.sendText(output, id);
	}
	
	/**
	 * processStream
	 * 
	 * @param output
	 */
	private void processGetStream(String id, DataOutputStream output)
	{
		ProcessWrapper wrapper = this._server.getProcess(id);
		String text = wrapper.getText();
		
		if (text != null)
		{
			this.sendText(output, text);
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
		
		if (p.startsWith("/stream?id="))
		{
			String id = p.substring(p.indexOf("=") + 1);
			
			processPostStream(id, input, output);
		}
		else
		{
			System.out.println("Unrecognized POST URL: " + p);
		}
	}
	
	/**
	 * processPostSize
	 * 
	 * @param id
	 * @param input
	 * @param output
	 */
	private void processGetSize(DataOutputStream output)
	{
		Point size = Utils.getCharacterWidth();
		String responseText = Integer.toString(size.x) + "," + Integer.toString(size.y);
		
		this.sendText(output, responseText);
	}
	
	/**
	 * processPostStream
	 * 
	 * @param input
	 * @param output
	 */
	private void processPostStream(String id, BufferedReader input, DataOutputStream output)
	{
		String content = getPostContent(input);
		ProcessWrapper wrapper = this._server.getProcess(id);
		
		if (wrapper != null)
		{
			wrapper.sendText(content);
		}
		
		try
		{
			output.writeBytes("HTTP/1.0 200 OK\nContent-Length: 0\n\n");
		}
		catch (IOException e)
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
			BufferedReader input = new BufferedReader(new InputStreamReader(this._clientSocket.getInputStream()));
			DataOutputStream output = new DataOutputStream(this._clientSocket.getOutputStream());
			
			//System.out.println("Socket: " + this._clientSocket);
			
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
	 * sendText
	 * 
	 * @param output
	 * @param text
	 */
	private void sendText(DataOutputStream output, String text)
	{
		byte[] bytes = text.getBytes();
		int length = bytes.length;
		
		try
		{
			output.writeBytes("HTTP/1.0 200 OK\nContent-Length:" + length + "\n\n");
			output.write(bytes, 0, length);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
