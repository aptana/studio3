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

import beaver.Parser;
import beaver.comp.ParserGenerator;
import beaver.comp.io.SrcReader;
import beaver.comp.util.Log;
import beaver.spec.Grammar;

/**
 */
public class Make
{
	static void printVersion()
	{
		System.err.print("Beaver parser generator v");
		System.err.println(ParserGenerator.VERSION);
		System.err.println("Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.");
		System.err.println("All rights reserved.");
	}

	static void printUsage()
	{
		System.err.println("Usage: java -jar beaver.jar [options] <grammar file>");
		System.err.println("where possible options are:");
		System.err.println("  -a        Generate parsing states report (.stat)");
		System.err.println("  -c        Do not compress parsing tables");
		System.err.println("  -d <dir>  Specify where to place generated files");
		System.err.println("  -D        Do not generate anything - dry-run");
		System.err.println("  -e        Export parsing tables into a file (.spec)");
		System.err.println("  -n        Generate non-anonymous delegates for action routines");
		System.err.println("  -s        Sort terminals (by name)");
		System.err.println("  -t        Generate terminal names");
		System.err.println("  -T        Export Terminals \"enum\" class into a file");
		System.err.println("  -s        Sort terminals (by name)");
		System.err.println("  -w        Use \"switch\" to invoke action routines");
		System.err.println("  -v        Print version information and exit");
		System.err.println("  -h        Print this help text and exit");
	}

	static Options parseOptions(String[] args)
	{
		Options opt = new Options();
		int file_name_arg_index = args.length - 1;
		for (int i = 0; i < file_name_arg_index; i++)
		{
			int len = args[i].length(); 
			if (len < 2 || args[i].charAt(0) != '-')
				throw new IllegalArgumentException("Error: \"" + args[i] + "\" is an invalid option.");
			multioptions:
			for (int j = 1; j < len; j++)
			{
				switch (args[i].charAt(j))
				{
					case 'a':
						opt.report_actions = true;
						break;
					case 'c':
						opt.no_compression = true;
						break;
					case 'd':
					{
						if (++i == file_name_arg_index)
							throw new IllegalArgumentException("-d option specified without a destination directory.");
						
						opt.dest_dir = new File(args[i]);
						break multioptions;
					}
					case 'D':
						opt.no_output = true;
						break;
					case 'e':
						opt.exp_parsing_tables = true;
						break;
					case 'n':
						opt.name_action_classes = true;
						break;
					case 's':
						opt.sort_terminals = true;
						break;
					case 't':
						opt.terminal_names = true;
						break;
					case 'T':
						opt.export_terminals = true;
						break;
					case 'w':
						opt.use_switch = true;
						break;
					case 'v':
						printVersion();
						System.exit(0);
						break;
					case 'h':
						printUsage();
						System.exit(0);
						break;
					default:
						throw new IllegalArgumentException("Error: \"-" + args[i].charAt(j) + "\" is an invalid option.");
				}
			}
		}
		return opt;
	}
	
	static File getSrcFile(String name)
	{
		File file = new File(name);
		if (!file.canRead())
			throw new IllegalArgumentException("Error: cannot read \"" + name + "\"");
		return file;
	}
	
	static void compile(SrcReader src, Options opt, Log log)
	{
		try
		{
			ParserGenerator.compile(src, opt, log);
		}
		catch (Parser.Exception e)
		{
			System.err.print("Error: ");
			System.err.println(e.getMessage());
		}
		catch (Grammar.Exception e)
		{
			System.err.print("Error: ");
			System.err.println(e.getMessage());
		}
		catch (IOException e)
		{
			System.err.print("System Error: ");
			System.err.println(e.getMessage());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		if (args.length == 0)
		{
			printUsage();
			System.exit(0);
		}
		try
		{
			Options opts = parseOptions(args);
			File src_file = getSrcFile(args[args.length - 1]);
			SrcReader src_reader = new SrcReader(src_file);
			
			Log log = new Log();
			compile(src_reader, opts, log);
			boolean logHasErrors = log.hasErrors();
			
			log.report(src_file.getName(), src_reader);
			System.exit(logHasErrors ? 1 : 0);
		}
		catch (IllegalArgumentException e) 
		{
			System.err.println(e.getMessage());
			printUsage();
			System.exit(1);
		}
	}
}
