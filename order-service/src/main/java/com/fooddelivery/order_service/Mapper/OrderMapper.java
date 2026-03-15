package com.fooddelivery.order_service.Mapper;

import com.fooddelivery.order_service.Dto.OrderResponse;
import com.fooddelivery.order_service.Entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toResponse(Order order);
}