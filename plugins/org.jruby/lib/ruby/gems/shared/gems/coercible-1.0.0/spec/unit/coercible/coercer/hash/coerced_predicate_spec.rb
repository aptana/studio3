require 'spec_helper'

describe Coercer::Hash, '#coerced?' do
  let(:object) { described_class.new }

  it_behaves_like 'Coercible::Coercer#coerced?' do
    let(:primitive_value)     { Hash.new }
    let(:non_primitive_value) { 'other' }
  end
end
