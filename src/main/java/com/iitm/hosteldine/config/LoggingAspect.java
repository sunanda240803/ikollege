package com.iitm.hosteldine.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.iitm.hosteldine..*(..))")
    public void includeAll() {}

    @Pointcut("execution(* com.iitm.hosteldine.repository..*(..))")
    public void excludePackage() {}

    @Pointcut("""
    execution(* com.iitm.hosteldine.config.MyAuthenticationProvider.authenticate(..))
 || execution(* com.iitm.hosteldine.config.DynamicSecurityService.getSchoolGeographyInfoDto())
 || execution(* com.iitm.hosteldine.util.SecureFileService..*(..))
 || execution(* com.iitm.hosteldine.util.Utility..*(..))
 || execution(* com.iitm.hosteldine.controller.GlobalModelController..*(..))
 || execution(* com.iitm.hosteldine.controller.LogController..*(..))
 || execution(* com.iitm.hosteldine.controller.ArchiveController.getProgress(..))
 || execution(* com.iitm.hosteldine.service.InMemoryLogService..*(..))
 || execution(* com.iitm.hosteldine.service.ArchiveService.getProgress(..))
 || execution(* com.iitm.hosteldine.service.FileService.encodeFile(..))
 || execution(* com.iitm.hosteldine.service.SimsConfigDataService..*(..))
 """)
    public void excludeMethods() {}

    @Pointcut("includeAll() && !excludePackage() && !excludeMethods()")
    public void applicationPackagePointcut() {}

    @Before("applicationPackagePointcut()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Entering : {}", joinPoint.getSignature().toShortString());
    }

    @After("applicationPackagePointcut()")
    public void logAfter(JoinPoint joinPoint) {
        log.info("Exiting : {}", joinPoint.getSignature().toShortString());
    }
}
