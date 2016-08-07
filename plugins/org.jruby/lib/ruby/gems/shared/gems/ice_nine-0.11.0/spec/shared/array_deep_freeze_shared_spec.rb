# encoding: utf-8

shared_examples 'IceNine::Freezer::Array.deep_freeze' do
  it_behaves_like 'IceNine::Freezer::Object.deep_freeze'

  it 'freezes each entry' do
    expect(subject.select(&:frozen?)).to eql(subject.to_a)
  end
end
