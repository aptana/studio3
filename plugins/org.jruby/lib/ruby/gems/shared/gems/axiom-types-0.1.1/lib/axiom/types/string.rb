# encoding: utf-8

module Axiom
  module Types

    # Represents a string type
    class String < Object
      extend LengthComparable, Encodable

      primitive       ::String
      coercion_method :to_string

      minimum_length 0
      maximum_length 255

    end # class String
  end # module Types
end # module Axiom
