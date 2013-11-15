/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.results.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.eclipse.test.internal.performance.InternalDimensions;
import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.db.SQL;

/**
 * Specific implementation for massive database requests.
 */
public class SQL_Results extends SQL
{

	private PreparedStatement queryBuildAllScenarios, queryBuildScenarios, queryScenarioSummaries, queryAllComments,
			queryScenariosBuilds, queryScenarioDataPoints, queryScenarioTimestampDataPoints,
			queryScenarioBuildDataPoints, queryDimScalars, queryAllVariations;

	SQL_Results(Connection con) throws SQLException
	{
		super(con);
		// TODO Auto-generated constructor stub
	}

	protected void dispose() throws SQLException
	{
		super.dispose();
		if (this.queryBuildScenarios != null)
			this.queryBuildScenarios.close();
		if (this.queryBuildAllScenarios != null)
			this.queryBuildAllScenarios.close();
		if (this.queryScenarioSummaries != null)
			this.queryScenarioSummaries.close();
		if (this.queryAllComments != null)
			this.queryAllComments.close();
		if (this.queryScenariosBuilds != null)
			this.queryScenariosBuilds.close();
		if (this.queryScenarioDataPoints != null)
			this.queryScenarioDataPoints.close();
		if (this.queryDimScalars != null)
			this.queryDimScalars.close();
		if (this.queryAllVariations != null)
			this.queryAllVariations.close();
	}

	/**
	 * Get all comments from database
	 * 
	 * @return A set of the query result
	 * @throws SQLException
	 */
	ResultSet queryAllComments() throws SQLException
	{
		if (this.queryAllComments == null)
			this.queryAllComments = this.fConnection.prepareStatement("select ID, KIND, TEXT from COMMENT"); //$NON-NLS-1$
		return this.queryAllComments.executeQuery();
	}

	/**
	 * Get all variations from database.
	 * 
	 * @param configPattern
	 *            The pattern for all the concerned configurations
	 * @return A set of the query result
	 * @throws SQLException
	 */
	ResultSet queryAllVariations(String configPattern) throws SQLException
	{
		long start = System.currentTimeMillis();
		if (DB_Results.DEBUG)
			DB_Results.DEBUG_WRITER.print("[SQL query (config pattern=" + configPattern); //$NON-NLS-1$
		if (this.queryAllVariations == null)
		{
			this.queryAllVariations = this.fConnection
					.prepareStatement("select KEYVALPAIRS from VARIATION where KEYVALPAIRS like ? order by KEYVALPAIRS"); //$NON-NLS-1$
		}
		this.queryAllVariations.setString(1, "%" + configPattern + "%"); //$NON-NLS-1$ //$NON-NLS-2$
		ResultSet resultSet = this.queryAllVariations.executeQuery();
		if (DB_Results.DEBUG)
			DB_Results.DEBUG_WRITER.print(")=" + (System.currentTimeMillis() - start) + "ms]"); //$NON-NLS-1$ //$NON-NLS-2$
		return resultSet;
	}

	/**
	 * Query all scenarios corresponding to the default scenario pattern
	 * 
	 * @param scenarioPattern
	 *            The pattern for all the concerned scenarios
	 * @return Set of the query result
	 * @throws SQLException
	 */
	ResultSet queryBuildAllScenarios(String scenarioPattern) throws SQLException
	{
		if (this.queryBuildAllScenarios == null)
		{
			String statement = "select distinct SCENARIO.ID, SCENARIO.NAME , SCENARIO.SHORT_NAME from SCENARIO where " + //$NON-NLS-1$
					"SCENARIO.NAME LIKE ? " + //$NON-NLS-1$
					"order by SCENARIO.NAME"; //$NON-NLS-1$
			this.queryBuildAllScenarios = this.fConnection.prepareStatement(statement);
		}
		this.queryBuildAllScenarios.setString(1, scenarioPattern);
		return this.queryBuildAllScenarios.executeQuery();
	}

	/**
	 * Query all scenarios corresponding to a given scenario pattern and for a specific build name.
	 * 
	 * @param scenarioPattern
	 *            The pattern for all the concerned scenarios
	 * @param buildName
	 *            The name of the concerned build
	 * @return Set of the query result
	 * @throws SQLException
	 */
	ResultSet queryBuildScenarios(String scenarioPattern, String buildName) throws SQLException
	{
		if (this.queryBuildScenarios == null)
		{
			String statement = "select distinct SCENARIO.ID, SCENARIO.NAME , SCENARIO.SHORT_NAME from SCENARIO, SAMPLE, VARIATION where " + //$NON-NLS-1$
					"SAMPLE.VARIATION_ID = VARIATION.ID and VARIATION.KEYVALPAIRS LIKE ? and " + //$NON-NLS-1$
					"SAMPLE.SCENARIO_ID = SCENARIO.ID and SCENARIO.NAME LIKE ? " + //$NON-NLS-1$
					"order by SCENARIO.NAME"; //$NON-NLS-1$
			this.queryBuildScenarios = this.fConnection.prepareStatement(statement);
		}
		this.queryBuildScenarios.setString(1, "|build=" + buildName + '%'); //$NON-NLS-1$
		this.queryBuildScenarios.setString(2, scenarioPattern);
		return this.queryBuildScenarios.executeQuery();
	}

