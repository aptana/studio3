require "java"
require "ruble/bundle_manager"
require "ruble/base_element"

module Ruble
  
  class SnippetCategory < BaseElement
    def initialize(name, path)
      super
    end    

    def icon_path=(icon_path)
      @jobj.icon_path = icon_path
    end

    def icon_path
      @jobj.icon_path
    end

    class << self
      def define_snippet_category(name, &block)
        log_info("loading snippet category #{name}")
        
        path = $0
        path = block.binding.eval("__FILE__") if block
        snippetCategory = SnippetCategory.new(name, path)
        block.call(snippetCategory) if block_given?
        
        # add snippet category to bundle
        bundle = BundleManager.bundle_from_path(snippetCategory.path)
        
        if !bundle.nil?
          bundle.add_child(snippetCategory)
        else
          log_warning("No bundle found for snippet category #{name}: #{snippetCategory.path}")
        end
      end
    end
    
    private
    
    def create_java_object
      com.aptana.scripting.model.SnippetCategoryElement.new(path)
    end
    
  end
  
end

# define top-level convenience methods

def snippet_category(name, &block)
  Ruble::SnippetCategory.define_snippet_category(name, &block)
end