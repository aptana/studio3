begin
  require 'maven'
rescue LoadError
  #allow to access the version anyways
end

module TeslaMaven

  VERSION = '0.1.1'.freeze
  
  def self.home
    File.expand_path( File.join( File.dirname( File.expand_path( __FILE__ ) ), '..' ) )
  end

  def self.bin( file = nil )
    if file
      File.join( path( 'bin' ), file )
    else
      path( 'bin' )
    end
  end

  def self.ext
    path( 'ext' )
  end

  def self.lib
    path( 'lib' )
  end

  def self.maven_home
    Maven.home
  end

  def self.maven_bin
    Maven.bin
  end

  def self.maven_lib
    Maven.lib
  end

  def self.maven_conf
    Maven.conf
  end

  def self.maven_boot
    Maven.boot
  end
  
  private

  def self.path( name )
    File.join( home, name )
  end
end
