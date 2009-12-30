require "java"

module RadRails

  class Context
    def initialize(jobj=nil)
      @jobj = jobj if jobj
    end

    def in
      @jobj.in
    end

    def method_missing(m, *args, &block)
      @jobj.map[m.to_s]
    end
  end
	
end