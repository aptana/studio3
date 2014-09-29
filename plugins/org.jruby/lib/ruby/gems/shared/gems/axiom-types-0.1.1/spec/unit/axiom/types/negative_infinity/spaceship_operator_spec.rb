# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::NegativeInfinity, '#<=>' do
  subject { object <=> other }

  let(:object) { described_class.instance }

  [
    1,                # Fixnum
    2**63,            # Bignum
    1.0,              # Float
    Rational(1, 1),   # Rational
    BigDecimal('1'),  # BigDecimal
  ].each do |number|
    context "when other object is a #{number.class}" do
      let(:other) { number }

      it { should be(-1) }

      it 'is symmetric' do
        should be(-(other <=> object))
      end
    end
  end

  context 'when the other object is infinity' do
    let(:other) { Axiom::Types::Infinity.instance }

    it { should be(-1) }

    it 'is symmetric' do
      should be(-(other <=> object))
    end
  end

  context 'when the other object is negative infinity' do
    let(:other) { object }

    it { should be(0) }

    it 'is symmetric' do
      should be(other <=> object)
    end
  end

  context 'when the other object is -Float::INFINITY' do
    let(:other) { -Float::INFINITY }

    it { should be(0) }

    it 'is symmetric' do
      should be(other <=> object)
    end
  end

  context 'when the other object is not comparable' do
    let(:other) { 'string' }

    it { should be_nil }

    it 'is symmetric' do
      should be(other <=> object)
    end
  end
end
