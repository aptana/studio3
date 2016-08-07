# encoding: utf-8

module IceNine
  class Freezer

    # A freezer class for handling Array objects
    class Array < Object

      # Deep Freeze an Array
      #
      # @example
      #   array = IceNine:Freezer::Array.deep_freeze(%w[a b c])
      #   array.select(&:frozen?)  # => ['a', 'b', 'c']
      #
      # @param [Array] array
      # @param [RecursionGuard] recursion_guard
      #
      # @return [Array]
      def self.guarded_deep_freeze(array, recursion_guard)
        super
        array.each do |entry|
          Freezer.guarded_deep_freeze(entry, recursion_guard)
        end
      end

    end # Array
  end # Freezer
end # IceNine
