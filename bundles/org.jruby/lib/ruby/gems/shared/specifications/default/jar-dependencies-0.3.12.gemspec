# -*- encoding: utf-8 -*-
# stub: jar-dependencies 0.3.12 ruby lib

Gem::Specification.new do |s|
  s.name = "jar-dependencies"
  s.version = "0.3.12"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.require_paths = ["lib"]
  s.authors = ["christian meier"]
  s.date = "2017-12-26"
  s.description = "manage jar dependencies for gems and keep track which jar was already loaded using maven artifact coordinates. it warns on version conflicts and loads only ONE jar assuming the first one is compatible to the second one otherwise your project needs to lock down the right version by providing a Jars.lock file."
  s.email = ["mkristian@web.de"]
  s.executables = ["lock_jars"]
  s.files = ["MIT-LICENSE", "Mavenfile", "Rakefile", "Readme.md", "bin/lock_jars", "jar-dependencies.gemspec", "lib/jar-dependencies.rb", "lib/jar_dependencies.rb", "lib/jar_install_post_install_hook.rb", "lib/jar_installer.rb", "lib/jars/attach_jars_pom.rb", "lib/jars/classpath.rb", "lib/jars/gemspec_artifacts.rb", "lib/jars/gemspec_pom.rb", "lib/jars/installer.rb", "lib/jars/lock.rb", "lib/jars/lock_down.rb", "lib/jars/lock_down_pom.rb", "lib/jars/maven_exec.rb", "lib/jars/maven_factory.rb", "lib/jars/maven_settings.rb", "lib/jars/output_jars_pom.rb", "lib/jars/post_install_hook.rb", "lib/jars/settings.xml", "lib/jars/setup.rb", "lib/jars/version.rb", "lib/rubygems_plugin.rb"]
  s.homepage = "https://github.com/mkristian/jar-dependencies"
  s.licenses = ["MIT"]
  s.post_install_message = "\nif you want to use the executable lock_jars then install ruby-maven gem before using lock_jars\n\n  $ gem install ruby-maven -v '~> 3.3.11'\n\nor add it as a development dependency to your Gemfile\n\n   gem 'ruby-maven', '~> 3.3.11'\n\n"
  s.rubygems_version = "2.4.8"
  s.summary = "manage jar dependencies for gems"

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_development_dependency(%q<minitest>, ["~> 5.3"])
      s.add_development_dependency(%q<rake>, ["~> 10.2"])
      s.add_development_dependency(%q<ruby-maven>, ["~> 3.3.11"])
    else
      s.add_dependency(%q<minitest>, ["~> 5.3"])
      s.add_dependency(%q<rake>, ["~> 10.2"])
      s.add_dependency(%q<ruby-maven>, ["~> 3.3.11"])
    end
  else
    s.add_dependency(%q<minitest>, ["~> 5.3"])
    s.add_dependency(%q<rake>, ["~> 10.2"])
    s.add_dependency(%q<ruby-maven>, ["~> 3.3.11"])
  end
end
