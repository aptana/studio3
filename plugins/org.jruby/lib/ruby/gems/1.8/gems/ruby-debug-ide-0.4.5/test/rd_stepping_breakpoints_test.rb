#!/usr/bin/env ruby

require 'rd_test_base'
require 'stepping_breakpoints_test'

class RDSteppingAndBreakpointsTest < RDTestBase

  include SteppingAndBreakpointsTest

  def test_hit_breakpoint_while_stepping_over
    create_test2 ["class Test2", "def print", "puts 'XX'", "puts 'XX'", "end", "end"]
    create_socket ["require 'test2.rb'", "Test2.new.print", "puts 'a'"]
    send_ruby("b #{@test2_name}:4")
    assert_breakpoint_added_no(1)
    run_to_line(2)
    send_next
    assert_breakpoint(@test2_name, 4)
    send_cont
  end

  def test_breakpoint_and_continue_from_other_file
    create_test2 ["class Test2", "def print12", "puts 'one'","puts 'two'", "end", "end"]
    create_socket ["require 'test2.rb'", "Test2.new.print12", "puts 'three'"]
    send_test_breakpoint(2)
    assert_breakpoint_added_no(1)
    send_ruby("b #{@test2_name}:4")
    assert_breakpoint_added_no(2)
    start_debugger
    assert_test_breakpoint(2)
    send_next # test:1 -> test2:4
    assert_breakpoint(@test2_name, 4)
    send_cont # test2:4 -> test:3
  end

end

