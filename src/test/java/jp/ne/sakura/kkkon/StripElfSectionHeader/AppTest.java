/**
 * The MIT License
 * 
 * Copyright (C) 2013 Kiyofumi Kondoh
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jp.ne.sakura.kkkon.StripElfSectionHeader;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kiyofumi Kondoh
 */
public class AppTest {
    
    public AppTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of stripRecursive method, of class App.
     */
    //@Test
    public void testStripRecursive() {
        System.out.println("stripRecursive");
        AppOption option = null;
        String rootPath = "";
        String path = "";
        App.stripRecursive(option, rootPath, path);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class App.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        App.main(args);
    }

    /**
     * Test of main method, of class App.
     */
    @Test
    public void testMainAndroidSingleFileDryRun() {
        System.out.println("mainAndroidSingleFileDryRun");
        String[] args = {
            "-B"
            , "target/test-classes/elf/android/hello-jni/obj/local/armeabi-v7a/libhello-jni.so"
            , "-o target/test-classes/elf-stripSingle/android/hello-jni/obj/local/armeabi-v7a/libhello-jni.so"
            , "--dry-run"
        };
        App.main(args);
    }
    /**
     * Test of main method, of class App.
     */
    @Test
    public void testMainAndroidSingleFile() {
        System.out.println("mainAndroidSingleFile");
        String[] args = {
            "-B"
            , "target/test-classes/elf/android/hello-jni/obj/local/armeabi-v7a/libhello-jni.so"
            , "-o target/test-classes/elf-stripSingle/android/hello-jni/obj/local/armeabi-v7a/libhello-jni.so"
        };
        App.main(args);
    }
    /**
     * Test of main method, of class App.
     */
    @Test
    public void testMainAndroidDryRun() {
        System.out.println("mainAndroidDryRun");
        String[] args = {
            "-B"
            , "--recursive"
            , "target/test-classes/elf/android"
            , "-o target/test-classes/elf-stripRecursive/android"
            , "--dry-run"
        };
        App.main(args);
    }
    /**
     * Test of main method, of class App.
     */
    @Test
    public void testMainAndroid() {
        System.out.println("mainAndroid");
        String[] args = {
            "-B"
            , "--recursive"
            , "target/test-classes/elf/android"
            , "-o target/test-classes/elf-stripRecursive/android"
        };
        App.main(args);
    }

    /**
     * Test of main method, of class App.
     */
    @Test
    public void testMainAndroidDebugSingleFileStripAndroid() {
        System.out.println("AndroidDebugSingleFileStripAndroid");
        String[] args = {
            "-B"
            , "--android"
            , "target/test-classes/elf/android/hello-jni/obj/local/armeabi-v7a/libhello-jni.so"
            , "-o target/test-classes/elf-stripAndroid/android/hello-jni/obj/local/armeabi-v7a/libhello-jni.so"
        };
        App.main(args);
    }
    /**
     * Test of main method, of class App.
     */
    @Test
    public void testMainAndroidSingleFileStripAndroid() {
        System.out.println("AndroidSingleFileStripAndroid");
        String[] args = {
            "-B"
            , "--android"
            , "target/test-classes/elf/android/hello-jni/libs/armeabi-v7a/libhello-jni.so"
            , "-o target/test-classes/elf-stripAndroid/android/hello-jni/libs/armeabi-v7a/libhello-jni.so"
        };
        App.main(args);
    }
    /**
     * Test of main method, of class App.
     */
    @Test
    public void testMainAndroidSingleFileStripAndroidO() {
        System.out.println("AndroidSingleFileStripAndroidO");
        String[] args = {
            "-B"
            , "--android-O"
            , "target/test-classes/elf/android/hello-jni/libs/armeabi-v7a/libhello-jni_rwx.so"
            , "-o target/test-classes/elf-stripAndroid/android/hello-jni/libs/armeabi-v7a/libhello-jni_rwx.so"
        };
        App.main(args);
    }

    @Test
    public void testMainAndroidArm64SingleFile() {
        System.out.println("mainAndroidSingleFile");
        String[] args = {
            "-B"
            , "--android"
            , "target/test-classes/elf/android/hello-jni_ndk-r10e/libs/arm64-v8a/libhello-jni.so"
            , "-o target/test-classes/elf-stripSingle/android/hello-jni_ndk-r10e/libs/arm64-v8a/libhello-jni.so"
        };
        App.main(args);
    }

    @Test
    public void testMainAndroidArm64SingleFileStripAndroidO() {
        System.out.println("AndroidSingleFileStripAndroidO");
        String[] args = {
            "-B"
            , "--android-O"
            , "target/test-classes/elf/android/hello-jni_ndk-r10e/libs/arm64-v8a/libhello-jni_rwx.so"
            , "-o target/test-classes/elf-stripSingle/android/hello-jni_ndk-r10e/libs/arm64-v8a/libhello-jni_rwx.so"
        };
        App.main(args);
    }

}