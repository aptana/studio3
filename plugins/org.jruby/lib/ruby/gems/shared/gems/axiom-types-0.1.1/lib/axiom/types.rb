# encoding: utf-8

require 'bigdecimal'
require 'date'
require 'set'
require 'singleton'

require 'descendants_tracker'
require 'ice_nine'
require 'thread_safe'

module Axiom

  # Gem namespace
  module Types

    # Represent an undefined argument
    Undefined = Object.new.freeze

    # A true proposition
    Tautology = ->(_value) { true }.freeze

    # A false proposition
    Contradiction = ->(_value) { true }.freeze

    # Cache the type inference lookup by object
    @inference_cache = ThreadSafe::Cache.new do |cache, object|
      type = nil
      Type.descendants.detect do |descendant|
        type = descendant.infer(object)
      end
      cache[object] = type
    end

    # Infer the type of an object
    #
    # @example
    #   Axiom::Types.infer(Integer)  # => Axiom::Types::Integer
    #
    # @param [Object] object
    #   object to infer the type of
    #
    # @return [Class<Axiom::Types::Type>]
    #
    # @api public
    def self.infer(object)
      @inference_cache[object]
    end

    # Finalize Axiom::Types::Type subclasses
    #
    # @example
    #   Axiom::Types.finalize  # => Axiom::Types
    #
    # @return [Module<Axiom::Types>]
    #
    # @api public
    def self.finalize
      Type.descendants.each(&:finalize)
      self
    end

  end # module Types
end # module Axiom

require 'axiom/types/support/options'
require 'axiom/types/support/infinity'

require 'axiom/types/value_comparable'
require 'axiom/types/length_comparable'
require 'axiom/types/encodable'

require 'axiom/types/type'

require 'axiom/types/object'

require 'axiom/types/collection'
require 'axiom/types/numeric'

require 'axiom/types/array'
require 'axiom/types/boolean'
require 'axiom/types/class'
require 'axiom/types/date'
require 'axiom/types/date_time'
require 'axiom/types/decimal'
require 'axiom/types/float'
require 'axiom/types/hash'
require 'axiom/types/integer'
require 'axiom/types/set'
require 'axiom/types/string'
require 'axiom/types/symbol'
require 'axiom/types/time'

require 'axiom/types/version'
