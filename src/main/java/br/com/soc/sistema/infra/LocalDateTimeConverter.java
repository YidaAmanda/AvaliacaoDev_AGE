package br.com.soc.sistema.infra;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;
import com.opensymphony.xwork2.conversion.TypeConversionException;

public class LocalDateTimeConverter extends StrutsTypeConverter {

    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
        if (values == null || values.length == 0 || values[0] == null || values[0].trim().isEmpty())
            return null;

        String value = values[0].trim();
        try {
            if (toClass == LocalDate.class) return LocalDate.parse(value);
            if (toClass == LocalTime.class) return LocalTime.parse(value);
        } catch (DateTimeParseException e) {
            throw new TypeConversionException("Data/hora invalida: " + value, e);
        }
        return null;
    }

    @Override
    public String convertToString(Map context, Object o) {
        return o == null ? "" : o.toString();
    }
}