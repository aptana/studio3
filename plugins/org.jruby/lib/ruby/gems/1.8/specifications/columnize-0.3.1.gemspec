# -*- encoding: utf-8 -*-

Gem::Specification.new do |s|
  s.name = %q{columnize}
  s.version = "0.3.1"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["R. Bernstein"]
  s.date = %q{2009-07-25}
  s.description = %q{Return a list of strings as a set of arranged in columns.

For example, for a line width of 4 characters (arranged vertically):
    ['1', '2,', '3', '4'] => '1  3
2  4
'

or arranged horizontally:
    ['1', '2,', '3', '4'] => '1  2
3  4
'

Each column is only as wide as necessary.  By default, columns are
separated by two spaces - one was not legible enough. Set "colsep"
to adjust the string separate columns. Set `displaywidth' to set
the line width.

Normally, consecutive items go down from the top to bottom from
the left-most column to the right-most. If +arrange_vertical+ is
set false, consecutive items will go across, left to right, top to
bottom.
}
  s.email = %q{rockyb@rubyforge.net}
  s.extra_rdoc_files = ["README", "lib/columnize.rb"]
  s.files = ["AUTHORS", "COPYING", "ChangeLog", "NEWS", "README", "Rakefile", "VERSION", "lib/columnize.rb", "test/test-columnize.rb"]
  s.homepage = %q{http://rubyforge.org/projects/rocky-hacks/columnize}
  s.require_paths = ["lib"]
  s.required_ruby_version = Gem::Requirement.new(">= 1.8.2")
  s.rubyforge_project = %q{rocky-hacks}
  s.rubygems_version = %q{1.3.6}
  s.summary = %q{Read file with caching}

  if s.respond_to? :specification_version then
    current_version = Gem::Specification::CURRENT_SPECIFICATION_VERSION
    s.specification_version = 3

    if Gem::Version.new(Gem::RubyGemsVersion) >= Gem::Version.new('1.2.0') then
    else
    end
  else
  end
end
