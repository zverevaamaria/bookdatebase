package booksdb.model;

public class aBook {

    private int id;
    private String tittle;
    private String author;
    private int year;

    public aBook() {
        this( 0, null, null, 2000);
    }

    public aBook(int id, String tittle, String author, int year) {
        this.id = id;
        this.tittle = tittle;
        this.author = author;
        this.year = year;

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

}
