<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<?php

function checkPlatform($line) {

   if (preg_match("/win32|linux|macosx/i", $line)) {
         return 1;
   } else {
		 return 0;
   }

}


function checkFile($p) {

     if ((is_file($p)) && (preg_match("/.txt/i", $p)))  {
         return 1; 
     } else {
         return 0;
     }

}


function listLogs($myDir) {

        $aDirectory = dir($myDir);
        $index = 0;
        $cdir = getcwd();
        while ($anEntry = $aDirectory->read()) {
            $path = $cdir . "/" . $myDir . "/" . $anEntry;
#            if ((is_file($path)) && (preg_match("/.txt/i", $path))) {
             $c = checkFile($path);
             if ($c == 1) {
                $entries[$index] = $anEntry;
                $index++;
            }
        }

        aDirectory.closedir();
        sort($entries);

        if ($index < 0) {
            echo "<br>There are no test logs for this build.";
            return;
        }
        for ($i = 0; $i < $index; $i++) {
            $anEntry = $entries[$i];
            $updateLine = 0;
            $updateLine = checkPlatform($anEntry);
		     if (($updateLine == 0) && (preg_match("/\//",$myDir))) {
                $linktext = $myDir . "_" . $anEntry;
                # remove the directory name from the link to the log
                $dir = substr(strrchr($linktext, "/"), 1);
            		 $line = "<td><a href=\"$myDir/$anEntry\">$dir</a></td>";
            } else {
		         $line = "<td><a href=\"$myDir/$anEntry\">$anEntry</a></td>";
            }
            echo "<li>$line</li>";
        }
}

function getBuildId() {
        $parts = explode("/", getcwd());
        $parts2 = explode("-", $parts[count($parts) - 1]);
        $buildName = $parts2[0] . "-" . $parts2[1];

        // Get build type names
        $fileHandle = fopen("../../dlconfig2.txt", "r");
        while (!feof($fileHandle)) {
                $aLine = fgets($fileHandle, 4096); // Length parameter only optional after 4.2.0
                $parts = explode(",", $aLine);
                $dropNames[trim($parts[0])] = trim($parts[1]);
        }
        fclose($fileHandle);

        $buildType = $dropNames[$parts2[0]];
    
        $buildId = $buildType.$buildName;

        return($buildId);
       
}




?>
<STYLE TYPE="text/css">
<!--
P {text-indent: 30pt;}
-->
</STYLE>


<title>Test Logs</title>
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
		 		  		 		  <li><a>Platform Navigation</a></li>
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
<li><a href="logs.php">Logs</a></li>
<li><a href="testResults.php#UnitTest">Unit Test Results</a></li>
<li><a href="testResults.php#PluginsErrors">Plugins Containing Compile Errors</a></li>
 
  </li>
  <li style="background-image: url(../../../eclipse.org-common/stylesheets/leftnav_fade.jpg); background-repeat: repeat-x; border-style: none;">
		 		  		 		  		 		  <br /><br /><br /><br /><br />
  </li>
</ul>

</div>

<div id="midcolumn">
<div class="homeitem3col">
<?php 
    global $buildId;
    $buildId = getBuildId(); 
    echo "<title>Test Results for $buildId </title>\n";

echo "<h3>Logs</h3>\n";

echo <<<END
<ul>
<li>
<strong><a name="chkpii" id="chkpii"></a>CHKPII Logs</strong>
These logs only need to be checked if the org.eclipse.releng.tests above report a test failures.
END;

echo"<ul>\n";
        listLogs("testresults/chkpii");
?>
</ul>
</li>

<li>
<strong><a name="javadoc" id="javadoc"></a>Javadoc Logs</strong>
<ul>

<?php
        listLogs("compilelogs");
?>
</ul>
</li>



<li>
<ul>
<strong><a name="console" id="console"></a>Console Logs</strong>
<p>These logs contain the console output captured while running the JUnit automated tests.</p>
<?php
        
        listLogs("testresults/consolelogs");
        echo "<strong>Individual linux.gtk.x86_6.0 test logs</strong>";
        listLogs("testresults/linux.gtk.x86_6.0");
        listLogs("testresults/consolelogs");
        echo "<strong>Individual win32.win32.x86_6.0 test logs</strong>";
        listLogs("testresults/win32.win32.x86_6.0");
        echo "<strong>Individual macosx.cocoa.x86_5.0 test logs</strong>";
        listLogs("testresults/macosx.cocoa.x86_5.0");
?>
</ul>
</li>
</div>
</br></br></br>
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
