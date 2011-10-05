<!--

    @author Kevin Lindsey

    Get a list of instance properties from a jQuery sdocml file:

        xsltproc instance_properties.xsl jQuery.1.6.2.sdocml | sort -u 

-->

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.1">
	
<xsl:output method="text"/>

<xsl:template match="/">
	<xsl:apply-templates select="//property[@scope='instance']/@name"/>
	<xsl:apply-templates select="//method[@scope='instance']/@name"/>
</xsl:template>

<xsl:template match="@*">
	<xsl:value-of select="."/>
	<xsl:text>&#x0A;</xsl:text>
</xsl:template>

<xsl:template match="text()"/>

</xsl:stylesheet>
