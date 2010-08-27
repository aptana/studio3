package org.eclipse.tm.terminal.model;

import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * An interface for clients to detect and contribute hyperlinks to Terminals.
 * 
 * @author cwilliams
 */
public interface IHyperlinkDetector
{
	IHyperlink[] detectHyperlinks(String contents);
}
