/**
 * 
 */
package com.dianping.codereview.ibatis;

import java.io.File;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dianping.codereview.ibatis.domain.Sql;
import com.dianping.codereview.ibatis.domain.SqlMap;


/**
 * @author sean.wang
 * @since Oct 13, 2011
 */
public class IbatisFileAnalyzerTest {
	protected static String getFileName(String string) {
		return System.getProperty("user.dir") + "/src/test/resources/configdemo/" + string;
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
	public void testAnalyze() throws DocumentException, IbatisFileFormatInvalidException {
		SqlMap sqlmap = IbatisFileAnalyzer.analyze(new File(getFileName("context-mapping.xml")));
		Assert.assertEquals(2, sqlmap.getSqls().size());
		Sql insert = sqlmap.getSqls().get(0);
		Assert.assertEquals("INSERT-CONTEXT", insert.getId());
		Assert.assertEquals("domain.Context", insert.getParameterClass());
		Assert.assertTrue(insert.getStatement().startsWith("INSERT INTO context"));
		Sql select = sqlmap.getSqls().get(1);
		Assert.assertEquals("GET-CONTEXT-NUM-BY-TOKEN", select.getId());
		Assert.assertEquals("int", select.getResultClass());
		Assert.assertTrue(select.getStatement().startsWith("select count(id)"));
	}

}
