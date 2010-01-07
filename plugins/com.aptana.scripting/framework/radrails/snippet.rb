require "java"
require "radrails/bundle_manager"
require "radrails/scope_selector"

module RadRails
  
  class Snippet < Command
    def initialize(name)
      if name.kind_of? String
        super(name)
      else
        # hack to pass in java object...should test type
        @jobj = name
      end
    end
    
    def expansion
      @jobj.expansion
    end
    
    def expansion=(expansion)
      @jobj.expansion = expansion
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
      )
      EOS
    end
    
    class << self
      def define_snippet(name, &block)
        log_info("loading snippet #{name}")
        
        snippet = Snippet.new(name)
        block.call(snippet) if block_given?
        
        # add snippet to bundle
        bundle = BundleManager.bundle_from_path(snippet.path)
        bundle.add_command(snippet) unless bundle.nil?
      end
    end
    
    private
    
    def create_java_object
      com.aptana.scripting.model.SnippetElement.new($fullpath)
    end
    
  end
  
end

# define top-level convenience methods

def snippet(name, &block)
  RadRails::Snippet.define_snippet(name, &block)
end
