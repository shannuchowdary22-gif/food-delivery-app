package com.zomiggy.dto;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.time.Instant; import java.util.*;
public final class ApiDtos { private ApiDtos(){}
 public record OtpRequest(@NotBlank @Pattern(regexp="\\d{10}") String mobile){}
 public record VerifyOtpRequest(@NotBlank @Pattern(regexp="\\d{10}") String mobile,@NotBlank @Pattern(regexp="\\d{6}") String otp){}
 public record UserResponse(Long id,String name,String mobile,String address,int coins){}
 public record AuthResponse(String token,UserResponse user){}
 public record ProfileRequest(@NotBlank @Size(max=80) String name,@NotBlank @Size(max=500) String address){}
 public record Address(String label,Double latitude,Double longitude){}
 public record OrderItemRequest(@NotNull Long dishId,@Min(1) @Max(50) int quantity){}
 public record OrderRequest(@NotEmpty List<@Valid OrderItemRequest> items,String couponCode,@NotBlank String paymentMethod,@Min(1) @Max(10) int splitCount,String splitPaymentId,@NotNull @Valid Address deliveryAddress,@NotBlank @Pattern(regexp="\\d{4}") String pin){}
 public record DishResponse(Long id,Long restaurantId,String restaurantName,String name,String category,int price,double rating,String time,boolean veg,String badge,String description,String imageUrl,boolean healthy){}
 public record RestaurantResponse(Long id,String name,double rating,String time,String cuisine,String imageUrl,String offer){}
 public record OrderResponse(String id,String status,String items,String method,int total,Instant createdAt){}
 public record TrackingResponse(String orderId,String status,int etaMinutes,double distanceKm,Driver driver,Address origin,Address destination){}
 public record Driver(String name,String phone,String vehicle,double rating){}
 public record AssistantRequest(@NotBlank @Size(max=500) String message,String orderId){}
}