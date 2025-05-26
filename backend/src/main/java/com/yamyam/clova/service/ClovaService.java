package com.yamyam.clova.service;

import com.yamyam.clova.dto.ClovaRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ClovaService {
    String requestClovaOCR(MultipartFile file, ClovaRequest payload);
}