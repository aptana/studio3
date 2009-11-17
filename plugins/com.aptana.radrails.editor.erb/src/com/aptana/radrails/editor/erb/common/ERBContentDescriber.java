package com.aptana.radrails.editor.erb.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;

public abstract class ERBContentDescriber implements ITextContentDescriber {

    private static final QualifiedName[] SUPPORTED_OPTIONS = new QualifiedName[] {
            IContentDescription.CHARSET, IContentDescription.BYTE_ORDER_MARK };

    public int describe(Reader contents, IContentDescription description) throws IOException {
        BufferedReader reader = new BufferedReader(contents);
        String line = reader.readLine();
        if (line == null) {
            return INDETERMINATE;
        }
        if (!line.startsWith(getPrefix())) {
            return INDETERMINATE;
        }
        return VALID;
    }

    public int describe(InputStream contents, IContentDescription description) throws IOException {
        byte[] bom = getByteOrderMark(contents);
        contents.reset();
        String xmlDeclEncoding = "UTF-8"; //$NON-NLS-1$
        if (bom != null) {
            if (bom == IContentDescription.BOM_UTF_16BE) {
                xmlDeclEncoding = "UTF-16BE"; //$NON-NLS-1$
            } else if (bom == IContentDescription.BOM_UTF_16LE) {
                xmlDeclEncoding = "UTF-16LE"; //$NON-NLS-1$
            }
            // skips BOM
            contents.skip(bom.length);
        }
        byte[] xmlPrefixBytes = getPrefix().getBytes(xmlDeclEncoding);
        byte[] prefix = new byte[xmlPrefixBytes.length];
        if (contents.read(prefix) < prefix.length) {
            return INDETERMINATE;
        }
        for (int i = 0; i < prefix.length; ++i) {
            if (prefix[i] != xmlPrefixBytes[i]) {
                return INDETERMINATE;
            }
        }
        return VALID;
    }

    public QualifiedName[] getSupportedOptions() {
        return SUPPORTED_OPTIONS;
    }

    protected abstract String getPrefix();

    private byte[] getByteOrderMark(InputStream input) throws IOException {
        int first = input.read();
        if (first == 0xEF) {
            // looks for the UTF-8 BOM
            int second = input.read();
            int third = input.read();
            if (second == 0xBB && third == 0xBF) {
                return IContentDescription.BOM_UTF_8;
            }
        } else if (first == 0xFE) {
            // looks for the UTF-16 BOM
            if (input.read() == 0xFF) {
                return IContentDescription.BOM_UTF_16BE;
            }
        } else if (first == 0xFF) {
            if (input.read() == 0xFE) {
                return IContentDescription.BOM_UTF_16LE;
            }
        }
        return null;
    }
}
