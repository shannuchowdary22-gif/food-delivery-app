package com.zomiggy.service;

import com.zomiggy.dto.ApiDtos.DishResponse;
import com.zomiggy.dto.ApiDtos.RestaurantResponse;
import com.zomiggy.dto.CouponResponse;
import com.zomiggy.entity.Dish;
import com.zomiggy.repository.CouponRepository;
import com.zomiggy.repository.DishRepository;
import com.zomiggy.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CatalogService {
    private final RestaurantRepository restaurants;
    private final DishRepository dishes;
    private final CouponRepository coupons;

    public CatalogService(RestaurantRepository restaurants, DishRepository dishes, CouponRepository coupons) {
        this.restaurants = restaurants;
        this.dishes = dishes;
        this.coupons = coupons;
    }

    public List<RestaurantResponse> restaurants(String category, boolean instant) {
        return restaurants.findAll().stream()
                .filter(Objects::nonNull)
                .filter(restaurant -> category == null || restaurant.getCuisine().toLowerCase().contains(category.toLowerCase()))
                .filter(restaurant -> !instant || restaurant.getTime().startsWith("10") || restaurant.getTime().startsWith("15"))
                .map(restaurant -> new RestaurantResponse(restaurant.getId(), restaurant.getName(), restaurant.getRating(), restaurant.getTime(), restaurant.getCuisine(), restaurant.getImageUrl(), restaurant.getOffer()))
                .toList();
    }

    public List<DishResponse> dishes(String query, String category, Long restaurantId, boolean veg, boolean instant, String sort) {
        List<Dish> filtered = dishes.findAll().stream()
                .filter(Objects::nonNull)
                .filter(dish -> query == null || query.isBlank() || (dish.getName() + " " + dish.getCategory() + " " + dish.getRestaurant().getName()).toLowerCase().contains(query.toLowerCase()))
                .filter(dish -> category == null || "All Food".equals(category) || dish.getCategory().equalsIgnoreCase(category))
                .filter(dish -> restaurantId == null || dish.getRestaurant().getId().equals(restaurantId))
                .filter(dish -> !veg || dish.isVeg())
                .filter(dish -> !instant || dish.getTime().startsWith("10") || dish.getTime().startsWith("15"))
                .collect(Collectors.toList());

        Comparator<Dish> comparator = Comparator.comparing(dish -> Objects.requireNonNull(dish).getId());
        if ("rating-high".equals(sort)) comparator = Comparator.comparing((Dish dish) -> Objects.requireNonNull(dish).getRating()).reversed();
        if ("price-low".equals(sort)) comparator = Comparator.comparing(dish -> Objects.requireNonNull(dish).getPrice());
        if ("price-high".equals(sort)) comparator = Comparator.comparing((Dish dish) -> Objects.requireNonNull(dish).getPrice()).reversed();
        if ("time-fast".equals(sort)) comparator = Comparator.comparing(dish -> Integer.parseInt(Objects.requireNonNull(dish).getTime().split("-")[0]));
        filtered.sort(comparator);

        return filtered.stream()
                .map(dish -> new DishResponse(dish.getId(), dish.getRestaurant().getId(), dish.getRestaurant().getName(), dish.getName(), dish.getCategory(), dish.getPrice(), dish.getRating(), dish.getTime(), dish.isVeg(), dish.getBadge(), dish.getDescription(), dish.getImageUrl(), dish.isHealthy()))
                .toList();
    }

    public List<CouponResponse> coupons() {
        return coupons.findAll().stream().filter(Objects::nonNull).map(coupon -> new CouponResponse(coupon.getCode(), coupon.getDescription())).toList();
    }
}