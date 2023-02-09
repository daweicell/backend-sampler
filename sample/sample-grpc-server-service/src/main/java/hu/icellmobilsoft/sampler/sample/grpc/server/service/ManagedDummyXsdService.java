/*-
 * #%L
 * Sampler
 * %%
 * Copyright (C) 2022 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.sampler.sample.grpc.server.service;

import hu.icellmobilsoft.sampler.sample.grpc.server.service.action.SampleGrpcAction;
import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Wrapper class to handle CDI context
 * 
 * @author balazs.joo
 * @since 2.0.0
 *
 */
@ApplicationScoped
public class ManagedDummyXsdService implements BindableService, IGrpcService {

    private DummyXsdServiceImpl service;

    public ManagedDummyXsdService() {
    }

    @Inject
    public ManagedDummyXsdService(SampleGrpcAction sampleCdiAction) {
        // nem lehet producert irni mivel az api megköti
        // WELD-001480: Bean type class is not proxyable because it contains a final method
        this.service = new DummyXsdServiceImpl(sampleCdiAction);
    }

    @Override
    public ServerServiceDefinition bindService() {
        // delegaljuk a grpc endpoint hivast
        return service.bindService();
    }

}