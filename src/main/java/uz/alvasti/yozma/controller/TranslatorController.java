package uz.alvasti.yozma.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.alvasti.yozma.entity.TranslateType;
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
            @RequestParam("type") TranslateType translateType,
            @RequestParam("text") String text) {

        return ResponseEntity.ok(
                TranslateResponse.of(
                        translatorService.translate(translateType, text)
                ));
    }
}
