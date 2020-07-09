package com.server.pool.resourseManagement.api;

import com.server.pool.resourseManagement.model.Server;
import com.server.pool.resourseManagement.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("api/v1/server")
@RestController
public class ServerController {

    public final ServerService serverService;

    @Autowired
    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @PostMapping
    public void addServer(@RequestBody Server server) throws ExecutionException, InterruptedException {
        this.serverService.addServer(server);
    }

    @GetMapping
    public List<Server> getAllServers() throws ExecutionException, InterruptedException {
        return serverService.getAllServers();
    }

    @GetMapping(path = "{id}")
    public Server getServerById(@PathVariable("id") UUID id) throws ExecutionException, InterruptedException {
        return this.serverService.getServerById(id)
                .orElse(null);
    }
    @PutMapping(path = "{id}")
    public int updateServerById( @PathVariable("id")UUID id,@RequestBody Server server,int size) throws ExecutionException, InterruptedException {
        return serverService.updateServerById(id, server, size);
    }
    @RequestMapping(method = GET, value = "/size/{size}")
    public int getServerSize(@PathVariable("size") int size) throws ExecutionException, InterruptedException {
        return serverService.getServerSize(size);
    }
}
