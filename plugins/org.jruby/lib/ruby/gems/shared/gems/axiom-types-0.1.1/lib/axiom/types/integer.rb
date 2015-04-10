# encoding: utf-8

module Axiom
  module Types

    # Represents a decimal type
    class Integer < Numeric
      primitive       ::Integer
      coercion_method :to_integer

    end # class Integer
  end # module Types
end # module Axiom
