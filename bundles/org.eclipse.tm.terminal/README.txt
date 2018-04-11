Terminal README
===============

The Terminal is a UI-less model of a grid of characters,
plus an SWT widget that's updated asynchronously for 
maximum performance. The widget can be hooked up to various
ITerminalConnectors providing an InputStream, OutputStream,
and a method for setting the Terminal Size.

The widget processes ANSI control characters, including NUL,
backspace, carriage return, linefeed, and a subset of ANSI
escape sequences sufficient to allow use of screen-oriented
applications, such as vi, Emacs, and any GNU readline-enabled
application (Bash, bc, ncftp, etc.).

This is not yet a fully compliant vt100 / vt102 terminal 
emulator!
