# encoding: utf-8

module Axiom
  module Types

    # Represents a collection type
    class Collection < Object
      primitive      ::Enumerable
      accept_options :member_type

      member_type Object

      # Infer the type of the object
      #
      # @example with a type
      #   Axiom::Types::Array.infer(Axiom::Types::Array)
      #   # => Axiom::Types::Array
      #
      # @example with a primitive class
      #   Axiom::Types::Collection.infer(::Array)
      #   # => Axiom::Types::Array
      #
      # @example with a primitive instance
      #   Axiom::Types::Array.infer(Array[])
      #   # => Axiom::Types::Array
      #
      # @example with a primitive instance and a member type
      #   Axiom::Types::Collection.infer(Array[Axiom::Types::String])
      #   # => Axiom::Types::Array subclass w/String member type
      #
      # @example with a primitive instance and a member primitive
      #   Axiom::Types::Collection.infer(Array[String])
      #   # => Axiom::Types::Array subclass w/String member type
      #
      # @param [Object] object
      #
      # @return [Class<Axiom::Types::Collection>]
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

      # Finalize by setting up constraints for the member
      #
      # @return [Class<Axiom::Types::Collection>]
      #
      # @api private
      def self.finalize
        return self if frozen?
        member_type.finalize
        matches_member_type
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
        super && member_type.equal?(Object)
      end
      private_class_method :match_primitive?

      # Infer the type from a primitive instance
      #
      # @param [Object] object
      #
      # @return [Class<Axiom::Types::Collection>]
      #   returned if the primitive instance matches
      # @return [nil]
      #   returned if the primitive instance does not match
      #
      # @api private
      def self.infer_from_primitive_instance(object)
        member_type = Types.infer(object.first) || Object
        infer_from(member_type) || new_from(member_type)
      end
      private_class_method :infer_from_primitive_instance

      # Infer the type from the member_type
      #
      # @param [Class<Axiom::Types::Object>] member_type
      #
      # @return [Class<Axiom::Types::Collection>]
      #   returned if the member_type matches
      # @return [nil]
      #   returned if the member_type does not match
      #
      # @api private
      def self.infer_from(member_type)
        self if self.member_type.equal?(member_type)
      end
      private_class_method :infer_from

      # Instantiate a new type from a base type
      #
      # @param [Class<Axiom::Types::Object>] member_type
      #
      # @return [Class<Axiom::Types::Collection>]
      #   returned if a base type
      # @return [nil]
      #   returned if not a base type
      #
      # @api private
      def self.new_from(member_type)
        new { member_type(member_type) } if base?
      end
      private_class_method :new_from

      # Test if the type is a base type
      #
      # @return [Boolean]
      #
      # @api private
      def self.base?
        # noop
      end
      private_class_method :base?

      # Add a constraints for the member
      #
      # @return [undefined]
      #
      # @api private
      def self.matches_member_type
        constraint do |object|
          object.all? { |member| member_type.include?(member) }
        end
      end

      private_class_method :matches_member_type

    end # class Collection
  end # module Types
end # module Axiom
