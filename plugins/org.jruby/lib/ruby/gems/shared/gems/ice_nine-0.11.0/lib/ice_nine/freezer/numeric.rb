# encoding: utf-8

module IceNine
  class Freezer

    # Skip freezing Numeric objects
    class Numeric < NoFreeze; end

  end # Freezer
end # IceNine
