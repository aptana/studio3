require "ruble/command"

module Ruble

  class Template < Command
    def initialize(name, path)
      super
    end    
    
    def filetype=(pattern)
      @jobj.filetype = pattern
    end    
    
    def location=(template_path)
      invoke do |context|
        ENV['TM_DATE'] = Time.now.strftime("%Y-%m-%d")
        raw_contents = IO.read("#{File.dirname(ENV['TM_BUNDLE_SUPPORT'])}/#{template_path}")
        raw_contents.gsub(/\$\{([^}]*)\}/) {|match| ENV[match[2..-2]] }
      end
    end
	
    def filetype
      @jojb.filetype
    end

    class << self
      def define_template(name, &block)
        log_info("loading template #{name}")

        path = $0
        path = block.binding.eval("__FILE__") if block
        command = Template.new(name, path)
        block.call(command) if block_given?

        # add command to bundle
        bundle = BundleManager.bundle_from_path(command.path)
        
        if !bundle.nil?
          bundle.add_child(command)
        else
          log_warning("No bundle found for template #{name}: #{command.path}")
        end
      end
    end

    private

    def create_java_object
      com.aptana.scripting.model.TemplateElement.new(path)
    end
  end

end

# define top-level convenience methods

def template(name, &block)
  Ruble::Template.define_template(name, &block)
end
