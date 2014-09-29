# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Encodable, '#finalize' do
  subject { object.finalize }

  let(:object) { Class.new(Axiom::Types::Type).extend(described_class) }

  # Test if Symbol encoding is supported
  let(:symbol_encoding) do
    !RUBY_PLATFORM.include?('java') && begin
      encoding = Encoding::UTF_32BE
      ''.force_encoding(encoding).to_sym.encoding.equal?(encoding)
    end
  end

  context 'when an ascii compatible encoding (UTF-8) is used' do
    it_should_behave_like 'a command method'
    it_should_behave_like 'an idempotent method'

    it { should be_frozen }

    its(:constraint) { should be_frozen }

    Encoding.list.each do |encoding|
      if encoding.equal?(Encoding::UTF_8)
        string = '𝒜wesome'.force_encoding(encoding)
        it "adds a constraint that returns true for #{encoding} encoding" do
          should include(string)
          should include(string.to_sym) if symbol_encoding
        end
      elsif encoding.ascii_compatible?
        string = ''.force_encoding(encoding)
        it "adds a constraint that returns true for #{encoding} encoding" do
          should include(string)
          should include(string.to_sym) if symbol_encoding
        end
      else
        string = ''.force_encoding(encoding)
        it "adds a constraint that returns false for #{encoding} encoding" do
          should_not include(string)
          should_not include(string.to_sym) if symbol_encoding
        end
      end
    end
  end

  context 'when an non-ascii compatible encoding (UTF-16BE) is used' do
    before do
      object.encoding Encoding::UTF_16BE
    end

    it_should_behave_like 'a command method'
    it_should_behave_like 'an idempotent method'

    it { should be_frozen }

    its(:constraint) { should be_frozen }

    Encoding.list.each do |encoding|
      if encoding.equal?(Encoding::UTF_16BE)
        string = '𝒜wesome'.force_encoding(encoding)
        it "adds a constraint that returns true for #{encoding} encoding" do
          should include(string)
          should include(string.to_sym) if symbol_encoding
        end
      else
        string = ''.force_encoding(encoding)
        it "adds a constraint that returns false for #{encoding} encoding" do
          should_not include(string)
          should_not include(string.to_sym) if symbol_encoding
        end
      end
    end
  end
end
