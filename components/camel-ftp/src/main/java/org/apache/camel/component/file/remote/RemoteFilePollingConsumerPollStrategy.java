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
package org.apache.camel.component.file.remote;

import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultPollingConsumerPollStrategy;

/**
 * Remote file consumer polling strategy that attempts to help recovering from lost connections.
 *
 * @version $Revision$
 */
public class RemoteFilePollingConsumerPollStrategy extends DefaultPollingConsumerPollStrategy {

    @Override
    public boolean rollback(Consumer consumer, Endpoint endpoint, int retryCounter, Exception e) throws Exception {
        // disconnect from the server to force it to re login at next poll to recover
        RemoteFileConsumer rfc = (RemoteFileConsumer) consumer;
        log.warn("Trying to recover by disconnecting from remote server forcing a re-connect at next poll: " + rfc.remoteServer());
        rfc.disconnect();
        return super.rollback(consumer, endpoint, retryCounter, e);
    }

}