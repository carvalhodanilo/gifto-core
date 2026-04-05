package com.vp.core.application.campaign.uploadBanner;

public record UploadCampaignBannerCommand(String tenantId, String campaignId, byte[] content, String contentType) {
}
