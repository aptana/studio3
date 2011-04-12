/* ***** BEGIN LICENSE BLOCK *****
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 * 
 * Contributor(s):
 *     Max Stepanov (Appcelerator, Inc.)
 *
 * ***** END LICENSE BLOCK ***** */
 
function AptanaDebugAPI(debuggr)
{
    this.version = debuggr.VERSION;
    
    // We store these functions as closures so that they can access the context privately,
    // because it would be insecure to store debuggr as a property of window.aptana and
    // and therefore expose it to web pages
    
    this.log = function(message)
    {
        debuggr.log("out",message);
    }

    this.fail = function()
    {
        debuggr.onAssert(arguments, null);
    }
    
    this.assert = function(x)
    {
        if (!x)
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 1), ["%o", x]);
    }

    this.assertEquals = function(x, y)
    {
        if (x != y)
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 2), ["%o != %o", x, y]);
    }    

    this.assertNotEquals = function(x, y)
    {
        if (x == y)
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 2), ["%o == %o", x, y]);
    }    

    this.assertGreater = function(x, y)
    {
        if (x <= y)
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 2), ["%o <= %o", x, y]);
    }    

    this.assertNotGreater = function(x, y)
    {
        if (!(x > y))
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 2), ["!(%o > %o)", x, y]);
    }    

    this.assertLess = function(x, y)
    {
        if (x >= y)
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 2), ["%o >= %o", x, y]);
    }    

    this.assertNotLess = function(x, y)
    {
        if (!(x < y))
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 2), ["!(%o < %o)", x, y]);
    }    

    this.assertContains = function(x, y)
    {
        if (!(x in y))
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 2), ["!(%o in %o)", x, y]);
    }    

    this.assertNotContains = function(x, y)
    {
        if (x in y)
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 2), ["%o in %o", x, y]);
    }    

    this.assertTrue = function(x)
    {
        if (x != true)
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 1), ["%o != %o", x, true]);
    }    

    this.assertFalse = function(x)
    {
        if (x != false)
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 1), ["%o != %o", x, false]);
    }    

    this.assertNull = function(x)
    {
        if (x != null)
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 1), ["%o != %o", x, null]);
    }    

    this.assertNotNull = function(x)
    {
        if (x == null)
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 1), ["%o == %o", x, null]);
    }    

    this.assertUndefined = function(x)
    {
        if (x != undefined)
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 1), ["%o != %o", x, undefined]);
    }    

    this.assertNotUndefined = function(x)
    {
        if (x == undefined)
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 1), ["%o == %o", x, undefined]);
    }    

    this.assertInstanceOf = function(x, y)
    {
        if (!(x instanceof y))
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 2), ["!(%o instanceof %o)", x, y]);
    }    

    this.assertNotInstanceOf = function(x, y)
    {
        if (x instanceof y)
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 2), ["%o instanceof %o", x, y]);
    }    

    this.assertTypeOf = function(x, y)
    {
        if (typeof(x) != y)
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 2), ["typeof(%o) != %o", x, y]);
    }    

    this.assertNotTypeOf = function(x, y)
    {
        if (typeof(x) == y)
            debuggr.onAssert(AptanaUtils.sliceArray(arguments, 2), ["typeof(%o) == %o", x, y]);
    }    

    this.trace = function(message)
    {
        debuggr.log("out",message,AptanaUtils.stackTrace(Components.stack));
    }
	
}
