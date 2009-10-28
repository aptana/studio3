package com.aptana.terminal.server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.terminal.Activator;
import com.aptana.terminal.Size;
import com.aptana.terminal.Utils;

public class HttpWorker implements Runnable
{
	private static final String ID_PARAMETER = "id"; //$NON-NLS-1$
	private static final String INDEX_PAGE_NAME = "index.html"; //$NON-NLS-1$
	private static final String SIZE_URL = "/size"; //$NON-NLS-1$
	private static final String ID_URL = "/id"; //$NON-NLS-1$
	private static final String STREAM_URL = "/stream"; //$NON-NLS-1$
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
				
				this.sendByteResponse(output, bytes);
			}
			else
			{
				this.sendErrorResponse(output);
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
	 * processGet
	 * 
	 * @param get
	 * @param input
	 * @param output
	 */
	private void processGet(Request request, DataOutputStream output)
	{
		String url = request.getURL();
		
		if (STREAM_URL.equals(url))
		{
			String id = request.getParameter(ID_PARAMETER);
			ProcessWrapper wrapper = this._server.getProcess(id);
			String text = wrapper.getText();
			
			if (text != null)
			{
				this.sendTextResponse(output, text);
			}
			else
			{
				this.sendEmptyResponse(output);
			}
		}
		else if (ID_URL.equals(url))
		{
			String id = UUID.randomUUID().toString();
			
			this._server.createProcess(id);
			this.sendTextResponse(output, id);
		}
		else if (SIZE_URL.equals(url))
		{
			Size size = Utils.getCharacterWidth();
			
			this.sendTextResponse(output, size.toString());
		}
		else
		{
			url = ("." + (url.endsWith("/") ? url + INDEX_PAGE_NAME : url)).replace('/', File.separatorChar); //$NON-NLS-1$ //$NON-NLS-2$
			
			emitFile(output, url);
		}
	}
	
	/**
	 * processPost
	 * 
	 * @param post
	 * @param input
	 * @param output
	 */
	private void processPost(Request request, DataOutputStream output)
	{
		String url = request.getURL();
		
		if (STREAM_URL.equals(url))
		{
			String content = request.getRawContent();
			String id = request.getParameter(ID_PARAMETER);
			ProcessWrapper wrapper = this._server.getProcess(id);
			
			if (wrapper != null)
			{
				wrapper.sendText(content);
			}
			
			this.sendEmptyResponse(output);
		}
		else
		{
			System.out.println(Messages.HttpWorker_Unrecognized_POST_URL + url);
		}
	}
	
	/**
	 * run
	 */
	public void run()
	{
		try
		{
			DataOutputStream output = new DataOutputStream(this._clientSocket.getOutputStream());
			
			try
			{
				Request request = Request.fromInputStream(this._clientSocket.getInputStream());
				String method = request.getMethod();
				
				if ("GET".equals(method)) //$NON-NLS-1$
				{
					this.processGet(request, output);
				}
				else if ("POST".equals(method)) //$NON-NLS-1$
				{
					this.processPost(request, output);
				}
				else
				{
					this.sendErrorResponse(output);
				}
			}
			catch (Exception e)
			{
				this.sendErrorResponse(output);
			}
			
			output.close();
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	 * sendByteResponse
	 * 
	 * @param output
	 * @param bytes
	 */
	private void sendByteResponse(DataOutputStream output, byte[] bytes)
	{
		int length = bytes.length;
		
		try
		{
			output.writeBytes("HTTP/1.0 200 OK\nContent-Length:" + length + "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
			output.write(bytes, 0, length);
		}
		catch (IOException e)
		{
		}
	}
	
	/**
	 * sendEmptyResponse
	 * 
	 * @param output
	 */
	private void sendEmptyResponse(DataOutputStream output)
	{
		try
		{
			output.writeBytes("HTTP/1.0 200 OK\nContent-Length: 0\n\n"); //$NON-NLS-1$
		}
		catch (IOException e)
		{
		}
	}
	
	/**
	 * sendError
	 * 
	 * @param output
	 */
	private void sendErrorResponse(DataOutputStream output)
	{
		try
		{
			output.writeBytes("HTTP/1.0 404 ERROR\n\n"); //$NON-NLS-1$
		}
		catch (IOException e)
		{
		}
	}
	
	/**
	 * sendText
	 * 
	 * @param output
	 * @param text
	 */
	private void sendTextResponse(DataOutputStream output, String text)
	{
		this.sendByteResponse(output, text.getBytes());
	}
}
