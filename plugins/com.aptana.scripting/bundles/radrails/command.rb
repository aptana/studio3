require "java"

module RadRails
  
  class Command
    def initialize(name)
      @jobj = com.aptana.scripting.model.Command.new($fullpath)
      @jobj.display_name = name;
    end
    
    def display_name
      @jobj.display_name
    end
    
    def display_name=(display_name)
      @jobj.display_name = display_name
    end
    
    def invoke
      @jobj.invoke
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
      as_strings = key_binding.map do |x|
        x.to_s
      end
      @jobj.key_binding = as_strings.join(" ")
    end
    
    def output
      @jobj.output
    end
    
    def output=(output)
      @jobj.output = output.to_s
    end
    
    def path
      @jobj.path
    end
    
    def scope
      @jobj.scope
    end
    
    def scope=(scope)
      @jobj.scope = scope
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
    
    class << self
      def define_command(name, &block)
        command = Command.new(name)
        block.call(command) if block_given?
        
        # add command to bundle
        bundle = BundleManager.bundle_from_path(command.path)
        
        if bundle.nil? == false
          bundle.add_command(command)
        end
      end
    end
  end
  
end
