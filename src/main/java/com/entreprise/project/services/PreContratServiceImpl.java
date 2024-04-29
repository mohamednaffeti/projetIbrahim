package com.entreprise.project.services;

import com.entreprise.project.entities.PreContrat;
import com.entreprise.project.entities.Prospect;
import com.entreprise.project.exceptions.DataNotFoundException;
import com.entreprise.project.repositories.PreContratRepository;
import com.entreprise.project.repositories.ProspectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class PreContratServiceImpl implements IPreContratService {
    @Autowired
    private PreContratRepository preContratRepository;

    @Autowired
    private ProspectRepository prospectRepository;
    @Override
    public PreContrat create(Long prospectId) {
        Prospect prospect = prospectRepository.findById(prospectId)
                .orElseThrow(() -> new DataNotFoundException("Prospect not found with id: " + prospectId));

        PreContrat preContrat = PreContrat.builder()
                .prospect(prospect)
                .DateAjout(LocalDate.now())
                .build();

        return preContratRepository.save(preContrat);
    }
    @Override
    public PreContrat getById(Long id) {
        return preContratRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("PreContrat not found with id: " + id));
    }

    @Override
    @Transactional
    public PreContrat update(Long id, PreContrat updatedPreContrat) {
        PreContrat preContrat = getById(id);
        preContrat.setProspect(updatedPreContrat.getProspect());
        preContrat.setDateAjout(LocalDate.now());

        return preContratRepository.save(preContrat);
    }
    @Override
    public void delete(Long id) {
        preContratRepository.deleteById(id);
    }

    @Override
    public List<PreContrat> getAll() {
        return preContratRepository.findAll();
    }
}
