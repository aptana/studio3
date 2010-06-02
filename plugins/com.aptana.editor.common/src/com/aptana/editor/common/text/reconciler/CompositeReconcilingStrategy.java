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
package com.aptana.editor.common.text.reconciler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;

/**
 * A reconciling strategy consisting of a sequence of internal reconciling strategies. By default, all requests are
 * passed on to the contained strategies.
 */
public class CompositeReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension
{

	/** The list of internal reconciling strategies. */
	private IReconcilingStrategy[] fStrategies;

	/**
	 * Creates a new, empty composite reconciling strategy.
	 */
	public CompositeReconcilingStrategy()
	{
	}

	/**
	 * Sets the reconciling strategies for this composite strategy.
	 * 
	 * @param strategies
	 *            the strategies to be set or <code>null</code>
	 */
	public void setReconcilingStrategies(IReconcilingStrategy[] strategies)
	{
		fStrategies = strategies;
	}

	/**
	 * Returns the previously set stratgies or <code>null</code>.
	 * 
	 * @return the contained strategies or <code>null</code>
	 */
	public IReconcilingStrategy[] getReconcilingStrategies()
	{
		return fStrategies;
	}

	public void setDocument(IDocument document)
	{
		if (fStrategies == null)
		{
			return;
		}

		for (int i = 0; i < fStrategies.length; i++)
		{
			fStrategies[i].setDocument(document);
		}
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	{
		if (fStrategies == null)
		{
			return;
		}

		for (int i = 0; i < fStrategies.length; i++)
		{
			fStrategies[i].reconcile(dirtyRegion, subRegion);
		}
	}

	public void reconcile(IRegion partition)
	{
		if (fStrategies == null)
		{
			return;
		}

		for (int i = 0; i < fStrategies.length; i++)
		{
			fStrategies[i].reconcile(partition);
		}
	}

	public void setProgressMonitor(IProgressMonitor monitor)
	{
		if (fStrategies == null)
		{
			return;
		}

		for (int i = 0; i < fStrategies.length; i++)
		{
			if (fStrategies[i] instanceof IReconcilingStrategyExtension)
			{
				IReconcilingStrategyExtension extension = (IReconcilingStrategyExtension) fStrategies[i];
				extension.setProgressMonitor(monitor);
			}
		}
	}

	public void initialReconcile()
	{
		if (fStrategies == null)
		{
			return;
		}

		for (int i = 0; i < fStrategies.length; i++)
		{
			if (fStrategies[i] instanceof IReconcilingStrategyExtension)
			{
				IReconcilingStrategyExtension extension = (IReconcilingStrategyExtension) fStrategies[i];
				extension.initialReconcile();
			}
		}
	}
}
