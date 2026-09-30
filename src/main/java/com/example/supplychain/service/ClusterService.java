package com.example.supplychain.service;

import com.example.supplychain.model.Cluster;
import com.example.supplychain.repository.ClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClusterService {
    @Autowired
    private ClusterRepository clusterRepository;

    public Cluster createCluster(Cluster cluster) {
        return clusterRepository.save(cluster);
    }

    public List<Cluster> getAllClusters() {
        return clusterRepository.findAll();
    }

    public Optional<Cluster> getClusterById(Long id) {
        return clusterRepository.findById(id);
    }
}
