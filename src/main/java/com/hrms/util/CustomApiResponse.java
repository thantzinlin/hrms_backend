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
public class CustomApiResponse<T> {
    @Builder.Default
    private String returnCode = "200";
    @Builder.Default
    private String returnMessage = "Success";
    private T data;
}
