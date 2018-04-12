# -*- encoding: utf-8 -*-
# stub: jruby-openssl 0.9.21 java lib

Gem::Specification.new do |s|
  s.name = "jruby-openssl"
  s.version = "0.9.21"
  s.platform = "java"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.require_paths = ["lib"]
  s.authors = ["Ola Bini", "JRuby contributors"]
  s.date = "2017-07-28"
  s.description = "JRuby-OpenSSL is an add-on gem for JRuby that emulates the Ruby OpenSSL native library."
  s.email = "ola.bini@gmail.com"
  s.files = ["History.md", "LICENSE.txt", "Mavenfile", "README.md", "Rakefile", "integration/1.47/pom.xml", "integration/1.48/pom.xml", "integration/1.49/pom.xml", "integration/1.50/pom.xml", "integration/Mavenfile", "integration/pom.xml", "lib/jopenssl.jar", "lib/jopenssl/load.rb", "lib/jopenssl/version.rb", "lib/jopenssl18/openssl.rb", "lib/jopenssl18/openssl/bn.rb", "lib/jopenssl18/openssl/buffering.rb", "lib/jopenssl18/openssl/cipher.rb", "lib/jopenssl18/openssl/config.rb", "lib/jopenssl18/openssl/digest.rb", "lib/jopenssl18/openssl/pkcs7.rb", "lib/jopenssl18/openssl/ssl-internal.rb", "lib/jopenssl18/openssl/ssl.rb", "lib/jopenssl18/openssl/x509-internal.rb", "lib/jopenssl18/openssl/x509.rb", "lib/jopenssl19/openssl.rb", "lib/jopenssl19/openssl/bn.rb", "lib/jopenssl19/openssl/buffering.rb", "lib/jopenssl19/openssl/cipher.rb", "lib/jopenssl19/openssl/config.rb", "lib/jopenssl19/openssl/digest.rb", "lib/jopenssl19/openssl/ssl-internal.rb", "lib/jopenssl19/openssl/ssl.rb", "lib/jopenssl19/openssl/x509-internal.rb", "lib/jopenssl19/openssl/x509.rb", "lib/jopenssl21/openssl.rb", "lib/jopenssl21/openssl/bn.rb", "lib/jopenssl21/openssl/buffering.rb", "lib/jopenssl21/openssl/cipher.rb", "lib/jopenssl21/openssl/config.rb", "lib/jopenssl21/openssl/digest.rb", "lib/jopenssl21/openssl/ssl.rb", "lib/jopenssl21/openssl/x509.rb", "lib/jopenssl22/openssl.rb", "lib/jopenssl22/openssl/bn.rb", "lib/jopenssl22/openssl/buffering.rb", "lib/jopenssl22/openssl/cipher.rb", "lib/jopenssl22/openssl/config.rb", "lib/jopenssl22/openssl/digest.rb", "lib/jopenssl22/openssl/ssl.rb", "lib/jopenssl22/openssl/x509.rb", "lib/jopenssl23/openssl.rb", "lib/jopenssl23/openssl/bn.rb", "lib/jopenssl23/openssl/buffering.rb", "lib/jopenssl23/openssl/cipher.rb", "lib/jopenssl23/openssl/config.rb", "lib/jopenssl23/openssl/digest.rb", "lib/jopenssl23/openssl/pkey.rb", "lib/jopenssl23/openssl/ssl.rb", "lib/jopenssl23/openssl/x509.rb", "lib/jopenssl24.rb", "lib/jruby-openssl.rb", "lib/openssl.rb", "lib/openssl/bn.rb", "lib/openssl/buffering.rb", "lib/openssl/cipher.rb", "lib/openssl/config.rb", "lib/openssl/digest.rb", "lib/openssl/pkcs12.rb", "lib/openssl/pkcs7.rb", "lib/openssl/pkey.rb", "lib/openssl/ssl-internal.rb", "lib/openssl/ssl.rb", "lib/openssl/x509-internal.rb", "lib/openssl/x509.rb", "lib/org/bouncycastle/bcpkix-jdk15on/1.56/bcpkix-jdk15on-1.56.jar", "lib/org/bouncycastle/bcprov-jdk15on/1.56/bcprov-jdk15on-1.56.jar", "pom.xml"]
  s.homepage = "https://github.com/jruby/jruby-openssl"
  s.licenses = ["EPL-1.0", "GPL-2.0", "LGPL-2.1"]
  s.requirements = ["jar org.bouncycastle:bcpkix-jdk15on, 1.56", "jar org.bouncycastle:bcprov-jdk15on, 1.56"]
  s.rubygems_version = "2.4.8"
  s.summary = "JRuby OpenSSL"

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_development_dependency(%q<jar-dependencies>, ["~> 0.1"])
      s.add_development_dependency(%q<mocha>, ["~> 1.1.0"])
      s.add_development_dependency(%q<ruby-maven>, ["~> 3.0"])
    else
      s.add_dependency(%q<jar-dependencies>, ["~> 0.1"])
      s.add_dependency(%q<mocha>, ["~> 1.1.0"])
      s.add_dependency(%q<ruby-maven>, ["~> 3.0"])
    end
  else
    s.add_dependency(%q<jar-dependencies>, ["~> 0.1"])
    s.add_dependency(%q<mocha>, ["~> 1.1.0"])
    s.add_dependency(%q<ruby-maven>, ["~> 3.0"])
  end
end
