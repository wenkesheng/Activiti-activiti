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

package org.activiti.services.query.app;

import org.activiti.services.query.app.dao.TaskRepository;
import org.activiti.services.query.app.resources.TaskQueryResource;
import org.activiti.services.query.app.specification.builders.TaskSpecificationsBuilder;
import org.activiti.services.query.app.model.Task;
import org.activiti.services.query.app.assembler.TaskQueryResourceAssembler;
import org.activiti.services.query.app.util.SearchTermResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.data.jpa.domain.Specification;
import static org.activiti.services.query.app.specification.builder.SpecificationBuilder.selectFrom;

import java.util.List;


@Controller
@RequestMapping(value = "/query/tasks", produces = MediaTypes.HAL_JSON_VALUE)
public class TaskQueryController {

    @Autowired
    private TaskRepository dao;

    @Autowired
    private TaskQueryResourceAssembler taskResourceAssembler;

    //this uses specification builder approach, supports OR and AND but not queries that go to associated collection
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public PagedResources<TaskQueryResource> findAllPaginated(@RequestParam(value = "search", required = false) String search, Pageable pageable, PagedResourcesAssembler<Task> pagedResourcesAssembler) {

        Specification<Task> spec = new SearchTermResolver<Task>().applyBuilderToSearchTerm(search,new TaskSpecificationsBuilder());
        return pagedResourcesAssembler.toResource(dao.findAll(spec,pageable), taskResourceAssembler);
    }

    //gives Illegal attempt to dereference path source [null.variables] when hitting query/tasks/filter?filter=priority~eq~20~and~variables.name~eq~bob
    @RequestMapping(method = RequestMethod.GET, value = "filter")
    @ResponseBody
    public PagedResources<TaskQueryResource> join(@RequestParam(value = "filter",required = false) String queryString, Pageable pageable, PagedResourcesAssembler<Task> pagedResourcesAssembler){
        Page<Task> tasks = selectFrom(dao).leftJoin("variables").where(queryString).findPage(pageable);
        return pagedResourcesAssembler.toResource(tasks, taskResourceAssembler);
    }

    //TODO: implement for a single task using findOne and include links from the findAll to individual records like runtime does - see TaskResourceAssembler


}
