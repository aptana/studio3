require 'java'

module Ruble
  # Returns the current platforms for this system. Returns an array of symbols.
  # :windows, :linux, :mac, :unix are all valid return values. 
  # Note that a linux system will return both :linux and :unix, while the rest should typically 
  # only return a single value in the array
  def Ruble.platforms
    platforms = com.aptana.scripting.model.Platform.getPlatformsForEclipsePlatform(org.eclipse.core.runtime.Platform.getOS())
    platforms.map {|platform| platform.getName.to_sym }
  end
end