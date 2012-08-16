/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.comp.run;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import beaver.Parser;
import beaver.Scanner;
import beaver.Symbol;
import beaver.comp.ParserGenerator;
import beaver.comp.io.SrcReader;
import beaver.comp.util.Log;
import beaver.spec.Grammar;
import beaver.spec.parser.GrammarParser.Terminals;
import beaver.spec.parser.GrammarScanner;

/**
 */
public class AntTask extends Task {
	private Options options = new Options();
	private File grammar_file;

	public void setFile(File file) {
		grammar_file = file;
	}

	public void setDestdir(File file) {
		options.dest_dir = file;
	}

	public void setExportTables(boolean opt) {
		options.exp_parsing_tables = opt;
	}

	public void setExportTerminals(boolean opt) {
		options.export_terminals = opt;
	}

	public void setSortTerminals(boolean opt) {
		options.sort_terminals = opt;
	}

	public void setReportActions(boolean opt) {
		options.report_actions = opt;
	}

	public void setCompress(boolean opt) {
		options.no_compression = !opt;
	}

	public void setTerminalNames(boolean opt) {
		options.terminal_names = opt;
	}

	public void setAnonymousActions(boolean opt) {
		options.name_action_classes = !opt;
	}

	public void setUseSwitch(boolean opt) {
		options.use_switch = opt;
	}

	public void execute() throws BuildException {
		if (!grammar_file.canRead())
			throw new BuildException("Cannot read grammar file " + grammar_file);

		if (options.dest_dir != null && !options.dest_dir.isDirectory())
			throw new IllegalArgumentException(options.dest_dir.getPath()
					+ " is not a directory.");

		SrcReader src = getSrcReader(grammar_file);
		try {
			if (existsCurrentOutput(getOutputFileName(src)))
				return;
		} catch (Exception e) {
			// Error(s) in source. Try to build anyway and compiler will print
			// erorr reports.
		}
		src.reset();
		Log log = new Log();
		compile(src, options, log);
		log.report(grammar_file.getName(), src);
	}

	private boolean existsCurrentOutput(String output_file_name) {
		String dir = grammar_file.getParent();
		File output_file = new File(dir, output_file_name
				+ ParserGenerator.SOURCE_FILE_EXT);
		if (!output_file.canRead()
				|| grammar_file.lastModified() > output_file.lastModified())
			return false;

		output_file = new File(dir, output_file_name
				+ ParserGenerator.SERIALIZED_PARSER_TABLES_FILE_EXT);
		return !options.exp_parsing_tables || output_file.canRead()
				&& grammar_file.lastModified() <= output_file.lastModified();
	}

	static private SrcReader getSrcReader(File file) throws BuildException {
		try {
			return new SrcReader(file);
		} catch (IOException e) {
			throw new BuildException("Failed to read grammar file " + file);
		}
	}

	static private String getOutputFileName(SrcReader src) throws IOException,
			Scanner.Exception {
		String output_file_name = src.file.getName();
		int dot_index = output_file_name.lastIndexOf('.');
		if (dot_index > 0) {
			output_file_name = output_file_name.substring(0, dot_index);
		}

		GrammarScanner scanner = new GrammarScanner(src);
		for (Symbol sym = scanner.nextToken(); sym.getId() != Terminals.EOF; sym = scanner
				.nextToken()) {
			if (sym.getId() == Terminals.CLASS) {
				if ((sym = scanner.nextToken()).getId() == Terminals.TEXT) {
					String class_name = (String) sym.value;
					if (class_name != null
							&& (class_name = class_name.trim()).length() > 0) {
						return class_name;
					}
				}
			}
		}
		return output_file_name;
	}

	static private void compile(SrcReader src, Options opt, Log log) {
		try {
			ParserGenerator.compile(src, opt, log);
		} catch (Parser.Exception e) {
			System.err.print("Error: ");
			System.err.println(e.getMessage());
		} catch (Grammar.Exception e) {
			System.err.print("Error: ");
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.print("System Error: ");
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
