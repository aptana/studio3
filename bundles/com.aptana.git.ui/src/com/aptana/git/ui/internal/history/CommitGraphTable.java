/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.history;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.aptana.core.util.StringUtil;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRef;

/**
 * Table to show the list of commits for a resource in reverse chronological order. Custom paints the first column so
 * that we can draw the branching history graphically.
 * 
 * @author cwilliams
 */
class CommitGraphTable extends TableViewer
{

	private BranchPainter renderer;
	private Map<GitCommit, GraphCellInfo> decorations;
	private List<GitCommit> commits;

	CommitGraphTable(Composite parent)
	{
		super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);

		renderer = new BranchPainter(parent.getDisplay());

		final Table table = getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
		final TableLayout layout = new TableLayout();
		table.setLayout(layout);

		final TableColumn graph = new TableColumn(table, SWT.NONE);
		graph.setResizable(true);
		graph.setText(StringUtil.EMPTY);
		graph.setWidth(250);
		layout.addColumnData(new ColumnWeightData(20, true));

		final TableColumn author = new TableColumn(table, SWT.NONE);
		author.setResizable(true);
		author.setText(Messages.CommitGraphTable_AuthorColumn_Label);
		author.setWidth(250);
		layout.addColumnData(new ColumnWeightData(10, true));

		final TableColumn date = new TableColumn(table, SWT.NONE);
		date.setResizable(true);
		date.setText(Messages.CommitGraphTable_DateColumn_Label);
		date.setWidth(250);
		layout.addColumnData(new ColumnWeightData(5, true));

		setContentProvider(ArrayContentProvider.getInstance());
		setLabelProvider(new CommitLabelProvider());

