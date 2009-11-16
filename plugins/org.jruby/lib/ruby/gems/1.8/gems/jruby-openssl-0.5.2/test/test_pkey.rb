if defined?(JRUBY_VERSION)
  require "java"
  base = File.dirname(__FILE__)
  $CLASSPATH << File.join(base, '..', 'pkg', 'classes')
  $CLASSPATH << File.join(base, '..', 'lib', 'bcprov-jdk14-139.jar')
end

begin
  require "openssl"
rescue LoadError
end

require "test/unit"

class TestPKey < Test::Unit::TestCase
  def test_has_correct_methods
    pkey_methods = OpenSSL::PKey::PKey.instance_methods(false).sort - ["initialize"]
    assert_equal ["sign", "verify"], pkey_methods

    rsa_methods = OpenSSL::PKey::RSA.instance_methods(false).sort - ["initialize"]
    assert_equal ["d", "d=", "dmp1", "dmp1=", "dmq1", "dmq1=", "e", "e=", "export", "iqmp", "iqmp=", "n", "n=", "p", "p=", "params", "private?", "private_decrypt", "private_encrypt", "public?", "public_decrypt", "public_encrypt", "public_key", "q", "q=", "to_der", "to_pem", "to_s", "to_text"], rsa_methods

    assert_equal ["generate"], OpenSSL::PKey::RSA.methods(false)
    
#     dsa_methods = OpenSSL::PKey::DSA.instance_methods(false).sort - ["initialize"]
#     assert_equal ["export", "g", "g=", "p", "p=", "params", "priv_key", "priv_key=", "private?", "pub_key", "pub_key=", "public?", "public_key", "q", "q=", "syssign", "sysverify", "to_der", "to_pem", "to_s", "to_text"], dsa_methods

#     assert_equal ["generate"], OpenSSL::PKey::DSA.methods(false)
  end
  
  #iqmp == coefficient
  #e == public exponent
  #n == modulus
  #d == private exponent
  #p == prime1
  #q == prime2
  #dmq1 == exponent2
  #dmp1 == exponent1
  
  def test_can_generate_rsa_key
    OpenSSL::PKey::RSA.generate(512)
  end

  def test_can_generate_dsa_key
  end
end
