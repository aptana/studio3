# encoding: utf-8

shared_examples 'IceNine::Freezer::Range.deep_freeze' do
  it_behaves_like 'IceNine::Freezer::Object.deep_freeze'

  it 'freeze the first element' do
    expect(subject.begin).to be_frozen
  end

  it 'freeze the last element' do
    expect(subject.end).to be_frozen
  end
end
