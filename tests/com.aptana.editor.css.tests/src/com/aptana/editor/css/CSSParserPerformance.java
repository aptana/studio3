package com.aptana.editor.css;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.aptana.editor.css.parsing.CSSParser;
import com.aptana.editor.css.parsing.CSSScanner;

public class CSSParserPerformance {

    public static void main(String[] args) throws Exception {
        InputStream stream = CSSParserPerformance.class.getResourceAsStream("test.css");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = -1;
        while ((read = stream.read()) != -1) {
            out.write(read);
        }
        stream.close();
        String src = new String(out.toByteArray());
        CSSParser parser = new CSSParser();
        CSSScanner scanner = new CSSScanner();

        int numRuns = 100;
        long start = System.currentTimeMillis();
        for (int i = 0; i < numRuns; i++) {
            scanner.setSource(src);
            parser.parse(scanner);
        }
        long diff = System.currentTimeMillis() - start;
        System.out.println("Total time: " + diff + "ms");
        System.out.println("Average time: " + (diff / numRuns) + "ms");
    }
}
