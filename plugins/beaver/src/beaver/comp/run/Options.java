/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.comp.run;

import java.io.File;

public class Options
{
	public boolean exp_parsing_tables;
	public boolean no_output;
	public boolean terminal_names;
	public boolean export_terminals;
	public boolean no_compression;
	public boolean use_switch;
	public boolean name_action_classes;
	public boolean report_actions;
	public boolean sort_terminals;
	public File    dest_dir;
}