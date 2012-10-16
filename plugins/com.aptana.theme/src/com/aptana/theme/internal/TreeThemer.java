/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.aptana.core.logging.IdeLog;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;

/**
 * A Utility class that makes "hijacking" a Tree and hooking it up to our themes easy.
 * 
 * @author cwilliams
 */
class TreeThemer extends ControlThemer
{

	private TreeViewer fTreeViewer;
	private IPropertyChangeListener fontListener;
	private Listener measureItemListener;
	private Listener customDrawingListener;
	private Listener selectionPaintListener;

	public TreeThemer(TreeViewer treeViewer)
	{
		super(treeViewer.getTree());
		this.fTreeViewer = treeViewer;
	}

	public TreeThemer(Tree tree)
	{
		super(tree);
	}

	public void apply()
	{
		super.apply();
		addSelectionColorOverride();
		addCustomTreeControlDrawing();
		addMeasureItemListener();
		addFontListener();
	}

	@Override
	protected void applyTheme()
	{
		super.applyTheme();
		if (fTreeViewer != null && !controlIsDisposed())
		{
			fTreeViewer.refresh(true);
		}
	}

	protected void addSelectionColorOverride()
	{
		if (controlIsDisposed())
		{
			return;
		}

		super.addSelectionColorOverride();
		final Tree tree = getTree();
		selectionPaintListener = new Listener()
		{
			public void handleEvent(Event event)
			{
				if (!invasiveThemesEnabled())
				{
					return;
				}
				GC gc = event.gc;
				Color oldBackground = gc.getBackground();
				try
				{
					Rectangle clientArea = tree.getClientArea();

					// FIX for busted drawing on some variants of Linux, notably OpenSuSE, where
					// setBackground on the tree doesn't behave. We color background behind items in
					// ControlThemer.addSelectionColorOverride()
					// This draws from bottom of the last item down to bottom of tree with background color
					if (!isWindows && !isMacOSX)
					{
						gc.setBackground(getBackground());
						Rectangle itemBounds = new Rectangle(0, 0, 0, 0);
						if (tree.getItemCount() != 0)
						{
							TreeItem lastItem = tree.getItem(tree.getItemCount() - 1);
							lastItem = getLastItemRecursively(lastItem);
							itemBounds = lastItem.getBounds();
						}
						int bottomY = itemBounds.y + itemBounds.height;
						// The +2 on width is for Linux, since clientArea begins at [-2,-2] and
						// without it we don't properly color full width
						Rectangle toColor = new Rectangle(clientArea.x, bottomY, clientArea.width + 2,
								clientArea.height - bottomY);
						gc.fillRectangle(toColor);
					}

					// FIX for TISTUD-426: http://jira.appcelerator.org/browse/TISTUD-426
					// HACK to grab cell editors of tree views (specifically Variables view) and set their control's fg
					// explicitly!
					if (fTreeViewer != null)
					{
						CellEditor[] editors = fTreeViewer.getCellEditors();
						if (editors != null)
						{
							for (CellEditor editor : editors)
							{
								if (editor == null)
								{
									continue;
								}
								Control c = editor.getControl();
								if (c != null)
								{
									c.setForeground(getForeground());
								}
							}
						}
					}

					// FIX For Windows, the selection color doesn't extend past bounds of the tree item, so here we
					// draw from right end of item to full width of tree, so selection bg color is full width of view
					if (!isWindows)
					{
						return;
					}

					TreeItem[] items = tree.getSelection();
					if (items == null || items.length == 0)
					{
						return;
					}
					gc.setBackground(getSelection());
					int clientWidth = clientArea.width + 2;
					int columns = tree.getColumnCount();
					for (TreeItem item : items)
					{
						if (item != null)
						{
							Rectangle bounds;
							if (columns == 0)
							{
								bounds = item.getBounds();
							}
							else
							{
								bounds = item.getBounds(columns - 1);
							}
							int x = bounds.x + bounds.width;
							if (x < clientWidth)
							{
								gc.fillRectangle(x, bounds.y, clientWidth - x, bounds.height);
							}
						}
					}
				}
				catch (Exception e)
				{
					// ignore
				}
				finally
				{
					gc.setBackground(oldBackground);

					// force foreground color. Otherwise on dark themes we get black FG (all the time on Win, on
					// non-focus for Mac)
					gc.setForeground(getForeground());
				}
			}
		};
		tree.addListener(SWT.Paint, selectionPaintListener);
	}

	protected TreeItem getLastItemRecursively(TreeItem lastItem)
	{
		if (lastItem == null)
		{
			return null;
		}
		int itemCount = lastItem.getItemCount();
		if (itemCount == 0 || !lastItem.getExpanded())
		{
			return lastItem;
		}
		return getLastItemRecursively(lastItem.getItem(itemCount - 1));
	}

