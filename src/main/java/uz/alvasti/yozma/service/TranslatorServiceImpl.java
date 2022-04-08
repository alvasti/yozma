package uz.alvasti.yozma.service;

import org.springframework.stereotype.Service;
import uz.alvasti.lotinkirill.texttranslator.TextTranslator;
import uz.alvasti.yozma.entity.TranslateType;

@Service
public class TranslatorServiceImpl implements TranslatorService{

    private final TextTranslator textTranslator;

    public TranslatorServiceImpl() {
        textTranslator = new TextTranslator();
    }

    @Override
    public String translate(TranslateType translateType, String text) {

        String translatedText;

        switch (translateType){
            case TO_UZKIRILL:
                translatedText = textTranslator.translateToCyrillic(text);
                break;
            case TO_UZLATIN:
                translatedText = textTranslator.translateToLatin(text);
                break;
            default:
                translatedText = textTranslator.translateAuto(text);
        }

        return translatedText;
    }
}
