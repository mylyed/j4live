package com.mylyed.j4live.stream;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * 流处理器名称
 *
 * @author lilei
 * created at 2020/4/30
 */
@Getter
@Setter
@ToString(callSuper = true)
public class StreamHandlerName extends AutoIncrementID {
    private final String app;
    private String name;

    public StreamHandlerName(String app, String name) {
        this.app = app;
        this.name = name;
    }

    public StreamHandlerName(String app) {
        this.app = app;
        this.name = null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StreamHandlerName that = (StreamHandlerName) o;
        return Objects.equals(app, that.app) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(app, name);
    }
}
