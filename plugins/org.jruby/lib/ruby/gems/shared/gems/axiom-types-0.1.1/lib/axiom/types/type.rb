# encoding: utf-8

module Axiom
  module Types

    # Abstract base class for every type
    class Type
      extend Options, DescendantsTracker

      accept_options :constraint
      constraint Tautology

      # Infer the type of the object
      #
      # @example
      #  type = Axiom::Types::Type.infer(Axiom::Types::Integer)
      #  # => Axiom::Types::Integer
      #
      # @param [Object] object
      #
      # @return [Class<Axiom::Types::Type>]
      #
      # @api public
      def self.infer(object)
        self if equal?(object)
      end

      # Instantiate a new Axiom::Types::Type subclass
      #
      # @example
      #   type = Axiom::Types::Type.new  # => Axiom::Types::Type
      #
      # @param [Array(#call)] args
      #   optional constraint for the new type
      #
      # @yield [object]
      #
      # @yieldparam object [Object]
      #   test if the object matches the type constraint
      #
      # @yieldreturn [Boolean]
      #   true if the object matches the type constraint
      #
      # @return [Class<Axiom::Types::Type>]
      #
      # @api public
      def self.new(*args, &block)
        type = ::Class.new(self, &block)
        type.constraint(*args)
        type.finalize
      end

      # Finalize by deep freezing
      #
      # @return [Class<Axiom::Types::Type>]
      #
      # @api private
      def self.finalize
        IceNine.deep_freeze(constraint)
        freeze
      end

      # Test if the object matches the type constraint
      #
      # @example
      #   type = Axiom::Types::Integer.new do
      #     minimum 1
      #     maximum 100
      #   end
      #
      #   type.include?(1)    # => true
      #   type.include?(100)  # => true
      #   type.include?(0)    # => false
      #   type.include?(101)  # => false
      #
      # @param [Object] object
      #
      # @return [Boolean]
      #
      # @api public
      def self.include?(object)
        constraint.call(object)
      end

      # Silence warnings when redeclaring constraint
      singleton_class.class_eval { undef_method :constraint }

      # Add a constraint to the type
      #
      # @example with an argument
      #   type.constraint(->(object) { object == 42 }
      #
      # @example with a block
      #   type.constraint { |object| object == 42 }
      #
      # @example with no arguments
      #   type.constraint  # => constraint
      #
      # @param [#call] constraint
      #   optional constraint
      #
      # @yield [object]
      #
      # @yieldparam object [Object]
      #   test if the object matches the type constraint
      #
      # @yieldreturn [Boolean]
      #   true if the object matches the type constraint
      #
      # @return [Class<Axiom::Types::Type>]
      #
      # @api public
      def self.constraint(constraint = Undefined, &block)
        constraint = block if constraint.equal?(Undefined)
        return @constraint if constraint.nil?
        add_constraint(constraint)
        self
      end

      # Add a constraint that the object must be included in a set
      #
      # @param [Array<Object>] members
      #
      # @return [undefined]
      #
      # @todo move into a module
      #
      # @api private
      def self.includes(*members)
        set = IceNine.deep_freeze(members.to_set)
        constraint(&set.method(:include?))
      end

      # The base type for the type
      #
      # @return [Class<Axiom::Types::Type>]
      #
      # @api public
      def self.base
        base? ? self : superclass.base
      end

      # Test if the type is a base type
      #
      # @return [Boolean]
      #
      # @api public
      def self.base?
        !anonymous?
      end

      # Test if the type is anonymous
      #
      # @return [Boolean]
      #
      # @api public
      def self.anonymous?
        name.to_s.empty?
      end

      # Add new constraint to existing constraint, if any
      #
      # @param [#call] constraint
      #
      # @return [undefined]
      #
      # @api private
      def self.add_constraint(constraint)
        current = self.constraint
        @constraint =
          if current
            ->(object) { constraint.call(object) && current.call(object) }
          else
            constraint
          end
      end

      private_class_method :add_constraint

    end # class Type
  end # module Types
end # module Axiom
