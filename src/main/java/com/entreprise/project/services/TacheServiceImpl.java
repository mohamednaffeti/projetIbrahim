package com.entreprise.project.services;

import com.entreprise.project.entities.Tache;
import com.entreprise.project.enums.TacheStatus;
import com.entreprise.project.exceptions.DataNotFoundException;
import com.entreprise.project.repositories.TacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TacheServiceImpl implements ITacheService {
    @Autowired
    private TacheRepository tacheRepository;

    @Override
    public Tache createTache(Tache tache) {
        return tacheRepository.save(tache);
    }

    @Override
    public Tache updateTache(Tache tache) {
        Optional<Tache> existingTache = tacheRepository.findById(tache.getId());

        if (existingTache.isPresent()) {
            Tache existingTacheEntity = existingTache.get();
            existingTacheEntity.setAssigne(tache.getAssigne());
            existingTacheEntity.setDescription(tache.getDescription());
            existingTacheEntity.setDateDeadline(tache.getDateDeadline());
            return tacheRepository.save(existingTacheEntity);
        } else {
            return null;
        }
    }

    @Override
    public List<Tache> getAllTachesByAssigneeId(Long assigneeId) {
        return tacheRepository.findByAssigne_Id(assigneeId);
    }

    @Override
    public List<Tache> getAllTachesByCreatorId(Long creatorId) {
        return tacheRepository.findByCreateur_Id(creatorId);
    }

    @Override
    public List<Tache> getAllTaches() {
        return tacheRepository.findAll();
    }

    @Override
    public void deleteTache(Long tacheId) {
        tacheRepository.deleteById(tacheId);
    }

    @Override
    public void changeStatus(Long tacheId) {
        Optional<Tache> existingTache = tacheRepository.findById(tacheId);
        if(existingTache.isPresent()){
            existingTache.get().setStatus(TacheStatus.CLOTURER);
        }else{
            throw new DataNotFoundException("Tache not exist");
        }
    }
}
