# test/unit compatibility layer using minitest.

require 'minitest/unit'
require 'test/unit/assertions'
require 'test/unit/testcase'

module Test
  module Unit
    TEST_UNIT_IMPLEMENTATION = 'test/unit compatibility layer using minitest'

    def self.setup_argv(original_argv=ARGV)
      minitest_argv = []
      files = []
      reject = []
      original_argv = original_argv.dup
      while arg = original_argv.shift
        case arg
        when '-v'
          minitest_argv << arg
        when /\A(-n)(.+)?/, /\A(--name)=?\b(.+)?/
          minitest_argv << $1
          minitest_argv << ($2 || original_argv.shift)
        when /\A-x(.+)?/
          reject << ($1 || original_argv.shift)
        else
          files << arg
        end
      end

      if block_given?
        files = yield files
      end

      files.map! {|f|
        f = f.tr(File::ALT_SEPARATOR, File::SEPARATOR) if File::ALT_SEPARATOR
        if File.directory? f
          Dir["#{f}/**/test_*.rb"]
        elsif File.file? f
          f
        else
          raise ArgumentError, "file not found: #{f}"
        end
      }
      files.flatten!

      reject_pat = Regexp.union(reject.map {|r| /#{r}/ })
      files.reject! {|f| reject_pat =~ f }

      files.each {|f|
        d = File.dirname(path = File.expand_path(f))
        unless $:.include? d
          $: << d
        end
        begin
          require path
        rescue LoadError
          puts "#{f}: #{$!}"
        end
      }

      ARGV.replace minitest_argv
    end
  end
end

MiniTest::Unit.autorun
