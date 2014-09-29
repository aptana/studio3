project 'example with execute' do

  build do
    execute( :phase => :validate ) do |ctx|
      p ctx.project
    end
    execute( :second, :phase => :validate ) do |ctx|
      p ctx.project
    end
    execute( :third, :validate ) do |ctx|
      p ctx.project
    end
    phase :validate do
      execute( :id => :forth ) do |ctx|
        p ctx.project
      end
    end
  end
  phase :validate do
    execute( :id => :fifth ) do |ctx|
      p ctx.project
    end
  end
end
