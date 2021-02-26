package uz.alvasti.yozma.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.alvasti.yozma.entity.TranslateType;
import uz.alvasti.yozma.payload.TranslateRequest;
import uz.alvasti.yozma.payload.TranslateResponse;
import uz.alvasti.yozma.service.TranslatorService;

@RestController
@RequestMapping("translate")
public class TranslatorController {

    private final TranslatorService translatorService;

    public TranslatorController(TranslatorService translatorService) {
        this.translatorService = translatorService;
    }

    @PostMapping
    public ResponseEntity<TranslateResponse> translate(
            @RequestBody TranslateRequest request) {

        return ResponseEntity.ok(
                TranslateResponse.of(
                        translatorService.translate(request.getType(), request.getText())
                ));
    }
}
