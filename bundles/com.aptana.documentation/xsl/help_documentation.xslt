<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:preserve-space elements="text" />
	<xsl:output method="html" indent="yes" encoding="utf-8" standalone="no"/>
	<xsl:param name="ReferenceName"/>
	<xsl:param name="ReferenceDisplayName"/>
	<xsl:param name="ReferenceLanguageType"/>
	<xsl:param name="browsers" select="//browser/@platform[not(.=preceding::browser/@platform) and (. != 'None')]"/>
	<xsl:param name="browsersGlobal" select="//browser/@platform[not(.=preceding::browser/@platform) and (. != 'None')]"/>
	<xsl:template match="/javascript">

    Content for docs.xml.
    
    <xsl:call-template name="toc"/>
    <xsl:call-template name="index-frame"/>
    <xsl:call-template name="index-toc"/>
    <xsl:call-template name="index"/>
    <xsl:call-template name="classes"/>

  </xsl:template>
	<xsl:template name="toc">
		<xsl:result-document href="{$ReferenceName}.toc.xml" method="xml">
			<xsl:processing-instruction name="NLS">TYPE="org.eclipse.help.toc"</xsl:processing-instruction>
			<toc label="{$ReferenceDisplayName}" link_to="../com.aptana.documentation/toc.xml#reference">
				<topic label="{$ReferenceDisplayName}" href="html/reference/api/{$ReferenceName}.index.html">
					<xsl:for-each select="class">
						<topic label="{@type}" href="html/reference/api/{@type}.html">
							<xsl:if test="constructors/constructor">
								<topic label="Constructors" href="html/reference/api/{@type}.html#{@type}.Constructors">
									<xsl:for-each select="constructors/constructor">
										<topic label="Constructor" href="html/reference/api/{../../@type}.html?visibility={@visibility}#{../../@type}.{@name}"/>
									</xsl:for-each>
								</topic>
							</xsl:if>
							<xsl:if test="properties/property">
								<topic label="Properties" href="html/reference/api/{@type}.html#{@type}.Properties">
									<xsl:for-each select="properties/property">
										<topic label="{@name}" href="html/reference/api/{../../@type}.html?visibility={@visibility}#{../../@type}.{@name}"/>
									</xsl:for-each>
								</topic>
							</xsl:if>
							<xsl:if test="methods/method">
								<topic label="Functions" href="html/reference/api/{@type}.html#{@type}.Functions">
									<xsl:for-each select="methods/method">
										<topic label="{@name}" href="html/reference/api/{../../@type}.html?visibility={@visibility}#{../../@type}.{@name}"/>
									</xsl:for-each>
								</topic>
							</xsl:if>
						</topic>
					</xsl:for-each>
				</topic>
			</toc>
		</xsl:result-document>
	</xsl:template>
	<xsl:template name="index-frame">
		<xsl:result-document href="{$ReferenceName}.index-frame.html" method="html">
			<html>
				<head>
					<title><xsl:value-of select="$ReferenceDisplayName"/> Reference Index</title>
					<meta http-equiv="cache-control" content="no-cache" />
				</head>
				<frameset cols="270,*" rows="*" border="0" framespacing="3">
					<frame src="{$ReferenceName}.index-toc.html"/>
					<frame src="{$ReferenceName}.index.html" name="content"/>
				</frameset>
				<noframes>
					<body>
						<p>This page uses frames, but your browser doesn't support them.</p>
					</body>
				</noframes>
			</html>
		</xsl:result-document>
	</xsl:template>
	<xsl:template name="index-toc">
		<xsl:result-document href="{$ReferenceName}.index-toc.html" method="html">
			<html>
				<head>
					<title><xsl:value-of select="$ReferenceDisplayName"/> Reference Index</title>
					<meta http-equiv="cache-control" content="no-cache" />
					<link rel="stylesheet" href="../../../content/shared.css" type="text/css"/>
					<link rel="stylesheet" href="../../../content/tree.css" type="text/css"/>
					<script src="../../../content/jquery.js" type="text/javascript"/>
					<script src="../../../content/jquery.cookie.js" type="text/javascript"/>
					<script src="../../../content/jquery.treeview.js" type="text/javascript"/>
					<script type="text/javascript">
			$(document).ready(function(){
				$("#black").treeview({persist: "cookie"});
			});
			</script>
				</head>
				<body>
					<h2>
						<a href="{$ReferenceName}.index.html" target="content"><xsl:value-of select="$ReferenceDisplayName"/> Reference Index</a>
					</h2>
					<ul id="black">
						<xsl:for-each select="class">
							<li class="closed">
								<span>
									<a href="{@type}.html" target="content">
										<xsl:value-of select="@type"/>
									</a>
								</span>
								<ul>
									<xsl:if test="constructors/constructor">
										<li class="closed">
											<span>
												<a href="{@type}.html#{@type}.Constructors" target="content">Constructors</a>
											</span>
											<ul>
												<xsl:for-each select="constructors/constructor">
													<li>
														<span class="constructor constructor-{@scope}" title="{description}">
															<a href="{../../@type}.html?visibility={@visibility}#{../../@type}.{@name}" target="content">Constructor</a><xsl:if test="@visibility != 'basic' and @visibility != ''"> <span class="visibility"> (<xsl:value-of select="@visibility"/>)</span></xsl:if>
														</span>
													</li>
												</xsl:for-each>
											</ul>
										</li>
									</xsl:if>
									<xsl:if test="properties/property">
										<li class="closed">
											<span>
												<a href="{@type}.html#{@type}.Properties" target="content">Properties</a>
											</span>
											<ul>
												<xsl:for-each select="properties/property">
													<li>
														<span class="property {@type} {@type}-{@scope}" title="{description}">
															<a href="{../../@type}.html?visibility={@visibility}#{../../@type}.{@name}" target="content">
																<xsl:value-of select="@name"/></a><xsl:if test="@visibility != 'basic' and @visibility != ''"> <span class="visibility"> (<xsl:value-of select="@visibility"/>)</span></xsl:if>
														</span>
													</li>
												</xsl:for-each>
											</ul>
										</li>
									</xsl:if>
									<xsl:if test="methods/method">
										<li class="closed">
											<span>
												<a href="{@type}.html#{@type}.Functions" target="content">Functions</a>
											</span>
											<ul>
												<xsl:for-each select="methods/method">
													<li>
														<span class="method method-{@scope}" title="{description}">
															<a href="{../../@type}.html?visibility={@visibility}#{../../@type}.{@name}" target="content">
																<xsl:value-of select="@name"/></a><xsl:if test="@visibility != 'basic' and @visibility != ''"> <span class="visibility"> (<xsl:value-of select="@visibility"/>)</span></xsl:if>
														</span>
													</li>
												</xsl:for-each>
											</ul>
										</li>
									</xsl:if>
								</ul>
							</li>
						</xsl:for-each>
					</ul>
				</body>
			</html>
		</xsl:result-document>
	</xsl:template>
	
	<xsl:template name="index">
		<xsl:result-document href="{$ReferenceName}.index.html" method="html">
			<html>
				<head>
					<title><xsl:value-of select="$ReferenceDisplayName"/> Reference Index</title>
					<meta http-equiv="cache-control" content="no-cache" />
					<link rel="stylesheet" href="../../../content/shared.css" type="text/css"/>
					<link rel="stylesheet" href="../../../PRODUCT_PLUGIN/book.css" type="text/css"/>
					<link rel="stylesheet" href="PLUGINS_ROOT/PRODUCT_PLUGIN/book.css" />
					<script src="../../../content/jquery.js" type="text/javascript"/>
					<script src="../../../content/api.js" type="text/javascript"/>
				</head>
				<body>
					<div class="classBlock">
						<h1><xsl:value-of select="$ReferenceDisplayName"/> Index</h1>
						<div class="content">
							<div class="classDescription">
								<xsl:value-of select="overview" disable-output-escaping="yes"/>
								<p>Below is a listing of all classes in the <xsl:value-of select="$ReferenceDisplayName"/> namespace.
								Click on a class to visit its documentation.</p>
							</div>
							<table cellspacing="2" width="90%">
								<tr class="compheader">
									<th style="text-align:left">Class</th>
									<xsl:for-each select="$browsers">
										<th class="browser">
											<xsl:value-of select="."/>
										</th>
									</xsl:for-each>
								</tr>
								<xsl:for-each select="class">
									<xsl:call-template name="item-index"/>
								</xsl:for-each>
							</table>
						</div>
					</div>
				</body>
			</html>
		</xsl:result-document>
	</xsl:template>
	
	<xsl:template name="item-index">
		<tr>
			<td class="declaration">
				<div class="name">
					<a href="{@type}.html">
						<xsl:value-of select="@type"/>
					</a>
				</div>
				<div class="description">
					<xsl:apply-templates select="description"/>
					<xsl:choose>
						<xsl:when test="browsers/browser/description">
							<xsl:call-template name="browser-notes"/>
						</xsl:when>
						<xsl:otherwise>
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</td>
			<xsl:apply-templates select="browsers"/>
		</tr>
	</xsl:template>
	<!-- End index templates -->
	<xsl:template name="classes">
		<xsl:for-each select="class">
			<xsl:result-document href="{@type}.html" method="html">
				<html>
					<head>
						<title><xsl:value-of select="$ReferenceDisplayName"/> Reference: <xsl:value-of select="@type"/></title>
						<meta http-equiv="cache-control" content="no-cache" />
						<link rel="stylesheet" href="../../../content/shared.css" type="text/css"/>
						<link rel="stylesheet" href="../../../PRODUCT_PLUGIN/book.css" type="text/css"/>
						<link rel="stylesheet" href="PLUGINS_ROOT/PRODUCT_PLUGIN/book.css" />
						<script src="../../../content/jquery.js" type="text/javascript"/>
						<script src="../../../content/api.js" type="text/javascript"/>
					</head>
					<body>
						<xsl:apply-templates select="."/>
						<div style="visibility:hidden;display:none"><xsl:value-of select="$ReferenceLanguageType"/> aptana_docs</div>
					</body>
				</html>
			</xsl:result-document>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="class">
		<xsl:variable name="superclass" select="@superclass" />
		<h1>
			<xsl:value-of select="@type"/>
			<xsl:if test="normalize-space(@superclass)"> : <a href="{@superclass}.html"><xsl:apply-templates select="@superclass"/></a></xsl:if>
			<div style="font-weight:normal;font-size:70%">Return to: <a href="{$ReferenceName}.index.html"><xsl:value-of select="$ReferenceDisplayName"/> index</a></div>
		</h1>
		<div class="content">
			<div class="classDescription">
				<xsl:apply-templates select="description"/>
			</div>
			<!--
				<div class="navigator">
					<xsl:if test="constructors/constructor"><a href="#{@type}.Constructors" >Constructors</a></xsl:if>
					<xsl:if test="properties/property"> | <a href="#{@type}.Properties" >Properties</a></xsl:if>
					<xsl:if test="methods/method"> | <a href="#{@type}.Functions" >Functions</a></xsl:if>
					<xsl:if test="example"> | <a href="#{@type}.Examples" >Examples</a></xsl:if>
					<xsl:if test="remarks and remarks/text()"> | <a href="#{@type}.Remarks" >Remarks</a></xsl:if>
					<xsl:if test="references/reference"> | <a href="#{@type}.References" >References</a></xsl:if>
					<xsl:if test="availability/specification"> | <a href="#{@type}.Availability" >Availability</a></xsl:if>
				</div>
			-->
			<xsl:if test="browsers/browser">
				<a name="{@type}.Platform Support"/>
				<h2>Platform Support</h2>
				<p>
					<xsl:call-template name="browser-table"/>
				</p>
			</xsl:if>
			<xsl:if test="constructors/constructor">
				<a name="{@type}.Constructors"/>
				<h2>Constructors</h2>
				<xsl:call-template name="constructors-summary"/>
			</xsl:if>
			<xsl:if test="@superclass != 'Object' and //class[@type=$superclass]/properties/property[@scope='instance']">
				<a name="{@type}.InheritedProperties"/>
				<h2>Inherited Properties</h2>
				<xsl:call-template name="properties-summary">
					<xsl:with-param name="properties" select="//class[@type=$superclass]/properties/property[@scope='instance']"/>
				</xsl:call-template>
			</xsl:if>			
			<xsl:if test="properties/property">
				<a name="{@type}.Properties"/>
				<h2>Properties</h2>
				<xsl:call-template name="properties-summary"/>
			</xsl:if>
			<xsl:if test="@superclass != 'Object' and //class[@type=$superclass]/methods/method[@scope='instance']">
				<a name="{@type}.InheritedMethods"/>
				<h2>Inherited Functions</h2>
				<xsl:call-template name="methods-summary">
					<xsl:with-param name="methods" select="//class[@type=$superclass]/methods/method[@scope='instance']"/>
				</xsl:call-template>
			</xsl:if>			
			<xsl:if test="methods/method">
				<a name="{@type}.Functions"/>
				<h2>Functions</h2>
				<xsl:call-template name="methods-summary"/>
			</xsl:if>
			<xsl:if test="example">
				<xsl:apply-templates select="example"/>
			</xsl:if>
			<xsl:if test="examples">
				<a name="{@type}.Examples"/>
				<h2>Examples</h2>
				<p>
					<xsl:apply-templates select="examples/example"/>
				</p>
			</xsl:if>
			<xsl:if test="remarks and remarks/text()">
				<a name="{@type}.Remarks"/>
				<h2>Remarks</h2>
				<p>
					<xsl:apply-templates select="remarks"/>
				</p>
			</xsl:if>
			<xsl:if test="references/reference">
				<a name="{@type}.References"/>
				<h2>References</h2>
				<p>
					<xsl:apply-templates select="references"/>
				</p>
			</xsl:if>
			<xsl:if test="availability/specification">
				<a name="{@type}.Availability"/>
				<h2>Availability</h2>
				<p>
					<xsl:apply-templates select="availability"/>
				</p>
			</xsl:if>
