# encoding: utf-8

module Axiom
  module Types

    # Represents an object type
    class Object < Type
      accept_options  :primitive, :coercion_method
      primitive       ::Object.superclass || ::Object
      coercion_method :to_object

      # Infer the type of the object
      #
      # @example
      #   Axiom::Types::Object.infer(::Object)  # => Axiom::Types::Object
      #
      # @param [Object] object
      #
      # @return [Class<Axiom::Types::Object>]
      #   returned if the type matches
      # @return [nil]
      #   returned if the type does not match
      #
      # @api public
      def self.infer(object)
        super || infer_from_primitive_class(object)
      end

      # Finalize by setting up a primitive constraint
      #
      # @return [Class<Axiom::Types::Object>]
      #
      # @api private
      def self.finalize
        return self if frozen?
        inherits_from_primitive
        super
      end

      # The type name and primitive
      #
      # @return [String]
      #
      # @api public
      def self.inspect
        "#{base} (#{primitive})"
      end

      # Infer the type if the primitive class matches
      #
      # @param [Object] object
      #
      # @return [Class<Axiom::Types::Object>]
      #   returned if the primitive class matches
      # @return [nil]
      #   returned if the primitive class does not match
      #
      # @api private
      def self.infer_from_primitive_class(object)
        self if match_primitive?(object)
      end
      private_class_method :infer_from_primitive_class

      # Test if the type matches a primitive class
      #
      # @param [Object] object
      #
      # @return [Boolean]
      #
      # @api private
      def self.match_primitive?(object)
        Module === object &&
        (equal?(Object) || object.ancestors.include?(primitive))
      end
      private_class_method :match_primitive?

      # Add a constraint for the primitive
      #
      # @return [undefined]
      #
      # @api private
      def self.inherits_from_primitive
        constraint(&primitive.method(:===))
      end
      private_class_method :inherits_from_primitive

    end # class Object
  end # module Types
end # module Axiom
