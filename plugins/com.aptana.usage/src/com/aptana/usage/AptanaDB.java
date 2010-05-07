/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Aptana provides a special exception to allow redistribution of this file
 * with certain Eclipse Public Licensed code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 *
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 *
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 *
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

import com.aptana.core.util.ResourceUtil;

/**
 * @author Kevin Lindsey
 */
public class AptanaDB
{

	private static final String DATABASE_NAME = "aptanaDB"; //$NON-NLS-1$
	private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver"; //$NON-NLS-1$
	private static final String PROTOCOL = "jdbc:derby:"; //$NON-NLS-1$

	private static AptanaDB INSTANCE;

	private List<IDBListener> _listeners = new ArrayList<IDBListener>();
	private boolean _driverLoaded;

	private AptanaDB()
	{
		_listeners = new ArrayList<IDBListener>();
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

	public void addListener(IDBListener listener)
	{
		if (!_listeners.contains(listener))
		{
			_listeners.add(listener);
		}
	}

	public void removeListener(IDBListener listener)
	{
		_listeners.remove(listener);
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
				UsagePlugin.logError(Messages.AptanaDB_Error_Execute_Query, e);
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
				UsagePlugin.logError(Messages.AptanaDB_FailedToConnect, e);
			}
		}
		return null;
	}

	public void shutdown()
	{
		if (_driverLoaded)
		{
			// fire shutdown event
			for (IDBListener listener : _listeners)
			{
				listener.shutdown();
			}
			try
			{
				DriverManager.getConnection(PROTOCOL + DATABASE_NAME + ";shutdown=true"); //$NON-NLS-1$
			}
			catch (SQLException e)
			{
				// NOTE: We always get an exception when shutting down the database. We make sure it was the right
				// one for successful shutdown. SQLState is "08006" and ErrorCode is 45000 for single database shutdown
				if (e.getErrorCode() != 45000 && !"XJ015".equals(e.getSQLState()) && !"08006".equals(e.getSQLState())) //$NON-NLS-1$ //$NON-NLS-2$
				{
					UsagePlugin.logError(Messages.AptanaDB_ErrorShutdown, e);
				}
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
			UsagePlugin.logError(Messages.AptanaDB_FailedToInstantiate, e);
		}
		catch (IllegalAccessException e)
		{
			UsagePlugin.logError(Messages.AptanaDB_FailedToAccess, e);
		}
		catch (ClassNotFoundException e)
		{
			UsagePlugin.logError(Messages.AptanaDB_FailedToLoad, e);
		}
	}
}
