package model;

public class CreateNewBookPutJson {

    private String userId;
    private String isbn;

    public CreateNewBookPutJson(String userId, String isbn) {
        this.userId = userId;
        this.isbn = isbn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
