require "java"
require "radrails/scope_selector"

module RadRails
  
  class Menu
    def initialize(name)
      @jobj = com.aptana.scripting.model.Menu.new($fullpath)
      @jobj.display_name = name;
    end
    
    def add_menu(menu)
      @jobj.add_menu menu.java_object
    end
    
    def command(name)
      child_menu = Menu.new(name)
      add_menu(child_menu)
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
    
    def menu(name, &block)
      child_menu = Menu.new(name)
      
      block.call(child_menu) if block_given?
      
      add_menu(child_menu)
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
    
    def separator
      menu("-")
    end
    
    class << self
      def define_menu(name, &block)
        new_menu = Menu.new(name)
        block.call(new_menu) if block_given?
        
        # add command to bundle
        bundle = BundleManager.bundle_from_path(new_menu.path)
        
        if bundle.nil? == false
          bundle.add_menu(new_menu)
        end
      end
    end
  end
  
end