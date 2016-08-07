package com.neptune.api.requestdelayer.service;

import com.neptune.api.requestdelayer.domain.Value;
import com.neptune.api.template.service.ServiceTemplate;

public interface ValueService extends ServiceTemplate<Value> {

    Value last();

}