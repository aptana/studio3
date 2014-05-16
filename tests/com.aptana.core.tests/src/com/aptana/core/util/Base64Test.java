package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class Base64Test
{

	@Test
	public void testEncodeBytesWithKnownResultValue() throws Exception
	{

		String original = "Hello World!";
		String encoded = Base64.encodeBytes(original.getBytes("UTF-8"));
		assertEquals("SGVsbG8gV29ybGQh", encoded);
	}

	@Test
	public void testDecodeBytesWithKnownEncodedValue() throws Exception
	{
		assertEquals("Hello World!", new String(Base64.decode("SGVsbG8gV29ybGQh"), "UTF-8"));
	}

	@Test
	public void testEncodeBytesDecodeBytes() throws Exception
	{

		String original = "Hello World!";
		String encoded = Base64.encodeBytes(original.getBytes("UTF-8"));

		assertNotNull(encoded);
		assertEquals(original, new String(Base64.decode(encoded.getBytes()), "UTF-8"));
	}

	@Test
	public void testEncodeBytesDecodeString() throws Exception
	{
		String original = "Hi there.";
		String encoded = Base64.encodeBytes(original.getBytes("UTF-8"));

		assertNotNull(encoded);
		assertEquals(original, new String(Base64.decode(encoded), "UTF-8"));
	}

	@Test
	public void testLongStringThatDoesntMeetLineBreakMinimum() throws Exception
	{

		String inputData = "The quick brown fox jumps over the lazy dog and some extr"; // 57 chars
		String expectedResult = "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZyBhbmQgc29tZSBleHRy";
		String result = Base64.encodeBytes(inputData.getBytes("UTF-8"));
		assertEquals("Result of encoding", result, expectedResult);

		String result2 = new String(Base64.decode(result), "UTF-8");
		assertEquals("Result of encoding", result2, inputData);

		result2 = new String(Base64.decode(expectedResult.getBytes()), "UTF-8");
		assertEquals("Result of encoding", result2, inputData);
	}

	@Test
	public void testLongStringBeyondLineBreakWithLineBreakOptionOff() throws Exception
	{
		String inputData = "The quick brown fox jumps over the lazy dog and some extra text that will cause a line wrap"; // 91
																															// chars
		String expectedResult = "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZyBhbmQgc29tZSBleHRyYSB0ZXh0IHRoYXQgd2lsbCBjYXVzZSBhIGxpbmUgd3JhcA==";

		String result = Base64.encodeBytes(inputData.getBytes("UTF-8"));
		assertEquals("Result of encoding", result, expectedResult);

		String result2 = new String(Base64.decode(result), "UTF-8");
		assertEquals("Result of encoding", result2, inputData);

		result2 = new String(Base64.decode(expectedResult.getBytes()), "UTF-8");
		assertEquals("Result of encoding", result2, inputData);
	}

	@Test
	public void testLongStringBeyondLineBreakWithLineBreakOptionOn() throws Exception
	{
		String inputData = "The quick brown fox jumps over the lazy dog and some extra text that will cause a line wrap"; // 91
																															// chars
		String expectedResult = "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZyBhbmQgc29tZSBleHRy\nYSB0ZXh0IHRoYXQgd2lsbCBjYXVzZSBhIGxpbmUgd3JhcA==";

		String result = Base64.encodeBytes(inputData.getBytes("UTF-8"), Base64.DO_BREAK_LINES);
		assertEquals("Result of encoding", result, expectedResult);

		String result2 = new String(Base64.decode(result), "UTF-8");
		assertEquals("Result of encoding", result2, inputData);

		result2 = new String(Base64.decode(expectedResult.getBytes()), "UTF-8");
		assertEquals("Result of encoding", result2, inputData);
	}

}
