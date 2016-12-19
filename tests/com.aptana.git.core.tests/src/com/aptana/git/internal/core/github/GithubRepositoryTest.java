package com.aptana.git.internal.core.github;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ProcessStatus;
import com.aptana.git.core.github.IGithubPullRequest;
import com.aptana.git.core.github.IGithubRepository;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRepository;

public class GithubRepositoryTest
{

	private GithubRepository repo;
	private Mockery context;
	private GitExecutable git;
	private GithubAPI api;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		git = context.mock(GitExecutable.class);
		api = context.mock(GithubAPI.class);
	}

	@After
	public void tearDown() throws Exception
	{
		repo = null;
		context = null;
		git = null;
		api = null;
	}

	@Test
	public void testGithubRepository() throws IOException, ParseException
	{
		createRepo("test_files/repository.json");

		assertEquals(1296269L, repo.getID());
		assertEquals("Hello-World", repo.getName());
		assertEquals("octocat", repo.getOwner());
		assertEquals("octocat/Hello-World", repo.getFullName());
		assertEquals("master", repo.getDefaultBranch());
		assertEquals("git@github.com:octocat/Hello-World.git", repo.getSSHURL());
		assertFalse(repo.isPrivate());
		assertFalse(repo.isFork());
	}

	protected void createRepo(String jsonFilePath) throws IOException, ParseException
	{
		String jsonString = IOUtil.read(FileLocator.openStream(Platform.getBundle("com.aptana.git.core.tests"),
				Path.fromPortableString(jsonFilePath), false));
		JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
		repo = new GithubRepository(json)
		{
			@Override
			protected GitExecutable getGitExecutable()
			{
				return git;
			}

			@Override
			protected GithubAPI getAPI()
			{
				return api;
			}
		};
	}

	@Test
	public void testGetParentOfNonFork() throws IOException, ParseException, CoreException
	{
		createRepo("test_files/repository.json");

		IGithubRepository parent = repo.getParent();
		assertNull(parent);
	}

	@Test
	public void testGetParentOfFork() throws IOException, ParseException, CoreException
	{
		createRepo("test_files/fork.json");

		IGithubRepository parent = repo.getParent();
		assertNotNull(parent);
		assertEquals(1296269L, parent.getID());
		assertEquals("Hello-World", parent.getName());
		assertEquals("octocat", parent.getOwner());
		assertEquals("octocat/Hello-World", parent.getFullName());
		assertEquals("master", parent.getDefaultBranch());
		assertEquals("git@github.com:octocat/Hello-World.git", parent.getSSHURL());
	}

	@Test
	public void testGetSourceOfNonFork() throws IOException, ParseException, CoreException
	{
		createRepo("test_files/repository.json");

		// Returns self as source
		IGithubRepository source = repo.getSource();
		assertSame(source, repo);
	}

	@Test
	public void testGetSourceOfFork() throws IOException, ParseException, CoreException
	{
		createRepo("test_files/fork.json");
		// Returns source repo from json
		IGithubRepository source = repo.getSource();
		assertNotSame(source, repo);
		assertEquals(1296269L, source.getID());
		assertEquals("Hello-World", source.getName());
		assertEquals("octocat", source.getOwner());
		assertEquals("octocat/Hello-World", source.getFullName());
		assertEquals("master", source.getDefaultBranch());
		assertEquals("git@github.com:octocat/Hello-World.git", source.getSSHURL());
	}

	@Test
	public void testCreatePullRequest() throws IOException, ParseException, CoreException
	{
		createRepo("test_files/repository.json");
		final IGithubRepository baseRepo = repo;
		createRepo("test_files/fork.json");

		final GitRepository head = context.mock(GitRepository.class);
		final JSONObject response = new JSONObject();
		context.checking(new Expectations()
		{
			{
				oneOf(head).currentBranch();
				will(returnValue("headBranch"));

				oneOf(head).push("origin", "headBranch");
				will(returnValue(Status.OK_STATUS));
				// POST against the baseRepo
				oneOf(api).post(with("repos/octocat/Hello-World/pulls"), with(aJSONString(
						"{\"body\":\"My body.\",\"title\":\"MyTitle\",\"base\":\"baseBranch\",\"head\":\"someuser:headBranch\"}")));
				will(returnValue(response));
			}
		});

		IGithubPullRequest pr = repo.createPullRequest("MyTitle", "My body.", head, baseRepo, "baseBranch", null);
		assertNotNull(pr);
		context.assertIsSatisfied();
	}

	@Test
	public void testGetOpenPullRequests() throws IOException, ParseException, CoreException
	{
		createRepo("test_files/fork.json");
		final JSONArray array = new JSONArray();
		context.checking(new Expectations()
		{
			{
				oneOf(api).get("repos/someuser/Hello-World/pulls");
				will(returnValue(array));
			}
		});
		List<IGithubPullRequest> prs = repo.getOpenPullRequests();
		assertEquals(0, prs.size());
		context.assertIsSatisfied();
	}

	@Test
	public void testGetBranches() throws IOException, ParseException
	{
		createRepo("test_files/fork.json");
		context.checking(new Expectations()
		{
			{
				oneOf(git).runInBackground(null, "ls-remote", "--heads", "git@github.com:someuser/Hello-World.git");
				will(returnValue(new ProcessStatus(0,
						"5fe978a5381f1fbad26a80e682ddd2a401966740        refs/heads/master\n"
								+ "c781a84b5204fb294c9ccc79f8b3baceeb32c061        refs/heads/pu\n"
								+ "b1d096f2926c4e37c9c0b6a7bf2119bedaa277cb        refs/heads/rc\n",
						"")));
			}
		});
		Set<String> branches = repo.getBranches();
		assertEquals(3, branches.size());
		assertTrue(branches.contains("master"));
		assertTrue(branches.contains("pu"));
		assertTrue(branches.contains("rc"));
		context.assertIsSatisfied();
	}

	@Test
	public void testGetForks() throws IOException, ParseException, CoreException
	{
		createRepo("test_files/fork.json");

		// let's return the non-forked repository as the only fork...
		final JSONArray array = new JSONArray();
		String jsonString = IOUtil.read(FileLocator.openStream(Platform.getBundle("com.aptana.git.core.tests"),
				Path.fromPortableString("test_files/repository.json"), false));
		JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
		array.add(json);
		context.checking(new Expectations()
		{
			{
				oneOf(api).get("repos/someuser/Hello-World/forks");
				will(returnValue(array));
			}
		});
		List<IGithubRepository> forks = repo.getForks();
		assertEquals(1, forks.size());
		IGithubRepository fork = forks.get(0);
		assertEquals(1296269L, fork.getID());
		assertEquals("Hello-World", fork.getName());
		assertEquals("octocat", fork.getOwner());
		assertEquals("octocat/Hello-World", fork.getFullName());
		assertEquals("master", fork.getDefaultBranch());
		assertEquals("git@github.com:octocat/Hello-World.git", fork.getSSHURL());
		assertFalse(fork.isPrivate());
		assertFalse(fork.isFork());
		context.assertIsSatisfied();
	}

	private static class JSONStringMatcher extends TypeSafeMatcher<String>
	{
		private JSONObject json;

		public JSONStringMatcher(String json) throws ParseException
		{
			this.json = (JSONObject) new JSONParser().parse(json);
		}

		public boolean matchesSafely(String s)
		{
			try
			{
				JSONObject obj = (JSONObject) new JSONParser().parse(s);
				return obj.equals(json);
			}
			catch (ParseException e)
			{
				return false;
			}
		}

		public void describeTo(Description description)
		{
			description.appendText("a JSON string equivalent to ").appendValue(json.toJSONString());
		}
	}

	@Factory
	public static Matcher<String> aJSONString(String json) throws ParseException
	{
		return new JSONStringMatcher(json);
	}

}
