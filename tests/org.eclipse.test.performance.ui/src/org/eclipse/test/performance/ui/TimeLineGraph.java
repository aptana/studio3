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

import java.util.*;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.test.internal.performance.data.Dim;

public class TimeLineGraph extends LineGraph{

    Hashtable fItemGroups;

    public TimeLineGraph (String title, Dim dim) {
        super(title, dim);
        this.fItemGroups=new Hashtable();
    }

    public void paint(Image im) {

        Rectangle bounds= im.getBounds();

        GC g= new GC(im);

        Point ee= g.stringExtent(this.fTitle);
        int titleHeight= ee.y;

        double maxItem= getMaxItem();
        double minItem= getMinItem();

        int max= (int) (Math.ceil(maxItem * (maxItem < 0 ? 0.8 : 1.2)));
        int min= (int) (Math.floor(minItem * (minItem < 0 ? 1.2 : 0.8)));

        String smin= this.fDimension.getDisplayValue(min);
        Point emin= g.stringExtent(smin);

        String smax= this.fDimension.getDisplayValue(max);
        Point emax= g.stringExtent(smax);

        int labelWidth= Math.max(emin.x, emax.x) + 2;

        int top= PADDING;
        int bottom= bounds.height - titleHeight - PADDING;
        int left= PADDING + labelWidth;

        //getMostRecent
        TimeLineGraphItem lastItem= getMostRecent(this.fItemGroups);
        int right=bounds.width - PADDING/2;
        if (lastItem!=null)
        	right= bounds.width - lastItem.getSize(g).x - PADDING/2;

        // draw the max and min values
        g.drawString(smin, PADDING/2+labelWidth-emin.x, bottom-titleHeight, true);
        g.drawString(smax, PADDING/2+labelWidth-emax.x, top, true);
        g.drawString("TIME (not drawn to scale)", (right-left)/3+PADDING+titleHeight,bottom-PADDING+(titleHeight*2), true);

        // draw the vertical and horizontal lines
        g.drawLine(left, top, left, bottom);
        g.drawLine(left, bottom, right, bottom);

        Color oldbg= g.getBackground();
        Color oldfg= g.getForeground();

        setCoordinates(right-left,left,bottom-top,bottom,max-min);

        Enumeration _enum=this.fItemGroups.elements();
        Comparator comparator=new TimeLineGraphItem.GraphItemComparator();

        while (_enum.hasMoreElements()) {
 			List items = (List) _enum.nextElement();
			Object[] fItemsArray=items.toArray();
			Arrays.sort(fItemsArray,comparator);
			int lastx = 0;
			int lasty = 0;

			int n = fItemsArray.length;

			for (int i = 0; i < n; i++) {
				TimeLineGraphItem thisItem = (TimeLineGraphItem) fItemsArray[i];

				int yposition = thisItem.y;
				int xposition = thisItem.x;
				g.setLineWidth(1);

				g.setBackground(thisItem.color);
				g.setForeground(thisItem.color);

				if (thisItem.drawAsBaseline){
					g.setLineWidth(0);
					g.drawLine(xposition, yposition,right,yposition);
					g.drawLine(left,yposition,xposition, yposition);
    		    }

				if (i > 0) // don't draw for first segment
					g.drawLine(lastx, lasty, xposition, yposition);

				g.setBackground(thisItem.color);
				g.setForeground(thisItem.color);
			//	g.fillOval(xposition - 2, yposition - 2, 6, 6);
				g.fillRectangle(xposition - 2, yposition - 2, 5, 5);

				if (thisItem.isSpecial)
					g.drawRectangle(xposition -4, yposition - 4, 8, 8);

				if (this.fAreaBuffer == null)
					this.fAreaBuffer = new StringBuffer();

				this.fAreaBuffer.append("\r<area shape=\"circle\" coords=\""
						+ (xposition - 2) + ',' + (yposition - 2) + ',' + 5
						+ " alt=\"" + thisItem.title + ": "
						+ thisItem.description + "\"" + " title=\""
						+ thisItem.title + ": " + thisItem.description + "\">");

				int shift;
				if (i > 0 && yposition < lasty)
					shift = 3; // below dot
				else
					shift = -(2 * titleHeight + 3); // above dot
				if (thisItem.displayDescription) {
					g.drawString(thisItem.title, xposition + 2, yposition
							+ shift, true);
					g.drawString(thisItem.description, xposition + 2, yposition
							+ shift + titleHeight, true);
				}
				g.setBackground(oldbg);
				g.setForeground(oldfg);

				lastx = xposition;
				lasty = yposition;
			}
		}

        g.dispose();
    }

    public void addItem(String groupName,String name, String description, double value, Color col, boolean display, long timestamp) {
    	addItem(groupName, name, description, value, col, display,	timestamp,false);
    }

    public void addItem(String groupName,String name, String description, double value, Color col, boolean display, long timestamp,boolean isSpecial) {
 		addItem(groupName, name,description, value, col, display,
 				timestamp,isSpecial,false);
	}

    public void addItem(String groupName,String name, String description, double value, Color col, boolean display, long timestamp,boolean isSpecial,boolean drawBaseline) {
      	List items = (List) this.fItemGroups.get(groupName);
  		if (this.fItemGroups.get(groupName) == null) {
  			items=new ArrayList();
  			this.fItemGroups.put(groupName, items);
  		}
  		items.add(new TimeLineGraphItem(name, description, value, col, display,
  				timestamp,isSpecial,drawBaseline));
    }

