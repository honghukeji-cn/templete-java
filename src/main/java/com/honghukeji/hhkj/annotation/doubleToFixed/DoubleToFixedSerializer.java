package com.honghukeji.hhkj.annotation.doubleToFixed;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.honghukeji.hhkj.helper.Helper;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class DoubleToFixedSerializer extends StdSerializer<Double> implements ContextualSerializer {
    private  String pattern;//保留几位小数
    public DoubleToFixedSerializer(){
        super(Double.class);
    }
    public DoubleToFixedSerializer(DoubleToFixed annotation){
        super(Double.class);
        this.pattern=annotation.pattern();
    }
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        AnnotatedMember member = beanProperty.getMember();
        DoubleToFixed annotation = member.getAnnotation(DoubleToFixed.class);
        if (annotation != null){
            return new DoubleToFixedSerializer(annotation);
        }
        return NumberSerializer.instance;
    }

    @Override
    public void serialize(Double aDouble, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//        return Helper.DoubleToFixed(aDouble);
        if(aDouble!=null)
        {
            jsonGenerator.writeNumber(Helper.DoubleToFixed(aDouble,pattern));
        }

    }
}