<!--
		<xsl:if test="constructors/constructor">
		<a name="{@type}.ConstructorDetail" ></a><h2>Constructor Detail</h2>
		<p><xsl:apply-templates select="constructors" /></p>
		</xsl:if>

		<xsl:if test="properties/property">
		<a name="{@type}.PropertyDetail" ></a><h2>Property Detail</h2>
		<p><xsl:apply-templates select="properties" /></p>
		</xsl:if>
	    
   	 	<xsl:if test="methods/method">
		<a name="{@type}.MethodDetail" ></a><h2>Method Detail</h2>
		<p><xsl:apply-templates select="methods" /></p>
		</xsl:if>
		-->
		</div>
	</xsl:template>
	<xsl:template match="constructors">
		<div class="list">
			<xsl:apply-templates select="constructor"/>
		</div>
	</xsl:template>
	<xsl:template match="properties">
		<div class="list">
			<xsl:apply-templates select="property"/>
		</div>
	</xsl:template>
	<xsl:template match="methods">
		<div class="list">
			<xsl:apply-templates select="method"/>
		</div>
	</xsl:template>
	<xsl:template name="constructors-summary">
		<table cellspacing="2" width="90%">
			<xsl:if test="constructors/constructor[@visibility = 'advanced']">
				<tr>
					<td colspan="{count($browsersGlobal) + 2}" class="toggle-bar">
						<img src="../../../content/show_advanced.gif" class="advanced-toggle" onclick="toggleAdvanced(this)" title="Show/Hide advanced API items"/>
					</td>
				</tr>
			</xsl:if>
			<tr class="compheader" visibility="{@visibility}">
				<th style="text-align:left">Constructor</th>
				<th>Action</th>
				<xsl:for-each select="$browsers">
					<th class="browser">
						<xsl:value-of select="."/>
					</th>
				</xsl:for-each>
			</tr>
			<xsl:for-each select="constructors/constructor">
				<xsl:call-template name="method-summary">
					<xsl:with-param name="name"><xsl:value-of select="../../@type"/> Constructor</xsl:with-param>
					<xsl:with-param name="browsers" select="../../browsers"/>
				</xsl:call-template>
			</xsl:for-each>
		</table>
	</xsl:template>
	<xsl:template match="constructor">
		<a name="{../../@type}.{@name}"/>
		<h3><xsl:if test="@scope != 'instance' and @scope != ''"><xsl:value-of select="@scope"/><xsl:text> </xsl:text></xsl:if><xsl:value-of select="../../@type"/>(<xsl:call-template name="parameters-condensed"/>) : <i><xsl:call-template name="return-types"/></i></h3>
		<xsl:if test="description/text()">
			<p class="padded">
				<xsl:apply-templates select="description"/>
				<xsl:if test="description != return-description"><xsl:apply-templates select="return-description"/></xsl:if>
			</p>
		</xsl:if>
		<xsl:if test="parameters/parameter">
			<p class="padded">
				<xsl:apply-templates select="parameters"/>
			</p>
		</xsl:if>
		<xsl:if test="return-types/return-type">
			<p class="padded">
				<xsl:call-template name="return-type-table"/>
			</p>
		</xsl:if>
		<dl class="details">
			<xsl:if test="example">
				<dd>
					<xsl:apply-templates select="example"/>
				</dd>
			</xsl:if>
			<xsl:if test="examples">
				<dt>Examples</dt>
				<dd>
					<xsl:apply-templates select="examples/example"/>
				</dd>
			</xsl:if>
			<xsl:if test="remarks">
				<dt>Remarks</dt>
				<dd>
					<xsl:apply-templates select="remarks"/>
				</dd>
			</xsl:if>
			<xsl:if test="exceptions/exception">
				<dt>Throws</dt>
				<dd>
					<xsl:apply-templates select="exceptions"/>
				</dd>
			</xsl:if>
			<xsl:if test="references/reference">
				<dt>See Also</dt>
				<dd>
					<p>
						<xsl:apply-templates select="references"/>
					</p>
				</dd>
			</xsl:if>
			<xsl:if test="availability/specification">
				<dt>Availability</dt>
				<dd>
					<p>
						<xsl:apply-templates select="availability"/>
					</p>
				</dd>
			</xsl:if>
