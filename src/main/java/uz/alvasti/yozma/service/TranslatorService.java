package uz.alvasti.yozma.service;

import uz.alvasti.yozma.entity.TranslateType;

public interface TranslatorService {

    String translate(TranslateType translateType, String text);
}
