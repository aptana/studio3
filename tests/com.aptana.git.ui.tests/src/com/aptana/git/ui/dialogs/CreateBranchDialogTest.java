package com.aptana.git.ui.dialogs;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRevSpecifier;
import com.aptana.ui.util.UIUtils;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.swt.locator.eclipse.WorkbenchLocator;

public class CreateBranchDialogTest extends UITestCaseSWT
{

	private Mockery context;

	/*
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		IUIContext ui = getUI();
		ui.ensureThat(new WorkbenchLocator().hasFocus());
		ui.ensureThat(ViewLocator.forName("Welcome").isClosed());

		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
	}

	@Override
	protected void tearDown() throws Exception
	{
		context = null;
		super.tearDown();
	}

	public void testAutoTrackIfStartingPointIsRemoteBranchAndGitConfigReportsAutoSetupMerge() throws Exception
	{
		IUIContext ui = getUI();

		final GitRepository repo = context.mock(GitRepository.class);
		context.checking(new Expectations()
		{
			{
				oneOf(repo).autoSetupMerge();
				will(returnValue(true));

				GitRevSpecifier revSpec = new GitRevSpecifier("development");
				oneOf(repo).headRef();
				will(returnValue(revSpec));

				Set<String> branchNames = new HashSet<String>();
				branchNames.add("origin/master");

				oneOf(repo).allSimpleRefs();
				will(returnValue(branchNames));

				Set<String> remotes = new HashSet<String>();
				remotes.add("origin");

				oneOf(repo).remotes();
				will(returnValue(remotes));
			}
		});
		final CreateBranchDialog[] dialogs = new CreateBranchDialog[1];
		runInUI(new Runnable()
		{
			public void run()
			{
				dialogs[0] = new CreateBranchDialog(UIUtils.getActiveShell(), repo);
				dialogs[0].setBlockOnOpen(false);
				dialogs[0].open();
			}
		});

		// Make sure dialog is open
		ui.wait(new ShellShowingCondition("Create New Branch"));
		// Make sure track button is not selected
		ui.assertThat(new ButtonLocator("Track").isSelected(false));
		// set starting point to a remote branch
		IWidgetReference ref = (IWidgetReference) ui.find(new LabeledTextLocator("Start point: "));
		final Text text = (Text) ref.getWidget();
		runInUI(new Runnable()
		{
			public void run()
			{
				text.setText("");
			}
		});
		ui.wait(new LabeledTextLocator("Start point: ").hasText(""));

		ui.click(new LabeledTextLocator("Start point: "));
		ui.enterText("origin/master");
		// now make sure that we set up tracking by default
		ui.assertThat(new ButtonLocator("Track").isSelected());

		runInUI(new Runnable()
		{
			public void run()
			{
				dialogs[0].close();
			}
		});

		context.assertIsSatisfied();
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