<!--
<xsl:if test="browsers/browser">
<a name="{@type}.Platform Support" ></a>
<dt>Platform Support</dt>
<dd><p><xsl:call-template name="browser-table" /></p></dd>
</xsl:if>	
-->
			<xsl:if test="@visibility != 'basic' and @visibility != ''">
				<dt>Visibility</dt>
				<dd>
					<xsl:value-of select="@visibility"/>
				</dd>
			</xsl:if>
		</dl>
	</xsl:template>
	<xsl:template name="properties-summary">
		<xsl:param name="properties" select="properties/property"/>		
		<table cellspacing="2" width="90%">
			<xsl:if test="$properties[@visibility = 'advanced']">
				<tr>
					<td colspan="{count($browsersGlobal) + 2}" class="toggle-bar">
						<img src="../../../content/show_advanced.gif" class="advanced-toggle" onclick="toggleAdvanced(this)" title="Show/Hide advanced API items"/>
					</td>
				</tr>
			</xsl:if>
			<tr class="compheader" visibility="{@visibility}">
				<th style="text-align:left">Property</th>
				<th>Action</th>
				<xsl:for-each select="$browsers">
					<th class="browser">
						<xsl:value-of select="."/>
					</th>
				</xsl:for-each>
			</tr>
			<xsl:for-each select="$properties">
				<xsl:call-template name="property-summary"/>
			</xsl:for-each>
		</table>
	</xsl:template>
	<xsl:template match="property">
		<dl class="details">
			<xsl:if test="example/text()">
				<dd>
					<xsl:apply-templates select="example"/>
				</dd>
			</xsl:if>
			<xsl:if test="examples/example">
				<dt>Examples</dt>
				<dd>
					<xsl:apply-templates select="examples/example"/>
				</dd>
			</xsl:if>
			<xsl:if test="remarks/text()">
				<dt>Remarks</dt>
				<dd>
					<xsl:apply-templates select="remarks"/>
				</dd>
			</xsl:if>
			<xsl:if test="exceptions/exception">
				<dt>Throws</dt>
				<dd>
					<xsl:apply-templates select="exceptions"/>
				</dd>
			</xsl:if>
			<xsl:if test="references/reference">
				<dt>See Also</dt>
				<dd>
					<p>
						<xsl:apply-templates select="references"/>
					</p>
				</dd>
			</xsl:if>
			<xsl:if test="availability/specification">
				<dt>Availability</dt>
				<dd>
					<p>
						<xsl:apply-templates select="availability"/>
					</p>
				</dd>
			</xsl:if>
