package no.timesaver.dao;

import no.timesaver.domain.Franchise;

import java.util.Optional;

public interface FranchiseDao extends AbstractDao {
    Optional<Franchise> getById(Long franchiseId);

    Long add(String franchiseName);
}
