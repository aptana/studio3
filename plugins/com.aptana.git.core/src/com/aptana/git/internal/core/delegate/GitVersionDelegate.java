package com.aptana.git.internal.core.delegate;

import org.eclipse.core.runtime.IPath;

import com.aptana.configurations.processor.AbstractProcessorDelegate;
import com.aptana.core.util.ProcessUtil;
import com.aptana.git.core.model.GitExecutable;

/**
 * A processor delegate class that returns the configured/installed Git version. This delegate invoke the
 * {@link GitExecutable} to get the result.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class GitVersionDelegate extends AbstractProcessorDelegate
{
	private static final String GIT = "git"; //$NON-NLS-1$

	public GitVersionDelegate()
	{
		supportedCommands.put(VERSION_COMMAND, "--version"); //$NON-NLS-1$
	}

	@Override
	public String getSupportedApplication()
	{
		return GIT;
	}

	@Override
	public Object runCommand(String commandType, IPath workingDir)
	{
		String command = supportedCommands.get(commandType);
		if (command != null)
		{
			IPath gitPath = GitExecutable.instance().path();
			if (gitPath != null)
			{
				return ProcessUtil.outputForCommand(gitPath.toString(), workingDir, command);
			}
		}
		return null;
	}
}
