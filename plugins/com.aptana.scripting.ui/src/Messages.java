import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$
	public static String EarlyStartup_ERROR_PREFIX;
	public static String EarlyStartup_INFO_PREFIX;
	public static String EarlyStartup_SCRIPTING_CONSOLE_NAME;
	public static String EarlyStartup_TRACE_PREFIX;
	public static String EarlyStartup_WARNING_PREFIX;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
