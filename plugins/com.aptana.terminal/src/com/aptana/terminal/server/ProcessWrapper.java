package com.aptana.terminal.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.terminal.Activator;

public class ProcessWrapper
{
	private Process _process;
	private ProcessReader _stdout;
	private ProcessReader _stderr;
	private ProcessWriter _stdin;
	private StringBuffer _output;
	
	private boolean _useSocket = false;
	private Socket _socket;

	/**
	 * ProcessWrapper
	 * 
	 * @param process
	 */
	public ProcessWrapper()
	{
	}

	/**
	 * start
	 */
	public void start()
	{
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("redtty"), null);

		try
		{
			URL fileURL = FileLocator.toFileURL(url);
			File file = new File(fileURL.toURI());
			ProcessBuilder builder = new ProcessBuilder(file.getAbsolutePath());
			Map<String, String> env = builder.environment();
			env.put("TERM", "xterm-color");

			if (this._useSocket)
			{
				final ServerSocket serverSocket = new ServerSocket(8182);
				new Thread("Controlling socket")
				{
					@Override
					public void run()
					{
						try
						{
							serverSocket.setSoTimeout(3000);
							_socket = serverSocket.accept();
							byte[] buffer = new byte[1024];
							int read_count = _socket.getInputStream().read(buffer);
							if (read_count > 0)
							{
								String string = new String(buffer, 0, read_count, "ISO-8859-1");
								if (string.endsWith("\n"))
								{
									int pid = Integer.parseInt(string.substring(0, string.length() - 1));
									System.out.println("BASH PID=" + pid);
									// socket.getOutputStream().write("GETDIM\n".getBytes("ISO-8859-1"));
									return;
								}
							}
							_socket.close();
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						finally
						{
							try
							{
								serverSocket.close();
							}
							catch (IOException ignore)
							{
							}
						}
					}
				}.start();
			}

			this._process = builder.start();
			this._output = new StringBuffer();
			this._stdout = new ProcessReader("STDOUT", this._process.getInputStream(), this._output);
			this._stderr = new ProcessReader("STDERR", this._process.getErrorStream(), this._output);
			this._stdin = new ProcessWriter(this._process.getOutputStream());

			this._stdout.start();
			this._stderr.start();
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
	 * stop
	 */
	public void stop()
	{
		if (this._process != null)
		{
			this._process.destroy();

			try
			{
				this._stdout.join();
				this._stderr.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		if (_socket != null)
		{
			try
			{
				_socket.close();
			}
			catch (IOException ignore)
			{
			}
			_socket = null;
		}
	}

	/**
	 * sendText
	 * 
	 * @param text
	 */
	public void sendText(char[] chars)
	{
		this.sendText(new String(chars));
	}

	/**
	 * sendText
	 * 
	 * @param text
	 */
	public void sendText(String text)
	{
		if (this._stdin != null)
		{
			this._stdin.sendText(text);
		}
	}

	/**
	 * getText
	 * 
	 * @return
	 */
	public String getText()
	{
		String result = null;

		if (this._output != null)
		{
			synchronized (this._output)
			{
				result = this._output.toString();
				this._output.setLength(0);
			}
		}

		return result;
	}
}
