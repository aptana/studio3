require "java"

module RadRails

  class Context
    def initialize(jobj=nil)
      @jobj = jobj if jobj
    end
    
    def exit_with_message(message, output_type)
      @jobj.forced_exit = true
      @jobj.map['output'] = message.to_s
      @jobj.output_type = output_type.to_s
      exit
    end
    
    def exit_show_tool_tip(message)
      exit_with_message(message, :show_as_tooltip)
    end
    
    def exit_discard
      exit_with_message('', :discard)
    end
    	
    def in
      @jobj.in
    end
    
    def input
      self.in
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