    public double getMaxItem() {
    	Enumeration _enum=this.fItemGroups.elements();
        double maxItem= 0;
    	while (_enum.hasMoreElements()) {
			List items = (List) _enum.nextElement();
			for (int i = 0; i < items.size(); i++) {
				TimeLineGraphItem graphItem = (TimeLineGraphItem) items.get(i);
				if (graphItem.value > maxItem)
					maxItem = graphItem.value;
			}
		}
        if (maxItem == 0)
            return 1;
        return maxItem;
    }

    public double getMinItem() {
       	Enumeration _enum = this.fItemGroups.elements();
		double minItem = getMaxItem();

		while (_enum.hasMoreElements()) {
			List items = (List) _enum.nextElement();
			for (int i = 0; i < items.size(); i++) {
				TimeLineGraphItem graphItem = (TimeLineGraphItem) items.get(i);
				if (graphItem.value < minItem)
					minItem = graphItem.value;
			}
		}
        if (minItem == 0)
            return -1;
        return minItem;
    }

    private TimeLineGraphItem getMostRecent(Hashtable lineGraphGroups) {
		Enumeration _enum = lineGraphGroups.elements();
		long mostRecentTimestamp = 0;
		TimeLineGraphItem mostRecentItem = null;

		while (_enum.hasMoreElements()) {
			List items = (List) _enum.nextElement();
			for (int i = 0; i < items.size(); i++) {
				if (items.size() == 1)
					return (TimeLineGraphItem) items.get(i);
				else {
					TimeLineGraphItem graphItem = (TimeLineGraphItem) items.get(i);
					if (graphItem.timestamp > mostRecentTimestamp) {
						mostRecentTimestamp = graphItem.timestamp;
						mostRecentItem = (TimeLineGraphItem) items.get(i);
					}
				}
			}
		}
		return mostRecentItem;
	}

    private void setCoordinates(int width, int xOffset, int height, int yOffset, int yValueRange){

        List mainGroup=(ArrayList)this.fItemGroups.get("main");
        List referenceGroup=(ArrayList)this.fItemGroups.get("reference");

        Comparator comparator=new TimeLineGraphItem.GraphItemComparator();

 		Object[] fItemsArray=mainGroup.toArray();
		Arrays.sort(fItemsArray,comparator);

		int n = mainGroup.size();
		int xIncrement=width/n;
		double max=getMaxItem()*1.2;
//		double min=getMinItem()*0.8;

		for (int i = 0; i < n; i++) {
			TimeLineGraphItem thisItem = (TimeLineGraphItem) fItemsArray[i];
			thisItem.setX(xOffset + (i * xIncrement));
			thisItem.setY((int)(PADDING+((max-thisItem.value) * (height)/(yValueRange))));

			}

		if (referenceGroup==null)
			return;

		n = referenceGroup.size();
		for (int i = 0; i < n; i++) {
			 TimeLineGraphItem thisItem = (TimeLineGraphItem) referenceGroup.get(i);
			 if (thisItem.timestamp==-1)
				 thisItem.setX(xOffset + (i * (width/n)));
			 else
				 setRelativeXPosition(thisItem,mainGroup);

			 thisItem.setY((int)(PADDING+((max-thisItem.value) * (height)/(yValueRange))));

		}
    }


	private void setRelativeXPosition (TimeLineGraphItem thisItem, List items){
			Comparator comparator=new TimeLineGraphItem.GraphItemComparator();
			Object[] fItemsArray=items.toArray();
			Arrays.sort(fItemsArray,comparator);

			TimeLineGraphItem closestPrecedingItem=null;
			long minimumTimeDiffPreceding=thisItem.timestamp;

			TimeLineGraphItem closestFollowingItem=null;
			long minimumTimeDiffFollowing=thisItem.timestamp;

			for (int i=0;i<fItemsArray.length;i++){
				TimeLineGraphItem anItem=(TimeLineGraphItem)fItemsArray[i];
				long timeDiff=thisItem.timestamp-anItem.timestamp;

				 if (timeDiff>0&&timeDiff<minimumTimeDiffPreceding){
					 closestPrecedingItem=anItem;
				 	minimumTimeDiffPreceding=thisItem.timestamp-anItem.timestamp;
				 }
				 if (timeDiff<=0&&Math.abs(timeDiff)<=minimumTimeDiffFollowing){
					 closestFollowingItem=anItem;
					 minimumTimeDiffFollowing=thisItem.timestamp-anItem.timestamp;
				 }
			}
			if (closestFollowingItem==null && closestPrecedingItem!=null)
				thisItem.setX(closestPrecedingItem.x);

			else if (closestFollowingItem!=null && closestPrecedingItem==null)
				thisItem.setX(closestFollowingItem.x);
			else{
				long timeRange=closestFollowingItem.timestamp-closestPrecedingItem.timestamp;

				int xRange=closestFollowingItem.x-closestPrecedingItem.x;
				double increments=(xRange*1.0)/timeRange;

				thisItem.setX((int)(Math.round((thisItem.timestamp-closestPrecedingItem.timestamp)*increments)+closestPrecedingItem.x));
			}
	}
}
