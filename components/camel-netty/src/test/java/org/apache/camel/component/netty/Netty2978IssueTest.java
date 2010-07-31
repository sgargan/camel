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
package org.apache.camel.component.netty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * @version $Revision$
 */
public class Netty2978IssueTest extends CamelTestSupport {

    @Test
    public void testNetty2978() throws Exception {
        CamelClient client = new CamelClient();
        for (int i = 0; i < 2000; i++) {
            Object reply = client.lookup(i);
            assertEquals("Bye " + i, reply);
        }
        client.close();
    }

    @Test
    public void testNetty2978Concurrent() throws Exception {
        final CamelClient client = new CamelClient();

        final List<Callable<String>> callables = new ArrayList<Callable<String>>();
        for (int count = 0; count < 2000; count++) {
            final int i = count;
            callables.add(new Callable<String>() {
                public String call() {
                    return client.lookup(i);
                }
            });
        }

        final ExecutorService executorService = Executors.newFixedThreadPool(20);
        final List<Future<String>> results = executorService.invokeAll(callables);
        final Set<String> replies = new HashSet<String>();
        for (Future<String> future : results) {
            String reply = future.get();
            assertTrue(reply.startsWith("Bye "));
            replies.add(reply);
        }
        client.close();

        // should be 2000 unique replies
        assertEquals(2000, replies.size());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("netty:tcp://localhost:2048?sync=true")
                        .process(new Processor() {
                            public void process(final Exchange exchange) {
                                String body = exchange.getIn().getBody(String.class);
                                exchange.getOut().setBody("Bye " + body);
                            }
                        });
            }
        };
    }

    private final class CamelClient {
        private final CamelContext context;
        private final Endpoint endpoint;
        private final ProducerTemplate producerTemplate;

        public CamelClient() {
            this.context = new DefaultCamelContext();
            this.endpoint = context.getEndpoint("netty:tcp://localhost:2048?sync=true");
            this.producerTemplate = context.createProducerTemplate();
        }

        public void close() throws Exception {
            producerTemplate.stop();
            context.stop();
        }

        public String lookup(int num) {
            return producerTemplate.requestBody(endpoint, num, String.class);
        }

    }


}