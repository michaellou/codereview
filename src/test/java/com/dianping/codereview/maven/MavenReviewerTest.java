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

	@Test
	public void testSearchBeDepend() throws SVNException, MavenPomFormatInvalidException, InterruptedException {
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
		List<Pom> poms = maven.searchBeDepended("h", null);
		for (Pom pom : poms) {
			Dependency searched = pom.getSearchedDependency();
			System.out.println(pom.getSvnPath() + ":" + pom.getArtifactId() + ":" + pom.getVersion() + ":" + searched.getArtifactId() + ":" + searched.getVersion());
		}
		System.out.println();
		poms = maven.searchBeDepended("h", "0.2.2");
		for (Pom pom : poms) {
			Dependency searched = pom.getSearchedDependency();
			System.out.println(pom.getSvnPath() + ":" + pom.getArtifactId() + ":" + pom.getVersion() + ":" + searched.getArtifactId() + ":" + searched.getVersion());
		}
		System.out.println();
		poms = maven.searchBeDepended("hawk-client", "0.2.2");
		for (Pom pom : poms) {
			Dependency searched = pom.getSearchedDependency();
			System.out.println(pom.getSvnPath() + ":" + pom.getArtifactId() + ":" + pom.getVersion() + ":" + searched.getArtifactId() + ":" + searched.getVersion());
		}
	}

	@Test
	public void testSearchDepend() throws SVNException, MavenPomFormatInvalidException, InterruptedException {
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
}
