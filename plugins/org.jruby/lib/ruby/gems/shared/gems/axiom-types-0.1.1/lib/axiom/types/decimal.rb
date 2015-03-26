# encoding: utf-8

module Axiom
  module Types

    # Represents a decimal type
    class Decimal < Numeric
      primitive       ::BigDecimal
      coercion_method :to_decimal

    end # class Decimal
  end # module Types
end # module Axiom
