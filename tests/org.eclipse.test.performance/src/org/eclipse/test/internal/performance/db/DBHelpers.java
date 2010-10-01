/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.db;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.test.internal.performance.PerformanceTestPlugin;
import org.eclipse.test.internal.performance.data.Dim;


public class DBHelpers {
        
    private Connection fConnection;
    
    
    public static void main(String[] args) throws SQLException {
        
        //System.setProperty("eclipse.perf.dbloc", "net://localhost");
                        
        DBHelpers db= new DBHelpers();
        
		long start= System.currentTimeMillis();
		
		
		db.dumpSizes(System.out);
		//db.renameVariation("|build=3.0.0_200410130800||config=relengbuildwin2|", "|build=3.0.0_200406251208_200410130800||config=relengbuildwin2|");
		//db.dumpTable(ps, "VARIATION", 1000); //$NON-NLS-1$
		//db.countSamplesWithNullVariations();
        
		//Variations v= new Variations();
		//v.put(PerformanceTestPlugin.CONFIG, "relengbuildwin2"); //$NON-NLS-1$
		//v.put(PerformanceTestPlugin.BUILD, "I20041104%"); //$NON-NLS-1$
		
		//db.dumpSummaries(v, null);
        	//db.removeSamples(v);
		//db.countSamples(ps, v);
        	//db.view(ps, v, "org.eclipse.jdt.core.tests.performance.FullSourceWorkspaceTests#testPerfFullBuild()");
        
		
        System.out.println("time: " + ((System.currentTimeMillis()-start)/1000.0)); //$NON-NLS-1$
    }

    public DBHelpers() {
        fConnection= DB.getConnection();
    }
    
    void renameVariation(String oldName, String newName) throws SQLException {
        PreparedStatement update= fConnection.prepareStatement("update VARIATION set KEYVALPAIRS = ? where KEYVALPAIRS = ? "); //$NON-NLS-1$
        update.setString(1, newName);
        update.setString(2, oldName);
        update.executeUpdate();
        update.close();
    }
    
    void dumpSummaries(Variations variations, String scenarioPattern) {
        SummaryEntry[] summries= DB.querySummaries(variations, scenarioPattern);
        for (int i= 0; i < summries.length; i++)
            System.out.println(summries[i]);
    }
    
    void count(PrintStream ps) throws SQLException {
        PreparedStatement stmt= fConnection.prepareStatement("select count(*) from SCALAR where DATAPOINT_ID not in (select DATAPOINT.ID from DATAPOINT)"); //$NON-NLS-1$
        ResultSet set= stmt.executeQuery();
        if (set.next())
            ps.println("count: " + set.getInt(1)); //$NON-NLS-1$
        set.close();
        stmt.close();
    }

