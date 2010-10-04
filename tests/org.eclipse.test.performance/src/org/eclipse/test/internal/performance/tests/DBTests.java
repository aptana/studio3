/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.tests;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.test.internal.performance.InternalDimensions;
import org.eclipse.test.internal.performance.PerformanceTestPlugin;
import org.eclipse.test.internal.performance.data.DataPoint;
import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.data.Scalar;
import org.eclipse.test.internal.performance.db.DB;
import org.eclipse.test.internal.performance.db.Scenario;
import org.eclipse.test.internal.performance.db.SummaryEntry;
import org.eclipse.test.internal.performance.db.Variations;
import org.eclipse.test.performance.Dimension;
import org.eclipse.test.performance.Performance;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class DBTests extends TestCase {
    
    private static final String CONFIG= "c"; //$NON-NLS-1$
    private static final String BUILD= "b"; //$NON-NLS-1$
    
    private static final String SCENARIO_NAME_0= "bar.testScenario0"; //$NON-NLS-1$
    private static final String SCENARIO_NAME_1= "bar.testScenario1"; //$NON-NLS-1$
    private static final String SCENARIO_NAME_2= "bar.testScenario2"; //$NON-NLS-1$
    private static final String SCENARIO_NAME_3= "foo.testScenario3"; //$NON-NLS-1$
    private static final String SCENARIO_NAME_4= "foo.testScenario4"; //$NON-NLS-1$
    private static final String SHORT_NAME_2= "ShortName2"; //$NON-NLS-1$
    private static final String SHORT_NAME_3= "ShortName3"; //$NON-NLS-1$
    private static final String SHORT_NAME_4= "ShortName4"; //$NON-NLS-1$

    private static final String DBLOC= "testDBs"; //$NON-NLS-1$
    //private static final String DBLOC= "net://localhost"; //$NON-NLS-1$
    private static String DBNAME;
    private static final String DBUSER= "testUser"; //$NON-NLS-1$
    private static final String DBPASSWD= "testPassword"; //$NON-NLS-1$
    
    
    protected void setUp() throws Exception {
        super.setUp();
        
        // generate a unique DB name
        DBNAME= "testDB_" + new Date().getTime(); //$NON-NLS-1$
        
        System.setProperty("eclipse.perf.dbloc", DBLOC + ";dbname=" + DBNAME + ";dbuser=" + DBUSER + ";dbpasswd=" + DBPASSWD); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        System.setProperty("eclipse.perf.config", CONFIG+"=test;"+BUILD+"=b0001;jvm=sun142"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        System.setProperty("eclipse.perf.assertAgainst", BUILD+"=base"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testPropertyGetters() {
        assertEquals(DBLOC, PerformanceTestPlugin.getDBLocation());
        assertEquals(DBNAME, PerformanceTestPlugin.getDBName());
        assertEquals(DBUSER, PerformanceTestPlugin.getDBUser());
        assertEquals(DBPASSWD, PerformanceTestPlugin.getDBPassword());
        
        assertEquals("|"+BUILD+"=b0001||"+CONFIG+"=test||jvm=sun142|", PerformanceTestPlugin.getVariations().toExactMatchString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("|"+BUILD+"=base||"+CONFIG+"=test||jvm=sun142|", PerformanceTestPlugin.getAssertAgainst().toExactMatchString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    
    public void testAssertPerformance() throws SQLException {

        Performance perf= Performance.getDefault();
        
        // set the variation for the reference data
        System.setProperty("eclipse.perf.config", CONFIG+"=test;"+BUILD+"=ref"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // store a reference value
        TestPerformanceMeter pm1= new TestPerformanceMeter(SCENARIO_NAME_0);
		pm1.addPair(InternalDimensions.CPU_TIME, 100, 1000);
		pm1.addPair(InternalDimensions.WORKING_SET, 1000, 2000);

		pm1.start();
		pm1.stop();
		pm1.commit();
		pm1.dispose();
		
		String build= "001"; //$NON-NLS-1$
        // set the variation for the this run
        System.setProperty("eclipse.perf.config", CONFIG+"=test;"+BUILD+"="+build); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        // assert that this run's values are compared against reference data
        System.setProperty("eclipse.perf.assertAgainst", BUILD+"=ref"); //$NON-NLS-1$ //$NON-NLS-2$
		
        // store a reference value
        TestPerformanceMeter pm2= new TestPerformanceMeter(SCENARIO_NAME_0);
		pm2.addPair(InternalDimensions.CPU_TIME, 100, 1100);
		pm2.addPair(InternalDimensions.WORKING_SET, 1000, 2000);
		
		pm2.start();
		pm2.stop();
		pm2.commit();
		boolean failed= false;
		try {
            perf.assertPerformanceInRelativeBand(pm2, InternalDimensions.CPU_TIME, -5, +5);
        } catch (AssertionFailedError e) {
            failed= true;
        }
		pm2.dispose();
		
		if (!failed) {
			// check in DB			
			Variations v= new Variations();
			v.put(CONFIG, "%"); //$NON-NLS-1$
			v.put(BUILD, build);
	        Scenario[] scenarios= DB.queryScenarios(v, SCENARIO_NAME_0, CONFIG, null);
	        if (scenarios != null && scenarios.length > 0) {
	        		Scenario s= scenarios[0];
	        		String[] failureMessages= s.getFailureMessages();
	        		if (failureMessages.length == 1) {
	        			String mesg= failureMessages[0];
	        			if (mesg != null && mesg.equals("Performance criteria not met when compared to '{b=ref, c=test}':\nCPU Time: 1 s is not within [95%, 105%] of 900 ms")) //$NON-NLS-1$
	        				failed= true;
	        		}
	        }
		}
        assertTrue(failed);
    }

    public void testBasicDBFunctionality() {
        
        Performance perf= Performance.getDefault();
        
        TestPerformanceMeter pm1= new TestPerformanceMeter(SCENARIO_NAME_1);
		pm1.addPair(InternalDimensions.CPU_TIME, 100, 1000);
		pm1.addPair(InternalDimensions.WORKING_SET, 1000, 2000);
		pm1.start();
		pm1.stop();
		pm1.commit();
		pm1.dispose();
		
		TestPerformanceMeter pm2= new TestPerformanceMeter(SCENARIO_NAME_2);
		pm2.addPair(InternalDimensions.CPU_TIME, 100, 1000);
		pm2.addPair(InternalDimensions.WORKING_SET, 1000, 2000);
		perf.tagAsGlobalSummary(pm2, SHORT_NAME_2, new Dimension[] { Dimension.CPU_TIME, Dimension.USED_JAVA_HEAP } );
		pm2.start();
		pm2.stop();
		pm2.commit();
		pm2.dispose();

		TestPerformanceMeter pm3= new TestPerformanceMeter(SCENARIO_NAME_3);
		pm3.addPair(InternalDimensions.CPU_TIME, 100, 1000);
		pm3.addPair(InternalDimensions.WORKING_SET, 1000, 2000);
		perf.tagAsGlobalSummary(pm3, SHORT_NAME_3, Dimension.CPU_TIME);
		pm3.start();
		pm3.stop();
		pm3.commit();
		pm3.dispose();

		TestPerformanceMeter pm4= new TestPerformanceMeter(SCENARIO_NAME_4);
		pm4.addPair(InternalDimensions.CPU_TIME, 100, 1000);
		pm4.addPair(InternalDimensions.WORKING_SET, 1000, 2000);
		perf.tagAsSummary(pm4, SHORT_NAME_4, Dimension.USED_JAVA_HEAP);
		pm4.start();
		pm4.stop();
		pm4.commit();
		pm4.dispose();

		//
		
		Variations v= new Variations();
		v.put(CONFIG, "test"); //$NON-NLS-1$
		v.put(BUILD, "b0001"); //$NON-NLS-1$
		v.put("jvm", "sun142"); //$NON-NLS-1$ //$NON-NLS-2$
		DataPoint[] points= DB.queryDataPoints(v, SCENARIO_NAME_1, null);
		assertEquals(1, points.length);
		
		DataPoint dp= points[0];
		Dim[] dimensions= dp.getDimensions();
		assertEquals(2, dimensions.length);
		
		Scalar s1= dp.getScalar(InternalDimensions.CPU_TIME);
		assertNotNull(s1);
		assertEquals(900, s1.getMagnitude());

		Scalar s2= dp.getScalar(InternalDimensions.WORKING_SET);
		assertNotNull(s2);
		assertEquals(1000, s2.getMagnitude());

		//
		Set dims= new HashSet();
		dims.add(InternalDimensions.WORKING_SET);
		points= DB.queryDataPoints(v, SCENARIO_NAME_1, dims);
		assertEquals(1, points.length);
		dimensions= points[0].getDimensions();
		assertEquals(1, dimensions.length);
		Scalar s= points[0].getScalar(InternalDimensions.WORKING_SET);
		assertNotNull(s);	
		assertEquals(1000, s.getMagnitude());
		
		//
		List buildNames= new ArrayList();
		Variations v2= new Variations();
		v2.put(CONFIG, "%"); //$NON-NLS-1$
		v2.put(BUILD, "b%"); //$NON-NLS-1$
		DB.queryDistinctValues(buildNames, BUILD, v2, "%"); //$NON-NLS-1$
		assertEquals(1, buildNames.size());
		assertEquals("b0001", buildNames.get(0)); //$NON-NLS-1$
		
	    SummaryEntry[] fps= DB.querySummaries(PerformanceTestPlugin.getVariations(), null);
	    assertEquals(3, fps.length);
	    
	    assertEquals(SCENARIO_NAME_2, fps[0].scenarioName);
	    assertEquals(SHORT_NAME_2, fps[0].shortName);
	    assertEquals(Dimension.USED_JAVA_HEAP, fps[0].dimension);

	    assertEquals(SCENARIO_NAME_2, fps[1].scenarioName);
	    assertEquals(SHORT_NAME_2, fps[1].shortName);
	    assertEquals(Dimension.CPU_TIME, fps[1].dimension);

	    assertEquals(SCENARIO_NAME_3, fps[2].scenarioName);
	    assertEquals(SHORT_NAME_3, fps[2].shortName);
	    assertEquals(Dimension.CPU_TIME, fps[2].dimension);
	    
	    
	    SummaryEntry[] fps2= DB.querySummaries(PerformanceTestPlugin.getVariations(), "foo.%"); //$NON-NLS-1$
	    assertEquals(2, fps2.length);
	    
	    assertEquals(SCENARIO_NAME_3, fps2[0].scenarioName);
	    assertEquals(SHORT_NAME_3, fps2[0].shortName);
	    assertEquals(Dimension.CPU_TIME, fps2[0].dimension);

	    assertEquals(SCENARIO_NAME_4, fps2[1].scenarioName);
	    assertEquals(SHORT_NAME_4, fps2[1].shortName);
	    assertEquals(Dimension.USED_JAVA_HEAP, fps2[1].dimension);
    }
    
}
