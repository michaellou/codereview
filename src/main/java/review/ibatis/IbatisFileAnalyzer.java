/**
 * 
 */
package review.ibatis;

import java.io.File;
import java.io.InputStream;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;

import review.ibatis.domain.DeleteSql;
import review.ibatis.domain.InsertSql;
import review.ibatis.domain.SelectSql;
import review.ibatis.domain.Sql;
import review.ibatis.domain.SqlMap;
import review.ibatis.domain.UpdateSql;

/**
 * @author sean.wang
 * @since Oct 13, 2011
 */
public class IbatisFileAnalyzer {

	private static final String ATTR_RESULT_CLASS = "resultClass";
	private static final String ATTR_PARAMETER_CLASS = "parameterClass";
	private static final String ATTR_ID = "id";
	private static final String NODE_DELETE = "delete";
	private static final String NODE_UPDATE = "update";
	private static final String NODE_SELECT = "select";
	private static final String NODE_INSERT = "insert";
	private static final String NODE_SQL_MAP = "sqlMap";

	public static SqlMap analyze(File file) throws IbatisFileFormatInvalidException {
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(file);
		} catch (DocumentException e) {
			throw new IbatisFileFormatInvalidException(e);
		}
		return parse(document);
	}
	
	public static SqlMap analyze(InputStream is) throws IbatisFileFormatInvalidException {
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(is);
		} catch (DocumentException e) {
			throw new IbatisFileFormatInvalidException(e);
		}
		return parse(document);
	}

	private static SqlMap parse(Document document) throws IbatisFileFormatInvalidException {
		Element rootElement = document.getRootElement();
		if (!NODE_SQL_MAP.equals(rootElement.getName())) {
			throw new IbatisFileFormatInvalidException();
		}
		final SqlMap sqlmap = new SqlMap();
		rootElement.accept(new VisitorSupport() {
			public void visit(Element element) {
				String eleName = element.getName();
				Sql sql = null;
				if (NODE_INSERT.equals(eleName)) {
					sql = new InsertSql();
				} else if (NODE_SELECT.equals(eleName)) {
					sql = new SelectSql();
				} else if (NODE_UPDATE.equals(eleName)) {
					sql = new UpdateSql();
				} else if (NODE_DELETE.equals(eleName)) {
					sql = new DeleteSql();
				}
				if (sql != null) {
					sql.setId(element.attributeValue(ATTR_ID));
					sql.setParameterClass(element.attributeValue(ATTR_PARAMETER_CLASS));
					sql.setResultClass(element.attributeValue(ATTR_RESULT_CLASS));
					sql.setStatement(element.getTextTrim());
					sqlmap.addSql(sql);
				}
			}

			public void visit(Attribute attr) {
			}
		});
		return sqlmap;
	}

}
