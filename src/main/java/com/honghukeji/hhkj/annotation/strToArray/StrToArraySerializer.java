package com.honghukeji.hhkj.annotation.strToArray;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class StrToArraySerializer extends StdSerializer<String> implements ContextualSerializer {
    private  String pattern;//切割字符串的关键词
    public StrToArraySerializer(){
        super(String.class);
    }
    public StrToArraySerializer(StrToArray annotation){
        super(String.class);
        this.pattern=annotation.pattern();
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if(beanProperty==null)
        {
            return new JsonSerializer<String>() {
                @Override
                public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                    jsonGenerator.writeString(s);
                }
            };
        }
        if(beanProperty!=null)
        {
            AnnotatedMember member = beanProperty.getMember();
            if(member!=null)
            {
                StrToArray annotation = member.getAnnotation(StrToArray.class);
                if (annotation != null){
                    return new StrToArraySerializer(annotation);
                }
            }
        }
        return new JsonSerializer<String>() {
            @Override
            public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(s);
            }
        };
    }

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if(s!=null)
        {
            String[] arr=s.split(pattern);
            int[] resArr=new int[arr.length];
            for(int i=0;i< arr.length;i++)
            {
                resArr[i]= Integer.parseInt(arr[i]);
            }
            System.out.println("arr:"+arr.length+"str:"+s+"patterm:"+pattern);
            jsonGenerator.writeArray(resArr,0,resArr.length);
        }
    }
}
