require "java"
require "radrails/bundle_manager"
require "radrails/scope_selector"

module RadRails
  
  class Snippet
    def initialize(name)
      @jobj = com.aptana.scripting.model.SnippetElement.new($fullpath)
      @jobj.display_name = name
      
      bundle = BundleManager.bundle_from_path(path)
      
      if bundle.nil? == false
        bundle.apply_defaults(self)
      end
    end
    
    def display_name
      @jobj.display_name
    end
    
    def display_name=(display_name)
      @jobj.display_name = display_name
    end
    
    def java_object
      @jobj
    end
    
    def trigger
      @jobj.trigger
    end
    
    def trigger=(trigger)
      @jobj.trigger = trigger
    end
    
    def expansion
      @jobj.expansion
    end
    
    def expansion=(expansion)
      @jobj.expansion = expansion
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
      snippet(
        path:      #{path}
        name:      #{display_name}
        trigger:   #{trigger}
        expansion: #{expansion}
        scope:     #{scope}
      )
      EOS
    end
    
    class << self
      def define_snippet(path, &block)
        snippet = Snippet.new(path)
        block.call(snippet) if block_given?
        
        # add snippet to bundle
        bundle = BundleManager.bundle_from_path(snippet.path)
        
        if bundle.nil? == false
          bundle.add_snippet(snippet)
        end
      end
    end
  end
  
end
