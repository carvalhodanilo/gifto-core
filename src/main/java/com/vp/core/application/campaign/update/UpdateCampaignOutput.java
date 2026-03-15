package com.vp.core.application.campaign.update;

public record UpdateCampaignOutput(String campaignId) {
    public static UpdateCampaignOutput of(final String campaignId) {
        return new UpdateCampaignOutput(campaignId);
    }
}