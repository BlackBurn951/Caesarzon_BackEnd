package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Getter
@Setter
public class ImagesDTO {

    private MultipartFile images;
}
