package com.dianping.codereview.svn;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;

public class SVNManagerTest {
	private static SVNManager svnManager;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		svnManager = new SVNManager("http://192.168.8.45:81", "hawk", "123456");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testIsFile() throws SVNException {
		Assert.assertTrue(svnManager.isFile("/svn/dianping/platform/middleware/trunk/hawk/pom.xml", -1));
		Assert.assertFalse(svnManager.isFile("/svn/dianping/platform/middleware/trunk/hawk", -1));
	}

	@Test
	public void testGetFile() throws Exception {
		OutputStream outputStream = new ByteArrayOutputStream();
		SVNProperties p = new SVNProperties();
		svnManager.getFile("/svn/dianping/platform/middleware/trunk/hawk/pom.xml", outputStream, -1, p);
		System.out.println(p.nameSet());
		Assert.assertNotNull(p.getStringValue("svn:executable"));
	}
	
	@Test
	public void testGetFileIfDir() throws Exception {
		OutputStream outputStream = new ByteArrayOutputStream();
		SVNProperties p = new SVNProperties();
		svnManager.getFile("/svn/dianping/platform/middleware/trunk/hawk", outputStream, -1, p);
		System.out.println(p.nameSet());
		Assert.assertNull(p.getStringValue("svn:executable"));
	}

	@Test
	public void testGetChildren() throws Exception {
		Resource res = new Resource();
		res.setPath("/svn/dianping/platform/middleware/trunk/hawk");
		List<Resource> rs = svnManager.getChildren(res, null);
		for (Resource r : rs) {
			System.out.println((r.isFile() ? "file:" : "directory:") + r.getPath() + ":" + r.getName());
		}
	}
	
	@Test
	public void testGetChildrenIfFile() throws Exception {
		Resource res = new Resource();
		res.setPath("/svn/dianping/platform/middleware/trunk/hawk/pom.xml");
		svnManager.getChildren(res, null);
	}

}
