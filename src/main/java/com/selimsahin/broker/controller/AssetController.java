package com.selimsahin.broker.controller;

import com.selimsahin.broker.service.AssetService;
import com.selimsahin.broker.model.Asset;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    public List<Asset> getCustomerAssets(@RequestParam String customerId) {
        return assetService.getAssetsByCustomer(customerId);
    }
}
