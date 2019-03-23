# rest-assured-extension

[![Build Status](https://travis-ci.org/JensPiegsa/rest-assured-extension.svg?branch=master)](https://travis-ci.org/JensPiegsa/rest-assured-extension)
[![codecov](https://codecov.io/gh/JensPiegsa/rest-assured-extension/branch/master/graph/badge.svg)](https://codecov.io/gh/JensPiegsa/rest-assured-extension)
[![](https://jitpack.io/v/JensPiegsa/rest-assured-extension.svg)](https://jitpack.io/#JensPiegsa/rest-assured-extension)
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=3WB8AXMP4VY98)

The rest-assured-extension is a [JUnit 5](https://junit.org/junit5/) [extension](https://junit.org/junit5/docs/current/user-guide/#extensions) for [REST-assured](http://rest-assured.io/).


## Usage

### Step 1. Add the JitPack repository to your **pom.xml** file

    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>


### Step 2. Add the rest-assured-extension dependency

    <dependency>
        <groupId>com.github.JensPiegsa</groupId>
        <artifactId>rest-assured-extension</artifactId>
        <version>0.1.2</version>
        <scope>test</scope>
    </dependency>

### Step 3. Annotate test classes by `@ExtendsWith(RestAssuredExtension.class)`

* see [`ExampleTest`](https://github.com/JensPiegsa/rest-assured-extension/blob/master/src/test/java/com/github/jenspiegsa/restassuredextension/ExampleTest.java) for further configuration and different use cases.

## Contribute

Feedback is welcome. The source is available on [Github](https://github.com/JensPiegsa/rest-assured-extension/). Please [report any issues](https://github.com/JensPiegsa/rest-assured-extension/issues).

## About

Plugin originally created by [Jens Piegsa](https://github.com/JensPiegsa).
