package ru.vlad2509.minionflow.application.util;

import jakarta.enterprise.context.ApplicationScoped;
import ru.vlad2509.minionflow.application.dto.WebSocketChannelInfo;

import java.util.UUID;

@ApplicationScoped
public class WebSocketChannelFactory {

    public WebSocketChannelInfo microtaskLogs(UUID microtaskId) {
        return new WebSocketChannelInfo("microtasks/" + microtaskId + "/logs", "websocket.microtask-logs."+microtaskId);
    }

    public WebSocketChannelInfo taskStatePatches(UUID taskId) {
        return new WebSocketChannelInfo("tasks/" + taskId + "/state", "websocket.task-state-patches."+taskId);
    }

    public UUID isMicrotaskLogs(String channelName){
        String[] chunks = channelName.split("/");
        if(chunks.length == 3 && chunks[0].equals("microtasks") && chunks[2].equals("logs")){
            try{
                return UUID.fromString(chunks[1]);
            }catch(Exception ignored){
            }
        }
        return null;
    }

    public UUID isTaskStatePatch(String channelName){
        String[] chunks = channelName.split("/");
        if(chunks.length == 3 && chunks[0].equals("tasks") && chunks[2].equals("state")){
            try{
                return UUID.fromString(chunks[1]);
            }catch(Exception ignored){
            }
        }
        return null;
    }

    public WebSocketChannelInfo recognise(String channelName){
        UUID var1 = isMicrotaskLogs(channelName);
        if(var1 != null)
            return microtaskLogs(var1);

        UUID var2 = isTaskStatePatch(channelName);
        if(var2 != null)
            return taskStatePatches(var2);

        return null;
    }
}
