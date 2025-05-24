package com.selimsahin.broker.service;

import com.selimsahin.broker.model.Asset;
import com.selimsahin.broker.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetService assetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAssetsByCustomer_returnsList() {
        String customerId = "alice";
        List<Asset> mockAssets = List.of(
                new Asset(null, customerId, "TRY", 1000, 800),
                new Asset(null, customerId, "ASELS", 50, 40)
        );

        when(assetRepository.findByCustomerId(customerId)).thenReturn(mockAssets);

        List<Asset> result = assetService.getAssetsByCustomer(customerId);

        assertEquals(2, result.size());
        assertEquals("TRY", result.get(0).getAssetName());
    }

    @Test
    void testGetOrCreateAsset_returnsExistingAsset() {
        String customerId = "bob";
        String assetName = "THYAO";

        Asset existing = new Asset(1L, customerId, assetName, 30, 20);

        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName))
                .thenReturn(Optional.of(existing));

        Asset result = assetService.getOrCreateAsset(customerId, assetName);

        assertEquals(assetName, result.getAssetName());
        verify(assetRepository, never()).save(any());
    }

    @Test
    void testGetOrCreateAsset_createsNewAssetIfNotFound() {
        String customerId = "bob";
        String assetName = "GARAN";

        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName))
                .thenReturn(Optional.empty());

        when(assetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Asset result = assetService.getOrCreateAsset(customerId, assetName);

        assertEquals(assetName, result.getAssetName());
        assertEquals(0, result.getSize());
        assertEquals(0, result.getUsableSize());
        verify(assetRepository).save(any());
    }

    @Test
    void testUpdateAsset_callsSave() {
        Asset asset = new Asset(1L, "alice", "TRY", 100, 50);

        assetService.updateAsset(asset);

        verify(assetRepository).save(asset);
    }
}