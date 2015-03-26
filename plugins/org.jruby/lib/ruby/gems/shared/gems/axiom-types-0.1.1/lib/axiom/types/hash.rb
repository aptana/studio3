# encoding: utf-8

module Axiom
  module Types

    # Represents a hash type
    class Hash < Object
      primitive       ::Hash
      coercion_method :to_hash
      accept_options  :key_type, :value_type

      key_type   Object
      value_type Object

      # Infer the type of the object
      #
      # @example
      #   type = Axiom::Types.infer(object)
      #
      # @param [Object] object
      #
      # @return [Class<Axiom::Types::Hash>]
      #   returned if the type matches
      # @return [nil]
      #   returned if the type does not match
      #
      # @api public
      def self.infer(object)
        case object
        when primitive
          infer_from_primitive_instance(object)
        else
          super
        end
      end

      # Finalize by setting up constraints for the key and value
      #
      # @return [Class<Axiom::Types::Hash>]
      #
      # @api private
      def self.finalize
        return self if frozen?
        key_type.finalize
        value_type.finalize
        matches_key_and_value_types
        super
      end

      # Test if the type matches a primitive class
      #
      # @param [Object] object
      #
      # @return [Boolean]
      #
      # @api private
      def self.match_primitive?(*)
        super && key_type.equal?(Object) && value_type.equal?(Object)
      end
      private_class_method :match_primitive?

      # Infer the type from a primitive instance
      #
      # @param [Object] object
      #
      # @return [Class<Axiom::Types::Hash>]
      #   returned if the primitive instance matches
      # @return [nil]
      #   returned if the primitive instance does not match
      #
      # @api private
      def self.infer_from_primitive_instance(object)
        key, value = object.first
        key_type   = Types.infer(key)   || Object
        value_type = Types.infer(value) || Object
        infer_from(key_type, value_type) || new_from(key_type, value_type)
      end
      private_class_method :infer_from_primitive_instance

      # Infer the type from the key_type and value_type
      #
      # @param [Class<Axiom::Types::Object>] key_type
      # @param [Class<Axiom::Types::Object>] value_type
      #
      # @return [Class<Axiom::Types::Hash>]
      #   returned if the key_type and value_type match
      # @return [nil]
      #   returned if the key_type and value_type do not match
      #
      # @api private
      def self.infer_from(key_type, value_type)
        self if self.key_type.equal?(key_type) &&
                self.value_type.equal?(value_type)
      end
      private_class_method :infer_from

      # Instantiate a new type from a base type
      #
      # @param [Class<Axiom::Types::Object>] key_type
      # @param [Class<Axiom::Types::Object>] value_type
      #
      # @return [Class<Axiom::Types::Hash>]
      #   returned if a base type
      # @return [nil]
      #   returned if not a base type
      #
      # @api private
      def self.new_from(key_type, value_type)
        new { key_type(key_type).value_type(value_type) } if base?
      end
      private_class_method :new_from

      # Test if the type is a base type
      #
      # @return [Boolean]
      #
      # @api private
      def self.base?
        equal?(Hash)
      end
      private_class_method :base?

      # Add a constraints for the key and value
      #
      # @return [undefined]
      #
      # @api private
      def self.matches_key_and_value_types
        constraint do |object|
          object.all? do |key, value|
            key_type.include?(key) && value_type.include?(value)
          end
        end
      end
      private_class_method :matches_key_and_value_types

    end # class Hash
  end # module Types
end # module Axiom
