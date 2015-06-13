package org.rrabarg.teamcaptain.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    Environment env;

    @PostConstruct
    public void hello() {
        log.info("logging aspect configured");
        log.info("Environment is " + env);
    }

    @AfterReturning(pointcut = "execution(* org.rrabarg.teamcaptain.*Service.*(..))", returning = "retval")
    public void logService(JoinPoint point, Object retval) {
        log.debug(point.getSignature().toShortString() + " returned " + retval);
    }

    @AfterReturning(pointcut = "execution(* org.rrabarg.teamcaptain.*Repository.*(..))", returning = "retval")
    public void logRepository(JoinPoint point, Object retval) {
        log.debug(point.getSignature().toShortString() + " returned " + retval);
    }

    @After("execution(* *Steps.*(..))")
    public void logRepository(JoinPoint point) {
        log.debug("Step " + point.getSignature().toShortString());
    }

    @AfterThrowing(pointcut = "execution(* org.rrabarg.teamcaptain.*.*(..))", throwing = "throwval")
    public void logThrowing(JoinPoint point, Object throwval) {
        log.debug(point.getSignature().toShortString() + " throws " + throwval);
    }

}
