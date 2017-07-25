/*
 * Copyright 2017 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.services.query.app.specification.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * TESTING THIS OUT BUT DON'T INTEND TO USE IT.
 */
public class SpecificationImpl implements Specification<Object> {
    private static final Logger logger = LoggerFactory.getLogger(SpecificationImpl.class);
    private List<Specification> specifications = new LinkedList<>();


    @Override
    public Predicate toPredicate(Root<Object> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        for(Specification specification: specifications){
            Predicate p = specification.toPredicate(root, cq, cb);
            if(p!=null)
                predicates.add(p);
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    public void add(Specification<Object> specification){
        specifications.add(specification);
    }

}
