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
import java.util.HashMap;
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
    FindIterable<Document> iterable = collection.find().sort(new BasicDBObject("_id", -1));
    iterable.forEach((Block<Document>) document -> blocks.add(converter(document)));
    return blocks;
  };
  static DataFetcher addFetcher = environment -> {
    Object type = environment.getArguments().get("type");
    Object data = environment.getArguments().get("data");
    Document newBlock = new Document()
        .append("type", type)
        .append("data", data);
    collection.insertOne(newBlock);
    return converter(newBlock);
  };
  static DataFetcher destroyFetcher = environment -> {
    String id = (String) environment.getArguments().get("id");
    List<Map<String, Object>> todos = new ArrayList<>();
    FindIterable<Document> iterable = collection.find(
        new Document("_id", new ObjectId(id))
    );
    iterable.forEach((Block<Document>) document -> todos.add(converter(document)));
    collection.deleteOne(new Document("_id", new ObjectId(id)));
    return todos.get(0);
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
    List<Map<String, Object>> blocks = new ArrayList<>();
    FindIterable<Document> iterable = collection.find(
        new Document("_id", new ObjectId(id))
    );
    iterable.forEach((Block<Document>) document -> {
      document.append("data", data);
      collection.updateOne(
          new Document("_id", new ObjectId(id)),
          new Document("$set", document)
      );
      blocks.add(converter(document));
    });
    return blocks.get(0);
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
    return new HashMap<>() {{
      put("id", document.get("_id"));
      put("type", document.get("type"));
      put("data", document.get("data"));
    }};
  }
}