<html>
<head>
<?php
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

	echo "<title>Build Notes for $buildType $buildName </title>";
?>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../default_style.css" type="text/css">
</head>
<body>

<p><b><font face="Verdana" size="+3">Build Notes</font></b> </p>

<table border=0 cellspacing=5 cellpadding=2 width="100%" >
  <tr> 
    <td align=LEFT valign=TOP colspan="3" bgcolor="#0080C0"><b><font color="#FFFFFF" face="Arial,Helvetica">Eclipse 
      Build Notes for <?php echo "$buildType $buildName"; ?></font></b></td>
  </tr>
</table>
<table border="0">

<?php
	$hasNotes = false;
	$aDirectory = dir("buildnotes");
	$index = 0;
	while ($anEntry = $aDirectory->read()) {
		if (($anEntry != "." && $anEntry != "..")&& (! preg_match("/\.css/",$anEntry))) {
			$entries[$index] = $anEntry;
			$index++;
		}
	}
	
	aDirectory.closedir();
	sort($entries);

	for ($i = 0; $i < $index; $i++) {
		$anEntry = $entries[$i];
		$parts = explode("_", $anEntry);
		$baseName = $parts[1];
		$parts = explode(".", $baseName);
		$component = $parts[0];
		$line = "<td>Component: <a href=\"buildnotes/$anEntry\">$component</a></td>";
		echo "<tr>";
		echo "$line";
		echo "</tr>";
		$hasNotes = true;
	}
	
	if (!$hasNotes) {
		echo "<br>There are no build notes for this build.";
	}
?>

</table>
</body>
</html>
