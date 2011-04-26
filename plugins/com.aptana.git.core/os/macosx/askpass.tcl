#!/bin/sh
# Tcl ignores the next line -*- tcl -*- \
exec wish "$0" -- "$@"

#/**
# * Aptana Studio
# * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
# * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
# * Please see the license.html included with this distribution for details.
# * Any modifications to this file must keep this entire header intact.
# */

package require Tk

set password {}
set result 255
set prompt [join $argv " "]

message .msg -text "Please provide HTTP authentication password" -aspect 2000
pack .msg -side top -fill x -padx 5 -pady 5 -expand 1

frame .r
pack .r -side top -fill x -padx 10 -pady 10

message .r.label -text $prompt -aspect 2000
pack .r.label -side left -fill y -padx 10 -pady 10 -expand 1

entry .r.edit -textvariable password -width 50
pack .r.edit -side right -fill x -padx 10 -pady 10
.r.edit configure -show "*"

frame .button
button .button.ok -text OK -width 10 -command onok
button .button.cancel -text Cancel -width 10 -command oncancel

pack .button.ok -side left -expand 1
pack .button.cancel -side right -expand 1
pack .button -side bottom -fill x -padx 10 -pady 10

bind . <Visibility> {focus -force .r.edit}
bind . <Key-Return> [list .button.ok invoke]
bind . <Key-Escape> [list .button.cancel invoke]
bind . <Destroy>    {set result $result}

proc oncancel {} {
	set ::result 255
}

proc onok {} {
	puts $::password
	set ::result 0
}

wm title . "HTTP"
wm focusmodel . active
wm attributes . -topmost 1
focus -force .r.edit
tk::PlaceWindow .
vwait result
exit $result
