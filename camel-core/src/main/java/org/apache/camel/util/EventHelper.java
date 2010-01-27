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
package org.apache.camel.util;

import java.util.EventObject;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.impl.ServiceSupport;
import org.apache.camel.spi.EventFactory;
import org.apache.camel.spi.EventNotifier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper for easily sending event notifications in a single line of code
 *
 * @version $Revision$
 */
public final class EventHelper {

    private static final Log LOG = LogFactory.getLog(EventHelper.class);

    private EventHelper() {
    }

    public static void notifyCamelContextStarting(CamelContext context) {
        List<EventNotifier> notifiers = context.getManagementStrategy().getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (notifier.isIgnoreCamelContextEvents()) {
                continue;
            }

            EventFactory factory = context.getManagementStrategy().getEventFactory();
            if (factory == null) {
                return;
            }
            EventObject event = factory.createCamelContextStartingEvent(context);
            if (event == null) {
                return;
            }
            doNotifyEvent(notifier, event);
        }
    }

    public static void notifyCamelContextStarted(CamelContext context) {
        List<EventNotifier> notifiers = context.getManagementStrategy().getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (notifier.isIgnoreCamelContextEvents()) {
                continue;
            }

            EventFactory factory = context.getManagementStrategy().getEventFactory();
            if (factory == null) {
                return;
            }
            EventObject event = factory.createCamelContextStartedEvent(context);
            if (event == null) {
                return;
            }
            doNotifyEvent(notifier, event);
        }
    }

    public static void notifyCamelContextStartupFailed(CamelContext context, Exception cause) {
        List<EventNotifier> notifiers = context.getManagementStrategy().getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (notifier.isIgnoreCamelContextEvents()) {
                continue;
            }

            EventFactory factory = context.getManagementStrategy().getEventFactory();
            if (factory == null) {
                return;
            }
            EventObject event = factory.createCamelContextStartupFailureEvent(context, cause);
            if (event == null) {
                return;
            }
            doNotifyEvent(notifier, event);
        }
    }

    public static void notifyCamelContextStopping(CamelContext context) {
        List<EventNotifier> notifiers = context.getManagementStrategy().getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (notifier.isIgnoreCamelContextEvents()) {
                return;
            }

            EventFactory factory = context.getManagementStrategy().getEventFactory();
            if (factory == null) {
                return;
            }
            EventObject event = factory.createCamelContextStoppingEvent(context);
            if (event == null) {
                return;
            }
            doNotifyEvent(notifier, event);
        }
    }

    public static void notifyCamelContextStopped(CamelContext context) {
        List<EventNotifier> notifiers = context.getManagementStrategy().getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (notifier.isIgnoreCamelContextEvents()) {
                continue;
            }

            EventFactory factory = context.getManagementStrategy().getEventFactory();
            if (factory == null) {
                return;
            }
            EventObject event = factory.createCamelContextStoppedEvent(context);
            if (event == null) {
                return;
            }
            doNotifyEvent(notifier, event);
        }
    }

    public static void notifyCamelContextStopFailed(CamelContext context, Exception cause) {
        List<EventNotifier> notifiers = context.getManagementStrategy().getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (notifier.isIgnoreCamelContextEvents()) {
                continue;
            }

            EventFactory factory = context.getManagementStrategy().getEventFactory();
            if (factory == null) {
                return;
            }
            EventObject event = factory.createCamelContextStopFailureEvent(context, cause);
            if (event == null) {
                return;
            }
            doNotifyEvent(notifier, event);
        }
    }

    public static void notifyServiceStopFailure(CamelContext context, Object service, Exception cause) {
        List<EventNotifier> notifiers = context.getManagementStrategy().getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (notifier.isIgnoreServiceEvents()) {
                continue;
            }

            EventFactory factory = context.getManagementStrategy().getEventFactory();
            if (factory == null) {
                return;
            }
            EventObject event = factory.createServiceStopFailureEvent(context, service, cause);
            if (event == null) {
                return;
            }
            doNotifyEvent(notifier, event);
        }
    }

    public static void notifyServiceStartupFailure(CamelContext context, Object service, Exception cause) {
        List<EventNotifier> notifiers = context.getManagementStrategy().getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (notifier.isIgnoreServiceEvents()) {
                continue;
            }

            EventFactory factory = context.getManagementStrategy().getEventFactory();
            if (factory == null) {
                return;
            }
            EventObject event = factory.createServiceStartupFailureEvent(context, service, cause);
            if (event == null) {
                return;
            }
            doNotifyEvent(notifier, event);
        }
    }

    public static void notifyRouteStarted(CamelContext context, Route route) {
        List<EventNotifier> notifiers = context.getManagementStrategy().getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (notifier.isIgnoreRouteEvents()) {
                return;
            }

            EventFactory factory = context.getManagementStrategy().getEventFactory();
            if (factory == null) {
                return;
            }
            EventObject event = factory.createRouteStartedEvent(route);
            if (event == null) {
                return;
            }
            doNotifyEvent(notifier, event);
        }
    }

    public static void notifyRouteStopped(CamelContext context, Route route) {
        List<EventNotifier> notifiers = context.getManagementStrategy().getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (notifier.isIgnoreRouteEvents()) {
                continue;
            }

            EventFactory factory = context.getManagementStrategy().getEventFactory();
            if (factory == null) {
                return;
            }
            EventObject event = factory.createRouteStoppedEvent(route);
            if (event == null) {
                return;
            }
            doNotifyEvent(notifier, event);
        }
    }

    public static void notifyExchangeCreated(CamelContext context, Exchange exchange) {
        List<EventNotifier> notifiers = context.getManagementStrategy().getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (notifier.isIgnoreExchangeEvents() || notifier.isIgnoreExchangeCreatedEvent()) {
                continue;
            }

            EventFactory factory = context.getManagementStrategy().getEventFactory();
            if (factory == null) {
                return;
            }
            EventObject event = factory.createExchangeCreatedEvent(exchange);
            if (event == null) {
                return;
            }
            doNotifyEvent(notifier, event);
        }
    }

    public static void notifyExchangeDone(CamelContext context, Exchange exchange) {
        List<EventNotifier> notifiers = context.getManagementStrategy().getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (notifier.isIgnoreExchangeEvents() || notifier.isIgnoreExchangeCompletedEvent()) {
                continue;
            }

            EventFactory factory = context.getManagementStrategy().getEventFactory();
            if (factory == null) {
                return;
            }
            EventObject event = factory.createExchangeCompletedEvent(exchange);
            if (event == null) {
                return;
            }
            doNotifyEvent(notifier, event);
        }
    }

    public static void notifyExchangeFailed(CamelContext context, Exchange exchange) {
        List<EventNotifier> notifiers = context.getManagementStrategy().getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (notifier.isIgnoreExchangeEvents() || notifier.isIgnoreExchangeFailureEvents()) {
                continue;
            }

            EventFactory factory = context.getManagementStrategy().getEventFactory();
            if (factory == null) {
                return;
            }
            EventObject event = factory.createExchangeFailureEvent(exchange);
            if (event == null) {
                return;
            }
            doNotifyEvent(notifier, event);
        }
    }

    public static void notifyExchangeFailureHandled(CamelContext context, Exchange exchange, Processor failureHandler,
                                                    boolean deadLetterChannel) {
        List<EventNotifier> notifiers = context.getManagementStrategy().getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (notifier.isIgnoreExchangeEvents() || notifier.isIgnoreExchangeFailureEvents()) {
                continue;
            }

            EventFactory factory = context.getManagementStrategy().getEventFactory();
            if (factory == null) {
                return;
            }
            EventObject event = factory.createExchangeFailureHandledEvent(exchange, failureHandler, deadLetterChannel);
            if (event == null) {
                return;
            }
            doNotifyEvent(notifier, event);
        }
    }

    private static void doNotifyEvent(EventNotifier notifier, EventObject event) {
        // only notify if notifier is started
        boolean started = true;
        if (notifier instanceof ServiceSupport) {
            started = ((ServiceSupport) notifier).isStarted();
        }
        if (!started) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ignoring notifying event " + event + ". The EventNotifier has not been started yet: " + notifier);
            }
            return;
        }

        if (!notifier.isEnabled(event)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Notification of event is disabled: " + event);
            }
            return;
        }

        try {
            notifier.notify(event);
        } catch (Exception e) {
            LOG.warn("Error notifying event " + event + ". This exception will be ignored. ", e);
        }
    }

}