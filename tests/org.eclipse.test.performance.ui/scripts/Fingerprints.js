function toggleFingerprints() {
	var formSelect=document.forms[0].elements[0];
	var type=formSelect.selectedIndex;
	var idx=document.URL.indexOf("php?");
	if (idx==-1) {
		window.open(document.URL+"?fp_type="+type, "_self");
	} else {
		window.open(document.URL.substring(0,idx)+"php?fp_type="+type, "_self");
	}
}

function setFingerprintsType() {
	var idx=document.URL.indexOf("?");
	var type=0;
	if (idx != -1) {
		var typeStr=document.URL.substring(idx+1, document.URL.length);
		idx=typeStr.indexOf("=");
		if (idx != -1) {
			var ch=typeStr.substring(idx+1, idx+2)
			switch (ch) {
				case '1':
					type=1;
					break;
				case '2':
					type=2;
					break;
			}
		}
	}
	var formSelect=document.forms[0].elements[0];
	formSelect.selectedIndex=type;
}
