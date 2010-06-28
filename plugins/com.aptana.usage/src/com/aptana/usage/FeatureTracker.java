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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kevin Lindsey
 */
public final class FeatureTracker
{

	private static final String TABLE_NAME = "features"; //$NON-NLS-1$
	private static final String FEATURE = "feature"; //$NON-NLS-1$
	private static final String VERSION = "version"; //$NON-NLS-1$
	private static final String ENABLED = "enabled"; //$NON-NLS-1$

	private static final String GET_FEATURES = MessageFormat.format("SELECT {0}, {1}, {2} FROM {3}", //$NON-NLS-1$
			new Object[] { FEATURE, VERSION, ENABLED, TABLE_NAME });

	private static FeatureTracker INSTANCE;

	private FeatureTracker()
	{
	}

	public static FeatureTracker getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new FeatureTracker();

			if (!AptanaDB.tableExists(TABLE_NAME))
			{
				// builds table creation query
				String query = MessageFormat.format(
						"CREATE TABLE {0}({1} varchar(255),{2} varchar(255),{3} varchar(255))", //$NON-NLS-1$
						new Object[] { TABLE_NAME, FEATURE, VERSION, ENABLED });

				// creates table
				AptanaDB.getInstance().execute(query);
			}
		}
		return INSTANCE;
	}

	public void addFeature(FeatureInfo feature)
	{
		addFeature(feature.name, feature.version, feature.enabled);
	}

	public void addFeature(String feature, String version, boolean enabled)
	{
		String enabledStr = Boolean.toString(enabled);
		String query = MessageFormat.format("INSERT INTO {0}({1},{2},{3}) VALUES(''{4}'',''{5}'',''{6}'')", //$NON-NLS-1$
				new Object[] { TABLE_NAME, FEATURE, VERSION, ENABLED,
						(feature != null && feature.length() > 0) ? feature : "", //$NON-NLS-1$
						(version != null && version.length() > 0) ? version : "", //$NON-NLS-1$
						(enabledStr != null && enabledStr.length() > 0) ? enabledStr : "" //$NON-NLS-1$
				});

		AptanaDB.getInstance().execute(query);
	}

	public void clearFeatures()
	{
		String query = "DELETE FROM " + TABLE_NAME; //$NON-NLS-1$
		AptanaDB.getInstance().execute(query);
	}

	public FeatureInfo[] getFeatures()
	{
		final List<FeatureInfo> features = new LinkedList<FeatureInfo>();

		AptanaDB.getInstance().execute(GET_FEATURES, new IResultSetHandler()
		{
			public void processResultSet(ResultSet resultSet) throws SQLException
			{
				String feature = resultSet.getString(1);
				String version = resultSet.getString(2);
				String enabled = resultSet.getString(3);

				features.add(new FeatureInfo(feature, version, Boolean.parseBoolean(enabled)));
			}
		});

		return features.toArray(new FeatureInfo[features.size()]);
	}
}
