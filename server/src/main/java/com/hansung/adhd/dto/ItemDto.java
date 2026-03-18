package com.hansung.adhd.dto;

import com.hansung.adhd.domain.Items;
import lombok.Builder;
import lombok.Getter;

public class ItemDto {

    /** 상점 아이템 응답 */
    @Getter
    @Builder
    public static class ItemResponse {
        private Long    itemId;
        private String  name;
        private String  type;
        private Integer price;
        private String  splineTriggerName;
        private Integer requiredLevel;
        private Long    requiredJobId;

        public static ItemResponse from(Items item) {
            return ItemResponse.builder()
                    .itemId(item.getId())
                    .name(item.getName())
                    .type(item.getType())
                    .price(item.getPrice())
                    .splineTriggerName(item.getSplineTriggerName())
                    .requiredLevel(item.getRequiredLevel())
                    .requiredJobId(item.getRequiredJob() != null
                            ? item.getRequiredJob().getId() : null)
                    .build();
        }
    }
}
