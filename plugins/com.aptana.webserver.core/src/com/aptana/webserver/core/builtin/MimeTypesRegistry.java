/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2011 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.webserver.core.builtin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("nls")
public class MimeTypesRegistry {
	
	private final static String TEXT_HTML = "text/html"; //$NON-NLS-1$

	public static final MimeTypesRegistry INSTANCE = new MimeTypesRegistry();

	private Map<String, String> map = new HashMap<String, String>();

	/**
	 * 
	 */
	private MimeTypesRegistry() {
		map(TEXT_HTML, "html", "htm", "xhtml", "stm");
		map("image/bmp", "bmp");
		map("image/cis-cod", "cod");
		map("image/gif", "gif");
		map("image/ief", "ief");
		map("image/jpeg", "jpg", "jpeg", "jpe");
		map("image/pipeg", "jfif");
		map("image/png", "png");
		map("image/svg+xml", "svg");
		map("image/tiff", "tiff", "tif");
		map("image/x-cmu-raster", "ras");
		map("image/x-cmx", "cmx");
		map("image/x-icon", "ico");
		map("image/x-portable-anymap", "pnm");
		map("image/x-portable-bitmap", "pbm");
		map("image/x-portable-graymap", "pgm");
		map("image/x-portable-pixmap", "ppm");
		map("image/x-rgb", "rgb");
		map("image/x-xbitmap", "xbm");
		map("image/x-xpixmap", "xpm");
		map("image/x-xwindowdump", "xwd");
		map("text/css", "css");
		map("text/h323", "323");
		map("text/iuls", "uls");
		map("text/plain", "bas", "c", "h", "txt");
		map("text/richtext", "rtx");
		map("text/scriptlet", "sct");
		map("text/tab-separated-values", "tsv");
		map("text/webviewhtml", "htt");
		map("text/x-component", "htc");
		map("text/x-setext", "etx");
		map("text/x-vcard", "vcf");
		map("video/mpeg", "mp2", "mpa", "mpe", "mpeg", "mpg", "mpv2");
		map("video/quicktime", "mov", "qt");
		map("video/x-la-asf", "lsf", "lsx");
		map("video/x-ms-asf", "asf", "asr", "asx");
		map("video/x-msvideo", "avi");
		map("video/x-sgi-movie", "movie");
		map("audio/basic", "au", "snd");
		map("audio/mid", "mid", "rmi");
		map("audio/mpeg", "mp3");
		map("audio/x-aiff", "aif", "aifc", "aiff");
		map("audio/x-mpegurl", "m3u");
		map("audio/x-pn-realaudio", "ra", "ram");
		map("audio/x-wav", "wav");
		map("application/envoy", "evy");
		map("application/fractals", "fif");
		map("application/futuresplash", "spl");
		map("application/hta", "hta");
		map("application/internet-property-stream", "acx");
		map("application/mac-binhex40", "hqx");
		map("application/msword", "doc", "dot");
		map("application/octet-stream", "bin", "class", "dms", "exe", "lha", "lzh");
		map("application/oda", "oda");
		map("application/olescript", "axs");
		map("application/pdf", "pdf");
		map("application/pics-rules", "prf");
		map("application/pkcs10", "p10");
		map("application/pkix-crl", "crl");
		map("application/postscript", "ai", "eps", "ps");
		map("application/rtf", "rtf");
		map("application/set-payment-initiation", "setpay");
		map("application/set-registration-initiation", "setreg");
		map("application/vnd.ms-excel", "xla", "xlc", "xlm", "xls", "xlt", "xlw");
		map("application/vnd.ms-outlook", "msg");
		map("application/vnd.ms-pkicertstore", "sst");
		map("application/vnd.ms-pkiseccat", "cat");
		map("application/vnd.ms-pkistl", "stl");
		map("application/vnd.ms-powerpoint", "pot", "pps", "ppt");
		map("application/vnd.ms-project", "mpp");
		map("application/vnd.ms-works", "wcm", "wdb", "wks", "wps");
		map("application/winhlp", "hlp");
		map("application/x-bcpio", "bcpio");
		map("application/x-cdf", "cdf");
		map("application/x-compress", "z");
		map("application/x-compressed", "tgz");
		map("application/x-cpio", "cpio");
		map("application/x-csh", "csh");
		map("application/x-director", "dcr", "dir", "dxr");
		map("application/x-dvi", "dvi");
		map("application/x-gtar", "gtar");
		map("application/x-gzip", "gz");
		map("application/x-hdf", "hdf");
		map("application/x-internet-signup", "ins", "isp");
		map("application/x-iphone", "iii");
		map("application/x-javascript", "js");
		map("application/x-latex", "latex");
		map("application/x-msaccess", "mdb");
		map("application/x-mscardfile", "crd");
		map("application/x-msclip", "clp");
		map("application/x-msdownload", "dll");
		map("application/x-msmediaview", "m13", "m14", "mvb");
		map("application/x-msmetafile", "wmf");
		map("application/x-msmoney", "mny");
		map("application/x-mspublisher", "pub");
		map("application/x-msschedule", "scd");
		map("application/x-msterminal", "trm");
		map("application/x-mswrite", "wri");
		map("application/x-netcdf", "cdf");
		map("application/x-netcdf", "nc");
		map("application/x-perfmon", "pma", "pmc", "pml", "pmr", "pmw");
		map("application/x-pkcs12", "p12", "pfx");
		map("application/x-pkcs7-certificates", "p7b", "spc");
		map("application/x-pkcs7-certreqresp", "p7r");
		map("application/x-pkcs7-mime", "p7c", "p7m");
		map("application/x-pkcs7-signature", "p7s");
		map("application/x-sh", "sh");
		map("application/x-shar", "shar");
		map("application/x-shockwave-flash", "swf");
		map("application/x-stuffit", "sit");
		map("application/x-sv4cpio", "sv4cpio");
		map("application/x-sv4crc", "sv4crc");
		map("application/x-tar", "tar");
		map("application/x-tcl", "tcl");
		map("application/x-tex", "tex");
		map("application/x-texinfo", "texi", "texinfo");
		map("application/x-troff", "roff", "t", "tr");
		map("application/x-troff-man", "man");
		map("application/x-troff-me", "me");
		map("application/x-troff-ms", "ms");
		map("application/x-ustar", "ustar");
		map("application/x-wais-source", "src");
		map("application/x-x509-ca-cert", "cer", "crt", "der");
		map("application/ynd.ms-pkipko", "pko");
		map("application/zip", "zip");
	}
	
	private void map(String mimeType, String...extensions) {
		for (String extension : extensions) {
			map.put(extension, mimeType);
		}
	}

	/**
	 * Returns mime type for the given extension
	 * @param extension
	 * @return
	 */
	public String getMimeType(String extension) {
		String mimeType = map.get(extension);
		if (mimeType != null) {
			return mimeType;
		}
		return TEXT_HTML;
	}

}
