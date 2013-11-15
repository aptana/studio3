




<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Eclipse Build Drop</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
	<meta name="author" content="Eclipse Foundation, Inc." />
	<meta name="keywords" content="eclipse,project,plug-ins,plugins,java,ide,swt,refactoring,free java ide,tools,platform,open source,development environment,development,ide" />
	<link rel="stylesheet" type="text/css" href="../../../eclipse.org-common/stylesheets/visual.css" media="screen" />
	<link rel="stylesheet" type="text/css" href="../../../eclipse.org-common/stylesheets/layout.css" media="screen" />
	<link rel="stylesheet" type="text/css" href="../../../eclipse.org-common/stylesheets/print.css" media="print" />
	<script type="text/javascript">

sfHover = function() {
	var sfEls = document.getElementById("leftnav").getElementsByTagName("LI");
	for (var i=0; i<sfEls.length; i++) {
		sfEls[i].onmouseover=function() {
			this.className+=" sfhover";
		}
		sfEls[i].onmouseout=function() {
			this.className=this.className.replace(new RegExp(" sfhover\\b"), "");
		}
	}
}
if (window.attachEvent) window.attachEvent("onload", sfHover);
</script>
</head>
<body>

<div id="header">
	<a href="/"><img src="../../../eclipse.org-common/stylesheets/header_logo.gif" width="163" height="68" border="0" alt="Eclipse Logo" class="logo" /></a>
	<div id="searchbar">
		<img src="../../../eclipse.org-common/stylesheets/searchbar_transition.gif" width="92" height="26" class="transition" alt="" />
		<img src="../../../eclipse.org-common/stylesheets/searchbar_header.gif" width="64" height="17" class="header" alt="Search" />
		<form method="get" action="/search/search.cgi">
			<input type="hidden" name="t" value="All" />
			<input type="hidden" name="t" value="Doc" />
			<input type="hidden" name="t" value="Downloads" />
			<input type="hidden" name="t" value="Wiki" />
			<input type="hidden" name="wf" value="574a74" />
			<input type="text" name="q" value="" />
			<input type="image" class="button" src="../../../eclipse.org-common/stylesheets/searchbar_submit.gif" alt="Submit" onclick="this.submit();" />
		</form>
	</div>
	<ul id="headernav">
		<li class="first"><a href="/org/foundation/contact.php">Contact</a></li>
		<li><a href="/legal/">Legal</a></li>
	</ul>
</div><div id="topnav">
	<ul>
		<li><a>Downloads</a></li>
		<li class="tabstart">&#160;&#160;&#160;</li>
		<li><a class="" href="index.php" target="_self">All Platforms</a></li>
		<li class="tabstart">&#160;&#160;&#160;</li>
		<li><a class="" href="winPlatform.php" target="_self">Windows</a></li>
		<li class="tabstart">&#160;&#160;&#160;</li>
		<li><a class="" href="linPlatform.php" target="_self">Linux</a></li>
		<li class="tabstart">&#160;&#160;&#160;</li>
		<li><a class="" href="solPlatform.php" target="_self">Solaris</a></li>
		<li class="tabstart">&#160;&#160;&#160;</li>
		<li><a class="" href="aixPlatform.php" target="_self">AIX</a></li>
		<li class="tabstart">&#160;&#160;&#160;</li>		
		<li><a class="" href="macPlatform.php" target="_self">Macintosh</a></li>
		<li class="tabseparator">&#160;&#160;&#160;</li>
		<li><a class="" href="hpuxPlatform.php" target="_self">HP-UX</a></li>
		<li class="tabseparator">&#160;&#160;&#160;</li>				
	</ul>
