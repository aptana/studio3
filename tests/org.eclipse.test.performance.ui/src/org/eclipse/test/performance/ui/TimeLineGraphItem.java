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
package org.eclipse.test.performance.ui;

import java.util.Comparator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class TimeLineGraphItem {


    String title;
    String description=null;
    double value;
    Color color;
    boolean displayDescription=false;
    long timestamp;
    boolean isSpecial=false;
    boolean drawAsBaseline=false;
    int x;
    int y;

    TimeLineGraphItem(String title, String description,double value,Color color,boolean display, long timestamp, boolean isSpecial,boolean isBaseline) {
    	this(title, description, value, color,display, timestamp,isSpecial);
    	this.drawAsBaseline=isBaseline;
    }

    TimeLineGraphItem(String title, String description,double value,Color color,boolean display, long timestamp, boolean isSpecial) {
    	this(title, description, value, color,display, timestamp);
    	this.isSpecial=isSpecial;
    }

    TimeLineGraphItem(String title, String description,double value,Color color,boolean display, long timestamp) {
    	this(title, description, value, color,timestamp);
    	this.displayDescription=display;
    }

    TimeLineGraphItem(String title, String description, double value, Color color,long timestamp) {
        this.title= title;
        this.value= value;
        this.color= color;
        this.description= description;
        this.timestamp=timestamp;
    }

    Point getSize(GC g) {
        Point e1= g.stringExtent(this.description);
        Point e2= g.stringExtent(this.title);
        return new Point(Math.max(e1.x, e2.x), e1.y+e2.y);
    }

    public static class GraphItemComparator implements Comparator{
		public int compare(Object o1, Object o2) {
			long ts1=((TimeLineGraphItem)o1).timestamp;
			long ts2=((TimeLineGraphItem)o2).timestamp;

			if (ts1>ts2)
				return 1;
			if (ts1<ts2)
				return -1;

			return 0;
		}
    }

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}


}
