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

/**
 * The parent class of all other Responses.  This class keeps track of the
 * HttpURLConnection response.
 */
public class Response {
    public HttpURLConnection connection;

    public Response(HttpURLConnection connection) throws IOException {
        this.connection = connection;
    }
}
