package icyfrog.demo.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/connection/{username}")
public class WebSocketController {

    // 保存 username --> session 的映射关系
    // TODO: 后面也许可以写进redis里
    private ConcurrentHashMap<String, Session> userSessionMap = new ConcurrentHashMap<>();

    //  TODO: LOG
    @OnOpen
    public void OnOpen(Session session, @PathParam("username") String username) {
        //if(userSessionMap.contains(username)) {return;}
        userSessionMap.put(username,session);
        //log.info("Connection connected");
        //log.info("username: "+ username + "\t session: " + session.toString());
    }



    //  TODO: LOG
    @OnClose
    public void OnClose( @PathParam("username") String username) {
        userSessionMap.remove(username);
        //log.info("Connection closed");
    }

    // 传输消息错误调用的方法
    @OnError
    public void OnError(Throwable error) {
        log.info("Connection error");
    }

    // send message to every username
    @ResponseBody
    @GetMapping(path = "/sendTextTest")
    public void sendTextTest() throws IOException {
        //return "123";
        System.out.println("lined text test");
        // Session s = userSessionMap.get("wxm");
        //s.getBasicRemote().sendText("尝试从后端发送消息");
        for(Map.Entry<String, Session> entry:userSessionMap.entrySet()) {
            entry.getValue().getBasicRemote().sendText("尝试从后端发送消息");
        }
    }
}
