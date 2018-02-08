require "ruble/command"

module Ruble

  class ProjectTemplate < BaseElement
    def initialize(name, path)
      super
    end    
    
    def type=(type)
      @jobj.type = type.to_s
    end    
    
    def type
      @jobj.type.to_s
    end
    
    def icon=(icon)
      @jobj.icon = icon.to_s
    end
    
    def icon
      @jobj.icon.to_s
    end
    
    def id=(id)
      @jobj.id = id
    end    
    
    def id
      @jobj.id
    end
    
    def location=(location)
      @jobj.location = location
    end    
    
    def location
      @jobj.location
    end
    
    def description=(description)
      @jobj.description = description
    end    
    
    def description
      @jobj.description
    end
    
    def tags=(tags)
      @jobj.tags = tags
    end
    
    def tags
      @tags.tags
    end

    class << self
      def define_template(name, &block)
        log_info("loading project template #{name}")

        path = $0
        path = block.binding.eval("__FILE__") if block
        command = ProjectTemplate.new(name, path)
        block.call(command) if block_given?

        # add command to bundle
        bundle = BundleManager.bundle_from_path(command.path)
        
        if !bundle.nil?
          bundle.add_child(command)
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
  Ruble::ProjectTemplate.define_template(name, &block)
end
