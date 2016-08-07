package com.neptune.api.requestdelayer.dao;

import com.neptune.api.template.dao.DAOTemplate;
import com.neptune.api.requestdelayer.domain.Value;

public interface ValueDAO extends DAOTemplate<Value> {

	Value last();
}