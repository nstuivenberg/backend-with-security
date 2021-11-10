package nl.novi.stuivenberg.springboot.example.security.payload.response;

public class ImageResponse {

    private String base64Image;

    public ImageResponse() {}

    public ImageResponse(String base64Image) {
        this.base64Image = base64Image;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }
}
