require 'spec_helper'

describe Coercer::Time, '#coerced?' do
  let(:object) { described_class.new }

  it_behaves_like 'Coercible::Coercer#coerced?' do
    let(:primitive_value)     { Time.now }
    let(:non_primitive_value) { 'other' }
  end
end
