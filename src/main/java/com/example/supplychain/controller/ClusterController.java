package com.example.supplychain.controller;

import com.example.supplychain.model.Cluster;
import com.example.supplychain.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clusters")
public class ClusterController {

    @Autowired
    private ClusterService clusterService;

    @PostMapping
    public ResponseEntity<?> createCluster(@RequestBody Cluster cluster) { return ResponseEntity.ok(clusterService.createCluster(cluster));
    }

    @GetMapping
    public List<Cluster> getAllClusters() {
        return clusterService.getAllClusters();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cluster> getClusterById(@PathVariable Long id) {
        return clusterService.getClusterById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

