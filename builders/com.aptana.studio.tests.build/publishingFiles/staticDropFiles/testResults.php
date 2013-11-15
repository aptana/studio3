<html>
<head>

<?php
	$parts = explode("/", getcwd());
	$parts2 = explode("-", $parts[count($parts) - 1]);
	$buildName = $parts2[1];
	
	// Get build type names

	$fileHandle = fopen("../../dlconfig2.txt", "r");
	while (!feof($fileHandle)) {
		
		$aLine = fgets($fileHandle, 4096); // Length parameter only optional after 4.2.0
		$parts = explode(",", $aLine);
		$dropNames[trim($parts[0])] = trim($parts[1]);
 	}
	fclose($fileHandle);

	$buildType = $dropNames[$parts2[0]];

	echo "<title>Test Results for $buildType $buildName </title>";
?>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../default_style.css" type="text/css">
</head>
<body>
<p>&nbsp;</p>
<p><b><font face="Verdana" size="+1">Testing in progress. . .</font></b></p>
<p>&nbsp;</p>
</body>
</html>
