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

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.List;

/**
 * TESTING THIS OUT BUT DON'T INTEND TO USE IT.
 */
public class JoinSpecification implements Specification<Object> {
    List<String> leftJoinFetchTables;
    List<String> innnerJoinFetchTables;
    List<String> rightJoinFetchTables;

    public JoinSpecification() {
    }

    @Override
    public Predicate toPredicate(Root<Object> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        //because this piece of code may be run twice for pagination,
        //first time 'count' , second time 'select',
        //So, if this is called by 'count', don't join fetch tables.
        if (isCountCriteriaQuery(cq))
            return null;

        join(root, leftJoinFetchTables, JoinType.LEFT);
        join(root, innnerJoinFetchTables, JoinType.INNER);
        join(root, rightJoinFetchTables, JoinType.RIGHT);
        ((CriteriaQuery<Object>) cq).select(root);
        return null;
    }

    /*
        For Issue:
        when run repository.findAll(specs,page)
        The method toPredicate(...) upon will return a Predicate for Count(TableName) number of rows.
        In hibernate query, we cannot do "select count(table_1) from table_1 left fetch join table_2 where ..."
        Resolution:
        In this scenario, CriteriaQuery<?> is CriteriaQuery<Long>, because return type is Long.
        we don't fetch other tables where generating query for "count";
     */
    private boolean isCountCriteriaQuery(CriteriaQuery<?> cq) {
        return cq.getResultType().toString().contains("java.lang.Long");
    }


    private void join(Root<Object> root, List<String> joinFetchTables, JoinType type) {
        if (joinFetchTables != null && (joinFetchTables.size() > 0)) {
            for (String table : joinFetchTables) {
                if (table != null)
                    root.fetch(table, type);
            }
        }
    }

    public JoinSpecification setLeftJoinFetchTables(List<String> leftJoinFetchTables) {
        this.leftJoinFetchTables = leftJoinFetchTables;
        return this;
    }

    public JoinSpecification setInnerJoinFetchTables(List<String> innerJoinFetchTables) {
        this.innnerJoinFetchTables = innerJoinFetchTables;
        return this;
    }

    public JoinSpecification setRightJoinFetchTables(List<String> rightJoinFetchTables) {
        this.rightJoinFetchTables = rightJoinFetchTables;
        return this;
    }
}