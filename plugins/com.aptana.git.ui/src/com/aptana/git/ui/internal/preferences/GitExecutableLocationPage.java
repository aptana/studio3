package com.aptana.git.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IPreferenceConstants;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.ui.GitUIPlugin;

public class GitExecutableLocationPage extends PreferencePage implements IWorkbenchPreferencePage
{

	private FileFieldEditor fileEditor;

	public GitExecutableLocationPage()
	{
		super();
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setFont(parent.getFont());

		fileEditor = new FileFieldEditor("", "Git Executable", true, FileFieldEditor.VALIDATE_ON_KEY_STROKE, composite)
		{
			@Override
			protected boolean checkState()
			{
				boolean ok = super.checkState();
				if (!ok)
					return ok;

				// Now check that the executable is ok
				String text = getTextControl().getText();
				if (text != null && text.trim().length() > 0)
				{
					if (!GitExecutable.acceptBinary(text))
					{
						showErrorMessage(NLS.bind("This path is not a valid git v{0} or higher binary.",
								GitExecutable.MIN_GIT_VERSION));
						return false;
					}
				}

				clearErrorMessage();
				return true;
			}
		};
		IEclipsePreferences prefs = new InstanceScope().getNode(GitPlugin.getPluginId());
		String path = prefs.get(IPreferenceConstants.GIT_EXECUTABLE_PATH, null);
		if (path == null)
			path = "";
		fileEditor.setStringValue(path);
		fileEditor.setPage(this);

		applyDialogFont(composite);

		return composite;
	}

	public void init(IWorkbench workbench)
	{
	}

	@Override
	protected void performDefaults()
	{
		IEclipsePreferences prefs = new DefaultScope().getNode(GitPlugin.getPluginId());
		String path = prefs.get(IPreferenceConstants.GIT_EXECUTABLE_PATH, null);
		if (path == null)
			path = "";
		fileEditor.setStringValue(path);

		super.performDefaults();
	}

	@Override
	public boolean performOk()
	{
		IEclipsePreferences prefs = new InstanceScope().getNode(GitPlugin.getPluginId());
		String value = fileEditor.getStringValue();
		if (value != null && value.trim().length() == 0)
		{
			if (prefs.get(IPreferenceConstants.GIT_EXECUTABLE_PATH, null) != null)
				prefs.remove(IPreferenceConstants.GIT_EXECUTABLE_PATH);
		}
		else
			prefs.put(IPreferenceConstants.GIT_EXECUTABLE_PATH, value);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			GitUIPlugin.logError(e.getMessage(), e);
		}
		return super.performOk();
	}

}
