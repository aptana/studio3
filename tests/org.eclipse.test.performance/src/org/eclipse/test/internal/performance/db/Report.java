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
package org.eclipse.test.internal.performance.db;

import java.io.PrintStream;
import java.util.HashMap;

public class Report {
    
    private static final String LEFT= "l";  //$NON-NLS-1$
    private static final String RIGHT= "r";  //$NON-NLS-1$
    
    private int fGap;
    private int fColumn;
    private int fRow;
    private int fRows;
    private HashMap fContent= new HashMap();
    private HashMap fWidths= new HashMap();
    private HashMap fAlignment= new HashMap();
    
    public Report(int gap) {
        fGap= gap;
    }
    
    public void addCell(String value) {
        setCell(fColumn, fRow, value, LEFT);
        fColumn++;
    }
    
    public void addCellRight(String value) {
        setCell(fColumn, fRow, value, RIGHT);
        fColumn++;
    }
    
    public void nextRow() {
        fRow++;
        fColumn= 0;
    }
    
    private void setCell(int x, int y, String value, String align) {
        fContent.put(x + "/" + y, value); //$NON-NLS-1$
        fAlignment.put(x + "/" + y, align); //$NON-NLS-1$
        Integer w= (Integer) fWidths.get(Integer.toString(x));
        if (w == null)
            w= new Integer(value.length());
        else
            w= new Integer(Math.max(w.intValue(), value.length()));
        fWidths.put(Integer.toString(x), w);
        fRows= Math.max(fRows, y+1);
    }
    
    private String getCell(int x, int y) {
        return (String) fContent.get(x + "/" + y); //$NON-NLS-1$
    }
    
    public void print(PrintStream ps) {
        int n= fWidths.size();
        for (int y= 0; y < fRows; y++) {
            for (int x= 0; x < n; x++) {
                Integer w= (Integer) fWidths.get(Integer.toString(x));
                int ww= w.intValue();
                String s= getCell(x, y);
                if (s == null)
                    s= ""; //$NON-NLS-1$
                
                if (x > 0)
                    for (int g= 0; g < fGap; g++)
                        ps.print(' ');
   
                int www= ww-s.length();
                String align= (String) fAlignment.get(x + "/" + y); //$NON-NLS-1$
                if (LEFT.equalsIgnoreCase(align))
                    ps.print(s);
                for (int l= 0; l < www; l++)
                    ps.print(' ');
                if (RIGHT.equalsIgnoreCase(align))
                    ps.print(s);
            }
            ps.println();
        }
    }
}
