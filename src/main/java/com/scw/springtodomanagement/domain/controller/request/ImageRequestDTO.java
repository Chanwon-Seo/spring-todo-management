package com.scw.springtodomanagement.domain.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor
public class ImageRequestDTO {

    private MultipartFile imageFile;

}
