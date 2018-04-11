require "java"
require "ruble/base_element"
require "ruble/scope_selector"

module Ruble
  
  class Menu < BaseElement
    def initialize(name, path)
      if name.kind_of? String
        super(name, path)
        
        @jobj.command_name = name;
        
        bundle = BundleManager.bundle_from_path(path)
        bundle.apply_defaults(self) unless bundle.nil?
      else
        # hack to pass in java object...should test type
        @jobj = name
      end
    end
    
    def add_menu(menu)
      @jobj.add_menu menu.java_object
    end
    
    def command(name, &block)
      Ruble::Command.define_command(name, &block) if block_given?
    
      child_menu = Menu.new(name, path)
      add_menu(child_menu)
    end
    
    def command_name
      @jobj.command_name
    end
    
    def command_name=(command_name)
      @jobj.command_name = command_name
    end
    
    def menu(name, &block)
      child_menu = Menu.new(name, path)
      
      block.call(child_menu) if block_given?
      
      add_menu(child_menu)
    end
    
    def scope
      @jobj.scope
    end
    
    def scope=(scope)
      @jobj.scope = Ruble::ScopeSelector.new(scope).to_s
    end
    
    def separator
      menu("-")
    end
    
    class << self
      def define_menu(name, &block)
        log_info("loading menu #{name}")
        
        path = $0
        path = block.binding.eval("__FILE__") if block
        new_menu = Menu.new(name, path)
        block.call(new_menu) if block_given?
        
        # add command to bundle
        bundle = BundleManager.bundle_from_path(new_menu.path)
        
        if !bundle.nil?
          bundle.add_child(new_menu)
        else
          log_warning("No bundle found for menu #{name}: #{new_menu.path}")
        end
      end
    end
    
    private
    
    def create_java_object
      com.aptana.scripting.model.MenuElement.new(path)
    end
  end
  
end

# define top-level convenience methods

def menu(name, &block)
  Ruble::Menu.define_menu(name, &block)
end
