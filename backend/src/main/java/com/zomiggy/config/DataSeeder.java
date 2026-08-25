package com.zomiggy.config;

import com.zomiggy.entity.Coupon;
import com.zomiggy.entity.Dish;
import com.zomiggy.entity.Restaurant;
import com.zomiggy.entity.User;
import com.zomiggy.entity.DeliveryPartner;
import com.zomiggy.repository.CouponRepository;
import com.zomiggy.repository.DishRepository;
import com.zomiggy.repository.RestaurantRepository;
import com.zomiggy.repository.UserRepository;
import com.zomiggy.repository.DeliveryPartnerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
public class DataSeeder {
    private static String image(String id) {
        return "https://images.unsplash.com/" + id + "?auto=format&fit=crop&w=800&q=80";
    }

    @Bean
    CommandLineRunner seed(RestaurantRepository restaurants, DishRepository dishes, CouponRepository coupons, UserRepository users, DeliveryPartnerRepository partners) {
        return args -> {
            if (restaurants.count() > 0 && dishes.count() > 0 && coupons.count() > 0) { ensurePartners(users, partners); return; }

            List<Restaurant> restaurantData = List.of(
                    new Restaurant(101, "Domino's Pizza", 4.5, "15-25 min", "Pizzas, Fast Food, Italian", image("photo-1555396273-367ea4eb4db5"), "50% OFF up to ₹100"),
                    new Restaurant(102, "Burger King", 4.2, "10-20 min", "Burgers, Fast Food, American", image("photo-1517248135467-4c7edcad34c4"), "Free Delivery"),
                    new Restaurant(103, "Bawarchi Biryani", 4.8, "30-40 min", "Biryani, North Indian, Mughlai", image("photo-1514933651103-005eec06c04b"), "₹75 OFF use ZOMNEW"),
                    new Restaurant(104, "The Sushi Bar", 4.7, "35-45 min", "Japanese, Sushi, Asian", image("photo-1554118811-1e0d58224f24"), "Buy 1 Get 1 Free"),
                    new Restaurant(105, "Wok This Way", 4.4, "20-30 min", "Chinese, Asian, Noodles", image("photo-1533777857889-4be7c70b33f7"), "20% OFF"),
                    new Restaurant(106, "Taco Fiesta", 4.7, "15-25 min", "Mexican, Street Food, Tacos", image("photo-1590846406792-0adc7f938f1d"), "Free Taco on ₹300+"),
                    new Restaurant(107, "Sweet Tooth Cafe", 4.8, "10-20 min", "Desserts, Bakery, Cakes", image("photo-1552566626-52f8b828add9"), "10% OFF"),
                    new Restaurant(108, "Punjabi Dhaba", 4.3, "25-35 min", "North Indian, Tandoori, Mughlai", image("photo-1537047902294-62a40c20a6ae"), "Flat ₹50 OFF"),
                    new Restaurant(118, "Rustic Hearth Cafe", 4.8, "15-25 min", "Continental, Bakery, Coffee", image("photo-1525610553991-2bede1a236e2"), "Buy 1 Get 1"),
                    new Restaurant(119, "Le Bistro Continental", 4.9, "20-30 min", "French, Continental, European", image("photo-1550966871-3ed3cdb5ed0c"), "Flat 20% OFF")
            );
            restaurants.saveAll(Objects.requireNonNull(restaurantData));
            Map<Integer, Restaurant> byId = new HashMap<>();
            for (Restaurant restaurant : restaurantData) {
                Long id = Objects.requireNonNull(restaurant.getId());
                byId.put(id.intValue(), restaurants.findById(id).orElseThrow());
            }

            List<Dish> dishData = List.of(
                    new Dish(1, byId.get(101), "Margherita Veg Pizza", "Pizza", 299, 4.5, "15-25 min", true, false, "Best Seller", "Classic delight with 100% real mozzarella cheese.", image("photo-1565299624946-b28f40a0ae38")),
                    new Dish(2, byId.get(101), "Chicken Pepperoni", "Pizza", 449, 4.8, "15-25 min", false, false, "Trending", "Loaded with chicken pepperoni and extra cheese.", image("photo-1628840042765-356cda07504e")),
                    new Dish(3, byId.get(102), "Crispy Veg Whopper", "Burger", 199, 4.5, "10-20 min", true, false, "Whopper King", "Signature flame-grilled vegetable whopper with sesame buns.", image("photo-1550547660-d9450f859349")),
                    new Dish(21, byId.get(102), "Double Chicken Burger", "Burger", 249, 4.6, "10-20 min", false, false, "Must Try", "Double grilled chicken patties with melted cheese.", image("photo-1568901346375-23c9450c58cd")),
                    new Dish(4, byId.get(103), "Hyderabadi Biryani", "Biryani", 220, 4.3, "30-40 min", false, false, "Hot & Spicy", "Aromatic basmati rice cooked with authentic rich spices.", image("photo-1631515243349-e0cb75fb8d3a")),
                    new Dish(46, byId.get(103), "Special Chicken Biryani", "Biryani", 340, 4.9, "30-40 min", false, false, "Iconic", "Authentic dum biryani with tender marinated chicken and saffron rice.", image("photo-1563379091339-03b21ab4a4f8")),
                    new Dish(6, byId.get(104), "California Sushi Roll", "Sushi", 450, 4.6, "35-45 min", false, true, "Popular", "Crab meat, avocado, and cucumber wrapped in seaweed.", image("photo-1553621042-f6e147245754")),
                    new Dish(49, byId.get(105), "Veg Hakka Chilli Garlic Noodles", "Chinese", 240, 4.4, "20-30 min", true, false, "Hot & Spicy", "Wok-tossed noodles with peppers, cabbage, and roasted garlic.", image("photo-1585032226651-759b368d7246")),
                    new Dish(11, byId.get(106), "Spicy Chicken Tacos", "Mexican", 299, 4.5, "15-25 min", false, true, "Best Seller", "Soft corn tortillas with grilled chicken and fresh salsa.", image("photo-1551504734-5ee1c4a1479b")),
                    new Dish(12, byId.get(107), "Death By Chocolate Cake", "Dessert", 150, 4.9, "10-20 min", true, false, "Trending", "Decadent chocolate cake with layers of ganache.", image("photo-1578985545062-69928b1d9587")),
                    new Dish(14, byId.get(108), "Classic Butter Chicken", "North Indian", 349, 4.5, "25-35 min", false, false, "Best Seller", "Rich and creamy tomato-based curry with tandoori chicken.", image("photo-1603894584373-5ac82b2ae398")),
                    new Dish(34, byId.get(118), "Grilled Herb Chicken Breast", "Continental", 390, 4.8, "15-25 min", false, true, "Healthy Choice", "Juicy grilled chicken with mashed potatoes and vegetables.", image("photo-1532550907401-a500c9a57435")),
                    new Dish(37, byId.get(118), "Continental Veg Ratatouille", "Continental", 310, 4.5, "15-25 min", true, true, "Healthy Choice", "French stewed zucchini, eggplant, and bell peppers.", image("photo-1572449043416-55f4685c9bb7")),
                    new Dish(38, byId.get(119), "Pan-Seared Herb Salmon", "Continental", 540, 4.9, "20-30 min", false, true, "Chef's Signature", "Norwegian salmon with asparagus and lemon caper drizzle.", image("photo-1467003909585-2f8a72700288"))
            );
            dishes.saveAll(Objects.requireNonNull(dishData));

            List<Coupon> couponData = List.of(
                    new Coupon("ZOMNEW", "₹75 OFF on orders above ₹199", 199, 0, 75, 75),
                    new Coupon("50OFF", "Flat 50% OFF up to ₹100", 0, 1, 50, 100),
                    new Coupon("PARTY150", "₹150 OFF on orders above ₹499", 499, 0, 150, 150),
                    new Coupon("TASTY30", "30% OFF up to ₹120", 0, 1, 30, 120),
                    new Coupon("FREEDEL", "Free Delivery", 0, 0, 40, 40)
            );
            coupons.saveAll(Objects.requireNonNull(couponData));
            ensurePartners(users, partners);
        };
    }

    private void ensurePartners(UserRepository users, DeliveryPartnerRepository partners) {
        ensurePartner(users, partners, "9000000001", "Rahul", "TS09AB1234");
        ensurePartner(users, partners, "9000000002", "Priya", "TS10CD5678");
    }

    private void ensurePartner(UserRepository users, DeliveryPartnerRepository partners, String mobile, String name, String vehicle) {
        User user = users.findByMobile(mobile).orElseGet(() -> users.save(new User(mobile)));
        user.setName(name); user.setRole(User.Role.DELIVERY_PARTNER); users.save(user);
        if (partners.findByUserMobile(mobile).isEmpty()) partners.save(new DeliveryPartner(user, vehicle, "BIKE"));
    }
}