package com.hansung.adhd.service;

import com.hansung.adhd.dto.ItemDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ItemsRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemsRepository itemsRepository;

    // 상점 아이템 목록 조회
    @Transactional(readOnly = true)
    public List<ItemDto.ItemResponse> getShopItems(String type, Long childJobId) {
        return itemsRepository.findShopItems(type, childJobId)
                .stream()
                .map(ItemDto.ItemResponse::from)
                .toList();
    }

    // 아이템 단건 조회 (내부용)
    @Transactional(readOnly = true)
    public ItemDto.ItemResponse getItem(Long itemId) {
        return itemsRepository.findById(itemId)
                .map(ItemDto.ItemResponse::from)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
    }
}
