/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.engineyard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.PlatformUtil;

public class EngineYardAPI
{

	/**
	 * Content Type Accept header. Tells Engine Yard we can accept given content types. We default to JSON.
	 */
	private static final String ACCEPT_HEADER = "Accept"; //$NON-NLS-1$
	private static final String ACCEPT_CONTENT_TYPES = "application/json"; //$NON-NLS-1$

	/**
	 * Special API token header for Engine Yard.
	 */
	private static final String EY_API_TOKEN_HEADER = "X-EY-Cloud-Token"; //$NON-NLS-1$

	private String apiToken;

	public EngineYardAPI()
	{
		this.apiToken = null;
	}

	public static File getCredentialsFile()
	{
		return new File(PlatformUtil.expandEnvironmentStrings("~/.eyrc")); //$NON-NLS-1$
	}

	public IStatus authenticateFromCredentials()
	{
		File file = getCredentialsFile();
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(file));
			// grab '---'
			reader.readLine();
			String token = reader.readLine();
			// extract the api token
			apiToken = token.split("api_token: ")[1]; //$NON-NLS-1$
		}
		catch (Exception e)
		{
			IdeLog.logError(EngineYardPlugin.getDefault(), e);
			return new Status(IStatus.ERROR, EngineYardPlugin.getPluginIdentifier(),
					Messages.EngineYardAPI_CredentialsFile_Invalid);
		}
		finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			}
			catch (IOException e)
			{
				// ignore
			}
		}

		HttpURLConnection connection = null;
		try
		{

			// authenticate API token from .eyrc with engine yards

			 URL url = new URL("https://cloud.engineyard.com/api/v2/apps"); //$NON-NLS-1$
			 connection = (HttpURLConnection) url.openConnection();
			 connection.setRequestProperty(ACCEPT_HEADER, ACCEPT_CONTENT_TYPES);
			 connection.setRequestProperty(EY_API_TOKEN_HEADER, apiToken);
			 connection.setUseCaches(false);
			 connection.setAllowUserInteraction(false);
			 
			 int code = connection.getResponseCode();
			 if (code == HttpURLConnection.HTTP_OK)
			 {
			 return Status.OK_STATUS;
			 }
			
			 if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN)
			 {
				return new Status(IStatus.ERROR, EngineYardPlugin.getPluginIdentifier(), ""); //$NON-NLS-1$
			 }
			// some other response code...
			return new Status(IStatus.ERROR, EngineYardPlugin.getPluginIdentifier(),
					""); //$NON-NLS-1$
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, EngineYardPlugin.getPluginIdentifier(), e.getMessage(), e);
		}
		finally
		{
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	private IStatus getAPIToken(String userId, String password)
	{
		// Use user and password to grab the api token from engine yard

		HttpURLConnection connection = null;
		try
		{

			 DataOutputStream output = null;
			 BufferedReader input = null;
			 
			 URL url = new URL("https://cloud.engineyard.com/api/v2/authenticate"); //$NON-NLS-1$
			 connection = (HttpURLConnection) url.openConnection();
			 connection.setRequestProperty(ACCEPT_HEADER, ACCEPT_CONTENT_TYPES);
			 connection.setUseCaches(false);
			 connection.setAllowUserInteraction(false);
			 connection.setDoOutput(true);

			 // writes POST
		 	 output = new DataOutputStream(connection.getOutputStream());
		 	 String postData = "email="+ userId + "&password=" + password; //$NON-NLS-1$ //$NON-NLS-2$
		 	 output.writeBytes(postData);
			 output.flush();
			 
			 // Get the response
			 input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			 StringBuffer sb = new StringBuffer();
			 String line;

			 while ((line = input.readLine()) != null)
			 {
				 sb.append(line);
		  	 }
			
			 apiToken = sb.toString().replace('}', ' ').split(":")[1].replace('"', ' ').trim(); //$NON-NLS-1$

			 int code = connection.getResponseCode();
			 if (code == HttpURLConnection.HTTP_OK)
			 {
			 return Status.OK_STATUS;
			 }
			
			 if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN)
			 {
				return new Status(IStatus.ERROR, EngineYardPlugin.getPluginIdentifier(), ""); //$NON-NLS-1$
			 }
//			 some other response code...
			return new Status(IStatus.ERROR, EngineYardPlugin.getPluginIdentifier(),
					 ""); //$NON-NLS-1$
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, EngineYardPlugin.getPluginIdentifier(), e.getMessage(), e);
		}
		finally
		{
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	public boolean writeCredentials(String userId, String password)
	{
		if (getAPIToken(userId, password).isOK())
		{
			File credentials = getCredentialsFile();
			BufferedWriter writer = null;
			try
			{
				writer = new BufferedWriter(new FileWriter(credentials));
				writer.write("--- "); //$NON-NLS-1$
				writer.newLine();
				writer.write("api_token: "); //$NON-NLS-1$
				writer.write(apiToken);
				writer.newLine();
				return true;
			}
			catch (IOException e)
			{
				IdeLog.logError(EngineYardPlugin.getDefault(), e);
			}
			finally
			{
				try
				{
					if (writer != null)
					{
						writer.close();
					}
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}

		return false;
	}
}
