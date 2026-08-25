const image = (id, width = 800) => `https://images.unsplash.com/${id}?auto=format&fit=crop&w=${width}&q=80`

export const restaurants = [
  { id: 101, name: "Domino's Pizza", rating: 4.5, time: '15-25 min', cuisine: 'Pizzas, Fast Food, Italian', image: image('photo-1555396273-367ea4eb4db5'), offer: '50% OFF up to ₹100' },
  { id: 102, name: 'Burger King', rating: 4.2, time: '10-20 min', cuisine: 'Burgers, Fast Food, American', image: image('photo-1517248135467-4c7edcad34c4'), offer: 'Free Delivery' },
  { id: 103, name: 'Bawarchi Biryani', rating: 4.8, time: '30-40 min', cuisine: 'Biryani, North Indian, Mughlai', image: image('photo-1514933651103-005eec06c04b'), offer: '₹75 OFF use ZOMNEW' },
  { id: 104, name: 'The Sushi Bar', rating: 4.7, time: '35-45 min', cuisine: 'Japanese, Sushi, Asian', image: image('photo-1554118811-1e0d58224f24'), offer: 'Buy 1 Get 1 Free' },
  { id: 105, name: 'Wok This Way', rating: 4.4, time: '20-30 min', cuisine: 'Chinese, Asian, Noodles', image: image('photo-1533777857889-4be7c70b33f7'), offer: '20% OFF' },
  { id: 106, name: 'Taco Fiesta', rating: 4.7, time: '15-25 min', cuisine: 'Mexican, Street Food, Tacos', image: image('photo-1590846406792-0adc7f938f1d'), offer: 'Free Taco on ₹300+' },
  { id: 107, name: 'Sweet Tooth Cafe', rating: 4.8, time: '10-20 min', cuisine: 'Desserts, Bakery, Cakes', image: image('photo-1552566626-52f8b828add9'), offer: '10% OFF' },
  { id: 108, name: 'Punjabi Dhaba', rating: 4.3, time: '25-35 min', cuisine: 'North Indian, Tandoori, Mughlai', image: image('photo-1537047902294-62a40c20a6ae'), offer: 'Flat ₹50 OFF' },
  { id: 118, name: 'Rustic Hearth Cafe', rating: 4.8, time: '15-25 min', cuisine: 'Continental, Bakery, Coffee', image: image('photo-1525610553991-2bede1a236e2'), offer: 'Buy 1 Get 1' },
  { id: 119, name: 'Le Bistro Continental', rating: 4.9, time: '20-30 min', cuisine: 'French, Continental, European', image: image('photo-1550966871-3ed3cdb5ed0c'), offer: 'Flat 20% OFF' },
]

