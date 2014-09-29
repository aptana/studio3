# encoding: utf-8

module Axiom
  module Types

    # Represents a Class type
    class Class < Object
      primitive       ::Class
      coercion_method :to_class

    end # class Class
  end # module Types
end # module Axiom
