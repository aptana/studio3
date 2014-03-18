package com.aptana.editor.coffee.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.junit.Ignore;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.MenuItemLocator;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.ComboItemLocator;
import com.windowtester.runtime.swt.locator.ComboLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.swt.locator.eclipse.WorkbenchLocator;

@Ignore("Need to fix up UI tests!")
public class CoffeePreferencePageTest extends UITestCaseSWT
{

	/*
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		IUIContext ui = getUI();
		ui.ensureThat(new WorkbenchLocator().hasFocus());
		ui.ensureThat(ViewLocator.forName("Welcome").isClosed());
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * Main test method.
	 */
	public void testCoffeePreferencePage() throws Exception
	{
		IUIContext ui = getUI();

		openPreferencesDialog(ui);
		ui.click(new FilteredTreeItemLocator("(Aptana|Titanium) Studio/Editors/Coffeescript"));
		ui.click(new ComboItemLocator("Use Global Editor Defaults"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Preferences"));

		// Re-open and verify that the settings took hold
		openPreferencesDialog(ui);
		ui.click(new FilteredTreeItemLocator("(Aptana|Titanium) Studio/Editors/Coffeescript"));
		// Check the combo value!
		ui.assertThat(new ComboLocator().hasText("Use Global Editor Defaults"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Preferences"));
	}

	private void openPreferencesDialog(IUIContext ui) throws WidgetSearchException
	{
		/*
		 * On the Mac, the preference menu item is in the application menu which is not visible to the WT runtime. To
		 * work around this, we invoke the menu via the associated hot-key.
		 */
		if (Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			// For whatever reason, even the keybinding ain't working. hack it!
			// ui.keyClick(WT.COMMAND, ',');
			runInUI(new Runnable()
			{
				public void run()
				{
					PreferencesUtil.createPreferenceDialogOn(null,
							"com.aptana.editor.coffee.preferences.CoffeePreferencePage", null, null).open();
				}
			});
		}
		else
		{
			ui.click(new MenuItemLocator("Window/Preferences"));
		}
		ui.wait(new ShellShowingCondition("Preferences"));
	}

	private void runInUI(Runnable runnable)
	{
		Display display = PlatformUI.getWorkbench().getDisplay();
		if (display.getThread() == Thread.currentThread())
		{
			// tests are running in the UI thread, just start another thread...
			new Thread(runnable).start();
		}
		else
		{
			// Not in the UI thread
			display.asyncExec(runnable);
		}
	}

}
