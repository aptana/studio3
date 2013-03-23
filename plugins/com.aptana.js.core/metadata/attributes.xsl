<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.1">

<xsl:output method="text"/>

<xsl:template match="/">
	<xsl:apply-templates select="//@*">
		<xsl:sort select="name()"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="@*">
	<xsl:value-of select="name()"/>
	<xsl:text>="</xsl:text>
	<xsl:value-of select="string()"/>
	<xsl:text>"&#x0A;</xsl:text>
</xsl:template>

<xsl:template match="text()">
</xsl:template>
	
</xsl:stylesheet>