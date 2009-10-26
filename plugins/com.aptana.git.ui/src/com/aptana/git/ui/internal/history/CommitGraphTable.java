package com.aptana.git.ui.internal.history;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
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

import com.aptana.git.core.model.GitCommit;

public class CommitGraphTable extends TableViewer
{

	public static final int LANE_WIDTH = 14;
	public static final int LINE_WIDTH = 2;
	private BranchPainter renderer;
	private Map<GitCommit, GraphCellInfo> decorations;

	public CommitGraphTable(Composite parent)
	{
		super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);

		renderer = new BranchPainter(parent.getDisplay());

		Table table = getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
		final TableLayout layout = new TableLayout();
		table.setLayout(layout);

		final TableColumn graph = new TableColumn(table, SWT.NONE);
		graph.setResizable(true);
		graph.setText(""); //$NON-NLS-1$
		graph.setWidth(250);
		layout.addColumnData(new ColumnWeightData(20, true));

		final TableColumn author = new TableColumn(table, SWT.NONE);
		author.setResizable(true);
		author.setText("Author");
		author.setWidth(250);
		layout.addColumnData(new ColumnWeightData(10, true));

		final TableColumn date = new TableColumn(table, SWT.NONE);
		date.setResizable(true);
		date.setText("Date");
		date.setWidth(250);
		layout.addColumnData(new ColumnWeightData(5, true));

		setContentProvider(ArrayContentProvider.getInstance());
		setLabelProvider(new CommitLabelProvider());

		createPaintListener(table);
	}

	private void createPaintListener(final Table rawTable)
	{
		// Tell SWT we will completely handle painting for some columns.
		//
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

	void doPaint(final Event event)
	{
		final GitCommit c = (GitCommit) ((TableItem) event.item).getData();
		// if (highlight != null && c.has(highlight))
		// event.gc.setFont(hFont);
		// else
		// event.gc.setFont(nFont);

		if (event.index == 0)
		{
			renderer.paint(event);
			return;
		}

		final ITableLabelProvider lbl = (ITableLabelProvider) getLabelProvider();
		final String txt = lbl.getColumnText(c, event.index);

		final Point textsz = event.gc.textExtent(txt);
		final int texty = (event.height - textsz.y) / 2;
		event.gc.drawString(txt, event.x, event.y + texty, true);
	}

	@Override
	protected void inputChanged(Object input, Object oldInput)
	{
		super.inputChanged(input, oldInput);

		List<GitCommit> commits = (List<GitCommit>) input;
		GitGrapher grapher = new GitGrapher();
		decorations = grapher.decorateCommits(commits);
	}
	
	static class CommitLabelProvider extends BaseLabelProvider implements ITableLabelProvider
	{

		private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

		@Override
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex)
		{
			GitCommit commit = (GitCommit) element;
			switch (columnIndex)
			{
				case 0:
					return commit.getSubject();
				case 1:
					return commit.getAuthor();
				case 2:
					return fmt.format(commit.date());
				default:
					return "";
			}
		}

	}

	private class BranchPainter
	{

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
		// Color for the "dot"
		private Color sys_black;

		public BranchPainter(Display d)
		{
			blue = new Color(d, 41, 94, 153);
			yellow = new Color(d, 207, 173, 0);
			green = new Color(d, 93, 166, 1);
			red = new Color(d, 181, 23, 0);
			purple = new Color(d, 112, 72, 121);
			orange = new Color(d, 216, 112, 0);
			laneColors = new Color[] { green, blue, yellow, purple, red, orange };
			sys_black = d.getSystemColor(SWT.COLOR_BLACK);
		}

		// FIXME Call this when our table is disposed!
		void dispose()
		{
			for (Color color : laneColors)
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
			GraphCellInfo info = decorations.get(commit);
			int maxCenter = 0;
			int myLaneX = 0;
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
						myLaneX = x1 + (LINE_WIDTH / 2);
					else
					{
						if (line.isUpper())
						{
							myLaneX = Math.min(x1, x2) + (LINE_WIDTH / 2);
						}
						else
							myLaneX = Math.max(x1, x2) + (LINE_WIDTH / 2);
					}
				}
				drawLine(color(line.getIndex()), x1, y1, x2, y2, LINE_WIDTH);
			}
			final int dotSize = computeDotSize(height);
			final int dotX = myLaneX - dotSize / 2 - 1;
			final int dotY = (height - dotSize) / 2;			
			drawCommitDot(dotX, dotY, dotSize, dotSize);
			
			final String msg = commit.getSubject();
			int textx = (maxCenter + LANE_WIDTH / 2) + 8;
			drawText(msg, textx, height / 2);
		}
		
		protected void drawCommitDot(final int x, final int y, final int w,
				final int h) {
			g.fillOval(cellX + x, cellY + y, w, h);
			g.setForeground(sys_black);
			g.setLineWidth(2);
			g.drawOval(cellX + x, cellY + y, w, h);
		}
		
		private int computeDotSize(final int h) {
			int d = (int) (Math.min(h, LANE_WIDTH) * 0.50f);
			d += (d & 1);
			return d;
		}

		private Color color(int index)
		{
			index = index % laneColors.length;
			return laneColors[index];
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
