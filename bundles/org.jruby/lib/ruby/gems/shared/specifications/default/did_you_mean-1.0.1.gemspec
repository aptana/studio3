# -*- encoding: utf-8 -*-
# stub: did_you_mean 1.0.1 ruby lib

Gem::Specification.new do |s|
  s.name = "did_you_mean"
  s.version = "1.0.1"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.require_paths = ["lib"]
  s.authors = ["Yuki Nishijima"]
  s.date = "2016-05-15"
  s.description = "\"did you mean?\" experience in Ruby: the error message will tell you the right one when you misspelled something."
  s.email = ["mail@yukinishijima.net"]
  s.files = [".gitignore", ".travis.yml", "CHANGELOG.md", "Gemfile", "LICENSE.txt", "README.md", "Rakefile", "benchmark/jaro_winkler/memory_usage.rb", "benchmark/jaro_winkler/speed.rb", "benchmark/levenshtein/memory_usage.rb", "benchmark/levenshtein/speed.rb", "benchmark/memory_usage.rb", "did_you_mean.gemspec", "doc/CHANGELOG.md.erb", "doc/changelog_generator.rb", "evaluation/calculator.rb", "evaluation/dictionary_generator.rb", "evaluation/incorrect_words.yaml", "lib/did_you_mean.rb", "lib/did_you_mean/core_ext/name_error.rb", "lib/did_you_mean/extra_features.rb", "lib/did_you_mean/extra_features/initializer_name_correction.rb", "lib/did_you_mean/extra_features/ivar_name_correction.rb", "lib/did_you_mean/formatter.rb", "lib/did_you_mean/jaro_winkler.rb", "lib/did_you_mean/levenshtein.rb", "lib/did_you_mean/spell_checkable.rb", "lib/did_you_mean/spell_checkers/method_name_checker.rb", "lib/did_you_mean/spell_checkers/name_error_checkers.rb", "lib/did_you_mean/spell_checkers/name_error_checkers/class_name_checker.rb", "lib/did_you_mean/spell_checkers/name_error_checkers/variable_name_checker.rb", "lib/did_you_mean/spell_checkers/null_checker.rb", "lib/did_you_mean/verbose_formatter.rb", "lib/did_you_mean/version.rb", "test/core_ext/name_error_extension_test.rb", "test/correctable/class_name_test.rb", "test/correctable/method_name_test.rb", "test/correctable/uncorrectable_name_test.rb", "test/correctable/variable_name_test.rb", "test/edit_distance/jaro_winkler_test.rb", "test/extra_features/initializer_name_correction_test.rb", "test/extra_features/method_name_checker_test.rb", "test/spell_checker_test.rb", "test/test_helper.rb", "test/verbose_formatter_test.rb"]
  s.homepage = "https://github.com/yuki24/did_you_mean"
  s.licenses = ["MIT"]
  s.required_ruby_version = Gem::Requirement.new(">= 2.3.0")
  s.rubygems_version = "2.4.8"
  s.summary = "\"Did you mean?\" experience in Ruby"
  s.test_files = ["test/core_ext/name_error_extension_test.rb", "test/correctable/class_name_test.rb", "test/correctable/method_name_test.rb", "test/correctable/uncorrectable_name_test.rb", "test/correctable/variable_name_test.rb", "test/edit_distance/jaro_winkler_test.rb", "test/extra_features/initializer_name_correction_test.rb", "test/extra_features/method_name_checker_test.rb", "test/spell_checker_test.rb", "test/test_helper.rb", "test/verbose_formatter_test.rb"]

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_development_dependency(%q<bundler>, ["~> 1.10"])
      s.add_development_dependency(%q<rake>, [">= 0"])
      s.add_development_dependency(%q<minitest>, [">= 0"])
    else
      s.add_dependency(%q<bundler>, ["~> 1.10"])
      s.add_dependency(%q<rake>, [">= 0"])
      s.add_dependency(%q<minitest>, [">= 0"])
    end
  else
    s.add_dependency(%q<bundler>, ["~> 1.10"])
    s.add_dependency(%q<rake>, [">= 0"])
    s.add_dependency(%q<minitest>, [">= 0"])
  end
end
