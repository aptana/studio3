SRCS = CssGrammar.jj

JRCS = $(SRCS:.jj=.java) Couple.java
OBJS = $(JRCS:.java=.class)

PACKAGE = CSS.Parser.Analyzer
JAVAC = /usr/local/java11/bin/javac
JAVADOC = javadoc
JAVADOCFLAGS = -version -d docs -protected
JAVACFLAGS =
MAKEDEPEND = jmakedepend

JAVACC_HOME = /u/www43/0/w3c/plehegar/Work/JavaCC

.SUFFIXES: .class .java .jj
.java.class:; $(JAVAC) $(JAVACFLAGS) $<


all: $(OBJS)

javadoc:; $(JAVADOC) $(JAVADOCFLAGS) $(PACKAGE)

clean:; -rm -f $(OBJS) $(JRCS) *~ core

depend:; $(MAKEDEPEND) $(JRCS)

CssGrammar.java: CssGrammar.jj
	( export CLASSPATH=$(JAVACC_HOME)/JavaCC.zip:$CLASSPATH ; $(JAVACC_HOME)/bin/javacc $< )

# DO NOT DELETE THIS LINE -- make depend depends on it.
Couple.class:: Couple.java\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Values/CssExpression.class
CssGrammar.class:: CssGrammar.java\
	/usr/local/java11/lib/classes.zip\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Values/CssValue.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Values/CssExpression.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Values/CssString.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Values/CssURL.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Values/CssLength.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Values/CssNumber.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Values/CssColor.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Values/CssIdent.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Values/CssPercentage.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Values/CssFrequency.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Values/CssTime.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Values/CssAngle.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Properties/CssProperty.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Parser/Frame.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Parser/CssError.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/Parser/CssSelectors.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/util/InvalidParamException.class\
	/u/www43/0/w3c/plehegar/Work/Java/CSS/util/Util.class
