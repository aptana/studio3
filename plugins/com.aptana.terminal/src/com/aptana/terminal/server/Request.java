package com.aptana.terminal.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.aptana.terminal.Activator;

public class Request
{
	private String _method;
	private String _url;
	private String _protocol;
	private Map<String,String> _headers;
	private Map<String,String> _parameters;
	private String _rawContent;
	
	public static Request fromInputStream(InputStream input)
	{
		Request result = new Request();
		
		try
		{
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(isr);
			
			// process request
			String s = br.readLine();
			
			if (s != null)
			{
				int methodEnd = s.indexOf(' ');
				int urlEnd = s.lastIndexOf(' ');
				
				if (methodEnd != -1 && urlEnd != -1)
				{
					String method = s.substring(0, methodEnd);
					String urlWithParams = s.substring(methodEnd + 1, urlEnd);
					String protocol = s.substring(urlEnd + 1);
					String url;
					
					int paramIndex = urlWithParams.indexOf("?"); //$NON-NLS-1$
					
					if (paramIndex == -1)
					{
						url = urlWithParams;
					}
					else
					{
						url = urlWithParams.substring(0, paramIndex);
						String params = urlWithParams.substring(paramIndex + 1);
						String[] keyPairs = params.split("&"); //$NON-NLS-1$
						
						for (String keyPair : keyPairs)
						{
							String[] parts = keyPair.split("="); //$NON-NLS-1$
							
							result.addParameter(parts[0], parts[1]);
						}
					}
					
					result.setMethod(method);
					result.setURL(url);
					result.setProtocol(protocol);
				}
				
				s = br.readLine();
			}
			
			// process headers
			while (s != null)
			{
				if (s.length() == 0)
				{
					// end of headers
					break;
				}
				
				int endKey = s.indexOf(':');
				String key = s.substring(0, endKey);
				String value = s.substring(endKey + 2);
				
				result.addHeader(key, value);
				
				s = br.readLine();
			}
			
			String lengthString = result.getHeader("Content-Length"); //$NON-NLS-1$
			
			if (lengthString != null && lengthString.length() > 0)
			{
				int contentLength = Integer.parseInt(lengthString);
				
				char[] chars = new char[contentLength];
				br.read(chars);
				
				result.setRawContent(new String(chars));
			}
		}
		catch (IOException e)
		{
			Activator.logError(Messages.Request_Request_Processing_Error, e);
			
			result = null;
		}
		
		return result;
	}
	
	/**
	 * Request
	 */
	public Request()
	{
		this._headers = new HashMap<String,String>();
		this._parameters = new HashMap<String,String>();
	}
	
	/**
	 * addHeader
	 * 
	 * @param name
	 * @param value
	 */
	protected void addHeader(String name, String value)
	{
		// TODO: support multiple instances of same header?
		this._headers.put(name, value);
	}
	
	/**
	 * addParameter
	 * 
	 * @param name
	 * @param value
	 */
	protected void addParameter(String name, String value)
	{
		// TODO: support multiple instances of same parameter?
		this._parameters.put(name, value);
	}
	
	/**
	 * getHeader
	 * 
	 * @param header
	 * @return
	 */
	public String getHeader(String header)
	{
		String result = ""; //$NON-NLS-1$
		
		if (this._headers.containsKey(header))
		{
			result = this._headers.get(header);
		}
		
		return result;
	}
	
	/**
	 * getMethod
	 * 
	 * @return
	 */
	public String getMethod()
	{
		return this._method;
	}
	
	/**
	 * getParameter
	 * 
	 * @param parameter
	 * @return
	 */
	public String getParameter(String parameter)
	{
		String result = ""; //$NON-NLS-1$
		
		if (this._parameters.containsKey(parameter))
		{
			result = this._parameters.get(parameter);
		}
		
		return result;
	}
	
	/**
	 * getProtocol
	 * 
	 * @return
	 */
	public String getProtocol()
	{
		return this._protocol;
	}
	
	/**
	 * getRawContent
	 * 
	 * @return
	 */
	public String getRawContent()
	{
		return this._rawContent;
	}
	
	/**
	 * getURL
	 * 
	 * @return
	 */
	public String getURL()
	{
		return this._url;
	}
	
	/**
	 * setMethod
	 * 
	 * @param method
	 */
	protected void setMethod(String method)
	{
		this._method = method;
	}
	
	/**
	 * setProcotol
	 * 
	 * @param protocol
	 */
	protected void setProtocol(String protocol)
	{
		this._protocol = protocol;
	}
	
	/**
	 * setRawContent
	 * 
	 * @param content
	 */
	protected void setRawContent(String content)
	{
		this._rawContent = content;
	}
	
	/**
	 * setURL
	 * 
	 * @param url
	 */
	protected void setURL(String url)
	{
		this._url = url;
	}
}
