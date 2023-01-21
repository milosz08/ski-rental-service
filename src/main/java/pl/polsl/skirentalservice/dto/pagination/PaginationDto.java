/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: PaginationDto.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.pagination;

import lombok.*;
import java.util.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class PaginationDto {
    private int page;
    private int totalPerPage;
    private int fromRecords;
    private int toRecords;
    private long totalRecords;
    private int allPages;
    private PaginationPage nextPage;
    private PaginationPage prevPage;
    private List<PaginationPage> pages = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public PaginationDto(int page, int totalPerPage, long totalRecords) {
        this.allPages = (int) Math.ceil(totalRecords * 1.0 / totalPerPage);
        this.page = page;
        this.totalPerPage = totalPerPage;
        this.totalRecords = totalRecords;
        this.prevPage = new PaginationPage(page == 1 ? page : page + 1, page == 1 ? "disabled" : "");
        this.nextPage = new PaginationPage(page == allPages ? page : page - 1, page == allPages ? "disabled" : "");
        this.fromRecords = (page - 1) * totalPerPage + 1;
        this.toRecords = (page - 1) * totalPerPage + totalPerPage;
        for (int i = 1; i <= this.allPages; i++) {
            pages.add(new PaginationPage(i, page == i ? "active" : ""));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean checkIfIsInvalid() {
        final int[] pages = { 10, 20, 25, 50, 100 };
        return (page > allPages || page < 1) || Arrays.stream(pages).noneMatch(p -> p == totalPerPage);
    }
}

