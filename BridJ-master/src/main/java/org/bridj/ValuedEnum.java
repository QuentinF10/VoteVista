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


/**
 * Interface for Java enumerations that have an integral value associated
 *
 * @author ochafik
 * @param <E> type of the enum
 */
public interface ValuedEnum<E extends Enum<E>> extends Iterable<E> {

    long value();
//
//    public static class EnumWrapper<EE extends Enum<EE>> implements ValuedEnum<EE> {
//        EE enumValue;
//        public EnumWrapper(EE enumValue) {
//            if (enumValue == null)
//                throw new IllegalArgumentException("Null enum value !");
//            this.enumValue = enumValue;
//        }
//
//        @Override
//        public long value() {
//            return enumValue.ordinal();
//        }
//
//        @Override
//        public Iterator<EE> iterator() {
//            return Collections.singleton(enumValue).iterator();
//        }
//
//    }
//
//    public enum MyEnum implements ValuedEnum<MyEnum> {
//        A(1), B(2);
//
//        MyEnum(long value) { this.value = value; }
//        long value;
//        @Override
//        public long value() {
//            return ordinal();
//        }
//
//        @Override
//        public Iterator<MyEnum> iterator() {
//            return Collections.singleton(this).iterator();
//        }
//
//        public static ValuedEnum<MyEnum> fromValue(long value) {
//            return FlagSet.fromValue(value, values());
//        }
//    }
}