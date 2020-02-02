package org.kountersign.core

import org.junit.jupiter.api.Tag

/**
 * Marks a test as a performance test
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Tag("performance")
annotation class PerformanceTest