</div>
<div id="topnavsep"></div>
<div id="leftcol">
<ul id="leftnav">
<li><a href="#EclipseSDK">Eclipse SDK</a></li>
<li><a href="#JUnitPlugin">JUnit Plugin Tests and Automated Testing Framework</a></li>
<li><a href="#ExamplePlugins">Example Plug-ins</a></li>
<li><a href="#RCPRuntime">RCP Runtime Binary</a></li>
<li><a href="#RCPSDK">RCP SDK</a></li>
<li><a href="#DeltaPack">Delta Pack</a></li>
<li><a href="#com.ibm.icu">com.ibm.icu.base Binary and Source Plug-ins</a></li>
<li><a href="#PlatformRuntime">Platform Runtime Binary</a></li>
<li><a href="#PlatformSDK">Platform SDK</a></li>
<li><a href="#JDTRuntime">JDT Runtime Binary</a></li>
<li><a href="#JDTSDK">JDT SDK</a></li>
<li><a href="#JDTCORE">JDT Core Batch Compiler</a></li>
<li><a href="#JARPROCESSOR">Jar Processor</a></li>
<li><a href="#PDERuntime">PDE Runtime Binary</a></li>
<li><a href="#PDESDK">PDE SDK</a></li>
<li><a href="#CVSRuntime">CVS Client Runtime Binary</a></li>
<li><a href="#CVSSDK">CVS Client SDK</a></li>
<li><a href="#SWT">SWT binary and Source</a></li>
<li><a href="#org.eclipse.releng">org.eclipse.releng.tools plug-in</a></li>
 
  </li>
  <li style="background-image: url(../../../eclipse.org-common/stylesheets/leftnav_fade.jpg); background-repeat: repeat-x; border-style: none;">
			<br /><br /><br /><br /><br />
  </li>
</ul>

</div>

&nbsp; <!--?php
	                                                                           
        $remoteName = gethostbyaddr($_SERVER['REMOTE_ADDR']);
                                    
        if (strstr($remoteName, "ibm.com")) {
                $jre = "http://w3.hursley.ibm.com/java/jim/";
        } else {
                $jre = "java-runtimes.html";
        }
		
	
