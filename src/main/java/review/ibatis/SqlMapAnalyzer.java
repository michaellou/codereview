/**
 * 
 */
package review.ibatis;

import java.io.File;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;

/**
 * @author sean.wang
 * @since Oct 13, 2011
 */
public class SqlMapAnalyzer {

	public static SqlMap analyze(File file) throws DocumentException {
		final SqlMap sqlmap = new SqlMap();
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		Element rootElement = document.getRootElement();
		rootElement.accept(new VisitorSupport() {
			public void visit(Element element) {
				String eleName = element.getName();
				Sql sql = null;
				if ("insert".equals(eleName)) {
					sql = new InsertSql();
				} else if ("select".equals(eleName)) {
					sql = new SelectSql();
				} else if ("update".equals(eleName)) {
					sql = new UpdateSql();
				} else if ("delete".equals(eleName)) {
					sql = new DeleteSql();
				}
				if (sql != null) {
					sql.setId(element.attributeValue("id"));
					sql.setParameterClass(element.attributeValue("parameterClass"));
					sql.setResultClass(element.attributeValue("resultClass"));
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
