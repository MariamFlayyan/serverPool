package com.server.pool.resourseManagement.service;

import com.server.pool.resourseManagement.dao.ServerDAO;
import com.server.pool.resourseManagement.model.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class ServerService {
    private final ServerDAO serverDAO;

    @Autowired
    public ServerService(@Qualifier("postgres") ServerDAO serverDAO) {
        this.serverDAO = serverDAO;
    }

    public int addServer(Server server) throws InterruptedException, ExecutionException {
        return this.serverDAO.insertServer(server).get();
    }

    public List<Server> getAllServers() throws ExecutionException, InterruptedException {
        System.out.println("My Name " + Thread.currentThread().getName() +"   " + serverDAO.listAllServers());
        return serverDAO.listAllServers().get();
    }


    public int updateServerById(UUID id, Server server, int size) throws ExecutionException, InterruptedException {
        return this.serverDAO.updateServerById(id, server, size).get();
    }

    public Optional<Server> getServerById(UUID id) throws ExecutionException, InterruptedException {

        return this.serverDAO.selectServerById(id).get();
    }

    public int getServerSize(int size) throws ExecutionException, InterruptedException {
        return  this.serverDAO.getServerSize(size).get();
    }
}
