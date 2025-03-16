package pl.polsl.skirentalservice.core.servlet.pageable;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public ServletPagination(int page, int totalPerPage, long totalRecords) {
        this.allPages = (int) Math.ceil(totalRecords * 1.0 / totalPerPage);
        this.page = page;
        this.totalPerPage = totalPerPage;
        this.totalRecords = totalRecords;
        this.prevPage = new PaginationPage(page < allPages ? page - 1 : page, page == 1 ? "disabled" : "");
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

    public boolean checkIfIsInvalid() {
        final int[] pages = {10, 20, 25, 50, 100};
        return ((page > allPages || page < 1) && allPages > 0) || Arrays.stream(pages).noneMatch(p -> p == totalPerPage);
    }
}

