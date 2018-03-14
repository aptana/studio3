# -*- encoding: utf-8 -*-
# stub: psych 2.2.4 java lib

Gem::Specification.new do |s|
  s.name = "psych"
  s.version = "2.2.4"
  s.platform = "java"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.require_paths = ["lib"]
  s.authors = ["Aaron Patterson", "SHIBATA Hiroshi", "Charles Oliver Nutter"]
  s.date = "2016-11-14"
  s.description = "Psych is a YAML parser and emitter. Psych leverages libyaml[http://pyyaml.org/wiki/LibYAML]\nfor its YAML parsing and emitting capabilities. In addition to wrapping libyaml,\nPsych also knows how to serialize and de-serialize most Ruby objects to and from the YAML format.\n"
  s.email = ["aaron@tenderlovemaking.com", "hsbt@ruby-lang.org", "headius@headius.com"]
  s.extra_rdoc_files = ["CHANGELOG.rdoc", "README.md"]
  s.files = [".gitignore", ".travis.yml", "CHANGELOG.rdoc", "Gemfile", "Mavenfile", "README.md", "Rakefile", "bin/console", "bin/setup", "ext/java/PsychEmitter.java", "ext/java/PsychLibrary.java", "ext/java/PsychParser.java", "ext/java/PsychToRuby.java", "ext/java/PsychYamlTree.java", "ext/psych/.gitignore", "ext/psych/depend", "ext/psych/extconf.rb", "ext/psych/psych.c", "ext/psych/psych.h", "ext/psych/psych_emitter.c", "ext/psych/psych_emitter.h", "ext/psych/psych_parser.c", "ext/psych/psych_parser.h", "ext/psych/psych_to_ruby.c", "ext/psych/psych_to_ruby.h", "ext/psych/psych_yaml_tree.c", "ext/psych/psych_yaml_tree.h", "ext/psych/yaml/LICENSE", "ext/psych/yaml/api.c", "ext/psych/yaml/config.h", "ext/psych/yaml/dumper.c", "ext/psych/yaml/emitter.c", "ext/psych/yaml/loader.c", "ext/psych/yaml/parser.c", "ext/psych/yaml/reader.c", "ext/psych/yaml/scanner.c", "ext/psych/yaml/writer.c", "ext/psych/yaml/yaml.h", "ext/psych/yaml/yaml_private.h", "lib/psych.jar", "lib/psych.rb", "lib/psych/class_loader.rb", "lib/psych/coder.rb", "lib/psych/core_ext.rb", "lib/psych/deprecated.rb", "lib/psych/exception.rb", "lib/psych/handler.rb", "lib/psych/handlers/document_stream.rb", "lib/psych/handlers/recorder.rb", "lib/psych/json/ruby_events.rb", "lib/psych/json/stream.rb", "lib/psych/json/tree_builder.rb", "lib/psych/json/yaml_events.rb", "lib/psych/nodes.rb", "lib/psych/nodes/alias.rb", "lib/psych/nodes/document.rb", "lib/psych/nodes/mapping.rb", "lib/psych/nodes/node.rb", "lib/psych/nodes/scalar.rb", "lib/psych/nodes/sequence.rb", "lib/psych/nodes/stream.rb", "lib/psych/omap.rb", "lib/psych/parser.rb", "lib/psych/scalar_scanner.rb", "lib/psych/set.rb", "lib/psych/stream.rb", "lib/psych/streaming.rb", "lib/psych/syntax_error.rb", "lib/psych/tree_builder.rb", "lib/psych/versions.rb", "lib/psych/visitors.rb", "lib/psych/visitors/depth_first.rb", "lib/psych/visitors/emitter.rb", "lib/psych/visitors/json_tree.rb", "lib/psych/visitors/to_ruby.rb", "lib/psych/visitors/visitor.rb", "lib/psych/visitors/yaml_tree.rb", "lib/psych/y.rb", "lib/psych_jars.rb", "psych.gemspec"]
  s.homepage = "https://github.com/ruby/psych"
  s.licenses = ["MIT"]
  s.rdoc_options = ["--main", "README.md"]
  s.required_ruby_version = Gem::Requirement.new(">= 1.9.2")
  s.requirements = ["jar org.yaml:snakeyaml, 1.18"]
  s.rubygems_version = "2.4.8"
  s.summary = "Psych is a YAML parser and emitter"

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_development_dependency(%q<rake-compiler>, [">= 0.4.1"])
      s.add_development_dependency(%q<minitest>, ["~> 5.0"])
      s.add_runtime_dependency(%q<jar-dependencies>, [">= 0.1.7"])
      s.add_development_dependency(%q<ruby-maven>, [">= 0"])
    else
      s.add_dependency(%q<rake-compiler>, [">= 0.4.1"])
      s.add_dependency(%q<minitest>, ["~> 5.0"])
      s.add_dependency(%q<jar-dependencies>, [">= 0.1.7"])
      s.add_dependency(%q<ruby-maven>, [">= 0"])
    end
  else
    s.add_dependency(%q<rake-compiler>, [">= 0.4.1"])
    s.add_dependency(%q<minitest>, ["~> 5.0"])
    s.add_dependency(%q<jar-dependencies>, [">= 0.1.7"])
    s.add_dependency(%q<ruby-maven>, [">= 0"])
  end
end
