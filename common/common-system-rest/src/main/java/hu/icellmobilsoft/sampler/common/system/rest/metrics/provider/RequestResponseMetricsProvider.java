/*-
 * #%L
 * Sampler
 * %%
 * Copyright (C) 2022 i-Cell Mobilsoft Zrt.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package hu.icellmobilsoft.sampler.common.system.rest.metrics.provider;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.Timer;

import hu.icellmobilsoft.sampler.dto.path.SamplePath;

/**
 * JAXRS provider for handling request/response metrics
 * 
 * @author mark.petrenyi
 * @since 0.1.0
 */
@Provider
public class RequestResponseMetricsProvider implements ContainerRequestFilter, WriterInterceptor {

    @Inject
    private MetricsContainer metricsContainer;

    @Inject
    private MetricRegistry metricRegistry;

    @Context
    private HttpServletResponse httpServletResponse;

    @Context
    private UriInfo uriInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String url = uriInfo.getAbsolutePath().toASCIIString();
        boolean standardHttp = StringUtils.containsAny(requestContext.getMethod(), HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
                HttpMethod.DELETE);
        if (standardHttp && StringUtils.contains(url, SamplePath.PREFIX_REST)) {
            metricsContainer.setStartTime(LocalDateTime.now());
        }
    }

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        String url = uriInfo.getAbsolutePath().toASCIIString();
        if (metricsContainer.getStartTime() != null && StringUtils.contains(url, SamplePath.PREFIX_REST)) {
            updateResponseTimer(url);
        }
        context.proceed();
    }

    /**
     * {@value MetricsConstants.Timer#SAMPLE} metric refresh
     *
     * @param url
     *            http request url
     */
    private void updateResponseTimer(String url) {
        Metadata metadata = Metadata.builder() //
                .withName("sample_http_response_time").withDescription("Input HTTP sample request count and response times.")
                .withType(MetricType.TIMER).withUnit(MetricUnits.MILLISECONDS).build();
        Tag responseCodeTag = new Tag("url", url);

        Timer timer = metricRegistry.timer(metadata, responseCodeTag);
        timer.update(Duration.between(metricsContainer.getStartTime(), LocalDateTime.now()));
    }
}
