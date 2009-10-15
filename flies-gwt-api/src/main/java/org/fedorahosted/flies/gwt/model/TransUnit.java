package org.fedorahosted.flies.gwt.model;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TransUnit implements IsSerializable, Serializable{
	
	private static final long serialVersionUID = -8247442475446266600L;

	private boolean fuzzy;
	
	private String source;
	private String target;
	
	@SuppressWarnings("unused")
	private TransUnit(){
	}
	
	public TransUnit(String source, String target) {
		this.source = source;
		this.target = target;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}
	
	public boolean isFuzzy() {
		return fuzzy;
	}
	
	public void setFuzzy(boolean fuzzy) {
		this.fuzzy = fuzzy;
	}
}