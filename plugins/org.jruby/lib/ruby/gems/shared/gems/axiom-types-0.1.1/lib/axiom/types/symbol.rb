# encoding: utf-8

module Axiom
  module Types

    # Represents a symbol type
    class Symbol < Object
      extend LengthComparable, Encodable

      primitive       ::Symbol
      coercion_method :to_symbol

      minimum_length 0
      maximum_length 255

    end # class Symbol
  end # module Types
end # module Axiom
