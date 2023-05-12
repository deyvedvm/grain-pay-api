package dev.deyve.grainpayapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.util.Objects;

@SpringBootApplication
public class GrainPayApiApplication {

    private static final Logger logger = LoggerFactory.getLogger(GrainPayApiApplication.class);

    private static final String SERVER_SERVLET_CONTEXT_PATH = "server.servlet.context-path";

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(GrainPayApiApplication.class);

        ConfigurableEnvironment environment = springApplication.run(args).getEnvironment();

        var serverPort = environment.getProperty("server.port");
        var protocol = getProtocol(environment);
        var hostAddress = getHostAddress();
        var contextPath = getContextPath(environment);

        logger.info("Application: {} is running", environment.getProperty("spring.application.name"));
        logger.info("Profile: {}", (Object) environment.getActiveProfiles());
        logger.info("Java version: {}", environment.getProperty("java.version"));
        logger.info("Local: {}", new StringBuilder()
                .append(protocol)
                .append("://localhost:")
                .append(serverPort)
                .append(contextPath));
        logger.info("External: {}", new StringBuilder()
                .append(protocol)
                .append("://")
                .append(hostAddress)
                .append(":")
                .append(serverPort)
                .append(contextPath));
    }

    private static String getProtocol(ConfigurableEnvironment environment) {
        if (environment.getProperty("server.ssl.key-store") != null) {
            return "https";
        } else {
            return "http";
        }
    }

    private static String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            logger.error("Get Host Address Error: {}", e.getMessage());
            return "localhost";
        }
    }

    private static String getContextPath(ConfigurableEnvironment environment) {
        if (environment.getProperty(SERVER_SERVLET_CONTEXT_PATH) == null || Objects.requireNonNull(environment.getProperty(SERVER_SERVLET_CONTEXT_PATH)).isEmpty()) {
            return "/";
        } else {
            return environment.getProperty(SERVER_SERVLET_CONTEXT_PATH);
        }
    }
}
