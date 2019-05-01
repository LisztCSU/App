package bean;

public class appointmentListBean {
    private String id;
    private String objectname;
    private String moviename;
    private String time;
   public appointmentListBean(String id,String objectname,String moviename,String time){
       this.id = id;
       this.objectname = objectname;
       this.moviename = moviename;
       this.time = time;
   }
    public String getId() {
        return id;
    }

    public String getMoviename() {
        return moviename;
    }

    public String getObjectname() {
        return objectname;
    }

    public String getTime() {
        return time;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMoviename(String moviename) {
        this.moviename = moviename;
    }

    public void setObjectname(String objectname) {
        this.objectname = objectname;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
