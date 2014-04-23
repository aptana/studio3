package com.aptana.editor.js.contentassist;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.common.EditorContentAssistBasedTests;
import com.aptana.editor.common.tests.TextViewer;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.ParameterElement;

public class JSContentAssistProcessorTest
{

	private JSContentAssistProcessor processor;
	private IDocument document;
	private Mockery context;
	private JSIndexQueryHelper helper;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		helper = context.mock(JSIndexQueryHelper.class);
		processor = new JSContentAssistProcessor(null)
		{
			@Override
			protected IDocument getDocument()
			{
				return document;
			}

			@Override
			protected String getFilename()
			{
				return "something.js";
			}

			@Override
			protected JSIndexQueryHelper getQueryHelper()
			{
				return helper;
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		processor = null;
		context = null;
		document = null;
		helper = null;
	}

	@Test
	public void testSuggestsConstantsForFunctionArgumentsWithThemDefined()
	{
		int offset = 4; // between parens
		document = new Document("foo();");

		ParameterElement param = new ParameterElement();
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("constants", CollectionsUtil.newList("BAR"));
		param.fromJSON(json);
		param.setName("param1");

		final FunctionElement function = new FunctionElement();
		function.setName("foo");
		function.addParameter(param);

		context.checking(new Expectations()
		{
			{
				oneOf(helper).findFunctionInHierarchy(JSTypeConstants.WINDOW_TYPE, "foo");
				will(returnValue(function));

				allowing(helper).getGlobals("something.js");
				will(returnValue(Collections.emptyList()));
			}
		});

		ICompletionProposal[] proposals = processor.computeCompletionProposals(new TextViewer(document), offset, '\t',
				false);
		EditorContentAssistBasedTests.assertContains(proposals, "BAR");
		context.assertIsSatisfied();
	}

}
