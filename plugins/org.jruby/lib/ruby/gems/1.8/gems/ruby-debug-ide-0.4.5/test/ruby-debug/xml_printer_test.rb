$:.unshift File.join(File.dirname(__FILE__),'..','lib')

require 'test/unit'
require 'ruby-debug'

class XmlPrinterTest < Test::Unit::TestCase

  def teardown
    Debugger.xml_debug = false
  end

  def test_print_msg
    interface = MockInterface.new
    printer = Debugger::XmlPrinter.new(interface)
    printer.print_msg('%s test', 'message')
    assert_equal(['<message>message test</message>'], interface.data)
  end

  def test_print_msg_with_debug
    Debugger.xml_debug = true
    interface = MockInterface.new
    printer = Debugger::XmlPrinter.new(interface)
    printer.print_msg('%s test', 'message')
    expected = ["<message>message test</message>"]
    assert_equal(expected, interface.data)
  end

  def test_print_debug
    Debugger.xml_debug = true
    interface = MockInterface.new
    printer = Debugger::XmlPrinter.new(interface)
    printer.print_debug('%s test', 'debug message 1')
    printer.print_debug('%s test', 'debug message 2')
    expected = [
        "<message debug='true'>debug message 1 test</message>",
        "<message debug='true'>debug message 2 test</message>"]
    assert_equal(expected, interface.data)
  end

  def test_print_frames
    interface = MockInterface.new
    printer = Debugger::XmlPrinter.new(interface)
    Debugger.start
    begin
      context = MockContext.new(2)
      printer.print_frames(context, 0)
    ensure
      Debugger.stop
    end
    test_path = File.join(Dir.pwd, 'test.rb')
    expected = [
        "<frames>",
          "<frame no='1' file='#{test_path}' line='0' current='true' />",
          "<frame no='2' file='#{test_path}' line='10' />",
        "</frames>"]
    assert_equal(expected, interface.data)
  end

  def test_print_at_line
    interface = MockInterface.new
    printer = Debugger::XmlPrinter.new(interface)
    Debugger.start
    begin
      printer.print_at_line('test.rb', 1)
    ensure
      Debugger.stop
    end
    test_path = File.join(Dir.pwd, 'test.rb')
    expected = ["<suspended file='#{test_path}' line='1' threadId='1' frames='2'/>"]
    assert_equal(expected, interface.data)
  end

  class MockContext < Debugger::Context

    attr_accessor :stack_size

    def initialize(stack_size)
      @stack_size = stack_size
    end

    def frame_file(id)
      "test.rb"
    end

    def frame_line(id)
      id * 10
    end

  end

  class MockInterface

    attr_accessor :data

    def initialize
      @data = []
    end
    
    def print(*args)
      @data << format(*args)
    end
    
  end
  
end
