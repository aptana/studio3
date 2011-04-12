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

(function() {

// ************************************************************************************************

const nsIPropertyElement = Components.interfaces.nsIPropertyElement;
const nsIStringBundle = Components.interfaces.nsIStringBundle;
const nsIStringBundleService = Components.interfaces.nsIStringBundleService;

this.initStringBundle = function (bundle,target)
{

    var enumer = bundle.getSimpleEnumeration();
		
    while (enumer.hasMoreElements())
    {
        var prop = enumer.getNext().QueryInterface(nsIPropertyElement);
        var ary = prop.key.match (/^const\./);
        if (ary)
        {
            var constValue;
            var constName = prop.key.toUpperCase().replace (/\./g, "_");
            constValue = prop.value.replace (/^\"/, "").replace (/\"$/, "");

            target[constName] = constValue;
        }
    }
}

this.createStringBundle = function(url)
{
	var bundleService = Components.classes["@mozilla.org/intl/stringbundle;1"]
									.getService(nsIStringBundleService);
	return bundleService.createBundle(url);
}

// ************************************************************************************************

}).apply(AptanaUtils);


