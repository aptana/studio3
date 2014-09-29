# encoding: utf-8

shared_examples 'IceNine::Freezer::Hash.deep_freeze' do
  it_behaves_like 'IceNine::Freezer::Object.deep_freeze'

  it 'freezes each key' do
    expect(subject.keys.select(&:frozen?)).to eql(subject.keys)
  end

  it 'freezes each value' do
    expect(subject.values.select(&:frozen?)).to eql(subject.values)
  end

  if RUBY_VERSION >= '1.9' && RUBY_ENGINE == 'rbx'
    it 'does not freeze the state' do
      expect(subject.instance_variable_get(:@state)).to_not be_frozen
    end

    it 'does not freeze the entries' do
      expect(subject.instance_variable_get(:@entries)).to_not be_frozen
    end
  end
end
