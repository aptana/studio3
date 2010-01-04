require "java"

module RadRails
  
  class KeyBinding
    def initialize(jobj)
      @jobj = jobj
    end

    def []=(os, key_binding)
      as_strings = key_binding.map {|x| x.to_s }
      @jobj.set_key_binding(os.to_s, as_strings.join(" "))
    end
  end

end
