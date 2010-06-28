<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.1">
	
<!--

	@author Kevin Lindsey
	
	generate a list of elements showing their attribute and child elements. The
	list is not unique but on *nix systems it's easy to pipe to sort. For
	example, you can run this on the Mac using the following:
	
	xsltproc list_elements.xsl html_metadata_original.xml | sort -u 
	
-->
	
<xsl:output method="text"/>

<xsl:template match="/">
	<xsl:apply-templates select="//*"/>
</xsl:template>

<xsl:template match="*">
	<xsl:value-of select="name()"/>
	<xsl:text>&#x0A;</xsl:text>
	<xsl:for-each select="@*">
		<xsl:value-of select="name(..)"/>
		<xsl:text>@</xsl:text>
		<xsl:value-of select="name()"/>
		<xsl:text>&#x0A;</xsl:text>
	</xsl:for-each>
	<xsl:for-each select="*">
		<xsl:value-of select="name(..)"/>
		<xsl:text>></xsl:text>
		<xsl:value-of select="name()"/>
		<xsl:text>&#x0A;</xsl:text>
	</xsl:for-each>
</xsl:template>

<xsl:template match="text()"/>

</xsl:stylesheet>