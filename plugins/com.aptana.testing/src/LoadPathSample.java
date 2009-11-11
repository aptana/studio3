import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;


public class LoadPathSample {
	private final static String filename = "/Users/kevin/Documents/Workspaces/jruby_tests/com.aptana.testing/script/file.rb";
	
	private LoadPathSample()
	{
		ScriptingContainer container = new ScriptingContainer();
		
		container.runScriptlet("puts \"Hello World - inline script\"");
		container.runScriptlet(PathType.ABSOLUTE, filename);
	}
	
	public static void main(String[] args)
	{
		new LoadPathSample();
	}
}
