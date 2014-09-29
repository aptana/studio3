# encoding: utf-8

module IceNine
  class Freezer

    # A freezer class for handling Hash objects
    class Hash < Object

      # Deep Freeze a Hash
      #
      # @example
      #   hash = IceNine::Freezer::Hash.deep_freeze('a' => '1', 'b' => '2')
      #   hash.keys.select(&:frozen?)    # => ['a', 'b']
      #   hash.values.select(&:frozen?)  # => ['1', '2']
      #
      # @param [Hash] hash
      # @param [RecursionGuard] recursion_guard
      #
      # @return [Hash]
      def self.guarded_deep_freeze(hash, recursion_guard)
        super
        default = hash.default_proc || hash.default
        Freezer.guarded_deep_freeze(default, recursion_guard)
        freeze_key_value_pairs(hash, recursion_guard)
      end

      # Handle freezing the key/value pairs
      #
      # @param [Hash] hash
      # @param [RecursionGuard] recursion_guard
      #
      # @return [undefined]
      #
      # @api private
      def self.freeze_key_value_pairs(hash, recursion_guard)
        hash.each do |key, value|
          Freezer.guarded_deep_freeze(key, recursion_guard)
          Freezer.guarded_deep_freeze(value, recursion_guard)
        end
      end

      private_class_method :freeze_key_value_pairs

    end # Hash
  end # Freezer
end # IceNine
