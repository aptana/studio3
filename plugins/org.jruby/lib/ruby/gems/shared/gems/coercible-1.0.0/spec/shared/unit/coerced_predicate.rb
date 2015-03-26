shared_examples_for 'Coercible::Coercer#coerced?' do
  context "with a primitive value" do
    subject { object.coerced?(primitive_value) }

    it { should be(true) }
  end

  context "with a non-primitive value" do
    subject { object.coerced?(non_primitive_value) }

    it { should be(false) }
  end
end
