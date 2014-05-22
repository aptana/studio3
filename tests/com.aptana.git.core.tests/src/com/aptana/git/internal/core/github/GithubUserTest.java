package com.aptana.git.internal.core.github;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.git.core.github.IGithubManager;
import com.aptana.git.core.github.IGithubOrganization;
import com.aptana.git.core.github.IGithubRepository;

public class GithubUserTest
{

	private GithubUser user;
	private Mockery context;
	private GithubAPI api;
	private GithubManager manager;
	private IGithubRepository repo;
	private IGithubOrganization org;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		api = context.mock(GithubAPI.class);
		manager = context.mock(GithubManager.class);
		repo = context.mock(IGithubRepository.class);
		org = context.mock(IGithubOrganization.class);
		user = new GithubUser("username", "password")
		{
			@Override
			protected IGithubManager getGithubManager()
			{
				return manager;
			}

			@Override
			protected GithubAPI getAPI()
			{
				return api;
			}

			@Override
			protected IGithubOrganization createOrganization(JSONObject json)
			{
				return org;
			}

			@Override
			protected IGithubRepository createRepository(JSONObject json)
			{
				return repo;
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		context = null;
		api = null;
		manager = null;
		repo = null;
		org = null;
	}

	@Test
	public void testGetRepoByName() throws IOException, ParseException, CoreException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(manager).getRepo("username", "repoName");
				will(returnValue(repo));
			}
		});

		IGithubRepository repo = user.getRepo("repoName");
		assertNotNull(repo);
		context.assertIsSatisfied();
	}

	@Test
	public void testGetRepos() throws IOException, ParseException, CoreException
	{
		final JSONArray array = new JSONArray();
		context.checking(new Expectations()
		{
			{
				oneOf(api).get("user/repos");
				will(returnValue(array));
			}
		});

		List<IGithubRepository> repos = user.getRepos();
		assertNotNull(repos);
		assertEquals(0, repos.size());
		context.assertIsSatisfied();
	}

	@Test
	public void testGetOrgs() throws IOException, ParseException, CoreException
	{
		final JSONArray array = new JSONArray();
		context.checking(new Expectations()
		{
			{
				oneOf(api).get("user/orgs");
				will(returnValue(array));
			}
		});

		Set<IGithubOrganization> orgs = user.getOrganizations();
		assertNotNull(orgs);
		assertEquals(0, orgs.size());
		context.assertIsSatisfied();
	}

	@Test
	public void testGetAllRepos() throws IOException, ParseException, CoreException
	{
		// pretend to have two repos for user
		final JSONArray array = new JSONArray();
		array.add(new JSONObject());
		array.add(new JSONObject());

		// pretend to have one org
		final JSONArray orgsArray = new JSONArray();
		orgsArray.add(new JSONObject());
		context.checking(new Expectations()
		{
			{
				// first get user's repos
				oneOf(api).get("user/repos");
				will(returnValue(array));

				// then orgs
				oneOf(api).get("user/orgs");
				will(returnValue(orgsArray));

				// then ask for the repos for each org. We can't test mutiple orgs, because they're contained in a set
				// and our mock is one object
				oneOf(org).getRepos();
				will(returnValue(CollectionsUtil.newList(repo)));
			}
		});

		List<IGithubRepository> repos = user.getAllRepos();
		assertNotNull(repos);
		assertEquals(3, repos.size()); // 2 from user, 1 from org
		context.assertIsSatisfied();
	}
}