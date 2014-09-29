# encoding: utf-8

module Axiom
  module Types

    # Represents a decimal type
    class Float < Numeric
      primitive       ::Float
      coercion_method :to_float

      minimum primitive::MIN
      maximum primitive::MAX

    end # class Float
  end # module Types
end # module Axiom
