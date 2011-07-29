/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import java.io.File;
import java.io.IOException;

import com.aptana.editor.css.tests.CSSEditorBasedTests;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.SnippetElement;

/**
 * JSContentAssistProposalTests
 */
public class CSSContentAssistProposalTests extends CSSEditorBasedTests
{
	/**
	 * testFailureAfterColon
	 */
	public void testFailureAfterColon()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/failure-after-colon.css",
			"center",
			"inherit",
			"justify",
			"left",
			"right"
		);
		// @formatter:on
	}

	/**
	 * testStringFunction
	 * 
	 * @throws IOException
	 */
	public void testBackgroundPoposals() throws IOException
	{
		File bundleFile = File.createTempFile("editor_unit_tests", "rb");
		bundleFile.deleteOnExit();

		BundleElement bundleElement = new BundleElement(bundleFile.getAbsolutePath());
		bundleElement.setDisplayName("Editor Unit Tests");

		File f = File.createTempFile("snippet", "rb");
		SnippetElement se = createSnippet(f.getAbsolutePath(), "background-color-template", "background", "source.css");
		bundleElement.addChild(se);
		BundleManager.getInstance().addBundle(bundleElement);

		// note template is interleaved into proposals
		this.checkProposals("contentAssist/background.css", true, true, "backface-visibility", "background",
				"background-attachment", "background-clip", "background-color", "background-color-template",
				"background-image", "background-origin", "background-position", "background-position-x",
				"background-position-y", "background-repeat", "background-size");

		BundleManager.getInstance().unloadScript(f);

	}

}
