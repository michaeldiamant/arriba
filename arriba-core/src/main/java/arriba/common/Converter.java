package arriba.common;

public interface Converter<F, T> {

    T convert(F from) throws ConverterException;
}
