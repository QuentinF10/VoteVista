__BEWARE__: _This project hasn't been maintained since 2015. An effort to refresh it (and [JNAerator](https://github.com/nativelibs4java/JNAerator)) is ongoing (Nov 2022) and 0.8.0-SNAPSHOT contains a sneak peek of it, with no guarantees_.

# BridJ

[![Maven Central](http://maven-badges.herokuapp.com/maven-central/com.nativelibs4java/bridj/badge.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.nativelibs4java%22%20AND%20a%3A%22bridj%22) [![Build Status (Travis: Linux)](https://travis-ci.org/nativelibs4java/BridJ.svg?branch=master)](https://travis-ci.org/nativelibs4java/BridJ) [![Build Status (AppVeyor: Windows)](https://img.shields.io/appveyor/ci/ochafik/bridj/master.svg?label=windows build)](https://ci.appveyor.com/project/ochafik/bridj/) [![Join the chat at https://gitter.im/nativelibs4java/BridJ](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/nativelibs4java/BridJ?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) 

[BridJ](http://bridj.googlecode.com) is a Java / native interoperability library that focuses on speed and ease of use.

It is similar in spirit to [JNA](https://github.com/twall/jna) (dynamic bindings that don't require any native compilation, unlike JNI), but was designed to support C++, to be blazing fast (thanks to [dyncall](http://dyncall.org) + hand-optimized assembly tweaks) and to use modern Java features.

A comprehensive documentation is available on its [Wiki](https://github.com/nativelibs4java/BridJ/wiki).

It was previously hosted on [ochafik/nativelibs4java](http://github.com/ochafik/nativelibs4java).

# Quick links

__Note__: the [Wiki](https://github.com/nativelibs4java/BridJ/wiki) is being refreshed.

* [Usage](https://github.com/nativelibs4java/BridJ/wiki/Download) (also see `Examples/BasicExample`)
* [FAQ](https://github.com/nativelibs4java/BridJ/wiki/FAQ)
* [CHANGELOG](./CHANGELOG.md)
* [JavaDoc](http://nativelibs4java.sourceforge.net/bridj/api/development/)
* [Credits and License](https://github.com/nativelibs4java/BridJ/wiki/CreditsAndLicense)

# Building

```bash
git clone http://github.com/nativelibs4java/BridJ.git
cd BridJ
mvn clean install
```

Iterate on native code:
```bash
mvn native:javah
./BuildNative && mvn surefire:test
```

# Cross-compiling

## Prerequisites

Assuming you have docker:

```bash
# One-off to let Docker use QEMU to run exotic architectures.
docker run --rm --privileged multiarch/qemu-user-static --reset -p yes --credential yes

# Install the cross-compiler for Windows
# sudo apt install mingw-w64
brew install mingw-w64

# https://jdk.java.net/archive/

# Get JDK for Mac ARM64 & X64
wget https://download.java.net/java/GA/jdk19/877d6127e982470ba2a7faa31cc93d04/36/GPL/openjdk-19_macos-{x64,aarch64}_bin.tar.gz && \
  tar zxvf openjdk-19_macos-aarch64_bin.tar.gz && mv jdk-19.jdk{,-darwin_arm64} && \
  tar zxvf openjdk-19_macos-x64_bin.tar.gz && mv jdk-19.jdk{,-darwin_x64} \

# Get JDK for Windows X64
wget https://download.java.net/java/GA/jdk19/877d6127e982470ba2a7faa31cc93d04/36/GPL/openjdk-19_windows-x64_bin.zip && \
    unzip openjdk-19_windows-x64_bin.zip && mv jdk-19{,-windows_x64}

# Get JDK for Windows ARM64
wget https://github.com/microsoft/openjdk-aarch64/releases/download/jdk-16.0.2-ga/microsoft-jdk-16.0.2.7.1-linux-aarch64.tar.gz && \
  tar zxvf openjdk-19_macos-aarch64_bin.tar.gz && mv jdk-16.0.2+7{,-windows_arm64}

wget https://builds.openlogic.com/downloadJDK/openlogic-openjdk/8u352-b08/openlogic-openjdk-8u352-b08-windows-x32.zip && \
    unzip openlogic-openjdk-8u352-b08-windows-x32.zip
```

## Cross-build commands

```bash
# Mac host: build Mac M1 & Intel binaries
ARCH=x64 ./BuildNative -DFORCE_JAVA_HOME=$PWD/../jdk-19.jdk-darwin_x64/Contents/Home
ARCH=arm64 ./BuildNative -DFORCE_JAVA_HOME=$PWD/../jdk-19.jdk-darwin_arm64/Contents/Home

# Mac or Linux host: build Windows X64 & X86 binaries w/ MinGW-w64
OS=windows ARCH=x64 ./BuildNative \
  -DCMAKE_TOOLCHAIN_FILE=$PWD/mingw-w64-x86_64.cmake \
  -DFORCE_JAVA_HOME=$PWD/../jdk-19-windows_x64
OS=windows ARCH=x86 ./BuildNative \
  -DCMAKE_TOOLCHAIN_FILE=$PWD/mingw-w64-i686.cmake \
  -DFORCE_JAVA_HOME=$PWD/../openlogic-openjdk-8u352-b08-windows-32

# Mac or Linux host: build & test Linux x86, x64, arm64, arm binaries inside Docker + QEMU:
# TODO: look at armel situation (no openjdk?)
         ./scripts/build-docker-qemu.sh linux/x86_64   debian:bullseye-slim           bridj-linux-x64
ARCH=x86 ./scripts/build-docker-qemu.sh linux/i386     i386/debian:bullseye-slim      bridj-linux-x86
         ./scripts/build-docker-qemu.sh linux/arm64    arm64v8/debian:bullseye-slim   bridj-linux-arm64
         ./scripts/build-docker-qemu.sh linux/arm/v7   arm32v7/debian:bullseye-slim   bridj-linux-arm
         ./scripts/build-docker-qemu.sh linux/ppc64le  ppc64le/debian:bullseye-slim   bridj-linux-ppc64le
#        ./scripts/build-docker-qemu.sh linux/arm/v6 balenalib/rpi-raspbian:bullseye bridj-linux-armel

# Windows x64 host (UNTESTED): build Windows X64 & ARM64 binary
ARCH=x64 ./BuildNative
ARCH=arm64 ./BuildNative -DFORCE_JAVA_HOME=$PWD/../jdk-16.0.2+7-windows_arm64
```

# Debugging

```bash
mvn dependency:build-classpath -DincludeScope=test -Dmdep.outputFile=deps-classpath-test.txt
DEBUG=1 mvn clean test-compile

# Or gdb --args java ...
lldb -- java -cp \
  target/generated-resources:target/generated-test-resources:target/test-classes:target/classes:$( cat deps-classpath-test.txt ) \
  org.junit.runner.JUnitCore \
  org.bridj.BridJTest
```

# Formatting

```
mvn format
```

# Support

Please use the [mailing-list](https://groups.google.com/forum/#!forum/nativelibs4java) and [file bugs](https://github.com/ochafik/nativelibs4java/issues/new).

# TODO

* Build for MIPS, PPC64Le, Sparc, Sparc64, s390x linux
  https://hub.docker.com/_/debian
  i386, mips64le, ppc64le, riscv64, s390x
  https://github.com/multiarch/qemu-user-static
  https://hub.docker.com/layers/qemu-user-static/multiarch/qemu-user-static/x86_64-sparc-7.0.0-7/images/sha256-bf38e980ec9303b8942b8a79c1a856d019a6b3f2f16ce0c36973c28110f0015f?context=explore
  https://github.com/docker-library/official-images#architectures-other-than-amd64
  https://hub.docker.com/r/arm32v5/debian
  https://hub.docker.com/r/arm32v5/clojure
  https://hub.docker.com/r/mips64le/debian
* Build for Sparc Solaris
  https://hub.docker.com/r/netcrave/sparcsolaris
* Try armel vs. arm32v5
* Rebuild for Android, cmake-style
* Factor some cruft into cmake helpers
* CI w/ all the cross builds (use upload / download artefact github actions)
* ~~Separate ARM / x86_64 builds on Darwin (link w/ ARM JDK https://jdk.java.net/archive/), or scripting to create fat libjvm.dylib and libjawt.dylib~~
* Update pom to make it independent from nativelibs4java-parent
* Update deps: ASM 5.x, JUnit 4.11
* Fix BridJ's armhf support
