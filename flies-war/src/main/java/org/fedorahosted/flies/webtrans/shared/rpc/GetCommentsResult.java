package org.fedorahosted.flies.webtrans.shared.rpc;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Result;

import org.fedorahosted.flies.webtrans.shared.model.DocumentInfo;
import org.fedorahosted.flies.webtrans.shared.model.ProjectIterationId;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GetCommentsResult implements Result {

	private static final long serialVersionUID = 1L;
	
	private String comment;
	
	@SuppressWarnings("unused")
	private GetCommentsResult()	{
	}
	
	public GetCommentsResult(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
}