package com.aptana.js.core.inferencing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CommonJSResolverTest.class, ConstructorInferencingTest.class, DocumentationTest.class,
		DynamicTypeInferencingTest.class, FunctionInferencingTest.class, InferencingBugsTest.class,
		JSTypeUtilTest.class, ObjectInferencingTest.class, OperatorInferencingTest.class,
		PrimitiveInferencingTest.class, RecursiveInferencingTest.class, })
public class CoreInferencingTests
{
}
