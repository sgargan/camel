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
package org.apache.camel.processor.async;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

/**
 * @version 
 */
public class AsyncEndpointTryCatchFinally2Test extends ContextTestSupport {

    private static String beforeThreadName;
    private static String middleThreadName;
    private static String afterThreadName;

    public void testAsyncEndpoint() throws Exception {
        getMockEndpoint("mock:before").expectedBodiesReceived("Hello Camel");
        getMockEndpoint("mock:catch").expectedBodiesReceived("Hello Camel");
        getMockEndpoint("mock:after").expectedBodiesReceived("Bye World");
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye World");

        String reply = template.requestBody("direct:start", "Hello Camel", String.class);
        assertEquals("Bye World", reply);

        assertMockEndpointsSatisfied();

        assertFalse("Should use different threads", beforeThreadName.equalsIgnoreCase(middleThreadName));
        assertFalse("Should use different threads", beforeThreadName.equalsIgnoreCase(afterThreadName));
        assertFalse("Should use different threads", middleThreadName.equalsIgnoreCase(afterThreadName));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.addComponent("async", new MyAsyncComponent());

                from("direct:start")
                        .to("mock:before")
                        .to("log:before")
                        .doTry()
                            .process(new Processor() {
                                public void process(Exchange exchange) throws Exception {
                                    beforeThreadName = Thread.currentThread().getName();
                                }
                            })
                            .to("async:Bye Camel?failFirstAttempts=1")
                        .doCatch(Exception.class)
                            .to("log:catch")
                            .to("mock:catch")
                            .process(new Processor() {
                                public void process(Exchange exchange) throws Exception {
                                    middleThreadName = Thread.currentThread().getName();
                                }
                            })
                            .to("async:Bye World")
                        .doFinally()
                            .to("log:finally")
                            .process(new Processor() {
                                public void process(Exchange exchange) throws Exception {
                                    afterThreadName = Thread.currentThread().getName();
                                }
                            })
                            .to("log:after")
                            .to("mock:after")
                        .end()
                        .to("mock:result");
            }
        };
    }

}