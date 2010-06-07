files = File.join(File.dirname(__FILE__), 'openssl', 'test_*.rb')
Dir.glob(files).sort.each do |tc|
  require tc
end
