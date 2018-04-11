/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.aptana.build.ui.preferences.IBuildParticipantPreferenceCompositeFactory;
import com.aptana.build.ui.preferences.ValidatorFiltersPreferenceComposite;
import com.aptana.core.build.IBuildParticipantWorkingCopy;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.IProblem.Severity;
import com.aptana.js.core.preferences.IPreferenceConstants;

public class JSParserValidatorPreferenceCompositeFactory implements IBuildParticipantPreferenceCompositeFactory
{

	public Composite createPreferenceComposite(Composite parent, final IBuildParticipantWorkingCopy participant)
	{
		Composite master = new Composite(parent, SWT.NONE);
		master.setLayout(GridLayoutFactory.fillDefaults().create());

		GridDataFactory fillHoriz = GridDataFactory.fillDefaults().grab(true, false);

		// Options
		Group group = new Group(master, SWT.BORDER);
		group.setText(Messages.JSParserValidatorPreferenceCompositeFactory_OptionsGroup);
		group.setLayout(new GridLayout());
		group.setLayoutData(fillHoriz.create());

		Composite pairs = new Composite(group, SWT.NONE);
		pairs.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());

		Label label = new Label(pairs, SWT.WRAP);
		label.setText(Messages.JSParserValidatorPreferenceCompositeFactory_MissingSemicolons);

		Combo combo = new Combo(pairs, SWT.READ_ONLY | SWT.SINGLE);
		for (IProblem.Severity severity : IProblem.Severity.values())
		{
			combo.add(severity.label());
			combo.setData(severity.label(), severity);
		}
		String severityValue = participant.getPreferenceString(IPreferenceConstants.PREF_MISSING_SEMICOLON_SEVERITY);
		combo.setText(IProblem.Severity.create(severityValue).label());

		combo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Combo c = ((Combo) e.widget);
				int index = c.getSelectionIndex();
				String text = c.getItem(index);
				IProblem.Severity s = (Severity) c.getData(text);
				participant.setPreference(IPreferenceConstants.PREF_MISSING_SEMICOLON_SEVERITY, s.id());
			}
		});
		fillHoriz.applyTo(pairs);

		// Filters
		Composite filtersGroup = new ValidatorFiltersPreferenceComposite(master, participant);
		filtersGroup.setLayoutData(fillHoriz.grab(true, true).hint(SWT.DEFAULT, 150).create());

		return master;
	}
}
