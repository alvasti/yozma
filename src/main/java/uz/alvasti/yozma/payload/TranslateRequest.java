package uz.alvasti.yozma.payload;

import uz.alvasti.yozma.entity.TranslateType;

import javax.validation.constraints.NotEmpty;

public class TranslateRequest {

    private TranslateType type = TranslateType.AUTO;

    @NotEmpty
    private String text;

    public TranslateRequest() {
    }

    public TranslateType getType() {
        return type;
    }

    public void setType(TranslateType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
