package com.workingbit.blocks.schema;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import graphql.schema.DataFetcher;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.workingbit.blocks.Application.appProperties;

class BlocksData {
  private static MongoClientURI uri = new MongoClientURI(appProperties.mongoUrl());
  private static MongoClient mongoClient = new MongoClient(uri);
  private static MongoDatabase db = mongoClient.getDatabase("blocks-graphql");
  private static MongoCollection<Document> collection = db.getCollection("blocks");
  static DataFetcher blockFetcher = environment -> {
    List<Map<String, Object>> blocks = new ArrayList<>();
    FindIterable<Document> iterable = collection.find().sort(new BasicDBObject("order", -1));
    iterable.forEach((Block<Document>) document -> blocks.add(converter(document)));
    return blocks;
  };
  static DataFetcher addFetcher = environment -> {
    Object type = environment.getArguments().get("type");
    Object data = environment.getArguments().get("data");
    Object order = environment.getArguments().get("order");
    Document newBlock = new Document()
        .append("type", type)
        .append("data", data)
        .append("order", order);
    collection.insertOne(newBlock);
    return converter(newBlock);
  };
  static DataFetcher destroyFetcher = environment -> {
    String id = (String) environment.getArguments().get("id");
    List<Map<String, Object>> blocks = new ArrayList<>();
    FindIterable<Document> iterable = collection.find(
        new Document("_id", new ObjectId(id))
    );
    iterable.forEach((Block<Document>) document -> blocks.add(converter(document)));
    collection.deleteOne(new Document("_id", new ObjectId(id)));
    return blocks.get(0);
  };

//    static DataFetcher toggleFetcher = new DataFetcher() {
//        @Override
//        public Object get(DataFetchingEnvironment environment) {
//            String id = (String) environment.getArguments().get("id");
//            List<Map<String, Object>> todos = new ArrayList<Map<String, Object>>();
//            FindIterable<Document> iterable = collection.find(
//                new Document("_id", new ObjectId(id))
//            );
//            iterable.forEach(new Block<Document>() {
//                @Override
//                public void apply(final Document document) {
//                    document.append("completed", !Boolean.parseBoolean(document.get("completed").toString()));
//                    collection.updateOne(
//                        new Document("_id", new ObjectId(id)),
//                        new Document("$set", document)
//                    );
//                    todos.add(converter(document));
//                }
//            });
//            return todos.get(0);
//        }
//    };

  //    static DataFetcher toggleAllFetcher = new DataFetcher() {
//        @Override
//        public Object get(DataFetchingEnvironment environment) {
//            Boolean checked = (Boolean) environment.getArguments().get("checked");
//            Document update = new Document().append("completed", checked);
//            collection.updateMany(
//                new Document("completed", new Document("$in", Arrays.asList(true, false))),
//                new Document("$set", update)
//            );
//
//            List<Map<String, Object>> todos = new ArrayList<Map<String, Object>>();
//            FindIterable<Document> iterable = collection.find();
//            iterable.forEach(new Block<Document>() {
//                @Override
//                public void apply(final Document document) {
//                    todos.add(converter(document));
//                }
//            });
//            return todos;
//        }
//    };
  static DataFetcher saveFetcher = environment -> {
    String id = (String) environment.getArguments().get("id");
    String data = (String) environment.getArguments().get("data");
    Integer order = (Integer) environment.getArguments().get("order");
    List<Map<String, Object>> blocks = new ArrayList<>();
    FindIterable<Document> iterable = collection.find(
        new Document("_id", new ObjectId(id))
    );
    iterable.forEach((Block<Document>) document -> {
      document.append("data", data);
      document.append("order", order);
      collection.updateOne(
          new Document("_id", new ObjectId(id)),
          new Document("$set", document)
      );
      blocks.add(converter(document));
    });
    return blocks.get(0);
  };
  static DataFetcher batchSaveFetcher = environment -> {
    List blocksIn = (List) environment.getArguments().get("blocks");
    blocksIn.forEach(System.out::println);
//    Bson filter = new Document();
//    collection.updateMany(filter, blocksIn);
//    List<Map<String, Object>> blocks = new ArrayList<>();
//    FindIterable<Document> iterable = collection.find(
//        new Document("_id", new ObjectId(id))
//    );
//    iterable.forEach((Block<Document>) document -> {
//      document.append("data", data);
//      document.append("order", order);
//      collection.updateOne(
//          new Document("_id", new ObjectId(id)),
//          new Document("$set", document)
//      );
//      blocks.add(converter(document));
//    });
    return new ArrayList<>();
  };

//    static DataFetcher clearCompletedFetcher = new DataFetcher() {
//        @Override
//        public Object get(DataFetchingEnvironment environment) {
//            List<Map<String, Object>> todos = new ArrayList<Map<String, Object>>();
//            List<ObjectId> toClear = new ArrayList<ObjectId>();
//            FindIterable<Document> iterable = collection.find(
//                new Document("completed", true)
//            );
//            iterable.forEach(new Block<Document>() {
//                @Override
//                public void apply(final Document document) {
//                    toClear.add((ObjectId) document.get("_id"));
//                    todos.add(converter(document));
//                }
//            });
//            collection.deleteMany(new Document("_id", new Document("$in", toClear)));
//            return todos;
//        }
//    };

  private static Map<String, Object> converter(Document document) {
    return Map.of(
        "id", document.get("_id"),
        "type", document.get("type"),
        "data", document.get("data"),
        "order", document.get("order")
    );
  }
}