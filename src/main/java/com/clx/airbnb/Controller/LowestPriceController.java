package com.clx.airbnb.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/lowest_price")
public class LowestPriceController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("by_neighbourhood")
    @ResponseBody
    public List<Map<String, Object>> listUvMonth(HttpServletRequest request) {

        String root_type="'"+request.getParameter("room_type")+"%'";
        String startDt ="'"+ request.getParameter("startDt")+"'";
        int rentDays=Integer.valueOf(request.getParameter("rentDays"));
        int addDays=rentDays-1;
        System.out.println(root_type+"\n"+startDt+"\n"+rentDays);

        String sql = "select c.neighbourhood,min(c.avg_price) min_price from(\n" +
                "select b.neighbourhood,count(*) total_day,cast(avg(price) as decimal(8,2)) avg_price from calendar a join \n" +
                "(select id ,neighbourhood from listing where  room_type like "+root_type+"  and minimum_night<="+rentDays+" )  b\n" +
                "on a.listing_id=b.id where a.dt between "+startDt+" and date_add("+startDt+",+"+addDays+") and a.available='t' group by b.id,b.neighbourhood\n" +
                ") c  where total_day="+rentDays+" group by c.neighbourhood ";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        return results;
    }

}
