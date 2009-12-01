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
    
    def path
      @jobj.path
    end
    
    def scope
      @jobj.scope
    end
    
    def scope=(scope)
      @jobj.scope = RadRails::ScopeSelector.new(scope).to_s
    end
  end
  
end