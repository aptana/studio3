# encoding: utf-8

module IceNine

  # Core Ruby extensions
  module CoreExt

    # Extend Object with deep freezing
    module Object

      # Deep freeze an object
      #
      # @example
      #   object = object.deep_freeze
      #
      # @return [self]
      #
      # @api public
      def deep_freeze
        IceNine.deep_freeze(self)
      end

      # Deep freeze an object
      #
      # @see IceNine.deep_freeze!
      #
      # @example
      #   object = object.deep_freeze!
      #
      # @return [self]
      #
      # @api public
      def deep_freeze!
        IceNine.deep_freeze!(self)
      end

    end # Object
  end # CoreExt
end # IceNine

# Add Object#deep_freeze
Object.instance_eval { include IceNine::CoreExt::Object }
