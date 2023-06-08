/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ServletPagination.java
 * Last modified: 3/12/23, 11:01 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.polsl.skirentalservice.paging.pagination;

import lombok.Data;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class ServletPagination {
    private int page;
    private int totalPerPage;
    private int fromRecords;
    private int toRecords;
    private long totalRecords;
    private int allPages;
    private PaginationPage nextPage;
    private PaginationPage prevPage;
    private List<PaginationPage> pages = new ArrayList<>();
    private List<PaginationPage> selectPages = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ServletPagination(int page, int totalPerPage, long totalRecords) {
        this.allPages = (int) Math.ceil(totalRecords * 1.0 / totalPerPage);
        this.page = page;
        this.totalPerPage = totalPerPage;
        this.totalRecords = totalRecords;
        this.prevPage = new PaginationPage(page < allPages ? page - 1: page, page == 1 ? "disabled" : "");
        this.nextPage = new PaginationPage(page >= 1 ? page + 1 : page, page == allPages ? "disabled" : "");
        this.fromRecords = (page - 1) * totalPerPage + 1;
        this.toRecords = (page - 1) * totalPerPage + totalPerPage;
        int start = page - 2;
        int end = page + 2;
        if (end > allPages) {
            start -= (end - allPages);
            end = allPages;
        }
        if (start <= 0) {
            end += ((start - 1) * -1);
            start = 1;
        }
        for (int i = start; i <= Math.min(end, allPages); i++) {
            pages.add(new PaginationPage(i, page == i ? "active" : ""));
        }
        for (int i = 1; i <= allPages; i++) {
            selectPages.add(new PaginationPage(i, page == i ? "selected" : ""));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean checkIfIsInvalid() {
        final int[] pages = { 10, 20, 25, 50, 100 };
        return ((page > allPages || page < 1) && allPages > 0) || Arrays.stream(pages).noneMatch(p -> p == totalPerPage);
    }
}

