package com.aptana.console.process;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

import com.aptana.core.util.ArrayUtil;

/* package */ class FilterProxyInputStream extends InputStream {

	private static final char CR = '\n';
	
	private IProcessOutputFilter processOutputFilter;
	private ByteArrayInputStream lineBuffer;
	private final BufferedReader reader;
	private Charset charset;

	protected FilterProxyInputStream(InputStream in, String encoding, IProcessOutputFilter processOutputFilter) {
		super();
		this.processOutputFilter = processOutputFilter;
		charset = Charset.defaultCharset();
		try {
			charset = Charset.forName(encoding);
		}
		catch (IllegalCharsetNameException e) {
		}
		this.reader = new BufferedReader(new InputStreamReader(in, charset));
	}

	public void setProcessOutputFilter(IProcessOutputFilter processOutputFilter) {
		this.processOutputFilter = processOutputFilter;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.FilterInputStream#read()
	 */
	@Override
	public int read() throws IOException {
		fillBuffer();
		return lineBuffer.read();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		fillBuffer();
		return lineBuffer.read(b, off, len);
	}

	private void fillBuffer() throws IOException {
		if (lineBuffer != null && lineBuffer.available() > 0) {
			return;
		}
		String line = null;
		do {
			line = reader.readLine();
			if (line == null) {
				break;
			}
			if (processOutputFilter != null) {
				line = processOutputFilter.filter(line);
			}
		}
		while (line == null);
		lineBuffer = new ByteArrayInputStream(line != null ? (line + CR).getBytes(charset.name()) : ArrayUtil.NO_BYTES);
	}

}