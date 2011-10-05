/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.dialogs;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.epl.UIEplPlugin;

/**
 * @author Max Stepanov
 *
 */
public class InputURLDialog extends Dialog {

    private static final String URL = "url"; //$NON-NLS-1$
	private static final String URLS = "urls"; //$NON-NLS-1$
	private static final String XML = "xml"; //$NON-NLS-1$

	/**
     * The title of the dialog.
     */
    private String title;

    /**
     * The message to display, or <code>null</code> if none.
     */
    private String message;

    /**
     * The input value; the empty string by default.
     */
    private String value = StringUtil.EMPTY;

    /**
     * The input validator, or <code>null</code> if none.
     */
    private IInputValidator validator;

    /**
     * Ok button widget.
     */
    private Button okButton;

    /**
     * Input combo widget.
     */
    private CCombo combo;

    /**
     * Error message label widget.
     */
    private Text errorMessageText;
    
    /**
     * Error message string.
     */
    private String errorMessage;

    /**
     * Creates an input URL dialog with OK and Cancel buttons. Note that the dialog
     * will have no visual representation (no widgets) until it is told to open.
     * <p>
     * Note that the <code>open</code> method blocks for input dialogs.
     * </p>
     * 
     * @param parentShell
     *            the parent shell, or <code>null</code> to create a top-level
     *            shell
     * @param dialogTitle
     *            the dialog title, or <code>null</code> if none
     * @param dialogMessage
     *            the dialog message, or <code>null</code> if none
     * @param initialValue
     *            the initial input value, or <code>null</code> if none
     *            (equivalent to the empty string)
     */
    public InputURLDialog(Shell parentShell, String dialogTitle,
            String dialogMessage, String initialValue) {
        super(parentShell);
        this.title = dialogTitle;
        message = dialogMessage;
        if (initialValue == null) {
			value = StringUtil.EMPTY;
		} else {
			value = initialValue;
		}
        this.validator = new IInputValidator() {
			public String isValid(String newText) {
				try {
					new URI(newText).toURL();
				} catch (Exception e) {
					return EplMessages.InputURLDialog_InvalidURL;
				}
				return null;
			}
        };
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            value = combo.getText();
            saveMemento();
        } else {
            value = null;
        }
        super.buttonPressed(buttonId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
			shell.setText(title);
		}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        okButton = createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
        //do this here because setting the text will set enablement on the ok button
        loadList();
        combo.setFocus();
        if (value != null) {
            combo.setText(value);
            setErrorMessage(null);
            if (validator.isValid(value) != null) {
        		Control button = getButton(IDialogConstants.OK_ID);
        		if (button != null) {
        			button.setEnabled(false);
        		}

            }
        }
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
        // create message
        if (message != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(message);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL
                    | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }
        combo = new CCombo(composite, getInputComboStyle());
        combo.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        combo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateInput();
            }
        });
        errorMessageText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
        errorMessageText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        errorMessageText.setBackground(errorMessageText.getDisplay()
                .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		errorMessageText.setForeground(errorMessageText.getDisplay()
				.getSystemColor(SWT.COLOR_RED));
        // Set the error message text
        // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=66292
        setErrorMessage(errorMessage);

        applyDialogFont(composite);
        return composite;
    }

    /**
     * Returns the ok button.
     * 
     * @return the ok button
     */
    protected Button getOkButton() {
        return okButton;
    }

    /**
     * Returns the combo.
     * 
     * @return the combo
     */
    protected CCombo getText() {
        return combo;
    }

    /**
     * Returns the validator.
     * 
     * @return the validator
     */
    protected IInputValidator getValidator() {
        return validator;
    }

    /**
     * Returns the string typed into this input dialog.
     * 
     * @return the input string
     */
    public String getValue() {
        return value;
    }

    /**
     * Validates the input.
     * <p>
     * The default implementation of this framework method delegates the request
     * to the supplied input validator object; if it finds the input invalid,
     * the error message is displayed in the dialog's message line. This hook
     * method is called whenever the text changes in the input field.
     * </p>
     */
    protected void validateInput() {
        String errorMessage = null;
        if (validator != null) {
        	String text = combo.getText();
            errorMessage = validator.isValid(text);
        }
        // Bug 16256: important not to treat "" (blank error) the same as null
        // (no error)
        setErrorMessage(errorMessage);
    }

    /**
     * Sets or clears the error message.
     * If not <code>null</code>, the OK button is disabled.
     * 
     * @param errorMessage
     *            the error message, or <code>null</code> to clear
     * @since 3.0
     */
    public void setErrorMessage(String errorMessage) {
    	this.errorMessage = errorMessage;
    	if (errorMessageText != null && !errorMessageText.isDisposed()) {
    		errorMessageText.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$
    		// Disable the error message text control if there is no error, or
    		// no error text (empty or whitespace only).  Hide it also to avoid
    		// color change.
    		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=130281
    		boolean hasError = errorMessage != null && (StringConverter.removeWhiteSpaces(errorMessage)).length() > 0;
    		errorMessageText.setEnabled(hasError);
    		errorMessageText.setVisible(hasError);
    		errorMessageText.getParent().update();
    		// Access the ok button by id, in case clients have overridden button creation.
    		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=113643
    		Control button = getButton(IDialogConstants.OK_ID);
    		if (button != null) {
    			button.setEnabled(errorMessage == null);
    		}
    	}
    }
    
	/**
	 * Returns the style bits that should be used for the input combo field.
	 * Subclasses may override.
	 * 
	 * @return the integer style bits that should be used when creating the
	 *         input combo
	 * 
	 */
	protected int getInputComboStyle() {
		return SWT.BORDER;
	}
	
	protected IMemento getMemento() {
		File file = UIEplPlugin.getDefault().getStateLocation().append(URLS).addFileExtension(XML).toFile();
		if (file.exists()) {
			try {
				FileReader reader = new FileReader(file);
				XMLMemento memento = XMLMemento.createReadRoot(reader);
				return memento;
			} catch (Exception e) {
				IdeLog.logError(UIEplPlugin.getDefault(), e);
			}
		}
		return null;
	}
	
	protected void saveMemento() {
		XMLMemento memento = XMLMemento.createWriteRoot(URLS);
		saveList(memento);
		File file = UIEplPlugin.getDefault().getStateLocation().append(URLS).addFileExtension(XML).toFile();
		try {
			FileWriter writer = new FileWriter(file);
			memento.save(writer);
		} catch (IOException e) {
			IdeLog.logError(UIEplPlugin.getDefault(), e);
		}

	}
	
	private final void loadList() {
		IMemento memento = getMemento();
		if (memento == null) {
			return;
		}
		IMemento[] list = memento.getChildren(URL);
		for(int i = 0; i < list.length; ++i) {
			combo.add(list[i].getTextData());
		}
	}
	
	protected final void saveList(IMemento memento) {
		String firstItem = combo.getText();
		IMemento child = memento.createChild(URL);
		child.putTextData(firstItem);
		
		String[] list = combo.getItems();
		for(int i = 0; i < list.length; ++i) {
			if (list[i].equals(firstItem)) {
				continue;
			}
			child = memento.createChild(URL);
			child.putTextData(list[i]);
		}
	}

}
