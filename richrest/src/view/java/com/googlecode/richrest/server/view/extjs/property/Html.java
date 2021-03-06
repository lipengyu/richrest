package com.googlecode.richrest.server.view.extjs.property;

import com.googlecode.richrest.server.view.extjs.ComponentTag;

public class Html extends ComponentTag {

	private static final long serialVersionUID = 1L;

	@Override
	protected String getComponentDefine() {
		String html = "";
		if (bodyContent != null)
			html = filter(bodyContent.getString());
		html = html.replace("\\", "\\\\");
		html = html.replace("\t", "\\t");
		html = html.replace("\f", "\\f");
		html = html.replace("\r", "\\r");
		html = html.replace("\n", "\\n");
		html = html.replace("\"", "\\\"");
		html = html.replace("\'", "\\\'");
		return "\"" + html + "\"";
	}

	@Override
	protected String getDefaultKey() {
		return "html";
	}

}
