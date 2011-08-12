/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.propertypages;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.IUniformResourceMarker;
import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.util.DebugUtil;
import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.js.debug.core.model.IJSLineBreakpoint;
import com.aptana.js.debug.ui.JSDebugUIPlugin;

/**
 * @author Max Stepanov
 */
public class JSLineBreakpointPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {
	private Button fEnabledButton;
	private Button fHitCountButton;
	private Text fHitCountText;
	private Button fEnableConditionButton;
	private Text fConditionEditor;
	private Button fConditionIsTrue;
	private Button fConditionHasChanged;

	private List<String> fErrorMessages = new ArrayList<String>();

	private static final String fgHitCountErrorMessage = Messages.JSLineBreakpointPropertyPage_HitCountMustBePositiveInteger;
	private static final String fgEmptyConditionErrorMessage = Messages.JSLineBreakpointPropertyPage_EnterCondition;

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();
		Composite mainComposite = createComposite(parent, 1);
		createLabels(mainComposite);
		try {
			createEnabledButton(mainComposite);
			createHitCountEditor(mainComposite);
			createTypeSpecificEditors(mainComposite);
		} catch (CoreException e) {
			IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
		}
		setValid(true);
		return mainComposite;
	}

	/**
	 * Creates the labels displayed for the breakpoint.
	 * 
	 * @param parent
	 */
	private void createLabels(Composite parent) {
		IJSLineBreakpoint breakpoint = getBreakpoint();
		Composite labelComposite = createComposite(parent, 2);
		String fileName;
		IMarker marker = breakpoint.getMarker();
		if (marker instanceof IUniformResourceMarker) {
			fileName = DebugUtil.getPath(((IUniformResourceMarker) marker).getUniformResource());
		} else {
			fileName = marker.getResource().getFullPath().toString();
		}
		if (fileName != null) {
			createLabel(labelComposite, Messages.JSLineBreakpointPropertyPage_File);
			createLabel(labelComposite, fileName);
		}
		createTypeSpecificLabels(labelComposite);
	}

	/**
	 * Creates a fully configured composite with the given number of columns
	 * 
	 * @param parent
	 * @param numColumns
	 * @return the configured composite
	 */
	private Composite createComposite(Composite parent, int numColumns) {
		Composite composit = new Composite(parent, SWT.NONE);
		composit.setFont(parent.getFont());
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composit.setLayout(layout);
		composit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return composit;
	}

	/**
	 * Creates a fully configured text editor with the given initial value
	 * 
	 * @param parent
	 * @param initialValue
	 * @return the configured text editor
	 */
	protected Text createText(Composite parent, String initialValue) {
		Composite textComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		textComposite.setLayout(layout);
		textComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textComposite.setFont(parent.getFont());
		Text text = new Text(textComposite, SWT.SINGLE | SWT.BORDER);
		text.setText(initialValue);
		text.setFont(parent.getFont());
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

	/**
	 * Creates a fully configured text editor with the given initial value
	 * 
	 * @param parent
	 * @param initialValue
	 * @return the configured text editor
	 */
	protected Text createMultiText(Composite parent, String initialValue) {
		Composite textComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		textComposite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		textComposite.setLayoutData(data);
		textComposite.setFont(parent.getFont());
		Text text = new Text(textComposite, SWT.MULTI | SWT.BORDER);
		text.setText(initialValue);
		text.setFont(parent.getFont());
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		return text;
	}

	/**
	 * Creates the button to toggle enablement of the breakpoint
	 * 
	 * @param parent
	 * @throws CoreException
	 */
	private void createEnabledButton(Composite parent) throws CoreException {
		fEnabledButton = createCheckButton(parent, Messages.JSLineBreakpointPropertyPage_Enabled);
		fEnabledButton.setSelection(getBreakpoint().isEnabled());
	}

	/**
	 * Creates a fully configured check button with the given text.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param text
	 *            the label of the returned check button
	 * @return a fully configured check button
	 */
	protected Button createCheckButton(Composite parent, String text) {
		Button button = new Button(parent, SWT.CHECK | SWT.LEFT);
		button.setText(text);
		button.setFont(parent.getFont());
		button.setLayoutData(new GridData());
		return button;
	}

	/**
	 * Creates a fully configured radio button with the given text.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param text
	 *            the label of the returned radio button
	 * @return a fully configured radio button
	 */
	protected Button createRadioButton(Composite parent, String text) {
		Button button = new Button(parent, SWT.RADIO | SWT.LEFT);
		button.setText(text);
		button.setFont(parent.getFont());
		button.setLayoutData(new GridData());
		return button;
	}

	/**
	 * @param parent
	 *            the composite in which the hit count editor will be created
	 */
	private void createHitCountEditor(Composite parent) throws CoreException {
		IJSLineBreakpoint breakpoint = getBreakpoint();
		Composite hitCountComposite = createComposite(parent, 2);
		fHitCountButton = createCheckButton(hitCountComposite, Messages.JSLineBreakpointPropertyPage_HitCount);
		fHitCountButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				fHitCountText.setEnabled(fHitCountButton.getSelection());
				hitCountChanged();
			}
		});
		int hitCount = breakpoint.getHitCount();
		String hitCountString = StringUtil.EMPTY;
		if (hitCount > 0) {
			hitCountString = Integer.toString(hitCount);
			fHitCountButton.setSelection(true);
		} else {
			fHitCountButton.setSelection(false);
		}
		fHitCountText = createText(hitCountComposite, hitCountString);
		if (hitCount <= 0) {
			fHitCountText.setEnabled(false);
		}
		fHitCountText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				hitCountChanged();
			}
		});
	}

	/**
	 * Validates the current state of the hit count editor. Hit count value must be a positive integer.
	 */
	private void hitCountChanged() {
		if (!fHitCountButton.getSelection()) {
			removeErrorMessage(fgHitCountErrorMessage);
			return;
		}
		String hitCountText = fHitCountText.getText();
		int hitCount = -1;
		try {
			hitCount = Integer.parseInt(hitCountText);
		} catch (NumberFormatException e1) {
			addErrorMessage(fgHitCountErrorMessage);
			return;
		}
		if (hitCount < 1) {
			addErrorMessage(fgHitCountErrorMessage);
		} else {
			if (fgHitCountErrorMessage.equals(getErrorMessage())) {
				removeErrorMessage(fgHitCountErrorMessage);
			}
		}
	}

	private void conditionChanged() {
		if (!fEnableConditionButton.getSelection()) {
			removeErrorMessage(fgEmptyConditionErrorMessage);
			return;
		}
		String conditionText = fConditionEditor.getText();
		if (conditionText.length() == 0) {
			addErrorMessage(fgEmptyConditionErrorMessage);
			return;
		}
		/* TODO: validate condition */

		if (fgEmptyConditionErrorMessage.equals(getErrorMessage())) {
			removeErrorMessage(fgEmptyConditionErrorMessage);
		}
	}

	/**
	 * Creates a fully configured label with the given text.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param text
	 *            the test of the returned label
	 * @return a fully configured label
	 */
	private Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		label.setFont(parent.getFont());
		label.setLayoutData(new GridData());
		return label;
	}

	/**
	 * Returns the breakpoint that this preference page configures
	 * 
	 * @return the breakpoint this page configures
	 */
	private IJSLineBreakpoint getBreakpoint() {
		return (IJSLineBreakpoint) getElement();
	}

	private void createTypeSpecificLabels(Composite parent) {
		// Line number
		IJSLineBreakpoint breakpoint = getBreakpoint();
		StringBuffer lineNumber = new StringBuffer(4);
		try {
			int lNumber = breakpoint.getLineNumber();
			if (lNumber > 0) {
				lineNumber.append(lNumber);
			}
		} catch (CoreException ce) {
			IdeLog.logError(JSDebugUIPlugin.getDefault(), ce);
		}
		if (lineNumber.length() > 0) {
			createLabel(parent, Messages.JSLineBreakpointPropertyPage_LineNumber);
			createLabel(parent, lineNumber.toString());
		}

	}

	private void createTypeSpecificEditors(Composite parent) throws CoreException {
		createConditionEditor(parent);
	}

	/**
	 * Creates the controls that allow the user to specify the breakpoint's condition
	 * 
	 * @param parent
	 *            the composite in which the condition editor should be created
	 * @throws CoreException
	 *             if an exception occurs accessing the breakpoint
	 */
	private void createConditionEditor(Composite parent) throws CoreException {
		IJSLineBreakpoint breakpoint = getBreakpoint();

		Composite conditionComposite = new Group(parent, SWT.NONE);
		conditionComposite.setFont(parent.getFont());
		conditionComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		conditionComposite.setLayout(new GridLayout());
		fEnableConditionButton = createCheckButton(conditionComposite,
				Messages.JSLineBreakpointPropertyPage_EnableCondition);
		fEnableConditionButton.setSelection(breakpoint.isConditionEnabled());
		fEnableConditionButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setConditionEnabled(fEnableConditionButton.getSelection());
				conditionChanged();
			}
		});

		fConditionEditor = createMultiText(conditionComposite, breakpoint.getCondition());
		fConditionEditor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				conditionChanged();
			}
		});

		fConditionIsTrue = createRadioButton(conditionComposite, Messages.JSLineBreakpointPropertyPage_conditionIsTrue);
		fConditionHasChanged = createRadioButton(conditionComposite,
				Messages.JSLineBreakpointPropertyPage_valueOfConditionChanges);
		if (breakpoint.isConditionSuspendOnTrue()) {
			fConditionIsTrue.setSelection(true);
		} else {
			fConditionHasChanged.setSelection(true);
		}
		setConditionEnabled(fEnableConditionButton.getSelection());
	}

	/**
	 * Sets the enabled state of the condition editing controls.
	 * 
	 * @param enabled
	 */
	private void setConditionEnabled(boolean enabled) {
		fConditionEditor.setEnabled(enabled);
		fConditionIsTrue.setEnabled(enabled);
		fConditionHasChanged.setEnabled(enabled);
	}

	/**
	 * Adds the given error message to the errors currently displayed on this page. The page displays the most recently
	 * added error message. Clients should retain messages that are passed into this method as the message should later
	 * be passed into removeErrorMessage(String) to clear the error. This method should be used instead of
	 * setErrorMessage(String).
	 * 
	 * @param message
	 *            the error message to display on this page.
	 */
	public void addErrorMessage(String message) {
		if (message == null) {
			return;
		}
		fErrorMessages.remove(message);
		fErrorMessages.add(message);
		setErrorMessage(message);
		setValid(false);
	}

	/**
	 * Removes the given error message from the errors currently displayed on this page. When an error message is
	 * removed, the page displays the error that was added before the given message. This is akin to popping the message
	 * from a stack. Clients should call this method instead of setErrorMessage(null).
	 * 
	 * @param message
	 *            the error message to clear
	 */
	public void removeErrorMessage(String message) {
		fErrorMessages.remove(message);
		if (fErrorMessages.isEmpty()) {
			setErrorMessage(null);
			setValid(true);
		} else {
			setErrorMessage((String) fErrorMessages.get(fErrorMessages.size() - 1));
		}
	}

	/**
	 * Store the breakpoint properties.
	 * 
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				doStore();
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(wr, null, 0, null);
		} catch (CoreException e) {
			DebugUiPlugin
					.errorDialog(Messages.JSLineBreakpointPropertyPage_ExceptionWhileSavingBreakpointProperties, e);
			IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
		}
		return super.performOk();
	}

	/**
	 * doStore
	 * 
	 * @throws CoreException
	 */
	protected void doStore() throws CoreException {
		IJSLineBreakpoint breakpoint = getBreakpoint();

		boolean enabled = fEnabledButton.getSelection();
		breakpoint.setEnabled(enabled);

		boolean hitCountEnabled = fHitCountButton.getSelection();
		int hitCount = -1;
		if (hitCountEnabled) {
			try {
				hitCount = Integer.parseInt(fHitCountText.getText());
			} catch (NumberFormatException e) {
				IdeLog.logError(JSDebugUIPlugin.getDefault(), MessageFormat.format(
						Messages.JSLineBreakpointPropertyPage_PageAllowedInputOfInvalidStringForHitCountValue_0,
						fHitCountText.getText()), e);
			}
		}
		breakpoint.setHitCount(hitCount);

		boolean enableCondition = fEnableConditionButton.getSelection();
		String condition = fConditionEditor.getText();
		boolean suspendOnTrue = fConditionIsTrue.getSelection();
		if (breakpoint.isConditionEnabled() != enableCondition) {
			breakpoint.setConditionEnabled(enableCondition);
		}
		if (!condition.equals(breakpoint.getCondition())) {
			breakpoint.setCondition(condition);
		}
		if (breakpoint.isConditionSuspendOnTrue() != suspendOnTrue) {
			breakpoint.setConditionSuspendOnTrue(suspendOnTrue);
		}
	}
}
