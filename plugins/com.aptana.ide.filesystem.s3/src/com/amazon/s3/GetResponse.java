//  This software code is made available "AS IS" without warranties of any
//  kind.  You may copy, display, modify and redistribute the software
//  code either by itself or as incorporated into your code; provided that
//  you do not remove any proprietary notices.  Your use of this software
//  code is at your own risk and you waive any claim against Amazon
//  Digital Services, Inc. or its affiliates with respect to your use of
//  this software code. (c) 2006 Amazon Digital Services, Inc. or its
//  affiliates.

package com.amazon.s3;

import java.net.HttpURLConnection;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * A Response object returned from AWSAuthConnection.get().  Exposes the attribute object, which
 * represents the retrieved object.
 */
public class GetResponse extends Response {
    public S3Object object;

    /**
     * Pulls a representation of an S3Object out of the HttpURLConnection response.
     */
    public GetResponse(HttpURLConnection connection) throws IOException {
        super(connection);
        if (connection.getResponseCode() < 400) {
            Map metadata = extractMetadata(connection);
            byte[] body = slurpInputStream(connection.getInputStream());
            this.object = new S3Object(body, metadata);
        }
    }

    /**
     * Examines the response's header fields and returns a Map from String to List of Strings
     * representing the object's metadata.
     */
    private Map extractMetadata(HttpURLConnection connection) {
        TreeMap metadata = new TreeMap();
        Map headers = connection.getHeaderFields();
        for (Iterator i = headers.keySet().iterator(); i.hasNext(); ) {
            String key = (String)i.next();
            if (key == null) continue;
            if (key.startsWith(Utils.METADATA_PREFIX)) {
                metadata.put(key.substring(Utils.METADATA_PREFIX.length()), headers.get(key));
            }
        }

        return metadata;
    }

    /**
     * Read the input stream and dump it all into a big byte array
     */
    static byte[] slurpInputStream(InputStream stream) throws IOException {
        final int chunkSize = 2048;
        byte[] buf = new byte[chunkSize];
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(chunkSize);
        int count;

        while ((count=stream.read(buf)) != -1) byteStream.write(buf, 0, count);

        return byteStream.toByteArray();
    }
}
