package com.spring.socket.domain.utils.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
  // CLIENTS 라는 변수에 세션을 담아두기 위한 맵형식의 공간
  private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<>();

  // 사용자가 웹소켓 서버에 접속하게 되면 동작하는 메서드
  // 이 때 WebSocketSession 값이 생성되는데 CLIENTS 변수에 담아줍니다
  // 키 값은 세션의 고유값
  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    CLIENTS.put(session.getId(), session);
  }

  // 웹소켓 서버접속이 끝났을 때 동작하는 메서드
  // 해당 세션을 제거
  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    CLIENTS.remove(session.getId());
  }

  // 사용자의 메세지를 받게되면 동작하는 메서드
  // CLIENT 변수에 담긴 세션값들을 가져와서 반복문으로 돌려서,
  // 위 처럼 메세지를 발송해주면, 본인 이외의 사용자에게 메세지를 보낼 수 있는 코드가 됩니다.
  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String id = session.getId(); // 메세지를 보낸 아이디
    CLIENTS.entrySet().forEach(
      arg -> {
        if(!arg.getKey().equals(id)) { // 같은 아이디가 아니면 메세지를 전달합니다
          try {
            arg.getValue().sendMessage(message);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    );
  }
}
