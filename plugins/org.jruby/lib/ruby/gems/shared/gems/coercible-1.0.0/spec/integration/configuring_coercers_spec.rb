require 'spec_helper'

describe "Configuring coercers" do
  it "allows to configure coercers" do
    coercer = Coercer.new do |config|
      config.string.boolean_map = { 'yup' => true, 'nope' => false }
    end

    expect(coercer[String].to_boolean('yup')).to be(true)
    expect(coercer[String].to_boolean('nope')).to be(false)

    expect { coercer[String].to_boolean('1') }.to raise_error
  end
end
