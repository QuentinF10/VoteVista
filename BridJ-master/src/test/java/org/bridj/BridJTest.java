/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2015, Olivier Chafik (http://ochafik.com/)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Olivier Chafik nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY OLIVIER CHAFIK AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.bridj;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.bridj.demangling.Demangler;
import org.junit.Ignore;
import org.junit.Test;

public class BridJTest {
	
	@Test
	public void testLongToIntCast() {
		for (long value : new long[] { 1, -1, -2, 100 }) {
			assertEquals((int)value, SizeT.safeIntCast(value));
		}
	}
	@Test
	public void loadPthread() throws Exception {
		if (!Platform.isUnix())
			return;
		
		assertNotNull(BridJ.getNativeLibrary(Platform.isMacOSX() ? "/usr/lib/system/libsystem_pthread.dylib" : "pthread"));
	}
	@Test
	public void symbolsTest() throws Exception {
		NativeLibrary lib = BridJ.getNativeLibrary("test");
		Collection<Demangler.Symbol> symbols = lib.getSymbols();
		
		assertTrue("Not enough symbols : found only " + symbols.size(), symbols.size() > 20);
		boolean found = false;
		for (Demangler.Symbol symbol : symbols) {
			if (symbol.getName().contains("Ctest")) {
				found = true;
				break;
			}
		}
		assertTrue("Failed to find any Ctest-related symbol !", found);
	}

	public static native double trampolineTestNative(double x);

	@Test
	@Ignore("Trampolines aren't working yet!")
	public void trampolineTest() throws Exception {
		// Java_org_bridj_JNI_newJNINativeTrampoline
		NativeLibrary lib = BridJ.getNativeLibrary("test");
		// System.err.println(lib.getSymbols());

		Demangler.Symbol sinSymbol = lib.getSymbol("testSin");
		
		long peer = JNI.newJNINativeTrampoline("d)d", sinSymbol.getAddress());
		assertNotEquals(0, peer);
		System.err.println("Built trampoline: " + peer);
		assertTrue(JNI.registerNatives(BridJTest.class.getName().replace('.', '/'), "(D)D", "trampolineTestNative", peer));

		System.err.println("Registered trampoline!");
		double epsilon = 0.0000001;
		assertEquals(0.0, trampolineTestNative(0.0), epsilon);
		assertEquals(Math.sin(1.0), trampolineTestNative(1.0), epsilon);
	}
}
