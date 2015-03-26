# encoding: utf-8

module Axiom
  module Types

    # Represents a date time type
    class Time < Object
      extend ValueComparable

      # The minimum and maximum seconds for Time on 32-bit systems
      MINIMUM_SECONDS = -2**31
      MAXIMUM_SECONDS =  2**31 - 1

      primitive       ::Time
      coercion_method :to_time

      minimum primitive.at(MINIMUM_SECONDS).utc
      maximum primitive.at(MAXIMUM_SECONDS).utc

    end # class Time
  end # module Types
end # module Axiom
