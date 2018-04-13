package com.rush.o2o.service;

import java.io.IOException;
import java.util.List;

import com.rush.o2o.entity.HeadLine;

public interface HeadLineService {
	List<HeadLine> getHeadLineList(HeadLine headLineCondition) throws IOException;
}
