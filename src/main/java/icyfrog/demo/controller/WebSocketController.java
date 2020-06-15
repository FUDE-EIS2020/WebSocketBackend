package icyfrog.demo.controller;


import com.alibaba.fastjson.JSON;
import icyfrog.demo.DTO.MarketDepthChangeMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

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
@RestController
@ServerEndpoint("/connection/{username}")
public class WebSocketController {

    // 保存 username --> session 的映射关系
    // TODO: 后面也许可以写进redis里
    private static ConcurrentHashMap<String, Session> userSessionMap = new ConcurrentHashMap<>();

    //  TODO: LOG
    @OnOpen
    public void OnOpen(Session session, @PathParam("username") String username) {
        userSessionMap.put(username,session);
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


    // TODO: send to some users not ALL
    // send message to every username
    @ResponseBody
    @GetMapping(path = "/sendMessage")
    public void sendTextTest() throws IOException {
        System.out.println("lined text test");
        System.out.println("Map size: " + userSessionMap.size());
        for(Map.Entry<String, Session> entry:userSessionMap.entrySet()) {
            entry.getValue().getBasicRemote().sendText("尝试从后端发送消息");
        }
    }

    @ResponseBody
    @PostMapping(path = "/sendFront")
    public String sendFront (@RequestBody MarketDepthChangeMsg o) throws IOException {
        for(Map.Entry<String, Session> entry:userSessionMap.entrySet()) {
            entry.getValue().getBasicRemote().sendText(JSON.toJSONString(o));
        }
        return "OK, Get New Market depth";
    }
    /*
    {
    "brokerId":"1",
    "productId":"3",
    "marketDepth":[
        [{"amount":450,"price":110.0},{"amount":450,"price":110.0},{"amount":450,"price":110.0}],
        [{"amount":450,"price":100.0}]]
    }
     */

}
