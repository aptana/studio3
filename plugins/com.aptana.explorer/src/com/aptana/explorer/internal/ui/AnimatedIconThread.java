package com.aptana.explorer.internal.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolItem;

import com.aptana.explorer.ExplorerPlugin;

public class AnimatedIconThread extends Thread
{

	private int i = 0;
	private String lastImagePath;
	private Display currentDisplay;
	private ToolItem currentItem;
	private String[] imagePaths;
	private Boolean running = true;

	public AnimatedIconThread(String defaultImagePath, String[] animatedImagePaths, Display display, ToolItem item)
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

	public void terminate()
	{
		running = false;
	}

}
