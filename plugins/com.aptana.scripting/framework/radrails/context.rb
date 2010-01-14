require "java"

module RadRails

  class Context
    def initialize(jobj=nil)
      @jobj = jobj if jobj
    end
    
    def exit_with_message(message, output_type)
      STDERR.puts message
      @jobj.output_type = output_type
      exit
    end
    	
    def in
      @jobj.in
    end
    
    def [](arg)
      @jobj.map[arg.to_s]
    end

    def method_missing(m, *args, &block)
      if @jobj
        @jobj.map[m.to_s]
      else
        super
      end
    end
  end
	
end