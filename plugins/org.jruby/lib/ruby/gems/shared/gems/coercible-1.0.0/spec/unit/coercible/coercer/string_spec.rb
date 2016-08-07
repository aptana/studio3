require 'spec_helper'

describe Coercer::String do
  it_behaves_like 'Configurable' do
    describe '.config_name' do
      subject { described_class.config_name }

      it { should be(:string) }
    end
  end
end
