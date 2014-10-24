# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Type, '.includes' do
  subject { object.includes(*members) }

  let(:object) { Class.new(described_class) }

  context 'with a non-empty list' do
    let(:member)  { Object.new       }
    let(:members) { [member, member] }

    it_should_behave_like 'a command method'

    it 'adds a constraint that returns true for a valid member' do
      should include(member)
    end

    it 'adds a constraint that returns false for an invalid member' do
      should_not include(nil)
    end

    it 'freezes the members' do
      expect { subject }.to change(member, :frozen?).from(false).to(true)
    end

    it 'removes duplicate members' do
      expect(member).to receive(:freeze).and_return(member)
      expect(member).to receive(:hash).exactly(3).times.and_return(1)
      subject
      expect(object).to include(member)
    end
  end

  context 'with an empty set' do
    let(:members) { [] }

    it_should_behave_like 'a command method'

    it 'adds a constraint that returns false' do
      should_not include(Object.new)
    end
  end
end
