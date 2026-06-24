package com.techmart.util;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * Binding annotation for performance tracking interceptor.
 */
@Inherited
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface PerformanceTracked {
}
