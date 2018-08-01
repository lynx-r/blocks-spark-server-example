package com.workingbit.blocks.controller;

import com.mongodb.BasicDBObject;
import com.workingbit.blocks.schema.BlockSchema;
import com.workingbit.blocks.schema.GraphqlQuery;
import graphql.ExecutionInput;
import graphql.GraphQL;
import spark.Route;

import java.util.Map;

import static com.workingbit.blocks.util.JsonUtils.jsonToData;
import static graphql.GraphQL.newGraphQL;

/**
 * Created by Aleksey Popryadukhin on 01/08/2018.
 */
public class GraphqlController {

  public static Route graphql = (req, res) -> {
    System.out.print("REQUEST: ");
    System.out.println(req.body());
    GraphqlQuery graphqlQuery = jsonToData(req.body(), GraphqlQuery.class);
    GraphQL.Builder graphql = newGraphQL(BlockSchema.schema);
    res.type("application/json");
    ExecutionInput executionInput = ExecutionInput.newExecutionInput()
        .operationName(graphqlQuery.getOperationName())
        .variables(graphqlQuery.getVariables())
        .query(graphqlQuery.getQuery())
        .build();
    Map data = graphql.build().execute(executionInput).getData();
//      Map data = graphql.build().execute(req.body()).getData();
    System.out.println(data);
    return new BasicDBObject(data).toJson();
  };
}
