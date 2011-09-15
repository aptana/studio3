/**
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import java.util.HashSet;
import java.util.Set;

/**
 * Controls the visibility of the find bar. Any change in the visibility should be redirected to this class (which will
 * later make the proper changes in the find bar decorators).
 * 
 * @author fabioz
 */
public class FindBarVisibilityControl
{

	/**
	 * Find bar decorators registered.
	 */
	private final Set<FindBarDecorator> decs = new HashSet<FindBarDecorator>();

	/**
	 * The current visibility state (always starts invisible).
	 */
	private boolean visible = false;

	/**
	 * Registers the find bar decorator so that the visibility is kept updated among all the find bar decorators.
	 */
	public void register(FindBarDecorator findBarDecorator)
	{
		if (visible)
		{
			findBarDecorator.showFindBar();
		}
		decs.add(findBarDecorator);
	}

	/**
	 * Unregisters a decorators (should only be done when the editor is disposed too).
	 */
	public void unregister(FindBarDecorator findBarDecorator)
	{
		decs.remove(findBarDecorator);
	}

	/**
	 * Updates the visibility for all the find bar decorators (and updates the settings from the eclipse settings --
	 * saving to the eclipse settings is done during the find action or some other change).
	 */
	public void setVisible(boolean enable)
	{
		try
		{
			if (enable)
			{
				if (!this.visible)
				{
					FindBarDecorator.updateFromEclipseFindSettings();
				}

				for (FindBarDecorator d : decs)
				{
					d.showFindBar();
				}
			}
			else
			{
				for (FindBarDecorator d : decs)
				{
					d.hideFindBar();
				}
			}
		}
		finally
		{
			this.visible = enable;
		}
	}

}
