require "java"

module Ruble
  
  class KeyBinding
    def initialize(jobj)
      @jobj = jobj
    end

    def []=(os, key_binding)
      if key_binding
        if key_binding.kind_of?(Array)
          @jobj.set_key_bindings(os.to_s, key_binding.to_java(:String))
        else
      	  @jobj.set_key_binding(os.to_s, key_binding.to_s)
      	end
      end
    end
  end

end
