//  This software code is made available "AS IS" without warranties of any
//  kind.  You may copy, display, modify and redistribute the software
//  code either by itself or as incorporated into your code; provided that
//  you do not remove any proprietary notices.  Your use of this software
//  code is at your own risk and you waive any claim against Amazon
//  Digital Services, Inc. or its affiliates with respect to your use of
//  this software code. (c) 2006 Amazon Digital Services, Inc. or its
//  affiliates.

package com.amazon.s3;

import java.util.Date;

/**
 * A class representing a single bucket.  Returned as a component of ListAllMyBucketsResponse.
 */
public class Bucket {
    /**
     * The name of the bucket.
     */
    public String name;

    /**
     * The bucket's creation date.
     */
    public Date creationDate;

    public Bucket() {
        this.name = null;
        this.creationDate = null;
    }

    public Bucket(String name, Date creationDate) {
        this.name = name;
        this.creationDate = creationDate;
    }

    public String toString() {
        return this.name;
    }
}
