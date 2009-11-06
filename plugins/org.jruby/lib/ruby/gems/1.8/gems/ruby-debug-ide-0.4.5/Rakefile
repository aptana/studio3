require 'rubygems'

require 'rake/gempackagetask'
require 'rake/rdoctask'
require 'rake/testtask'
require 'date'

desc 'Default: run unit tests.'
task :default => [:test]

# ------- Default Package ----------
RUBY_DEBUG_BASE_VERSION = "0.10.3"
RUBY_DEBUG_IDE_VERSION = "0.4.5"

FILES = FileList[
  'CHANGES',
  'ChangeLog',
  'ChangeLog.archive',
  'MIT-LICENSE',
  'Rakefile',
  'bin/*',
  'lib/**/*',
  'test/**/*'
]

ide_spec = Gem::Specification.new do |spec|
  spec.name = "ruby-debug-ide"

  spec.homepage = "http://rubyforge.org/projects/debug-commons/"
  spec.summary = "IDE interface for ruby-debug."
  spec.description = <<-EOF
An interface which glues ruby-debug to IDEs like Eclipse (RDT) and NetBeans.
EOF

  spec.version = RUBY_DEBUG_IDE_VERSION

  spec.author = "Markus Barchfeld, Martin Krauskopf"
  spec.email = "rubyeclipse-dev-list@sourceforge.net"
  spec.platform = Gem::Platform::RUBY
  spec.require_path = "lib"
  spec.bindir = "bin"
  spec.executables = ["rdebug-ide"]
  spec.autorequire = "ruby-debug-base"
  spec.files = FILES.to_a

  spec.required_ruby_version = '>= 1.8.2'
  spec.date = DateTime.now
  spec.rubyforge_project = 'debug-commons'
  spec.add_dependency('ruby-debug-base', "~> #{RUBY_DEBUG_BASE_VERSION}.0")

  # rdoc
  spec.has_rdoc = false
end

# Rake task to build the default package
Rake::GemPackageTask.new(ide_spec) do |pkg|
  pkg.need_tar = true
end

# Unit tests
Rake::TestTask.new do |t|
  t.libs << "test"
  t.libs << "test-base"
  t.pattern = 'test/*_test.rb'
  t.verbose = true
  t.warning = false
end


desc "Create a GNU-style ChangeLog via svn2cl"
task :ChangeLog do
  system("svn2cl --authors=svn2cl_usermap svn://rubyforge.org/var/svn/debug-commons/ruby-debug-ide/trunk -o ChangeLog")
end

#desc "Publish ruby-debug to RubyForge."
#task :publish do
#  require 'rake/contrib/sshpublisher'
#
#  # Get ruby-debug path
#  ruby_debug_path = File.expand_path(File.dirname(__FILE__))
#
#  publisher = Rake::SshDirPublisher.new("kent@rubyforge.org",
#        "/var/www/gforge-projects/ruby-debug", ruby_debug_path)
#end
#
#desc "Clear temp files"
#task :clean do
#  cd "ext" do
#    if File.exists?("Makefile")
#      sh "make clean"
#      rm "Makefile"
#    end
#  end
#end
#
## ---------  RDoc Documentation ------
#desc "Generate rdoc documentation"
#Rake::RDocTask.new("rdoc") do |rdoc|
#  rdoc.rdoc_dir = 'doc'
#  rdoc.title    = "ruby-debug"
#  # Show source inline with line numbers
#  rdoc.options << "--inline-source" << "--line-numbers"
#  # Make the readme file the start page for the generated html
#  rdoc.options << '--main' << 'README'
#  rdoc.rdoc_files.include('bin/**/*',
#                          'lib/**/*.rb',
#                          'ext/**/ruby_debug.c',
#                          'README',
#                          'LICENSE')
#end
