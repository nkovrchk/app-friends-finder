package com.friendsfinder.app.component;

import com.friendsfinder.app.repository.GraphRepository;
import com.friendsfinder.app.service.node.NodeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class TaskScheduler {

    private final GraphRepository graphRepository;

    private final NodeServiceImpl nodeService;

    private final Logger logger = Logger.getLogger(TaskScheduler.class.getName());

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteOldGraphs() {
        logger.log(Level.INFO, "Выполняется удаление неактуальных графов");
        graphRepository.deleteOldGraphs();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void doDailyUpdate() {
        logger.log(Level.INFO, "Выполняется обновление данных в вершинах графа");

        /*
        var graphs = graphRepository.getAllActiveGraphs();

        if (graphs.size() == 0) return;

        graphs.forEach(graph -> {
            graph.getNodes().forEach(level -> {
                level.forEach(nodes -> {
                    if (nodes == null) return;

                    nodes.forEach(node -> {
                        if (node == null) return;

                        nodeService.updateNodeData(node);
                    });
                });
            });
        });

        graphRepository.saveAll(graphs);

         */
    }
}
