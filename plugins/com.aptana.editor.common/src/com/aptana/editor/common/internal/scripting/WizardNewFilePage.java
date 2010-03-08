package com.aptana.editor.common.internal.scripting;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.IModelFilter;
import com.aptana.scripting.model.TemplateElement;

public class WizardNewFilePage extends WizardNewFileCreationPage
{

	public WizardNewFilePage(String pageName, IStructuredSelection selection)
	{
		super(pageName, selection);
	}

	@Override
	protected InputStream getInitialContents()
	{
		CommandElement[] commands = BundleManager.getInstance().getCommands(new IModelFilter()
		{

			@Override
			public boolean include(AbstractElement element)
			{
				// TODO Filter here by matching the filetype?
				return element instanceof TemplateElement;
			}
		});
		if (commands != null && commands.length > 0)
		{
			for (CommandElement command : commands)
			{
				if (!(command instanceof TemplateElement))
					continue;
				TemplateElement template = (TemplateElement) command;
				String pattern = template.getFiletype();
				if (pattern.isEmpty())
					continue;

				// TODO This code was copy-pasted from BundleManager.getTopLevelScope
				// Escape periods in pattern (for regexp)
				pattern = pattern.replaceAll("\\.", "\\\\."); //$NON-NLS-1$ //$NON-NLS-2$

				// Replace * wildcard pattern with .+? regexp
				pattern = pattern.replaceAll("\\*", "\\.\\+\\?"); //$NON-NLS-1$ //$NON-NLS-2$
				if (getFileName().matches(pattern))
				{
					CommandContext context = template.createCommandContext();
					context.getMap().put("TM_NEW_FILE_BASENAME", getFileBaseName()); //$NON-NLS-1$
					CommandResult result = template.execute(context);
					String output = result.getOutputString();
					return new ByteArrayInputStream(output.getBytes());
				}
			}
		}
		return super.getInitialContents();
	}

	private String getFileBaseName()
	{
		String filename = getFileName();
		int index = filename.indexOf('.');
		if (index != -1)
		{
			return filename.substring(0, index);
		}
		return filename;
	}

}
