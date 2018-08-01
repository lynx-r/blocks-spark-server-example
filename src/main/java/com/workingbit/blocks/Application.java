package com.workingbit.blocks;

import com.workingbit.blocks.config.AppProperties;
import com.workingbit.blocks.controller.GraphqlController;
import com.workingbit.blocks.util.SparkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.workingbit.blocks.config.Config4j.configurationProvider;
import static com.workingbit.blocks.config.CorsConfig.enableCors;
import static spark.Spark.port;
import static spark.Spark.post;

public class Application {

  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  public static AppProperties appProperties;

  static {
    appProperties = configurationProvider("application.yaml").bind("app", AppProperties.class);
  }

  public static void main(String[] args) {
    port(appProperties.port());
    start();
  }

  private static void start() {
    LOG.info("Initializing routes");

    Logger logger = LoggerFactory.getLogger(Application.class);
    SparkUtils.createServerWithRequestLog(logger);
    enableCors(appProperties.origin().toString(), appProperties.methods(), appProperties.headers());
    establishRoutes();
    LOG.info("Started");
  }

  private static void establishRoutes() {
    post("/graphql", GraphqlController.graphql);
  }
}