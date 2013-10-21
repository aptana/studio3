package net.jeeeyul.eclipse.themes.rendering;

public class KTabRendererHelper {
	public int operator_and(int e1, int e2) {
		return e1 & e2;
	}

	public int operator_or(int e1, int e2) {
		return e1 | e2;
	}
	
	public int removeFlag(int value, int flag){
		return value & ~flag;
	}
}
