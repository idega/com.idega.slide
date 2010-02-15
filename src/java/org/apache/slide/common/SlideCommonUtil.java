package org.apache.slide.common;

public class SlideCommonUtil {

	private static final SlideCommonUtil util = new SlideCommonUtil();
	
	private SlideCommonUtil() {}
	
	public static final SlideCommonUtil getInstance() {
		return util;
	}
	
	public final Namespace getDefaultNamespace() {
		return getNamespace("slide");
	}
	
	public final Namespace getNamespace(String name) {
		return Domain.getNamespace(name);
	}
	
}