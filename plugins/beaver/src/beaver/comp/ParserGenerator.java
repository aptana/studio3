/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.comp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.DeflaterOutputStream;

import beaver.Parser;
import beaver.comp.io.SrcReader;
import beaver.comp.run.Options;
import beaver.comp.util.BitSet;
import beaver.comp.util.Log;
import beaver.spec.Grammar;
import beaver.spec.GrammarBuilder;
import beaver.spec.NonTerminal;
import beaver.spec.Production;
import beaver.spec.Terminal;
import beaver.spec.ast.GrammarTreeRoot;
import beaver.spec.parser.GrammarParser;
import beaver.spec.parser.GrammarScanner;

/**
 * This class provides parser generation driving routines. In a way it is a driver, where only top level calls are left
 * for the Main application class.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ParserGenerator
{
	static public final String VERSION = "0.9.6.1";
	static public final String SOURCE_FILE_EXT = ".java";
	static public final String SERIALIZED_PARSER_TABLES_FILE_EXT = ".spec";
	static public final String PARSER_ACTIONS_REPORT_FILE_EXT = ".stat";

	static public class CompiledParser
	{
		/**
		 * A table with production descriptions. It contains information about every rule that is used during the reduce.
		 * <p/>Each slot in this table is a "structure":
		 *
		 * <pre>
		 * short lhs_symbol_id; // Symbol on the left-hand side of the production
		 * short rhs_length;    // Number of right-hand side symbols in the production
		 *
		 * </pre>
		 *
		 * where lhs_symbol_id uses higher 16 bit, and rhs_length - lower 16 bit
		 */
		static private int[] makeProductionDescriptors(Grammar grammar)
		{
			int[] rules = new int[grammar.rules.length];
			for (int i = 0; i < grammar.rules.length; i++)
			{
				Production rule = grammar.rules[i];
				rules[i] = rule.lhs.id << 16 | rule.rhs.items.length;
			}
			return rules;
		}

		static private final Comparator TERMINAL_NAME_CMP = new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				Terminal term1 = (Terminal) o1;
				Terminal term2 = (Terminal) o2;

				return term1.id == 0 ? -1 : term1.name.compareTo(term2.name);
			}
		};

		static private void writeTerminalsClass(Grammar grammar, Options opts, String indent, Writer out) throws IOException
		{
			out.write(indent);
			if (indent.length() > 0)
				out.write("static ");
			out.write("public class Terminals {\n");

			Terminal[] terms;
			if (opts.sort_terminals)
			{
				terms = new Terminal[grammar.terminals.length];
				System.arraycopy(grammar.terminals, 0, terms, 0, terms.length);
				Arrays.sort(terms, TERMINAL_NAME_CMP);
			}
			else
			{
				terms = grammar.terminals;
			}

			for (int i = 0; i < terms.length; i++)
			{
                if (terms[i].name.charAt(0) == '$')
                    continue;
				out.write(indent);
				out.write("\tstatic public final short ");
				out.write(terms[i].name);
				out.write(" = ");
				out.write(String.valueOf(terms[i].id));
				out.write(";\n");
			}
			if (opts.terminal_names)
			{
				terms = grammar.terminals;
				out.write('\n');
				out.write(indent);
				out.write("\tstatic public final String[] NAMES = {\n");
				for (int i = 0; i < terms.length - 1; i++)
				{
                    if (terms[i].name.charAt(0) == '$')
                        continue;
					out.write(indent);
					out.write("\t\t\"");
					out.write(terms[i].name);
					out.write("\",\n");
				}
				if (terms.length > 0 && terms[terms.length - 1].name.charAt(0) != '$')
				{
					out.write(indent);
					out.write("\t\t\"");
					out.write(terms[terms.length - 1].name);
					out.write("\"\n");
				}
				out.write(indent);
				out.write("\t};\n");
			}
			out.write(indent);
			out.write("}\n");
		}

        static private boolean writeMarkersClass(Terminal[] terms, Writer out) throws IOException
        {
            boolean header_is_out = false;

            for (int i = 0; i < terms.length; i++)
            {
                if (terms[i].name.charAt(0) == '$')
                {
                    if (!header_is_out)
                    {
                        out.write("\tstatic public class AltGoals {\n");
                        header_is_out = true;
                    }
                    out.write("\t\tstatic public final short ");
                    out.write(terms[i].name.substring(1));
                    out.write(" = ");
                    out.write(String.valueOf(terms[i].id));
                    out.write(";\n");
                }
            }
            if (header_is_out)
            {
                out.write("\t}\n");
            }
            return header_is_out;
        }
        
		static private void writeReduceActionClasses(Grammar grammar, Writer out) throws IOException
		{
			for (int i = 0; i < grammar.rules.length; i++)
			{
				Production rule = grammar.rules[i];
				if (rule.code == null)
					continue;

				out.write("\n\t/**");
				out.write("\n\t * ");
				out.write(rule.toString());
				out.write("\n\t */");
				out.write("\n\tfinal class Action");
				out.write(String.valueOf(rule.id));
				out.write(" extends Action {\n");
				out.write("\t\t\t\tpublic Symbol reduce(Symbol[] _symbols, int offset) {\n");
				writeReduceActionCode(rule, out);
				out.write("\t\t\t\t}");
				out.write("\n\t}\n");
			}
		}

		static private void writeStaticReturns(Grammar grammar, Writer out) throws IOException
		{
			BitSet ret_elems = new BitSet();
			for (int i = 0; i < grammar.rules.length; i++)
			{
				Production rule = grammar.rules[i];
				if (rule.code == null && rule.rhs.size() > 1)
				{
					int n = indexOfLastReferencedSymbol(rule.rhs);
                    if (n == 0)
                        continue;
                    if (n < 0)
                        n = rule.rhs.size() - 1;
					if (ret_elems.add(n))
					{
						out.write("\n\tstatic final Action RETURN");
						out.write(String.valueOf(n + 1));
						out.write(" = new Action() {\n");
						out.write("\t\tpublic Symbol reduce(Symbol[] _symbols, int offset) {\n");
						out.write("\t\t\treturn _symbols[offset + ");
						out.write(String.valueOf(n + 1));
						out.write("];\n");
						out.write("\t\t}\n");
						out.write("\t};\n");
					}
				}
			}			
		}
		
		static private int countReferencedSymbols(Production.RHS rhs)
		{
			int c = 0;
			for (int i = 0; i < rhs.items.length; i++)
			{
				if (rhs.items[i].alias != null) c++;
			}
			return c;
		}

		static private int indexOfLastReferencedSymbol(Production.RHS rhs)
		{
            int i = rhs.size();
			while (--i >= 0 && rhs.items[i].alias == null)
                ;
			return i;
		}

		static private void writeParserActionsArray(Grammar grammar, Options opts, Writer out) throws IOException
		{
			for (int i = 0, last_i = grammar.rules.length - 1; i < grammar.rules.length; i++)
			{
				Production rule = grammar.rules[i];
				out.write("\n\t\t\t");
				if (rule.code == null)
				{
					if (rule.rhs.size() == 0)
					{
						out.write("Action.NONE");
						if (i != last_i) out.write(",  ");
						out.write("\t// [");
						out.write(String.valueOf(rule.id));
						out.write("] ");
						out.write(rule.toString());
					}
					else if (rule.rhs.size() == 1)
					{
						out.write("Action.RETURN");
						if (i != last_i) out.write(',');
						out.write("\t// [");
						out.write(String.valueOf(rule.id));
						out.write("] ");
						out.write(rule.toString());
					}
					else
					{
						int n = indexOfLastReferencedSymbol(rule.rhs);
						if (n == 0)
						{
							out.write("Action.RETURN");
						}
						else if (n > 0)
						{
							out.write("RETURN");
							out.write(String.valueOf(n + 1));
						}
                        else
                        {
                            out.write("RETURN");
                            out.write(String.valueOf(rule.rhs.size()));
                        }
						if (i != last_i) out.write(',');
						out.write("\t// [");
						out.write(String.valueOf(rule.id));
						out.write("] ");
						out.write(rule.toString());

						if (n < 0)
						{
							out.write("; returns '");
							out.write(rule.rhs.items[rule.rhs.size() - 1].symbol.name);
							out.write("' although none is marked");
						}
						else if (countReferencedSymbols(rule.rhs) > 1)
						{
							out.write("; returns '");
							out.write(rule.rhs.items[n].alias);
							out.write("' although more are marked");
						}
					}
				}
				else
				{
					out.write("new Action");
					if (opts.name_action_classes)
					{
						out.write(String.valueOf(rule.id));
						out.write("()");
						if (i != last_i) out.write(',');
						out.write("\t// [");
						out.write(String.valueOf(rule.id));
						out.write("] ");
						out.write(rule.toString());
					}
					else
					{
						out.write("() {\t// [");
						out.write(String.valueOf(rule.id));
						out.write("] ");
						out.write(rule.toString());
						out.write('\n');
						out.write("\t\t\t\tpublic Symbol reduce(Symbol[] _symbols, int offset) {\n");
						writeReduceActionCode(rule, out);
						out.write("\t\t\t\t}");
						out.write("\n\t\t\t}");
						if (i != last_i) out.write(',');
					}
				}
			}
		}

		static private void writeParserActionsSwitch(Grammar grammar, Options opts, Writer out) throws IOException
		{
			out.write("\t\tswitch(rule_num) {\n");
            
            int n = grammar.rules.length;
            Production[] rules = new Production[n];
            System.arraycopy(grammar.rules, 0, rules, 0, n);
            
			for (int i = 0; i < rules.length; i++)
			{
                if (rules[i].code != null)
                {
    				out.write("\t\t\tcase ");
    				out.write(String.valueOf(rules[i].id));
    				out.write(": // ");
    				out.write(rules[i].toString());
    				out.write("\n\t\t\t{\n");
					writeReduceActionCode(rules[i], out);
                    out.write("\t\t\t}\n");
                    
                    rules[i] = null;
                    n--;
				}
            }
            for (int w = 0; n > 0; w++)
            {
                int cnt = 0;
                for (int i = 0; i < rules.length; i++)
                {
                    if (rules[i] != null)
                    {
                        int ref_off = indexOfLastReferencedSymbol(rules[i].rhs) + 1;
                        if (ref_off == 0)
                        {
                            ref_off = rules[i].rhs.size();
                        }
                        
                        if (ref_off == w)
                        {
                            out.write("\t\t\tcase ");
                            out.write(String.valueOf(rules[i].id));
                            out.write(": // ");
                            out.write(rules[i].toString());
                            out.write("\n");
    
                            rules[i] = null;
                            n--;
                            cnt++;
                        }
                    }
                }
                if (cnt > 0)
                {
                    out.write("\t\t\t{\n");
                    if (w == 0)
                    {
                        out.write("\t\t\t\treturn new Symbol(null);\n");
                    }
                    else
                    {
                        out.write("\t\t\t\treturn _symbols[offset + ");
                        out.write(String.valueOf(w));
                        out.write("];\n");
                    }
                    out.write("\t\t\t}\n");
                }
            }
            out.write("\t\t\tdefault:\n");
            out.write("\t\t\t\tthrow new IllegalArgumentException(\"unknown production #\" + rule_num);\n");
            out.write("\t\t}\n");
		}

		private static void writeReduceActionCode(Production rule, Writer out) throws IOException
		{
			for (int i = 0; i < rule.rhs.items.length; i++)
			{
				Production.RHS.Item rhs_item = rule.rhs.items[i];
				if (rhs_item.alias != null)
				{
					out.write("\t\t\t\t\t");
					String type = rhs_item.symbol.type;
					if (type == null)
					{
						out.write("final Symbol ");
						out.write(rhs_item.alias);
						out.write(" = _symbols[offset + ");
						out.write(String.valueOf(i + 1));
						out.write("];\n");
					}
					else
					{
						out.write("final Symbol _symbol_");
						out.write(rhs_item.alias);
						out.write(" = _symbols[offset + ");
						out.write(String.valueOf(i + 1));
						out.write("];\n");

						if (type.charAt(0) == '+')
						{
							type = type.substring(1);
							out.write("\t\t\t\t\tfinal ");
							out.write(Grammar.EBNF_LIST_TYPE_NAME);
							out.write(" _list_");
							out.write(rhs_item.alias);
							out.write(" = (");
							out.write(Grammar.EBNF_LIST_TYPE_NAME);
							out.write(") _symbol_");
							out.write(rhs_item.alias);
							out.write(".value;\n");

							out.write("\t\t\t\t\tfinal ");
							out.write(type);
							out.write("[] ");
							out.write(rhs_item.alias);
							out.write(" = _list_");
							out.write(rhs_item.alias);
							out.write(" == null ? new ");
							out.write(type);
							out.write("[0] : (");
							out.write(type);
							out.write("[]) _list_");
							out.write(rhs_item.alias);
							out.write(".toArray(new ");
							out.write(type);
							out.write("[_list_");
							out.write(rhs_item.alias);
							out.write(".size()]);\n");
						}
						else
						{
							out.write("\t\t\t\t\tfinal ");
							out.write(type);
							out.write(' ');
							out.write(rhs_item.alias);
							out.write(" = (");
							out.write(type);
							out.write(") _symbol_");
							out.write(rhs_item.alias);
							out.write(".value;\n");
						}
					}
				}
			}
			out.write("\t\t\t\t\t");
			out.write(rule.code);
			out.write('\n');
		}

		static private ByteArrayOutputStream serializeParsingTables(ParsingTables tables, int[] rule_descr, NonTerminal error) throws IOException
		{
			ByteArrayOutputStream bytes_stream = new ByteArrayOutputStream(16384);
			DataOutputStream data_stream = new DataOutputStream(new DeflaterOutputStream(bytes_stream));

			tables.writeTo(data_stream);

			data_stream.writeInt(rule_descr.length);
			for (int i = 0; i < rule_descr.length; i++)
			{
				data_stream.writeInt(rule_descr[i]);
			}
			data_stream.writeShort(error.id);
			data_stream.close();
			return bytes_stream;
		}

		static private String encode(byte[] bytes)
		{
			final StringBuffer text = new StringBuffer((bytes.length * 4 + 2) / 3);
			int i = 0, end = bytes.length - bytes.length % 3;
			while (i < end)
			{
				int b1 = bytes[i++] & 0xFF, b2 = bytes[i++] & 0xFF, b3 = bytes[i++] & 0xFF;
				encode(b1 >> 2, text);
				encode((b1 << 4 & 0x30) | b2 >> 4, text);
				encode((b2 << 2 & 0x3C) | b3 >> 6, text);
				encode(b3 & 0x3F, text);
			}
			if (i < bytes.length)
			{
				int b1 = bytes[i++] & 0xFF;
				if (i < bytes.length)
				{
					int b2 = bytes[i] & 0xFF;
					encode(b1 >> 2, text);
					encode((b1 << 4 & 0x30) | b2 >> 4, text);
					encode((b2 << 2 & 0x3C), text);
					text.append('=');
				}
				else
				{
					encode(b1 >> 2, text);
					encode(b1 << 4 & 0x30, text);
					text.append('=');
					text.append('=');
				}
			}
			return text.toString();
		}

		static private final char[] _62_or_63 = { '#', '$' };

		static private void encode(int c, StringBuffer text)
		{
			if (c < 10)
				text.append((char)('0' + c));
			else if (c < 36)
				text.append((char)('A' - 10 + c));
			else if (c < 62)
				text.append((char)('a' - 36 + c));
			else
				text.append(_62_or_63[c - 62]);
		}

		private Grammar grammar;
		private ParsingTables tables;
		private int[] rule_descr;

		CompiledParser(Grammar grammar, ParsingTables parsing_tables)
		{
			this.grammar = grammar;
			this.tables = parsing_tables;
			this.rule_descr = makeProductionDescriptors(grammar);
		}

		public void writeActionsReport(File dir, String output_file_name) throws IOException
		{
			FileWriter out = new FileWriter(new File(dir, output_file_name + PARSER_ACTIONS_REPORT_FILE_EXT));
			try
			{
				out.write("// This file was generated by Beaver v");
				out.write(VERSION);
				out.write("\n\n");
				for (State state = tables.first_state; state != null; state = state.next)
				{
					out.write(String.valueOf(state.id));
					out.write(':');
					for (Action act = state.terminal_lookahead_actions.first; act != null; act = act.next)
					{
						out.write('\t');
						out.write(act.toString());
						out.write('\n');
					}
					for (Action act = state.nonterminal_lookahead_actions.first; act != null; act = act.next)
					{
						out.write('\t');
						out.write(act.toString());
						out.write('\n');
					}
					if (state.default_action != null)
					{
						out.write('\t');
						out.write(state.default_action.toString());
						out.write('\n');
					}
				}
			}
			finally
			{
				out.close();
			}
		}

		/**
		 * Writes a Java class that is a parser from the user point of view. Actual implementation though simply extends
		 * LALR parser implementation from Page and supplies action implementations for all productions and "enum" for all
		 * terminal symbols.
		 *
		 * @throws IOException
		 *             when writing to a file fails
		 */
		public void writeParserSource(File src_file, File dir, String class_name, Options opts) throws IOException
		{
			FileWriter out = new FileWriter(new File(dir, class_name + SOURCE_FILE_EXT));
			try
			{
				if (grammar.prolog != null)
				{
					out.write(grammar.prolog);
					out.write('\n');
				}
				if (grammar.package_name != null)
				{
					out.write("package ");
					out.write(grammar.package_name);
					out.write(";\n\n");
				}
				for (int i = 0; i < grammar.imports.length; i++)
				{
					out.write("import ");
					out.write(grammar.imports[i]);
					out.write(";\n");
				}
				out.write('\n');
				out.write("/**\n");
				out.write(" * This class is a LALR parser generated by\n");
				out.write(" * <a href=\"http://beaver.sourceforge.net\">Beaver</a> v");
				out.write(VERSION);
				out.write('\n');
				out.write(" * from the grammar specification \"");
				out.write(src_file.getName());
				out.write("\".\n");
				out.write(" */\n");
				writeClass(class_name, opts, out);
			}
			finally
			{
				out.close();
			}
		}

		public void writeTerminalsSource(File src_file, File dir, String output_file_name, Options opts) throws IOException
		{
			FileWriter out = new FileWriter(new File(dir, output_file_name + SOURCE_FILE_EXT));
			try
			{
				if (grammar.package_name != null)
				{
					out.write("package ");
					out.write(grammar.package_name);
					out.write(";\n\n");
				}
				out.write("/**\n");
				out.write(" * This class lists terminals used by the\n");
				out.write(" * grammar specified in \"");
				out.write(src_file.getName());
				out.write("\".\n");
				out.write(" */\n");
				writeTerminalsClass(grammar, opts, "", out);
			}
			finally
			{
				out.close();
			}
		}

		public void writeParsingTables(File dir, String output_file_name) throws IOException
		{
			FileOutputStream out = new FileOutputStream(new File(dir, output_file_name + SERIALIZED_PARSER_TABLES_FILE_EXT));
			try
			{
				serializeParsingTables(tables, rule_descr, grammar.error).writeTo(out);
			}
			finally
			{
				out.close();
			}
		}

		private void writeClass(String class_name, Options opts, Writer out) throws IOException
		{
			out.write("@SuppressWarnings({ \"unchecked\", \"rawtypes\" })\n");
			out.write("public class ");
			out.write(class_name);
			out.write(" extends ");
			if (class_name.equals("Parser"))
				out.write("beaver.");
			if (grammar.impls != null && grammar.impls.length > 0)
			{
				out.write("Parser implements ");
				for (int i = 0; i < grammar.impls.length; i++)
				{
					String impl = grammar.impls[i];
					
					if (i > 0)
						out.write(",");
					
					out.write(impl);
				}
				out.write(" {\n");
			}
			else
			{
				out.write("Parser {\n");
			}

			if (!opts.export_terminals)
			{
				writeTerminalsClass(grammar, opts, "\t", out);
			}
            writeMarkersClass(grammar.terminals, out);
            
            out.write("\n\tstatic final ParsingTables PARSING_TABLES = new ParsingTables(");
            if (opts.exp_parsing_tables)
            {
                out.write(class_name);
                out.write(".class");
            }
            else
            {
                String enc = encodeParsingTables();

                out.write('\n');
                int from = 0;
                int dlen = enc.length() != 71 ? 71 : 73;
                for (int to = dlen; to < enc.length(); from = to, to += dlen)
                {
                    out.write("\t\t\"");
                    out.write(enc.substring(from, to));
                    out.write("\" +\n");
                }
                out.write("\t\t\"");
                out.write(enc.substring(from));
                out.write('"');
            }
            out.write(");\n");

			if (!opts.use_switch)
			{
				if (opts.name_action_classes)
					writeReduceActionClasses(grammar, out);
				writeStaticReturns(grammar, out);
			}
			if (grammar.class_code != null)
			{
				out.write(grammar.class_code);
				out.write('\n');
			}
			if (!opts.use_switch)
			{
				out.write('\n');
				out.write("\tprivate final Action[] actions;\n");
			}
			out.write('\n');
            
			out.write("\tpublic ");
			out.write(class_name);
			out.write("() {\n");
			out.write("\t\tsuper(PARSING_TABLES);\n");
			if (!opts.use_switch)
			{
				out.write("\t\tactions = new Action[] {");
				writeParserActionsArray(grammar, opts, out);
				out.write("\n\t\t};\n");
			}
			if (grammar.init_code != null)
			{
				out.write('\n');
				out.write(grammar.init_code);
				out.write('\n');
			}
			out.write("\t}\n");
			out.write('\n');
			out.write("\tprotected Symbol invokeReduceAction(int rule_num, int offset) {\n");
			if (opts.use_switch)
			{
				writeParserActionsSwitch(grammar, opts, out);
			}
			else
			{
				out.write("\t\treturn actions[rule_num].reduce(_symbols, offset);\n");
			}
			out.write("\t}\n");
			out.write("}\n");
		}

		private String encodeParsingTables() throws IOException
		{
			return encode(serializeParsingTables(tables, rule_descr, grammar.error).toByteArray());
		}
	}

	static public void compile(SrcReader src, Options opt, Log log) throws IOException, Parser.Exception, Grammar.Exception
	{
		Grammar grammar = ParserGenerator.parseGrammar(src, log);
		if (!log.hasErrors())
		{
			ParserGenerator.CompiledParser parser = ParserGenerator.compile(grammar, opt, log);
			if (!log.hasErrors())
			{
				File dir = src.file.getParentFile();
				if (opt.dest_dir != null)
				{
					dir = opt.dest_dir;
					if (grammar.package_name != null)
					{
						dir = new File(dir, grammar.package_name.replace('.', File.separatorChar));
						if (!dir.exists())
						{
							dir.mkdirs();
						}
					}
				}	
				String output_file_name = ParserGenerator.getOutputFileName(grammar, src.file);
				if (opt.report_actions)
				{
					parser.writeActionsReport(dir, output_file_name);
					log.message("Generated: " + output_file_name + PARSER_ACTIONS_REPORT_FILE_EXT);
				}
				if (!opt.no_output)
				{
					parser.writeParserSource(src.file, dir, output_file_name, opt);
					log.message("Generated: " + output_file_name + SOURCE_FILE_EXT);
					if (opt.export_terminals)
					{
						parser.writeTerminalsSource(src.file, dir, "Terminals", opt);
						log.message("Generated: " + "Terminals" + SOURCE_FILE_EXT);
					}
					if (opt.exp_parsing_tables)
					{
						parser.writeParsingTables(dir, output_file_name);
						log.message("Generated: " + output_file_name + SERIALIZED_PARSER_TABLES_FILE_EXT);
					}
				}
			}
		}
	}

	static public Grammar parseGrammar(SrcReader reader, Log log) throws IOException, Parser.Exception, Grammar.Exception
	{
		GrammarTreeRoot root = (GrammarTreeRoot) new GrammarParser(log).parse(new GrammarScanner(reader));
		if (log.hasErrors())
			throw new Grammar.Exception("cannot parse grammar");
		GrammarBuilder maker = new GrammarBuilder(log);
		root.accept(maker);
		return maker.getGrammar();
	}

	static public ParserGenerator.CompiledParser compile(Grammar grammar, Options opts, Log log) throws Grammar.Exception
	{
		grammar.markNullableProductions();
		grammar.buildFirstSets();
		State first = makeStates(grammar);
		findLookaheads(first);
		buildActions(grammar, first);
		checkAndResolveConflicts(first, log);
		checkUnreducibleProductions(grammar, first, log);
		if (!opts.no_compression)
			compressActions(first);
		splitActions(first);
		return new CompiledParser(grammar, new ParsingTables(grammar, first));
	}

	static private State makeStates(Grammar grammar)
	{
		Configuration.Set.Factory conf_set_factory = new Configuration.Set.Factory(grammar);
		for (Production rule = grammar.goal_symbol.definitions.start(); rule != null; rule = rule.next_definition)
		{
			Configuration conf = conf_set_factory.addConfiguration(rule, 0);
			conf.addLookahead(grammar.eof);
		}
		State first = new State.Factory(conf_set_factory).getState(conf_set_factory.getCore());
		for (State state = first; state != null; state = state.next)
		{
			state.conf_set.reverseReversePropagation();
			state.conf_set.resetContributionFlags();
		}
		return first;
	}

	static private void findLookaheads(State first)
	{
		boolean more_found;
		do
		{
			more_found = false;

			for (State state = first; state != null; state = state.next)
			{
				if (state.findLookaheads())
				{
					more_found = true;
				}
			}
		}
		while (more_found);
	}

	static private void buildActions(Grammar grammar, State first)
	{
		new Action.Reduce.Maker(grammar.terminals, first).buildReduceActions();

		// Add to the first state (which is always the starting state of the finite state machine)
		// an action to ACCEPT if the lookahead is the start nonterminal.
		first.actions.add(new Action.Accept(grammar));
	}

	static private void checkAndResolveConflicts(State first, Log log) throws Grammar.Exception
	{
		int num_conflicts = 0;
		for (State state = first; state != null; state = state.next)
		{
			num_conflicts += state.actions.resolveConflicts(log);
		}
		if (num_conflicts > 0)
		{
			for (State state = first; state != null; state = state.next)
			{
				state.actions.reportConflicts(log);
			}
			throw new Grammar.Exception("grammar has conflicts");
		}
	}

	static private void checkUnreducibleProductions(Grammar grammar, State first, Log log) throws Grammar.Exception
	{
		for (State state = first; state != null; state = state.next)
		{
			state.actions.markReducibleProductions();
		}
		boolean has_unreducible = false;
		for (int i = 0; i < grammar.rules.length; i++)
		{
			Production rule = grammar.rules[i];
			if (!rule.is_reducible)
			{
				log.error(rule.start_pos, rule.end_pos, "Production \"" + rule.toString() + "\" can not be reduced");
				has_unreducible = true;
			}
		}
		if (has_unreducible)
		{
			throw new Grammar.Exception("grammar has unreducible productions");
		}
	}

	static private void compressActions(State first)
	{
		for (State state = first; state != null; state = state.next)
		{
			state.actions.compress();
		}
	}

	static private void splitActions(State first)
	{
		for (State state = first; state != null; state = state.next)
		{
			state.splitActions();
		}
	}
	
	static public String getOutputFileName(Grammar grammar, File src_file)
	{
		if (grammar.class_name != null)
			return grammar.class_name;

		String spec_file_name = src_file.getName();
		int dot_index = spec_file_name.lastIndexOf('.');
		if (dot_index > 0)
		{
			spec_file_name = spec_file_name.substring(0, dot_index);
		}
		return spec_file_name;
	}
}