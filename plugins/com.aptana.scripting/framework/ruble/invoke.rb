require "java"

module Ruble

  class Invoke
    def initialize(command, jobj)
      @command = command
      @jobj = jobj
    end

    def all(&block)
      if block_given?
        @jobj.set_invoke_block(&block)
      else
        log_error("Missing block in invoke.all of Command #{@command.display_name}")
      end
    end

    def all=(invokeString)
      if invokeString
        @jobj.set_invoke(invokeString)
      else
        log_error("Missing invoke string in invoke.all= of Command #{@command.display_name}")
      end
    end

    def mac(&block)
      if block_given?
        @jobj.set_invoke_block(:mac.to_s, &block)
      else
        log_error("Missing block in invoke.mac of Command #{@command.display_name}")
      end
    end

    def mac=(invokeString)
      if invokeString
        @jobj.set_invoke(:mac.to_s, invokeString)
      else
        log_error("Missing invoke string in invoke.mac= of Command #{@command.display_name}")
      end
    end

    def windows(&block)
      if block_given?
        @jobj.set_invoke_block(:windows.to_s, &block)
      else
        log_error("Missing block in invoke.windows of Command #{@command.display_name}")
      end
    end

    def windows=(invokeString)
      if invokeString
        @jobj.set_invoke(:windows.to_s, invokeString)
      else
        log_error("Missing invoke string in invoke.windows= of Command #{@command.display_name}")
      end
    end

    def linux(&block)
      if block_given?
        @jobj.set_invoke_block(:linux.to_s, &block)
      else
        log_error("Missing block in invoke.linux of Command #{@command.display_name}")
      end
    end

    def linux=(invokeString)
      if invokeString
        @jobj.set_invoke(:linux.to_s, invokeString)
      else
        log_error("Missing invoke string in invoke.linux= of Command #{@command.display_name}")
      end
    end

    def unix(&block)
      if block_given?
        @jobj.set_invoke_block(:unix.to_s, &block)
      else
        log_error("Missing block in invoke.unix of Command #{@command.display_name}")
      end
    end

    def unix=(invokeString)
      if invokeString
        @jobj.set_invoke(:unix.to_s, invokeString)
      else
        log_error("Missing invoke string in invoke.unix= of Command #{@command.display_name}")
      end
    end

  end

end
