# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Type, '.constraint' do
  let(:object) { Class.new(described_class) }

  let(:callable) { ->(number) { number > 1 } }
  let(:other)    { ->(number) { number < 3 } }

  context 'with no arguments or block' do
    subject { object.constraint }

    it { should respond_to(:call) }
    it { should be(Tautology)     }
  end

  context 'with a callable object' do
    subject { object.constraint(callable) }

    it_should_behave_like 'a command method'

    its(:constraint) { should respond_to(:call) }

    it 'creates a constraint that matches a number > 1' do
      expect(object).to include(1)
      expect(object).to include(2)
      expect(object).to include(3)
      subject
      expect(object).to_not include(1)
      expect(object).to     include(2)
      expect(object).to     include(3)
    end

    context 'with another constraint' do
      subject { super().constraint(other) }

      it_should_behave_like 'a command method'

      its(:constraint) { should respond_to(:call) }

      it 'creates a constraint that matches a number > 1 and < 3' do
        expect(object).to include(1)
        expect(object).to include(2)
        expect(object).to include(3)
        subject
        expect(object).to_not include(1)
        expect(object).to     include(2)
        expect(object).to_not include(3)
      end
    end
  end

  context 'with a block' do
    subject { object.constraint(&callable) }

    it_should_behave_like 'a command method'

    its(:constraint) { should respond_to(:call) }

    it 'creates a constraint that matches a number > 1' do
      expect(object).to include(1)
      expect(object).to include(2)
      expect(object).to include(3)
      subject
      expect(object).to_not include(1)
      expect(object).to     include(2)
      expect(object).to     include(3)
    end

    context 'with another constraint' do
      subject { super().constraint(&other) }

      it_should_behave_like 'a command method'

      its(:constraint) { should respond_to(:call) }

      it 'creates a constraint that matches a number > 1 and < 3' do
        expect(object).to include(1)
        expect(object).to include(2)
        expect(object).to include(3)
        subject
        expect(object).to_not include(1)
        expect(object).to     include(2)
        expect(object).to_not include(3)
      end
    end
  end
end
