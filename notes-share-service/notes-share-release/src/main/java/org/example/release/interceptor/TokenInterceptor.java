package org.example.release.interceptor;

import org.checkerframework.checker.units.qual.A;
import org.example.model.user.pojos.ApUser;
import org.example.utils.thread.UserThreadLocalUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        Optional<String> optional = Optional.ofNullable(userId);
        if(optional.isPresent()){
            ApUser apUser=new ApUser();
            apUser.setId(Integer.valueOf(userId));
            UserThreadLocalUtil.setUser(apUser);

        }
        return true;
    }
}