	private void addCustomTreeControlDrawing()
	{
		// Hack to overdraw the native tree expand/collapse controls and use custom plus/minus box.
		if (isMacOSX || isUbuntu || controlIsDisposed())
		{
			return;
		}

		// FIXME The native control/arrow still shows through on OpenSuSE 11.4
		final Tree tree = getTree();
		customDrawingListener = new Listener()
		{
			public void handleEvent(Event event)
			{
				if (!invasiveThemesEnabled())
				{
					return;
				}
				GC gc = event.gc;
				Widget item = event.item;
				boolean isExpanded = false;
				boolean draw = false;
				if (item instanceof TreeItem)
				{
					TreeItem tItem = (TreeItem) item;
					isExpanded = tItem.getExpanded();
					draw = tItem.getItemCount() > 0;
				}
				if (!draw)
				{
					return;
				}
				final int width = 10;
				final int height = 12;
				final int x = event.x - 16;
				final int y = event.y + 4;
				Color oldBackground = gc.getBackground();
				gc.setBackground(getBackground());
				// wipe out the native control
				gc.fillRectangle(x, y, width + 1, height - 1); // +1 and -1 because of hovering selecting on windows
				// vista
				// draw a plus/minus based on expansion!
				gc.setBackground(getForeground());
				// draw surrounding box (with alpha so that it doesn't get too strong).
				gc.setAlpha(195);
				gc.drawRectangle(x + 1, y + 1, width - 2, width - 2); // make it smaller than the area erased
				gc.setAlpha(255);
				// draw '-'
				int halfWidth = width >> 1;
				gc.drawLine(x + 3, y + halfWidth, x + 7, y + halfWidth);
				if (!isExpanded)
				{
					// draw '|' to make it a plus
					gc.drawLine(x + halfWidth, y + 3, x + halfWidth, y + 7);
				}
				gc.setBackground(oldBackground);

				event.detail &= ~SWT.BACKGROUND;
			}
		};
		tree.addListener(SWT.PaintItem, customDrawingListener);
	}

	private void addMeasureItemListener()
	{
		if (controlIsDisposed())
		{
			return;
		}

		final Tree tree = getTree();
		// Hack to force a specific row height and width based on font
		measureItemListener = new Listener()
		{
			public void handleEvent(Event event)
			{
				if (!useEditorFont())
				{
					return;
				}
				Font font = JFaceResources.getFont(IThemeManager.VIEW_FONT_NAME);
				if (font == null)
				{
					font = JFaceResources.getTextFont();
				}
				if (font != null)
				{
					event.gc.setFont(font);
					FontMetrics metrics = event.gc.getFontMetrics();
					int height = metrics.getHeight() + 2;
					TreeItem item = (TreeItem) event.item;
					int width = event.gc.stringExtent(item.getText()).x + 24; // minimum width we need for text plus eye
					event.height = height;
					if (width > event.width)
					{
						event.width = width;
					}
				}
			}
		};
		tree.addListener(SWT.MeasureItem, measureItemListener);
	}

	private void addFontListener()
	{
		if (controlIsDisposed())
		{
			return;
		}
		final Tree tree = getTree();
		fontListener = new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				if (!event.getProperty().equals(IThemeManager.VIEW_FONT_NAME) || !useEditorFont())
				{
					return;
				}
				Display.getCurrent().asyncExec(new Runnable()
				{
					public void run()
					{
						Font font = getFont();
						if (font != null && !tree.isDisposed()) // as it's asynchronous, make sure it wasn't disposed in
																// the meanhile.
						{
							tree.setFont(font);

							GC gc = new GC(Display.getDefault());
							gc.setFont(font);
							FontMetrics metrics = gc.getFontMetrics();
							int height = metrics.getHeight() + 2;
							gc.dispose();

							if (isWindows)
							{
								try
								{
									Method m = Tree.class.getDeclaredMethod("setItemHeight", Integer.TYPE); //$NON-NLS-1$
									if (m != null)
									{
										m.setAccessible(true);
										m.invoke(tree, height);
									}
								}
								catch (Exception e)
								{
									IdeLog.logError(ThemePlugin.getDefault(), e);
								}
							}
							// HACK! This is a major hack to force down the height of the row when we resize our font to
							// a
							// smaller height!
							else if (isMacOSX && isCocoa)
							{
								try
								{
									Field f = Control.class.getField("view"); //$NON-NLS-1$
									if (f != null)
									{
										Object widget = f.get(tree);
										if (widget != null)
										{
											Method m = widget.getClass().getMethod("setRowHeight", Double.TYPE); //$NON-NLS-1$
											if (m != null)
											{
												m.invoke(widget, height);
											}
										}
									}
								}
								catch (Exception e)
								{
									IdeLog.logError(ThemePlugin.getDefault(), e);
								}
							}
						}
						// OK, the app explorer font changed. We need to force a refresh of the app explorer tree
						if (fTreeViewer != null)
						{
							fTreeViewer.refresh();
						}
						tree.redraw();
						tree.update();
					}
				});

			}
		};
		JFaceResources.getFontRegistry().addListener(fontListener);
	}

	public void dispose()
	{
		super.dispose();
		removeSelectionOverride();
		removeCustomTreeControlDrawing();
		removeMeasureItemListener();
		removeFontListener();
	}

	private void removeCustomTreeControlDrawing()
	{
		if (customDrawingListener != null && !controlIsDisposed())
		{
			getTree().removeListener(SWT.PaintItem, customDrawingListener);
		}
		customDrawingListener = null;
	}

	protected void removeSelectionOverride()
	{
		super.removeSelectionOverride();

		if (selectionPaintListener != null && !controlIsDisposed())
		{
			getTree().removeListener(SWT.Paint, selectionPaintListener);
		}
		selectionPaintListener = null;
	}

	private void removeMeasureItemListener()
	{
		if (measureItemListener != null && !controlIsDisposed())
		{
			getTree().removeListener(SWT.MeasureItem, measureItemListener);
		}
		measureItemListener = null;
	}

	protected Tree getTree()
	{
		return (Tree) getControl();
	}

	private void removeFontListener()
	{
		if (fontListener != null)
		{
			JFaceResources.getFontRegistry().removeListener(fontListener);
		}
		fontListener = null;
	}
}
