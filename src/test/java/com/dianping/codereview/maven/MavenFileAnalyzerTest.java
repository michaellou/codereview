/**
 * 
 */
package com.dianping.codereview.maven;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dianping.codereview.maven.domain.Dependency;
import com.dianping.codereview.maven.domain.Packaging;
import com.dianping.codereview.maven.domain.Pom;
import com.dianping.codereview.maven.domain.Scope;

/**
 * @author sean.wang
 * @since Nov 3, 2011
 */
public class MavenFileAnalyzerTest {
	protected static String getFileName(String string) {
		return System.getProperty("user.dir") + "/src/test/resources/com/dianping/codereview/maven/demo1/" + string;
	}

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
	public void testAnalyzeParentPom() throws MavenPomFormatInvalidException {
		Pom pom = MavenFileAnalyzer.analyze(new File(getFileName("pom.xml")));
		Assert.assertEquals("com.dianping", pom.getGroupId());
		Assert.assertEquals("group", pom.getArtifactId());
		Assert.assertEquals("1.0.1", pom.getVersion());
		Assert.assertEquals(Packaging.POM, pom.getPackaging());
		Assert.assertTrue(pom.getModules().containsAll(Arrays.asList("group-service", "group-remote")));
		List<Dependency> deps = pom.getDependencies();
		Assert.assertEquals("com.dianping.dpsf", deps.get(0).getGroupId());
		Assert.assertEquals("dpsf-net", deps.get(0).getArtifactId());
		Assert.assertEquals("1.4.2", deps.get(0).getVersion());
		Assert.assertEquals("com.dianping.hawk", deps.get(1).getGroupId());
		Assert.assertEquals("hawk-client", deps.get(1).getArtifactId());
		Assert.assertEquals("0.2.2", deps.get(1).getVersion());
		Assert.assertEquals(Scope.TEST, deps.get(1).getScope());
	}

	@Test
	public void testAnalyzeModulePom() throws MavenPomFormatInvalidException {
		Pom pom = MavenFileAnalyzer.analyze(new File(getFileName("groupservice/pom.xml")));
		Dependency parent = pom.getParent();
		Assert.assertEquals("com.dianping", parent.getGroupId());
		Assert.assertEquals("group", parent.getArtifactId());
		Assert.assertEquals("1.0.1", parent.getVersion());
		Assert.assertEquals("com.dianping", pom.getGroupId());
		Assert.assertEquals("group-service", pom.getArtifactId());
		Assert.assertEquals("1.0.1", pom.getVersion());
		Assert.assertEquals(Packaging.WAR, pom.getPackaging());
		List<Dependency> deps = pom.getDependencies();
		Assert.assertEquals("com.dianping.dpsf", deps.get(0).getGroupId());
		Assert.assertEquals("dpsf-net", deps.get(0).getArtifactId());
		Assert.assertEquals("com.dianping.hawk", deps.get(1).getGroupId());
		Assert.assertEquals("hawk-client", deps.get(1).getArtifactId());
	}

}