		createPaintListener(table);
		table.addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				renderer.dispose();
			}
		});

		table.addListener(SWT.SetData, new Listener()
		{
			public void handleEvent(Event event)
			{
				if (commits == null)
					return;
				TableItem item = (TableItem) event.item;
				int index = table.indexOf(item);
				item.setData(commits.get(index));
			}
		});
	}

	void setCommits(final List<GitCommit> commits)
	{
		this.commits = commits;
		decorations = new GitGrapher().decorateCommits(commits);
		setInput(commits);
		if (!commits.isEmpty())
		{
			setSelection(new StructuredSelection(commits.get(0)));
		}
	}

	/**
	 * Tell SWT that we'll be painting the first column.
	 * 
	 * @param rawTable
	 */
	private void createPaintListener(final Table rawTable)
	{
		rawTable.addListener(SWT.EraseItem, new Listener()
		{
			public void handleEvent(final Event event)
			{
				if (0 <= event.index && event.index <= 2)
					event.detail &= ~SWT.FOREGROUND;
			}
		});

		rawTable.addListener(SWT.PaintItem, new Listener()
		{
			public void handleEvent(final Event event)
			{
				doPaint(event);
			}
		});
	}

	private void doPaint(final Event event)
	{
		if (event.index == 0)
		{
			renderer.paint(event);
			return;
		}

		final GitCommit c = (GitCommit) ((TableItem) event.item).getData();
		final ITableLabelProvider lbl = (ITableLabelProvider) getLabelProvider();
		final String txt = lbl.getColumnText(c, event.index);

		final Point textsz = event.gc.textExtent(txt);
		final int texty = (event.height - textsz.y) / 2;
		event.gc.drawString(txt, event.x, event.y + texty, true);
	}

	private static class CommitLabelProvider extends BaseLabelProvider implements ITableLabelProvider
	{

		private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			GitCommit commit = (GitCommit) element;
			if (commit == null)
			{
				return StringUtil.EMPTY;
			}
			switch (columnIndex)
			{
				case 0:
					return commit.getSubject();
				case 1:
					return commit.getAuthor();
				case 2:
					return fmt.format(commit.date());
				default:
					return StringUtil.EMPTY;
			}
		}

	}

	private class BranchPainter
	{

		private static final int HORIZONTAL_PADDING = 8;
		private static final int LANE_WIDTH = 10;
		private static final int LINE_WIDTH = 2;

		private GC g;
		private int cellX;
		private int cellY;
		private Color cellFG;
		private Color cellBG;

		// Colors for the lanes
		private Color blue;
		private Color yellow;
		private Color green;
		private Color red;
		private Color purple;
		private Color orange;
		private Color[] laneColors;
		// Color for the "dot" (and ref border and text)
		private Color sys_black;
		// Color for refs
		private Color sys_white; // (unknown)
		private Color refOrange; // head
		private Color refBlue; // remote
		private Color refYellow; // tag
		private Color[] refColors;

		BranchPainter(Display d)
		{
			blue = new Color(d, 41, 94, 153);
			yellow = new Color(d, 207, 173, 0);
			green = new Color(d, 93, 166, 1);
			red = new Color(d, 181, 23, 0);
			purple = new Color(d, 112, 72, 121);
			orange = new Color(d, 216, 112, 0);
			laneColors = new Color[] { green, blue, yellow, purple, red, orange };

			refOrange = new Color(d, 253, 180, 97);
			refBlue = new Color(d, 190, 229, 254);
			refYellow = new Color(d, 252, 237, 96);
			refColors = new Color[] { refOrange, refBlue, refYellow };

			sys_black = d.getSystemColor(SWT.COLOR_BLACK);
			sys_white = d.getSystemColor(SWT.COLOR_WHITE);
		}

		void dispose()
		{
			for (Color color : laneColors)
			{
				color.dispose();
			}
			for (Color color : refColors)
			{
				color.dispose();
			}
		}

		public void paint(Event event)
		{
			g = event.gc;
			cellX = event.x;
			cellY = event.y;
			cellFG = g.getForeground();
			cellBG = g.getBackground();

			final TableItem ti = (TableItem) event.item;
			paintCommit((GitCommit) ti.getData(), event.height);
		}

		private void paintCommit(GitCommit commit, int height)
		{
			if (commit == null)
				return;
			GraphCellInfo info = ((decorations == null) ? null : decorations.get(commit));
			int myLaneX = LANE_WIDTH + (LANE_WIDTH / 2);
			int maxCenter = myLaneX;
			if (info != null)
			{
				for (GitGraphLine line : info.getLines())
				{
					int y1 = 0;
					int y2 = height / 2;
					if (!line.isUpper())
					{
						y1 = height / 2;
						y2 = height;
					}
					int x1 = (line.getFrom() * LANE_WIDTH) + (LANE_WIDTH / 2);
					int x2 = (line.getTo() * LANE_WIDTH) + (LANE_WIDTH / 2);
					maxCenter = Math.max(maxCenter, Math.max(x1, x2));
					if (line.getTo() == info.getPosition())
					{
						if (x1 == x2)
						{
							myLaneX = x1 + (LINE_WIDTH / 2);
						}
						else
						{
							myLaneX = Math.min(x1, x2) + (LINE_WIDTH / 2);
						}
					}
					drawLine(color(line.getIndex()), x1, y1, x2, y2, LINE_WIDTH);
				}
			}
			final int dotSize = computeDotSize(height);
			final int dotX = myLaneX - dotSize / 2 - 1;
			final int dotY = (height - dotSize) / 2;
			drawCommitDot(dotX, dotY, dotSize, dotSize);

			final String msg = commit.getSubject();
			int textx = Math.max(maxCenter + LANE_WIDTH / 2, dotX + dotSize) + HORIZONTAL_PADDING;
			int n = commit.refCount();
			if (commit.hasRefs())
			{
				for (GitRef ref : commit.getRefs())
				{
					textx += drawLabel(textx + dotSize, height / 2, ref);
				}
			}
			drawText(msg, textx + dotSize + n * 2, height / 2);
		}

		protected int drawLabel(int x, int y, GitRef ref)
		{
			y += 1;
			String txt = ref.shortName();
			if (ref.type().equals(GitRef.TYPE.HEAD))
			{
				g.setBackground(refOrange);
			}
			else if (ref.type().equals(GitRef.TYPE.REMOTE))
			{
				g.setBackground(refBlue);
			}
			else if (ref.type().equals(GitRef.TYPE.TAG))
			{
				g.setBackground(refYellow);
			}
			else
			{
				// Whatever this would be
				g.setBackground(sys_white);
			}

			if (txt.length() > 12)
				txt = txt.substring(0, 11) + "\u2026"; // ellipsis (in UTF-8) //$NON-NLS-1$

			Point textsz = g.stringExtent(txt);
			int arc = textsz.y / 2;
			final int texty = (y * 2 - textsz.y) / 2;

			// Draw backgrounds
			g.fillRoundRectangle(x + 1, cellY + texty, textsz.x + 3, textsz.y - 1, arc, arc);
			g.setForeground(sys_black);
			g.drawString(txt, x + 2, cellY + texty, true);
			g.setLineWidth(2);

			// Add a thin black border
			g.setLineWidth(1);
			g.setForeground(sys_black);
			g.drawRoundRectangle(x + 1, cellY + texty, textsz.x + 3, textsz.y - 1, arc, arc);
			g.setAlpha(255);

			return HORIZONTAL_PADDING + textsz.x;
		}

		protected void drawCommitDot(final int x, final int y, final int w, final int h)
		{
			g.fillOval(cellX + x, cellY + y, w, h);
			g.setForeground(cellFG);
			g.setLineWidth(2);
			g.drawOval(cellX + x, cellY + y, w, h);
		}

		private int computeDotSize(final int h)
		{
			int d = (int) (Math.min(h, LANE_WIDTH) * 0.50f);
			d += (d & 1);
			return d;
		}

		/**
		 * Use the lane's index to assign it a color. Cycle through the set we have.
		 * 
		 * @param index
		 * @return
		 */
		private Color color(int index)
		{
			return laneColors[index % laneColors.length];
		}

		protected void drawLine(final Color color, final int x1, final int y1, final int x2, final int y2,
				final int width)
		{
			g.setForeground(color);
			g.setLineWidth(width);
			g.drawLine(cellX + x1, cellY + y1, cellX + x2, cellY + y2);
		}

		protected void drawText(final String msg, final int x, final int y)
		{
			final Point textsz = g.textExtent(msg);
			final int texty = (y * 2 - textsz.y) / 2;
			g.setForeground(cellFG);
			g.setBackground(cellBG);
			g.drawString(msg, cellX + x, cellY + texty, true);
		}
	}
}
