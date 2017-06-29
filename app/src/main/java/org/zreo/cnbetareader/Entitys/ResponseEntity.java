package org.zreo.cnbetareader.Entitys;

/**
 * Created by zqh on 2015/7/30  10:08.
 * Email:zqhkey@163.com
 */
public class ResponseEntity<T> {
    private T result;
    private String state;

    @Override
    public String toString() {
        return "ResponseEntity{" +
                "result=" + result +
                ", state='" + state + '\'' +
                '}';
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}


