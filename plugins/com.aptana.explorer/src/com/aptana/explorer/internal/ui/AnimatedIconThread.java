/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.internal.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolItem;

import com.aptana.explorer.ExplorerPlugin;

class AnimatedIconThread extends Thread
{

	private int i = 0;
	private String lastImagePath;
	private Display currentDisplay;
	private ToolItem currentItem;
	private String[] imagePaths;
	private Boolean running = true;

	protected AnimatedIconThread(String defaultImagePath, String[] animatedImagePaths, Display display, ToolItem item)
	{
		imagePaths = animatedImagePaths;
		currentDisplay = display;
		currentItem = item;
		lastImagePath = defaultImagePath;
	}

	@Override
	public void run()
	{

		while (running)
		{
			try
			{
				currentDisplay.syncExec(new Runnable()
				{
					public void run()
					{
						currentItem.setImage(ExplorerPlugin.getImage(imagePaths[i]));
					}
				});
				Thread.sleep(800);
				i = (i + 1) % imagePaths.length;
			}
			catch (Exception e)
			{

			}
		}
		currentDisplay.syncExec(new Runnable()
		{
			public void run()
			{
				currentItem.setImage(ExplorerPlugin.getImage(lastImagePath));
			}
		});

	}

	protected void terminate()
	{
		running = false;
	}

}
