/**
 * 
 */
package com.dianping.codereview.ibatis;

import org.dom4j.DocumentException;

import com.dianping.codereview.FormatInvalidException;

/**
 * @author sean.wang
 * @since Oct 14, 2011
 */
public class IbatisFileFormatInvalidException extends FormatInvalidException {

	private static final long serialVersionUID = 200873368162741116L;

	public IbatisFileFormatInvalidException(DocumentException e) {
		super(e);
	}

	public IbatisFileFormatInvalidException() {
		super();
	}

}