export const dishes = [
  { id: 1, restaurantId: 101, restaurantName: "Domino's Pizza", name: 'Margherita Veg Pizza', category: 'Pizza', price: 299, rating: 4.5, time: '15-25 min', veg: true, badge: 'Best Seller', description: 'Classic delight with 100% real mozzarella cheese.', image: image('photo-1565299624946-b28f40a0ae38'), healthy: false },
  { id: 2, restaurantId: 101, restaurantName: "Domino's Pizza", name: 'Chicken Pepperoni', category: 'Pizza', price: 449, rating: 4.8, time: '15-25 min', veg: false, badge: 'Trending', description: 'Loaded with chicken pepperoni and extra cheese.', image: image('photo-1628840042765-356cda07504e'), healthy: false },
  { id: 3, restaurantId: 102, restaurantName: 'Burger King', name: 'Crispy Veg Whopper', category: 'Burger', price: 199, rating: 4.5, time: '10-20 min', veg: true, badge: 'Whopper King', description: 'Signature flame-grilled vegetable whopper with sesame buns.', image: image('photo-1550547660-d9450f859349'), healthy: false },
  { id: 21, restaurantId: 102, restaurantName: 'Burger King', name: 'Double Chicken Burger', category: 'Burger', price: 249, rating: 4.6, time: '10-20 min', veg: false, badge: 'Must Try', description: 'Double grilled chicken patties with melted cheese.', image: image('photo-1568901346375-23c9450c58cd'), healthy: false },
  { id: 4, restaurantId: 103, restaurantName: 'Bawarchi Biryani', name: 'Hyderabadi Biryani', category: 'Biryani', price: 220, rating: 4.3, time: '30-40 min', veg: false, badge: 'Hot & Spicy', description: 'Aromatic basmati rice cooked with authentic rich spices.', image: image('photo-1631515243349-e0cb75fb8d3a'), healthy: false },
  { id: 46, restaurantId: 103, restaurantName: 'Bawarchi Biryani', name: 'Special Chicken Biryani', category: 'Biryani', price: 340, rating: 4.9, time: '30-40 min', veg: false, badge: 'Iconic', description: 'Authentic dum biryani with tender marinated chicken and saffron rice.', image: image('photo-1563379091339-03b21ab4a4f8'), healthy: false },
  { id: 6, restaurantId: 104, restaurantName: 'The Sushi Bar', name: 'California Sushi Roll', category: 'Sushi', price: 450, rating: 4.6, time: '35-45 min', veg: false, badge: 'Popular', description: 'Crab meat, avocado, and cucumber wrapped in seaweed.', image: image('photo-1553621042-f6e147245754'), healthy: true },
  { id: 49, restaurantId: 105, restaurantName: 'Wok This Way', name: 'Veg Hakka Chilli Garlic Noodles', category: 'Chinese', price: 240, rating: 4.4, time: '20-30 min', veg: true, badge: 'Hot & Spicy', description: 'Wok-tossed noodles with peppers, cabbage, and roasted garlic.', image: image('photo-1585032226651-759b368d7246'), healthy: false },
  { id: 11, restaurantId: 106, restaurantName: 'Taco Fiesta', name: 'Spicy Chicken Tacos', category: 'Mexican', price: 299, rating: 4.5, time: '15-25 min', veg: false, badge: 'Best Seller', description: 'Soft corn tortillas with grilled chicken and fresh salsa.', image: image('photo-1551504734-5ee1c4a1479b'), healthy: true },
  { id: 12, restaurantId: 107, restaurantName: 'Sweet Tooth Cafe', name: 'Death By Chocolate Cake', category: 'Dessert', price: 150, rating: 4.9, time: '10-20 min', veg: true, badge: 'Trending', description: 'Decadent chocolate cake with layers of ganache.', image: image('photo-1578985545062-69928b1d9587'), healthy: false },
  { id: 14, restaurantId: 108, restaurantName: 'Punjabi Dhaba', name: 'Classic Butter Chicken', category: 'North Indian', price: 349, rating: 4.5, time: '25-35 min', veg: false, badge: 'Best Seller', description: 'Rich and creamy tomato-based curry with tandoori chicken.', image: image('photo-1603894584373-5ac82b2ae398'), healthy: false },
  { id: 34, restaurantId: 118, restaurantName: 'Rustic Hearth Cafe', name: 'Grilled Herb Chicken Breast', category: 'Continental', price: 390, rating: 4.8, time: '15-25 min', veg: false, badge: 'Healthy Choice', description: 'Juicy grilled chicken with mashed potatoes and vegetables.', image: image('photo-1532550907401-a500c9a57435'), healthy: true },
  { id: 37, restaurantId: 118, restaurantName: 'Rustic Hearth Cafe', name: 'Continental Veg Ratatouille', category: 'Continental', price: 310, rating: 4.5, time: '15-25 min', veg: true, badge: 'Healthy Choice', description: 'French stewed zucchini, eggplant, and bell peppers.', image: image('photo-1572449043416-55f4685c9bb7'), healthy: true },
  { id: 38, restaurantId: 119, restaurantName: 'Le Bistro Continental', name: 'Pan-Seared Herb Salmon', category: 'Continental', price: 540, rating: 4.9, time: '20-30 min', veg: false, badge: "Chef's Signature", description: 'Norwegian salmon with asparagus and lemon caper drizzle.', image: image('photo-1467003909585-2f8a72700288'), healthy: true },
]

export const categories = ['All Food', 'Pizza', 'Burger', 'Biryani', 'Sushi', 'Continental', 'Fast Food', 'Dessert', 'Chinese', 'Mexican', 'North Indian']
export const coupons = [
  { code: 'ZOMNEW', label: '₹75 OFF', description: '₹75 OFF on orders above ₹199' },
  { code: '50OFF', label: '50% OFF', description: 'Flat 50% OFF up to ₹100' },
  { code: 'PARTY150', label: '₹150 OFF', description: '₹150 OFF on orders above ₹499' },
  { code: 'TASTY30', label: '30% OFF', description: '30% OFF up to ₹120' },
  { code: 'FREEDEL', label: '₹40 OFF', description: 'Free Delivery' },
]
