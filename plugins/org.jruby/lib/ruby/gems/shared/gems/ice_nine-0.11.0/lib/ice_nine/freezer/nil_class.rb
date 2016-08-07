# encoding: utf-8

module IceNine
  class Freezer

    # Skip freezing nil objects
    class NilClass < NoFreeze; end

  end # Freezer
end # IceNine
