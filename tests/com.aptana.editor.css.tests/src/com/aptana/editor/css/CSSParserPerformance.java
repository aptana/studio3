package com.aptana.editor.css;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.aptana.editor.css.parsing.CSSParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;

public class CSSParserPerformance {

    public static void main(String[] args) throws Exception {
        InputStream stream = CSSParserPerformance.class.getResourceAsStream("yui.css");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = -1;
        while ((read = stream.read()) != -1) {
            out.write(read);
        }
        stream.close();
        String src = new String(out.toByteArray());

        CSSParser parser = new CSSParser();
        IParseState parseState = new ParseState();
        int numRuns = 100;
        long start = System.currentTimeMillis();
        for (int i = 0; i < numRuns; i++) {
            parseState.setEditState(src, src, 0, 0);
            try {
                parser.parse(parseState);
            } catch (Exception e) {
            }
        }
        long diff = System.currentTimeMillis() - start;
        System.out.println("Total time: " + diff + "ms");
        System.out.println("Average time: " + (diff / numRuns) + "ms");
    }
}
