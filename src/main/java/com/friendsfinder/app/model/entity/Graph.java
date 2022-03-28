package com.friendsfinder.app.model.entity;

import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.model.converter.GraphConverter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Set;

@Entity
@Table(name = "graph")
public class Graph extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = GraphConverter.class)
    private ArrayList<ArrayList<ArrayList<Node>>> graph;

    @ElementCollection
    private Set<Integer> uniqueIds;
}
