package com.udacity.project.reddit.capstone.multilevelexpindlistview.src.androidTest.java.com.oissela.software.multilevelexpindlistview;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FullTestSuite extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(FullTestSuite.class)
                        .includeAllPackagesUnderHere().build();
    }

    public FullTestSuite() {
        super();
    }
}