require "ruble/command"

module Ruble

  class ProjectSample < BaseElement
    def initialize(name, path)
      super
    end    

    def id=(id)
      @jobj.id = id
    end    

    def id
      @jobj.id
    end

    def category=(category)
      @jobj.category = category
    end    
    
    def category
      @jobj.category
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

    def natures=(natures)
      @jobj.natures = natures
    end    
      
    def natures
      @jobj.natures
    end

    def icon=(icon_map)
      @jobj.icon = icon_map
    end

    def icon
      @jobj.icon
    end

    class << self
      def define_sample(name, &block)
        log_info("loading project sample #{name}")

        path = $0
        path = block.binding.eval("__FILE__") if block
        command = ProjectSample.new(name, path)
        block.call(command) if block_given?

        # add command to bundle
        bundle = BundleManager.bundle_from_path(command.path)
        
        if !bundle.nil?
          bundle.add_child(command)
        else
          log_warning("No bundle found for project sample #{name}: #{command.path}")
        end
      end
    end

    private

    def create_java_object
      com.aptana.scripting.model.ProjectSampleElement.new(path)
    end
  end

end

# define top-level convenience methods

def project_sample(name, &block)
  Ruble::ProjectSample.define_sample(name, &block)
end
