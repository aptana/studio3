#!/usr/bin/env ruby

$:.unshift File.join(File.dirname(__FILE__), "..", "test-base")
$:.unshift File.join(File.dirname(__FILE__), "..", "lib")

require 'test_base'

class RDTestBase < TestBase

  def setup
    super
    @rdebug_ide = config_load('rdebug_ide', true) || find_rdebug_ide
    unless @rdebug_ide and File.exist?(@rdebug_ide)
      @fast_fail = true
      assert_not_nil(@rdebug_ide, "Cannot find rdebug-ide executable. " +
          "Neither set in the config(.private).yaml nor found on the PATH")
      assert(false, "#{@rdebug_ide} exist")
    end
  end

  def debug_command(script, port)
    cmd = "#{interpreter}"
    cmd << " --debug" if jruby?
    cmd << " -J-Xdebug -J-Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y" if jruby? and debug_jruby?
    cmd << " -I '#{File.dirname(script)}' #{@rdebug_ide} _0.4.5_" +
      (@verbose_server ? " -d" : "") + 
      " -p #{port} -- '#{script}'"
  end

  def start_debugger
    send_ruby("start")
  end
  
  private
  
  def find_rdebug_ide
    ENV['PATH'].split(File::PATH_SEPARATOR).each do |dir|
      rdebug_ide = File.join(dir, 'rdebug-ide')
      return rdebug_ide if File.exists?(rdebug_ide)
    end
    nil
  end
  
end
