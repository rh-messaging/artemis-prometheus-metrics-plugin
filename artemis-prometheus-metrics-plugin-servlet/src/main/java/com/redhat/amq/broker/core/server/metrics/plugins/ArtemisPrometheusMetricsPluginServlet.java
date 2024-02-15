/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.amq.broker.core.server.metrics.plugins;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ArtemisPrometheusMetricsPluginServlet extends HttpServlet {

   private PrometheusMeterRegistry registry;

   public ArtemisPrometheusMetricsPluginServlet() {
      locateRegistry();
   }

   private PrometheusMeterRegistry locateRegistry() {
      if (registry == null) {
         final Set<MeterRegistry> registries = Metrics.globalRegistry.getRegistries();
         if (registries != null && !registries.isEmpty()) {
            for (final MeterRegistry meterRegistry : registries) {
               if (meterRegistry instanceof PrometheusMeterRegistry) {
                  registry = (PrometheusMeterRegistry) meterRegistry;
                  break;
               }
            }
         }
      }
      return registry;
   }

   @Override
   protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
      resp.setStatus(HttpServletResponse.SC_OK);

      if (locateRegistry() == null) {
         resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Prometheus meter registry is null. Has the Prometheus Metrics Plugin been configured?");
      } else {
         try (Writer writer = resp.getWriter()) {
            writer.write(registry.scrape());
            writer.flush();
         }
      }
   }

   @Override
   protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
      doGet(req, resp);
   }
}
