package com.workingbit.blocks.schema;

import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

public class BlockSchema {

  private static GraphQLObjectType blockText = newObject()
      .name("blockText")
      .field(newFieldDefinition()
          .type(GraphQLString)
          .name("id")
          .description("Id текстового блока")
          .build())
      .field(newFieldDefinition()
          .type(GraphQLString)
          .name("type")
          .description("Тип блока")
          .build())
      .field(newFieldDefinition()
          .type(GraphQLString)
          .name("data")
          .description("Строка данных")
          .build())
      .build();

  private static GraphQLObjectType queryType = newObject()
      .name("Block")
      .field(newFieldDefinition()
          .type(new GraphQLList(blockText))
          .name("blocks")
          .description("Блоки")
          .dataFetcher(BlocksData.blockFetcher)
          .build())
      .build();

  private static GraphQLObjectType mutationType = newObject()
      .name("Blocks")
      .field(newFieldDefinition()
          .type(blockText)
          .name("add")
          .description("Добавить блок")
          .argument(newArgument()
              .name("type")
              .description("Тип блока")
              .type(new GraphQLNonNull(GraphQLString))
              .build())
          .argument(newArgument()
              .name("data")
              .description("Данные блока")
              .type(new GraphQLNonNull(GraphQLString))
              .build())
          .dataFetcher(BlocksData.addFetcher)
          .build())

//        .field(newFieldDefinition()
//            .type(blockText)
//            .name("toggle")
//            .description("toggle the todo")
//            .argument(newArgument()
//                .name("id")
//                .description("todo id")
//                .type(new GraphQLNonNull(GraphQLString))
//                .build())
//            .dataFetcher(TodoData.toggleFetcher)
//            .build())
//
//        .field(newFieldDefinition()
//            .type(new GraphQLList(blockText))
//            .name("toggleAll")
//            .description("toggle all todos")
//            .argument(newArgument()
//                .name("checked")
//                .description("checked flag")
//                .type(new GraphQLNonNull(GraphQLBoolean))
//                .build())
//            .dataFetcher(TodoData.toggleAllFetcher)
//            .build())

      .field(newFieldDefinition()
          .type(blockText)
          .name("destroy")
          .description("Удалить блок")
          .argument(newArgument()
              .name("id")
              .description("Id блока")
              .type(new GraphQLNonNull(GraphQLString))
              .build())
          .dataFetcher(BlocksData.destroyFetcher)
          .build())

//        .field(newFieldDefinition()
//            .type(new GraphQLList(blockText))
//            .name("clearCompleted")
//            .description("clear all completed todos")
//            .dataFetcher(TodoData.clearCompletedFetcher)
//            .build())

      .field(newFieldDefinition()
          .type(blockText)
          .name("save")
          .description("Редактировать блок")
          .argument(newArgument()
              .name("id")
              .description("Id блока")
              .type(new GraphQLNonNull(GraphQLString))
              .build())
          .argument(newArgument()
              .name("text")
              .description("Текст блока")
              .type(new GraphQLNonNull(GraphQLString))
              .build())
          .dataFetcher(BlocksData.saveFetcher)
          .build())

      .build();


  public static GraphQLSchema schema = GraphQLSchema.newSchema()
      .query(queryType)
      .mutation(mutationType)
      .build();
}