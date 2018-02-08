/*******************************************************************************
 * Copyright (c) 2007 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Michael Scharf (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.model;

import org.eclipse.tm.terminal.model.ITerminalTextData;
import org.eclipse.tm.terminal.model.ITerminalTextDataSnapshot;
import org.eclipse.tm.terminal.model.Style;
import org.eclipse.tm.terminal.model.StyleColor;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.aptana.testing.categories.PerformanceTests;

@Category({ PerformanceTests.class })
public class TerminalTextDataPerformanceTest {
	long TIME=100;
	
	private void initPerformance(ITerminalTextData term) {
		term.setDimensions(300,200);
	}
	
	@Test
	public void testPerformance0() {
		ITerminalTextData term=new TerminalTextData();
		method0(term,"0 ");
	}
	
	@Test
	public void testPerformance0a() {
		ITerminalTextData term=new TerminalTextData();
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		method0(term,"0a");
		snapshot.updateSnapshot(true);
	}
	
	@Test
	public void testPerformance0b() {
		ITerminalTextData term=new TerminalTextData();
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		N=0;
		snapshot.addListener(new ITerminalTextDataSnapshot.SnapshotOutOfDateListener(){
			public void snapshotOutOfDate(ITerminalTextDataSnapshot snapshot) {
				N++;
			}});
		method0(term,"0b");
		snapshot.updateSnapshot(true);
	}
	private void method0(ITerminalTextData term, String label) {
		Style style=Style.getStyle(StyleColor.getStyleColor("fg"), StyleColor.getStyleColor("bg"), false, false, false, false);
		initPerformance(term);
		String s="This is a test string";
		long n=0;
		long t0=System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			char c=s.charAt(i%s.length());
			for (int line = 0; line < term.getHeight(); line++) {
				for (int column = 0; column < term.getWidth(); column++) {
					term.setChar(line, column, c, style);
					n++;
				}
			}
			if(System.currentTimeMillis()-t0>TIME) {
				System.out.println(label+" "+(n*1000)/(System.currentTimeMillis()-t0)+" setChar()/sec "+ N);
				break;
			}
		}
	}
	
	@Test
	public void testPerformance1() {
		ITerminalTextData term=new TerminalTextData();
		method1(term, "1 ");
	}
	
	@Test
	public void testPerformance1a() {
		ITerminalTextData term=new TerminalTextData();
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		method1(term, "1a");
		snapshot.updateSnapshot(true);
	}
	
	@Test
	public void testPerformance1b() {
		ITerminalTextData term=new TerminalTextData();
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		N=0;
		snapshot.addListener(new ITerminalTextDataSnapshot.SnapshotOutOfDateListener(){
			public void snapshotOutOfDate(ITerminalTextDataSnapshot snapshot) {
				N++;
			}});
		method1(term, "1b");
		snapshot.updateSnapshot(true);
	}
	private void method1(ITerminalTextData term, String label) {
		Style style=Style.getStyle(StyleColor.getStyleColor("fg"), StyleColor.getStyleColor("bg"), false, false, false, false);
		initPerformance(term);
		String s="This is a test string";
		long n=0;
		long t0=System.currentTimeMillis();
		char[] chars=new char[term.getWidth()];
		for (int i = 0; i < 10000000; i++) {
			for (int j = 0; j < chars.length; j++) {
				chars[j]=s.charAt((i+j)%s.length());
			}
			for (int line = 0; line < term.getHeight(); line++) {
				term.setChars(line, 0, chars, style);
				n+=chars.length;
			}
			if(System.currentTimeMillis()-t0>TIME) {
				System.out.println(label+" "+(n*1000)/(System.currentTimeMillis()-t0)+" setChars()/sec "+ N);
				break;
			}
		}
	}
	
	@Test
	public void testPerformance2() {
		TerminalTextData term=new TerminalTextData();
		Style style=Style.getStyle(StyleColor.getStyleColor("fg"), StyleColor.getStyleColor("bg"), false, false, false, false);
		initPerformance(term);
		TerminalTextData copy=new TerminalTextData();
		copy.copy(term);
		
		String s="This is a test string";
		long n=0;
		long t0=System.currentTimeMillis();
		char[] chars=new char[term.getWidth()];
		for (int i = 0; i < 10000000; i++) {
			for (int j = 0; j < chars.length; j++) {
				chars[j]=s.charAt((i+j)%s.length());
			}
			for (int line = 0; line < term.getHeight(); line++) {
				term.setChars(line, 0, chars, 0,1,style);
				copy.copy(term);
				n+=1;
				if(System.currentTimeMillis()-t0>TIME) {
					System.out.println((n*1000)/(System.currentTimeMillis()-t0)+" copy()/sec");
					return;
				}
			}
		}
	}
	
	@Test
	public void testPerformance2a() {
		TerminalTextData term=new TerminalTextData();
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		Style style=Style.getStyle(StyleColor.getStyleColor("fg"), StyleColor.getStyleColor("bg"), false, false, false, false);
		initPerformance(term);
		TerminalTextData copy=new TerminalTextData();
		copy.copy(term);
		
		String s="This is a test string";
		long n=0;
		long t0=System.currentTimeMillis();
		char[] chars=new char[term.getWidth()];
		for (int i = 0; i < 10000000; i++) {
			for (int j = 0; j < chars.length; j++) {
				chars[j]=s.charAt((i+j)%s.length());
			}
			for (int line = 0; line < term.getHeight(); line++) {
				term.setChars(line, 0, chars, 0,1,style);
				copy.copy(term);
				n+=1;
				if(System.currentTimeMillis()-t0>TIME) {
					System.out.println((n*1000)/(System.currentTimeMillis()-t0)+" copy()/sec");
					return;
				}
			}
		}
		snapshot.updateSnapshot(true);
	}
	int N=0;
	
	@Test
	public void testPerformance2b() {
		TerminalTextData term=new TerminalTextData();
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		N=0;
		snapshot.addListener(new ITerminalTextDataSnapshot.SnapshotOutOfDateListener(){
			public void snapshotOutOfDate(ITerminalTextDataSnapshot snapshot) {
				N++;
			}});
		Style style=Style.getStyle(StyleColor.getStyleColor("fg"), StyleColor.getStyleColor("bg"), false, false, false, false);
		initPerformance(term);
		TerminalTextData copy=new TerminalTextData();
		copy.copy(term);
		
		String s="This is a test string";
		long n=0;
		long t0=System.currentTimeMillis();
		char[] chars=new char[term.getWidth()];
		for (int i = 0; i < 10000000; i++) {
			for (int j = 0; j < chars.length; j++) {
				chars[j]=s.charAt((i+j)%s.length());
			}
			for (int line = 0; line < term.getHeight(); line++) {
				term.setChars(line, 0, chars, 0,1,style);
				copy.copy(term);
				n+=1;
				if(System.currentTimeMillis()-t0>TIME) {
					System.out.println((n*1000)/(System.currentTimeMillis()-t0)+" copy()/sec "+n);
					return;
				}
			}
		}
		snapshot.updateSnapshot(true);
	}
	
	@Test
	public void testPerformance3() {
		TerminalTextData term=new TerminalTextData();
		Style style=Style.getStyle(StyleColor.getStyleColor("fg"), StyleColor.getStyleColor("bg"), false, false, false, false);
		initPerformance(term);
		TerminalTextData copy=new TerminalTextData();
		copy.copy(term);
		String s="This is a test string";
		long n=0;
		long t0=System.currentTimeMillis();
		char[] chars=new char[term.getWidth()];
		for (int i = 0; i < 10000000; i++) {
			boolean[] linesToCopy=new boolean[term.getHeight()];
			for (int j = 0; j < chars.length; j++) {
				chars[j]=s.charAt((i+j)%s.length());
			}
			for (int line = 0; line < term.getHeight(); line++) {
				term.setChars(line, 0, chars, 0,1,style);
				linesToCopy[line]=true;
				copy.copyLine(term,0,0);
				linesToCopy[line]=false;
				n+=1;
				if(System.currentTimeMillis()-t0>TIME) {
					System.out.println((n*1000)/(System.currentTimeMillis()-t0)+" copy()/sec");
					return;
				}
			}
		}
	}
}
