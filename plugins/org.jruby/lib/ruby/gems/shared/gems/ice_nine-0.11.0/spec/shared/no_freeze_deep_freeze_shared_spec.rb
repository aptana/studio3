# encoding: utf-8

shared_examples 'IceNine::Freezer::NoFreeze.deep_freeze' do
  before do
    value.instance_eval { @a = '1' } unless value.frozen?
  end

  it 'returns the object' do
    should be(value)
  end

  it 'does not freeze the object' do
    expect { subject }.to_not change(value, :frozen?).from(false)
  end

  it 'does not freeze instance variables' do
    expect(subject.instance_variable_get(:@a)).to_not be_frozen
  end
end
