package com.aptana.portal.ui.dispatch.processorDelegates;


/**
 * A SQLite3 version processor that can get the current SQLite3 version in the system
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class SQLiteVersionProcessor extends BaseVersionProcessor
{
	private static final String SQLITE = "sqlite3"; //$NON-NLS-1$

	/**
	 * @return "sqlite3"
	 */
	@Override
	public String getSupportedApplication()
	{
		return SQLITE;
	}
}
