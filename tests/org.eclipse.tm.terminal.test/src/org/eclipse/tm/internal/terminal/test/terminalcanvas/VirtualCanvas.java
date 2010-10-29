/*******************************************************************************
 * Copyright (c) 2007 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Michael Scharf (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.test.terminalcanvas;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * A <code>Canvas</code> showing a virtual object.
 * Virtual: the extent of the total canvas.
 * Screen: the visible client area in the screen.
 */
public abstract class VirtualCanvas extends Canvas {

	private Rectangle fVirtualBounds = new Rectangle(0,0,0,0);
	private Rectangle fClientArea;
	private GC fPaintGC=null;
	public VirtualCanvas(Composite parent, int style) {
		super(parent, style|SWT.NO_BACKGROUND|SWT.NO_REDRAW_RESIZE);
		fPaintGC= new GC(this);
		addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {
				paint(event.gc);
			}
		});
		addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				fClientArea=getClientArea();
				updateViewRectangle();
			}
		});
		getVerticalBar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				scrollY((ScrollBar)e.widget);
				postScrollEventHandling(e);

			}

		});
		getHorizontalBar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				scrollX((ScrollBar)e.widget);
				postScrollEventHandling(e);

			}
		});
		addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				if(fPaintGC!=null){
					fPaintGC.dispose();
					fPaintGC=null;
				}
			}
			
		});
	}
	public void setAutoSelect(boolean on) {
	}
	public boolean hasAutoSelect() {
		return false;
	}
	public void doAutoSelect() {
	}
	
	/** HACK: run an event loop if the scrollbar is dragged...*/
	private void postScrollEventHandling(Event e) {
		if(true&&e.detail==SWT.DRAG) {
			// TODO check if this is always ok???
			// used to process runnables while scrolling
			// This fixes the update problems when scrolling!
			// see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=47582#5
			// TODO investigate:
			// The alternative is to call redraw on the new visible area
			// 	  redraw(expose.x, expose.y, expose.width, expose.height, true);

			while (!getDisplay().isDisposed() && getDisplay().readAndDispatch()) {	
				// do nothing here...
			}
		}
	}
	
	protected void scrollX(ScrollBar hBar) {
		int hSelection = hBar.getSelection ();
		int destX = -hSelection - fVirtualBounds.x;
		fVirtualBounds.x = -hSelection;
		scrollSmart(destX, 0);
		updateViewRectangle();
	}
	protected void scrollXDelta(int delta) {
		getHorizontalBar().setSelection(-fVirtualBounds.x+delta);
		scrollX(getHorizontalBar());
	}

	protected void scrollY(ScrollBar vBar) {
		int vSelection = vBar.getSelection ();
		int destY = -vSelection - fVirtualBounds.y;
		fVirtualBounds.y = -vSelection;
		scrollSmart(0,destY);
		updateViewRectangle();
		
	}
	protected void scrollYDelta(int delta) {
		getVerticalBar().setSelection(-fVirtualBounds.y+delta);
		scrollY(getVerticalBar());
	}


	private void scrollSmart(int deltaX, int deltaY) {
		Rectangle rect = getBounds();
		scroll (deltaX, deltaY, 0, 0, rect.width, rect.height, false);
	}

	protected void revealRect(Rectangle rect) {
		Rectangle visibleRect=getScreenRectInVirtualSpace();
		// scroll the X part
		int deltaX=0;
		if(rect.x<visibleRect.x) {
			deltaX=rect.x-visibleRect.x;
		} else if(visibleRect.x+visibleRect.width<rect.x+rect.width){
			deltaX=(rect.x+rect.width)-(visibleRect.x+visibleRect.width);
		}
		if(deltaX!=0) {
			getHorizontalBar().setSelection(-fVirtualBounds.x+deltaX);
			scrollX(getHorizontalBar());
		}
	
		// scroll the Y part
		int deltaY=0;
		if(rect.y<visibleRect.y){
			deltaY=rect.y-visibleRect.y;
		} else if(visibleRect.y+visibleRect.height<rect.y+rect.height){
			deltaY=(rect.y+rect.height)-(visibleRect.y+visibleRect.height);
			
		}
		if(deltaY!=0) {
			getVerticalBar().setSelection(-fVirtualBounds.y+deltaY);
			scrollY(getVerticalBar());
		}
	}

	protected void repaint(Rectangle r) {
		if (fPaintGC!=null) {
			if(inClipping(r,fClientArea)) {
				fPaintGC.setClipping(r);
				paint(fPaintGC);
			}
		}
	}

	/**
	 * @param gc
	 */
	abstract protected void paint(GC gc);
