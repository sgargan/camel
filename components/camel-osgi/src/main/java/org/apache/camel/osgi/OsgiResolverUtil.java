/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.osgi;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.camel.util.ResolverUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.util.BundleDelegatingClassLoader;

public class OsgiResolverUtil extends ResolverUtil {
    private Bundle bundle;
    
    public OsgiResolverUtil(Bundle bundle) {
        this.bundle = bundle;
    }
    
    public OsgiResolverUtil(BundleContext context) {
        bundle = context.getBundle();
    }
    
    /**
     * Returns the classloaders that will be used for scanning for classes. 
     * Here we just add BundleDelegatingClassLoader here
     *
     * @return the ClassLoader instances that will be used to scan for classes
     */
    public Set<ClassLoader> getClassLoaders() {
        Set<ClassLoader> classLoaders = super.getClassLoaders();
        // Using the Activator's bundle to make up a class loader
        ClassLoader osgiLoader = BundleDelegatingClassLoader.createBundleClassLoaderFor(bundle);
        classLoaders.add(osgiLoader);
        return classLoaders;
    }
    
    /**
     * Scans for classes starting at the package provided and descending into
     * subpackages. Each class is offered up to the Test as it is discovered,
     * and if the Test returns true the class is retained. Accumulated classes
     * can be fetched by calling {@link #getClasses()}.
     *
     * @param test        an instance of {@link Test} that will be used to filter
     *                    classes
     * @param packageName the name of the package from which to start scanning
     *                    for classes, e.g. {@code net.sourceforge.stripes}
     */
    public void find(Test test, String packageName) {
        packageName = packageName.replace('.', '/');

        Set<ClassLoader> set = getClassLoaders();
        int classSize = getClasses().size();

        ClassLoader osgiClassLoader = getOsgiClassLoader(set);
        log.debug("The osgi bundle classloader is " + osgiClassLoader);
        if (osgiClassLoader != null) {
            // if we have an osgi bundle loader use this one first
            log.debug("Using only osgi bundle classloader");
            findInOsgiClassLoader(test, packageName, osgiClassLoader);
        }
        // try to use other classloader if we don't find any class from Osgi
        if (classSize == getClasses().size()) {            
            log.debug("Using only regular classloaders");
            for (ClassLoader classLoader : set.toArray(new ClassLoader[set.size()])) {
                if (!isOsgiClassloader(classLoader)) {
                    find(test, packageName, classLoader);
                }        
            }
        }
    }

    
    private void findInOsgiClassLoader(Test test, String packageName, ClassLoader osgiClassLoader) {
        try {
            Method mth = osgiClassLoader.getClass().getMethod("getBundle", new Class[]{});
            if (mth != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Loading from osgi bundle using classloader: " + osgiClassLoader);
                }
                loadImplementationsInBundle(test, packageName, osgiClassLoader, mth);
                return;
            }
        } catch (NoSuchMethodException e) {
            log.warn("It's not an osgi bundle classloader: " + osgiClassLoader);
            return;
        }
        
    }

    /**
     * Gets the osgi classloader if any in the given set
     */
    private static ClassLoader getOsgiClassLoader(Set<ClassLoader> set) {
        for (ClassLoader loader : set) {
            if (isOsgiClassloader(loader)) {
                return loader;
            }
        }
        return null;
    }

    /**
     * Is it an osgi classloader
     */
    private static boolean isOsgiClassloader(ClassLoader loader) {
        try {
            Method mth = loader.getClass().getMethod("getBundle", new Class[]{});
            if (mth != null) {
                return true;
            }
        } catch (NoSuchMethodException e) {
            // ignore its not an osgi loader
        }
        return false;
    }
    
    private void loadImplementationsInBundle(Test test, String packageName, ClassLoader loader, Method mth) {
        // Use an inner class to avoid a NoClassDefFoundError when used in a non-osgi env
        Set<String> urls = OsgiUtil.getImplementationsInBundle(test, packageName, loader, mth);
        if (urls != null) {
            for (String url : urls) {
                // substring to avoid leading slashes
                addIfMatching(test, url);
            }
        }
    }

    private static final class OsgiUtil {
        private static final transient Log LOG = LogFactory.getLog(OsgiUtil.class);

        private OsgiUtil() {
            // Helper class
        }

        static Set<String> getImplementationsInBundle(Test test, String packageName, ClassLoader loader, Method mth) {
            try {
                Bundle bundle = (Bundle) mth.invoke(loader);                
                Bundle[] bundles = null;
                
                BundleContext bundleContext = bundle.getBundleContext();
                if (bundleContext == null) {
                    // Bundle is not in STARTING|ACTIVE|STOPPING state
                    // (See OSGi 4.1 spec, section 4.3.17)
                    bundles = new Bundle[] {bundle};
                } else {
                    bundles = bundleContext.getBundles();
                }
                
                Set<String> urls = new HashSet<String>();
                for (Bundle bd : bundles) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Searching in bundle:" + bd);
                    }
                    Enumeration<URL> paths = bd.findEntries("/" + packageName, "*.class", true);
                    while (paths != null && paths.hasMoreElements()) {
                        URL path = paths.nextElement();
                        String pathString = path.getPath();
                        pathString.indexOf(packageName);
                        urls.add(pathString.substring(pathString.indexOf(packageName)));
                    }
                }
                return urls;
            } catch (Throwable t) {
                LOG.error("Could not search osgi bundles for classes matching criteria: " + test
                          + "due to an Exception: " + t.getMessage());
                return null;
            }
        }
    }

}