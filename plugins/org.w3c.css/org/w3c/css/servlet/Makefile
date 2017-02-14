SRCS = CssValidator.java

OBJS = $(SRCS:.java=.class)

PACKAGE = org.w3c.css.servlet
JAVAC = jc -val
JAVADOC = javadoc
JAVADOCFLAGS = -version -d docs -protected
JAVACFLAGS =
MAKEDEPEND = jmakedepend

.SUFFIXES: .class .java
.java.class:; $(JAVAC) $(JAVACFLAGS) $<

all: $(OBJS)

javadoc:; $(JAVADOC) $(JAVADOCFLAGS) $(PACKAGE)

clean:; -rm -f $(OBJS) *~ core

depend:; $(MAKEDEPEND) $(SRCS)

# DO NOT DELETE THIS LINE -- make depend depends on it.
CssValidator.class:: CssValidator.java
