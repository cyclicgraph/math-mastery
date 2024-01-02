package com.cyclicgraph.masterymath.util;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserEntitySecurityContextFactory.class)
public @interface WithMockUserEntity {

    String username() default "user";

    String[] roles() default {"USER"};

    String password() default "password";

}

