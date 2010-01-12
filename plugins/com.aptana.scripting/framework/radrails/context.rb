require "java"

module RadRails

  class Context
    def initialize(jobj=nil)
      @jobj = jobj if jobj
    end

    def in
      @jobj.in
    end
    
    def [](arg)
      @jobj.map[arg.to_s]
    end

    def method_missing(m, *args, &block)
      if @jobj
        @jobj.map[m.to_s]
      else
        super
      end
    end
  end
	
end