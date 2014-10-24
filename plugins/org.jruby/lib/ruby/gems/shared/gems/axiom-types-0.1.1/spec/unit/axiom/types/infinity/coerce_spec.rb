# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Infinity, '#coerce' do
  subject { object.coerce(other) }

  let(:object) { described_class.instance }

  [
    1,                # Fixnum
    2**63,            # Bignum
    1.0,              # Float
    Rational(1, 1),   # Rational
    BigDecimal('1'),  # BigDecimal
  ].each do |number|
    context "when other is a #{number.class}" do
      let(:other) { number }

      it 'coerces into an array containing inverse and self' do
        should eql([Axiom::Types::NegativeInfinity.instance, object])
      end

      it 'coerces when comparing' do
        expect(other).to     be < object
        expect(other).to_not be == object
      end
    end
  end

  context 'when other is a Float::INFINITY' do
    let(:other) { Float::INFINITY }

    it 'coerces into an array containing inverse and self' do
      should eql([Axiom::Types::Infinity.instance, object])
    end

    it 'coerces when comparing' do
      expect(other).to_not be < object
      expect(other).to     be == object
    end
  end

  context 'when other is a -Float::INFINITY' do
    let(:other) { -Float::INFINITY }

    it 'coerces into an array containing inverse and self' do
      should eql([Axiom::Types::NegativeInfinity.instance, object])
    end

    it 'coerces when comparing' do
      expect(other).to     be < object
      expect(other).to_not be == object
    end
  end

  context 'when other is not a number' do
    let(:other) { 'string' }

    specify do
      expect { subject }.to raise_error(TypeError, 'String cannot be coerced')
    end
  end
end
