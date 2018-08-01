package com.workingbit.blocks.config;

import java.net.URL;

/**
 * Created by Aleksey Popryadukhin on 31/07/2018.
 */
public interface AppProperties {

  String mongoUrl();

  URL origin();

  String methods();

  String headers();

  int port();
}
