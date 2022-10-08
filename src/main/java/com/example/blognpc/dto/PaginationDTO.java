package com.example.blognpc.dto;

import lombok.Data;
import org.springframework.core.GenericTypeResolver;
import org.testng.Assert;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO<T> {
    private List<T> data;
    private boolean showPageNav;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Long page;
    private Long totalPage;
    private List<Long> pages = new ArrayList<>();

    public void setPagination(Long totalCount, Long page, Long size) {
        assert totalCount >= 0 && size >= 0;
        // 页面总数
        Long totalPage = 0L;
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        this.totalPage = totalPage;

        // 当前页面
        if (page < 1L)
            page = 1L;
        if (page > totalPage)
            page = totalPage == 0 ? 1 : totalPage;
        this.page = page;


        // 页面显示
        if (totalPage <= 7) {
            for (Long i = 1L; i <= totalPage; i++)
                pages.add(i);
        } else {
            Long start;
            if (page <= 4) {
                start = 1L;
            } else if (totalPage - page <= 2) {
                start = totalPage - 6;
            } else {
                start = page - 3;
            }
            for (Long i = start; i <= start + 6; i++)
                pages.add(i);
        }

        // 是否显示分页栏
        if (totalPage == 1) {
            showPageNav = false;
        } else {
            showPageNav = true;
        }

        // 是否展示上一页
        if (page == 1) {
            showPrevious = false;
        } else {
            showPrevious = true;
        }
        // 是否展示下一页
        if (page == totalPage) {
            showNext = false;
        } else {
            showNext = true;
        }

        // 是否展示第一页
        if (pages.contains(1L)) {
            showFirstPage = false;
        } else {
            showFirstPage = true;
        }

        // 是否展示最后一页
        if (pages.contains(totalPage)) {
            showEndPage = false;
        } else {
            showEndPage = true;
        }
    }

    public static PaginationDTO empty() {
        PaginationDTO paginationDTO = new PaginationDTO();
        paginationDTO.setPagination(0L, 0L, 1L);
        paginationDTO.setData(null);
        return paginationDTO;
    }
}
