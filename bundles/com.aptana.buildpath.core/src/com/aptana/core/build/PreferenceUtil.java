package com.aptana.core.build;

import java.text.MessageFormat;

import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.core.internal.build.BuildParticipantWorkingCopy;
import com.aptana.core.util.StringUtil;

/**
 * Just a simple utility class for hanging duplicate code between {@link BuildParticipantWorkingCopy} and
 * {@link AbstractBuildParticipant}
 * 
 * @author cwilliams
 */
public class PreferenceUtil
{

	public static String getFiltersKey(String participantId)
	{
		return MessageFormat.format("{0}_filters", participantId); //$NON-NLS-1$
	}

	public static String getEnablementPreferenceKey(String participantId, BuildType type)
	{
		return MessageFormat.format("{0}_{1}_enabled", participantId, type.name().toLowerCase()); //$NON-NLS-1$
	}

	public static String serializeFilters(String[] filters)
	{
		return StringUtil.join(AbstractBuildParticipant.FILTER_DELIMITER, filters);
	}
}
