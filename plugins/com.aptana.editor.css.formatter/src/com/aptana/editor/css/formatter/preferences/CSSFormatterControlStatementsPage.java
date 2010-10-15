package com.aptana.editor.css.formatter.preferences;

import java.net.URL;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.aptana.editor.css.formatter.CSSFormatterConstants;
import com.aptana.formatter.ui.CodeFormatterConstants;
import com.aptana.formatter.ui.FormatterModifyTabPage;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.ui.util.SWTFactory;

public class CSSFormatterControlStatementsPage extends FormatterModifyTabPage
{
	private final String[] tabOptionItems = new String[] { CodeFormatterConstants.SPACE, CodeFormatterConstants.TAB };
	private final String[] tabOptionNames = new String[] { "Spaces only", "Tabs only" };

	public CSSFormatterControlStatementsPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group generalGroup = SWTFactory.createGroup(parent, "General Settings", 2, 1, GridData.FILL_HORIZONTAL);
		final Combo tabOptions = manager.createCombo(generalGroup, CSSFormatterConstants.FORMATTER_TAB_CHAR, "Tab policy", tabOptionItems,
				tabOptionNames);
		final Text indentationSize = manager.createNumber(generalGroup, CSSFormatterConstants.FORMATTER_INDENTATION_SIZE, "Indentation size:");
		final Text tabSize = manager.createNumber(generalGroup, CSSFormatterConstants.FORMATTER_TAB_SIZE, "Tab size:");

		tabSize.addModifyListener(new ModifyListener()
		{
			
			public void modifyText(ModifyEvent e)
			{
				int index = tabOptions.getSelectionIndex();
				if (index >=0 )
				{
					final boolean tabMode = CodeFormatterConstants.TAB.equals(tabOptionItems[index]);
					if(tabMode){
						indentationSize.setText(tabSize.getText());
					}
					
				}
				
			}
		});
		new TabOptionHandler(manager, tabOptions, indentationSize);
	}

	/**
	 * Listens to changes in the type of tab selected.
	 */
	private class TabOptionHandler extends SelectionAdapter implements IFormatterControlManager.IInitializeListener
	{

		private IFormatterControlManager manager;
		private Combo tabOptions;
		private Text indentationSize;

		/**
		 * Constructor.
		 * 
		 * @param controlManager
		 */
		public TabOptionHandler(IFormatterControlManager controlManager, Combo tabOptions, Text indentationSize)
		{
			this.manager = controlManager;
			this.tabOptions = tabOptions;
			this.indentationSize = indentationSize;
			tabOptions.addSelectionListener(this);
			manager.addInitializeListener(this);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e)
		{
			int index = tabOptions.getSelectionIndex();
			if (index >= 0)
			{
				final boolean tabMode = CodeFormatterConstants.TAB.equals(tabOptionItems[index]);
				manager.enableControl(indentationSize, !tabMode);
			}
		}

		public void initialize()
		{
			final boolean tabMode = CodeFormatterConstants.TAB.equals(manager
					.getString(CSSFormatterConstants.FORMATTER_TAB_CHAR));
			manager.enableControl(indentationSize, !tabMode);
		}
	}

	
	
	protected URL getPreviewContent()
	{
		return getClass().getResource("control-statements-preview.css");
	}
}
