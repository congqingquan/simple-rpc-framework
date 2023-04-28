package org.cqq.cqqrpc.framework.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by QQ.Cong on 2023-04-23 / 15:25:09
 *
 * @Description 枚举工具类
 */
public class EnumUtils {

    private EnumUtils() {
    }

    /**
     * 校验枚举是否存在，根据枚举名称
     * @param enumClass 枚举的类对象
     * @param name 枚举实例的名称
     * @param <E> 枚举类型
     * @return
     */
    public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String name) {
        try {
            Enum.valueOf(enumClass, name);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    /**
     * 获取枚举常量列表
     *
     * @param enumClass 枚举的类对象
     * @param <E> 枚举类型
     * @return
     */
    public static <E extends Enum<E>> List<E> getEnumConstants(Class<E> enumClass) {
        return enumClass == null ? new ArrayList<>() : Stream.of(enumClass.getEnumConstants()).collect(Collectors.toList());
    }

    /**
     * 匹配枚举实例
     * @param enumClass 枚举的类对象
     * @param matchValue 匹配值
     * @param predicate 断言式
     * @return
     * @param <M> 匹配值类型
     * @param <E> 枚举类型
     */
    public static <M, E extends Enum<E>> Optional<E> match(Class<E> enumClass, M matchValue, BiPredicate<M, E> predicate) {
        return match(enumClass.getEnumConstants(), matchValue, predicate);
    }

    /**
     * 匹配枚举实例
     * @param enums 需要匹配的枚举实例
     * @param matchVal 匹配值
     * @param predicate 断言式
     * @return
     * @param <M> 匹配值类型
     * @param <E> 枚举类型
     */
    public static <M, E extends Enum<E>> Optional<E> match(E[] enums, M matchVal, BiPredicate<M, E> predicate) {
        for (E enumConstant : enums) {
            if (predicate.test(matchVal, enumConstant)) {
                return Optional.of(enumConstant);
            }
        }
        return Optional.empty();
    }

    /**
     * 匹配枚举实例
     * @param enumClass 枚举的类对象
     * @param matchValue 匹配值
     * @param predicateField Equal断言字段
     * @return
     * @param <M> 匹配值类型
     * @param <E> 枚举类型
     */
    public static <M, E extends Enum<E>> Optional<E> equalMatch(Class<E> enumClass, Function<E, M> predicateField, M matchValue) {
        return equalMatch(enumClass.getEnumConstants(), predicateField, matchValue);
    }

    /**
     * 匹配枚举实例
     * @param enums 需要匹配的枚举实例
     * @param matchVal 匹配值
     * @param predicateField Equal断言字段
     * @return
     * @param <M> 匹配值类型
     * @param <E> 枚举类型
     */
    public static <M, E extends Enum<E>> Optional<E> equalMatch(E[] enums, Function<E, M> predicateField, M matchVal) {
        for (E enumConstant : enums) {
            if (predicateField.apply(enumConstant).equals(matchVal)) {
                return Optional.of(enumConstant);
            }
        }
        return Optional.empty();
    }
}