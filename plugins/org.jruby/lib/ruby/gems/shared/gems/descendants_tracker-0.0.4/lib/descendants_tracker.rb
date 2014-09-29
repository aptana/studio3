# encoding: utf-8

require 'thread_safe'

# Module that adds descendant tracking to a class
module DescendantsTracker

  # Return the descendants of this class
  #
  # @example
  #   descendants = ParentClass.descendants
  #
  # @return [Array<Class<DescendantsTracker>>]
  #
  # @api public
  attr_reader :descendants

  # Setup the class for descendant tracking
  #
  # @param [Class<DescendantsTracker>] descendant
  #
  # @return [undefined]
  #
  # @api private
  def self.setup(descendant)
    descendant.instance_variable_set(:@descendants, ThreadSafe::Array.new)
  end

  class << self
    alias_method :extended, :setup
    private :extended
  end

  # Add the descendant to this class and the superclass
  #
  # @param [Class] descendant
  #
  # @return [self]
  #
  # @api private
  def add_descendant(descendant)
    ancestor = superclass
    if ancestor.respond_to?(:add_descendant)
      ancestor.add_descendant(descendant)
    end
    descendants.unshift(descendant)
    self
  end

private

  # Hook called when class is inherited
  #
  # @param [Class] descendant
  #
  # @return [self]
  #
  # @api private
  def inherited(descendant)
    super
    DescendantsTracker.setup(descendant)
    add_descendant(descendant)
  end

end # module DescendantsTracker