?--> 
<div id="midcolumn">
		<table BORDER=0 CELLSPACING=5 CELLPADDING=2 WIDTH="100%" > 
		<tr> 
			<td> 
				<p><b><font face="Verdana" size="+2"> Download Details </font></b><br></td><tr><tr>
			</tr>
		</table>		
		<div class="homeitem3col">
			<h3>Details</h3>
				<ul class="midlist">
				<li><a name="EclipseSDK"> <b>Eclipse SDK</b> </a>
				<ul>
				<li>
				The Eclipse SDK includes the Eclipse Platform, Java development tools, and Plug-in Development 
				Environment, including source and both user and programmer documentation. If you 
				aren't sure which download you want... then you probably want this one.<br> <b>Eclipse 
				does not include a Java runtime environment (JRE).</b> You will need a 1.4.2 level 
				or higher Java runtime or Java development kit (JDK) installed on your machine 
				in order to run Eclipse. Click <a href="<?php echo "$jre";?>"><b>here</b></a> if you 
				need help finding a Java runtime.</li></ul>
				
				<li><a name="JUnitPlugin"> <b> JUnit Plugin Tests and Automated Testing Framework </b> </a>
				<ul>
				<li>
				These drops contain the framework and JUnit test plugins used to run JUnit 
				plug-in tests from the command line. Click <a href="automatedtesting.html"><b>here</b></a> 
				for more information and installation instructions. Includes both source code 
				and binary.</li></ul>
				
				<li><a name="ExamplePlugins"> <b> Example Plug-ins </b> </a>
				<ul>
				<li>
				To install the examples, download the p2 repository zip containing the examples into a directory on disk. Select <b>Help 
				-> Install New Software</b>.  Select <b>Add</b> to add a new software site. Select <b>Archive</b>
				and specify the location of the examples p2 repository zip and <b>Okay</b>.  You will be prompted
				to restart Eclipse to enable the new bundles.  For information on what the examples do and how to run them, 
				look in the &quot;Examples Guide&quot; section of the &quot;Platform Plug-in Developer 
				Guide&quot;, by selecting Help Contents from the Help menu, and choosing &quot;Platform 
				Plug-in Developer Guide&quot; book from the combo box.</li></ul>
				
				<li><a name="RCPRuntime"> <b> RCP Runtime Binary </b> </a>
				<ul>
				<li>
				This p2 repository contains the Eclipse Rich Client Platform base bundles and do not contain 
				source or programmer documentation. These downloads are meant to be used as target 
				platforms when developing RCP applications, and are not executable, stand-alone 
				applications.</li></ul>
				
				<li><a name="RCPSDK"> <b> RCP SDK </b> </a>
				<ul>
				<li>
				This p2 repository consists of the Eclipse Rich Client Platform base bundles and their source and the RCP 
				delta pack. 
				</li></ul>

				<li><a name="DeltaPack"> <b> Delta Pack </b> </a>
				<ul>
				<li>				
				The delta pack contains all the platform specific resources from the SDK and is used for cross-platform exports of RCP applications.</li></ul>
				</li></ul>
				
				<li><a name="com.ibm.icu"> <b> com.ibm.icu.base binary and source Plug-ins </b> </a>
				<ul>
				<li>
				</li></ul>
				
				<li><a name="PlatformRuntime"> <b> Platform Runtime Binary </b> </a>
				<ul>
				<li>
				These drops contain only the Eclipse Platform with user documentation and no source 
				and no programmer documentation. The Java development tools and Plug-in Development 
				Environment are NOT included. You can use these drops to help you package your 
				tool plug-ins for redistribution when you don't want to ship the entire SDK.</li></ul>
				
				<li><a name="PlatformSDK"> <b> Platform SDK </b> </a>
				<ul>
				<li>
				These drops contain the Eclipse Platform Runtime binary with associated source and programmer 
				documentation.</li></ul>

				<li><a name="JDTRuntime"> <b> JDT Runtime Binary </b> </a>
				<ul>
				<li>
				This p2 repository contains the Java development tools bundles only, with user documentation 
				and no source and no programmer documentation. The Eclipse platform and Plug-in 
				development environment are NOT included. You can combine this with the Platform 
				Runtime Binary if your tools rely on the JDT being present.</li></ul>

				<li><a name="JDTSDK"> <b> JDT SDK </b> </a>
				<ul>
				<li>
				This p2 repository contains the JDT Runtime binary with associated source and programmer 
				documentation.</li></ul>
				
				<li><a name="JDTCORE"> <b> JDT Core Batch Compiler </b> </a>
				<ul>
				<li>
				These drops contain the standalone batch java compiler, Ant compiler adapter and associated source. The batch compiler and Ant adapter (ecj.jar) are extracted from the org.eclipse.jdt.core plug-in as a 1.2MB download. For examples of usage, please refer to this help section: JDT Plug-in Developer Guide>Programmer's Guide>JDT Core>Compiling Java code.</li></ul>

				<li><a name="JARPROCESSOR"> <b> Jar Processor </b> </a>
				<ul>
				<li>
				These drops contain the standalone jar processor and associated source.  The jar processor is extracted from org.eclipse.update.core.  For details, see the	wiki pages for <a href="http://wiki.eclipse.org/index.php/Update_Site_Optimization">Update Site Optimization</a> and <a href="http://wiki.eclipse.org/index.php/Pack200#Jar_Processor">Pack200 Compression</a>.</li></ul>

				<li><a name="PDERuntime"> <b> PDE Runtime Binary </b> </a>
				<ul>
				<li>
				This p2 repository contains the Plug-in Development Enviroment bundles only, with user documentation. 
				The Eclipse platform and Java development tools are NOT included. You can combine 
				this with the Platform and JDT Runtime Binary or SDK if your tools rely on the 
				PDE being present.</li></ul>
				
				
				<li><a name="PDEProducts"> <b> PDE Build Products </b> </a>
				<ul>
				<li>The PDE Builders are self-contained, executable PDE Build configurations that can be used to build OSGi and Eclipse-based systems. 
				They can also be used as the basis for more sophisticated build systems that run tests, do API scans, publish builds etc.</li></ul>
				

				<li><a name="PDESDK"> <b> PDE SDK </b> </a>
				<ul>
				<li>
				These drops contain the PDE Runtime Binary with associated source.</li></ul>
				
				<li><a name="CVSRuntime"> <b> CVS Client Runtime Binary </b> </a>
				<ul>
				<li>
				This p2 repository contains the CVS Client plug-ins only. 
				The Eclipse platform, Java development, and Plug-in Development Environment tools are NOT included. You can combine 
				this with the Platform and JDT Runtime Binary or SDK if your tools rely on the 
				CVS client being present.</li></ul>

				<li><a name="CVSSDK"> <b> CVS Client SDK </b> </a>
				<ul>
				<li>
				This p2 repository contains the CVS Runtime Binary with associated source.</li></ul>		

				<li><a name="SWT"> <b> SWT Binary and Source </b> </a>
				<ul><li><p>These drops contain the SWT libraries and source for standalone SWT
				application development. For examples of standalone SWT applications
				refer to the <a href=" http://www.eclipse.org/swt/snippets/">snippets</a> 
				section of the SWT Component page.				
				</p><p>To run a standalone SWT application, add the swt jar(s) to the 
				classpath and add the directory/folder for the SWT JNI library to the
				java.library.path. For example, if you extract the download below to
				C:\SWT you would launch the HelloWorld application with the following command: 
				</p>
				<p>
				java -classpath C:\SWT\swt.jar;C:\MyApp\helloworld.jar 	-Djava.library.path=C:\SWT HelloWorld
				</p>
				<p>
				<b>Note that if you are running on Eclipse 3.3 or later</b>, you do not 
				need to specify the library path, so you would launch the HelloWorld
				application with the following command:
				</p>
				<p>
				java -classpath C:\SWT\swt.jar;C:\MyApp\helloworld.jar HelloWorld
				</p>
				<p>
				To run the standalone SWT examples that are shipped with Eclipse, download them
				from <a href="index.php#ExamplePlugins">here</a>. Then copy the file
				eclipse\plugins\org.eclipse.swt.examples_xxx\swtexamples.jar to C:\SWT.    
				Now you can run the examples that are described  
				<a href="http://www.eclipse.org/swt/examples.php">here</a>.  For example:
				</p>
				<p>
				cd C:\SWT<br>
				java -classpath swt.jar;swtexamples.jar
				org.eclipse.swt.examples.controlexample.ControlExample
				</p>
				<p>
				On Linux systems, note that the classpath separator character is a colon,
				so the equivalent command becomes:
				</p>
				<p>
				java -classpath swt.jar:swtexamples.jar
				org.eclipse.swt.examples.controlexample.ControlExample
				</p>
				</li></ul>

				<li><a name="org.eclipse.releng"> <b> org.eclipse.releng.tools plug-in </b> </a>
				<ul>
				<li>
				This plug-in provides features that will help with the Eclipse development process. Installing 
				the plug-in will add the following actions. To install simply unzip the file into 
				your plugins directory and restart Eclipse. <b>Please use the Release feature 
				of this plug-in to do your build submissions.</b></p><ol> <li><b>Release</b> to 
				the Team menu. This action will Tag selected projects with the specified version 
				<b>and</b> update the appropriate loaded *.map files with the version. The user 
				must have the *.map files loaded in their workspace and the use must commit the 
				map file changes to the repository when done.</li><li><b>Load Map Projects </b>to 
				the Team menu. Select one or more *.map file and this action will load the projects 
				listed in the *.map file into your workspace. Naturally the versions specified 
				in the *.map file will be loaded.</li><li><b>Tag Map Projects</b> to the Team 
				menu. Select one or more *Map files and this action will tag the projects listed 
				in the *Map files with a tag you specify.</li><li><b>Compare with Released</b> 
				to the Compare menu. Compare the selected projects with the versions referenced 
				in the currently loaded map files.</li><li><b>Replace with Released</b> to the 
				Replace menu. Replace the selected projects with the versions referenced in the 
				currently loaded map files.</li><li><b>Fix Copyright</b> to the Resource Perspective 
				Projects context menu. Select one or more projects in the Resource Perspective. 
				This action will sanity check the copyright notices in all the *.java and *.properties 
				files. Copyrights will be updated automatically where the tool deems appropriate. 
				A copyright.log file will be written to the workspace directory noting odd conflicts 
				that need to be looked at. You need to commit the changes yourself. This is the 
				tool that was used to do the 2.1 Copyright pass.</li></ol></li></ul>

				</ul>
		</div>

	<hr class="clearer" />
	</div>
