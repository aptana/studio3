require "java"

module Ruble
  
  class KeyBinding
    def initialize(jobj)
      @jobj = jobj
    end

    def all=(key_binding)
      set_key_bindings(:all, key_binding)
    end

    def mac=(key_binding)
      set_key_bindings(:mac, key_binding)
    end

    def windows=(key_binding)
      set_key_bindings(:windows, key_binding)
    end

    def linux=(key_binding)
      set_key_bindings(:linux, key_binding)
    end

    def unix=(key_binding)
      set_key_bindings(:unix, key_binding)
    end

    private

    def set_key_bindings(os, key_binding)
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
