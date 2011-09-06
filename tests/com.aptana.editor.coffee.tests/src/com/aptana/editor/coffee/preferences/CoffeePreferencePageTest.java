package com.aptana.editor.coffee.preferences;

import junit.framework.TestCase;

public class CoffeePreferencePageTest extends TestCase
{
//
//	/*
//	 * @see junit.framework.TestCase#setUp()
//	 */
//	protected void setUp() throws Exception
//	{
//		super.setUp();
//		IUIContext ui = getUI();
//		ui.ensureThat(new WorkbenchLocator().hasFocus());
//		ui.ensureThat(ViewLocator.forName("Welcome").isClosed());
//	}
//
//	@Override
//	protected void tearDown() throws Exception
//	{
//		super.tearDown();
//	}
//
//	/**
//	 * Main test method.
//	 */
//	public void testCoffeePreferencePage() throws Exception
//	{
//		IUIContext ui = getUI();
//
//		openPreferencesDialog(ui);
//		ui.click(new FilteredTreeItemLocator("Aptana Studio/Editors/Coffeescript"));
//		ui.click(new ComboItemLocator("Use Global Editor Defaults"));
//		ui.click(new ButtonLocator("OK"));
//		ui.wait(new ShellDisposedCondition("Preferences"));
//
//		// Re-open and verify that the settings took hold
//		openPreferencesDialog(ui);
//		ui.click(new FilteredTreeItemLocator("Aptana Studio/Editors/Coffeescript"));
//		// Check the combo value!
//		ui.assertThat(new ComboLocator().hasText("Use Global Editor Defaults"));
//		ui.click(new ButtonLocator("Cancel"));
//		ui.wait(new ShellDisposedCondition("Preferences"));
//	}
//
//	private void openPreferencesDialog(IUIContext ui) throws WidgetSearchException
//	{
//		/*
//		 * On the Mac, the preference menu item is in the application menu which is not visible to the WT runtime. To
//		 * work around this, we invoke the menu via the associated hot-key.
//		 */
//		if (Platform.OS_MACOSX.equals(Platform.getOS()))
//		{
//			// For whatever reason, even the keybinding ain't working. hack it!
//			// ui.keyClick(WT.COMMAND, ',');
//			runInUI(new Runnable()
//			{
//				public void run()
//				{
//					PreferencesUtil.createPreferenceDialogOn(null,
//							"com.aptana.editor.coffee.preferences.CoffeePreferencePage", null, null).open();
//				}
//			});
//		}
//		else
//		{
//			ui.click(new MenuItemLocator("Window/Preferences"));
//		}
//		ui.wait(new ShellShowingCondition("Preferences"));
//	}
//
//	private void runInUI(Runnable runnable)
//	{
//		Display display = PlatformUI.getWorkbench().getDisplay();
//		if (display.getThread() == Thread.currentThread())
//		{
//			// tests are running in the UI thread, just start another thread...
//			new Thread(runnable).start();
//		}
//		else
//		{
//			// Not in the UI thread
//			display.asyncExec(runnable);
//		}
//	}

}
