package org.jboss.errai.jms.shared.impl;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Type {

  public static byte DEFAULT_TYPE = 0;

  public static byte OBJECT_TYPE = 2;

  public static byte TEXT_TYPE = 3;

  public static byte BYTES_TYPE = 4;

  public static byte MAP_TYPE = 5;

  public static byte STREAM_TYPE = 6;

}