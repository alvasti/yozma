package uz.alvasti.yozma.payload;

public class TranslateResponse {

    private TranslateResponse(){
    }

    private TranslateResponse(String text) {
        this.text = text;
    }

    public static TranslateResponse of(String text){
        return new TranslateResponse(text);
    }

    public String text;
}
