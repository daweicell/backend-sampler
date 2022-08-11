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
package hu.icellmobilsoft.sampler.sample.redisservice.rest;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.sampler.api.jee.rest.ISampleRest;
import hu.icellmobilsoft.sampler.common.system.rest.rest.BaseRestService;
import hu.icellmobilsoft.sampler.dto.sample.rest.post.SampleRequest;
import hu.icellmobilsoft.sampler.dto.sample.rest.post.SampleResponse;
import hu.icellmobilsoft.sampler.sample.redisservice.action.RedisSampleGetAction;
import hu.icellmobilsoft.sampler.sample.redisservice.action.RedisSamplePostAction;

/**
 * sample service redis implementation
 *
 * @author mark.petrenyi
 * @since 0.1.0
 */
@Model
public class RedisSampleRest extends BaseRestService implements ISampleRest {

    @Inject
    private RedisSampleGetAction redisSampleGetAction;

    @Inject
    private RedisSamplePostAction redisSamplePostAction;

    @Override
    public SampleResponse getSample() throws BaseException {
        return wrapNoParam(redisSampleGetAction::sample, "getSample");
    }

    @Override
    public SampleResponse postSample(SampleRequest sampleRequest) throws BaseException {
        return wrapPathParam1(redisSamplePostAction::sampleWriteRead, sampleRequest, "postSample", "sampleRequest");
    }
}
