require 'spec_helper'

describe Coercer::Configurable, '.config' do
  subject { object.config(&block) }

  let(:object)  {
    Class.new {
      extend Coercer::Configurable, Options
      config_keys [ :one, :two ]
    }
  }

  let(:block)               { Proc.new { |config| config.test } }
  let(:configuration)       { mock('configuration') }
  let(:configuration_class) { mock('configuration_class') }

  before do
    object.stub!(:configuration_class => configuration_class)
    configuration_class.should_receive(:build).with(object.config_keys).
      and_return(configuration)
    configuration.should_receive(:test)
  end

  it { should be(configuration) }
end
