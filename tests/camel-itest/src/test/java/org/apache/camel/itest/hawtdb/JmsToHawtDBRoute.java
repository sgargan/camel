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
package org.apache.camel.itest.hawtdb;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hawtdb.HawtDBAggregationRepository;

/**
 * @version 
 */
public class JmsToHawtDBRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("activemq:queue:foo")
            .transacted()
                .to("mock:input")
                //.log("Incoming ${header.group} with body ${body}")
                .aggregate(header("group"), new MyConcatAggregatationStrategy())
                    .aggregationRepository(new HawtDBAggregationRepository("events", "data/hawtdb.dat"))
                    .completionSize(10)
                    .log("Aggregated #${header.counter} ${header.group} with body ${body}")
                    .to("activemq:queue:out")
                    .to("mock:out");

        from("activemq:queue:out")
            .to("mock:result");
    }
}
