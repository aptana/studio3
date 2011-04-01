/* ***** BEGIN LICENSE BLOCK *****
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain Eclipse Public Licensed code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 * 
 * Contributor(s):
 *     Max Stepanov (Aptana, Inc.)
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
