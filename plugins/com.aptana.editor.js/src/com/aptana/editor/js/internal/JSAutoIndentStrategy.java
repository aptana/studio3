/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.editor.js.internal;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.editor.common.text.AbstractRegexpAutoIndentStrategy;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.preferences.IPreferenceConstants;
import com.aptana.editor.js.preferences.PreferenceInitializer;

/**
 * @deprecated
 **/
//TODO Remove now that it's not used
public class JSAutoIndentStrategy extends AbstractRegexpAutoIndentStrategy
{

	private static final String DECREASE_INDENT_REGEXP = "^(.*\\*/)?\\s*(\\}|\\))([^{]*\\{)?([;,]?\\s*|\\.[^{]*|\\s*\\)[;\\s]*)$"; //$NON-NLS-1$
	private static final String INCREASE_INDENT_REGEXP = "^.*(\\{[^}\"']*|\\([^)\"']*)$"; //$NON-NLS-1$

	public JSAutoIndentStrategy(String contentType, SourceViewerConfiguration configuration, ISourceViewer sourceViewer)
	{
		super(INCREASE_INDENT_REGEXP, DECREASE_INDENT_REGEXP, contentType, configuration, sourceViewer);
	}

	protected boolean shouldAutoIndent()
	{
		return Platform.getPreferencesService().getBoolean(Activator.PLUGIN_ID,
				IPreferenceConstants.AUTO_INDENT_ON_CARRIAGE_RETURN,
				PreferenceInitializer.DEFAULT_AUTO_INDENT_ON_RETURN, null);
	}

	@Override
	protected boolean indentAndPushTrailingContentAfterNewlineAndCursor(String contentBeforeNewline,
			String contentAfterNewline)
	{
		return true;
	}
}
