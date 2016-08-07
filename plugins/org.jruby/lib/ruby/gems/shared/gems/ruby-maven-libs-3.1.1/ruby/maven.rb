module Maven

  VERSION = '3.1.1'.freeze
  
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

  def self.lib
    path( 'lib' )
  end

  def self.conf
    path( 'conf' )
  end

  def self.boot
    path( 'boot' )
  end
  
  private

  def self.path( name )
    File.join( home, name )
  end
end
