package com.jade.marketplace.cart;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jade.marketplace.exception.ResourceNotFoundException;
import com.jade.marketplace.product.Product;
import com.jade.marketplace.product.ProductService;
import com.jade.marketplace.redis.CartCacheService;
import com.jade.marketplace.user.User;
import com.jade.marketplace.user.UserService;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;

/**
 * Cart service handles all logic related to cart
 * 
 * Responsibilites:
 * Add item to cart
 * Remove item from cart
 * Clear all items from cart
 */
@Service
public class CartService {
    
    private final CartRepository cartRepository;
    private final UserService userService;
    private final ProductService productService;
    private final CartCacheService cartCacheService;

    /**
     * Constructor
     */
    public CartService(CartRepository cartRepository, UserService userService, ProductService productService, CartCacheService cartCacheService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.userService = userService;
        this.cartCacheService = cartCacheService;
    }

    /**
     * Gets current user's cart
     */
    @Transactional
    public Cart getCart() {
        // get user 
        User user = userService.getCurrentUser();

        // return cart owned by this user; if cart not existed, create a new one for this user and save into cart repository
        return cartRepository.findByUser(user).orElseGet(() -> cartRepository.save(new Cart(user)));
    }

    /**
     * Validates the requested quantity is valid
     */
    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new ValidationException("Quantity must be at least 1");
        }
    }

    /**
     * Find cart item by product ID
     */
    private Optional<CartItem> findItemByProductId(Cart cart, Long productId) {
        // return the item with the matching product ID inside the cart
        return cart.getItems().stream().filter(item -> item.getProduct().getId().equals(productId)).findFirst();
    }

    /**
     * Adds a product into user's cart
     * If a product already exsist in cart, increases its quantity
     * Else, create a new product item inside cart
     */
    public Cart addToCart(AddToCartRequest request) {
        // get cart
        Cart cart = getCart();

        // get product
        Product product = productService.findById(request.productId());

        // validate if the requested quantity is valid
        validateQuantity(request.quantity());

        // check if the cart item already exists in the cart
        Optional<CartItem> cartItem = findItemByProductId(cart, product.getId());

        // if the cart item exists, increase the existing quantity by requested quantity
        if (cartItem.isPresent()) {
            // get cart item from Optional box
            CartItem item = cartItem.get();
            // set the item's quantity
            item.setQuantity(item.getQuantity() + request.quantity());
        }
        // if the cart item does not exist, make a new cart item and set its quantity to be requested quantity
        else {
            // create new cart item
            CartItem item = new CartItem(product, request.quantity());

            // add new item into cart
            cart.addItem(item);
        }

        // mark cart updated
        cart.updated();

        // save cart into repository
        Cart savedCart = cartRepository.save(cart);

        // remove cart from Redis cache
        cartCacheService.removeCart(cart.getUser().getId());

        // return saved cart
        return savedCart;
    }

    /**
     * Updates a cart item inside user's cart
     * If product already exists inside cart, updates
     * Else, throw an error
     */
    public Cart updateCart(UpdateCartItemRequest request) {
        // get cart
        Cart cart = getCart();

        // get product
        Product product = productService.findById(request.productId());

        // validate requested quantity is valid
        validateQuantity(request.quantity());

        // check if cart item already exists in the cart
        CartItem cartItem = findItemByProductId(cart, product.getId()).orElseThrow(() -> new ResourceNotFoundException("Product not found in cart with id: " + request.productId()));

        // set the item's quantity
        cartItem.setQuantity(request.quantity());

        // mark cart updated
        cart.updated();

        // save cart into repository
        Cart savedCart = cartRepository.save(cart);

        // remove cart from Redis cache
        cartCacheService.removeCart(cart.getUser().getId());

        // return saved cart
        return savedCart;
    }

    /**
     * Removes an item from a user's cart
     */
    public Cart removeFromCart(Long productId) {
        // get cart
        Cart cart = getCart();

        // get item of product inside cart
        CartItem cartItem = findItemByProductId(cart, productId).orElseThrow(() -> new ResourceNotFoundException("Product not found in cart with id: " + productId));

        // remove item from cart
        cart.removeItem(cartItem);

        // mark cart updated
        cart.updated();

        // save cart into repository
        Cart savedCart = cartRepository.save(cart);

        // remove cart from Redis cache
        cartCacheService.removeCart(cart.getUser().getId());

        // return saved cart
        return savedCart;
    }

    /**
     * Removes all items from a user's cart
     */
    public Cart removeAllFromCart() {
        // get cart
        Cart cart = getCart();

        // remove all items from cart
        cart.clearCart();

        // mark cart updated
        cart.updated();

        // save cart into repository
        Cart savedCart = cartRepository.save(cart);

        // remove cart from Redis cache
        cartCacheService.removeCart(cart.getUser().getId());

        // return saved cart
        return savedCart;
    }

    /**
     * Calculate cart's total price
     */
    public BigDecimal calculateCartTotal() {
        // get cart
        Cart cart = getCart();

        // get subtotal for each cart item and then add them all
        return cart.getItems().stream().map(CartItem::getItemSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
