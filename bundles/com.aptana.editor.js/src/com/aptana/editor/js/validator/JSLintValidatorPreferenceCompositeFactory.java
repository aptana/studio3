/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.aptana.build.ui.preferences.IBuildParticipantPreferenceCompositeFactory;
import com.aptana.build.ui.preferences.ValidatorFiltersPreferenceComposite;
import com.aptana.core.build.IBuildParticipantWorkingCopy;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.js.core.preferences.IPreferenceConstants;

public class JSLintValidatorPreferenceCompositeFactory implements IBuildParticipantPreferenceCompositeFactory
{

	public Composite createPreferenceComposite(Composite parent, final IBuildParticipantWorkingCopy participant)
	{
		Composite master = new Composite(parent, SWT.NONE);
		master.setLayout(GridLayoutFactory.fillDefaults().create());

		GridDataFactory fillHoriz = GridDataFactory.fillDefaults().grab(true, false);

		// JSON Options
		Group group = new Group(master, SWT.BORDER);
		group.setText(Messages.JSLintValidatorPreferenceCompositeFactory_OptionsTitle);
		group.setLayout(new GridLayout());
		group.setLayoutData(fillHoriz.create());

		Label label = new Label(group, SWT.WRAP);
		label.setText(Messages.JSLintValidatorPreferenceCompositeFactory_OptionsMsg);
		fillHoriz.applyTo(label);

		final Text text = new Text(group, SWT.MULTI | SWT.V_SCROLL);

		final ControlDecoration decoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		decoration.setDescriptionText(Messages.JSLintValidatorPreferenceCompositeFactory_OptionsParseError);
		decoration.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR));
		decoration.hide();

		text.setText(participant.getPreferenceString(IPreferenceConstants.JS_LINT_OPTIONS));
		fillHoriz.hint(SWT.DEFAULT, 100).applyTo(text);
		text.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				decoration.hide();
				try
				{
					String optionsAsJSON = text.getText();
					JSON.parse(optionsAsJSON);
					participant.setPreference(IPreferenceConstants.JS_LINT_OPTIONS, text.getText());
				}
				catch (IllegalStateException e1)
				{
					decoration.show();
				}
			}
		});

		// Filters
		Composite filtersGroup = new ValidatorFiltersPreferenceComposite(master, participant);
		filtersGroup.setLayoutData(fillHoriz.grab(true, true).hint(SWT.DEFAULT, 150).create());

		return master;
	}
}
