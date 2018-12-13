package com.tuyu.controller;

import lombok.Data;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author tuyu
 * @date 12/12/18
 * Talk is cheap, show me the code.
 */
@RestController
public class AddressController{

    @RequestMapping(value = "address")
    public Object listAddresses(@RequestParam("user_name") String userName, Integer userAge) {
        if (StringUtils.isEmpty(userName)) {
            return ReturnUtils.fail();
        }
        System.out.println("userName: " + String.valueOf(userName) + " , userAge: " + String.valueOf(userAge));
        return result();
    }

    @RequestMapping(value = "address2")
    public Object listAddresses(User user) {
        if (StringUtils.isEmpty(user) || StringUtils.isEmpty(user.getUserName())) {
            return ReturnUtils.fail();
        }
        System.out.println(user);
        return result();
    }

    private Object result() {
        Address address = new Address();
        address.setAdrName("杭州");
        address.setCreateTime(new Date());
        address.setUpdateTime(new Date());
        Page<Address> pageInfo = PageUtils.valueOf(Arrays.asList(address));
        return ReturnUtils.success(pageInfo);
    }

    @Data
    public static class User{
        private String userName;
        private Integer userAge;
    }

    @Data
    public static class Address{
        private String adrName;
        private Date createTime;
        private Date updateTime;
    }

    @Data
    public static class Page<T>{
        private Integer pageNum;
        private Integer pageSize;
        private Integer total;
        private List<T> list;
    }
    public static class PageUtils{
        public static <T> Page<T> valueOf(List<T> list) {
            Page<T> tPage = new Page<T>();
            tPage.setPageNum(1);
            tPage.setPageSize(10);
            tPage.setList(list);
            return tPage;
        }
    }

    @Data
    public static class ReturnCode<T>{
        private String returnCode;
        private String returnMessage;
        private T data;
    }

    public static class ReturnUtils{
        public static <T> ReturnCode<T> success(T t){
            ReturnCode<T> tReturnCode = new ReturnCode<T>();
            tReturnCode.setReturnCode("0");
            tReturnCode.setReturnMessage("success");
            tReturnCode.setData(t);
            return tReturnCode;
        }

        public static <T> ReturnCode<T> fail() {
            ReturnCode<T> tReturnCode = new ReturnCode<T>();
            tReturnCode.setReturnCode("-1");
            tReturnCode.setReturnMessage("param error");
            return tReturnCode;
        }
    }


}
