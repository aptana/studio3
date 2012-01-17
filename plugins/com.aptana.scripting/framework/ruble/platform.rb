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
  
  def Ruble.is_windows_xp?
    os_name.start_with? "Windows XP"
  end  
  
  def Ruble.is_windows_7?
    os_name == "Windows 7"
  end
  
  def Ruble.is_windows_vista?
    os_name == "Windows Vista"
  end
  
  def Ruble.is_windows?
    Ruble.platforms.include? :windows
  end
  
  def Ruble.is_mac?
    Ruble.platforms.include? :mac
  end  

  def Ruble.is_linux?
    Ruble.platforms.include? :linux
  end  

  def Ruble.is_unix?
    Ruble.platforms.include? :unix
  end  

  def Ruble.os_name
    java.lang.System.getProperty("os.name")
  end  
end