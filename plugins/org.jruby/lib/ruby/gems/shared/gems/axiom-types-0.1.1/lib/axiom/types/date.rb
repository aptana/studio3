# encoding: utf-8

module Axiom
  module Types

    # Represents a date type
    class Date < Object
      extend ValueComparable

      primitive       ::Date
      coercion_method :to_date

      minimum primitive.new(1, 1, 1)
      maximum primitive.new(9999, 12, 31)

    end # class Date
  end # module Types
end # module Axiom
