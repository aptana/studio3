<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.1">

<!--

	@author Kevin Lindsey
	
	Convert the Studio 2.0 (and early) CSS content assist format to what we're
	using for Studio 3.0. You can run this on the Mac using the following:
	
	xsltproc update_metadata.xsl css_metadata_original.xml | xmllint &#x2D;&#x2D;format - > css_metadata.xml
	
-->

<xsl:output method="xml" indent="yes"/>

<xsl:template match="/content-assist">
	<css>
		<xsl:apply-templates select="elements"/>
		<xsl:apply-templates select="fields"/>
	</css>
</xsl:template>

<xsl:template match="elements">
	<elements>
		<xsl:apply-templates select="element">
			<xsl:sort select="@name"/>
		</xsl:apply-templates>
	</elements>
</xsl:template>

<xsl:template match="element">
	<element>
		<xsl:copy-of select="@name"/>
		<xsl:if test="@full-name">
			<xsl:attribute name="display-name">
				<xsl:value-of select="@full-name"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:copy-of select="browsers"/>
		<xsl:copy-of select="description"/>
		<xsl:copy-of select="example"/>
		<xsl:apply-templates select="fields" mode="reference"/>
		<xsl:copy-of select="remarks"/>
	</element>
</xsl:template>

<xsl:template match="fields">
	<properties>
		<xsl:apply-templates select="field">
			<xsl:sort select="@name"/>
		</xsl:apply-templates>
	</properties>
</xsl:template>

<xsl:template match="fields" mode="reference">
	<property-refs>
		<xsl:for-each select="field">
			<xsl:sort select="@name"/>
			<property-ref name="{@name}"/>
		</xsl:for-each>
	</property-refs>
</xsl:template>

<xsl:template match="field">
	<property>
		<xsl:copy-of select="@allow-multiple-values"/>
		<xsl:copy-of select="@name"/>
		<xsl:copy-of select="@type"/>
		<xsl:copy-of select="availability"/>
		<xsl:copy-of select="browsers"/>
		<xsl:copy-of select="description"/>
		<xsl:copy-of select="example"/>
		<xsl:copy-of select="hint"/>
		<xsl:copy-of select="remarks"/>
		<xsl:copy-of select="values"/>
	</property>
</xsl:template>

</xsl:stylesheet>