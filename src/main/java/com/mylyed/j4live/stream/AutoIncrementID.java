package com.mylyed.j4live.stream;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自增长ID
 *
 * @author lilei
 * created at 2020/4/30
 */
@ToString
public abstract class AutoIncrementID implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    @Getter
    private final int id = NEXT_ID.getAndIncrement();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutoIncrementID that = (AutoIncrementID) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
