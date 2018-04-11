This project contains only the subset of:

org.eclipse.jetty.util.source_8.1.3.v20120416

which Aptana Studio Uses (i.e.: JSON utilities)

Previously Aptana Studio used org.mortbay.jetty.util (in Eclipse 3.x), but as of 
Eclipse 4.x, this package was renamed to org.eclipse.jetty.util.

So, to keep Aptana Studio running in both Eclipse 3 and Eclipse 4, the project 
com.aptana.jetty.util.epl was created.

If at some time the support to Eclipse 3 is dropped, this package may be removed 
in favor of org.eclipse.jetty.util. 