    void countDimension(PrintStream ps, Dim dim) throws SQLException {
        PreparedStatement stmt= fConnection.prepareStatement("select count(*) from SCALAR where DIM_ID = ?"); //$NON-NLS-1$
        stmt.setInt(1, dim.getId());
        ResultSet set= stmt.executeQuery();
        if (set.next())
            ps.println("dimension " + dim + ": " + set.getInt(1)); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    void countAllDimensions(PrintStream ps) throws SQLException {
        PreparedStatement stmt= fConnection.prepareStatement("select distinct DIM_ID from SCALAR"); //$NON-NLS-1$
        ResultSet set= stmt.executeQuery();
        while (set.next()) {
            Dim dimension= Dim.getDimension(set.getInt(1));
            if (dimension != null)
                countDimension(ps, dimension);
        }
    }

    int countSamples(PrintStream ps, Variations v) throws SQLException {
        PreparedStatement stmt= fConnection.prepareStatement("select count(*) from SAMPLE, VARIATION where VARIATION.KEYVALPAIRS = ? and SAMPLE.VARIATION_ID = VARIATION.ID"); //$NON-NLS-1$
        stmt.setString(1, v.toExactMatchString());
        ResultSet set= stmt.executeQuery();
        int n= 0;
        if (set.next())
            n= set.getInt(1);
        ps.println("samples with variation " + v + ": " + n); //$NON-NLS-1$ //$NON-NLS-2$
        return n;
    }
    
    void countDatapoints(PrintStream ps, Variations v) throws SQLException {
        PreparedStatement stmt= fConnection.prepareStatement("select count(*) from DATAPOINT, SAMPLE, VARIATION where VARIATION.KEYVALPAIRS = ? and SAMPLE.VARIATION_ID = VARIATION.ID and DATAPOINT.SAMPLE_ID= SAMPLE.ID"); //$NON-NLS-1$
        stmt.setString(1, v.toExactMatchString());
        ResultSet set= stmt.executeQuery();
        if (set.next())
            ps.println("datapoints with variation " + v + ": " + set.getInt(1)); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    void countScalars(PrintStream ps, Variations v) throws SQLException {
        PreparedStatement stmt= fConnection.prepareStatement("select count(*) from SCALAR, DATAPOINT, SAMPLE, VARIATION where VARIATION.KEYVALPAIRS = ? and SAMPLE.VARIATION_ID = VARIATION.ID and DATAPOINT.SAMPLE_ID= SAMPLE.ID and DATAPOINT.ID = SCALAR.DATAPOINT_ID"); //$NON-NLS-1$
        stmt.setString(1, v.toExactMatchString());
        ResultSet set= stmt.executeQuery();
        if (set.next())
            ps.println("scalars with variation " + v + ": " + set.getInt(1)); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    void removeSamples(Variations v) throws SQLException {
        
        boolean delete= true;
        
        int n= countSamples(System.out, v);
                
        int variation_id= 0;
        PreparedStatement stmt= fConnection.prepareStatement("select ID from VARIATION where KEYVALPAIRS = ?"); //$NON-NLS-1$
        stmt.setString(1, v.toExactMatchString());
        ResultSet set= stmt.executeQuery();
        if (set.next()) {
            variation_id= set.getInt(1);
            System.err.println("variation_id: " + variation_id); //$NON-NLS-1$
        }
        
        if (variation_id <= 0) {
            System.err.println("nothing found for variation " + v); //$NON-NLS-1$
        		return;
    		}

        PreparedStatement iterSamples= fConnection.prepareStatement("select SAMPLE.ID, SAMPLE.SCENARIO_ID from SAMPLE where SAMPLE.VARIATION_ID = ?"); //$NON-NLS-1$
        PreparedStatement iterDatapoints= fConnection.prepareStatement("select DATAPOINT.ID from DATAPOINT where DATAPOINT.SAMPLE_ID = ?"); //$NON-NLS-1$
        
        PreparedStatement deleteScalars= fConnection.prepareStatement("delete from SCALAR where DATAPOINT_ID = ?"); //$NON-NLS-1$
        PreparedStatement deleteDatapoints= fConnection.prepareStatement("delete from DATAPOINT where SAMPLE_ID = ?"); //$NON-NLS-1$
        PreparedStatement deleteSamples= fConnection.prepareStatement("delete from SAMPLE where SAMPLE.ID = ?"); //$NON-NLS-1$
        PreparedStatement deleteScenario= fConnection.prepareStatement("delete from SCENARIO where SCENARIO.ID = ?"); //$NON-NLS-1$
        
		ResultSet samples= null, datapoints= null;
        iterSamples.setInt(1, variation_id);
        samples= iterSamples.executeQuery();
        while (samples.next()) {
            int sample_id= samples.getInt(1);
            int scenario_id= samples.getInt(2);
            System.out.print(n + ": sample(" + sample_id + "):"); //$NON-NLS-1$ //$NON-NLS-2$
            iterDatapoints.setInt(1, sample_id);
	        datapoints= iterDatapoints.executeQuery();
	        int dps= 0;
	        while (datapoints.next()) {
	            int dp_id= datapoints.getInt(1);
	            //ps.println("  dp: " + dp_id); //$NON-NLS-1$
	            if (delete) {
	                deleteScalars.setInt(1, dp_id);
	                try {
                        deleteScalars.executeUpdate();
    		            fConnection.commit();
    		            dps++;
                    } catch (SQLException e) {
                        System.err.println("removing scalars: " + e); //$NON-NLS-1$
                    }
	            }
	        }
            System.out.println(" dps: " + dps); //$NON-NLS-1$
	        if (delete) {
	            deleteDatapoints.setInt(1, sample_id);
	            try {
                    deleteDatapoints.executeUpdate();
                    fConnection.commit();
                } catch (SQLException e1) {
                    System.err.println("removing datapoints: " + e1); //$NON-NLS-1$
                }
	            
	            deleteSamples.setInt(1, sample_id);
	            try {
                    deleteSamples.executeUpdate();
                    fConnection.commit();
                } catch (SQLException e) {
                    System.err.println("removing sample: " + e); //$NON-NLS-1$
                }
                
                deleteScenario.setInt(1, scenario_id);
	            try {
	                deleteScenario.executeUpdate();
                    fConnection.commit();
                } catch (SQLException e) {
                    // System.err.println("removing scenario: " + e); //$NON-NLS-1$
                }
	        }
	        n--;
        }
        if (delete) {
            PreparedStatement deleteSummaries= fConnection.prepareStatement("delete from SUMMARYENTRY where VARIATION_ID = ?"); //$NON-NLS-1$
            deleteSummaries.setInt(1, variation_id);
            deleteSummaries.executeUpdate();
            deleteSummaries.close();
            
            PreparedStatement deleteVariation= fConnection.prepareStatement("delete from VARIATION where ID = ?"); //$NON-NLS-1$
            deleteVariation.setInt(1, variation_id);
            try {
                deleteVariation.executeUpdate();
            } catch (SQLException e) {
                System.err.println("removing variation: " + e); //$NON-NLS-1$
            }
            deleteVariation.close();
        }

        if (samples != null) samples.close();
        if (datapoints != null) datapoints.close();
        
        if (iterSamples != null) iterSamples.close();
        if (iterDatapoints != null) iterDatapoints.close();
        
        if (deleteSamples != null) deleteSamples.close();
        if (deleteScenario != null) deleteScenario.close();
        if (deleteScalars != null) deleteScalars.close();
        if (deleteDatapoints != null) deleteDatapoints.close();
    }

    void countSamplesWithNullVariations() throws SQLException {
        Statement stmt= fConnection.createStatement();
        ResultSet rs= stmt.executeQuery("select count(*) from SAMPLE where SAMPLE.VARIATION_ID is null"); //$NON-NLS-1$
        while (rs.next()) {
            int config_id= rs.getInt(1);
            System.out.println("samples with NULL variation: " + config_id); //$NON-NLS-1$
        }
        rs.close();
        stmt.close();
    }
   
    void removeDimension(Dim dim) throws SQLException {
        PreparedStatement q= fConnection.prepareStatement("delete from SCALAR where SCALAR.DIM_ID = ?"); //$NON-NLS-1$
        q.setInt(1, dim.getId());
        q.executeUpdate();
        q.close();
    }
    
    void dumpScenarios(PrintStream ps, String pattern) throws SQLException {
        PreparedStatement stmt= fConnection.prepareStatement("select NAME from SCENARIO where NAME like ? order by NAME"); //$NON-NLS-1$
        stmt.setString(1, pattern);
        ResultSet rs= stmt.executeQuery();
        while (rs.next())
            ps.println(rs.getString(1));
        rs.close();
        stmt.close();
    }
    
    void dumpSizes(PrintStream ps) throws SQLException {
        if (fConnection == null)
            return;
        Statement stmt= fConnection.createStatement();
        try {
	        ResultSet rs= stmt.executeQuery("SELECT sys.systables.tablename FROM sys.systables WHERE sys.systables.tablename NOT LIKE 'SYS%' "); //$NON-NLS-1$
	        while (rs.next())
	            dumpSize(ps, rs.getString(1));
	        rs.close();
        } finally {
            stmt.close();
        }
    }

    void dumpSize(PrintStream ps, String table) throws SQLException {
        Statement stmt= fConnection.createStatement();
        ResultSet rs= stmt.executeQuery("select Count(*) from " + table); //$NON-NLS-1$
        if (rs.next())
            ps.println(table + ": " + rs.getInt(1)); //$NON-NLS-1$
        rs.close();
        stmt.close();
    }
    
    public void dumpAll(PrintStream ps, int maxRow) throws SQLException {
        if (fConnection == null)
            return;
        if (maxRow < 0)
            maxRow= 1000000;
		Statement stmt= fConnection.createStatement();
        try {
	        ResultSet rs= stmt.executeQuery("select SYS.SYSTABLES.TABLENAME from SYS.SYSTABLES where SYS.SYSTABLES.TABLENAME not like 'SYS%' "); //$NON-NLS-1$
	        while (rs.next()) {
	            dumpTable(ps, rs.getString(1), maxRow);
	            ps.println();
	        }
	        rs.close();
        } finally {
            stmt.close();
        }
    }
    
    void dumpTable(PrintStream ps, String tableName, int maxRow) throws SQLException {
        ps.print(tableName + '(');
		Statement select= fConnection.createStatement();
        ResultSet result= select.executeQuery("select * from " + tableName); //$NON-NLS-1$
        ResultSetMetaData metaData= result.getMetaData();
        int n= metaData.getColumnCount();
        for (int i= 0; i < n; i++) {
            ps.print(metaData.getColumnLabel(i+1));
            if (i < n-1)
                ps.print(", "); //$NON-NLS-1$
        }
        ps.println("):"); //$NON-NLS-1$
        for (int r= 0; result.next() && r < maxRow; r++) {
            for (int i= 0; i < n; i++)
                ps.print(' ' + result.getString(i+1));
            ps.println();
        }
        select.close();
    }

    void view(PrintStream ps, Variations v, String scenarioPattern) throws SQLException {
        Scenario[] scenarios= DB.queryScenarios(v, scenarioPattern, PerformanceTestPlugin.BUILD, null);
        ps.println(scenarios.length + " Scenarios"); //$NON-NLS-1$
        ps.println();
        for (int s= 0; s < scenarios.length; s++)
            scenarios[s].dump(ps, PerformanceTestPlugin.BUILD);
    }
}
