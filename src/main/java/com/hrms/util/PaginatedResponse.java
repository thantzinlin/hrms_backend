package com.hrms.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PaginatedResponse<T> {
    private T content;
    private int currentPage;
    private long totalItems;
    private int totalPages;
}
