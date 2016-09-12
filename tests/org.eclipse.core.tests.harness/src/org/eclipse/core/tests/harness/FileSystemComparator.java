/*******************************************************************************
 * Copyright (c) 2005, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *		Danail Nachev - Bug 197605 [Test Harness] FileSystemComparator use timestamp as size
 *******************************************************************************/
package org.eclipse.core.tests.harness;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.junit.Assert;

/**
 * A utility class that compares file system states. It is able to take snapshot of the file system and save it into a
 * text file for later comparison.
 */
public class FileSystemComparator {

	private class FileSummary {
		boolean directory;
		private String path;
		private long size;
		private long timestamp;

		FileSummary(File file) {
			if (!file.exists()) {
				throw new IllegalArgumentException(file + " does not exist");
			}
			path = file.getAbsolutePath();
			timestamp = file.lastModified();
			size = file.isDirectory() ? -1 : file.length();
		}

		FileSummary(String file, long timestamp, long size) {
			this.path = file;
			this.timestamp = timestamp;
			this.size = directory ? -1 : size;
		}

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof FileSummary) && ((FileSummary) obj).path.equals(path);
		}

		public String getPath() {
			return path;
		}

		public long getSize() {
			return size;
		}

		public long getTimestamp() {
			return timestamp;
		}

		@Override
		public int hashCode() {
			return path.hashCode();
		}

		@Override
		public String toString() {
			return path + " timestamp: " + timestamp + " size: " + size;
		}
	}

	private static final String SNAPSHOT_FILE_NAME = "snapshot";

	public void compareSnapshots(String tag, Object oldOne, Object newOne) {
		Map<?, ?> oldSnapshot = (Map<?, ?>) oldOne;
		Map<?, ?> newSnapshot = (Map<?, ?>) newOne;
		boolean sameSize = oldSnapshot.size() == newSnapshot.size();
		for (Iterator<?> i = newSnapshot.values().iterator(); i.hasNext();) {
			FileSummary newElement = (FileSummary) i.next();
			FileSummary oldElement = (FileSummary) oldSnapshot.get(newElement.getPath());
			Assert.assertNotNull(tag + " - " + newElement.getPath() + " was added", oldElement);
			Assert.assertEquals(tag + " - " + newElement.getPath() + " changed timestamp ", oldElement.getTimestamp(), newElement.getTimestamp());
			Assert.assertEquals(tag + " - " + newElement.getPath() + " changed size ", oldElement.getSize(), newElement.getSize());
		}
		// one or more entries were removed
		// need to do the reverse (take the old snapshot as basis) to figure out what are the missing entries
		if (!sameSize) {
			for (Iterator<?> i = oldSnapshot.values().iterator(); i.hasNext();) {
				FileSummary oldElement = (FileSummary) i.next();
				FileSummary newElement = (FileSummary) newSnapshot.get(oldElement.getPath());
				Assert.assertNotNull(tag + " - " + oldElement.getPath() + " was removed", newElement);
			}
		}
	}

	public Object loadSnapshot(File rootLocation) throws IOException {
		File summaryFile = new File(rootLocation, SNAPSHOT_FILE_NAME);
		Map<String, FileSummary> snapshot = new HashMap<>();
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(summaryFile)));
		try {
			String line;
			while ((line = in.readLine()) != null) {
				String path = line;
				long timestamp = Long.parseLong(in.readLine());
				long size = Long.parseLong(in.readLine());
				snapshot.put(path, new FileSummary(path, timestamp, size));
			}
		} finally {
			in.close();
		}
		return snapshot;
	}

	public void saveSnapshot(Object toSave, File rootLocation) throws IOException {
		File summaryFile = new File(rootLocation, SNAPSHOT_FILE_NAME);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(summaryFile))));
		Map<?, ?> snapshot = (Map<?, ?>) toSave;
		try {
			for (Iterator<?> i = snapshot.values().iterator(); i.hasNext();) {
				FileSummary element = (FileSummary) i.next();
				out.println(element.getPath());
				out.println(element.getTimestamp());
				out.println(element.getSize());
			}
		} finally {
			out.close();
		}
	}

	/**
	 * It is a good idea to skip the root location because when the snapshot file is saved, it may cause
	 * the timestamp for the parent direcotry to change on some OSes.
	 */
	public Object takeSnapshot(File rootLocation, boolean skip) {
		Map<String, FileSummary> snapshot = new HashMap<>();
		takeSnapshot(snapshot, rootLocation, skip);
		return snapshot;
	}

	private void takeSnapshot(Map<String, FileSummary> snapshot, File rootLocation, boolean skip) {
		FileSummary summary = new FileSummary(rootLocation);
		if (!skip && !rootLocation.getName().equals(SNAPSHOT_FILE_NAME)) {
			snapshot.put(rootLocation.getAbsolutePath(), summary);
		}
		if (!rootLocation.isDirectory()) {
			return;
		}
		File[] entries = rootLocation.listFiles();
		if (entries == null) {
			return;
		}
		for (int i = 0; i < entries.length; i++) {
			takeSnapshot(snapshot, entries[i], false);
		}
	}

}