	/**
	 * Query all scalars for a given data point.
	 * 
	 * @param datapointId
	 *            The id of the data point
	 * @return Set of the query result
	 * @throws SQLException
	 */
	ResultSet queryDimScalars(int datapointId) throws SQLException
	{
		if (this.queryDimScalars == null)
		{
			StringBuffer buffer = new StringBuffer("select DIM_ID, VALUE from SCALAR where "); //$NON-NLS-1$
			buffer.append("DATAPOINT_ID = ? and "); //$NON-NLS-1$
			Dim[] dimensions = DB_Results.getResultsDimensions();
			int length = dimensions.length;
			for (int i = 0; i < length; i++)
			{
				if (i == 0)
				{
					buffer.append("(");
				}
				else
				{
					buffer.append(" or ");
				}
				buffer.append("DIM_ID = ");
				buffer.append(dimensions[i].getId());
			}
			buffer.append(") order by DIM_ID");
			this.queryDimScalars = this.fConnection.prepareStatement(buffer.toString());
		}
		this.queryDimScalars.setInt(1, datapointId);
		return this.queryDimScalars.executeQuery();
	}

	/**
	 * Get all data points for a given scenario and configuration.
	 * 
	 * @param config
	 *            The name of the concerned configuration
	 * @param scenarioID
	 *            The id of the scenario
	 * @param lastBuildName
	 *            Name of the last build on which data were stored locally
	 * @param lastBuildTime
	 *            Date in ms of the last build on which data were stored locally
	 * @return A set of the query result
	 * @throws SQLException
	 */
	ResultSet queryScenarioTimestampDataPoints(String config, int scenarioID, String lastBuildName, long lastBuildTime)
			throws SQLException
	{
		if (DB_Results.LOG)
			DB_Results.LOG_WRITER
					.starts("		+ SQL query (config=" + config + ", scenario ID=" + scenarioID + ", build name=" + lastBuildName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (this.queryScenarioTimestampDataPoints == null)
		{
			String statement = "select DATAPOINT.ID, DATAPOINT.STEP, VARIATION.KEYVALPAIRS from SAMPLE, DATAPOINT, VARIATION where " + //$NON-NLS-1$
					"SAMPLE.SCENARIO_ID = ? and " + //$NON-NLS-1$
					"DATAPOINT.SAMPLE_ID = SAMPLE.ID and " + //$NON-NLS-1$
					"SAMPLE.STARTTIME > ? and " + //$NON-NLS-1$
					"SAMPLE.VARIATION_ID = VARIATION.ID " + //$NON-NLS-1$
					"ORDER BY DATAPOINT.ID, DATAPOINT.STEP"; //$NON-NLS-1$
			this.queryScenarioTimestampDataPoints = this.fConnection.prepareStatement(statement);
		}
		this.queryScenarioTimestampDataPoints.setInt(1, scenarioID);
		Timestamp timestamp = new Timestamp(lastBuildTime + (5 * 3600L * 1000)); // create a time-stamp 5h after the
																					// given build time
		this.queryScenarioTimestampDataPoints.setTimestamp(2, timestamp);
		ResultSet resultSet = this.queryScenarioTimestampDataPoints.executeQuery();
		if (DB_Results.LOG)
			DB_Results.LOG_WRITER.ends(")"); //$NON-NLS-1$
		return resultSet;
	}

	ResultSet queryScenarioBuildDataPoints(String config, int scenarioID, String buildName) throws SQLException
	{
		if (DB_Results.LOG)
			DB_Results.LOG_WRITER
					.starts("		+ SQL query (config=" + config + ", scenario ID=" + scenarioID + ", build name=" + buildName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (this.queryScenarioBuildDataPoints == null)
		{
			String statement = "select DATAPOINT.ID, DATAPOINT.STEP, VARIATION.KEYVALPAIRS from SAMPLE, DATAPOINT, VARIATION where " + //$NON-NLS-1$
					"SAMPLE.VARIATION_ID = VARIATION.ID and VARIATION.KEYVALPAIRS LIKE ? and " + //$NON-NLS-1$
					"SAMPLE.SCENARIO_ID = ? and " + //$NON-NLS-1$
					"DATAPOINT.SAMPLE_ID = SAMPLE.ID " + //$NON-NLS-1$
					"ORDER BY DATAPOINT.ID, DATAPOINT.STEP"; //$NON-NLS-1$
			this.queryScenarioBuildDataPoints = this.fConnection.prepareStatement(statement);
		}
		this.queryScenarioBuildDataPoints.setString(1, "|build=" + buildName + '%'); //$NON-NLS-1$
		this.queryScenarioBuildDataPoints.setInt(2, scenarioID);
		ResultSet resultSet = this.queryScenarioBuildDataPoints.executeQuery();
		if (DB_Results.LOG)
			DB_Results.LOG_WRITER.ends(")"); //$NON-NLS-1$
		return resultSet;
	}

	/**
	 * Get all data points for a given scenario and configuration.
	 * 
	 * @param config
	 *            The name of the concerned configuration
	 * @param scenarioID
	 *            The id of the scenario
	 * @return A set of the query result
	 * @throws SQLException
	 */
	ResultSet queryScenarioDataPoints(String config, int scenarioID) throws SQLException
	{
		long start = System.currentTimeMillis();
		if (DB_Results.DEBUG)
			DB_Results.DEBUG_WRITER.print("[SQL query (config=" + config + ", scenario ID=" + scenarioID); //$NON-NLS-1$ //$NON-NLS-2$
		if (this.queryScenarioDataPoints == null)
		{
			String statement = "select DATAPOINT.ID, DATAPOINT.STEP, VARIATION.KEYVALPAIRS from VARIATION, SAMPLE, DATAPOINT where " + //$NON-NLS-1$
					"VARIATION.KEYVALPAIRS like ? and SAMPLE.VARIATION_ID = VARIATION.ID and " + //$NON-NLS-1$
					"SAMPLE.SCENARIO_ID = ? and " + //$NON-NLS-1$
					"DATAPOINT.SAMPLE_ID = SAMPLE.ID " + //$NON-NLS-1$
					"ORDER BY DATAPOINT.ID, DATAPOINT.STEP"; //$NON-NLS-1$
			this.queryScenarioDataPoints = this.fConnection.prepareStatement(statement);
		}
		this.queryScenarioDataPoints.setString(1, "%" + "hudson-ubuntu32" + "%"); //$NON-NLS-1$ //$NON-NLS-2$
		this.queryScenarioDataPoints.setInt(2, scenarioID);
		ResultSet resultSet = this.queryScenarioDataPoints.executeQuery();
		if (DB_Results.DEBUG)
			DB_Results.DEBUG_WRITER.print(")=" + (System.currentTimeMillis() - start) + "ms]"); //$NON-NLS-1$ //$NON-NLS-2$
		return resultSet;
	}

	/**
	 * Query all summaries from database for a given scenario, configuration and builds.
	 * 
	 * @param config
	 *            The name of the concerned configuration
	 * @param scenarioID
	 *            The id of the scenario
	 * @param builds
	 *            The list of builds to get summaries. When <code>null</code> summaries for all DB builds will be read.
	 * @return Set of the query result
	 * @throws SQLException
	 */
	ResultSet queryScenarioSummaries(int scenarioID, String config, String[] builds) throws SQLException
	{
		int length = builds == null ? 0 : builds.length;
		String buildPattern;
		switch (length)
		{
			case 0:
				buildPattern = "%"; //$NON-NLS-1$
				break;
			case 1:
				buildPattern = builds[0];
				break;
			default:
				StringBuffer buffer = new StringBuffer();
				loop: for (int idx = 0; idx < builds[0].length(); idx++)
				{
					char ch = builds[0].charAt(idx);
					for (int i = 1; i < length; i++)
					{
						if (idx == builds[i].length())
						{
							break loop;
						}
						if (builds[i].charAt(idx) != ch)
						{
							buffer.append('_');
							continue loop;
						}
					}
					buffer.append(ch);
				}
				buffer.append("%"); //$NON-NLS-1$
				buildPattern = buffer.toString();
				break;
		}
		if (this.queryScenarioSummaries == null)
		{
			this.queryScenarioSummaries = this.fConnection
					.prepareStatement("select KEYVALPAIRS , IS_GLOBAL, COMMENT_ID, DIM_ID from VARIATION, SUMMARYENTRY where " + //$NON-NLS-1$
							"KEYVALPAIRS like ? and " + //$NON-NLS-1$
							"VARIATION_ID = VARIATION.ID and " + //$NON-NLS-1$
							"SCENARIO_ID = ? and " + //$NON-NLS-1$
							"(DIM_ID = " + InternalDimensions.ELAPSED_PROCESS.getId() + " or DIM_ID = 0)" + //$NON-NLS-1$ //$NON-NLS-2$
							" order by VARIATION_ID, DIM_ID"); //$NON-NLS-1$
		}
		this.queryScenarioSummaries.setString(1, "|build=" + buildPattern + "||config=" + config + "||jvm=sun|"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.queryScenarioSummaries.setInt(2, scenarioID);
		return this.queryScenarioSummaries.executeQuery();
	}

}
