package com.tuyu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.beans.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tuyu
 * @date 12/12/18
 * Talk is cheap, show me the code.
 */
@Configuration
//@EnableWebMvc
public class MessageConverter
        extends WebMvcConfigurationSupport
{

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(new UnderlineToCamelArgumentResolver());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper().setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy()));
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        converters.add(converter);
    }

    private static class UnderlineToCamelArgumentResolver implements HandlerMethodArgumentResolver {
        /**
         * 匹配下划线的格式
         */
        private static Pattern pattern = Pattern.compile("_(\\w)");

        private static String underLineToCamel(String source) {
            Matcher matcher = pattern.matcher(source);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            }
            matcher.appendTail(sb);
            return sb.toString();
        }

        @Override
        public boolean supportsParameter(MethodParameter methodParameter) {
            return true;
        }


        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
                                      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
            return handleParameterNames(parameter, webRequest);
        }

        private Object handleParameterNames(MethodParameter parameter, NativeWebRequest webRequest) {
            if (isSimpleType(parameter.getParameterType())) {
                String parameterName = parameter.getParameterName();
                System.out.println(parameterName);
                String snake = humpToLine(parameterName);
                String real = webRequest.getParameter(snake);
                if (real == null) {
                    real = webRequest.getParameter(parameterName);
                }
                return real == null ? null : getObject(parameter.getParameterType(), real);
            } else {
                Object obj = getInstance(parameter.getParameterType());
                BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
                Iterator<String> paramNames = webRequest.getParameterNames();
                while (paramNames.hasNext()) {
                    String paramName = paramNames.next();
                    Object o = webRequest.getParameter(paramName);
                    try {
                        wrapper.setPropertyValue(underLineToCamel(paramName), o);
                    } catch (BeansException e) {

                    }
                }
                return obj;
            }
        }
    }

    private static Object getObject(Class clazz, String value) {
        Constructor constructor = null;
        Object o1 = null;
        try {
            constructor = clazz.getConstructor(String.class);
            o1 = constructor.newInstance(value);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return o1;
    }

    private static Object getInstance(Class clazz) {
        if (clazz == null) {
            return null;
        }
        Object o = null;
        try {
            o = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    private static boolean isSimpleType(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        boolean simple = false;
        switch (simpleName.toLowerCase()) {
            case "byte":
                simple = true;
                break;
            case "short":
                simple = true;
                break;
            case "int":
                simple = true;
                break;
            case "integer":
                simple = true;
                break;
            case "long":
                simple = true;
                break;
            case "float":
                simple = true;
                break;
            case "double":
                simple = true;
                break;
            case "boolean":
                simple = true;
                break;
            case "char":
                simple = true;
                break;
            case "character":
                simple = true;
                break;
            case "bigdecimal":
                simple = true;
                break;
            case "string":
                simple = true;
                break;
            default:
                simple = false;
                break;

        }
        return simple;
    }

    /**
     * 下划线转驼峰
     *
     * @param str
     *
     * @return
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static Pattern linePattern = Pattern.compile("_([a-z])");
    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 驼峰转下划线
     *
     * @param str
     *
     * @return
     */
    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}