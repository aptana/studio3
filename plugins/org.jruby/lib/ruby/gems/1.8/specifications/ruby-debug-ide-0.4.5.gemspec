# -*- encoding: utf-8 -*-

Gem::Specification.new do |s|
  s.name = %q{ruby-debug-ide}
  s.version = "0.4.5"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Markus Barchfeld, Martin Krauskopf"]
  s.autorequire = %q{ruby-debug-base}
  s.date = %q{2009-03-12}
  s.default_executable = %q{rdebug-ide}
  s.description = %q{An interface which glues ruby-debug to IDEs like Eclipse (RDT) and NetBeans.}
  s.email = %q{rubyeclipse-dev-list@sourceforge.net}
  s.executables = ["rdebug-ide"]
  s.files = ["CHANGES", "ChangeLog", "ChangeLog.archive", "MIT-LICENSE", "Rakefile", "bin/rdebug-ide", "lib/ruby-debug", "lib/ruby-debug/xml_printer.rb", "lib/ruby-debug/command.rb", "lib/ruby-debug/processor.rb", "lib/ruby-debug/commands", "lib/ruby-debug/commands/load.rb", "lib/ruby-debug/commands/breakpoints.rb", "lib/ruby-debug/commands/variables.rb", "lib/ruby-debug/commands/control.rb", "lib/ruby-debug/commands/enable.rb", "lib/ruby-debug/commands/threads.rb", "lib/ruby-debug/commands/stepping.rb", "lib/ruby-debug/commands/eval.rb", "lib/ruby-debug/commands/catchpoint.rb", "lib/ruby-debug/commands/condition.rb", "lib/ruby-debug/commands/frame.rb", "lib/ruby-debug/commands/inspect.rb", "lib/ruby-debug/interface.rb", "lib/ruby-debug/printers.rb", "lib/ruby-debug/helper.rb", "lib/ruby-debug/event_processor.rb", "lib/ruby-debug.rb", "test/rd_basic_test.rb", "test/rd_test_base.rb", "test/rd_inspect_test.rb", "test/rd_catchpoint_test.rb", "test/ruby-debug", "test/ruby-debug/xml_printer_test.rb", "test/rd_enable_disable_test.rb", "test/rd_threads_and_frames_test.rb", "test/rd_condition_test.rb", "test/rd_stepping_breakpoints_test.rb", "test/rd_variables_test.rb"]
  s.homepage = %q{http://rubyforge.org/projects/debug-commons/}
  s.require_paths = ["lib"]
  s.required_ruby_version = Gem::Requirement.new(">= 1.8.2")
  s.rubyforge_project = %q{debug-commons}
  s.rubygems_version = %q{1.3.1}
  s.summary = %q{IDE interface for ruby-debug.}

  if s.respond_to? :specification_version then
    current_version = Gem::Specification::CURRENT_SPECIFICATION_VERSION
    s.specification_version = 3

    if Gem::Version.new(Gem::RubyGemsVersion) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<ruby-debug-base>, ["~> 0.10.3.0"])
    else
      s.add_dependency(%q<ruby-debug-base>, ["~> 0.10.3.0"])
    end
  else
    s.add_dependency(%q<ruby-debug-base>, ["~> 0.10.3.0"])
  end
end
