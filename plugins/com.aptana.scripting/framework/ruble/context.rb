require "java"

module Ruble

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
    
    alias :exit_show_tooltip :exit_show_tool_tip
    
    def exit_replace_document(message)
      exit_with_message(message, :replace_document)
    end
    
    def exit_insert_snippet(snippet)
      exit_with_message(snippet, :insert_as_snippet)
    end
    
    def exit_insert_text(text)
      exit_with_message(text, :insert_as_text)
    end
    
    def exit_discard
      exit_with_message('', :discard)
    end
    
    def dynamic_properties
      @jobj.map.key_set.to_array
    end
    
    def input
      @cache_input ||= STDIN.read
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