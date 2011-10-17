package com.dianping.codereview.ibatis;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

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
	 * 列出svn地址上ibatis配置文件内的所有sql语句
	 * 
	 * @throws SVNException
	 */
	@Test 
	public void testFetchSqlsFromSvn() throws SVNException {
		List<Sql> sqls = IbatisReviewer.fetchSqlsFromSvn("http://192.168.8.45:81", "/svn/dianping/platform/middleware/trunk/hawk", "hawk", "123456");
		for(Sql sql: sqls) {
			System.out.println("from: " + sql.getPath());
			System.out.println(sql.getStatement());
		}
	}

}
