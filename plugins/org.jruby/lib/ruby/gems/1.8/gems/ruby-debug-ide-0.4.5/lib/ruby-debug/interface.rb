class TCPSocket
  
  # Workaround for JRuby issue http://jira.codehaus.org/browse/JRUBY-2063
  def non_blocking_gets
    loop do
      result, _, _ = IO.select( [self], nil, nil, 0.2 )
      next unless result
      return result[0].gets
    end
  end
  
end

module Debugger  

  class RemoteInterface # :nodoc:

    def initialize(socket)
      @socket = socket
    end
    
    def read_command
      result = @socket.non_blocking_gets
      raise IOError unless result
      result.chomp
    end

    def print(*args)
      @socket.printf(*args)
    end
    
    def close
      @socket.close
    rescue Exception
    end
    
  end
  
end
