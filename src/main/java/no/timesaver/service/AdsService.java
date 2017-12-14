package no.timesaver.service;

import no.timesaver.dao.AdsDao;
import no.timesaver.domain.Advertisement;
import no.timesaver.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdsService {
    private final static Logger log = LoggerFactory.getLogger(AdsService.class);

    private final AdsDao adsDao;
    private final ProductFetchService productFetchService;

    @Autowired
    public AdsService(AdsDao adsDao, ProductFetchService productFetchService) {
        this.adsDao = adsDao;
        this.productFetchService = productFetchService;
    }


    public List<Advertisement> getAllActiveAds() {
        List<Advertisement> activeAds = adsDao.getAllActiveAdsWithoutProductInfo();
        List<Product> products = productFetchService.getActiveProductsByIds(activeAds.stream().map(Advertisement::getProductId).collect(Collectors.toSet()));
        Map<Long, Product> productIdToProduct = products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        activeAds.forEach(ad -> {
            Product productForAd = productIdToProduct.get(ad.getProductId());
            if(productForAd == null){
                log.error("No product with id {} found for ad with id {}",ad.getProductId(),ad.getId());
            } else {
                populateAdWithProductInfo(ad, productForAd);
            }
        });
        return activeAds;
    }

    public Optional<Advertisement> getAdById(Long id) {
        Optional<Advertisement> ad = adsDao.getAdWithoutProductById(id);
        if(!ad.isPresent()){
            return ad;
        }
        Advertisement advertisement = ad.get();
        Optional<Product> productById = productFetchService.getProductById(advertisement.getProductId());
        if(!productById.isPresent()){
            return Optional.empty();
        }

        populateAdWithProductInfo(advertisement,productById.get());
        return Optional.of(advertisement);
    }


    private void populateAdWithProductInfo(Advertisement ad, Product productForAd) {
        ad.setName(productForAd.getName());
        ad.setDescription(productForAd.getDescription());
        ad.setStoreId(productForAd.getStoreId());
        ad.setStoreName(productForAd.getStoreName());
        ad.setPrice(productForAd.getPrice());
        ad.setIconSrc(productForAd.getIconSrc());
    }


}
