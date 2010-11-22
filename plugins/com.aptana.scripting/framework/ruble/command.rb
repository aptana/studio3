require "java"
require "ruble/base_element"
require "ruble/key_binding"
require "ruble/invoke"
require "ruble/scope_selector"
require "ruble/bundle_manager"

module Ruble

  class Command < BaseElement
    def initialize(name, path = nil)
      if name.kind_of? String
        super(name, path)

        @key_binding = KeyBinding.new(java_object)
        @invoke = Invoke.new(self, java_object)
        bundle = BundleManager.bundle_from_path(path)
        bundle.apply_defaults(self) unless bundle.nil?
      else
        # hack to pass in java object...should test type
        @jobj = name
      end
      @jobj.runtime = self # set the runtime for this command
    end

    def async
      @jojb.async
    end

    def async=(flag)
      @jobj.async = flag
    end

    def input
     @jobj.input
    end

    def input=(input)
      return if input.nil?

      case input
      when Array
        @jobj.input_type = input.map { |element| element.to_s }.to_java(:String)
      when Symbol
        @jobj.input_type = input.to_s
      else
        require "pathname"
        bundle = BundleManager.bundle_from_path(path)
        base_path = Pathname.new(File.dirname(bundle.path))
        @jobj.input_type = "input_from_file"
        @jobj.input_path = (base_path + Pathname.new(input.to_s)).to_s
      end
    end

    #
    # Handles cmd.invoke do...end
    # In other cases provides instanceo of Invoke
    # cmd.invoke provides the instance of Invoke
    def invoke(&block)
      if block_given?
        @jobj.set_invoke_block(:all.to_s, &block)
      end
        @invoke
      else
    end

	#
    # Handles cmd.invoke = "..."
    #
    def invoke=(invokeString)
      if invokeString
        @jobj.set_invoke(:all.to_s, invokeString)
      else
        log_error("Missing invoke string in invoke.mac= of Command #{display_name}")
      end
    end

    def key_binding
      @key_binding
    end

    def key_binding=(key_binding)
      @key_binding.all = key_binding
    end

    def output
      @jobj.output
    end

    def output=(output)
      return if output.nil?

      if output.kind_of? Symbol
        @jobj.output_type = output.to_s
      else
        require "pathname"
        bundle = BundleManager.bundle_from_path(path)
        base_path = Pathname.new(File.dirname(bundle.path))
        @jobj.output_type = "output_to_file"
        @jobj.output_path = (base_path + Pathname.new(output.to_s)).to_s
      end
    end

    def owning_bundle
      @jobj.owning_bundle
    end

    def scope
      @jobj.scope
    end

    def scope=(scope)
      @jobj.scope = Ruble::ScopeSelector.new(scope).to_s
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
    
    def trigger=(values)
      if values.kind_of? Symbol
        @jobj.setTrigger(values.to_s)
      elsif values.kind_of? Array
        if values[0].kind_of? Symbol
          @jobj.setTrigger(values.shift.to_s, values.to_java(:String))
        else
          @jobj.setTrigger("prefix", values.to_java(:String))
        end
      else
        @jobj.setTrigger("prefix", [values].to_java(:String))
      end
    end

    class << self
      def define_command(name, &block)
        log_info("loading command #{name}")
        
        path = $0
        path = block.binding.eval("__FILE__") if block
        command = Command.new(name, path)
        block.call(command) if block_given?

        # add command to bundle
        bundle = BundleManager.bundle_from_path(command.path)
        
        if !bundle.nil?
          bundle.add_child(command)
        else
          log_warning("No bundle found for command #{name}: #{command.path}")
        end
      end
    end

    private

    def create_java_object
      com.aptana.scripting.model.CommandElement.new(path)
    end
  end

end

# define top-level convenience methods

def command(name, &block)
  Ruble::Command.define_command(name, &block)
end
