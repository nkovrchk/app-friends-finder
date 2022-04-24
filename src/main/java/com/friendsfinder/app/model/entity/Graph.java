package com.friendsfinder.app.model.entity;

import com.friendsfinder.app.model.Node;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Graph")
@Table(name = "graph")
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class Graph extends BaseEntity {
    @Id
    @Getter
    @Setter
    @Column(name = "user_id")
    private Integer userId;

    @Getter
    @Setter
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private ArrayList<ArrayList<ArrayList<Node>>> nodes;

    @Getter
    @Setter
    @Type(type = "jsonb")
    @Column(name = "unique_ids", columnDefinition = "jsonb")
    private List<Integer> uniqueIds;
}
