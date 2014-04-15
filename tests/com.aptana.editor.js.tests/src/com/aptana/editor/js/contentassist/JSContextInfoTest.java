/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.junit.Test;

import com.aptana.core.util.ArrayUtil;
import com.aptana.editor.common.tests.TextViewer;
import com.aptana.editor.js.tests.JSEditorBasedTestCase;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.js.core.index.JSFileIndexingParticipant;

/**
 * JSContextInfoTests
 */
public class JSContextInfoTest extends JSEditorBasedTestCase
{
	public void assertArgIndexes(String resource)
	{
		JSContextInformationValidator validator = getValidator(resource);

		// Now travel through the offset pairs, testing that each position within that closed interval is the expected
		// argument index
		for (int index = 0; index < cursorOffsets.size(); index += 2)
		{
			int startingOffset = cursorOffsets.get(index);
			int endingOffset = cursorOffsets.get(index + 1);
			int expectedArgIndex = index / 2;

			for (int offset = startingOffset; offset <= endingOffset; offset++)
			{
				int argIndex = validator.getArgumentIndex(offset);

				assertEquals("Expected argument index to be " + expectedArgIndex + ", but was " + argIndex,
						expectedArgIndex, argIndex);
			}
		}
	}

	public void assertArgStyleRanges(String resource)
	{
		String lineDelimiter = JSContextInformation.DESCRIPTION_DELIMITER;
		int lineDelimiterLength = lineDelimiter.length();

		JSContextInformationValidator validator = getValidator(resource);
		IContextInformation info = validator.getContextInformation();
		String displayText = info.getInformationDisplayString();
		TextPresentation presentation = new TextPresentation();

		for (int index = 0; index < cursorOffsets.size(); index += 2)
		{
			int expectedArgIndex = index / 2;
			int startingOffset = cursorOffsets.get(index);

			validator.updatePresentation(startingOffset, presentation);
			Iterator<?> iterator = presentation.getNonDefaultStyleRangeIterator();

			// TODO: test styles on first line
			for (int i = 0; i < 2 && iterator.hasNext(); i++)
			{
				iterator.next();
			}

			// TODO: simplify calculation of offsets
			int descriptionOffset = displayText.indexOf(lineDelimiter) + lineDelimiterLength;
			int nextOffset = displayText.indexOf(lineDelimiter, descriptionOffset);

			if (nextOffset == -1)
			{
				nextOffset = displayText.length();
			}
			else
			{
				nextOffset += lineDelimiterLength;
			}

			// skip over unstyled arg descriptions
			for (int i = 0; i < expectedArgIndex; i++)
			{
				descriptionOffset = nextOffset;

				nextOffset = displayText.indexOf(lineDelimiter, descriptionOffset);

				if (nextOffset == -1)
				{
					nextOffset = displayText.length();
				}
				else
				{
					nextOffset += lineDelimiterLength;
				}
			}

			int length = nextOffset - descriptionOffset;

			if (nextOffset != displayText.length())
			{
				length -= lineDelimiterLength;
			}

			// make sure we have a style
			assertTrue(iterator.hasNext());

			while (iterator.hasNext())
			{
				StyleRange styleRange = (StyleRange) iterator.next();

				// skip "normal" style ranges
				if (styleRange.fontStyle == SWT.BOLD)
				{
					// check from bulletOffset - nextBullet-1
					assertEquals(descriptionOffset, styleRange.start);
					assertEquals(length, styleRange.length);

					break;
				}
			}
		}
	}

	@Override
	protected IFileStoreIndexingParticipant createIndexer()
	{
		return new JSFileIndexingParticipant();
	}

	/**
	 * @param resource
	 * @return
	 */
	protected JSContextInformationValidator getValidator(String resource)
	{
		setupTestContext(resource);

		// make sure we offsets and that we have an even count. Odd offsets mark what should be the start of a new
		// argument and even offsets mark what should be the end of an argument
		assertTrue("No offsets defined", cursorOffsets != null && cursorOffsets.size() != 0);
		assertTrue("Must have an even number of offsets", (cursorOffsets.size() % 2) == 0);

		// create a context info validator via the content assist processor
		int activationOffset = cursorOffsets.get(0);
		ITextViewer textViewer = new TextViewer(document);
		IContextInformation[] infos = processor.computeContextInformation(textViewer, activationOffset);
		assertTrue("No context information instances", infos != null && infos.length != 0);

		// install the validator
		JSContextInformationValidator validator = (JSContextInformationValidator) this.processor
				.getContextInformationValidator();
		IContextInformation info = infos[0];
		validator.install(info, textViewer, activationOffset);

		// check the offsets before and after the range of offsets we have. Those should be invalid positions for the
		// info popup
		int invalidLeftOffset = activationOffset - 1;
		int invalidRightOffset = cursorOffsets.get(cursorOffsets.size() - 1) + 1;
		assertFalse("Context info should not appear at offset " + invalidLeftOffset,
				validator.isContextInformationValid(invalidLeftOffset));
		assertFalse("Context info should not appear at offset " + invalidRightOffset,
				validator.isContextInformationValid(invalidRightOffset));
		return validator;
	}

	@Test
	public void testArrayArguments()
	{
		assertArgIndexes("contextInfo/arrayArgs.js");
	}

	@Test
	public void testArrayStyles()
	{
		assertArgStyleRanges("contextInfo/arrayArgs.js");
	}

	@Test
	public void testInvocationArguments()
	{
		assertArgIndexes("contextInfo/invocationArgs.js");
	}

	@Test
	public void testInvocationStyles()
	{
		assertArgStyleRanges("contextInfo/invocationArgs.js");
	}

	@Test
	public void testNumberArguments()
	{
		assertArgIndexes("contextInfo/numberArgs.js");
	}

	@Test
	public void testNumberStyles()
	{
		assertArgStyleRanges("contextInfo/numberArgs.js");
	}

	@Test
	public void testObjectArguments()
	{
		assertArgIndexes("contextInfo/objectArgs.js");
	}

	@Test
	public void testObjectStyles()
	{
		assertArgStyleRanges("contextInfo/objectArgs.js");
	}

	@Test
	public void testTagStripperAndTypeBolder()
	{
		JSContextInformationValidator validator = getValidator("contextInfo/invocationArgsWithTagsAndTypes.js");
		IContextInformation info = validator.getContextInformation();
		String displayText = info.getInformationDisplayString();

		assertNotNull("Context info display text should not be empty", displayText);

		String[] parts = displayText.split(JSContextInformation.DESCRIPTION_DELIMITER);
		assertTrue("Context info display text should have at least one entry", !ArrayUtil.isEmpty(parts));

		String lastLine = parts[parts.length - 1];
		assertTrue("Last line does not appear to have had tags stripped and types bolded",
				lastLine.endsWith("Last one with a type Titanium.UI.createWindow and some tags."));
	}
}
