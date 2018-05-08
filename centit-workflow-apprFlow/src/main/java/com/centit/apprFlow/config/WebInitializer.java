package com.centit.apprFlow.config;

import com.centit.framework.config.SystemSpringMvcConfig;
import com.centit.framework.config.WebConfig;
import com.centit.framework.system.config.SystemBeanConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;


/**
 * Created by zou_wy on 2017/3/29.
 */


public class WebInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        initializeSpringConfig(servletContext);

        initializeSystemServletConfig(servletContext);

        initializeNormalServletConfig(servletContext);

        WebConfig.registerRequestContextListener(servletContext);

        WebConfig.registerSingleSignOutHttpSessionListener(servletContext);

//        WebConfig.registerResponseCorsFilter(servletContext);

        WebConfig.registerCharacterEncodingFilter(servletContext);

        WebConfig.registerHttpPutFormContentFilter(servletContext);

        WebConfig.registerHiddenHttpMethodFilter(servletContext);

        WebConfig.registerRequestThreadLocalFilter(servletContext);

        WebConfig.registerSpringSecurityFilter(servletContext);
    }

    /**
     * 加载Spring 配置
     * @param servletContext ServletContext
     */
    private void initializeSpringConfig(ServletContext servletContext){
        AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
        springContext.register(SystemBeanConfig.class,ServiceConfig.class);
        servletContext.addListener(new ContextLoaderListener(springContext));
    }

    /**
     * 加载Servlet 配置
     * @param servletContext ServletContext
     */
    private void initializeSystemServletConfig(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(SystemSpringMvcConfig.class);
        Dynamic system  = servletContext.addServlet("system", new DispatcherServlet(context));
        system.addMapping("/system/*");
        system.setLoadOnStartup(1);
        system.setAsyncSupported(true);
    }


    private void initializeNormalServletConfig(ServletContext servletContext) {

        AnnotationConfigWebApplicationContext contextSer = new AnnotationConfigWebApplicationContext();
        contextSer.register(NormalSpringMvcConfig.class);
        contextSer.setServletContext(servletContext);
        Dynamic service  = servletContext.addServlet("service", new DispatcherServlet(contextSer));
        service.addMapping("/service/*");
        service.setLoadOnStartup(1);
        service.setAsyncSupported(true);
        //这个过滤器保证hibernate懒加载成功
        /* javax.servlet.FilterRegistration.Dynamic corsFilter = servletContext.addFilter("hibernateFilter", OpenSessionInViewFilter.class);
        corsFilter.addMappingForUrlPatterns((EnumSet)null, false, new String[]{"*//*"});*/
    }
}
