package com.friendsfinder.app.repository;

import com.friendsfinder.app.model.entity.Graph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GraphRepository extends JpaRepository<Graph, Integer> {
    @Query(nativeQuery = true, value = """
        select g from graph as g
        join Token t on g.user_id = t.user_id
            and now() > t.creation_date
            and t.creation_date + t.expires_in * interval '1 second' > now()
            and now() > g.created_on
            and g.created_on + interval '7 day' > now();
    """)
    List<Graph> getAllActiveGraphs();

    @Query(nativeQuery = true, value = """
        delete from graph g
        where now() > g.created_on + interval '7 day';
    """)
    void deleteOldGraphs ();
}
