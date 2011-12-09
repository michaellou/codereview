/**
 * 
 */
package com.dianping.codereview.maven;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

import com.dianping.codereview.FormatInvalidException;
import com.dianping.codereview.maven.domain.Dependency;
import com.dianping.codereview.maven.domain.Pom;

/**
 * @author sean.wang
 * @since Nov 7, 2011
 */
public class MavenReviewerTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 搜索被依赖关系 
	 * 
	 * @throws SVNException
	 * @throws InterruptedException
	 * @throws FormatInvalidException
	 */
	@Test
	public void testSearchBeDepend() throws SVNException, InterruptedException, FormatInvalidException {
		MavenReviewer maven = new MavenReviewer();
		maven.setSvnUrl("http://192.168.8.45:81");
		maven.setUsername("hawk");
		maven.setPassword("123456");
		maven.setSvnRootDir("/svn/dianping/dianping/group");
		maven.init();
		while (true) {
			if (maven.isFirstInit()) {
				break;
			}
			Thread.sleep(1000);
		}
		List<Dependency> bedeps = maven.searchBeDepended("h", null);
		for (Dependency bedep : bedeps) {
			Pom pom = bedep.getSearchedPom();
			System.out.println(bedep.getArtifactId() + ":" + bedep.getVersion() + ":" + pom.getArtifactId() + ":" + pom.getVersion());
		}
		System.out.println();
		bedeps = maven.searchBeDepended("h", "0.2.2");
		for (Dependency bedep : bedeps) {
			Pom pom = bedep.getSearchedPom();
			System.out.println(bedep.getArtifactId() + ":" + bedep.getVersion() + ":" + pom.getArtifactId() + ":" + pom.getVersion());
		}
		System.out.println();
		bedeps = maven.searchBeDepended("hawk-client", "0.2.2");
		for (Dependency bedep : bedeps) {
			Pom pom = bedep.getSearchedPom();
			System.out.println(bedep.getArtifactId() + ":" + bedep.getVersion() + ":" + pom.getArtifactId() + ":" + pom.getVersion());
		}
	}

	/**
	 * 搜索依赖关系
	 * 
	 * @throws SVNException
	 * @throws InterruptedException
	 * @throws FormatInvalidException
	 */
	@Test
	public void testSearchDepend() throws SVNException, InterruptedException, FormatInvalidException {
		MavenReviewer maven = new MavenReviewer();
		maven.setSvnUrl("http://192.168.8.45:81");
		maven.setUsername("hawk");
		maven.setPassword("123456");
		maven.setSvnRootDir("/svn/dianping/dianping/group");
		maven.init();
		while (true) {
			if (maven.isFirstInit()) {
				break;
			}
			Thread.sleep(1000);
		}
		List<Dependency> searchDepended = maven.searchDepended("group", null);
		for (Dependency dependency : searchDepended) {
			Pom beDependency = dependency.getSearchedPom();
			System.out.println(dependency.getArtifactId() + ":" + dependency.getVersion() + ":" + beDependency.getArtifactId() + ":" + beDependency.getVersion());
		}
		System.out.println();
		searchDepended = maven.searchDepended("group", "1.0.1");
		for (Dependency dependency : searchDepended) {
			Pom beDependency = dependency.getSearchedPom();
			System.out.println(dependency.getArtifactId() + ":" + dependency.getVersion() + ":" + beDependency.getArtifactId() + ":" + beDependency.getVersion());
		}
		System.out.println();
		searchDepended = maven.searchDepended("group-service", "1.0.1");
		for (Dependency dependency : searchDepended) {
			Pom searchedPom = dependency.getSearchedPom();
			System.out.println(dependency.getArtifactId() + ":" + dependency.getVersion() + ":" + searchedPom.getArtifactId() + ":" + searchedPom.getVersion());
		}
	}

	@Test
	public void testSearchUseGroupId() throws SVNException, InterruptedException, FormatInvalidException {
		MavenReviewer maven = new MavenReviewer();
		maven.setSvnUrl("http://192.168.8.45:81");
		maven.setUsername("hawk");
		maven.setPassword("123456");
		maven.setSvnRootDir("/svn/dianping/dianping/group");
		maven.init();
		while (true) {
			if (maven.isFirstInit()) {
				break;
			}
			Thread.sleep(1000);
		}
		maven.dumpBeDepended();
		System.out.println("被依赖方项目名,被依赖方项目版本,依赖方项目名,依赖方项目版本");
		String artifactId = null;
		String groupId = "com.dianping";
		String version = null;
		List<Dependency> bedeps = maven.searchBeDepended(artifactId, groupId, version);
		for (Dependency bedep : bedeps) {
			Pom pom = bedep.getSearchedPom();
			System.out.println(bedep.getArtifactId() + "," + bedep.getVersion() + "," + pom.getArtifactId() + ":" + pom.getVersion());
		}
		System.out.println("项目名,项目版本,依赖项目名,依赖项目版本");
		List<Dependency> searchDepended = maven.searchDepended(artifactId, groupId, version);
		for (Dependency dependency : searchDepended) {
			Pom searchedPom = dependency.getSearchedPom();
			System.out.println(searchedPom.getArtifactId() + "," + searchedPom.getVersion() + "," + dependency.getArtifactId() + "," + dependency.getVersion());
		}
	}
}
