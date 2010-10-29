require "java"

module Ruble
  
  class BaseElement
  	def initialize(name, path)
  	  @path = path
  	  @jobj = create_java_object
      @jobj.display_name = name;
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
    
    def method_missing(m, *args, &block)
      property_name = m.to_s
      
      if property_name.end_with?("=")
        @jobj.put(property_name.chop, args[0])
      else
        @jobj.get property_name
      end
    end
    
    def path
      @path ||= @jobj.path
    end
    
    def to_env
      {}
    end
    
    private
    
    def create_java_object
      # sub-classes need to override
      nil
    end
  end
  
end
