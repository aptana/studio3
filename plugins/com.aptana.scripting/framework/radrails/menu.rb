require "java"
require "radrails/base_element"
require "radrails/scope_selector"

module RadRails
  
  class Menu < BaseElement
    def initialize(name)
      super(name)
      
      @jobj.command_name = name;
      
      bundle = BundleManager.bundle_from_path(path)
      bundle.apply_defaults(self) unless bundle.nil?
    end
    
    def add_menu(menu)
      @jobj.add_menu menu.java_object
    end
    
    def command(name, &block)
      RadRails::Command.define_command(name, &block) if block_given?
    
      child_menu = Menu.new(name)
      add_menu(child_menu)
    end
    
    def command_name
      @jobj.command_name
    end
    
    def command_name=(command_name)
      @jobj.command_name = command_name
    end
    
    def menu(name, &block)
      child_menu = Menu.new(name)
      
      block.call(child_menu) if block_given?
      
      add_menu(child_menu)
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
        log_info("loading menu #{name}")
        
        new_menu = Menu.new(name)
        block.call(new_menu) if block_given?
        
        # add command to bundle
        bundle = BundleManager.bundle_from_path(new_menu.path)
        bundle.add_menu(new_menu) unless bundle.nil?
      end
    end
    
    private
    
    def create_java_object
      com.aptana.scripting.model.MenuElement.new($fullpath)
    end
  end
  
end

# define top-level convenience methods

def menu(name, &block)
  RadRails::Menu.define_menu(name, &block)
end