<div id="rightcolumn">
		</br></br></br></br>
		<div class="sideitem">
			<h6>Eclipse SDK's</h6>
			<ul>
				<li><a href="index.php#EclipseSDK">All Platforms</a></li>
				<li><a href="winPlatform.php#EclipseSDK">Windows Platform</a></li>
				<li><a href="linPlatform.php#EclipseSDK">Linux Platform</a></li>
				<li><a href="solPlatform.php#EclipseSDK">Solaris Platform</a></li>
				<li><a href="aixPlatform.php#EclipseSDK">AIX Platform</a></li>				
				<li><a href="macPlatform.php#EclipseSDK">Macintosh Platform</a></li>
				<li><a href="hpuxPlatform.php#EclipseSDK">HP-UX Platform</a></li>
				<li><a href="sourceBuilds.php#EclipseSDK">Source Builds</a></li>
			</ul>
		</div>	
</div>		

<div id="footer">
	<ul id="footernav">
		<li class="first"><a href="/">Home</a></li>
		<li><a href="/legal/privacy.php">Privacy Policy</a></li>
		<li><a href="/legal/termsofuse.php">Terms of Use</a></li>
	</ul>
	<p>Copyright &copy; 2006 The Eclipse Foundation. All Rights
Reserved</p>
</div>
</body>
</html>

