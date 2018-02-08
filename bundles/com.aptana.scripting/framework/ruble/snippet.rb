require "java"
require "ruble/bundle_manager"
require "ruble/scope_selector"

module Ruble
  
  class Snippet < Command
    def initialize(name, path = nil)
      if name.kind_of? String
        super(name, path)
      else
        # hack to pass in java object...should test type
        @jobj = name
        @jobj.runtime = self # set the runtime for this command
      end
    end
    
    def expansion
      @jobj.expansion
    end

    def expansion=(expansion)
      @jobj.expansion = expansion
    end
        
    def icon_path=(icon_path)
      @jobj.icon_path = icon_path.to_s
    end
    
    def icon_path
      @jobj.icon_path.to_s
    end

    def category=(category)
      @jobj.category = category.to_s
    end
    
    def category
      @category.category.to_s
    end
            
    def tags=(tags)
      @jobj.tags = tags
    end
    
    def tags
      @tags.tags
    end
    
    def description=(description)
      @jobj.description = description.to_s
    end
    
    def description
      @description.description.to_s
    end
    
    def to_env
      {
        :TM_SNIPPET_NAME => display_name,
        :TM_SNIPPET_PATH => path
      }
    end
    
    def to_s
      <<-EOS
      snippet(
        path:      #{path}
        name:      #{display_name}
        trigger:   #{trigger}
        expansion: #{expansion}
        keys:   #{key_binding}
        scope:     #{scope}
        icon_path: #{icon_path}
        category:  #{category}
        tags:	   #{tags}
      )
      EOS
    end
    
    class << self
      def define_snippet(name, &block)
        log_info("loading snippet #{name}")
        
        path = $0
        path = block.binding.eval("__FILE__") if block
        snippet = Snippet.new(name, path)
        block.call(snippet) if block_given?
        
        # add snippet to bundle
        bundle = BundleManager.bundle_from_path(snippet.path)
        
        if !bundle.nil?
          bundle.add_child(snippet)
        else
          log_warning("No bundle found for snippet #{name}: #{snippet.path}")
        end
      end
    end
    
    private
    
    def create_java_object
      com.aptana.scripting.model.SnippetElement.new(path)
    end
    
  end
  
end

# define top-level convenience methods

def snippet(name, &block)
  Ruble::Snippet.define_snippet(name, &block)
end
