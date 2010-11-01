/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.configurationProcessors;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.mortbay.util.ajax.JSON;
import org.osgi.framework.Version;

import com.aptana.configurations.processor.AbstractConfigurationProcessor;
import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.processorDelegates.BaseVersionProcessor;

/**
 * A configuration processor for Ruby Gems management.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class GemsConfigurationProcessor extends AbstractConfigurationProcessor
{
	private static final String GEMS_ATTR = "gems"; //$NON-NLS-1$
	private static final String GEM_LIST = "gem list"; //$NON-NLS-1$

	@Override
	public ConfigurationStatus computeStatus(IProgressMonitor progressMonitor, Object attributes)
	{
		configurationStatus.removeAttribute(GEMS_ATTR);
		clearErrorAttributes();
		boolean returnAll = false;
		Map<String, String> attrItems = new HashMap<String, String>();
		if (attributes == null || !(attributes instanceof Object[]))
		{
			returnAll = true;
		}
		else
		{
			// Place the array values into a hash.
			Object[] attrArray = (Object[]) attributes;
			for (Object itemDef : attrArray)
			{
				Object[] def = null;
				if (!(itemDef instanceof Object[]) || (def = (Object[]) itemDef).length != 3)
				{
					applyErrorAttributes(Messages.GemsConfigurationProcessor_wrongGemsRequest);
					PortalUIPlugin.logError(new Exception(Messages.GemsConfigurationProcessor_wrongGemsRequest));
					return configurationStatus;
				}
				// We only use the first two arguments. The third is the installation site URL.
				attrItems.put((String) def[0], (String) def[1]);
			}
		}
		String shellCommandPath = getShellPath();
		if (shellCommandPath == null)
		{
			applyErrorAttributes(Messages.GemsConfigurationProcessor_missingShellError);
			PortalUIPlugin.logError(new Exception(Messages.GemsConfigurationProcessor_missingShellError));
			return configurationStatus;
		}
		// Do the actual processing
		configurationStatus.setStatus(ConfigurationStatus.PROCESSING);

		Map<String, Map<String, String>> itemsData = new HashMap<String, Map<String, String>>();

		// TODO: Shalom - This needs to be verified on all operating systems, and we might need to pass a -l to the
		// command.
		// This -l will cause the output to contains some more header lines which needs to be filtered to
		// get to the real gem list.
		String allGems = ProcessUtil.outputForCommand(shellCommandPath, null, new String[] { "-c", GEM_LIST }); //$NON-NLS-1$
		if (allGems != null)
		{
			String[] gemsArray = allGems.split(ResourceUtil.getLineSeparatorValue(null));
			for (String gem : gemsArray)
			{
				Gem g = parseGem(gem);
				if (g != null)
				{
					if (returnAll)
					{
						addGem(itemsData, attrItems, g);
					}
					else
					{
						if (attrItems.containsKey(g.name))
						{
							addGem(itemsData, attrItems, g);
						}
					}
				}
			}
			// Traverse what we have left in the original map that was created from the attributes and mark all gems as
			// 'missing'
			Set<String> missingGems = attrItems.keySet();
			for (String gemName : missingGems)
			{
				Map<String, String> gemInfo = new HashMap<String, String>(4);
				gemInfo.put(ITEM_EXISTS, NO);
				itemsData.put(gemName, gemInfo);
			}
			// Finally, set the gems data status into the configuration attribute
			configurationStatus.setAttribute(GEMS_ATTR, JSON.toString(itemsData));
			configurationStatus.setStatus(ConfigurationStatus.OK);
		}
		else
		{
			applyErrorAttributes(Messages.GemsConfigurationProcessor_errorInvokingGemList);
			return configurationStatus;
		}
		return configurationStatus;
	}

	@Override
	public ConfigurationStatus configure(IProgressMonitor progressMonitor, Object attributes)
	{
		// TODO Shalom: Parse the attributes and figure out what are the gems that the remote browser asks us to install
		return configurationStatus;
	}

	/*
	 * Add the gem to the itemsData map and remove it from the attrItems.
	 * @param itemsData
	 * @param attrItems
	 * @param gem
	 */
	private void addGem(Map<String, Map<String, String>> itemsData, Map<String, String> attrItems, Gem gem)
	{
		Version gemVersion = gem.version;
		Version requestedVersion = Version.parseVersion(attrItems.get(gem.name));
		String compatibility = (gemVersion.compareTo(requestedVersion) >= 0) ? COMPATIBILITY_OK : COMPATIBILITY_UPDATE;
		Map<String, String> gemInfo = new HashMap<String, String>(4);
		gemInfo.put(ITEM_EXISTS, YES);
		gemInfo.put(ITEM_VERSION, gemVersion.toString());
		gemInfo.put(ITEM_COMPATIBILITY, compatibility);
		gemInfo.put(ITEM_VERSION_OUTPUT, gem.rawOutput);
		itemsData.put(gem.name, gemInfo);
		// Remove the name from the original map. Eventually, we will be left with the gems we could not
		// locate in the system
		attrItems.remove(gem.name);
	}

	/*
	 * Parse a give gem version string into a Gem instance.
	 * @param gem The gem's version output
	 * @return A Gem instance; Null, in case the parse fail.
	 */
	private Gem parseGem(String gem)
	{
		if (gem == null)
			return null;
		// parse the gem name. It's the first part up to the open parentheses
		int openParenIndex = gem.indexOf('(');
		if (openParenIndex > 0)
		{
			String name = gem.substring(0, openParenIndex).trim();
			Version version = BaseVersionProcessor.parseVersion(gem);
			if (version != null)
			{
				return new Gem(name, version, gem);
			}
		}
		return null;
	}

	protected class Gem
	{
		protected String name;
		protected Version version;
		protected String rawOutput;

		protected Gem(String name, Version version, String rawOutput)
		{
			this.name = name;
			this.version = version;
			this.rawOutput = rawOutput;
		}
	}
}
