require "ruble/command"

module Ruble

  class ProjectTemplate < Command
    def initialize(name, path)
      super
    end    
    
    def type=(type)
      @jobj.type = type.to_s
    end    
    
    def type
      @jojb.type.to_s
    end
    
    def location=(location)
      @jobj.location = location
    end    
    
    def location
      @jojb.location
    end
    
    def description=(description)
      @jobj.description = description
    end    
    
    def description
      @jojb.description
    end

    class << self
      def define_project_template(name, &block)
        log_info("loading project template #{name}")

        path = block.binding.eval("__FILE__")
        command = ProjectTemplate.new(name, path)
        block.call(command) if block_given?

        # add command to bundle
        bundle = BundleManager.bundle_from_path(command.path)
        
        if !bundle.nil?
          bundle.add_project_template(command)
        else
          log_warning("No bundle found for project template #{name}: #{command.path}")
        end
      end
    end

    private

    def create_java_object
      com.aptana.scripting.model.ProjectTemplateElement.new(path)
    end
  end

end

# define top-level convenience methods

def project_template(name, &block)
  Ruble::Template.define_template(name, &block)
end
