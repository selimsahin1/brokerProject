package com.selimsahin.broker.service;

import com.selimsahin.broker.model.Asset;
import com.selimsahin.broker.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;

    public List<Asset> getAssetsByCustomer(String customerId) {
        return assetRepository.findByCustomerId(customerId);
    }

    public Asset getOrCreateAsset(String customerId, String assetName) {
        return assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseGet(() -> assetRepository.save(
                        Asset.builder()
                                .customerId(customerId)
                                .assetName(assetName)
                                .size(0)
                                .usableSize(0)
                                .build()));
    }

    public void updateAsset(Asset asset) {
        assetRepository.save(asset);
    }
}