require "java"
require "radrails/base_element"
require "radrails/key_binding"
require "radrails/scope_selector"

module RadRails
  
  class Command < BaseElement
    def initialize(name)
      if name.kind_of? String
        super(name)
        
        @key_binding = KeyBinding.new(java_object)
        bundle = BundleManager.bundle_from_path(path)
        bundle.apply_defaults(self) unless bundle.nil?
      else
        # hack to pass in java object...should test type
        @jobj = name
      end
    end
    
    def input
     @jobj.input 
    end
    
    def input=(input)
      @jobj.input_type = (input && input.kind_of?(Array)) ? input.to_java(:String) : input.to_s;
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
    
    def key_binding
      @key_binding
    end
    
    def key_binding=(key_binding)
      @key_binding[:all] = key_binding
    end
    
    def output
      @jobj.output
    end
    
    def output=(output)
      if output.kind_of? Symbol
        @jobj.output_type = output.to_s
      else
        @jobj.output_type = "output_to_file"
        @jobj.output_path = output.to_s
      end
    end
    
    def owning_bundle
      @jobj.owning_bundle
    end
    
    def scope
      @jobj.scope
    end
    
    def scope=(scope)
      @jobj.scope = RadRails::ScopeSelector.new(scope).to_s
    end
    
    def working_directory=(dir)
      if dir.kind_of? Symbol
        @jobj.working_directory_type = dir.to_s
      else
        @jobj.working_directory_type = "path"
        @jobj.working_directory_path = dir.to_s
      end
    end

    def working_directory
      @jobj.working_directory
    end

    def to_env
      {
        :TM_COMMAND_NAME => display_name,
        :TM_COMMAND_PATH => path
      }
    end
    
    def to_s
      <<-EOS
      command(
        path:               #{path}
        name:               #{display_name}
        scope:              #{scope}
        working_directory:  #{working_directory}
        invoke:             #{invoke}
        key_binding:        #{key_binding}
        output:             #{output}
      )
      EOS
    end
    
    def trigger
      @jobj.trigger
    end
    
    def trigger=(trigger)
      @jobj.trigger = (trigger && trigger.kind_of?(Array)) ? trigger.to_java(:String) : trigger.to_s;
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
    
    private
    
    def create_java_object
      com.aptana.scripting.model.CommandElement.new($fullpath)
    end
  end
  
end

# define top-level convenience methods

def command(name, &block)
  RadRails::Command.define_command(name, &block)
end
