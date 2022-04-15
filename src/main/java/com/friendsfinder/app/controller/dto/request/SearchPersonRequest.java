package com.friendsfinder.app.controller.dto.request;

import lombok.Data;

@Data
public class SearchPersonRequest {
    private int depth;

    private int width;
}
