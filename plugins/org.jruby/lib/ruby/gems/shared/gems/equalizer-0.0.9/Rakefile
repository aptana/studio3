# encoding: utf-8

require 'bundler'
Bundler::GemHelper.install_tasks

require 'rspec/core/rake_task'
RSpec::Core::RakeTask.new(:spec)

task :test    => :spec
task :default => :spec

require 'reek/rake/task'
Reek::Rake::Task.new do |reek|
  reek.reek_opts     = '--quiet'
  reek.fail_on_error = true
  reek.config_files  = '.reek.yml'
end

begin
  require 'rubocop/rake_task'
  Rubocop::RakeTask.new
rescue LoadError
  desc 'Run RuboCop'
  task :rubocop do
    $stderr.puts 'Rubocop is disabled'
  end
end

require 'yardstick/rake/measurement'
Yardstick::Rake::Measurement.new do |measurement|
  measurement.output = 'measurement/report.txt'
end

require 'yardstick/rake/verify'
Yardstick::Rake::Verify.new do |verify|
  verify.threshold = 100
end

task :ci => [:spec, :rubocop, :reek, :verify_measurements]