<!--
		 	<xsl:if test="browsers/browser">
		 	<a name="{@type}.Platform Support" ></a>
		 	<dt>Platform Support</dt>
		 	<dd><p><xsl:call-template name="browser-table" /></p></dd>
		 	</xsl:if>	
		 	-->
			<xsl:if test="@visibility != 'basic' and @visibility != ''">
				<dt>Visibility</dt>
				<dd>
					<xsl:value-of select="@visibility"/>
				</dd>
			</xsl:if>
		</dl>
	</xsl:template>
	<xsl:template name="methods-summary">
		<xsl:param name="methods" select="methods/method"/>		
		<table cellspacing="2" width="90%">
			<xsl:if test="$methods[@visibility = 'advanced']">
				<tr>
					<td colspan="{count($browsersGlobal) + 2}" class="toggle-bar">
						<img src="../../../content/show_advanced.gif" class="advanced-toggle" onclick="toggleAdvanced(this)" title="Show/Hide advanced API items"/>
					</td>
				</tr>
			</xsl:if>
			<tr class="compheader" visibility="{@visibility}">
				<th style="text-align:left">Method</th>
				<th>Action</th>
				<xsl:for-each select="$browsers">
					<th class="browser">
						<xsl:value-of select="."/>
					</th>
				</xsl:for-each>
			</tr>
			<xsl:for-each select="$methods">
				<xsl:call-template name="method-summary"/>
			</xsl:for-each>
		</table>
	</xsl:template>
	<xsl:template name="property-summary">
		<xsl:param name="name" select="@name"/>
		<xsl:param name="browsers" select="browsers"/>
		<tr class="item {@visibility}">
			<td class="declaration">
				<div class="name"><a name="{../../@type}.{@name}"/><xsl:if test="@scope != 'instance' and @scope != ''"><xsl:value-of select="@scope"/><xsl:text> </xsl:text></xsl:if>
					<xsl:choose>
						<xsl:when test="parameters/parameter or return-types/return-type or example/text() or examples/example or remarks/text() or exceptions/exception or references/reference or availability/specification">
							<a href="javascript:void(0);" onclick="toggleClickDetails(this)"><xsl:value-of select="$name"/></a></xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$name"/></xsl:otherwise>
					</xsl:choose>
					 : <i><xsl:value-of select="@type"/></i></div>
				<div class="description">
					<xsl:apply-templates select="description"/>
				</div>
			</td>
			<td class="show-details">
				<xsl:choose>
					<xsl:when test="parameters/parameter or return-types/return-type or example/text() or examples/example or remarks/text() or exceptions/exception or references/reference or availability/specification">
						<xsl:attribute name="class">show-details</xsl:attribute>
						<a href="javascript:void(0);" onclick="toggleRowDetails(this)" style="color:#FFFFFF;">Show Details</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="class">no-details</xsl:attribute>
						No Details
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<xsl:apply-templates select="$browsers"/>
		</tr>
		<tr class="details-property">
			<td valign="top" colspan="{count($browsersGlobal) + 2}">
				<xsl:choose>
					<xsl:when test="$browsers/browser/description">
						<xsl:call-template name="browser-notes"/>
					</xsl:when>
					<xsl:otherwise>
							
					</xsl:otherwise>
				</xsl:choose>
				<xsl:apply-templates select="."/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template name="method-summary">
		<xsl:param name="name" select="@name"/>
		<xsl:param name="browsers" select="browsers"/>
		<tr class="item {@visibility}">
			<td class="declaration">
				<div class="name"><a name="{../../@type}.{@name}"/><xsl:if test="@scope != 'instance' and @scope != ''"><xsl:value-of select="@scope"/><xsl:text> </xsl:text></xsl:if>
					<xsl:choose>
						<xsl:when test="parameters/parameter or return-types/return-type or example/text() or examples/example or remarks/text() or exceptions/exception or references/reference or availability/specification">
							<a href="javascript:void(0);" onclick="toggleClickDetails(this)"><xsl:value-of select="$name"/></a></xsl:when>
						<xsl:otherwise><xsl:value-of select="$name"/></xsl:otherwise>
					</xsl:choose>(<xsl:call-template name="parameters-condensed"/>) : <i><xsl:call-template name="return-types"/></i>
	  		</div>
				<div class="description">
					<xsl:apply-templates select="description"/>
			<xsl:if test="@description != @return-description"><xsl:apply-templates select="return-description"/></xsl:if>
				</div>
			</td>
			<td>
				<xsl:choose>
					<xsl:when test="parameters/parameter or return-types/return-type or example/text() or examples/example or remarks/text() or exceptions/exception or references/reference or availability/specification">
						<xsl:attribute name="class">show-details</xsl:attribute>
						<a href="javascript:void(0);" onclick="toggleRowDetails(this)" style="color:#FFFFFF;">Show Details</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="class">no-details</xsl:attribute>
						No Details
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<xsl:apply-templates select="$browsers"/>
		</tr>
		<tr class="details-method">
			<td valign="top" colspan="{count($browsersGlobal) + 2}">
				<xsl:choose>
					<xsl:when test="$browsers/browser/description">
						<xsl:call-template name="browser-notes"/>
					</xsl:when>
					<xsl:otherwise>
							
					</xsl:otherwise>
				</xsl:choose>
				<xsl:apply-templates select="."/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="method">
		<xsl:if test="parameters/parameter">
			<p class="padded">
				<xsl:apply-templates select="parameters"/>
			</p>
		</xsl:if>
		<xsl:if test="return-types/return-type">
			<p class="padded">
				<xsl:call-template name="return-type-table"/>
			</p>
		</xsl:if>
		<dl class="details">
			<xsl:if test="example/text()">
				<dd>
					<xsl:apply-templates select="example"/>
				</dd>
			</xsl:if>
			<xsl:if test="examples/example">
				<dt>Examples</dt>
				<dd>
					<xsl:apply-templates select="examples/example"/>
				</dd>
			</xsl:if>
			<xsl:if test="remarks/text()">
				<dt>Remarks</dt>
				<dd>
					<xsl:apply-templates select="remarks"/>
				</dd>
			</xsl:if>
			<xsl:if test="exceptions/exception">
				<dt>Throws</dt>
				<dd>
					<xsl:apply-templates select="exceptions"/>
				</dd>
			</xsl:if>
			<xsl:if test="references/reference">
				<dt>See Also</dt>
				<dd>
					<p>
						<xsl:apply-templates select="references"/>
					</p>
				</dd>
			</xsl:if>
			<xsl:if test="availability/specification">
				<dt>Availability</dt>
				<dd>
					<p>
						<xsl:apply-templates select="availability"/>
					</p>
				</dd>
			</xsl:if>
