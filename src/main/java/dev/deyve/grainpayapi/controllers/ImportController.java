package dev.deyve.grainpayapi.controllers;

import dev.deyve.grainpayapi.dtos.ImportResultResponse;
import dev.deyve.grainpayapi.dtos.Response;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.services.ImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private static final Logger logger = LoggerFactory.getLogger(ImportController.class);

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Response> importCsv(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Import CSV filename={} size={}", file.getOriginalFilename(), file.getSize());

        if (file.isEmpty()) {
            return new ResponseEntity<>(new Response(null, 400, "File is empty"), org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        ImportResultResponse result = importService.importCsv(file, user);
        return new ResponseEntity<>(new Response(result, OK.value(), "Import completed"), OK);
    }
}
