package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.CategoryDao;
import cn.itcast.travel.dao.impl.CategoryDaoImpl;
import cn.itcast.travel.domain.Category;
import cn.itcast.travel.service.CategoryService;
import cn.itcast.travel.util.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CategoryServiceImpl implements CategoryService {
    private CategoryDao categoryDao=new CategoryDaoImpl();
    /**
     * 查询所有
     * @return
     */
    @Override
    public List<Category> findAll() {
        //1.从redis中查询
        //1.1获取jedis客户端
        Jedis jedis= JedisUtil.getJedis();
        //1.2使用storedSet来排序
        //Set<String> set = jedis.zrange("category", 0, -1);
        //1.3查询storedset中的分数(cid)和值(cname)
        Set<Tuple> set = jedis.zrangeWithScores("category", 0, -1);
        List<Category> all=null;
        //2.判断集合是否为空
        if(set==null||set.size()==0){
            System.out.println("从数据库查询....");
            //3.如果为空则查询数据库,再放入redis中
            //3.1查询数据库
             all = categoryDao.findAll();
            //3.2将数据放入redis
            for(int i=0;i<all.size();i++){
                jedis.zadd("category",all.get(i).getCid(),all.get(i).getCname());
            }
        }else{
            System.out.println("从redis中查询");
            //将set集合存入list集合
            all=new ArrayList<Category>();
            for (Tuple  tuple : set) {
                Category category=new Category();
                category.setCname(tuple.getElement());
                category.setCid((int)tuple.getScore());
                all.add(category);
            }
        }

        //4.如果不为空,则直接返回
        return all;
    }
}
