package org.example.utils.thread;

import org.example.model.user.pojos.ApUser;

public class UserThreadLocalUtil {
    public final static ThreadLocal<ApUser> USER_THREAD_LOCAL=new ThreadLocal<>();
    public static void setUser(ApUser apUser){
        USER_THREAD_LOCAL.set(apUser);
    }

    public static ApUser getUser(){
        return USER_THREAD_LOCAL.get();
    }

    public static void clear(){
        USER_THREAD_LOCAL.remove();
    }


}
