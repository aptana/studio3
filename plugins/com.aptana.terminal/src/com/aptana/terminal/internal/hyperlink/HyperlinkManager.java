/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.terminal.internal.hyperlink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.tm.terminal.model.ITerminalTextDataReadOnly;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.terminal.TerminalPlugin;
import com.aptana.terminal.hyperlink.IHyperlinkDetector;

/**
 * This class manages a set of hyperlinks for a given Terminal. An instance should be shared between a TextCanvas and a
 * ThemedLineRenderer. TWe detect and store the hyperlinks by line here, the renderer uses the set to draw underlines
 * for the hyperlinks; the TextCanvas detects clicks/hover on the links to activate them or show a different cursor.
 * 
 * @author cwilliams
 */
public class HyperlinkManager
{
	/**
	 * Constant for returning no hyperlinks.
	 */
	private static final IHyperlink[] NO_HYPERLINKS = new IHyperlink[0];

	/**
	 * Constants related to the hyperlink detector extension point.
	 */
	private static final String CLASS = "class"; //$NON-NLS-1$
	private static final String HYPERLINK_DETECTOR_EXT_PT = TerminalPlugin.PLUGIN_ID + ".terminalHyperlinkDetectors"; //$NON-NLS-1$

	/**
	 * a Map of line numbers to links on the line.
	 */
	private Map<Integer, IHyperlink[]> fLinks;

	/**
	 * A simple caching mechanism to return the cache links for a given line if we already calculated it in our last
	 * call.
	 */
	private int fLastHash;

	/*
	 * The array of hyperlink detectors from the extension point.
	 */
	private IHyperlinkDetector[] fDetectors;
	private ITerminalTextDataReadOnly fTextData;

	public HyperlinkManager(ITerminalTextDataReadOnly textData)
	{
		fTextData = textData;
		fLinks = new HashMap<Integer, IHyperlink[]>();
	}

	private synchronized IHyperlinkDetector[] getHyperlinkDetectors()
	{
		if (fDetectors == null)
		{
			fDetectors = new IHyperlinkDetector[0];
			IConfigurationElement[] config = RegistryFactory.getRegistry().getConfigurationElementsFor(
					HYPERLINK_DETECTOR_EXT_PT);
			if (!ArrayUtil.isEmpty(config))
			{
				List<IHyperlinkDetector> result = new ArrayList<IHyperlinkDetector>(config.length);
				for (IConfigurationElement c : config)
				{
					try
					{
						result.add((IHyperlinkDetector) c.createExecutableExtension(CLASS));
					}
					catch (CoreException e)
					{
						TerminalPlugin.log(e);
					}
				}
				fDetectors = result.toArray(new IHyperlinkDetector[result.size()]);
			}
		}
		return fDetectors;
	}

	public synchronized IHyperlink[] searchLineForHyperlinks(int line)
	{
		String text = getTerminalText(line);
		int hash = line * 31 + text.hashCode();
		if (hash == fLastHash)
		{
			// Return the array of links we already calculated for this text
			return fLinks.get(Integer.valueOf(line));
		}
		fLastHash = hash;

		if (!StringUtil.isEmpty(text))
		{
			// Detect new links
			IHyperlink[] newLinks = NO_HYPERLINKS;
			IHyperlinkDetector[] detectors = getHyperlinkDetectors();
			if (!ArrayUtil.isEmpty(detectors))
			{
				List<IHyperlink> list = new ArrayList<IHyperlink>(detectors.length);
				for (IHyperlinkDetector detector : detectors)
				{
					IHyperlink[] partialNewLinks = detector.detectHyperlinks(text);
					if (partialNewLinks != null && partialNewLinks.length > 0)
					{
						list.addAll(Arrays.asList(partialNewLinks));
					}
				}
				newLinks = list.toArray(new IHyperlink[list.size()]);
			}
			// Update map
			fLinks.put(Integer.valueOf(line), newLinks);
			return newLinks;
		}
		return NO_HYPERLINKS;
	}

	private String getTerminalText(int line)
	{
		char[] c = fTextData.getChars(line);
		if (c != null && c.length > 0)
		{
			return new String(c);
		}
		return StringUtil.EMPTY;
	}
}
