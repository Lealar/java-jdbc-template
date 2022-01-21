package com.epam.izh.rd.online.auction.repository;

import com.epam.izh.rd.online.auction.entity.Bid;
import com.epam.izh.rd.online.auction.entity.Item;
import com.epam.izh.rd.online.auction.entity.User;
import com.epam.izh.rd.online.auction.mappers.BidMapper;
import com.epam.izh.rd.online.auction.mappers.ItemMapper;
import com.epam.izh.rd.online.auction.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class JdbcTemplatePublicAuction implements PublicAuction {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public List<Bid> getUserBids(long id) {
        String query = "SELECT * FROM bids WHERE user_id = ?";
        return jdbcTemplate.query(query, new Object[]{id}, new BidMapper());
    }

    @Override
    public List<Item> getUserItems(long id) {
        String query = "SELECT * FROM items WHERE user_id = ?";
        return jdbcTemplate.query(query, new Object[]{id}, new ItemMapper());
    }

    @Override
    public Item getItemByName(String name) {
        String query = "SELECT * FROM items WHERE title LIKE  ?";
        return jdbcTemplate.queryForObject(query, new Object[]{name}, new ItemMapper());
    }

    @Override
    public Item getItemByDescription(String name) {
        String query = "SELECT * FROM items WHERE DESCRIPTION LIKE  ?";
        return jdbcTemplate.queryForObject(query, new Object[]{name}, new ItemMapper());
    }

    /**
     * Средняя цена лотов каждого пользователя
     */
    @Override
    public Map<User, Double> getAvgItemCost() {
        Map<User, Double> avgItemCost = new HashMap<>();
        List<User> userList = jdbcTemplate.query("SELECT * FROM USERS where USER_ID IN " +
                "(SELECT USER_ID FROM ITEMS)", new UserMapper());
        String query = "SELECT AVG(start_price) FROM items WHERE USER_ID = ?";
        for (User u : userList) {
            Double avgPrice = jdbcTemplate.queryForObject(query, new Object[]{u.getUserId()}, Double.class);
            avgItemCost.put(u, avgPrice);
        }
        return avgItemCost;
    }

    @Override
    public Map<Item, Bid> getMaxBidsForEveryItem() {
        Map<Item, Bid> maxBids = new HashMap<>();
        List<Item> itemList = jdbcTemplate.query("SELECT * FROM items where item_id in (SELECT item_id FROM bids)", new ItemMapper());
        Bid bid;
        String query = "SELECT * FROM bids where bid_value = (SELECT MAX(bid_value) from bids where item_id = ?)";
        for (Item item : itemList) {
            bid = jdbcTemplate.queryForObject(query, new Object[]{item.getItemId()}, new BidMapper());
            maxBids.put(item, bid);
        }
        return maxBids;
    }

    @Override
    public boolean createUser(User user) {
        return (jdbcTemplate.update("INSERT INTO users VALUES ( ?,?,?,?,? )",
                user.getUserId(), user.getBillingAddress(), user.getFullName(),
                user.getLogin(), user.getPassword()) > 0);
    }

    @Override
    public boolean createItem(Item item) {
        return (jdbcTemplate.update("INSERT INTO items VALUES ( ?,?,?,?,?,?,?,?,? )",
                item.getItemId(), item.getBidIncrement(), item.getBuyItNow(), item.getDescription(),
                item.getStartDate(), item.getStartPrice(), item.getStopDate(), item.getTitle(), item.getUserId())) > 0;
    }

    @Override
    public boolean createBid(Bid bid) {
        return (jdbcTemplate.update("INSERT INTO bids VALUES ( ?,?,?,?,? )",
                bid.getBidId(), bid.getBidDate(), bid.getBidValue(), bid.getItemId(), bid.getUserId())) > 0;
    }

    @Override
    public boolean deleteUserBids(long id) {
        return jdbcTemplate.update("DELETE from bids where user_id = ?", id) > 0;
    }

    @Override
    public boolean doubleItemsStartPrice(long id) {
        return jdbcTemplate.update("UPDATE items SET start_price = start_price * 2 WHERE user_id =?", id) > 0;
    }
}
