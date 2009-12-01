require "java"
require "radrails/scope_selector"

module RadRails
  
  class Menu
    def initialize(name)
      @jobj = com.aptana.scripting.model.Menu.new($fullpath)
      @jobj.display_name = name;
    end
    
    def command_name
      @jobj.command_name
    end
    
    def command_name=(command_name)
      @jobj.command_name = command_name
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
    
    def path
      @jobj.path
    end
    
    def scope
      @jobj.scope
    end
    
    def scope=(scope)
      @jobj.scope = RadRails::ScopeSelector.new(scope).to_s
    end
    
    class << self
      def define_menu(name, &block)
        menu = Menu.new(name)
        block.call(menu) if block_given?
        
        # add command to bundle
        bundle = BundleManager.bundle_from_path(menu.path)
        
        if bundle.nil? == false
          bundle.add_menu(menu)
        end
      end
    end
  end
  
end