package bean;

public class ChatListBean implements Comparable<ChatListBean> {
    private String id;
    private String account;
    private String time;
    private String content;
    public ChatListBean(String id,String account,String time,String content){
        this.id = id;
        this.account = account;
        this.time = time;
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public String getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getContent() {
        return content;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setContent(String content) {
        this.content = content;
    }
    @Override
    public int compareTo(ChatListBean bean){
        String arr[] = this.getTime().split(" ");
        String arr2[] = bean.getTime().split(" ");
        String arr3[] = arr[0].split("-");
        String arr4[] = arr[1].split(":");
        String arr5[] = arr2[0].split("-");
        String arr6[] = arr2[1].split(":");
        int year = Integer.parseInt(arr3[0]);
        int month = Integer.parseInt(arr3[1]);
        int day = Integer.parseInt(arr3[2]);
        int hour = Integer.parseInt(arr4[0]);
        int minute = Integer.parseInt(arr4[1]);
        int second = Integer.parseInt(arr4[2]);
        int year2 = Integer.parseInt(arr5[0]);
        int month2 = Integer.parseInt(arr5[1]);
        int day2 = Integer.parseInt(arr5[2]);
        int hour2 = Integer.parseInt(arr6[0]);
        int minute2 = Integer.parseInt(arr6[1]);
        int second2= Integer.parseInt(arr6[2]);
        if(year>year2){
            return 1;
        }
        else if(month>month2){
            return 1;
        }
        else if(day>day2){
            return 1;
        }
        else if(hour>hour2){
            return 1;
        }
        else if(minute>minute2){
            return 1;
        }
        else if(second>second2){
            return 1;
        }
        else if(second==second2){
            return 0;
        }
        else {
            return -1;
        }
    }
}
