require File.expand_path( 'spec_helper', File.dirname( __FILE__ ) )
require 'maven/tools/jarfile'

class Container

  attr_reader :artifacts, :repositories

  def initialize
    @artifacts = []
    @repositories = []
  end
  
  def add_artifact(a)
    @artifacts << a
  end

  def add_repository(name, url)
    @repositories << name
  end
end

describe Maven::Tools::Jarfile do

  let(:workdir) { 'pkg' }
  let(:jfile) { File.join(workdir, 'tmp-jarfile') }
  let(:jfile_lock) { jfile + ".lock"}
  let(:container) { Container.new }
  subject { Maven::Tools::Jarfile.new(jfile) }

  before do
    FileUtils.mkdir_p workdir
    Dir[File.join(workdir, "tmp*")].each { |f| FileUtils.rm_f f }
  end

  after do
    FileUtils.rm_rf(File.join(workdir, "tmp-*"))
  end

  it 'generates lockfile' do
    subject.generate_lockfile(%w( a b c d e f ruby.bundler:bla))
    File.read(jfile_lock).must_equal <<-EOF
a
b
c
d
e
f
EOF
  end

  it 'check locked coordinate' do
    File.open(jfile_lock, 'w') do |f|
      f.write <<-EOF
a:b:pom:3
a:c:jar:1
EOF
    end
    subject.locked.must_equal ["a:b:pom:3", "a:c:jar:1"]
    subject.locked?("a:b:pom:321").must_equal true
    subject.locked?("a:b:jar:321").must_equal true
    subject.locked?("a:d:jar:432").must_equal false
  end

  it 'populate repositories' do
    File.open(jfile, 'w') do |f|
      f.write <<-EOF
repository :first, "http://example.com/repo"
source 'second', "http://example.org/repo"
source "http://example.org/repo/3"
EOF
    end
    subject.populate_unlocked container
    container.repositories.size.must_equal 3
    container.artifacts.size.must_equal 0
    container.repositories[0].must_equal "first"
    container.repositories[1].must_equal "second"
    container.repositories[2].must_equal "http://example.org/repo/3"
  end

  it 'populate artifacts without locked' do
    File.open(jfile, 'w') do |f|
      f.write <<-EOF
jar 'a:b', '123'
pom 'x:y', '987'
EOF
    end
    subject.populate_unlocked container
    container.repositories.size.must_equal 0
    container.artifacts.size.must_equal 2
    container.artifacts[0].to_s.must_equal "a:b:jar:123"
    container.artifacts[1].to_s.must_equal "x:y:pom:987"
  end

  it 'populate artifacts with locked' do
    File.open(jfile, 'w') do |f|
      f.write <<-EOF
jar 'a:b', '123'
pom 'x:y', '987'
EOF
    end
    File.open(jfile_lock, 'w') do |f|
      f.write <<-EOF
a:b:jar:432
EOF
    end
    
    subject.populate_unlocked container
    container.repositories.size.must_equal 0
    container.artifacts.size.must_equal 1
    container.artifacts[0].to_s.must_equal "x:y:pom:987"
  end

  it 'populate locked artifacts' do
    File.open(jfile_lock, 'w') do |f|
      f.write <<-EOF
a:b:jar:432
EOF
    end
    
    subject.populate_locked container
    container.repositories.size.must_equal 0
    container.artifacts.size.must_equal 1
    container.artifacts[0].to_s.must_equal "a:b:jar:432"
  end
end
