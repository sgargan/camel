/**
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
package org.apache.camel.scala.dsl

import builder.RouteBuilder
import org.apache.camel.model.ThreadsDefinition
import java.util.concurrent.ExecutorService
import org.apache.camel.WaitForTaskToComplete;

/**
 * Scala enrichment for Camel's ThreadsDefinition
 */
case class SThreadsDefinition(override val target: ThreadsDefinition)(implicit val builder: RouteBuilder) extends SAbstractDefinition[ThreadsDefinition] {

  def executorService(service: ExecutorService) = wrap(target.executorService(service))
  def executorService(ref: String) = wrap(target.executorServiceRef(ref))

  def poolSize(size: Int) = wrap(target.poolSize(size))

  def waitForTaskToComplete(wait: WaitForTaskToComplete) = wrap(target.waitForTaskToComplete(wait))

  override def wrap(block: => Unit) = super.wrap(block).asInstanceOf[SThreadsDefinition]

}
  