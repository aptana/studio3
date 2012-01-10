/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     		IBM Corporation - initial API and implementation
 * 			Shalom Gibly <sgibly@aptana.com>
 *******************************************************************************/
package com.aptana.formatter.ui.preferences;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.aptana.editor.dtd.text.rules.DTDNameDetector;
import com.aptana.formatter.preferences.IPreferenceDelegate;
import com.aptana.formatter.ui.widgets.CListViewer;

/**
 * A list of items with an 'Add' and 'Remove' buttons that allow adding and removing items from it.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
@SuppressWarnings("restriction")
public class AddRemoveList implements Listener
{
	private CListViewer listViewer;
	private Composite control;
	private Button addButton;
	private Button removeButton;
	private ListContentProvider contentProvider;

	/**
	 * Constructs a new AddRemoveList
	 * 
	 * @param parent
	 */
	public AddRemoveList(Composite parent)
	{
		createContents(parent);
	}

	/**
	 * Returns the control for this list.
	 * 
	 * @return A {@link Composite} control.
	 */
	public Control getControl()
	{
		return control;
	}

	/**
	 * Returns the inner list that is held by this instance.
	 * 
	 * @return A {@link org.eclipse.jface.viewers.ListViewer}
	 */
	public CListViewer getList()
	{
		return listViewer;
	}

	/**
	 * Creates the contents of this list.
	 */
	protected void createContents(Composite parent)
	{
		control = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		control.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessVerticalSpace = true;
		control.setLayoutData(data);

		listViewer = new CListViewer(control, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		listViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateButtonsState();
			}
		});
		contentProvider = new ListContentProvider();
		listViewer.setContentProvider(contentProvider);
		listViewer.setComparator(new ViewerComparator());

		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 300;
		listViewer.getControl().setLayoutData(data);

		Composite groupComponent = new Composite(control, SWT.NULL);
		GridLayout groupLayout = new GridLayout();
		groupLayout.marginWidth = 0;
		groupLayout.marginHeight = 0;
		groupComponent.setLayout(groupLayout);
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		groupComponent.setLayoutData(data);

		addButton = new Button(groupComponent, SWT.PUSH);
		addButton.setText(Messages.AddRemoveList_add);
		addButton.addListener(SWT.Selection, this);
		addButton.setLayoutData(data);
		setButtonLayoutData(addButton);

		removeButton = new Button(groupComponent, SWT.PUSH);
		removeButton.setText(Messages.AddRemoveList_remove);
		removeButton.addListener(SWT.Selection, this);
		setButtonLayoutData(removeButton);

		// Spacer
		Label label = new Label(control, SWT.LEFT);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		updateButtonsState();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	public void handleEvent(Event event)
	{
		if (event.widget == addButton)
		{
			promptForNewElements();
		}
		else if (event.widget == removeButton)
		{
			removeSelectedElements();
		}
		updateButtonsState();
	}

	/**
	 * Update the buttons state
	 */
	protected void updateButtonsState()
	{
		boolean elementSelected = listViewer.getList().getSelectionIndex() != -1;
		removeButton.setEnabled(elementSelected);
	}

	/**
	 * Display a prompt to allow adding new elements.
	 */
	protected void promptForNewElements()
	{
		IInputValidator inputValidator = new IInputValidator()
		{
			public String isValid(String newText)
			{
				IWordDetector dtdDetector = new DTDNameDetector();
				String[] inputWords = newText.split(" |,"); //$NON-NLS-1$
				for (String word : inputWords)
				{
					// Only letters, digits, spaces and commas are valid here.
					int length = word.length();
					for (int i = 0; i < length; i++)
					{
						char c = word.charAt(i);
						if (i == 0)
						{
							if (!dtdDetector.isWordStart(c))
							{
								return NLS.bind(Messages.AddRemoveList_invalidBeginTagChar, word);
							}
						}
						else if (!dtdDetector.isWordPart(c))
						{
							return NLS.bind(Messages.AddRemoveList_invalidCharInTag, word);
						}
					}
				}
				return null;
			}
		};
		InputDialog dialog = new InputDialog(listViewer.getList().getShell(), Messages.AddRemoveList_inputMessageTitle,
				Messages.AddRemoveList_inputMessageText, "", inputValidator); //$NON-NLS-1$
		if (dialog.open() == Window.OK)
		{
			String value = dialog.getValue().trim();
			if (value.length() == 0)
			{
				return;
			}
			// At this point we know that the elements have been validated by the input-validator.
			value = value.replaceAll(",", " "); //$NON-NLS-1$ //$NON-NLS-2$
			String[] values = value.split(" "); //$NON-NLS-1$
			// Filter out any duplicates that we might have before setting the input.
			Set<Object> elementsSet = new TreeSet<Object>();
			Object[] existingElements = contentProvider.getElements(null);
			for (Object o : existingElements)
			{
				elementsSet.add(o);
			}
			for (String v : values)
			{
				if (v.trim().length() != 0)
				{
					elementsSet.add(v.toLowerCase());
				}
			}
			listViewer.setInput(elementsSet.toArray(new Object[elementsSet.size()]));
		}
	}

	/**
	 * Remove the selected elements from the list
	 */
	protected void removeSelectedElements()
	{
		Set<Object> elementsSet = new HashSet<Object>();
		Object[] existingElements = contentProvider.getElements(null);
		for (Object o : existingElements)
		{
			elementsSet.add(o);
		}
		Object[] removedElements = ((IStructuredSelection) listViewer.getSelection()).toArray();
		for (Object o : removedElements)
		{
			elementsSet.remove(o);
		}
		if (elementsSet.size() != existingElements.length)
		{
			listViewer.setInput(elementsSet.toArray(new Object[elementsSet.size()]));
		}
	}

	/**
	 * Sets the <code>GridData</code> on the specified button to be one that is spaced for the current dialog page
	 * units. The method <code>initializeDialogUnits</code> must be called once before calling this method for the first
	 * time.
	 * 
	 * @param button
	 *            the button to set the <code>GridData</code>
	 * @return the <code>GridData</code> set on the specified button
	 */
	protected GridData setButtonLayoutData(Button button)
	{
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		PixelConverter converter = new PixelConverter(button);
		int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);
		button.setLayoutData(data);
		return data;
	}

	/**
	 * The add-remove list content provider.
	 */
	private class ListContentProvider implements IStructuredContentProvider
	{
		private String[] elements = new String[0];

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return elements;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{
			elements = null;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 * java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			if (newInput instanceof Object[])
			{
				Object[] in = (Object[]) newInput;
				this.elements = new String[in.length];
				for (int i = 0; i < in.length; i++)
				{
					String value = in[i].toString().toLowerCase();
					this.elements[i] = value;
				}
			}
			else
			{
				if (newInput != null)
				{
					String str = newInput.toString().toLowerCase();
					this.elements = str.split(IPreferenceDelegate.PREFERECE_DELIMITER);
				}
				else
				{
					this.elements = new String[0];
				}
			}
		}
	}
}
