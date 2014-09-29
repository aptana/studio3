# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Options, '#accept_options' do
  subject { object.accept_options(*new_options) }

  let(:object)     { Class.new { extend Options, DescendantsTracker } }
  let(:descendant) { Class.new(object)                                }

  context 'with valid options' do
    let(:new_options) { [:primitive, :coerce_method] }

    it_should_behave_like 'a command method'
    it_should_behave_like 'an idempotent method'

    it 'adds methods to the object' do
      expect(object).to_not respond_to(*new_options)
      subject
      expect(object).to respond_to(*new_options)
    end

    it 'defines the instance variables for the options' do
      subject
      expect(object.instance_variable_defined?(:@primitive)).to be(true)
      expect(object.instance_variable_defined?(:@coerce_method)).to be(true)
    end

    it 'adds methods to the object that can set a value' do
      subject
      expect { object.primitive(Class) }
        .to change(object, :primitive).from(nil).to(Class)
      expect { object.coerce_method(:to_class) }
        .to change(object, :coerce_method).from(nil).to(:to_class)
    end

    context 'with the descendant class' do
      let(:default) { ::String }

      def force_inherit
        descendant
      end

      context 'option added before inherited' do
        before do
          subject
          expect(object.primitive(default)).to be(object)
          force_inherit
        end

        it 'is idempotent' do
          expect(descendant.accept_options(:primitive)).to be(descendant)
        end

        it 'adds the method to the descendants' do
          expect(descendant).to respond_to(*new_options)
        end

        it 'sets the descendant defaults' do
          expect(descendant.primitive).to be(default)
        end

        it 'adds methods to the descendant that can set a value' do
          descendant.primitive(::Symbol)
          expect(descendant.primitive).to be(::Symbol)
        end
      end

      context 'option added after inherited' do
        before do
          force_inherit
          subject
          expect(object.primitive(default)).to be(object)
        end

        it 'is idempotent' do
          expect(descendant.accept_options(:primitive)).to be(descendant)
        end

        it 'adds the method to the descendants' do
          expect(descendant).to respond_to(*new_options)
        end

        it 'does not set the descendant defaults' do
          expect(descendant.primitive).to be_nil
        end

        it 'adds methods to the descendant that can set a value' do
          descendant.primitive(::Symbol)
          expect(descendant.primitive).to be(::Symbol)
        end
      end
    end
  end

  context 'with an option that conflicts with an existing method' do
    let(:new_options) { [:name] }

    specify do
      expect { subject }.to raise_error(
        Axiom::Types::Options::ReservedMethodError,
        'method named `:name` is already defined'
      )
    end
  end
end
