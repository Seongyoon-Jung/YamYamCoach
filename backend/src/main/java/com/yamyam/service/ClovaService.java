package com.yamyam.service;

import com.yamyam.dto.request.ClovaRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ClovaService {
    String requestClovaOCR(MultipartFile file, ClovaRequest payload);
}