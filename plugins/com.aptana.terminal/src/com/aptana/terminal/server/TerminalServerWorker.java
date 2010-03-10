package com.aptana.terminal.server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.theme.IThemeManager;
import com.aptana.editor.common.theme.Theme;
import com.aptana.terminal.Activator;
import com.aptana.util.StringUtil;

public class TerminalServerWorker implements Runnable
{
	private static final String ID_PARAMETER = "id"; //$NON-NLS-1$
	private static final String INDEX_PAGE_NAME = "index.html"; //$NON-NLS-1$
	private static final String ID_URL = "/id"; //$NON-NLS-1$
	private static final String STREAM_URL = "/stream"; //$NON-NLS-1$
	private static final String STATUS_200 = "200"; //$NON-NLS-1$
	private static final String STATUS_404 = "404"; //$NON-NLS-1$

	private TerminalServer _server;
	private Socket _clientSocket;

	/**
	 * HttpWorker
	 * 
	 * @param clientSocket
	 */
	public TerminalServerWorker(TerminalServer server, Socket clientSocket)
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
			if (url != null)
			{
				URL fileURL = FileLocator.toFileURL(url);
				File file = new File(new Path(fileURL.getPath()).toOSString());
	
				if (file.exists() && file.canRead())
				{
					int length = (int) file.length();
					byte[] bytes = new byte[length];
	
					new FileInputStream(file).read(bytes);
	
					if (p.endsWith(".template")) //$NON-NLS-1$
					{
						String result = populateTemplate(new String(bytes));
						bytes = result.getBytes();
					}
					this.sendResponse(output, STATUS_200, bytes);
				}
				else
				{
					this.sendErrorResponse(output);
				}
			}
			else
			{
				this.sendErrorResponse(output);
			}
		}
		catch (IOException e)
		{
			String message = MessageFormat.format(Messages.HttpWorker_Error_Locating_File, new Object[] { url
					.toString() });

			Activator.logError(message, e);
		}
	}

	/**
	 * Populates the string with theme and font values.
	 * 
	 * @param bytes
	 * @return
	 */
	private String populateTemplate(String content)
	{
		Theme currentTheme = this.getThemeManager().getCurrentTheme();
		
		Map<String, String> variables = new HashMap<String, String>();
		// Add theme colors
		variables.put("\\{caret\\}", toCSSRGB(currentTheme.getCaret())); //$NON-NLS-1$
		variables.put("\\{foreground\\}", toCSSRGB(currentTheme.getForeground())); //$NON-NLS-1$
		variables.put("\\{background\\}", toCSSRGB(currentTheme.getBackground())); //$NON-NLS-1$
		variables.put("\\{selection\\}", toCSSRGB(currentTheme.getSelection()));  //$NON-NLS-1$

		// ANSI Colors
		addAnsiColor(variables, "ansi.black", "0,0,0"); //$NON-NLS-1$ //$NON-NLS-2$
		addAnsiColor(variables, "ansi.red", "255,0,0"); //$NON-NLS-1$ //$NON-NLS-2$
		addAnsiColor(variables, "ansi.green", "0,255,0"); //$NON-NLS-1$ //$NON-NLS-2$
		addAnsiColor(variables, "ansi.yellow", "255,242,0"); //$NON-NLS-1$ //$NON-NLS-2$
		addAnsiColor(variables, "ansi.blue", "0,0,255"); //$NON-NLS-1$ //$NON-NLS-2$
		addAnsiColor(variables, "ansi.magenta", "236,0,140"); //$NON-NLS-1$ //$NON-NLS-2$
		addAnsiColor(variables, "ansi.cyan", "0,174,239"); //$NON-NLS-1$ //$NON-NLS-2$
		addAnsiColor(variables, "ansi.white", "255,255,255"); //$NON-NLS-1$ //$NON-NLS-2$

		// Now add the text editor font
		FontDescriptor fd = JFaceResources.getTextFontDescriptor();
		
		if (fd != null && fd.getFontData() != null && fd.getFontData().length > 0)
		{
			FontData data = fd.getFontData()[0];
			
			if (data != null)
			{
				String units = (Platform.getOS().equals(Platform.OS_WIN32)) ? "pt" : "px";
				
				variables.put("\\{font-name\\}", data.getName()); //$NON-NLS-1$
				variables.put("\\{font-size\\}", Integer.toString(data.getHeight()) + units); //$NON-NLS-1$
				Display display = Display.getCurrent();
				
				if (display == null)
					display = Display.getDefault();
				final Display theDisplay = display;
				final int lineHeight[] = new int[] { data.getHeight() + 2 };
				display.syncExec(new Runnable()
				{

					@Override
					public void run()
					{
						GC gc = new GC(theDisplay);
						gc.setFont(JFaceResources.getTextFont());
						lineHeight[0] = gc.getFontMetrics().getHeight();
						gc.dispose();
					}
				});
				variables.put("\\{line-height\\}", Integer.toString(lineHeight[0]) + units); //$NON-NLS-1$
			}
		}
		
		return StringUtil.replaceAll(content, variables);
	}

	private void addAnsiColor(Map<String, String> variables, String tokenName, String defaultValue)
	{
		String value = null;
		if (getThemeManager().getCurrentTheme().hasEntry(tokenName))
		{
			value = toCSSRGB(getThemeManager().getCurrentTheme().getForegroundAsRGB(tokenName));
		}
		else
		{
			// Use default color if there is no specific override!
			value = defaultValue;
		}
		variables.put("\\{" + tokenName + "\\}", value); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected IThemeManager getThemeManager()
	{
		return CommonEditorPlugin.getDefault().getThemeManager();
	}

	/**
	 * Output an RGB object to the string used inside rgb() in CSS.
	 * 
	 * @param caret
	 * @return
	 */
	private String toCSSRGB(RGB caret)
	{
		return MessageFormat.format("{0},{1},{2}", caret.red, caret.green, caret.blue); //$NON-NLS-1$
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
					this.sendResponse(output, text);
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
			this.sendResponse(output, id);
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
					wrapper.sendText(content);
				}
			}

			this.sendEmptyResponse(output);
		}
		else
		{
			String message = MessageFormat.format(Messages.HttpWorker_Unrecognized_POST_URL, new Object[] { url });

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

			if (this._clientSocket.getInetAddress().isLoopbackAddress())
			{
				try
				{
					Request request = Request.fromInputStream(this._clientSocket.getInputStream());
					String method = (request != null) ? request.getMethod() : null;

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
			}
			else
			{
				// NOTE: We're not sending FORBIDDEN as that may be too revealing
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
	 * sendResponse
	 * 
	 * @param output
	 * @param content
	 */
	private void sendResponse(DataOutputStream output, String content)
	{
		this.sendResponse(output, STATUS_200, content.getBytes());
	}

	/**
	 * sendReponse
	 * 
	 * @param output
	 * @param status
	 * @param content
	 */
	private void sendResponse(DataOutputStream output, String status, String content)
	{
		this.sendResponse(output, status, content.getBytes());
	}

	/**
	 * sendByteResponse
	 * 
	 * @param output
	 * @param status
	 * @param bytes
	 */
	private void sendResponse(DataOutputStream output, String status, byte[] bytes)
	{
		int length = bytes.length;

		try
		{
			output.writeBytes("HTTP/1.0 " + status + " OK\nContent-Length:" + length + "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
		this.sendResponse(output, ""); //$NON-NLS-1$
	}

	/**
	 * sendError
	 * 
	 * @param output
	 */
	private void sendErrorResponse(DataOutputStream output)
	{
		this.sendResponse(output, STATUS_404, Messages.HttpWorker_Not_Found4);
	}
}
