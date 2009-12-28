require "java"
require "radrails/scope_selector"

module RadRails
  
  class Command
    def initialize(name)
      @jobj = com.aptana.scripting.model.CommandElement.new($fullpath)
      @jobj.display_name = name;
      
      bundle = BundleManager.bundle_from_path(path)
      bundle.apply_defaults(self) unless bundle.nil?
    end
    
    def display_name
      @jobj.display_name
    end
    
    def display_name=(display_name)
      @jobj.display_name = display_name
    end
    
    def input
     @jobj.input 
    end
    
    def input=(input)
      @jobj.input_type = input.to_s
    end
    
    def invoke(&block)
      if block_given?
        @jobj.invoke_block = block
      else
        @jobj.invoke
      end
    end
    
    def invoke=(invoke)
      @jobj.invoke = invoke
    end
    
    def java_object
      @jobj
    end
    
    def key_binding
      @jobj.key_binding
    end
    
    def key_binding=(key_binding)
      as_strings = key_binding.map {|x| x.to_s }
      @jobj.key_binding = as_strings.join(" ")
    end
    
    def output
      @jobj.output
    end
    
    def output=(output)
      @jobj.output_type = output.to_s
    end
    
    def path
      @jobj.path
    end
    
    def scope
      @jobj.scope
    end
    
    def scope=(scope)
      @jobj.scope = RadRails::ScopeSelector.new(scope).to_s
    end
    
    def to_s
      <<-EOS
      command(
        path:   #{path}
        name:   #{display_name}
        invoke: #{invoke}
        keys:   #{key_binding}
        output: #{output}
        scope:  #{scope}
      )
      EOS
    end
    
    def trigger
      @jobj.trigger
    end
    
    def trigger=(trigger)
      @jobj.trigger = trigger
    end
    
    class << self
      def define_command(name, &block)
        log_info("loading command #{name}")
        
        command = Command.new(name)
        block.call(command) if block_given?
        
        # add command to bundle
        bundle = BundleManager.bundle_from_path(command.path)
        bundle.add_command(command) unless bundle.nil?
      end
    end
  end
  
end