//	protected Color getBackgroundColor() {
//		return getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
//	}
	protected void paintUnoccupiedSpace(GC gc, Rectangle clipping) {
		int width=fVirtualBounds.width;
		int height=fVirtualBounds.height;
		int marginWidth = (clipping.x+clipping.width) - width;
		int marginHeight = (clipping.y+clipping.height) - height;
		if(marginWidth>0||marginHeight>0){
			Color bg=getBackground();
			gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			if (marginWidth > 0) {
				gc.fillRectangle (width, clipping.y, marginWidth, clipping.height);
			}
			if (marginHeight > 0) {
				gc.fillRectangle (clipping.x, height, clipping.width, marginHeight);
			}
			gc.setBackground(bg);
		}
	}
	/**
	 * @private
	 */
	protected boolean inClipping(Rectangle clipping, Rectangle r) {
		// TODO check if this is OK in all cases (the <=!)
		// 
		if(r.x+r.width<=clipping.x)
			return false;
		if(clipping.x+clipping.width<=r.x)
			return false;
		if(r.y+r.height<=clipping.y)
			return false;
		if(clipping.y+clipping.height<=r.y)
			return false;
		
		return true;
	}
	/**
	 * @return the screen rect in virtual space (starting with (0,0))
	 * of the visible screen. (x,y>=0)
	 */
	protected Rectangle getScreenRectInVirtualSpace() {
		return new Rectangle(fClientArea.x-fVirtualBounds.x,fClientArea.y-fVirtualBounds.y,fClientArea.width,fClientArea.height);
	}
	/**
	 * @return the rect in virtual space (starting with (0,0))
	 * of the visible screen. (x,y>=0)
	 */
	protected Rectangle getRectInVirtualSpace(Rectangle r) {
		return new Rectangle(r.x-fVirtualBounds.x,r.y-fVirtualBounds.y,r.width,r.height);
	}
	
	/**
	 * Sets the extend of the virtual dieplay ares
	 * @param width
	 * @param height
	 */
	protected void setVirtualExtend(int width, int height) {
		fVirtualBounds.width=width;
		fVirtualBounds.height=height;
		updateScrollbars();
		updateViewRectangle();
	}
	/**
	 * sets the scrolling origin. Also sets the scrollbars.
	 * Does NOT redraw!
	 * Use negative values (move the virtual origin to the top left
	 * to see something in the screen (which is located at (0,0))
	 * @param x
	 * @param y
	 */
	protected void setVirtualOrigin(int x, int y) {
		fVirtualBounds.x=x;
		fVirtualBounds.y=y;
		getHorizontalBar().setSelection(x);
		getVerticalBar().setSelection(y);
		updateViewRectangle();
	}

	/**
	 * @param x
	 * @return the virtual coordinate in scree space
	 */
	protected int virtualXtoScreen(int x) {
		return x+fVirtualBounds.x;
	}
	protected int virtualYtoScreen(int y) {
		return y+fVirtualBounds.y;
	}
	protected int screenXtoVirtual(int x) {
		return x-fVirtualBounds.x;
	}
	protected int screenYtoVirtual(int y) {
		return y-fVirtualBounds.y;
	}
	/** called when the viewed part is changing */
	private Rectangle fViewRectangle=new Rectangle(0,0,0,0);
	void updateViewRectangle() {
		if(
				fViewRectangle.x==-fVirtualBounds.x 
				&& fViewRectangle.y==-fVirtualBounds.y
				&& fViewRectangle.width==fClientArea.width
				&& fViewRectangle.height==fClientArea.height
			)
			return;
		fViewRectangle.x=-fVirtualBounds.x;
		fViewRectangle.y=-fVirtualBounds.y;
		fViewRectangle.width=fClientArea.width;
		fViewRectangle.height=fClientArea.height;
		viewRectangleChanged(fViewRectangle.x,fViewRectangle.y,fViewRectangle.width,fViewRectangle.height);
	}
	protected Rectangle getViewRectangle() {
		return fViewRectangle;
	}
	/**
	 * Called when the viewed part has changed.
	 * Override when you need this information....
	 * Is only called if the values change!
	 * @param x visible in virtual space
	 * @param y visible in virtual space
	 * @param width
	 * @param height
	 */
	protected void viewRectangleChanged(int x, int y, int width, int height) {
//		System.out.println(x+" "+y+" "+width+" "+height);
	}
	/**
	 * @private
	 */
	private void updateScrollbars() {
		Point size= getSize();
		Rectangle clientArea= getClientArea();
	
		ScrollBar horizontal= getHorizontalBar();
		if (fVirtualBounds.width <= clientArea.width) {
			// TODO IMPORTANT in ScrollBar.setVisible comment out the line
			// that checks 'isvisible' and returns (at the beginning)
			horizontal.setVisible(false);
			horizontal.setSelection(0);
		} else {
			horizontal.setPageIncrement(clientArea.width - horizontal.getIncrement());
			int max= fVirtualBounds.width + (size.x - clientArea.width);
			horizontal.setMaximum(max);
			horizontal.setThumb(size.x > max ? max : size.x);
			horizontal.setVisible(true);
		}
	
		ScrollBar vertical= getVerticalBar();
		if (fVirtualBounds.height <= clientArea.height) {
			vertical.setVisible(false);
			vertical.setSelection(0);
		} else {
			vertical.setPageIncrement(clientArea.height - vertical.getIncrement());
			int max= fVirtualBounds.height + (size.y - clientArea.height);
			vertical.setMaximum(max);
			vertical.setThumb(size.y > max ? max : size.y);
			vertical.setVisible(true);
		}
	}
}

