package com.workingbit.blocks.controller;

import com.workingbit.blocks.schema.BlockSchema;
import com.workingbit.blocks.schema.GraphqlQuery;
import graphql.ExecutionInput;
import graphql.GraphQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Route;

import java.util.Map;

import static com.workingbit.blocks.util.JsonUtils.dataToJson;
import static com.workingbit.blocks.util.JsonUtils.jsonToData;
import static graphql.GraphQL.newGraphQL;

/**
 * Created by Aleksey Popryadukhin on 01/08/2018.
 */
public class GraphqlController {

  private static Logger LOG = LoggerFactory.getLogger(GraphqlController.class);

  public static Route graphql = (req, res) -> {
    LOG.debug("req: " + req.body());
    GraphqlQuery graphqlQuery = jsonToData(req.body(), GraphqlQuery.class);
    GraphQL.Builder graphql = newGraphQL(BlockSchema.schema);
    ExecutionInput executionInput = ExecutionInput.newExecutionInput()
        .operationName(graphqlQuery.getOperationName())
        .variables(graphqlQuery.getVariables())
        .query(graphqlQuery.getQuery())
        .build();

    Map data = graphql.build().execute(executionInput).toSpecification();
    String json = dataToJson(data);
    LOG.debug("res: " + json);
    return json;
  };
}
