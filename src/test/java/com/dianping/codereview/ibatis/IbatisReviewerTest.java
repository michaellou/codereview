package com.dianping.codereview.ibatis;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

import com.dianping.codereview.FormatInvalidException;
import com.dianping.codereview.ibatis.domain.Sql;

public class IbatisReviewerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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

	/**
	 * svn地址上ibatis配置文件内的所有sql语句
	 * 
	 * @throws SVNException
	 */
	@Test
	public void testFetchSqlsFromSvnDir() throws SVNException {
		IbatisReviewer ir = new IbatisReviewer();
		ir.setSvnUrl("http://192.168.8.45:81");
		ir.setUsername("hawk");
		ir.setPassword("123456");
		ir.initSvn();
		List<Sql> sqls = ir.fetchSqlsFromSvn("/svn/dianping/platform/middleware/trunk/hawk/hawk-server/src/main/resources/config/sqlmap");
		for (Sql sql : sqls) {
			System.out.println("from: " + sql.getPath());
			System.out.println(sql.getStatement());
		}
	}

	/**
	 * 单个ibatis配置文件
	 * 
	 * @throws SVNException
	 */
	@Test
	public void testFetchSqlsFromSvnFile() throws SVNException {
		IbatisReviewer ir = new IbatisReviewer();
		ir.setSvnUrl("http://192.168.8.45:81");
		ir.setUsername("hawk");
		ir.setPassword("123456");
		ir.initSvn();
		List<Sql> sqls = ir.fetchSqlsFromSvn("/svn/dianping/platform/middleware/trunk/hawk/hawk-server/src/main/resources/config/sqlmap/public/context-mapping.xml");
		for (Sql sql : sqls) {
			System.out.println("from: " + sql.getPath());
			System.out.println(sql.getStatement());
		}
	}

	/**
	 * 先cache并查询svn地址上ibatis配置文件内的所有sql语句
	 * 
	 * @throws SVNException
	 * @throws FormatInvalidException
	 * @throws InterruptedException
	 */
	@Test
	public void testFetchSqlsFromSvnDirAfterCache() throws SVNException, FormatInvalidException, InterruptedException {
		IbatisReviewer ir = new IbatisReviewer();
		ir.setSvnUrl("http://192.168.8.45:81");
		ir.setUsername("hawk");
		ir.setPassword("123456");
		ir.setSvnRootDir("/svn/dianping/platform/middleware/trunk/hawk/hawk-server/src/main/resources/config/sqlmap/public/");
		ir.init();// cache
		while (true) {
			if (ir.isFirstInit()) {
				break;
			}
			Thread.sleep(1000);
		}
		System.out.println("---- finish cache -----");
		List<Sql> sqls = ir.fetchSqlsFromCache();
		for (Sql sql : sqls) {
			System.out.println("from: " + sql.getPath());
			System.out.println(sql.getStatement());
		}
	}

}
