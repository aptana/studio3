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


import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.tm.internal.terminal.control.impl.TerminalPlugin;

/**
 * A <code>Canvas</code> showing a virtual object.
 * Virtual: the extent of the total canvas.
 * Screen: the visible client area in the screen.
 */
public abstract class VirtualCanvas extends Canvas {

	private final Rectangle fVirtualBounds = new Rectangle(0,0,0,0);
	private Rectangle fClientArea;
	/**
	 * prevent infinite loop in {@link #updateScrollbars()}
	 */
	private boolean fInUpdateScrollbars;
	private static boolean fInUpdateScrollbarsLogged;
	
	public VirtualCanvas(Composite parent, int style) {
		super(parent, style|SWT.NO_BACKGROUND|SWT.NO_REDRAW_RESIZE);
		fClientArea=getClientArea();
		addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {
				paint(event.gc);
			}
		});
		addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				fClientArea=getClientArea();
				onResize();
			}
		});
		getVerticalBar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				scrollY((ScrollBar)e.widget);

			}

		});
		getHorizontalBar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				scrollX((ScrollBar)e.widget);

			}
		});
	}
	protected void onResize() {
		updateViewRectangle();
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
		if(destY!=0) {
			fVirtualBounds.y = -vSelection;
			scrollSmart(0,destY);
			updateViewRectangle();
		}
		
	}
	protected void scrollYDelta(int delta) {
		getVerticalBar().setSelection(-fVirtualBounds.y+delta);
		scrollY(getVerticalBar());
	}


	protected void scrollSmart(int deltaX, int deltaY) {
		if (deltaX != 0 || deltaY != 0) {
			Rectangle rect = getBounds();
			scroll (deltaX, deltaY, 0, 0, rect.width, rect.height, false);
		}
	}

	/**
	 * @param rect in virtual space
	 */
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
		if(isDisposed())
			return;
		if(inClipping(r,fClientArea)) {
			redraw(r.x, r.y, r.width, r.height, true);
			update();
		}
	}

	/**
	 * Paint the virtual canvas.
	 * Override to implement actual paint method.
	 * @param gc graphics context to paint in
	 */
	abstract protected void paint(GC gc);
	protected Color getTerminalBackgroundColor() {
//		return getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		return getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	}
	protected void paintUnoccupiedSpace(GC gc, Rectangle clipping) {
		int width=fVirtualBounds.width + fVirtualBounds.x;
		int height=fVirtualBounds.height + fVirtualBounds.y;
		int marginWidth = (clipping.x+clipping.width) - width;
		int marginHeight = (clipping.y+clipping.height) - height;
		if(marginWidth>0||marginHeight>0){
			Color bg=getBackground();
			gc.setBackground(getTerminalBackgroundColor());
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
		Rectangle r= new Rectangle(fClientArea.x-fVirtualBounds.x,fClientArea.y-fVirtualBounds.y,fClientArea.width,fClientArea.height);
		return r;
	}
	/**
	 * @return the rect in virtual space (starting with (0,0))
	 * of the visible screen. (x,y>=0)
	 */
	protected Rectangle getRectInVirtualSpace(Rectangle r) {
		return new Rectangle(r.x-fVirtualBounds.x,r.y-fVirtualBounds.y,r.width,r.height);
	}
	
	/**
	 * Sets the extent of the virtual display area
	 * @param width width of the display area
	 * @param height height of the display area
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
		if (fVirtualBounds.x != x || fVirtualBounds.y != y) {
			fVirtualBounds.x=x;
			fVirtualBounds.y=y;
			getHorizontalBar().setSelection(-x);
			getVerticalBar().setSelection(-y);
			updateViewRectangle();
		}
	}
	protected Rectangle getVirtualBounds() {
		return cloneRectangle(fVirtualBounds);
	}
	/**
	 * @param x
	 * @return the virtual coordinate in screen space
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
	private final Rectangle fViewRectangle=new Rectangle(0,0,0,0);
	protected void updateViewRectangle() {
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
		return cloneRectangle(fViewRectangle);
	}
	private Rectangle cloneRectangle(Rectangle r) {
		return new Rectangle(r.x,r.y,r.width,r.height);
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
	}
	/**
	 * @private
	 */
	private void updateScrollbars() {
		// don't get into infinite loops....
		if(!fInUpdateScrollbars) {
			fInUpdateScrollbars=true;
			try {
				doUpdateScrollbar();
			} finally {
				fInUpdateScrollbars=false;
			}
		} else {
			if(!fInUpdateScrollbarsLogged) {
				fInUpdateScrollbarsLogged=true;
				TerminalPlugin.getDefault().getLog().log(new Status(IStatus.WARNING, 
						TerminalPlugin.PLUGIN_ID, IStatus.OK, "Unexpected Recursion in terminal", //$NON-NLS-1$
						new RuntimeException()));
			}
		}
	}
	private void doUpdateScrollbar() {
		Rectangle clientArea= getClientArea();
		ScrollBar horizontal= getHorizontalBar();
		// even if setVisible was called on the scrollbar, isVisible
		// returns false if its parent is not visible. 
		if(!isVisible() || horizontal.isVisible()) {
			horizontal.setPageIncrement(clientArea.width - horizontal.getIncrement());
			int max= fVirtualBounds.width;
			horizontal.setMaximum(max);
			horizontal.setThumb(clientArea.width);
		}
		ScrollBar vertical= getVerticalBar();
		// even if setVisible was called on the scrollbar, isVisible
		// returns false if its parent is not visible. 
		if(!isVisible() || vertical.isVisible()) {
			vertical.setPageIncrement(clientArea.height - vertical.getIncrement());
			int max= fVirtualBounds.height;
			vertical.setMaximum(max);
			vertical.setThumb(clientArea.height);
		}
	}
	protected boolean isVertialBarVisible() {
		return getVerticalBar().isVisible();
	}
	protected void serVerticalBarVisible(boolean showVScrollBar) {
		ScrollBar vertical= getVerticalBar();
		vertical.setVisible(showVScrollBar);
		vertical.setSelection(0);
	}
	protected boolean isHorizontalBarVisble() {
		return getHorizontalBar().isVisible();
	}
	protected void setHorizontalBarVisible(boolean showHScrollBar) {
		ScrollBar horizontal= getHorizontalBar();
		horizontal.setVisible(showHScrollBar);
		horizontal.setSelection(0);
	}
}

