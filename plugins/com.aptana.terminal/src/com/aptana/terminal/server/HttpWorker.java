package com.aptana.terminal.server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.text.MessageFormat;
import java.util.UUID;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

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
	private static final boolean IS_WINDOWS = Platform.getOS().equals(Platform.OS_WIN32);
	
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
			File file = new File(new Path(fileURL.getPath()).toOSString());
			
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
			String message = MessageFormat.format(
				Messages.HttpWorker_Error_Locating_File,
				new Object[] { url.toString() }
			);
			
			Activator.logError(message, e);
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
			
			if (wrapper != null)
			{
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
			
			if (content != null && content.length() > 0)
			{
				String id = request.getParameter(ID_PARAMETER);
				ProcessWrapper wrapper = this._server.getProcess(id);
				
				if (wrapper != null)
				{
					if (content.equals("\r") && IS_WINDOWS)
					{
						content += "\n";
					}
					
					wrapper.sendText(content);
				}
			}
			
			this.sendEmptyResponse(output);
		}
		else
		{
			String message = MessageFormat.format(
				Messages.HttpWorker_Unrecognized_POST_URL,
				new Object[] { url }
			);
			
			Activator.logWarning(message);
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
		catch (IOException e)
		{
			Activator.logError(Messages.HttpWorker_Error_Accessing_Output_Stream, e);
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
			Activator.logError(Messages.HttpWorker_Error_Writing_To_Client, e);
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
			Activator.logError(Messages.HttpWorker_Error_Writing_To_Client, e);
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
			Activator.logError(Messages.HttpWorker_Error_Writing_To_Client, e);
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
