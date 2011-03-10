package com.aptana.git.ui.hyperlink;

import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import com.aptana.git.ui.GitUIPlugin;

/**
 * Tracks git processes in the console, detecting hyperlinks to files.
 * 
 * @author cwilliams
 */
public class GitConsoleLineTracker implements IConsoleLineTracker
{

	private IConsole console;
	private HyperlinkDetector detector;

	public void init(IConsole console)
	{
		this.console = console;
		this.detector = new HyperlinkDetector();
	}

	public void lineAppended(IRegion line)
	{
		try
		{
			String lineContents = console.getDocument().get(line.getOffset(), line.getLength());

			IHyperlink[] links = detector.detectHyperlinks(lineContents);
			if (links != null)
			{
				for (IHyperlink link : links)
				{
					console.addLink(new WrappingConsoleHyperlink(link), line.getOffset()
							+ link.getHyperlinkRegion().getOffset(), link.getHyperlinkRegion().getLength());
				}
			}
		}
		catch (BadLocationException e)
		{
			GitUIPlugin.logError(e);
		}
	}

	public void dispose()
	{
		this.detector = null;
		this.console = null;
	}

	/**
	 * Wraps a JFace hyperlink to conform to the console IHyperlink interface.
	 * 
	 * @author cwilliams
	 */
	private static class WrappingConsoleHyperlink implements org.eclipse.ui.console.IHyperlink
	{
		private IHyperlink link;

		WrappingConsoleHyperlink(IHyperlink link)
		{
			this.link = link;
		}

		public void linkEntered()
		{
		}

		public void linkExited()
		{
		}

		public void linkActivated()
		{
			link.open();
		}
	}
}
