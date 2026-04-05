package com.vp.core.infrastructure.api.response;

public record AssetUrlResponse(String url) {
    public static AssetUrlResponse of(final String url) {
        return new AssetUrlResponse(url);
    }
}
