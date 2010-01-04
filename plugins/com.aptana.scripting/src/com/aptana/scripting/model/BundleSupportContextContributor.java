package com.aptana.scripting.model;

import java.io.File;

public class BundleSupportContextContributor implements ContextContributor
{

	@Override
	public void modifyContext(CommandElement command, CommandContext context)
	{
		File bundleDir = command.getOwningBundle().getBundleDirectory();	
		context.put("TM_BUNDLE_SUPPORT", new File(bundleDir, "lib").getAbsolutePath());		 //$NON-NLS-1$ //$NON-NLS-2$
	}

}
