package com.workingbit.blocks.schema;

import lombok.Data;

import java.util.Map;

/**
 * Created by Aleksey Popryadukhin on 01/08/2018.
 */
@Data
public class GraphqlQuery {
  private String operationName;
  private Map<String, Object> variables;
  private String query;
}
