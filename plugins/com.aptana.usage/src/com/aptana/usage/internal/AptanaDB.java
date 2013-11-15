/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage.internal;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ResourceUtil;
import com.aptana.usage.IDebugScopes;
import com.aptana.usage.IResultSetHandler;
import com.aptana.usage.Messages;
import com.aptana.usage.UsagePlugin;

/**
 * @author Kevin Lindsey
 */
public class AptanaDB
{

	private static final String DATABASE_NAME = "aptanaDB"; //$NON-NLS-1$
	private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver"; //$NON-NLS-1$
	private static final String PROTOCOL = "jdbc:derby:"; //$NON-NLS-1$

	private static AptanaDB INSTANCE;

	private boolean _driverLoaded;

	private AptanaDB()
	{
	}

	public static AptanaDB getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new AptanaDB();
			INSTANCE.loadDriver();
		}
		return INSTANCE;
	}

	public void execute(String query)
	{
		execute(query, null);
	}

	public void execute(String query, IResultSetHandler handler)
	{
		Connection connection = getInstance().getConnection();

		if (connection != null)
		{
			Statement statement = null;
			ResultSet resultSet = null;
			try
			{
				statement = connection.createStatement();

				if (IdeLog.isTraceEnabled(UsagePlugin.getDefault(), IDebugScopes.DB))
				{
					IdeLog.logTrace(UsagePlugin.getDefault(), query);
				}

				if (handler == null)
				{
					statement.execute(query);
				}
				else
				{
					resultSet = statement.executeQuery(query);
					while (resultSet.next())
					{
						handler.processResultSet(resultSet);
					}
				}
			}
			catch (SQLException e)
			{
				IdeLog.logWarning(UsagePlugin.getDefault(), Messages.AptanaDB_Error_Execute_Query, e);
			}
			finally
			{
				if (resultSet != null)
				{
					try
					{
						resultSet.close();
					}
					catch (SQLException e)
					{
						// ignores
					}
				}

				if (statement != null)
				{
					try
					{
						statement.close();
					}
					catch (SQLException e)
					{
						// ignores
					}
				}

				try
				{
					connection.close();
				}
				catch (SQLException e)
				{
					// ignores
				}
			}
		}
	}

	public Connection getConnection()
	{
		if (_driverLoaded)
		{
			try
			{
				/*
				 * The connection specifies create=true to cause the database to be created. To remove the database,
				 * remove the directory aptanaDB and its contents. The directory aptanaDB will be created under the
				 * directory that the system property derby.system.home points to, or the current directory if
				 * derby.system.home is not set.
				 */
				return DriverManager.getConnection(PROTOCOL + DATABASE_NAME + ";create=true"); //$NON-NLS-1$
			}
			catch (SQLException e)
			{
				IdeLog.logWarning(UsagePlugin.getDefault(), Messages.AptanaDB_FailedToConnect, e);
			}
		}
		return null;
	}

	public void shutdown()
	{
		if (_driverLoaded)
		{
			try
			{
				DriverManager.getConnection(PROTOCOL + DATABASE_NAME + ";shutdown=true"); //$NON-NLS-1$
			}
			catch (SQLException e)
			{
				// ignores since it's during shutdown
			}
		}
	}

	public static boolean tableExists(String tableName)
	{
		Connection connection = null;
		Statement statement = null;

		try
		{
			connection = getInstance().getConnection();
			if (connection == null)
			{
				return false;
			}

			statement = connection.createStatement();
			statement.execute("SELECT COUNT(*) FROM " + tableName); //$NON-NLS-1$
		}
		catch (SQLException sqle)
		{
			String errorStateCode = sqle.getSQLState();
			if (errorStateCode.equals("42X05")) // Table does not exist //$NON-NLS-1$
			{
				return false;
			}
		}
		finally
		{
			if (statement != null)
			{
				try
				{
					statement.close();
				}
				catch (SQLException e)
				{
				}
			}

			if (connection != null)
			{
				try
				{
					connection.close();
				}
				catch (SQLException e)
				{
				}
			}
		}

		return true;
	}

	private void loadDriver()
	{
		try
		{
			Location location = Platform.getConfigurationLocation();
			if (location == null || location.isReadOnly())
			{
				location = Platform.getInstallLocation();
				if (location == null || location.isReadOnly())
				{
					location = Platform.getUserLocation();
					if (location == null || location.isReadOnly())
					{
						location = null;
					}
				}
			}
			if (location != null)
			{
				File homeDirectory = ResourceUtil.resourcePathToFile(location.getURL());

				// makes sure home directory exists
				homeDirectory.mkdirs();
				// points Derby home to directory
				System.setProperty("derby.system.home", homeDirectory.getAbsolutePath()); //$NON-NLS-1$
				// loads driver into VM
				Class.forName(DRIVER).newInstance();

				// tags as successfully loaded
				_driverLoaded = true;
			}
		}
		catch (InstantiationException e)
		{
			IdeLog.logError(UsagePlugin.getDefault(), Messages.AptanaDB_FailedToInstantiate, e);
		}
		catch (IllegalAccessException e)
		{
			IdeLog.logError(UsagePlugin.getDefault(), Messages.AptanaDB_FailedToAccess, e);
		}
		catch (ClassNotFoundException e)
		{
			IdeLog.logError(UsagePlugin.getDefault(), Messages.AptanaDB_FailedToLoad, e);
		}
	}
}
