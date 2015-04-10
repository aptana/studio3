# encoding: utf-8

shared_examples 'IceNine.deep_freeze' do

  context 'with an Object' do
    let(:value) { Object.new }

    before do
      value.instance_eval { @a = '1' }
    end

    it 'returns the object' do
      should be(value)
    end

    it 'freezes the object' do
      expect { subject }.to change(value, :frozen?).from(false).to(true)
    end

    it 'freezes the instance variables in the Object' do
      expect(subject.instance_variable_get(:@a)).to be_frozen
    end

    context 'with a circular reference' do
      before do
        value.instance_eval { @self = self }
      end

      it 'returns the object' do
        should be(value)
      end

      it 'freezes the object' do
        expect { subject }.to change(value, :frozen?).from(false).to(true)
      end

      it 'freezes the instance variables in the Object' do
        expect(subject.instance_variable_get(:@a)).to be_frozen
      end
    end
  end

  context 'with an Array' do
    let(:value) { %w[a] }

    it 'returns the object' do
      should be(value)
    end

    it 'freezes the object' do
      expect { subject }.to change(value, :frozen?).from(false).to(true)
    end

    it 'freezes each element in the Array' do
      expect(subject.select(&:frozen?)).to eql(subject)
    end

    context 'with a circular reference' do
      before do
        value << value
      end

      it 'returns the object' do
        should be(value)
      end

      it 'freezes the object' do
        expect { subject }.to change(value, :frozen?).from(false).to(true)
      end

      it 'freezes each element in the Array' do
        expect(subject.select(&:frozen?)).to eql(subject)
      end
    end
  end

  context 'with a Hash' do
    let(:value) { { Object.new => Object.new } }

    it 'returns the object' do
      should be(value)
    end

    it 'freezes the object' do
      expect { subject }.to change(value, :frozen?).from(false).to(true)
    end

    it 'freezes each key in the Hash' do
      expect(subject.keys.select(&:frozen?)).to eql(subject.keys)
    end

    it 'freezes each value in the Hash' do
      expect(subject.values.select(&:frozen?)).to eql(subject.values)
    end

    context 'with a circular reference' do
      before do
        value[value] = value
      end

      it 'returns the object' do
        should be(value)
      end

      it 'freezes the object' do
        expect { subject }.to change(value, :frozen?).from(false).to(true)
      end

      it 'freezes each key in the Hash' do
        expect(subject.keys.select(&:frozen?)).to eql(subject.keys)
      end

      it 'freezes each value in the Hash' do
        expect(subject.values.select(&:frozen?)).to eql(subject.values)
      end
    end
  end

  context 'with a Range' do
    let(:value) { 'a'..'z' }

    it 'returns the object' do
      should be(value)
    end

    it 'freezes the object' do
      expect { subject }.to change(value, :frozen?).from(false).to(true)
    end

    it 'freeze the first object in the Range' do
      expect(subject.begin).to be_frozen
    end

    it 'freeze the last object in the Range' do
      expect(subject.end).to be_frozen
    end
  end

  context 'with a String' do
    let(:value) { '' }

    before do
      value.instance_eval { @a = '1' }
    end

    it 'returns the object' do
      should be(value)
    end

    it 'freezes the object' do
      expect { subject }.to change(value, :frozen?).from(false).to(true)
    end

    it 'freezes the instance variables in the String' do
      expect(subject.instance_variable_get(:@a)).to be_frozen
    end

    context 'with a circular reference' do
      before do
        value.instance_eval { @self = self }
      end

      it 'returns the object' do
        should be(value)
      end

      it 'freezes the object' do
        expect { subject }.to change(value, :frozen?).from(false).to(true)
      end

      it 'freezes the instance variables in the String' do
        expect(subject.instance_variable_get(:@a)).to be_frozen
      end
    end
  end

  context 'with a Struct' do
    let(:value) { klass.new(%w[ 1 2 ]) }
    let(:klass) { Struct.new(:a)       }

    it 'returns the object' do
      should be(value)
    end

    it 'freezes the object' do
      expect { subject }.to change(value, :frozen?).from(false).to(true)
    end

    it 'freezes each value in the Struct' do
      expect(subject.values.select(&:frozen?)).to eql(subject.values)
    end

    context 'with a circular reference' do
      before do
        value.a = value
      end

      it 'returns the object' do
        should be(value)
      end

      it 'freezes the object' do
        expect { subject }.to change(value, :frozen?).from(false).to(true)
      end

      it 'freezes each value in the Struct' do
        expect(subject.values.select(&:frozen?)).to eql(subject.values)
      end
    end
  end

  context 'with an SimpleDelegator' do
    let(:value) { SimpleDelegator.new(nil) }

    before do
      value.instance_eval { @a = '1' }
    end

    it 'returns the object' do
      should be(value)
    end

    it 'freezes the object' do
      expect { subject }.to change(value, :frozen?).from(false).to(true)
    end

    it 'freezes the instance variables in the SimpleDelegator' do
      expect(subject.instance_variable_get(:@a)).to be_frozen
    end

    context 'with a circular reference' do
      before do
        value.instance_eval { @self = self }
      end

      it 'returns the object' do
        should be(value)
      end

      it 'freezes the object' do
        expect { subject }.to change(value, :frozen?).from(false).to(true)
      end

      it 'freezes the instance variables in the SimpleDelegator' do
        expect(subject.instance_variable_get(:@a)).to be_frozen
      end
    end
  end

  [0.0, 0, 0x7fffffffffffffff, true, false, nil, :symbol].each do |value|
    context "with a #{value.class}" do
      let(:value) { value }

      it 'returns the object' do
        should be(value)
      end

      it 'does not freeze the object' do
        expect { subject }.to_not change(value, :frozen?).from(false)
      end
    end
  end
end
