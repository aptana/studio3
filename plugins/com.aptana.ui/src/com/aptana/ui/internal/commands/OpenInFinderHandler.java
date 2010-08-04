package com.aptana.ui.internal.commands;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;

import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.ProcessUtil;
import com.aptana.ui.UIPlugin;

public class OpenInFinderHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (event == null)
		{
			return null;
		}
		Object context = event.getApplicationContext();
		if (context instanceof EvaluationContext)
		{
			EvaluationContext evContext = (EvaluationContext) event.getApplicationContext();
			Object input = evContext.getVariable("showInInput"); //$NON-NLS-1$
			if (input instanceof IFileEditorInput)
			{
				IFileEditorInput fei = (IFileEditorInput) input;
				open(fei.getFile().getLocationURI());
			}
			else if (input instanceof IURIEditorInput)
			{
				IURIEditorInput uriInput = (IURIEditorInput) input;
				open(uriInput.getURI());
			}
			else
			{
				@SuppressWarnings("unchecked")
				List<IResource> selectedFiles = (List<IResource>) evContext.getDefaultVariable();
				for (IResource selected : selectedFiles)
				{
					open(selected.getLocationURI());
				}
			}
		}
		return null;
	}

	private boolean open(URI uri)
	{
		if (uri == null)
		{
			return false;
		}
		if (!"file".equalsIgnoreCase(uri.getScheme())) //$NON-NLS-1$
		{
			return false;
		}
		File file = new File(uri);
		if (Platform.getOS().equals(Platform.OS_MACOSX))
		{
			return openInFinder(file);
		}
		else if (Platform.getOS().equals(Platform.OS_WIN32))
		{
			return openInWindowsExplorer(file);
		}
		return openOnLinux(file);
	}

	private boolean openOnLinux(File file)
	{
		// TODO Do we also need to try 'gnome-open' or 'dolphin' if nautilus fails?
		Map<Integer, String> result = ProcessUtil.runInBackground("nautilus", null, "\"" //$NON-NLS-1$ //$NON-NLS-2$
				+ file.getAbsolutePath() + "\""); //$NON-NLS-1$
		if (result == null || result.isEmpty())
		{
			return false;
		}
		return result.keySet().iterator().next() == 0;
	}

	private boolean openInWindowsExplorer(File file)
	{
		String explorer = PlatformUtil.expandEnvironmentStrings("%SystemRoot%\\explorer.exe"); //$NON-NLS-1$
		Map<Integer, String> result = ProcessUtil.runInBackground(explorer, null, "/select,\"" //$NON-NLS-1$
				+ file.getAbsolutePath() + "\""); //$NON-NLS-1$
		if (result == null || result.isEmpty())
		{
			return false;
		}
		return result.keySet().iterator().next() == 0;
	}

	private boolean openInFinder(File file)
	{
		String subcommand = "open"; //$NON-NLS-1$
		String path = file.getAbsolutePath();
		if (file.isFile())
		{
			subcommand = "reveal"; //$NON-NLS-1$
		}
		try
		{
			String appleScript = "tell application \"Finder\" to " + subcommand + " (POSIX file \"" + path + "\")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("AppleScript"); //$NON-NLS-1$
			engine.eval(appleScript);
			return true;
		}
		catch (ScriptException e)
		{
			UIPlugin.logError(e.getMessage(), e);
		}
		return false;
	}

}
