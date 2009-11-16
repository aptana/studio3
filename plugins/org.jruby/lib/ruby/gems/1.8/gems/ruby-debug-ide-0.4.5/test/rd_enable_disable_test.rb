#!/usr/bin/env ruby

require 'rd_test_base'

class RDEnableDisableTest < RDTestBase

  def test_enable_disable_basics
    create_socket ['1.upto(10) do', 'sleep 0.01', 'sleep 0.01', 'end']
    
    send_test_breakpoint(2)
    assert_breakpoint_added_no(1)
    send_test_breakpoint(3)
    assert_breakpoint_added_no(2)
    
    start_debugger
    assert_test_breakpoint(2)
    send_cont
    assert_test_breakpoint(3)
    send_cont
    assert_test_breakpoint(2)
    send_ruby('disable 2')
    assert_breakpoint_disabled(2)
    send_cont
    assert_test_breakpoint(2)
    send_cont
    assert_test_breakpoint(2)
    send_ruby('enable 2')
    assert_breakpoint_enabled(2)
    send_cont
    assert_test_breakpoint(3)
    send_cont
    assert_test_breakpoint(2)
    send_cont
    assert_test_breakpoint(3)
    send_ruby('disable 1')
    assert_breakpoint_disabled(1)
    send_ruby('disable 2')
    assert_breakpoint_disabled(2)
    send_cont
  end

end

