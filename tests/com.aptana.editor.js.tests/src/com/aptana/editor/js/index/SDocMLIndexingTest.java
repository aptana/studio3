/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.index;

import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Ignore;
import org.junit.Test;

import com.aptana.editor.js.tests.JSEditorBasedTestCase;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.js.core.index.SDocMLFileIndexingParticipant;

/**
 * SDocMLIndexingTests
 */
public class SDocMLIndexingTest extends JSEditorBasedTestCase
{
	protected void indexAndCheckProposals(String indexResource, String fileResource, String... proposals)
			throws Exception
	{
		URI uri = null;

		try
		{
			// create IFileStore for indexing
			IFileStore indexFile = getFileStore(indexResource);

			// grab source file URI
			IFileStore sourceFile = getFileStore(fileResource);
			uri = sourceFile.toURI();

			// create index for file
			Index index = getIndexManager().getIndex(uri);
			SDocMLFileIndexingParticipant indexer = new SDocMLFileIndexingParticipant();

			// index file
			indexer.index(new FileStoreBuildContext(indexFile), index, new NullProgressMonitor());

			// check proposals
			checkProposals(fileResource, proposals);
		}
		finally
		{
			if (uri != null)
			{
				getIndexManager().removeIndex(uri);
			}
		}
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	public void assertStaticProperties_1_6_2(String fileResource) throws Exception
	{
		// @formatter:off
		indexAndCheckProposals(
			"sdocml/jquery.1.6.2.sdocml",
			fileResource,
			"ajax",
			"ajaxPrefilter",
			"ajaxSetup",
			"boxModel",
			"browser",
			"contains",
			"cssHooks",
			"data",
			"dequeue",
			"each",
			"error",
			"extend",
			"get",
			"getJSON",
			"getScript",
			"globalEval",
			"grep",
			"hasData",
			"holdReady",
			"inArray",
			"isArray",
			"isEmptyObject",
			"isFunction",
			"isPlainObject",
			"isWindow",
			"isXMLDoc",
			"makeArray",
			"map",
			"merge",
			"noConflict",
			"noop",
			"now",
			"param",
			"parseJSON",
			"parseXML",
			"post",
			"proxy",
			"queue",
			"removeData",
			"sub",
			"support",
			"trim",
			"type",
			"unique",
			"when"
		);
		// @formatter:on
	}

	protected void assertInstanceProperties_1_6_2(String fileResource) throws Exception
	{
		// @formatter:off
		indexAndCheckProposals(
			"sdocml/jquery.1.6.2.sdocml",
			fileResource,
			"add",
			"addClass",
			"after",
			"ajaxComplete",
			"ajaxError",
			"ajaxSend",
			"ajaxStart",
			"ajaxStop",
			"ajaxSuccess",
			"andSelf",
			"animate",
			"append",
			"appendTo",
			"attr",
			"before",
			"bind",
			"blur",
			"change",
			"children",
			"clearQueue",
			"click",
			"clone",
			"closest",
			"contents",
			"context",
			"css",
			"data",
			"dblclick",
			"delay",
			"delegate",
			"dequeue",
			"detach",
			"die",
			"each",
			"empty",
			"end",
			"eq",
			"error",
			"fadeIn",
			"fadeOut",
			"fadeTo",
			"fadeToggle",
			"filter",
			"find",
			"first",
			"focus",
			"focusin",
			"focusout",
			"get",
			"has",
			"hasClass",
			"height",
			"hide",
			"hover",
			"html",
			"index",
			"innerHeight",
			"innerWidth",
			"insertAfter",
			"insertBefore",
			"is",
			"jquery",
			"keydown",
			"keypress",
			"keyup",
			"last",
			"length",
			"live",
			"load",
			"map",
			"mousedown",
			"mouseenter",
			"mouseleave",
			"mousemove",
			"mouseout",
			"mouseover",
			"mouseup",
			"next",
			"nextAll",
			"nextUntil",
			"not",
			"offset",
			"offsetParent",
			"one",
			"outerHeight",
			"outerWidth",
			"parent",
			"parents",
			"parentsUntil",
			"position",
			"prepend",
			"prependTo",
			"prev",
			"prevAll",
			"prevUntil",
			"promise",
			"prop",
			"pushStack",
			"queue",
			"ready",
			"remove",
			"removeAttr",
			"removeClass",
			"removeData",
			"removeProp",
			"replaceAll",
			"replaceWith",
			"resize",
			"scroll",
			"scrollLeft",
			"scrollTop",
			"select",
			"serialize",
			"serializeArray",
			"show",
			"siblings",
			"size",
			"slice",
			"slideDown",
			"slideToggle",
			"slideUp",
			"stop",
			"submit",
			"text",
			"toArray",
			"toggle",
			"toggleClass",
			"trigger",
			"triggerHandler",
			"unbind",
			"undelegate",
			"unload",
			"unwrap",
			"val",
			"width",
			"wrap",
			"wrapAll",
			"wrapInner"
		);
		// @formatter:on
	}

	@Test
	public void testJQuerySymbolStatics_1_6_2() throws Exception
	{
		assertStaticProperties_1_6_2("sdocml/jQuery-statics.js");
	}

	@Test
	public void testDollarSymbolStatics_1_6_2() throws Exception
	{
		assertStaticProperties_1_6_2("sdocml/$-statics.js");
	}

	@Test
	public void testJQueryAddReturnValueProperties() throws Exception
	{
		assertInstanceProperties_1_6_2("sdocml/jQuery-add-properties.js");
	}

	@Test
	public void testDollarAddReturnValueProperties() throws Exception
	{
		assertInstanceProperties_1_6_2("sdocml/$-add-properties.js");
	}

	@Test
	@Ignore(" Commented out ATM, as the following test fail. Attached to ticket APSTUD-3389")
	public void testDollarJQXHR() throws Exception
	{
		// @formatter:off
		indexAndCheckProposals(
			"sdocml/jquery.1.6.2.sdocml",
			"sdocml/$-jqXHR.js",
			"readyState", // Properties
			"responseText",
			"responseXML",
			"status",
			"statusText",
			"overrideMimeType", //jqXHR methods
			"abort",
			"getAllResponseHeaders",
			"getResponseHeader",
			"setRequestHeader",
			"pipe", // Deferred methods
			"always",
			"promise",
			"fail",
			"done",
			"then",
			"isRejected",
			"isResolved"
		);
		// @formatter:on
	}

	@Test
	@Ignore(" Commented out ATM, as the following test fail. Attached to ticket APSTUD-3389")
	public void testDollarDeferred() throws Exception
	{
		// @formatter:off
		indexAndCheckProposals(
			"sdocml/jquery.1.6.2.sdocml",
			"sdocml/$-Deferred.js",
			"pipe", // Deferred methods
			"always",
			"promise",
			"resolveWith",
			"rejectWith",
			"fail",
			"done",
			"then",
			"reject",
			"isRejected",
			"isResolved",
			"resolve"
		);
		// @formatter:on
	}

	@Test
	@Ignore(" Commented out ATM, as the following test fail. Attached to ticket APSTUD-3389")
	public void testPromise() throws Exception
	{
		// @formatter:off
		indexAndCheckProposals(
			"sdocml/jquery.1.6.2.sdocml",
			"sdocml/Promise.js",
			"pipe", // Promise methods
			"always",
			"promise",
			"fail",
			"done",
			"then",
			"isRejected",
			"isResolved"
		);
		// @formatter:on
	}

}
