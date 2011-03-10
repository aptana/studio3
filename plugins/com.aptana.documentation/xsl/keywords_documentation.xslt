<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="no"  encoding="utf-8" standalone="no"/>

  <xsl:param name="ReferenceName" />
  <xsl:param name="ReferenceDisplayName" />
  <xsl:param name="ReferenceLanguageType" />

  <xsl:template match="/keywords">

    Content for docs.xml.
    
    <xsl:call-template name="toc" />
    <xsl:call-template name="index" />
    <xsl:call-template name="keywords" />

  </xsl:template>

  <xsl:template name="toc">
    <xsl:result-document href="{$ReferenceName}.toc.xml" method="xml">
	<xsl:processing-instruction name="NLS">TYPE="org.eclipse.help.toc"</xsl:processing-instruction> 

	<toc label="{$ReferenceDisplayName}" link_to="toc.xml#reference"> 
	    <topic label="{$ReferenceDisplayName}" href="html/reference/api/{$ReferenceName}.index.html">
		    <xsl:for-each select="keyword">
			<topic label="{@name} keyword"  href="html/reference/api/{$ReferenceName}.{@full-name}.html"></topic>
		    </xsl:for-each>
	    </topic>
	</toc>
    </xsl:result-document>
  </xsl:template>

  <xsl:template name="index">
    <xsl:result-document href="{$ReferenceName}.index.html" method="html">
    <html>
      <head>
        <title><xsl:value-of select="$ReferenceDisplayName" /> Reference Index</title>
		<link rel="stylesheet" href="../../../content/shared.css" type="text/css" />
		<link rel="stylesheet" href="../../../PRODUCT_PLUGIN/book.css" type="text/css"/>
		<link rel="stylesheet" href="PLUGINS_ROOT/PRODUCT_PLUGIN/book.css" />
      </head>
      <body>
        <div class="classBlock">
	    <h1><xsl:value-of select="$ReferenceDisplayName" /> Index</h1>
	    <div class="content">
		<div class="classDescription">Below is a listing of all <xsl:value-of select="$ReferenceDisplayName" /> language. Click on an item to visit its documentation.</div>
		
		<xsl:for-each select="keyword">
			<h2><a href="{$ReferenceName}.{@full-name}.html"><xsl:value-of select="@name"/></a></h2>
			<p><xsl:apply-templates select="description" /></p>
		</xsl:for-each>
        </div>
        </div>
      </body>
    </html>
    </xsl:result-document>
  </xsl:template>

  <xsl:template name="keywords">
    <xsl:for-each select="keyword">
    <xsl:result-document href="{$ReferenceName}.{@full-name}.html" method="html">
    <html>
      <head>
        <title><xsl:value-of select="$ReferenceDisplayName" />: <xsl:value-of select="@name"/></title>
		<link rel="stylesheet" href="../../../content/shared.css" type="text/css" />
	  </head>
      <body>
        <xsl:apply-templates select="." />
        <div style="visibility:hidden;display:none"><xsl:value-of select="$ReferenceLanguageType" /> aptana_docs</div>
      </body>
    </html>
    </xsl:result-document>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="keyword">
    <img src="../../../content/aptana.gif" hspace="21" vspace="3"></img>
    <h1>
      <xsl:value-of select="@name"/>
    </h1>
    <div class="content">
		<div class="classDescription"><xsl:apply-templates select="description" /></div>

		<a name="{@name}.Syntax" ></a><h2>Syntax</h2>
		<xsl:apply-templates select="syntax" />

	  <xsl:if test="example">
        <a name="{@name}.Example" ></a>
        <h2>Example</h2>
	        <xsl:apply-templates select="example" />
		</xsl:if>
		
		<xsl:if test="remarks and remarks/text()">
		<a name="{@name}.Remarks" ></a><h2>Remarks</h2>
		<p><xsl:apply-templates select="remarks" /></p>
		</xsl:if>
		
	 </div>  
  </xsl:template>

  <xsl:template match="example">
    <xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>

  <xsl:template match="description">
    <xsl:choose>
	<xsl:when test="not(normalize-space(.))">
		No description provided.
	</xsl:when>
	<xsl:otherwise>
	    <xsl:value-of select="." disable-output-escaping="yes"/>
	</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="remarks">
    <xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>

  <xsl:template match="syntax">
    <xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>

</xsl:stylesheet>
