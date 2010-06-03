/*******************************************************************************
 * Copyright (c) 2007, 2010 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Michael Scharf (Wind River) - initial API and implementation
 * Anton Leherbauer (Wind River) - [294468] Fix scroller and text line rendering
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.textcanvas;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * A Grid based Canvas. The canvas has rows and columns.
 * CellPainting is done with the abstract method drawCell
 */
abstract public class GridCanvas extends VirtualCanvas {
	/** width of a cell */
	private int fCellWidth;
	/** height of a cell */
	private int fCellHeight;

	public GridCanvas(Composite parent, int style) {
		super(parent, style);
		addListener(SWT.MouseWheel, new Listener() {
			public void handleEvent(Event event) {
				if(getVerticalBar().isVisible()) {
					int delta=-fCellHeight*5;
					if(event.count<0)
						delta=-delta;
					scrollYDelta(delta);
				}
				event.doit=false;
			}
		});

	}

	/** template method paint.
	 * iterates over all cells in the clipping rectangle and paints them.
	 */
	protected void paint(GC gc) {
		Rectangle clipping=gc.getClipping();
		if(clipping.width==0 || clipping.height==0)
			return;
		Rectangle clientArea= getScreenRectInVirtualSpace();
		// Beginning coordinates
		int xOffset=clientArea.x;
		int yOffset=clientArea.y;
		int colFirst=virtualXToCell(xOffset+clipping.x);
		if(colFirst>getCols())
			colFirst=getCols();
		else if (colFirst < 0) {
			colFirst = 0;
		}
		int rowFirst=virtualYToCell(yOffset+clipping.y);
		// End coordinates
		int colLast=virtualXToCell(xOffset+clipping.x+clipping.width+fCellWidth);
		if(colLast>getCols())
			colLast=getCols();
		int rowLast=virtualYToCell(yOffset+clipping.y+clipping.height+fCellHeight);
		if(rowLast>getRows())
			rowLast=getRows();
		// System.out.println(rowFirst+"->"+rowLast+" "+System.currentTimeMillis());
		// draw the cells
		for(int row=rowFirst;row<=rowLast;row++) {
			int cx=colFirst*fCellWidth-xOffset;
			int cy=row*fCellHeight-yOffset;
			drawLine(gc,row,cx,cy,colFirst,colLast);
		}
		paintUnoccupiedSpace(gc,clipping);
	}
	/**
	 * @param gc
	 * @param row the line to draw
	 * @param x coordinate on screen
	 * @param y coordinate on screen
	 * @param colFirst first column to draw
	 * @param colLast last column to draw
	 */
	abstract void drawLine(GC gc, int row, int x, int y, int colFirst, int colLast);

	abstract protected int getRows();
	abstract protected int getCols();

	protected void setCellWidth(int cellWidth) {
		fCellWidth = cellWidth;
		getHorizontalBar().setIncrement(fCellWidth);
	}

	public int getCellWidth() {
		return fCellWidth;
	}

	protected void setCellHeight(int cellHeight) {
		fCellHeight = cellHeight;
		getVerticalBar().setIncrement(fCellHeight);
	}

	public int getCellHeight() {
		return fCellHeight;
	}

	int virtualXToCell(int x) {
		return x/fCellWidth;
	}

	int virtualYToCell(int y) {
		return y/fCellHeight;
	}

	protected Point screenPointToCell(int x, int y) {
		x=screenXtoVirtual(x)/fCellWidth;
		y=screenYtoVirtual(y)/fCellHeight;
		return new Point(x,y);
	}

	Point screenPointToCell(Point point) {
		return screenPointToCell(point.x,point.y);
	}

	protected Point cellToOriginOnScreen(int x, int y) {
		x=virtualXtoScreen(fCellWidth*x);
		y=virtualYtoScreen(fCellHeight*y);
		return new Point(x,y);
	}

	Point cellToOriginOnScreen(Point cell) {
		return cellToOriginOnScreen(cell.x,cell.y);
	}

	Rectangle getCellScreenRect(Point cell) {
		return getCellScreenRect(cell.x,cell.y);
	}

	Rectangle getCellScreenRect(int x, int y) {
		x=fCellWidth*virtualXtoScreen(x);
		y=fCellHeight*virtualYtoScreen(y);
		return new Rectangle(x,y,fCellWidth,fCellHeight);
	}

	protected Rectangle getCellVirtualRect(Point cell) {
		return getCellVirtualRect(cell.x,cell.y);
	}

	Rectangle getCellVirtualRect(int x, int y) {
		x=fCellWidth*x;
		y=fCellHeight*y;
		return new Rectangle(x,y,fCellWidth,fCellHeight);
	}
	protected void viewRectangleChanged(int x, int y, int width, int height) {
		int cellX=virtualXToCell(x);
		int cellY=virtualYToCell(y);
		// End coordinates
		int xE=virtualXToCell(x+width);
//		if(xE>getCols())
//			xE=getCols();
		int yE=virtualYToCell(y+height);
//		if(yE>getRows())
//			yE=getRows();
		visibleCellRectangleChanged(cellX,cellY,xE-cellX,yE-cellY);
	}
	
	/**
	 * Called when the viewed part has changed.
	 * Override when you need this information....
	 * Is only called if the values change (well, almost)
	 * @param x origin of visible cells
	 * @param y origin of visible cells
	 * @param width number of cells visible in x direction
	 * @param height number of cells visible in y direction
	 */
	protected void visibleCellRectangleChanged(int x, int y, int width, int height) {
	}
	
	protected void setVirtualExtend(int width, int height) {
		int cellHeight = getCellHeight();
		if (cellHeight > 0) {
			height -= height % cellHeight;
		}
		super.setVirtualExtend(width, height);
	}
	
	protected void setVirtualOrigin(int x, int y) {
		int cellHeight = getCellHeight();
		if (cellHeight > 0) {
			int remainder = y % cellHeight;
			if (remainder < 0) {
				y -= (cellHeight + remainder);
			} else {
				y -= remainder;
			}
		}
		super.setVirtualOrigin(x, y);
	}
	
	protected void scrollY(ScrollBar vBar) {
		int vSelection = vBar.getSelection ();
		Rectangle bounds = getVirtualBounds();
		int y = -vSelection;
		int cellHeight = getCellHeight();
		if (cellHeight > 0) {
			int remainder = y % cellHeight;
			if (remainder < 0) {
				y -= (cellHeight + remainder);
			} else {
				y -= remainder;
			}
		}
		int deltaY = y - bounds.y;
		if(deltaY!=0) {
			scrollSmart(0,deltaY);
			setVirtualOrigin(bounds.x, bounds.y += deltaY);
		}
		if (-bounds.y + getRows() * getCellHeight() >= bounds.height) {
			// scrolled to bottom - need to redraw bottom area
			Rectangle clientRect = getClientArea();
			redraw(0, clientRect.height - fCellHeight, clientRect.width, fCellHeight, false);
		}
	}
}