<!--
		 	<xsl:if test="browsers/browser">
		 	<a name="{@type}.Platform Support" ></a>
		 	<dt>Platform Support</dt>
		 	<dd><p><xsl:call-template name="browser-table" /></p></dd>
		 	</xsl:if>
		 	-->
		</dl>
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
	<xsl:template match="return-description">
		<xsl:value-of select="." disable-output-escaping="yes"/>
	</xsl:template>
	<xsl:template match="remarks">
		<xsl:value-of select="." disable-output-escaping="yes"/>
	</xsl:template>
	<xsl:template name="parameters-condensed">
		<xsl:for-each select="parameters/parameter"><xsl:if test="@usage = 'optional'">[</xsl:if><i><xsl:value-of select="@type"/></i>&#160;<xsl:value-of select="@name"/><xsl:if test="position() != last()">,</xsl:if><xsl:if test="@usage = 'optional'">]</xsl:if>
		<xsl:if test="position() != last()"><xsl:text> </xsl:text></xsl:if></xsl:for-each>
	</xsl:template>
	
	<xsl:template match="parameters">
		<b>Parameters</b>
		<table cellspacing="1" width="90%" class="parameter-table">
			<xsl:for-each select="parameter">
				<tr>
					<td width="10%">
						<i>
							<xsl:value-of select="@type"/>
						</i>
					</td>
					<td width="10%">
						<b>
							<xsl:value-of select="@name"/>
						</b>
					</td>
					<td width="80%">
						<xsl:if test="@usage != 'required'"><b>(<xsl:value-of select="@usage"/>)</b></xsl:if>
						<xsl:apply-templates select="description"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
	
	<xsl:template name="return-type-table">
		<b>Returns</b>
		<table cellspacing="1" width="90%" class="parameter-table">
			<xsl:for-each select="return-types/return-type">
				<tr>
					<td width="20%">
						<i>
							<xsl:value-of select="@type"/>
						</i>
					</td>
					<td width="80%">
						<xsl:apply-templates select="description"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
	
	<xsl:template match="exceptions">
		<dl>
			<xsl:apply-templates select="exception"/>
		</dl>
	</xsl:template>
	<xsl:template match="exception">
		<xsl:if test="@name">
			<dt>
				<xsl:value-of select="@name"/>
			</dt>
		</xsl:if>
		<dd>
			<xsl:apply-templates select="description"/>
		</dd>
	</xsl:template>
	<xsl:template match="references">
		<xsl:for-each select="reference">
			<xsl:variable name="tokenizedSample" select="tokenize(@name,'\.')"/>
			<a>
				<xsl:choose>
					<xsl:when test="starts-with(@name,lower-case(substring(@name,1,1)))">
						<xsl:attribute name="href">#<xsl:value-of select="@name"/></xsl:attribute>
					</xsl:when>
					<xsl:when test="count($tokenizedSample) = 1">
						<xsl:attribute name="href"><xsl:value-of select="$tokenizedSample[1]"/>.html</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="href">
						    <xsl:for-each select="$tokenizedSample">
						 		<xsl:choose>
						    		<xsl:when test="position() = 1"><xsl:value-of select="."/></xsl:when>
									<xsl:when test="position() = last()">
										<xsl:choose>
											<xsl:when test="starts-with(.,upper-case(substring(.,1,1)))">.<xsl:value-of select="."/></xsl:when>
											<xsl:otherwise></xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:otherwise>.<xsl:value-of select="."/></xsl:otherwise>
							</xsl:choose></xsl:for-each>.html#<xsl:value-of select="@name"/></xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:value-of select="@name"/>
			</a>
			<xsl:if test="position() != last()">|</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="availability">
		<xsl:for-each select="specification">
			<xsl:value-of select="@name"/>
			<xsl:if test="position() != last()">|</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="return-types">
		<xsl:choose>
			<xsl:when test="return-types/return-type">
				<xsl:for-each select="return-types/return-type">
					<xsl:value-of select="@type"/>
					<xsl:if test="position() != last()">|</xsl:if>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
			void
		</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="browser-table">
		<table cellspacing="2" width="90%">
			<tr class="compheader">
				<xsl:call-template name="browser-headers"/>
			</tr>
			<xsl:apply-templates select="browsers"/>
			<xsl:if test="browsers/browser/description">
				<tr>
					<td>
						<xsl:attribute name="colspan">
							<xsl:value-of select="count($browsersGlobal)"/>
						</xsl:attribute>
						<xsl:call-template name="browser-notes"/>
					</td>
				</tr>
			</xsl:if>
		</table>
	</xsl:template>
	<xsl:template name="browser-headers">
		<xsl:for-each select="$browsers">
			<th class="browser">
				<xsl:value-of select="."/>
			</th>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="browser-notes">
		<ul>
			<xsl:for-each select="browsers/browser[description/text()]">
				<li><xsl:value-of select="@platform"/>: <xsl:value-of select="description"/></li>
			</xsl:for-each>
		</ul>
	</xsl:template>
	<xsl:template match="browsers">
		<xsl:variable name="elementBrowsers" select="."/>
		<xsl:for-each select="$browsers">
			<td align="center">
				<xsl:variable name="currentBrowser" select="."/>
				<xsl:attribute name="title" select="$currentBrowser"/>
				<xsl:choose>
					<xsl:when test="$elementBrowsers/browser/@platform = .">
						<xsl:variable name="currentBrowserDescription" select="$elementBrowsers/browser[@platform = $currentBrowser]/description"/>
						<xsl:choose>
							<xsl:when test="contains($currentBrowserDescription, 'buggy')">
								<xsl:attribute name="class">comparison buggy</xsl:attribute>
							</xsl:when>
							<xsl:when test="string-length($currentBrowserDescription) &gt; 0">
								<xsl:attribute name="class">comparison incomplete</xsl:attribute>
							</xsl:when>
							<xsl:otherwise>
								<xsl:attribute name="class">comparison yes</xsl:attribute>
							</xsl:otherwise>
						</xsl:choose>
						<xsl:for-each select="$elementBrowsers/browser[@platform = $currentBrowser]">
							<xsl:value-of select="@version"/>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise><xsl:attribute name="class">comparison no</xsl:attribute>no</xsl:otherwise>
				</xsl:choose>
			</td>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
