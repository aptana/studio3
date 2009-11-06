begin
  require "openssl"
  require File.join(File.dirname(__FILE__), "utils.rb")
rescue LoadError
end
require "rbconfig"
require "socket"
require "test/unit"
require 'tempfile'

if defined?(OpenSSL)

class OpenSSL::TestSSL < Test::Unit::TestCase
  RUBY = ENV["RUBY"] || File.join(
    ::Config::CONFIG["bindir"],
    ::Config::CONFIG["ruby_install_name"] + ::Config::CONFIG["EXEEXT"]
  )
  SSL_SERVER = File.join(File.dirname(__FILE__), "ssl_server.rb")
  PORT = 20443
  ITERATIONS = ($0 == __FILE__) ? 5 : 5

  # Disable in-proc process launching and either run jruby with specified args
  # or yield args to a given block
  def jruby_oop(*args)
    prev_in_process = JRuby.runtime.instance_config.run_ruby_in_process
    JRuby.runtime.instance_config.run_ruby_in_process = false
    if block_given?
      yield args
    else
      `#{RUBY} #{args.join(' ')}`
    end
  ensure
    JRuby.runtime.instance_config.run_ruby_in_process = prev_in_process
  end

  def setup
    @ca_key  = OpenSSL::TestUtils::TEST_KEY_RSA2048
    @svr_key = OpenSSL::TestUtils::TEST_KEY_RSA1024
    @cli_key = OpenSSL::TestUtils::TEST_KEY_DSA256
    @ca  = OpenSSL::X509::Name.parse("/DC=org/DC=ruby-lang/CN=CA")
    @svr = OpenSSL::X509::Name.parse("/DC=org/DC=ruby-lang/CN=localhost")
    @cli = OpenSSL::X509::Name.parse("/DC=org/DC=ruby-lang/CN=localhost")

    now = Time.at(Time.now.to_i)
    ca_exts = [
      ["basicConstraints","CA:TRUE",true],
      ["keyUsage","cRLSign,keyCertSign",true],
    ]
    ee_exts = [
      ["keyUsage","keyEncipherment,digitalSignature",true],
    ]
    @ca_cert  = issue_cert(@ca, @ca_key, 1, now, now+3600, ca_exts,
                           nil, nil, OpenSSL::Digest::SHA1.new)
    @svr_cert = issue_cert(@svr, @svr_key, 2, now, now+1800, ee_exts,
                           @ca_cert, @ca_key, OpenSSL::Digest::SHA1.new)
    @cli_cert = issue_cert(@cli, @cli_key, 3, now, now+1800, ee_exts,
                           @ca_cert, @ca_key, OpenSSL::Digest::SHA1.new)
    @server = nil
  end

  def teardown
  end

  def issue_cert(*arg)
    OpenSSL::TestUtils.issue_cert(*arg)
  end

  def issue_crl(*arg)
    OpenSSL::TestUtils.issue_crl(*arg)
  end

  def choose_port(port)
    tcps = nil
    100.times{|i|
    begin
      tcps = TCPServer.new("127.0.0.1", port+i)
      port = port + i
      break
    rescue Errno::EADDRINUSE
      next 
    end
    }
    return tcps, port
  end

  def start_server(port0, verify_mode, start_immediately, ctx = nil, &block)
    tcps, port = choose_port(port0)
    t = Thread.start {
      begin
      if ctx.nil?
        store = OpenSSL::X509::Store.new
        store.add_cert(@ca_cert)
        store.purpose = OpenSSL::X509::PURPOSE_ANY
        ctx = OpenSSL::SSL::SSLContext.new
        ctx.cert_store = store
        #ctx.extra_chain_cert = [ ca_cert ]
        ctx.cert = @svr_cert
        ctx.key = @svr_key
        ctx.verify_mode = verify_mode
      end

      Socket.do_not_reverse_lookup = true
      ssls = OpenSSL::SSL::SSLServer.new(tcps, ctx)
      ssls.start_immediately = start_immediately

      loop do
        begin
          ssl = ssls.accept
          Thread.start{
            q = Queue.new
            th = Thread.start{ ssl.write(q.shift) while true }
            while line = ssl.gets
              if line =~ /^STARTTLS$/
                ssl.accept
                next
              end
              q.push(line)
            end
            th.kill if q.empty?
            ssl.close
          }
        rescue
          if $DEBUG
            puts $!
            puts $!.backtrace.join("\n")
          end
        end
      end
      rescue
        puts $!
        puts $!.backtrace.join("\n")
      end
    }
    sleep 1
    block.call(nil, port.to_i)
  end

  def starttls(ssl)
    ssl.puts("STARTTLS")

    sleep 1   # When this line is eliminated, process on Cygwin blocks
              # forever at ssl.connect. But I don't know why it does.

    ssl.connect
  end

  def test_connect_and_close
    start_server(PORT, OpenSSL::SSL::VERIFY_NONE, true){|s, p|
      sock = TCPSocket.new("127.0.0.1", p)
      ssl = OpenSSL::SSL::SSLSocket.new(sock)
      assert(ssl.connect)
      ssl.close
      assert(!sock.closed?)
      sock.close

      sock = TCPSocket.new("127.0.0.1", p)
      ssl = OpenSSL::SSL::SSLSocket.new(sock)
      ssl.sync_close = true  # !!
      assert(ssl.connect)
      ssl.close
      assert(sock.closed?)
    }
  end

  def test_read_and_write
    start_server(PORT, OpenSSL::SSL::VERIFY_NONE, true){|s, p|
      sock = TCPSocket.new("127.0.0.1", p)
      ssl = OpenSSL::SSL::SSLSocket.new(sock)
      ssl.sync_close = true
      ssl.connect

      # syswrite and sysread
      ITERATIONS.times{|i|
        str = "x" * 100 + "\n"
        ssl.syswrite(str)
        assert_equal(str, ssl.sysread(str.size))

        str = "x" * i * 100 + "\n"
        buf = ""
        ssl.syswrite(str)
        assert_equal(buf.object_id, ssl.sysread(str.size, buf).object_id)
        assert_equal(str, buf)
      }

      # puts and gets
      ITERATIONS.times{
        str = "x" * 100 + "\n"
        ssl.puts(str)
        assert_equal(str, ssl.gets)
      }

      # read and write
      ITERATIONS.times{|i|
        str = "x" * 100 + "\n"
        ssl.write(str)
        assert_equal(str, ssl.read(str.size))

        str = "x" * i * 100 + "\n"
        buf = ""
        ssl.write(str)
        assert_equal(buf.object_id, ssl.read(str.size, buf).object_id)
        assert_equal(str, buf)
      }

      ssl.close
    }
  end

  def test_client_auth
    vflag = OpenSSL::SSL::VERIFY_PEER|OpenSSL::SSL::VERIFY_FAIL_IF_NO_PEER_CERT
    start_server(PORT, vflag, true){|s, p|
      assert_raises(OpenSSL::SSL::SSLError){
        sock = TCPSocket.new("127.0.0.1", p)
        ssl = OpenSSL::SSL::SSLSocket.new(sock)
        ssl.connect
      }
      ctx = OpenSSL::SSL::SSLContext.new
      ctx.key = @cli_key
      ctx.cert = @cli_cert
      sock = TCPSocket.new("127.0.0.1", p)
      ssl = OpenSSL::SSL::SSLSocket.new(sock, ctx)
      ssl.sync_close = true
      ssl.connect
      ssl.puts("foo")
      assert_equal("foo\n", ssl.gets)
      ssl.close

      called = nil
      ctx = OpenSSL::SSL::SSLContext.new
      ctx.client_cert_cb = Proc.new{|ssl2|
        called = true
        [@cli_cert, @cli_key]
      }
      sock = TCPSocket.new("127.0.0.1", p)
      ssl = OpenSSL::SSL::SSLSocket.new(sock, ctx)
      ssl.sync_close = true
      ssl.connect
      assert(called)
      ssl.puts("foo")
      assert_equal("foo\n", ssl.gets)
      ssl.close
    }
  end

  def test_client_auth_with_server_store
    vflag = OpenSSL::SSL::VERIFY_PEER

    localcacert_file = Tempfile.open("cafile")
    localcacert_file << @ca_cert.to_pem
    localcacert_file.close
    localcacert_path = localcacert_file.path

    ssl_store = OpenSSL::X509::Store.new
    ssl_store.purpose = OpenSSL::X509::PURPOSE_ANY
    ssl_store.add_file(localcacert_path)

    server_ctx = OpenSSL::SSL::SSLContext.new
    server_ctx.cert = @svr_cert
    server_ctx.key = @svr_key
    server_ctx.verify_mode = vflag
    server_ctx.cert_store = ssl_store

    start_server(PORT, vflag, true, server_ctx){|s, p|
      ctx = OpenSSL::SSL::SSLContext.new
      ctx.cert = @cli_cert
      ctx.key = @cli_key
      sock = TCPSocket.new("127.0.0.1", p)
      ssl = OpenSSL::SSL::SSLSocket.new(sock, ctx)
      ssl.sync_close = true
      ssl.connect
      ssl.puts("foo")
      assert_equal("foo\n", ssl.gets)
      ssl.close
      localcacert_file.unlink
    }
  end

  def test_client_crl_with_server_store
    vflag = OpenSSL::SSL::VERIFY_PEER

    localcacert_file = Tempfile.open("cafile")
    localcacert_file << @ca_cert.to_pem
    localcacert_file.close
    localcacert_path = localcacert_file.path

    ssl_store = OpenSSL::X509::Store.new
    ssl_store.purpose = OpenSSL::X509::PURPOSE_ANY
    ssl_store.add_file(localcacert_path)
    ssl_store.flags = OpenSSL::X509::V_FLAG_CRL_CHECK_ALL|OpenSSL::X509::V_FLAG_CRL_CHECK

    crl = issue_crl([], 1, Time.now, Time.now+1600, [],
                    @cli_cert, @ca_key, OpenSSL::Digest::SHA1.new)

    ssl_store.add_crl(OpenSSL::X509::CRL.new(crl.to_pem))

    server_ctx = OpenSSL::SSL::SSLContext.new
    server_ctx.cert = @svr_cert
    server_ctx.key = @svr_key
    server_ctx.verify_mode = vflag
    server_ctx.cert_store = ssl_store

    start_server(PORT, vflag, true, server_ctx){|s, p|
      ctx = OpenSSL::SSL::SSLContext.new
      ctx.cert = @cli_cert
      ctx.key = @cli_key
      assert_raises(OpenSSL::SSL::SSLError){
        sock = TCPSocket.new("127.0.0.1", p)
        ssl = OpenSSL::SSL::SSLSocket.new(sock, ctx)
        ssl.sync_close = true
        ssl.connect
        ssl.close
      }
      localcacert_file.unlink
    }
  end

  def test_starttls
    start_server(PORT, OpenSSL::SSL::VERIFY_NONE, false){|s, p|
      sock = TCPSocket.new("127.0.0.1", p)
      ssl = OpenSSL::SSL::SSLSocket.new(sock)
      ssl.sync_close = true
      str = "x" * 1000 + "\n"
      ITERATIONS.times{
        ssl.puts(str)
        assert_equal(str, ssl.gets)
      }

      starttls(ssl)

      ITERATIONS.times{
        ssl.puts(str)
        assert_equal(str, ssl.gets)
      }

      ssl.close
    }
  end

  def test_parallel
    GC.start
    start_server(PORT, OpenSSL::SSL::VERIFY_NONE, true){|s, p|
      ssls = []
      10.times{
        sock = TCPSocket.new("127.0.0.1", p)
        ssl = OpenSSL::SSL::SSLSocket.new(sock)
        ssl.connect
        ssl.sync_close = true
        ssls << ssl
      }
      str = "x" * 1000 + "\n"
      ITERATIONS.times{
        ssls.each{|ssl|
          ssl.puts(str)
          assert_equal(str, ssl.gets)
        }
      }
      ssls.each{|ssl| ssl.close }
    }
  end

  def test_post_connection_check
    sslerr = OpenSSL::SSL::SSLError

    start_server(PORT, OpenSSL::SSL::VERIFY_NONE, true){|s, p|
      sock = TCPSocket.new("127.0.0.1", p)
      ssl = OpenSSL::SSL::SSLSocket.new(sock)
      ssl.connect
      assert_raises(sslerr){ssl.post_connection_check("localhost.localdomain")}
      assert_raises(sslerr){ssl.post_connection_check("127.0.0.1")}
      assert(ssl.post_connection_check("localhost"))
      assert_raises(sslerr){ssl.post_connection_check("foo.example.com")}
    }

    now = Time.now
    exts = [
      ["keyUsage","keyEncipherment,digitalSignature",true],
      ["subjectAltName","DNS:localhost.localdomain",false],
      ["subjectAltName","IP:127.0.0.1",false],
    ]
    @svr_cert = issue_cert(@svr, @svr_key, 4, now, now+1800, exts,
                           @ca_cert, @ca_key, OpenSSL::Digest::SHA1.new)
    start_server(PORT, OpenSSL::SSL::VERIFY_NONE, true){|s, p|
      sock = TCPSocket.new("127.0.0.1", p)
      ssl = OpenSSL::SSL::SSLSocket.new(sock)
      ssl.connect
      assert(ssl.post_connection_check("localhost.localdomain"))
      assert(ssl.post_connection_check("127.0.0.1"))
      assert_raises(sslerr){ssl.post_connection_check("localhost")}
      assert_raises(sslerr){ssl.post_connection_check("foo.example.com")}
    }

    now = Time.now
    exts = [
      ["keyUsage","keyEncipherment,digitalSignature",true],
      ["subjectAltName","DNS:*.localdomain",false],
    ]
    @svr_cert = issue_cert(@svr, @svr_key, 5, now, now+1800, exts,
                           @ca_cert, @ca_key, OpenSSL::Digest::SHA1.new)
    start_server(PORT, OpenSSL::SSL::VERIFY_NONE, true){|s, p|
      sock = TCPSocket.new("127.0.0.1", p)
      ssl = OpenSSL::SSL::SSLSocket.new(sock)
      ssl.connect
      assert(ssl.post_connection_check("localhost.localdomain"))
      assert_raises(sslerr){ssl.post_connection_check("127.0.0.1")}
      assert_raises(sslerr){ssl.post_connection_check("localhost")}
      assert_raises(sslerr){ssl.post_connection_check("foo.example.com")}
    }
  end
end

end
