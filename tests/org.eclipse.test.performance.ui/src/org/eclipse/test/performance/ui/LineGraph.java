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

public class LineGraph {

    StringBuffer fAreaBuffer;

    private static class GraphItem {

        String title;
        String description=null;
        double value;
        Color color;
        boolean displayDescription=false;

        GraphItem(String title, String description,double value, Color color,boolean display) {
        	this(title, description, value, color);
        	this.displayDescription=display;
        }

        GraphItem(String title, String description, double value, Color color) {
            this.title= title;
            this.value= value;
            this.color= color;
            this.description= description;
        }

        Point getSize(GC g) {
            Point e1= g.stringExtent(this.description);
            Point e2= g.stringExtent(this.title);
            return new Point(Math.max(e1.x, e2.x), e1.y+e2.y);
        }
    }

    static final int PADDING= 15;


    String fTitle;
    List fItems;
    Dim fDimension;


    public LineGraph(String title, Dim dim) {
        this.fTitle= title;
        this.fItems= new ArrayList();
        this.fDimension= dim;
    }

    public void paint(Image im) {

        Rectangle bounds= im.getBounds();

        GC g= new GC(im);

        Point ee= g.stringExtent(this.fTitle);
        int titleHeight= ee.y;

        double maxItem= getMaxItem();
        double minItem= getMinItem();

        int max= (int) (Math.ceil(maxItem * (maxItem < 0 ? 0.9 : 1.2)));
        int min= (int) (Math.floor(minItem * (minItem < 0 ? 1.2 : 0.9)));

        String smin= this.fDimension.getDisplayValue(min);
        Point emin= g.stringExtent(smin);

        String smax= this.fDimension.getDisplayValue(max);
        Point emax= g.stringExtent(smax);

        int labelWidth= Math.max(emin.x, emax.x) + 2;

        int top= PADDING;
        int bottom= bounds.height - titleHeight - PADDING;
        int left= PADDING + labelWidth;

        GraphItem lastItem= (GraphItem) this.fItems.get(this.fItems.size()-1);
        int right= bounds.width - lastItem.getSize(g).x - PADDING/2;

        // draw the title
        //g.drawString(fTitle, (bounds.width - titleWidth) / 2, titleHeight, true);

        // draw the max and min values
        g.drawString(smin, PADDING/2+labelWidth-emin.x, bottom-titleHeight, true);
        g.drawString(smax, PADDING/2+labelWidth-emax.x, top, true);

        // draw the vertical and horizontal lines
        g.drawLine(left, top, left, bottom);
        g.drawLine(left, bottom, right, bottom);

        Color oldbg= g.getBackground();
        Color oldfg= g.getForeground();

        int n= this.fItems.size();
        int xincrement= n > 1 ? (right-left) / (n-1) : 0;

        int graduations= max - min;
        if (graduations == 0)
            graduations= 1;

        int lastx= 0;
        int lasty= 0;

        int xposition= left;

        for (int i= 0; i < n; i++) {
            GraphItem thisItem= (GraphItem) this.fItems.get(i);

            int yposition= (int) (bottom - (((thisItem.value-min) * (bottom-top)) / graduations));

            if (i > 0)	// don't draw for first segment
                g.drawLine(lastx, lasty, xposition, yposition);

            g.setBackground(thisItem.color);
            g.setForeground(thisItem.color);
            g.fillOval(xposition-2, yposition-2, 5, 5);

            if (this.fAreaBuffer == null)
                this.fAreaBuffer= new StringBuffer();

            this.fAreaBuffer.append("\r<area shape=\"CIRCLE\" coords=\""+(xposition-2)+','+(yposition-2)+','+5+" alt=\""+ thisItem.title+": "+thisItem.description+"\""+ " title=\""+ thisItem.title+": "+thisItem.description+"\">");


            int shift;
            if (i > 0 && yposition < lasty)
                shift= 3;	 // below dot
            else
                shift= -(2*titleHeight+3);	// above dot
            if (thisItem.displayDescription){
            	g.drawString(thisItem.title, xposition+2, yposition+shift, true);
            	g.drawString(thisItem.description, xposition+2, yposition+shift+titleHeight, true);
            }
            g.setBackground(oldbg);
            g.setForeground(oldfg);

            lastx= xposition;
            lasty= yposition;
            xposition+= xincrement;
        }

        g.dispose();
    }

    public void addItem(String name, String description, double value, Color col) {
    	addItem(name, description, value, col,false);
    }

    public void addItem(String name, String description, double value, Color col, boolean display) {
        this.fItems.add(new GraphItem(name, description, value, col,display));
    }

    public double getMaxItem() {
        double maxItem= 0;
        for (int i= 0; i < this.fItems.size(); i++) {
            GraphItem graphItem= (GraphItem) this.fItems.get(i);
            if (graphItem.value > maxItem)
                maxItem= graphItem.value;
        }
        if (maxItem == 0)
            return 1;
        return maxItem;
    }

    public double getMinItem() {
        double minItem= getMaxItem();
        for (int i= 0; i < this.fItems.size(); i++) {
            GraphItem graphItem= (GraphItem) this.fItems.get(i);
            if (graphItem.value < minItem)
                minItem= graphItem.value;
        }
        if (minItem == 0)
            return -1;
        return minItem;
    }
    public String getAreas() {
        if (this.fAreaBuffer != null) {
            String s= this.fAreaBuffer.toString();
            this.fAreaBuffer= null;
            return s;
        }
        return null;
    }
}
