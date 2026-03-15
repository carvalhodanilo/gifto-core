package com.vp.core.application.campaign.create;

public record CreateCampaignOutput(String campaignId) {
    public static CreateCampaignOutput of(final String campaignId) {
        return new CreateCampaignOutput(campaignId);
    }
}