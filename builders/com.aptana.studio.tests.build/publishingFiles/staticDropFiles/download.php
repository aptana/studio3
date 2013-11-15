<html>
<head>
<title>Eclipse Download Click Through</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../default_style.css" type="text/css">
<?php

		$servername=$_SERVER['SERVER_NAME'];
                                                                                                                       
		$script = $_SERVER['SCRIPT_NAME'];
		$patharray = pathinfo($_SERVER['SCRIPT_NAME']);
		$path = $patharray['dirname'];
		$buildLabel = array_pop(split("/",$path,-1));
		$qstring = $_SERVER['QUERY_STRING'];
	        $dropFile=array_pop(split("=",$qstring,-1));
         
		if ($qstring) {
		    $url = "http://$servername$script?$qstring";
		} else {
		    $url = "http://$servername$path$script";
		}

		$dropdir = explode("/", getcwd());
		$parts2 = explode("-", $dropdir[count($dropdir) - 1]);
                if ($parts2[1]) {
		    $buildName = $parts2[0] . "-" .$parts2[1];
                } else {
		    $buildName = $parts2[1];
                }

        $mirror=true;
        if (strstr($_SERVER['SERVER_NAME'],"eclipse.org")) {
#       if (strstr($_SERVER['SERVER_NAME'],"ibm.com")) {
        	$mirror=false;
        	$eclipselink="http://www.eclipse.org/downloads/download.php?file=/eclipse/downloads/drops/$buildLabel/$dropFile";
        } else {
        	$mirrorlink  = "http://$servername$path/$dropFile";
        }

		$clickFile = "clickThroughs/";
                $clickFileName = str_replace("-$buildName","",$dropFile);
		$clickFile = $clickFile.$clickFileName.".txt";

		if (file_exists($clickFile)) {
			$fileHandle = fopen($clickFile, "r");
		 		 while (!feof($fileHandle)) {
		 		 		 $aLine = fgets($fileHandle, 4096);
		 		 		 $result = $result.$aLine;
		 		 }
		 		 fclose($fileHandle);
		 	} else {
            	if ($mirror) {
                	echo '<META HTTP-EQUIV="Refresh" CONTENT="0;URL='.$dropFile.'">';		 
					echo '<b><font size "+4">Downloading: '.$mirrorlink.'</font></b>';
                } else {
                    echo '<META HTTP-EQUIV="Refresh" CONTENT="0;URL='.$eclipselink.'">';
		 		    echo '<b><font size "+4">Downloading: '.$eclipselink.'</font></b>';
                }
			echo '<BR>';
		 	echo '<BR>';
			if ($mirror) {
		 		echo 'If your download does not begin automatically click <a href='.$dropFile.'>here</a>.';
            } else {
		 	 	echo 'If your download does not begin automatically click <a href='.$eclipselink.'>here</a>.';
            }
		 }
?>
</head>

<body bgcolor="#FFFFFF" text="#000000">
  <?php
		if (file_exists($clickFile)) {
			echo '<p><b><font size="+4">Important Notes<BR>';
			echo '</font></b></font></p>
		 <p>It is very important to read the following notes in order to run this version 
		   of Eclipse. Once you have read the notes you can click on the Download link 
		   to download the drop.</p>
		 ';
		   echo '<textarea name="textfield" cols="80" rows="18" wrap="PHYSICAL">'.$result;
		   echo '</textarea>';
		   echo '<BR>';
		   echo '<BR>';
          
		if ($mirror) {     	
			echo '<a href="'.$dropFile.'">Download</a>';
		} else {
    	    echo '<a href="'.$eclipselink.'">Download</a>';
        }
	 }
?>
</body>
</html>
