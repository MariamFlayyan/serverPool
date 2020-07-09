package com.server.pool.resourseManagement.dao;

import com.server.pool.resourseManagement.model.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Repository("postgres")
public class ServerDataAccessService implements ServerDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ServerDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Async
    public CompletableFuture<Integer> insertServer(UUID id, Server server) throws InterruptedException {
        final String sql = "insert into server (id, name, freesize) values(?,?,?)";

        Thread.sleep(20*1000);
        int row = jdbcTemplate.update(sql, id, server.getName(), server.getFreeSize());
        return CompletableFuture.completedFuture(row);
    }

    @Override
    @Async
    public CompletableFuture<List<Server>> listAllServers() {
        final String sql = "SELECT id, name, freesize from server";
        List<Server> servers = jdbcTemplate.query(sql, (resultSet, i) -> {
            UUID id = UUID.fromString(resultSet.getString("id"));
            String name = resultSet.getString("name");
            int freeSize = Integer.valueOf(resultSet.getString("freesize"));
            return new Server(id, name, freeSize);
        });
        return CompletableFuture.completedFuture(servers);
    }

    @Override
    @Async
    public CompletableFuture<Optional<Server>> selectServerById(UUID id) {
        final String sql =  "SELECT id, name , freesize from server where id = ?";
        Server server =  jdbcTemplate.queryForObject(sql , new Object[]{id}, (resultSet, i) ->{
            String name = resultSet.getString("name");
            int freeSize = Integer.valueOf(resultSet.getString("freesize"));
            return new Server(id,name,freeSize);
        });
        return CompletableFuture.completedFuture(Optional.ofNullable(server));
    }

    @Override
    @Async
    public CompletableFuture<Integer> updateServerById(UUID id, Server server, int size) {
        System.out.println("here");
        int free = server.getFreeSize() - size;
        final String sql = "update server set freesize = ? where id = ?";
        int row = jdbcTemplate.update(sql,free, id);
        return CompletableFuture.completedFuture(row);
    }

    @Override
    @Async
    public synchronized CompletableFuture<Integer> getServerSize(int size) throws InterruptedException, ExecutionException {
        List<Server> servers = listAllServers().get();
        Server availableServer = servers.stream().filter(server -> server.getFreeSize() >= size ).findFirst().orElse(null);
        if(availableServer != null)
        {
            return updateServerById(availableServer.getId(), availableServer, size);
        }
        UUID randId = UUID.randomUUID();
        String name = "server" + randId.toString();
        Server newServer = new Server(randId, name, 100);
        insertServer(randId, newServer);
        return updateServerById(randId, newServer, size);
    }

}
