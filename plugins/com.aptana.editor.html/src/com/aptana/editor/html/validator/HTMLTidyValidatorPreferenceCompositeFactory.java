/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.validator;

import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.aptana.build.ui.preferences.IBuildParticipantPreferenceCompositeFactory;
import com.aptana.core.build.IBuildParticipantWorkingCopy;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.IProblem.Severity;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.html.validator.HTMLTidyValidator.ProblemCategory;
import com.aptana.editor.html.validator.HTMLTidyValidator.ProblemType;
import com.aptana.ui.preferences.ScrolledPageContent;

/**
 * @author cwilliams
 */
public class HTMLTidyValidatorPreferenceCompositeFactory implements IBuildParticipantPreferenceCompositeFactory
{

	private IBuildParticipantWorkingCopy participant;

	public Composite createPreferenceComposite(Composite parent, IBuildParticipantWorkingCopy participant)
	{
		this.participant = participant;

		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setFont(parent.getFont());
		mainComp.setLayout(new FillLayout());

		final ScrolledPageContent pageContent = new ScrolledPageContent(mainComp);

		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		Composite composite = pageContent.getBody();
		composite.setLayout(layout);
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		EnumMap<ProblemCategory, List<ProblemType>> map = new EnumMap<HTMLTidyValidator.ProblemCategory, List<HTMLTidyValidator.ProblemType>>(
				ProblemCategory.class);
		for (ProblemType type : HTMLTidyValidator.ProblemType.values())
		{
			List<ProblemType> types;
			if (map.containsKey(type.category()))
			{
				types = map.get(type.category());
				types.add(type);
			}
			else
			{
				types = CollectionsUtil.newList(type);
			}
			map.put(type.category(), types);
		}

		for (Entry<ProblemCategory, List<ProblemType>> entry : map.entrySet())
		{
			ExpandableComposite excomposite = new ExpandableComposite(composite, SWT.NONE, ExpandableComposite.TWISTIE
					| ExpandableComposite.CLIENT_INDENT);
			excomposite.setText(entry.getKey().label());
			excomposite.setExpanded(false);
			excomposite.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
			excomposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
			excomposite.addExpansionListener(new ExpansionAdapter()
			{
				@Override
				public void expansionStateChanged(ExpansionEvent e)
				{
					expandedStateChanged((ExpandableComposite) e.getSource());
				}
			});
			// fExpandableComposites.add(excomposite);
			makeScrollableCompositeAware(excomposite);

			Composite othersComposite = new Composite(excomposite, SWT.NONE);
			excomposite.setClient(othersComposite);
			othersComposite.setLayout(new GridLayout(2, false));

			// Create a label and combo box for each problem type.
			for (ProblemType type : entry.getValue())
			{
				createEntry(type, othersComposite);
			}
		}
		return mainComp;
	}

	protected void createEntry(ProblemType type, Composite group)
	{
		Label label1 = new Label(group, SWT.WRAP);
		label1.setText(type.description());
		label1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));

		final Combo combo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.add(IProblem.Severity.IGNORE.label());
		combo.add(IProblem.Severity.INFO.label());
		combo.add(IProblem.Severity.WARNING.label());
		combo.add(IProblem.Severity.ERROR.label());
		// Set the value based on the user's prefs!
		final String prefKey = type.getPrefKey();
		int num = participant.getPreferenceInt(type.getPrefKey(), IProblem.Severity.WARNING.intValue());
		IProblem.Severity severity = IProblem.Severity.create(num);
		combo.setText(severity.label());

		combo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String text = combo.getText();
				Severity severity = IProblem.Severity.create(text);
				participant.setPreference(prefKey, severity.intValue());
			}
		});
		combo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		makeScrollableCompositeAware(combo);
	}

	protected ScrolledPageContent getParentScrolledComposite(Control control)
	{
		Control parent = control.getParent();
		while (!(parent instanceof ScrolledPageContent) && parent != null)
		{
			parent = parent.getParent();
		}
		if (parent instanceof ScrolledPageContent)
		{
			return (ScrolledPageContent) parent;
		}
		return null;
	}

	protected ExpandableComposite getParentExpandableComposite(Control control)
	{
		Control parent = control.getParent();
		while (!(parent instanceof ExpandableComposite) && parent != null)
		{
			parent = parent.getParent();
		}
		if (parent instanceof ExpandableComposite)
		{
			return (ExpandableComposite) parent;
		}
		return null;
	}

	private void makeScrollableCompositeAware(Control control)
	{
		ScrolledPageContent parentScrolledComposite = getParentScrolledComposite(control);
		if (parentScrolledComposite != null)
		{
			parentScrolledComposite.adaptChild(control);
		}
	}

	protected final void expandedStateChanged(ExpandableComposite expandable)
	{
		ScrolledPageContent parentScrolledComposite = getParentScrolledComposite(expandable);
		if (parentScrolledComposite != null)
		{
			parentScrolledComposite.reflow(true);
		}
	}

}
