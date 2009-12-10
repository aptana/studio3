module RadRails
  class ScopeSelector
    
    def initialize(selector)
      case selector
        when String then @selector = selector
        when Array then
          ors = selector.map do |orExpr|
            case orExpr
              when String then orExpr
              when Symbol then orExpr.to_s.tr("_", ".")
              when Array then
                ands = orExpr.map do |andExpr|
                  case andExpr
                    when String then andExpr
                    when Symbol then andExpr.to_s.tr("_", ".")
                    else "?"
                  end
                end
                
                ands.join " "
            end
          end
          @selector = ors.join ", "
        else @selector = "?"
      end
    end
    
    def to_s
      @selector
    end
  end
end