# encoding: utf-8

module IceNine
  class Freezer

    # Skip freezing Rubinius objects
    class Rubinius < NoFreeze; end

  end # Freezer
end # IceNine
