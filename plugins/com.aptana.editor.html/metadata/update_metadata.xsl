<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.1">

<!--

	@author Kevin Lindsey
	
	Convert the Studio 2.0 (and early) HTML content assist format to what we're
	using for Studio 3.0. You can run this on the Mac using the following:
	
	xsltproc update_metadata.xsl html_metadata_original.xml | xmllint &#x2D;&#x2D;format - > html_metadata.xml
	
-->

<xsl:output method="xml" indent="yes"/>

<xsl:template match="/content-assist">
	<html>
		<xsl:apply-templates select="elements"/>
		<xsl:apply-templates select="fields"/>
		<xsl:apply-templates select="events"/>
		<xsl:apply-templates select="entities"/>
		<!-- xsl:apply-templates select="escape-codes"/ -->
	</html>
</xsl:template>

<xsl:template match="elements">
	<elements>
		<xsl:apply-templates select="element">
			<xsl:sort select="@name"/>
		</xsl:apply-templates>
	</elements>
</xsl:template>

<xsl:template match="fields">
	<attributes>
		<xsl:apply-templates select="field|/content-assist/elements/element/fields/field[count(*) > 0]">
			<xsl:sort select="@name"/>
		</xsl:apply-templates>
	</attributes>
</xsl:template>

<xsl:template match="fields" mode="reference">
	<attribute-refs>
		<xsl:for-each select="field">
			<xsl:sort select="@name"/>
			<attribute-ref name="{@name}"/>
		</xsl:for-each>
	</attribute-refs>
</xsl:template>

<xsl:template match="events">
	<events>
		<xsl:apply-templates select="event">
			<xsl:sort select="@name"/>
		</xsl:apply-templates>
	</events>
</xsl:template>

<xsl:template match="events" mode="reference">
	<event-refs>
		<xsl:for-each select="event">
			<xsl:sort select="@name"/>
			<event-ref name="{@name}"/>
		</xsl:for-each>
	</event-refs>
</xsl:template>

<xsl:template match="entities">
	<entities>
		<xsl:apply-templates select="entity">
			<xsl:sort select="@name"/>
		</xsl:apply-templates>
	</entities>
</xsl:template>

<xsl:template match="element">
	<element>
		<xsl:copy-of select="@name"/>
		<xsl:copy-of select="@related-class"/>
		<xsl:if test="@full-name">
			<xsl:attribute name="display-name">
				<xsl:value-of select="@full-name"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:apply-templates select="fields" mode="reference"/>
		<xsl:copy-of select="availability"/>
		<xsl:copy-of select="browsers"/>
		<xsl:copy-of select="deprecated"/>
		<xsl:copy-of select="description"/>
		<xsl:apply-templates select="events" mode="reference"/>
		<xsl:copy-of select="example"/>
		<xsl:copy-of select="references"/>
		<xsl:copy-of select="remarks"/>
	</element>
</xsl:template>

<xsl:template match="field">
	<attribute>
		<xsl:copy-of select="@name"/>
		<xsl:copy-of select="@type"/>
		<xsl:copy-of select="availability"/>
		<xsl:copy-of select="browsers"/>
		<xsl:copy-of select="deprecated"/>
		<xsl:copy-of select="description"/>
		<xsl:copy-of select="hint"/>
		<xsl:copy-of select="references"/>
		<xsl:copy-of select="remarks"/>
		<xsl:copy-of select="values"/>
	</attribute>
</xsl:template>

<xsl:template match="event">
	<event>
		<xsl:copy-of select="@name"/>
		<xsl:copy-of select="@type"/>
		<xsl:copy-of select="availability"/>
		<xsl:copy-of select="browsers"/>
		<xsl:copy-of select="description"/>
		<xsl:copy-of select="remarks"/>
	</event>
</xsl:template>

<xsl:template match="entity">
	<entity>
		<xsl:copy-of select="@name"/>
		<xsl:copy-of select="@decimal"/>
		<xsl:copy-of select="@hex"/>
		<xsl:copy-of select="description"/>
		<!-- skipping examples since they don't seem to provide any benefit -->
	</entity>
</xsl:template>

<xsl:template match="text()">
</xsl:template>

</xsl:stylesheet>