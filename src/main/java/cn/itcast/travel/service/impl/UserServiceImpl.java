package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.UserDao;
import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.util.MailUtils;
import cn.itcast.travel.util.UuidUtil;

public class UserServiceImpl implements UserService {
    private UserDao userDao=new UserDaoImpl();
    /**
     * 注册用户
     * @param user
     * @return
     */
    @Override
    public boolean regist(User user) {
        //1.根据用户名查询对象
        User u = userDao.findByUsername(user.getUsername());
        if(u!=null){
            //用户名存在,注册失败
            return false;
        }
        //2.保存用户名信息
        //2.1设置激活码
        user.setCode(UuidUtil.getUuid());
        //2.2设置激活状态
        user.setStatus("N");
        userDao.save(user);
        //激活邮件发送
        String content="<a href='http://localhost:8081/travel/user/active?code="+user.getCode()+"'>点击激活[think旅游网]";
        MailUtils.sendMail(user.getEmail(),content, "激活邮件");
        return true;
    }

    /**
     * 激活用户
     * @param code
     * @return
     */
    @Override
    public boolean active(String code) {
        UserDao userDao=new UserDaoImpl();
        //1.根据激活码查询对象
        User user=userDao.findByCode(code);
        if(user!=null){
            //2.修改激活码状态
            userDao.updateStatus(user);
            return true;
        }else{
            return false;
        }
    }

    /**
     *
     * @param user
     * @return
     */
    @Override
    public User login(User user) {

        return userDao.findByUserameAndPassword(user.getUsername(),user.getPassword());
    }
}
