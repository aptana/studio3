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
package com.aptana.editor.js.formatter.preferences;

import java.net.URL;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.aptana.editor.js.formatter.JSFormatterConstants;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

/**
 * Blank-lines configuration tab for the JS code formatter.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class JSFormatterBlankLinesPage extends FormatterModifyTabPage
{

	private static final String BLANK_LINES_PREVIEW_FILE = "blank-lines-preview.js"; //$NON-NLS-1$

	/**
	 * @param dialog
	 */
	public JSFormatterBlankLinesPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group blankLinesGroup = SWTFactory.createGroup(parent, Messages.JSFormatterBlankLinesPage_blankLinesGroupLabel,
				2, 1, GridData.FILL_HORIZONTAL);
		manager.createNumber(blankLinesGroup, JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION,
				Messages.JSFormatterBlankLinesPage_afterFunctionDeclaration);
		manager.createNumber(blankLinesGroup, JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION_IN_EXPRESSION,
				Messages.JSFormatterBlankLinesPage_afterFunctionDeclarationInExpression);

		Group preserveLinesGroup = SWTFactory.createGroup(parent,
				Messages.JSFormatterBlankLinesPage_existingBlankLinesGroupLabel, 2, 1, GridData.FILL_HORIZONTAL);
		manager.createNumber(preserveLinesGroup, JSFormatterConstants.PRESERVED_LINES,
				Messages.JSFormatterBlankLinesPage_existingBlankLinesToPreserve);
	}

	protected URL getPreviewContent()
	{
		return getClass().getResource(BLANK_LINES_PREVIEW_FILE);
	}

}
