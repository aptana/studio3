# encoding: utf-8

module IceNine
  class Freezer

    # A freezer class for handling Range objects
    class Range < Object

      # Deep Freeze a Range
      #
      # @example
      #   range = IceNine:Freezer::Range.deep_freeze('a'..'z')
      #   range.begin.frozen?  # => true
      #   range.end.frozen?    # => true
      #
      # @param [Range] range
      # @param [RecursionGuard] recursion_guard
      #
      # @return [Range]
      def self.guarded_deep_freeze(range, recursion_guard)
        super
        Freezer.guarded_deep_freeze(range.begin, recursion_guard)
        Freezer.guarded_deep_freeze(range.end, recursion_guard)
        range
      end

    end # Range
  end # Freezer
end # IceNine
