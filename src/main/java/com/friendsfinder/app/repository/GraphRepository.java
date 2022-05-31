package com.friendsfinder.app.repository;

import com.friendsfinder.app.model.entity.Graph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface GraphRepository extends JpaRepository<Graph, Integer> {
    @Query(value = """
        select g from Graph as g
        join Token t where g.userId = t.userId
    """)
    List<Graph> getAllActiveGraphs();

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        delete from graph g
        where now() > g.created_on + interval '7 day';
    """)
    void deleteOldGraphs ();
}
