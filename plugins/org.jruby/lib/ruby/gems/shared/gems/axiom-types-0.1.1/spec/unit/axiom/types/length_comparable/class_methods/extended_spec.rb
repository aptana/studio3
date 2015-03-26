# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::LengthComparable, '.extended' do
  subject { object.extend(described_class) }

  let(:object) { Class.new(Axiom::Types::Type) }

  it 'delegates to the ancestor' do
    # RSpec will reset stubbed methods after the example. A normal expectation
    # causes a SystemStackError to be thrown, so we stub it first so that
    # RSpec tracks the original method (if any), then we add our own stub that
    # actually works, and finally when the example finishes RSpec will reset
    # the Module#extended method back to it's original state.
    allow_any_instance_of(Module).to receive(:extended).with(object)

    delegated_ancestor = false
    Module.send(:undef_method, :extended)
    Module.send(:define_method, :extended) { |_| delegated_ancestor = true }
    expect { subject }.to change { delegated_ancestor }.from(false).to(true)
  end

  it 'adds minimum_length method' do
    expect { subject }.to change { object.respond_to?(:minimum_length) }
      .from(false).to(true)
  end

  it 'adds maxumum_length method' do
    expect { subject }.to change { object.respond_to?(:maximum_length) }
      .from(false).to(true)
  end

  it 'sets the default minimum_length' do
    expect(subject.minimum_length)
      .to equal(Axiom::Types::Infinity.instance)
  end

  it 'sets the default maximum_length' do
    expect(subject.maximum_length)
      .to equal(Axiom::Types::NegativeInfinity.instance)
  end
end
