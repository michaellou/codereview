/**
 * 
 */
package review.ibatis.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sean.wang
 * @since Oct 13, 2011
 */
public class SqlMap {
	private ResultMap resultMap;

	private List<Sql> sqls;

	public ResultMap getResultMap() {
		return resultMap;
	}

	public void setResultMap(ResultMap resultMap) {
		this.resultMap = resultMap;
	}

	public List<Sql> getSqls() {
		return sqls;
	}

	public void setSqls(List<Sql> sqls) {
		this.sqls = sqls;
	}

	public void addSql(Sql sql) {
		if(sqls == null) {
			sqls = new ArrayList<Sql>();
		}
		sqls.add(sql);
	}
}
