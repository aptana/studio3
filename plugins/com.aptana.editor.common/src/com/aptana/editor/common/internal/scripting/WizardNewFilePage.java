package com.aptana.editor.common.internal.scripting;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.BundleManager;
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
		// TODO Generate contents from scripting bundles!
		TemplateElement[] templates = (TemplateElement[]) BundleManager.getInstance().getCommands(new IModelFilter()
		{
			
			@Override
			public boolean include(AbstractElement element)
			{
				// TODO Filter by matching filetype pattern!
				return element instanceof TemplateElement;
			}
		});
		if (templates != null && templates.length > 0)
		{
			for (TemplateElement template : templates)
			{
				String pattern = template.getFiletype();
				pattern = pattern.replace(Pattern.quote("."), Matcher.quoteReplacement("\\.")).replace(Pattern.quote("*"), Matcher.quoteReplacement(".+"));
				if (getFileName().matches(pattern))
				{
					CommandResult result = template.execute();
					String output = result.getOutputString();
					return new ByteArrayInputStream(output.getBytes());
				}
			}
		}
		return super.getInitialContents();
	}

}
