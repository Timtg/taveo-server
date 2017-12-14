package no.timesaver.dao;

import no.timesaver.domain.Advertisement;

import java.util.List;
import java.util.Optional;

public interface AdsDao extends AbstractDao{

    List<Advertisement> getAllActiveAdsWithoutProductInfo();

    Optional<Advertisement> getAdWithoutProductById(Long id);
}
