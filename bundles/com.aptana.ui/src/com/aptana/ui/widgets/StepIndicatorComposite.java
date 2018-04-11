/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.widgets;

import java.text.MessageFormat;
import java.util.Arrays;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.aptana.ui.util.SWTUtils;
import com.aptana.ui.util.UIUtils;

/**
 * Composite used to display the progression through a wizard. Each step in the composite represents a wizard page
 * 
 * @author Nam Le <nle@appcelerator.com>
 */
public class StepIndicatorComposite extends Composite
{
	private String[] stepNames;
	private static Color selectedColor = new Color(UIUtils.getDisplay(), 41, 55, 64);
	private static Color normalColor = new Color(UIUtils.getDisplay(), 132, 156, 180);
	private static Color borderColor = UIUtils.getDisplay().getSystemColor(SWT.COLOR_GRAY);
	private static Color selectedTextColor = UIUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE);
	private Font selectedTextFont;
	private StepComposite[] steps;

	private class StepComposite extends Composite
	{
		String stepName;
		Label stepLabel;
		Composite stepComposite;
		Composite stepDecorator;
		boolean selection = false;
		boolean siblingSelected = false;
		boolean drawBorder = false;

		public StepComposite(Composite parent, String step, boolean drawBorder)
		{
			super(parent, SWT.NONE);
			this.stepName = step;
			this.drawBorder = drawBorder;

			GridLayout layout = new GridLayout(2, false);
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			layout.horizontalSpacing = 0;
			setLayout(layout);

			createElements();
		}

		private void createElements()
		{
			stepComposite = new Composite(this, SWT.NONE);
			stepComposite.setLayout(new GridLayout());
			stepComposite.setBackground(normalColor);
			stepComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).create());
			stepComposite.addPaintListener(new PaintListener()
			{

				public void paintControl(PaintEvent e)
				{
					GC gc = e.gc;
					Rectangle bounds = stepComposite.getClientArea();
					gc.setForeground(borderColor);
					gc.drawLine(bounds.x - 1, bounds.y, bounds.x + bounds.width, bounds.y);
					gc.drawLine(bounds.x - 1, bounds.y + bounds.height - 1, bounds.x + bounds.width, bounds.y
							+ bounds.height - 1);

					if (drawBorder)
					{
						gc.drawLine(bounds.x, bounds.y, bounds.x, bounds.y + bounds.height);
					}
				}
			});

			stepLabel = new Label(stepComposite, SWT.NONE);
			stepLabel.setBackground(normalColor);
			stepLabel.setText(MessageFormat.format("  {0}  ", stepName)); //$NON-NLS-1$
			stepLabel.setLayoutData(GridDataFactory.swtDefaults().grab(false, false).align(SWT.CENTER, SWT.CENTER)
					.create());
			((GridData) stepLabel.getLayoutData()).widthHint = stepLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;

			stepDecorator = new Composite(this, SWT.NONE);
			GridData gridData = GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).create();
			gridData.heightHint = stepComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			if ((gridData.heightHint % 2) == 0)
			{
				gridData.heightHint++;
			}

			gridData.widthHint = ((gridData.heightHint - 1) / 2) + 1;

			((GridData) stepComposite.getLayoutData()).heightHint = gridData.heightHint;

			if (selectedTextFont == null)
			{
				Font font2 = stepLabel.getFont();
				FontData[] fontData = SWTUtils.boldFont(font2);
				selectedTextFont = new Font(getDisplay(), fontData);
			}

			stepDecorator.setLayoutData(gridData);
			stepDecorator.setBackground(normalColor);
			stepDecorator.addPaintListener(new PaintListener()
			{

				public void paintControl(PaintEvent e)
				{
					GC gc = e.gc;
					Composite decorator = (Composite) e.widget;
					Rectangle bounds = decorator.getClientArea();

					if (selection)
					{
						gc.setForeground(selectedColor);
						gc.setBackground(selectedColor);

						int[] polygonPath = new int[] { bounds.x, bounds.y, bounds.x + bounds.width - 1,
								bounds.y + ((bounds.height - 1) / 2), bounds.x, bounds.y + bounds.height - 1 };

						gc.fillPolygon(polygonPath);
					}
					else if (siblingSelected)
					{
						gc.setForeground(selectedColor);
						gc.setBackground(selectedColor);

						int[] polygonPath = new int[] { bounds.x + 1, bounds.y + 1, bounds.x + bounds.width,
								bounds.y + ((bounds.height - 1) / 2) + 1, bounds.x + bounds.width, bounds.y + 1 };

						gc.fillPolygon(polygonPath);

						polygonPath = new int[] { bounds.x + 1, bounds.y + bounds.height - 1, bounds.x + bounds.width,
								bounds.y + ((bounds.height - 1) / 2) + 1, bounds.x + bounds.width,
								bounds.y + bounds.height - 1 };

						gc.fillPolygon(polygonPath);
					}

					gc.setForeground(borderColor);
					gc.drawLine(bounds.x, bounds.y, bounds.x + bounds.width - 1, bounds.y + ((bounds.height - 1) / 2));
					gc.drawLine(bounds.x + bounds.width - 1, bounds.y + ((bounds.height - 1) / 2), bounds.x, bounds.y
							+ bounds.height - 1);
					gc.drawLine(bounds.x - 1, bounds.y, bounds.x + bounds.width + 1, bounds.y);
					gc.drawLine(bounds.x - 1, bounds.y + bounds.height - 1, bounds.x + bounds.width + 1, bounds.y
							+ bounds.height - 1);
				}
			});
		}

		void setSelection(boolean selection)
		{
			this.selection = selection;
			stepComposite.setBackground(this.selection ? selectedColor : normalColor);
			stepLabel.setBackground(this.selection ? selectedColor : normalColor);
			stepLabel.setForeground(this.selection ? selectedTextColor : null);
			stepLabel.setFont(this.selection ? selectedTextFont : null);
			((GridData) stepLabel.getLayoutData()).widthHint = stepLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			stepDecorator.redraw();
		}

		void setSiblingSelected(boolean selection)
		{
			this.siblingSelected = selection;
		}
	}

	public StepIndicatorComposite(Composite parent, String[] stepNames)
	{
		super(parent, SWT.NONE);

		setBackground(normalColor);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.marginBottom = 1;

		setLayout(layout);
		setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

		if (stepNames != null)
		{
			createSteps(stepNames);
		}
		else
		{
			setVisible(false);
		}

		addPaintListener(new PaintListener()
		{

			public void paintControl(PaintEvent e)
			{
				GC gc = e.gc;
				gc.setForeground(borderColor);
				Composite stepComposite = (Composite) e.widget;
				Rectangle bounds = stepComposite.getClientArea();
				bounds.width--;
				bounds.height--;
				gc.drawRectangle(bounds);
			}
		});
	}

	public void createSteps(String[] stepNames)
	{
		this.stepNames = stepNames;
		GridLayout layout = new GridLayout(stepNames.length, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		setLayout(layout);

		if (steps != null)
		{
			for (StepComposite composite : steps)
			{
				composite.dispose();
			}
		}

		steps = new StepComposite[stepNames.length];

		for (int i = 0; i < stepNames.length; i++)
		{
			steps[i] = new StepComposite(this, stepNames[i], (i == 0));
		}

		if (steps.length > 0)
		{
			steps[0].setSelection(true);
		}
	}

	public void addStep(String newStepName)
	{
		// add a column for new step
		GridLayout layout = (GridLayout) getLayout();
		layout.numColumns++;

		// TODO If the step already exists, don't add it?

		// Add new step name
		String[] newStepNames = new String[stepNames.length + 1];
		System.arraycopy(stepNames, 0, newStepNames, 0, stepNames.length);
		newStepNames[stepNames.length] = newStepName;
		this.stepNames = newStepNames;

		// Add new step composite
		StepComposite[] newSteps = new StepComposite[steps.length + 1];
		System.arraycopy(steps, 0, newSteps, 0, steps.length);
		newSteps[steps.length] = new StepComposite(this, newStepName, false);
		this.steps = newSteps;

		// relayout and draw
		layout();
		redraw();
	}

	/**
	 * removes the last step
	 */
	public void removeStep()
	{

		// remove column for step
		GridLayout layout = (GridLayout) getLayout();
		layout.numColumns--;

		// Remove step name
		String[] newStepNames = new String[stepNames.length - 1];
		System.arraycopy(stepNames, 0, newStepNames, 0, stepNames.length - 1);
		this.stepNames = newStepNames;

		// Remove last step composite
		StepComposite[] newSteps = new StepComposite[steps.length - 1];
		steps[steps.length - 1].dispose();
		System.arraycopy(steps, 0, newSteps, 0, steps.length - 1);
		this.steps = newSteps;

		// relayout and draw
		layout();
		redraw();
	}

	public void setSelection(int index)
	{
		if (steps != null)
		{
			for (int i = 0; i < steps.length; i++)
			{
				steps[i].setSiblingSelected((i + 1) == index);
				steps[i].setSelection((i == index));
			}
		}
	}

	public void setSelection(String stepName)
	{
		if (steps != null)
		{
			int selectedIndex = Arrays.asList(stepNames).indexOf(stepName);
			setSelection(selectedIndex);
		}
	}

	@Override
	public void dispose()
	{
		super.dispose();

		if (selectedColor != null)
		{
			selectedColor.dispose();
		}

		if (selectedTextFont != null)
		{
			selectedTextFont.dispose();
		}

		if (normalColor != null)
		{
			normalColor.dispose();
		}
	}
}
