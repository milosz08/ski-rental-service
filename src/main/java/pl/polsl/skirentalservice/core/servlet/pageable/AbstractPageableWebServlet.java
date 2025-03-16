package pl.polsl.skirentalservice.core.servlet.pageable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.servlet.AbstractWebServlet;
import pl.polsl.skirentalservice.core.servlet.HttpMethodMode;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.WebServletResponse;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.util.*;

public abstract class AbstractPageableWebServlet extends AbstractWebServlet {
    private Map<String, ServletSorterField> sorterFieldMap = new HashMap<>();
    private List<FilterColumn> filterFieldMap = new ArrayList<>();
    private String defaultSorterColumn;
    private PageableAttributes pageableAttributes;

    protected AbstractPageableWebServlet(ServerConfigBean serverConfigBean) {
        super(serverConfigBean);
    }

    @Override
    public void init() {
        sorterFieldMap = configureServletSorterFields();
        filterFieldMap = configureServletFilterFields();
        defaultSorterColumn = defaultSorterColumn();
        pageableAttributes = setPageableAttributes();
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final PaginationData data = getPaginationDataFromRequest(req);
        final SorterDataDto sorterDataDto = generateSortingJPQuery(req);
        final FilterDataDto filterDataDto = generateFilterJPQuery(req);

        req.addAttribute("sorterData", sorterFieldMap);
        req.addAttribute("filterData", filterDataDto);

        final PageableDto pageableDto = PageableDto.builder()
            .page(data.page())
            .total(data.total())
            .sorterData(sorterDataDto)
            .filterData(filterDataDto)
            .build();
        return onFetchPageableData(req, pageableDto);
    }

    @Override
    protected WebServletResponse httpPostCall(WebServletRequest req) {
        final PaginationData data = getPaginationDataFromRequest(req);
        final String redirectOnPostCall = setRedirectOnPostCall(req);
        generateSortingJPQuery(req);
        generateFilterJPQuery(req);
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo(redirectOnPostCall + "?page=" + data.page() + "&total=" + data.total())
            .build();
    }

    private PaginationData getPaginationDataFromRequest(WebServletRequest req) {
        return PaginationData.builder()
            .page(getQueryParameter(req, "page", 1))
            .total(getQueryParameter(req, "total", 10))
            .build();
    }

    private int getQueryParameter(WebServletRequest req, String name, int defaultValue) {
        return NumberUtils.toInt(Objects
            .requireNonNullElse(req.getParameter(name), String.valueOf(defaultValue)), defaultValue);
    }

    private SorterDataDto generateSortingJPQuery(WebServletRequest req) {
        final SessionAttribute attribute = pageableAttributes.sorter();
        SorterDataDto sortedData = req.getFromSession(attribute, SorterDataDto.class);
        if (sortedData == null) {
            sortedData = new SorterDataDto(sorterFieldMap, defaultSorterColumn);
        }
        final String columnName = req.getParameter("sortBy");
        if (columnName == null) {
            return sortedData;
        }
        for (final Map.Entry<String, ServletSorterField> entry : sortedData.getFieldsMap().entrySet()) {
            final ServletSorterField field = entry.getValue();
            if (entry.getKey().equals(columnName)) {
                switch (field.getDirection()) {
                    case IDLE -> sortedData.setJpql(field.setAscending());
                    case ASC -> sortedData.setJpql(field.setDescending());
                    default -> sortedData.setJpql(field.reset(defaultSorterColumn));
                }
            } else {
                field.reset(defaultSorterColumn);
            }
        }
        req.setSessionAttribute(attribute, sortedData);
        return sortedData;
    }

    public FilterDataDto generateFilterJPQuery(WebServletRequest req) {
        final SessionAttribute attribute = pageableAttributes.filter();
        FilterDataDto filterData = req.getFromSession(attribute, FilterDataDto.class);
        if (filterData == null) {
            filterData = new FilterDataDto(filterFieldMap);
        }
        final String searchText = StringUtils.trimToEmpty(req.getParameter("searchText"));
        final String searchByValue = req.getParameter("searchBy");
        if (searchByValue == null) {
            return filterData;
        }
        for (final FilterColumn select : filterData.getSearchBy()) {
            if (select.getValue().equals(searchByValue)) {
                select.setIsSelected("selected");
                filterData.setSearchColumn(select.getColumnName());
            } else {
                select.setIsSelected(StringUtils.EMPTY);
            }
        }
        filterData.setSearchText(searchText);
        req.setSessionAttribute(attribute, filterData);
        return filterData;
    }

    protected abstract WebServletResponse onFetchPageableData(WebServletRequest req, PageableDto pageable);

    protected abstract Map<String, ServletSorterField> configureServletSorterFields();

    protected abstract List<FilterColumn> configureServletFilterFields();

    protected abstract String defaultSorterColumn();

    protected abstract PageableAttributes setPageableAttributes();

    protected abstract String setRedirectOnPostCall(WebServletRequest req);
}
