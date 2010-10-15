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
 * A structure representing a single object stored in S3.  Returned as a part of ListBucketResponse.
 */
public class ListEntry {
    /**
     * The name of the object
     */
    public String key;

    /**
     * The date at which the object was last modified.
     */
    public Date lastModified;

    /**
     * The object's ETag, which can be used for conditional GETs.
     */
    public String eTag;

    /**
     * The size of the object in bytes.
     */
    public long size;

    /**
     * The object's storage class
     */
    public String storageClass;

    /**
     * The object's owner
     */
    public Owner owner;

    public String toString() {
        return key;
    }
}
