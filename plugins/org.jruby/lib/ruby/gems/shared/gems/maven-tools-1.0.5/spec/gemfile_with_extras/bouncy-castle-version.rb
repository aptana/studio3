class BouncyCastle
  MAVEN_VERSION = '1.49' unless defined? MAVEN_VERSION
  VERSION_ = MAVEN_VERSION.sub( /[.]/, '' ) unless defined? BouncyCastle::VERSION_
end
