package com.mbc.controller;

import com.mbc.dto.*;
import com.mbc.entity.OrderItem;
import com.mbc.service.CartService;
import com.mbc.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;

//    @PostMapping(value = "/order")
//    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto,
//                                              BindingResult bindingResult
////            , Principal principal
//    ) {
//
//        if(bindingResult.hasErrors()) {
//            StringBuilder sb = new StringBuilder();
//            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
//            for (FieldError fieldError : fieldErrors) {
//                sb.append(fieldError.getDefaultMessage());
//            }
//            return new ResponseEntity<String>(sb.toString(),
//                    HttpStatus.BAD_REQUEST);
//        }
//
////        String name = principal.getName();
//        Long orderItemId;
//
//        try{
//            orderItemId = orderService.addOrderDetail(orderDto);
//        } catch (Exception e) {
//            return new ResponseEntity<String>(e.getMessage(),
//                    HttpStatus.BAD_REQUEST);
//        }
//
//        return new ResponseEntity<Long>(orderItemId, HttpStatus.OK);
//    }
//
//    @GetMapping(value = "/order")
//    public String orderHist(Principal principal, Model model) {
//        OrderDetailDto orderDetail =
//                orderService.getOrderDetail(principal.getName());
//        model.addAttribute("orderItems", orderDetail);
//        return "order/orderDetail";
//    }

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity orderComplete(@RequestBody @Valid OrderDto orderDto,
                                                      BindingResult bindingResult, Principal principal) {

        if(bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(),
                    HttpStatus.BAD_REQUEST);
        }

        String name = principal.getName();
        Long orderId;

        try{
            orderId = orderService.order(orderDto, name);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0 , 5);

        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principal.getName(), pageable);
        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHist";

    }

    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder
            (@PathVariable("orderId") Long orderId, Principal principal){

        if(!orderService.validateOrder(orderId, principal.getName())){
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}
