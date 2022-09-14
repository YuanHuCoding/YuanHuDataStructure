package me.yuanhu.core.DataStructure.VS_Queue;



import java.io.Serializable;

public class LogEvent implements Serializable{

    private static final long serialVersionUID = 1L;
    private long logId;
    private String content;


//    public static final Recycler<LogEvent> Recycler_LogEvent = new Recycler<LogEvent>(Integer.MAX_VALUE,1,1,Integer.MAX_VALUE) {
//        @Override
//        protected LogEvent newObject(Handle<LogEvent> handle) {
//            return new LogEvent(handle);
//        }
//    };
//
//    private Recycler.Handle<LogEvent> handle;
//    public LogEvent(Recycler.Handle<LogEvent> handle) {
//        this.handle = handle;
//    }
//
//    @Override
//    public void recycle(){
//        handle.recycle(this);//通过handler进行对象的回收
//    }
//
//    public void reuse(long logId, String content) {
//        this.logId = logId;
//        this.content = content;
//    }


    public LogEvent(long logId, String content){
        this.logId = logId;
        this.content = content;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
