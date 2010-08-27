module Ruble

  # Wraps an Eclipse Job and allows us to use a block to implement the one abstract method we need
  class Job < org.eclipse.core.runtime.jobs.Job
    def initialize(name, &blk)
      super(name)
      @block = blk
    end
    
    def run(monitor)
      @block.call(monitor)
      return org.eclipse.core.runtime.Status::OK_STATUS
    end
  end
  
  def Ruble.call_with_progress( args, &block )
    title           = args[:title] || 'Progress'
    message         = args[:message] || args[:summary] || 'Frobbing the widget...'
    details         = args[:details] || ''
    cancel_proc     = args[:cancel]
    indeterminate   = args[:indeterminate]
    job = Ruble::Job.new(title, &block)
    job.schedule
    job.join
  end
  
  def Ruble.run(title, &blk)
#    if in_ui_thread?
      blk.call(org.eclipse.core.runtime.NullProgressMonitor.new)
#    else
#      job = Ruble::Job.new(title, &blk)
#      job.schedule
#      job.join
#    end
  end
end