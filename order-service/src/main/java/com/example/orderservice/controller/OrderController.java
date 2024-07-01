package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.request.RequestOrder;
import com.example.orderservice.vo.response.ResponseOrder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/health_check")
    public String status(HttpServletRequest request) {
        return String.format("It's working in User Service on Port %s", request.getServerPort());
    }

    @PostMapping("/{userId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseOrder createOrder(@PathVariable("userId") String userId, @RequestBody RequestOrder order) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(order, OrderDto.class);
        orderDto.setUserId(userId);
        orderDto = orderService.createOrder(orderDto);

        return mapper.map(orderDto, ResponseOrder.class);
    }

    @GetMapping("/{userId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ResponseOrder> getOrder(@PathVariable("userId") String userId) {
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        ModelMapper mapper = new ModelMapper();
        List<ResponseOrder> result = new ArrayList<>();

        orderList.forEach(v -> {
            result.add(mapper.map(v, ResponseOrder.class));
        });

        return result;
    }
}
