/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hop.core.parameters;

import org.apache.hop.core.exception.HopException;

/**
 * This is an Apache Hop Exception for the named parameters.
 *
 * @author Sven Boden
 * @since 28Feb2009
 */
public class NamedParamsException extends HopException {
  private static final long serialVersionUID = -6684406077955662033L;

  /** Constructs a new throwable with null as its detail message. */
  public NamedParamsException() {
    super();
  }

  /**
   * Constructs a new throwable with the specified detail message.
   *
   * @param message - the detail message. The detail message is saved for later retrieval by the
   *     getMessage() method.
   */
  public NamedParamsException(String message) {
    super(message);
  }

  /**
   * Constructs a new throwable with the specified cause and a detail message of (cause==null ? null
   * : cause.toString()) (which typically contains the class and detail message of cause).
   *
   * @param cause the cause (which is saved for later retrieval by the getCause() method). (A null
   *     value is permitted, and indicates that the cause is nonexistent or unknown.)
   */
  public NamedParamsException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new throwable with the specified detail message and cause.
   *
   * @param message the detail message (which is saved for later retrieval by the getMessage()
   *     method).
   * @param cause the cause (which is saved for later retrieval by the getCause() method). (A null
   *     value is permitted, and indicates that the cause is nonexistent or unknown.)
   */
  public NamedParamsException(String message, Throwable cause) {
    super(message, cause);
  }
}
