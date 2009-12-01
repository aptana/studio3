package com.aptana.explorer.internal.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Inflector
{

	private static class Replacement
	{
		private Pattern rule;

		private String replacement;

		Replacement(Pattern rule, String replacement)
		{
			this.rule = rule;
			this.replacement = replacement;
		}
	}

	private static List<Replacement> plurals = new ArrayList<Replacement>();
	private static List<Replacement> singulars = new ArrayList<Replacement>();
	private static List<String> uncountables = new ArrayList<String>();

	public static String singularize(String plural)
	{
		if (uncountables.contains(plural.toLowerCase()))
		{
			return plural;
		}
		for (Replacement replacement : singulars)
		{
			Matcher matcher = replacement.rule.matcher(plural);
			if (matcher.find())
			{
				return matcher.replaceFirst(replacement.replacement);
			}
		}
		return plural;
	}

	public static String pluralize(String singular)
	{
		if (uncountables.contains(singular.toLowerCase()))
		{
			return singular;
		}
		for (Replacement replacement : plurals)
		{
			Matcher matcher = replacement.rule.matcher(singular);
			if (matcher.find())
			{
				return matcher.replaceFirst(replacement.replacement);
			}
		}
		return singular;
	}

	private static void plural(Pattern rule, String replacement)
	{
		plurals.add(0, new Replacement(rule, replacement));
	}

	private static void singular(Pattern rule, String replacement)
	{
		singulars.add(0, new Replacement(rule, replacement));
	}

	private static void irregular(String singular, String plural)
	{
		plural(Pattern.compile("(" + singular.charAt(0) + ")" + singular.substring(1) + "$", Pattern.CASE_INSENSITIVE), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"$1" + plural.substring(1)); //$NON-NLS-1$
		singular(Pattern.compile("(" + plural.charAt(0) + ")" + plural.substring(1) + "$", Pattern.CASE_INSENSITIVE), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"$1" + singular.substring(1)); //$NON-NLS-1$
	}

	private static void uncountable(String[] words)
	{
		for (int i = 0; i < words.length; i++)
		{
			uncountables.add(words[i]);
		}
	}

	static
	{
		Inflector.plural(Pattern.compile("$"), "s"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("s$", Pattern.CASE_INSENSITIVE), "s"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("(ax|test)is$", Pattern.CASE_INSENSITIVE), "$1es"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("(octop|vir)us$", Pattern.CASE_INSENSITIVE), "$1i"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("(alias|status)$", Pattern.CASE_INSENSITIVE), "$1es"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("(bu)s$", Pattern.CASE_INSENSITIVE), "$1ses"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("(buffal|tomat)o$", Pattern.CASE_INSENSITIVE), "$1oes"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("([ti])um$", Pattern.CASE_INSENSITIVE), "$1a"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("sis$", Pattern.CASE_INSENSITIVE), "ses"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("(?:([^f])fe|([lr])f)$", Pattern.CASE_INSENSITIVE), "$1$2ves"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("(hive)$", Pattern.CASE_INSENSITIVE), "$1s"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("([^aeiouy]|qu)y$", Pattern.CASE_INSENSITIVE), "$1ies"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("(x|ch|ss|sh)$", Pattern.CASE_INSENSITIVE), "$1es"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("(matr|vert|ind)ix|ex$", Pattern.CASE_INSENSITIVE), "$1ices"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("([m|l])ouse$", Pattern.CASE_INSENSITIVE), "$1ice"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("^(ox)$", Pattern.CASE_INSENSITIVE), "$1en"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.plural(Pattern.compile("(quiz)$", Pattern.CASE_INSENSITIVE), "$1zes"); //$NON-NLS-1$ //$NON-NLS-2$

		Inflector.singular(Pattern.compile("s$", Pattern.CASE_INSENSITIVE), ""); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("(n)ews$", Pattern.CASE_INSENSITIVE), "$1ews"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("([ti])a$", Pattern.CASE_INSENSITIVE), "$1um"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", //$NON-NLS-1$
				Pattern.CASE_INSENSITIVE), "$1$2sis"); //$NON-NLS-1$
		Inflector.singular(Pattern.compile("(^analy)ses$", Pattern.CASE_INSENSITIVE), "$1sis"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("([^f])ves$", Pattern.CASE_INSENSITIVE), "$1fe"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("(hive)s$", Pattern.CASE_INSENSITIVE), "$1"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("(tive)s$", Pattern.CASE_INSENSITIVE), "$1"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("([lr])ves$", Pattern.CASE_INSENSITIVE), "$1f"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("([^aeiouy]|qu)ies$", Pattern.CASE_INSENSITIVE), "$1y"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("(s)eries$", Pattern.CASE_INSENSITIVE), "$1eries"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("(m)ovies$", Pattern.CASE_INSENSITIVE), "$1ovie"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("(x|ch|ss|sh)es$", Pattern.CASE_INSENSITIVE), "$1"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("([m|l])ice$", Pattern.CASE_INSENSITIVE), "$1ouse"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("(bus)es$", Pattern.CASE_INSENSITIVE), "$1"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("(o)es$", Pattern.CASE_INSENSITIVE), "$1"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("(shoe)s$", Pattern.CASE_INSENSITIVE), "$1"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("(cris|ax|test)es$", Pattern.CASE_INSENSITIVE), "$1is"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("(octop|vir)i$", Pattern.CASE_INSENSITIVE), "$1us"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("(alias|status)es$", Pattern.CASE_INSENSITIVE), "$1"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("^(ox)en", Pattern.CASE_INSENSITIVE), "$1"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("(vert|ind)ices$", Pattern.CASE_INSENSITIVE), "$1ex"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("(matr)ices$", Pattern.CASE_INSENSITIVE), "$1ix"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.singular(Pattern.compile("(quiz)zes$", Pattern.CASE_INSENSITIVE), "$1"); //$NON-NLS-1$ //$NON-NLS-2$

		Inflector.irregular("person", "people"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.irregular("man", "men"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.irregular("child", "children"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.irregular("sex", "sexes"); //$NON-NLS-1$ //$NON-NLS-2$
		Inflector.irregular("move", "moves"); //$NON-NLS-1$ //$NON-NLS-2$

		Inflector.uncountable(new String[] { "equipment", "information", "rice", "money", "species", "series", "fish", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
				"sheep" }); //$NON-NLS-1$
	}
}
