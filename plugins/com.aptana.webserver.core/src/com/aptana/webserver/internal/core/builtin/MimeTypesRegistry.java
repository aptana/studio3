/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2011 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.webserver.internal.core.builtin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("nls")
public class MimeTypesRegistry
{

	private final static String TEXT_HTML = "text/html"; //$NON-NLS-1$

	public static final MimeTypesRegistry INSTANCE = new MimeTypesRegistry();

	private Map<String, String> map = new HashMap<String, String>();

	/**
	 * 
	 */
	private MimeTypesRegistry()
	{
		map(TEXT_HTML, "html", "htm", "xhtml", "stm");
		map("application/acad", "dwg");
		map("application/andrew-inset", "ez");
		map("application/atom+xml", "atom");
		map("application/clariscad", "ccad");
		map("application/drafting", "drw");
		map("application/dsptype", "tsp");
		map("application/dxf", "dxf");
		map("application/envoy", "evy");
		map("application/fractals", "fif");
		map("application/hta", "hta");
		map("application/i-deas", "unv");
		map("application/internet-property-stream", "acx");
		map("application/java-archive", "jar", "war", "ear");
		map("application/json", "json");
		map("application/mac-binhex40", "hqx");
		map("application/mac-compactpro", "cpt");
		map("application/msword", "doc", "dot");
		map("application/octet-stream", "bin", "class", "dms", "exe", "lha", "lzh", "dmg", "eot", "iso", "img", "msi",
				"msp", "msm");
		map("application/oda", "oda");
		map("application/ogg", "ogm");
		map("application/olescript", "axs");
		map("application/pdf", "pdf");
		map("application/pgp", "pgp");
		map("application/pics-rules", "prf");
		map("application/pkcs10", "p10");
		map("application/pkix-crl", "crl");
		map("application/postscript", "ai", "eps", "ps");
		map("application/pro_eng", "prt");
		map("application/rss+xml", "rss");
		map("application/rtf", "rtf");
		map("application/set", "set");
		map("application/set-payment-initiation", "setpay");
		map("application/set-registration-initiation", "setreg");
		map("application/SLA", "stl");
		map("application/smil", "smil");
		map("application/solids", "sol");
		map("application/STEP", "stp");
		map("application/vda", "vda");
		map("application/vnd.google-earth.kml+xml", "kml");
		map("application/vnd.google-earth.kmz", "kmz");
		map("application/vnd.ms-excel", "xla", "xlc", "xlm", "xls", "xlt", "xlw");
		map("application/vnd.ms-outlook", "msg");
		map("application/vnd.ms-pkicertstore", "sst");
		map("application/vnd.ms-pkiseccat", "cat");
		map("application/vnd.ms-powerpoint", "pot", "pps", "ppt", "ppz");
		map("application/vnd.ms-project", "mpp");
		map("application/vnd.ms-works", "wcm", "wdb", "wks", "wps");
		map("application/vnd.rim.cod", "cod");
		map("application/vnd.wap.wmlc", "wmlc");
		map("application/vnd.wap.xhtml+xml", "xhtml");
		map("application/winhlp", "hlp");
		map("application/x-7z-compressed", "7z");
		map("application/x-arj-compressed", "arj");
		map("application/x-bcpio", "bcpio");
		map("application/x-cdf", "cdf");
		map("application/x-cdlink", "vcd");
		map("application/x-chess-pgn", "pgn");
		map("application/x-cocoa", "cco");
		map("application/x-compress", "z");
		map("application/x-cpio", "cpio");
		map("application/x-csh", "csh");
		map("application/x-deb", "deb"); // http://wiki.debian.org/DebianPackage
		map("application/x-director", "dcr", "dir", "dxr");
		map("application/x-dvi", "dvi");
		map("application/x-freelance", "pre");
		map("application/x-futuresplash", "spl");
		map("application/x-gtar", "gtar");
		map("application/x-gzip", "gz");
		map("application/x-hdf", "hdf");
		map("application/x-internet-signup", "ins", "isp");
		map("application/x-iphone", "iii");
		map("application/x-ipix", "ipx");
		map("application/x-ipscript", "ips");
		map("application/x-java-archive-diff", "jardiff");
		map("application/x-java-jnlp-file", "jnlp");
		map("application/x-javascript", "js");
		map("application/x-koan", "skt");
		map("application/x-latex", "latex");
		map("application/x-lisp", "lsp");
		map("application/x-lotusscreencam", "scm");
		map("application/x-makeself", "run");
		map("application/x-mif", "mif");
		map("application/x-msaccess", "mdb");
		map("application/x-mscardfile", "crd");
		map("application/x-msclip", "clp");
		map("application/x-msdos-program", "com");
		map("application/x-msdownload", "dll");
		map("application/x-msmediaview", "m13", "m14", "mvb");
		map("application/x-msmetafile", "wmf");
		map("application/x-msmoney", "mny");
		map("application/x-mspublisher", "pub");
		map("application/x-msschedule", "scd");
		map("application/x-msterminal", "trm");
		map("application/x-mswrite", "wri");
		map("application/x-netcdf", "nc");
		map("application/x-perfmon", "pma", "pmc", "pml", "pmr", "pmw");
		map("application/x-perl", "pl", "pm");
		map("application/x-pilot", "prc", "pdb");
		map("application/x-pkcs12", "p12", "pfx");
		map("application/x-pkcs7-certificates", "p7b", "spc");
		map("application/x-pkcs7-certreqresp", "p7r");
		map("application/x-pkcs7-mime", "p7c", "p7m");
		map("application/x-pkcs7-signature", "p7s");
		map("application/x-rar-compressed", "rar");
		map("application/x-redhat-package-manager", "rpm");
		map("application/x-sea", "sea");
		map("application/x-sh", "sh");
		map("application/x-shar", "shar");
		map("application/x-shockwave-flash", "swf");
		map("application/x-stuffit", "sit");
		map("application/x-sv4cpio", "sv4cpio");
		map("application/x-sv4crc", "sv4crc");
		map("application/x-tar", "tar");
		map("application/x-tar-gz", "tgz");
		map("application/x-tcl", "tcl", "tk");
		map("application/x-tex", "tex");
		map("application/x-texinfo", "texi", "texinfo");
		map("application/x-troff", "roff", "t", "tr");
		map("application/x-troff-man", "man");
		map("application/x-troff-me", "me");
		map("application/x-troff-ms", "ms");
		map("application/x-ustar", "ustar");
		map("application/x-wais-source", "src");
		map("application/x-x509-ca-cert", "cer", "crt", "der", "pem");
		map("application/x-xpinstall", "xpi");
		map("application/ynd.ms-pkipko", "pko");
		map("application/zip", "zip");
		map("audio/basic", "au", "snd");
		map("audio/mid", "rmi");
		map("audio/midi", "mid", "midi", "kar");
		map("audio/mpeg", "mp3", "mpga");
		map("audio/TSP-audio", "tsi");
		map("audio/x-aiff", "aif", "aifc", "aiff");
		map("audio/x-mpegurl", "m3u");
		map("audio/x-ms-wax", "wax");
		map("audio/x-ms-wma", "wma");
		map("audio/x-pn-realaudio", "ra", "ram", "rm");
		map("audio/x-wav", "wav");
		map("chemical/x-pdb", "xyz");
		map("image/gif", "gif");
		map("image/ief", "ief");
		map("image/jpeg", "jpg", "jpeg", "jpe");
		map("image/pipeg", "jfif");
		map("image/png", "png");
		map("image/svg+xml", "svg");
		map("image/tiff", "tif", "tiff");
		map("image/vnd.wap.wbmp", "wbmp");
		map("image/x-cmu-raster", "ras");
		map("image/x-cmx", "cmx");
		map("image/x-icon", "ico");
		map("image/x-jng", "jng");
		map("image/x-ms-bmp", "bmp");
		map("image/x-portable-anymap", "pnm");
		map("image/x-portable-bitmap", "pbm");
		map("image/x-portable-graymap", "pgm");
		map("image/x-portable-pixmap", "ppm");
		map("image/x-rgb", "rgb");
		map("image/x-xpixmap", "xpm");
		map("image/x-xwindowdump", "xwd");
		map("model/iges", "igs");
		map("model/mesh", "silo");
		map("model/vrml", "wrl");
		map("text/cache-manifest", "manifest");
		map("text/css", "css");
		map("text/css", "less");
		map("text/h323", "323");
		map("text/iuls", "uls");
		map("text/mathml", "mml");
		map("text/plain", "bas", "c", "h", "txt", "m");
		map("text/richtext", "rtx");
		map("text/scriptlet", "sct");
		map("text/sgml", "sgml");
		map("text/tab-separated-values", "tsv");
		map("text/vnd.sun.j2me.app-descriptor", "jad");
		map("text/vnd.wap.wml", "wml");
		map("text/webviewhtml", "htt");
		map("text/x-component", "htc");
		map("text/x-setext", "etx");
		map("text/x-vcard", "vcf");
		map("application/xml", "xml");
		map("video/3gpp", "3gpp", "3gp");
		map("video/dl", "dl");
		map("video/gl", "gl");
		map("video/mp4", "mp4", "m4v");
		map("video/mpeg", "mp2", "mpa", "mpe", "mpeg", "mpg", "mpv2");
		map("video/ogg", "ogg", "ogv");
		map("video/quicktime", "mov", "qt");
		map("video/vnd.vivo", "vivo");
		map("video/webm", "webm");
		map("video/x-fli", "fli");
		map("video/x-flv", "flv");
		map("video/x-la-asf", "lsf", "lsx");
		map("video/x-mng", "mng");
		map("video/x-ms-asf", "asf", "asr");
		map("video/x-ms-asx", "asx");
		map("video/x-ms-wmx", "wmx");
		map("video/x-ms-wvx", "wvx");
		map("video/x-msvideo", "avi");
		map("video/x-sgi-movie", "movie");
		map("www/mime", "mime");
		map("x-conference/x-cooltalk", "ice");
		map("x-world/x-vrml", "vrm");
		map("zip", "application/zip");
	}

	private void map(String mimeType, String... extensions)
	{
		for (String extension : extensions)
		{
			map.put(extension, mimeType);
		}
	}

	/**
	 * Returns mime type for the given extension
	 * 
	 * @param extension
	 * @return
	 */
	public String getMimeType(String extension)
	{
		String mimeType = map.get(extension);
		if (mimeType != null)
		{
			return mimeType;
		}
		return TEXT_HTML;
	}

}
