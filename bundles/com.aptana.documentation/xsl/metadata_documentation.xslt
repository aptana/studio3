<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="no"  encoding="utf-8" standalone="no"/>

  <xsl:param name="ReferenceName" />
  <xsl:param name="ReferenceDisplayName" />
  <xsl:param name="ReferenceElementName" />
  <xsl:param name="ReferenceFieldName" />
  <xsl:param name="ReferenceEventName" />
  <xsl:param name="ReferenceElementNamePlural" />
  <xsl:param name="ReferenceFieldNamePlural" />
  <xsl:param name="ReferenceEventNamePlural" />
  <xsl:param name="ReferenceLanguageType" />
  <xsl:param name="browsers" select="//browser/@platform[not(.=preceding::browser/@platform) and (. != 'None')]" />

  <xsl:variable name="lcletters">abcdefghijklmnopqrstuvwxyz</xsl:variable>
  <xsl:variable name="ucletters">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
  
  <xsl:template match="/javascript">

    Content for docs.xml.
    
    <xsl:call-template name="toc" />
    <xsl:call-template name="index" />
	<xsl:if test="elements/element">
	    <xsl:call-template name="index-elements" />
	    <xsl:call-template name="elements" />
	</xsl:if>
	<xsl:if test="fields/field">
	    <xsl:call-template name="index-fields" />
	    <xsl:call-template name="fields" />
	</xsl:if>
	<xsl:if test="events/event">
	    <xsl:call-template name="index-events" />
	    <xsl:call-template name="events" />
	</xsl:if>
	
  </xsl:template>

  <!-- Start TOC templates -->
  <xsl:template name="toc">
    <xsl:result-document href="{$ReferenceName}.toc.xml" method="xml">
	<xsl:processing-instruction name="NLS">TYPE="org.eclipse.help.toc"</xsl:processing-instruction> 

	<toc label="{$ReferenceDisplayName}" link_to="toc.xml#reference"> 
	    <topic label="{$ReferenceDisplayName}" href="html/reference/api/{$ReferenceName}.index.html">
	    	<topic label="{$ReferenceDisplayName} {$ReferenceElementNamePlural}" href="html/reference/api/{$ReferenceName}.index-elements.html">
			    <xsl:for-each select="elements/element">
				<topic label="{@name}" href="html/reference/api/{$ReferenceName}.element.{translate(@full-name,':','_')}.html"></topic>
			    </xsl:for-each>
			</topic>
			<xsl:if test="fields/field">
			    <topic label="{$ReferenceDisplayName} {$ReferenceFieldNamePlural}" href="html/reference/api/{$ReferenceName}.index-fields.html">
				    <xsl:for-each select="fields/field | elements/element/fields/field[child::description]">
                        <xsl:sort select="@name" />
                        <xsl:choose>
                            <xsl:when test="../../@name">
        						<topic label="{@name} ({../../@name})" href="html/reference/api/{$ReferenceName}.field.{translate(@name,':','_')}.{../../@name}.html"></topic>
                            </xsl:when>
                            <xsl:otherwise>
        						<topic label="{@name}" href="html/reference/api/{$ReferenceName}.field.{translate(@name,':','_')}.html"></topic>
                            </xsl:otherwise>
                        </xsl:choose>
				    </xsl:for-each>
			    </topic>
			</xsl:if>
			<xsl:if test="events/event">
			    <topic label="{$ReferenceDisplayName} {$ReferenceEventNamePlural}" href="html/reference/api/{$ReferenceName}.index-events.html">
				    <xsl:for-each select="events/event | elements/element/events/event[child::description]">
                        <xsl:sort select="@name" />
                        <xsl:choose>
                            <xsl:when test="../../@name">
        						<topic label="{@name} ({../../@name})" href="html/reference/api/{$ReferenceName}.event.{translate(@name,':','_')}.{../../@name}.html"></topic>
                            </xsl:when>
                            <xsl:otherwise>
        						<topic label="{@name}" href="html/reference/api/{$ReferenceName}.event.{translate(@name,':','_')}.html"></topic>
                            </xsl:otherwise>
                        </xsl:choose>
				    </xsl:for-each>
			    </topic>	    
			</xsl:if>
	    </topic>
	</toc>
    </xsl:result-document>
  </xsl:template>
  <!-- End TOC templates -->

  <!-- Start index templates -->
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
	    <h1><xsl:value-of select="$ReferenceDisplayName" /> Reference</h1>
	    <div class="content">
		<div class="classDescription">Please select a <xsl:value-of select="$ReferenceDisplayName" /> item</div>
		
			<xsl:if test="elements/element">
			    <h2><a href="{$ReferenceName}.index-elements.html"><xsl:value-of select="$ReferenceDisplayName"/>&#160;<xsl:value-of select="$ReferenceElementNamePlural" /></a></h2>
				<ul>
				<xsl:for-each select="elements/element">
					<li><a href="{$ReferenceName}.element.{translate(@full-name,':','_')}.html"><xsl:value-of select="@name"/></a><xsl:if test="@full-name">&#160;(<xsl:value-of select="@full-name"/>)</xsl:if></li>
				</xsl:for-each>		    
				</ul>
			</xsl:if>
			<xsl:if test="fields/field">
			    <h2><a href="{$ReferenceName}.index-fields.html"><xsl:value-of select="$ReferenceDisplayName"/>&#160;<xsl:value-of select="$ReferenceFieldNamePlural" /></a></h2>
                <ul>
				<xsl:for-each select="fields/field | elements/element/fields/field[child::description]">
                    <xsl:sort select="@name" />
                    <xsl:choose>
                        <xsl:when test="../../@name">
         					<li><a href="{$ReferenceName}.field.{translate(@name,':','_')}.{../../@name}.html"><xsl:value-of select="@name"/></a>&#160;(<xsl:value-of select="../../@name"/>)</li>
                        </xsl:when>
                        <xsl:otherwise>
         					<li><a href="{$ReferenceName}.field.{translate(@name,':','_')}.html"><xsl:value-of select="@name"/></a></li>
                       </xsl:otherwise>
                    </xsl:choose>
				</xsl:for-each>		    
				</ul>
			</xsl:if>
			<xsl:if test="events/event">
			    <h2><a href="{$ReferenceName}.index-events.html"><xsl:value-of select="$ReferenceDisplayName"/>&#160;<xsl:value-of select="$ReferenceEventNamePlural" /></a></h2>
				<ul>
				<xsl:for-each select="events/event | elements/element/events/event[child::description]">
                    <xsl:sort select="@name" />
                    <xsl:choose>
                        <xsl:when test="../../@name">
         					<li><a href="{$ReferenceName}.event.{translate(@name,':','_')}.{../../@name}.html"><xsl:value-of select="@name"/></a>&#160;(<xsl:value-of select="../../@name"/>)</li>
                        </xsl:when>
                        <xsl:otherwise>
         					<li><a href="{$ReferenceName}.event.{translate(@name,':','_')}.html"><xsl:value-of select="@name"/></a></li>
                       </xsl:otherwise>
                    </xsl:choose>
				</xsl:for-each>		    
				</ul>
			</xsl:if>
		
        </div>
        </div>
      </body>
    </html>
    </xsl:result-document>
  </xsl:template>

   <xsl:template name="index-elements">
    <xsl:result-document href="{$ReferenceName}.index-elements.html" method="html">
    <html>
      <head>
        <title><xsl:value-of select="$ReferenceDisplayName" />&#160;<xsl:value-of select="$ReferenceElementName" /> Index</title>
		<link rel="stylesheet" href="../../../content/shared.css" type="text/css" />        
      </head>
      <body>
        <div class="classBlock">
	    <img src="../../../content/aptana.gif" hspace="21" vspace="3"></img>
	    <h1><xsl:value-of select="$ReferenceDisplayName" />&#160;<xsl:value-of select="$ReferenceElementName" /> Index</h1>
	    <div class="content">
		<div class="classDescription">Select a <xsl:value-of select="translate($ReferenceElementName,$ucletters,$lcletters)"/> to visit its documentation.</div>
		
    	<table cellspacing="3" width="90%">

		<tr class="compheader">
			<th align="left"><xsl:value-of select="$ReferenceElementName" /></th>
	    	<xsl:for-each select="$browsers">
	    		<th><xsl:value-of select="." /></th>
			</xsl:for-each>
		</tr>
		    		
        <xsl:for-each select="elements/element">
        	<xsl:call-template name="item-index">
        		<xsl:with-param name="name" select="@full-name" />
        		<xsl:with-param name="type">element</xsl:with-param>
         	</xsl:call-template>
    	</xsl:for-each>
    	</table>
		
        </div>
        </div>
      </body>
    </html>
    </xsl:result-document>
  </xsl:template>
  
    <xsl:template name="index-fields">
    <xsl:result-document href="{$ReferenceName}.index-fields.html" method="html">
    <html>
      <head>
        <title><xsl:value-of select="$ReferenceDisplayName" />&#160;<xsl:value-of select="$ReferenceFieldName" /> Index</title>
		<link rel="stylesheet" href="../../../content/shared.css" type="text/css" />        
      </head>
      <body>
        <div class="classBlock">
	    <img src="../../../content/aptana.gif" hspace="21" vspace="3"></img>
	    <h1><xsl:value-of select="$ReferenceDisplayName" />&#160;<xsl:value-of select="$ReferenceFieldName" /> Index</h1>
	    <div class="content">
		<div class="classDescription">Select a <xsl:value-of select="translate($ReferenceFieldName,$ucletters,$lcletters)"/> to visit its documentation.</div>

    	<table cellspacing="3" width="90%">

		<tr class="compheader">
			<th align="left"><xsl:value-of select="$ReferenceFieldName" /></th>
	    	<xsl:for-each select="$browsers">
	    		<th><xsl:value-of select="." /></th>
			</xsl:for-each>
		</tr>
		    		
        <xsl:for-each select="fields/field | elements/element/fields/field[child::description]">
            <xsl:sort select="@name" />
        	<xsl:call-template name="item-index">
        		<xsl:with-param name="name" select="@name" />
        		<xsl:with-param name="type">field</xsl:with-param>
           	</xsl:call-template>
    	</xsl:for-each>
    	</table>
		
        </div>
        </div>
      </body>
    </html>
    </xsl:result-document>
  </xsl:template>
  
    <xsl:template name="index-events">
    <xsl:result-document href="{$ReferenceName}.index-events.html" method="html">
    <html>
      <head>
        <title><xsl:value-of select="$ReferenceDisplayName" />&#160;<xsl:value-of select="$ReferenceEventName" /> Index</title>
		<link rel="stylesheet" href="../../../content/shared.css" type="text/css" />        
      </head>
      <body>
        <div class="classBlock">
	    <img src="../../../content/aptana.gif" hspace="21" vspace="3"></img>
	    <h1><xsl:value-of select="$ReferenceDisplayName" />&#160;<xsl:value-of select="$ReferenceEventName" /> Index</h1>
	    <div class="content">
		<div class="classDescription">Select a <xsl:value-of select="translate($ReferenceEventName,$ucletters,$lcletters)"/> to visit its documentation.</div>
						
    	<table cellspacing="3" width="90%">

		<tr class="compheader">
			<th align="left"><xsl:value-of select="$ReferenceEventName" /></th>
	    	<xsl:for-each select="$browsers">
	    		<th><xsl:value-of select="." /></th>
			</xsl:for-each>
		</tr>
		    		
        <xsl:for-each select="events/event | elements/element/events/event[child::description]">
            <xsl:sort select="@name" />
        	<xsl:call-template name="item-index">
        		<xsl:with-param name="name" select="@name" />
        		<xsl:with-param name="type">event</xsl:with-param>
        	</xsl:call-template>
    	</xsl:for-each>
    	</table>
		
        </div>
        </div>
      </body>
    </html>
    </xsl:result-document>
  </xsl:template>

  <xsl:template name="item-index">
    <xsl:param name="name" select="@name" />
    <xsl:param name="type">element</xsl:param>
  	<tr>
  		<td class="declaration" rowspan="2">
  		<div class="name">
            <xsl:choose>
                <xsl:when test="../../@name">
          			<a href="{$ReferenceName}.{$type}.{translate($name,':','_')}.{../../@name}.html"><xsl:value-of select="@name"/></a>&#160;(<xsl:value-of select="../../@name"/>)
                </xsl:when>
                <xsl:otherwise>
          			<a href="{$ReferenceName}.{$type}.{translate($name,':','_')}.html"><xsl:value-of select="@name"/></a><xsl:if test="@full-name">&#160;(<xsl:value-of select="@full-name"/>)</xsl:if>
               </xsl:otherwise>
            </xsl:choose>
  		</div>
  		<div><xsl:apply-templates select="description" /></div>

		</td>
		<xsl:apply-templates select="browsers" />
	</tr>
	<tr>
		<td colspan="5">
			<xsl:choose>
					<xsl:when test="browsers/browser/description">
						<xsl:call-template name="browser-notes" />
					</xsl:when>
					<xsl:otherwise>
						&#160;
					</xsl:otherwise>
			</xsl:choose>
		</td>
	</tr>
  </xsl:template>
  <!-- End index templates -->

  <xsl:template name="elements">
    <xsl:for-each select="elements/element">
        <xsl:call-template name="element_page" />
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="element_page">
    <xsl:result-document href="{$ReferenceName}.element.{@full-name}.html" method="html">
    <html>
      <head>
        <title><xsl:value-of select="$ReferenceDisplayName" />: <xsl:value-of select="@name"/></title>
		<link rel="stylesheet" href="../../../content/shared.css" type="text/css" />
	  </head>
      <body>
            <img src="../../../content/aptana.gif" hspace="21" vspace="3"></img>
            <h1>
              <xsl:value-of select="@name"/>
            </h1>
            <div class="content">
        		<div class="classDescription"><xsl:apply-templates select="description" /></div>
        
        	 	<xsl:if test="browsers/browser">
        	 	<a name="{@type}.Platform Support" ></a><h2>Platform Support</h2>
        	 	<p>
        	 		<table cellspacing="3" width="90%">
        			<tr class="compheader">
        				<xsl:call-template name="browser-headers" />
        			</tr>
        	 		<xsl:apply-templates select="browsers" />
        			<xsl:if test="browsers/browser/description">
        				<tr>
        					<td colspan="5">
        						<xsl:call-template name="browser-notes" />
        					</td>
        				</tr>			
        			</xsl:if>
        			</table>
        			</p>
        	 	</xsl:if>
        
        	    <xsl:if test="example">
                <a name="{@name}.Example" ></a>
                <h2>Example</h2>
        	    <p><xsl:apply-templates select="example" /></p>
        		</xsl:if>
        		
        		<xsl:if test="remarks and remarks/text()">
        		<a name="{@name}.Remarks" ></a><h2>Remarks</h2>
        		<p><xsl:apply-templates select="remarks" /></p>
        		</xsl:if>
        		
        		<xsl:if test="fields/field">
        		<a name="{@name}.FieldDetail" ></a><h2><xsl:value-of select="$ReferenceFieldName" /> Detail</h2>
        		<p><xsl:apply-templates select="fields" /></p>
        		</xsl:if>
        	    
           	 	<xsl:if test="events/event">
        		<a name="{@name}.EventDetail" ></a><h2><xsl:value-of select="$ReferenceEventName" /> Detail</h2>
        		<p><xsl:apply-templates select="events" /></p>
        		</xsl:if>
        		
        	 	<xsl:if test="availability/specification">
        	 	<a name="{@type}.Availability" ></a><h2>Availability</h2>
        	 	<p><xsl:apply-templates select="availability" /></p>
        	 	</xsl:if>
        		
        	 </div>  
      		<div style="visibility:hidden;display:none"><xsl:value-of select="$ReferenceLanguageType" /> aptana_docs</div>
      </body>
    </html>
    </xsl:result-document>
  </xsl:template>

  <xsl:template name="fields">
    <xsl:for-each select="fields/field | elements/element/fields/field[child::description]">
        <xsl:choose>
            <xsl:when test="../../@name">
                <xsl:call-template name="field_page">
                	<xsl:with-param name="modifier"><xsl:value-of select="../../@name" />.</xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="field_page" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="field_page">
	<xsl:param name="modifier" />
    <xsl:result-document href="{$ReferenceName}.field.{translate(@name,':','_')}.{$modifier}html" method="html">
    <html>
      <head>
        <title><xsl:value-of select="$ReferenceDisplayName" />: <xsl:value-of select="@name"/></title>
		<link rel="stylesheet" href="../../../content/shared.css" type="text/css" />
	    <script type="text/javascript" src="../../../content/jquery-compressed.js"></script>
	    <script type="text/javascript" src="../../../content/thickbox.js"></script> 
	    <link rel="stylesheet" href="../../../content/thickbox.css" type="text/css" media="screen" />     
	  </head>
      <body>
        <xsl:call-template name="field-expanded" />
      	<div style="visibility:hidden;display:none"><xsl:value-of select="$ReferenceLanguageType" /> aptana_docs</div>
      </body>
    </html>
    </xsl:result-document>
  </xsl:template>
  
  <xsl:template name="events">
    <xsl:for-each select="events/event | elements/element/events/event[child::description]">
        <xsl:sort select="@name" />
        <xsl:choose>
            <xsl:when test="../../@name">
                <xsl:call-template name="event_page">
                	<xsl:with-param name="modifier"><xsl:value-of select="../../@name" />.</xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="event_page" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="event_page">
	<xsl:param name="modifier" />
    <xsl:result-document href="{$ReferenceName}.event.{translate(@name,':','_')}.{$modifier}html" method="html">
    <html>
      <head>
        <title><xsl:value-of select="$ReferenceDisplayName" />: <xsl:value-of select="@name"/></title>
		<link rel="stylesheet" href="../../../content/shared.css" type="text/css" />
	  </head>
      <body>
        <xsl:call-template name="field-expanded" />
      	<div style="visibility:hidden;display:none"><xsl:value-of select="$ReferenceLanguageType" /> aptana_docs</div>
      </body>
    </html>
    </xsl:result-document>
  </xsl:template>
  
  <xsl:template name="field-expanded">
    <img src="../../../content/aptana.gif" hspace="21" vspace="3"></img>
    <h1>
      <xsl:value-of select="@name"/>
    </h1>
    <div class="content">
		<div class="classDescription"><xsl:apply-templates select="description" /></div>

	 	<xsl:if test="browsers/browser">
	 	<a name="{@type}.Platform Support" ></a><h2>Platform Support</h2>
	 	<p>
	 		<table cellspacing="3" width="90%">
			<tr class="compheader">
				<xsl:call-template name="browser-headers" />
			</tr>
	 		<xsl:apply-templates select="browsers" />
			<xsl:if test="browsers/browser/description">
				<tr>
					<td colspan="5">
						<xsl:call-template name="browser-notes" />
					</td>
				</tr>			
			</xsl:if>
			</table>
			</p>
	 	</xsl:if>

	    <xsl:if test="hint">
        <a name="{@name}.Usage" ></a>
        <h2>Usage</h2>
	        <p><xsl:value-of select="hint" disable-output-escaping="yes"/></p>
		</xsl:if>
		
	    <xsl:if test="values/value">
	        <h2>Values</h2>	    
	        <p><xsl:choose>
	        	<xsl:when test="values/value/browsers/browser">
	        		<xsl:apply-templates select="values" />
	        	</xsl:when>
	        	<xsl:otherwise>
					<table width="100%" class="parameter-table">
					  	<xsl:for-each select="values/value">
							<tr>
							  	<td width="10%">
					  				<i><xsl:value-of select="@name" /></i>
					  			</td>
							  	<td width="80%">
					  				<xsl:apply-templates select="@description" />
					  			</td>
					  		</tr>
					  	</xsl:for-each>
					</table>
	        	</xsl:otherwise>
	       	</xsl:choose>
	       	</p>
		</xsl:if>
		
	    <xsl:if test="example">
        <a name="{@name}.Example" ></a>
        <h2>Example</h2>
	    <p><xsl:apply-templates select="example" /></p>
		</xsl:if>
		
		<xsl:if test="remarks and remarks/text()">
		<a name="{@name}.Remarks" ></a><h2>Remarks</h2>
		<p><xsl:apply-templates select="remarks" /></p>
		</xsl:if>
		
	 	<xsl:if test="availability/specification">
	 	<a name="{@type}.Availability" ></a><h2>Availability</h2>
	 	<p><xsl:apply-templates select="availability" /></p>
	 	</xsl:if>

	 	<xsl:variable name="name" select="@name" />
	 	<xsl:if test="//elements/element/fields/field[@name = $name] | //elements/element/events/event[@name = $name]">
	 	<a name="{@type}.Related" ></a><h2>Related</h2>
	 	<p>
	 	   <ul>
	 	    <xsl:for-each select="//elements/element/fields/field[@name = $name] | //elements/element/events/event[@name = $name]">
			  	<li><a href="{$ReferenceName}.element.{../../@full-name}.html"><xsl:value-of select="../../@name" /></a></li>
		  	</xsl:for-each>
		  	</ul>
	 	</p>
	 	</xsl:if>
		
	 </div>  
  </xsl:template>
  
  <xsl:template name="browser-headers">
  		    	<xsl:for-each select="$browsers">
		    		<th><xsl:value-of select="." /></th>
				</xsl:for-each>
  </xsl:template>
  
  <xsl:template name="browser-notes">
	<ul>
	<xsl:for-each select="browsers/browser[description/text()]">
		<li><xsl:value-of select="@platform" />: <xsl:value-of select="description" /></li>
	</xsl:for-each>
	</ul>
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
  
    <xsl:template match="fields">
    <div class="list">
    	<table cellspacing="3" width="90%">

		<tr class="compheader">
			<th align="left"><xsl:value-of select="$ReferenceFieldName" /></th>
	    	<xsl:for-each select="$browsers">
	    		<th><xsl:value-of select="." /></th>
			</xsl:for-each>
		</tr>
		    		
        <xsl:for-each select="field">
        	<xsl:variable name="fieldName" select="@name" />
        	<xsl:choose>
	        	<xsl:when test="not(description)">
					<xsl:apply-templates select="/content-assist/fields/field[@name = $fieldName]" />
				</xsl:when>
				<xsl:otherwise>
				    <xsl:apply-templates select="." />
				</xsl:otherwise>
			</xsl:choose>
    	</xsl:for-each>
    	</table>
    </div>
  </xsl:template>

  <xsl:template match="events">
    <div class="list">
    	<table cellspacing="3" width="90%">

		<tr class="compheader">
			<th align="left"><xsl:value-of select="$ReferenceEventName" /></th>
	    	<xsl:for-each select="$browsers">
	    		<th><xsl:value-of select="." /></th>
			</xsl:for-each>
		</tr>
    		
        <xsl:for-each select="event">
        	<xsl:variable name="eventName" select="@name" /> 
        	<xsl:choose>
	        	<xsl:when test="not(description)">
					<xsl:apply-templates select="/content-assist/events/event[@name = $eventName]" />
				</xsl:when>
				<xsl:otherwise>
				    <xsl:apply-templates select="." />
				</xsl:otherwise>
			</xsl:choose>
    	</xsl:for-each>
    	</table>
    </div>
  </xsl:template>

  <xsl:template match="values">
    <div class="list">
    	<table cellspacing="3" width="90%">

		<tr class="compheader">
			<th align="left">Value</th>
	    	<xsl:for-each select="$browsers">
	    		<th><xsl:value-of select="." /></th>
			</xsl:for-each>
		</tr>
    		
        <xsl:for-each select="value">
        	<xsl:apply-templates select="." />
    	</xsl:for-each>
    	</table>
    </div>
  </xsl:template>
  
  <xsl:template match="field">
  	<tr>
  		<td class="declaration" rowspan="2">

  		<div class="name"><a name="{../../@name}.{@name}"></a>
			<xsl:choose>
				<xsl:when test="../../@name">
          			<a href="{$ReferenceName}.field.{translate(@name,':','_')}.{../../@name}.html"><xsl:value-of select="@name"/></a> (<xsl:value-of select="../../@name"/>)
				</xsl:when>
				<xsl:otherwise>
          			<a href="{$ReferenceName}.field.{translate(@name,':','_')}.html"><xsl:value-of select="@name"/></a>
				</xsl:otherwise>
			</xsl:choose>
  			<xsl:if test="hint"><br /><xsl:value-of select="hint" disable-output-escaping="yes" /></xsl:if>
  		</div>
  		<div><xsl:apply-templates select="description" /></div>

		</td>
		<xsl:apply-templates select="browsers" />
	</tr>
	<tr>
		<td colspan="5">
			<xsl:choose>
					<xsl:when test="browsers/browser/description">
						<xsl:call-template name="browser-notes" />
					</xsl:when>
					<xsl:otherwise>
						&#160;
					</xsl:otherwise>
			</xsl:choose>
		</td>
	</tr>
  </xsl:template>

  <xsl:template match="value">
  	<tr>
  		<td class="declaration" rowspan="2"><div class="name"><a name="{../../@name}.{@name}"></a><xsl:value-of select="@name" /></div>
  		<div><xsl:apply-templates select="description" /></div>
		</td>
		<xsl:apply-templates select="browsers" />
	</tr>
	<tr>
		<td colspan="5">
			<xsl:choose>
					<xsl:when test="browsers/browser/description">
						<xsl:call-template name="browser-notes" />
					</xsl:when>
					<xsl:otherwise>
						&#160;
					</xsl:otherwise>
			</xsl:choose>
		</td>
	</tr>
  </xsl:template>
  
  <xsl:template match="event">
  	<tr>
  		<td class="declaration" rowspan="2">
  		<div class="name"><a name="{../../@name}.{@name}"></a>
			<xsl:choose>
				<xsl:when test="../../@name">
          			<a href="{$ReferenceName}.event.{translate(@name,':','_')}.{../../@name}.html"><xsl:value-of select="@name"/></a> (<xsl:value-of select="../../@name"/>)
				</xsl:when>
				<xsl:otherwise>
          			<a href="{$ReferenceName}.event.{translate(@name,':','_')}.html"><xsl:value-of select="@name"/></a>
				</xsl:otherwise>
			</xsl:choose>
  			<xsl:if test="hint"><br /><xsl:value-of select="hint" disable-output-escaping="yes" /></xsl:if>
  		</div>
  		<div><xsl:apply-templates select="description" /></div>
		
		</td>
		<xsl:apply-templates select="browsers" />
	</tr>
	<tr>
		<td colspan="5">
			<xsl:choose>
					<xsl:when test="browsers/browser/description">
						<xsl:call-template name="browser-notes" />
					</xsl:when>
					<xsl:otherwise>
						&#160;
					</xsl:otherwise>
			</xsl:choose>
		</td>
	</tr>
  </xsl:template>

  <xsl:template match="availability">
	<xsl:for-each select="specification">
	  	<xsl:value-of select="@name" />
		<xsl:if test="@version">&#160;<xsl:value-of select="@version" /></xsl:if>	  	
  		<xsl:if test="position() != last()">&#160;|&#160;</xsl:if>
  	</xsl:for-each>
  </xsl:template>

  <xsl:template match="browsers">
  	<xsl:variable name="elementBrowsers" select="." />
	<xsl:for-each select="$browsers">
		<td align="center">
	  	<xsl:variable name="currentBrowser" select="." />
		<xsl:attribute name="title" select="$currentBrowser" />
		<xsl:choose>
		<xsl:when test="$elementBrowsers/browser/@platform = .">	
		  	<xsl:variable name="currentBrowserDescription" select="$elementBrowsers/browser[@platform = $currentBrowser]/description" />
		  	<xsl:choose>
		  		<xsl:when test="contains($currentBrowserDescription, 'buggy')">
					<xsl:attribute name="class">comparison buggy</xsl:attribute>
				</xsl:when>
		  		<xsl:when test="string-length($currentBrowserDescription) > 0">
					<xsl:attribute name="class">comparison incomplete</xsl:attribute>
				</xsl:when>
		  		<xsl:otherwise>
					<xsl:attribute name="class">comparison yes</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:for-each select="$elementBrowsers/browser[@platform = $currentBrowser]"><xsl:value-of select="@version" /></xsl:for-each>
		</xsl:when>	
		<xsl:otherwise><xsl:attribute name="class">comparison no</xsl:attribute>no</xsl:otherwise>
		</xsl:choose>
		</td>
  	</xsl:for-each>
  </xsl:template>
    
</xsl:stylesheet>
