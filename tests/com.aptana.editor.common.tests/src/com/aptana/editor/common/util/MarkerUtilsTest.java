package com.aptana.editor.common.util;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.resources.FileStoreUniformResource;
import com.aptana.core.resources.MarkerUtils;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;

/**
 * {@link MarkerUtils} tests.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class MarkerUtilsTest
{
	private static String TEST_BUNDLE_ID = "com.aptana.editor.common.tests";
	private static final String TEST_RESOURCE_PATH = "resources/markerutils/markers_001.js";
	private static final String ERROR_MARKER_TYPE = "error.marker";
	private static final String WARNING_MARKER_TYPE = "warning.marker";

	private FileStoreUniformResource uniformResource;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		URL entry = Platform.getBundle(TEST_BUNDLE_ID).getEntry(TEST_RESOURCE_PATH);
		uniformResource = new FileStoreUniformResource(EFS.getLocalFileSystem().getStore(entry.toURI()));
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
//	@Override
	@After
	public void tearDown() throws Exception
	{
		MarkerUtils.deleteMarkers(uniformResource, null, false);
		uniformResource = null;
//		super.tearDown();
	}

	@Test
	public void testMarkerCreation() throws Exception
	{
		assertNoMarkers();

		// Create a single marker.
		String message = "Oops. There is an error here";
		createMarker(ERROR_MARKER_TYPE, 1, 0, 2, message);

		// Check that the marker was created
		IMarker[] markers = MarkerUtils.findMarkers(uniformResource, ERROR_MARKER_TYPE, false);
		assertFalse("Expected a marker", ArrayUtil.isEmpty(markers));
		assertEquals("Expected exactly 1 marker", 1, markers.length);
		IMarker marker = markers[0];
		assertEquals(1, marker.getAttribute(IMarker.LINE_NUMBER, -1));
		assertEquals(0, marker.getAttribute(IMarker.CHAR_START, -1));
		assertEquals(2, marker.getAttribute(IMarker.CHAR_END, -1));
		assertEquals(message, marker.getAttribute(IMarker.MESSAGE, StringUtil.EMPTY));
	}

	@Test
	public void testMultiMarkersCreation() throws Exception
	{
		assertNoMarkers();
		// Create multiple markers.
		String errMsg1 = "Oops. There is an error here";
		String errMsg2 = "Oops. There is an error here too";
		String warnMgs = "Oops. There is a warning here";
		createMarker(ERROR_MARKER_TYPE, 1, 0, 2, errMsg1);
		createMarker(ERROR_MARKER_TYPE, 2, 6, 7, errMsg2);
		createMarker(WARNING_MARKER_TYPE, 2, 8, 10, warnMgs);

		// Check that the markers were created
		IMarker[] errorMarkers = MarkerUtils.findMarkers(uniformResource, ERROR_MARKER_TYPE, false);
		assertFalse("Expected markers", ArrayUtil.isEmpty(errorMarkers));
		assertEquals("Expected exactly 2 error markers", 2, errorMarkers.length);
		IMarker marker = errorMarkers[0];
		assertEquals(1, marker.getAttribute(IMarker.LINE_NUMBER, -1));
		assertEquals(0, marker.getAttribute(IMarker.CHAR_START, -1));
		assertEquals(2, marker.getAttribute(IMarker.CHAR_END, -1));
		assertEquals(errMsg1, marker.getAttribute(IMarker.MESSAGE, StringUtil.EMPTY));
		marker = errorMarkers[1];
		assertEquals(2, marker.getAttribute(IMarker.LINE_NUMBER, -1));
		assertEquals(6, marker.getAttribute(IMarker.CHAR_START, -1));
		assertEquals(7, marker.getAttribute(IMarker.CHAR_END, -1));
		assertEquals(errMsg2, marker.getAttribute(IMarker.MESSAGE, StringUtil.EMPTY));

		IMarker[] warningMarkers = MarkerUtils.findMarkers(uniformResource, WARNING_MARKER_TYPE, false);
		assertFalse("Expected a marker", ArrayUtil.isEmpty(warningMarkers));
		assertEquals("Expected exactly 1 warning marker", 1, warningMarkers.length);
		marker = warningMarkers[0];
		assertEquals(2, marker.getAttribute(IMarker.LINE_NUMBER, -1));
		assertEquals(8, marker.getAttribute(IMarker.CHAR_START, -1));
		assertEquals(10, marker.getAttribute(IMarker.CHAR_END, -1));
		assertEquals(warnMgs, marker.getAttribute(IMarker.MESSAGE, StringUtil.EMPTY));
	}

	@Test
	public void testMarkerDeletion() throws Exception
	{
		assertNoMarkers();
		createMarker(ERROR_MARKER_TYPE, 1, 0, 2, "Err!");

		// First, try to delete a non-existing marker
		MarkerUtils.deleteMarkers(uniformResource, WARNING_MARKER_TYPE, false);
		IMarker[] errorMarkers = MarkerUtils.findMarkers(uniformResource, ERROR_MARKER_TYPE, false);
		assertFalse("Expected a marker", ArrayUtil.isEmpty(errorMarkers));
		assertEquals("Expected exactly 1 marker", 1, errorMarkers.length);

		// Now, delete the error marker
		MarkerUtils.deleteMarkers(uniformResource, ERROR_MARKER_TYPE, false);
		errorMarkers = MarkerUtils.findMarkers(uniformResource, ERROR_MARKER_TYPE, false);
		assertTrue("Did not expect any markers after the deletion", ArrayUtil.isEmpty(errorMarkers));
	}

	/**
	 * Make sure that there are no markers on the external resource
	 */
	private void assertNoMarkers()
	{
		IMarker[] markers = MarkerUtils.findMarkers(uniformResource, null, false);
		assertTrue("Found markers that should no be there", ArrayUtil.isEmpty(markers));
	}

	/**
	 * Create a marker.
	 * 
	 * @param type
	 * @param lineNumber
	 * @param start
	 * @param end
	 * @param message
	 * @throws CoreException
	 */
	private void createMarker(String type, int lineNumber, int start, int end, String message) throws CoreException
	{
		Map<String, String> attributes = new HashMap<String, String>();
		MarkerUtils.setLineNumber(attributes, lineNumber);
		MarkerUtils.setCharStart(attributes, start);
		MarkerUtils.setCharEnd(attributes, end);
		MarkerUtils.setMessage(attributes, message);
		MarkerUtils.createMarker(uniformResource, attributes, type);
	}
}
